
import React from 'react';
import Moment from 'react-moment';
import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';


import Label from '../components/dashboard/Label'
import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'
import Widget from '../components/dashboard/Widget'

import PagePanel from '../components/dashboard/panel/PagePanel'
import PagePanelHead from '../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../components/dashboard/panel/PagePanelBody'

import PageTabPanel from '../components/dashboard/panel/PageTabPanel'
import PageTabHead from '../components/dashboard/panel/PageTabHead'
import PageTab from '../components/dashboard/panel/PageTab'
import PageTabContent from '../components/dashboard/panel/PageTabContent'
import PageTabPane from '../components/dashboard/panel/PageTabPane'

import AppApiRepo from '../common/AppApiRepo'

import DataTable from '../components/table/DataTable'

import './MonitorPage.css'

class MonitorPage extends React.Component {

    constructor(props) {
        super(props);

        this.formatDate = this.formatDate.bind(this);
        this.streamApplications = this.streamApplications.bind(this);
        this.onclickApplicationTable = this.onclickApplicationTable.bind(this);
        this.renderStatusOnTable = this.renderStatusOnTable.bind(this);
        this.populateMemoryHeap = this.populateMemoryHeap.bind(this);

        let self = this;

        this.state = {
            selectedApplicationEventSource: null,
            selectedApplication: {
                id: "-",
                status: "-",
                diskSpace: {
                    status: "-",
                    free: "0",
                    total: "0",
                    threshold: "0"
                },
                lastUpdated: "-",
                build: {
                    version: "-",
                    artifact: "-",
                    name: "-",
                    group: "0",
                    time: "0"
                },
                instance: {
                    columnDefs: [
                        {
                            render: function (data, type, row) {
                                return self.renderStatusOnTable(data, type, row);
                            },
                            targets: 2
                        }
                    ],
                    head: [
                        { title: "Instance", width: "30%" },
                        { title: "IP Address", width: "10%" },
                        { title: "Status", width: "10%" },
                        { title: "Ren Int", width: "5%" },
                        { title: "Dur Int", width: "5%" },
                        { title: "Last Updated", width: "30%" },

                    ],
                    body: []
                }
            },
            applicationData: {
                columnDefs: [
                    {
                        render: function (data, type, row) {
                            return self.renderStatusOnTable(data, type, row);
                        },
                        targets: 3
                    }
                ],
                head: [
                    { title: "ID", width: "20%" },
                    { title: "Name", width: "20%" },
                    { title: "Version", width: "15%" },
                    { title: "Status", width: "10%" },
                    { title: "Build Time", width: "20%" },
                    { title: "Last Updated", width: "40%" },

                ],
                body: []
            }

        }

        this.sampleGroupedData = this.sampleGroupedData.bind(this);
        this.colours = this.colours.bind(this);
    }

    componentDidMount() {
        this.streamApplications();

        let self = this;

        let values = [];
        let items = [];
        var legends = [];

        let data = {
            items: items,
            values: values,
            legends: legends
        }

        let colours = self.colours();

        data = this.sampleGroupedData();

        this.populateMemoryHeap(data);
    }

    renderStatusOnTable(data, type, row) {
        if (data == "UP") {
            return '<span class="label label-success">' + data + '</span>';
        } else if (data == "DOWN") {
            return '<span class="label label-danger">' + data + '</span>';
        } else {
            return '<span class="label label-warning">' + data + '</span>';
        }
    }

    streamApplications() {
        let self = this;

        AppApiRepo.eventSource("/monitor/stream/applications/basic-info",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const eventData = JSON.parse(event.data);

                const applicationData = self.state.applicationData;
                const applicationBody = [];

                eventData.forEach(app => {

                    var colums = [];

                    colums.push(app.name);
                    colums.push(app.build.name);
                    colums.push(app.build.version);
                    colums.push(app.status);
                    colums.push(app.build.time);
                    colums.push(app.lastUpdated);

                    applicationBody.push(colums);
                });

                applicationData.body = applicationBody;

                self.setState({
                    applicationData: applicationData
                })

            }, function (err) {
                console.log("Stream Applications Event Source Error : " + err.error);
            }
        );


    }

    onclickApplicationTable(row) {
        let self = this;
        const selectedApplication = row[0];

        if (selectedApplication != null) {

            var eventSource = self.state.selectedApplicationEventSource;

            if (eventSource != null) {
                eventSource.close();
            }

            eventSource = AppApiRepo.eventSource("/monitor/stream/applications/" + selectedApplication,
                {
                    'Authorization': AppApiRepo.getToken()
                }, function (event) {

                    const eventData = JSON.parse(event.data);
                    const instanceBody = [];

                    var selected = self.state.selectedApplication;

                    selected.id = eventData.name;
                    selected.status = eventData.status;
                    selected.diskSpace.status = eventData.diskSpace.status;
                    selected.diskSpace.free = eventData.diskSpace.free;
                    selected.diskSpace.total = eventData.diskSpace.total;
                    selected.diskSpace.threshold = eventData.diskSpace.threshold;
                    selected.lastUpdated = eventData.lastUpdated;
                    selected.build.version = eventData.build.version;
                    selected.build.artifact = eventData.build.artifact;
                    selected.build.name = eventData.build.name;
                    selected.build.group = eventData.build.group;
                    selected.build.time = eventData.build.time;


                    eventData.instance.forEach(instance => {
                        const timestamp = Date(instance.lastUpdatedTimestamp);
                        var colums = [];

                        colums.push(instance.homePageUrl);
                        colums.push(instance.ipAddr);
                        colums.push(instance.status);
                        colums.push(instance.leaseInfo.renewalIntervalInSecs);
                        colums.push(instance.leaseInfo.durationInSecs);
                        colums.push(timestamp);

                        instanceBody.push(colums);
                    });

                    selected.instance.body = instanceBody;

                    self.setState({
                        selectedApplication: selected
                    })

                }, function (err) {
                    console.log("Monitor Application " + selectedApplication + " Source Error : " + err.error);
                }
            );

            self.setState({
                selectedApplicationEventSource: eventSource
            })



        }
    }

    populateMemoryHeap(data) {

        window.d3LinesChart({
            id: "#memroy-heap",
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            items: data.items,
            axis: {
                x: {
                    transform: function (width, height) {
                        return "translate(0," + height + ")";
                    },
                    range: function (width, height) {
                        return [0, width];
                    }
                },
                y: {
                    range: function (width, height) {
                        return [height, 0];
                    }
                },
                stroke: "#818896",
                fill: "none",
                text: "#818896",
                font: "10px Arial"
            },
            values: data.values,
            extent: function (d) { return d.date; },
            max: function (d) { return d.value; },
        });
    }

    formatDate(date) {
        return window.d3.timeParse("%Y-%m-%d %H:%M:%S")(date);
    }


    render() {
        let selected = this.state.selectedApplication;

        var statusLabelClass = "label-warning";
        if (selected.status == "UP") {
            statusLabelClass = 'label-success';
        } else if (selected.status == "DOWN") {
            statusLabelClass = 'label-danger';
        }

        var statusDiskLabelClass = "label-warning";
        if (selected.diskSpace.status == "UP") {
            statusDiskLabelClass = 'label-success';
        } else if (selected.diskSpace.status == "DOWN") {
            statusDiskLabelClass = 'label-danger';
        }

        return (
            <PageContent>
                <Row>
                    <PagePanel cols="col-xxl-12 col-lg-12" >
                        <PagePanelHead title="Applications">
                        </PagePanelHead>
                        <PagePanelBody>
                            <DataTable id="table-application" width="100%" data={this.state.applicationData} onclick={this.onclickApplicationTable}></DataTable>
                        </PagePanelBody>
                    </PagePanel>
                </Row>

                <Row>
                    <PageTabPanel cols="col-xxl-12 col-lg-12" >
                        <PageTab id="applicationInfoTab">
                            <PageTabHead id="info-tab" active="true" href="#info" controls="info" selected="true" title="Info"></PageTabHead>
                            <PageTabHead id="instance-tab" href="#instance" controls="instance" title="Instance"></PageTabHead>
                        </PageTab>

                        <PageTabContent id="applicationInfoTabContent">
                            <PageTabPane id="info" labelledby="info-tab" show="true" active="true">
                                <div class="container-fluid monitor">
                                    <div class="row">
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Application Info</div>
                                                    <div class="bd-highlight pr-10"><Label class={statusLabelClass}> {selected.status} </Label></div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">ID</div>
                                                            <div class="bd-highlight">{selected.id}</div>
                                                        </div>
                                                    </div>

                                                    <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Last Updated</div>
                                                            <div class="bd-highlight">{selected.lastUpdated}</div>
                                                        </div>
                                                    </div>

                                                </div>
                                                <div class="bd-highlight">
                                                    <div class="d-flex flex-row bd-highlight mb-2 ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Build</div>
                                                            <div class="bd-highlight" width="100%">
                                                                <p class="m-0">Artifact: {selected.build.artifact}</p>
                                                                <p class="m-0">Name: {selected.build.artifact}</p>
                                                                <p class="m-0">Group: {selected.build.group}</p>
                                                                <p class="m-0">Version: {selected.build.version}</p>
                                                                <p class="m-0">Time: {selected.build.time}</p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Disk Space</div>
                                                    <div class="bd-highlight pr-10"><Label class={statusDiskLabelClass}> {selected.diskSpace.status} </Label></div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Total</div>
                                                            <div class="bd-highlight">{selected.diskSpace.total}</div>
                                                        </div>
                                                    </div>

                                                    <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Free</div>
                                                            <div class="bd-highlight">{selected.diskSpace.free}</div>
                                                        </div>
                                                    </div>

                                                    <div class="d-flex flex-row bd-highlight  mb-2 ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Threshold</div>
                                                            <div class="bd-highlight">{selected.diskSpace.threshold}</div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Memory: Heap</div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div id="memroy-heap" style={{ height: "150px" }}></div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xxl-6 col-lg-6">
                                        </div>
                                    </div>
                                </div>
                            </PageTabPane>
                            <PageTabPane id="instance" labelledby="instance-tab">
                                <div class="container-fluid monitor">
                                    <div class="row">
                                        <div class="col-xxl-12 col-lg-12">
                                            <DataTable id="table-instance" width="100%" data={selected.instance}></DataTable>
                                        </div>
                                    </div>
                                </div>
                            </PageTabPane>
                        </PageTabContent>
                    </PageTabPanel>
                </Row>
            </PageContent>
        );

    }

    colours() {
        return ["ORCHID", "GOLD", "ORANGERED", "LIMEGREEN", "HOTPINK", "MEDIUMVIOLETRED", "DARKVIOLET"];
    }

    sampleGroupedData() {

        let self = this;

        var legends = [
            { value: "Application 1", fill: 'steelblue', stroke: "steelblue", strokeWidth: 1.5 },
            { value: "Application 2", fill: 'red', stroke: "steelblue", strokeWidth: 1.5 }
        ]

        var values = [
            { date: self.formatDate("2020-05-17 09:10:20"), value: 200 },
            { date: self.formatDate("2020-05-17 09:12:20"), value: 100 },
            { date: self.formatDate("2020-05-17 09:40:20"), value: 300 },
            { date: self.formatDate("2020-05-17 09:55:20"), value: 600 },
            { date: self.formatDate("2020-05-17 10:10:20"), value: 150 },
            { date: self.formatDate("2020-05-17 09:10:20"), value: 50 },
            { date: self.formatDate("2020-05-17 09:12:20"), value: 70 },
            { date: self.formatDate("2020-05-17 09:40:20"), value: 90 },
            { date: self.formatDate("2020-05-17 09:55:20"), value: 400 },
            { date: self.formatDate("2020-05-17 10:10:20"), value: 40 }
        ]

        var items = [
            {
                fill: 'steelblue',
                stroke: "steelblue",
                strokeWidth: 1.5,
                data: [
                    { date: self.formatDate("2020-05-17 09:10:20"), value: 200 },
                    { date: self.formatDate("2020-05-17 09:12:20"), value: 100 },
                    { date: self.formatDate("2020-05-17 09:40:20"), value: 300 },
                    { date: self.formatDate("2020-05-17 09:55:20"), value: 600 },
                    { date: self.formatDate("2020-05-17 10:10:20"), value: 150 },
                ],
                x: function (d) { return d.date },
                y: function (d) { return d.value }
            },
            {
                fill: 'red',
                stroke: "red",
                strokeWidth: 1.5,
                data: [
                    { date: self.formatDate("2020-05-17 09:10:20"), value: 50 },
                    { date: self.formatDate("2020-05-17 09:12:20"), value: 70 },
                    { date: self.formatDate("2020-05-17 09:40:20"), value: 90 },
                    { date: self.formatDate("2020-05-17 09:55:20"), value: 400 },
                    { date: self.formatDate("2020-05-17 10:10:20"), value: 40 },
                ],
                x: function (d) { return d.date },
                y: function (d) { return d.value }
            }
        ]

        return { values: values, items: items, legends: legends };
    }
}

export default MonitorPage;