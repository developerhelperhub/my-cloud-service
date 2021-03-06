
import React from 'react';
import Moment from 'react-moment';
import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';


import Label from '../../components/dashboard/Label'
import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'
import Widget from '../../components/dashboard/Widget'

import PagePanel from '../../components/dashboard/panel/PagePanel'
import PagePanelHead from '../../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../../components/dashboard/panel/PagePanelBody'

import PageTabPanel from '../../components/dashboard/panel/PageTabPanel'
import PageTabHead from '../../components/dashboard/panel/PageTabHead'
import PageTab from '../../components/dashboard/panel/PageTab'
import PageTabContent from '../../components/dashboard/panel/PageTabContent'
import PageTabPane from '../../components/dashboard/panel/PageTabPane'

import AppApiRepo from '../../common/AppApiRepo'

import DataTable from '../../components/table/DataTable'

import './MonitorPage.css'

class MonitorPage extends React.Component {

    constructor(props) {
        super(props);

        this.formatDate = this.formatDate.bind(this);
        this.streamApplications = this.streamApplications.bind(this);
        this.onclickApplicationTable = this.onclickApplicationTable.bind(this);
        this.renderStatusOnTable = this.renderStatusOnTable.bind(this);
        this.populateMemory = this.populateMemory.bind(this);
        this.fileSizeFormat = this.fileSizeFormat.bind(this);

        let self = this;

        this.state = {
            selectedApplicationEventSource: null,
            selectedApplicationInstance: {
                id: "",
                status: "N/A",
                diskSpace: {
                    status: "N/A",
                    free: "",
                    total: "",
                    threshold: ""
                },
                lastUpdated: "",
                build: {
                    version: "",
                    artifact: "",
                    name: "",
                    group: "",
                    time: ""
                }
            },
            selectedApplicationInstances: {
                id: "",
                status: "N/A",
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
            selectedApplicationInfo: {
                id: "",
                status: "N/A",
                instanceTotal: 0,
                instanceRunning: 0,
                lastUpdated: ""
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
                    { title: "Name", width: "30%" },
                    { title: "Running", width: "10%" },
                    { title: "Total", width: "10%" },
                    { title: "Status", width: "10%" },
                    { title: "Last Updated", width: "40%" },

                ],
                body: []
            }

        }
    }

    componentDidMount() {
        this.streamApplications();
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

        AppApiRepo.eventSource("/monitor/stream/applications/all",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const eventData = JSON.parse(event.data);

                const applicationData = self.state.applicationData;
                const applicationBody = [];

                eventData.forEach(app => {

                    var colums = [];

                    colums.push(app.name);
                    colums.push(app.runningInstance);
                    colums.push(app.totalInstance);
                    colums.push(app.status);
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
        var selectedApplication = null;

        if (row != null) {
            selectedApplication = row[0];
        }

        if (selectedApplication != null) {

            var eventSource = self.state.selectedApplicationEventSource;

            if (eventSource != null) {
                eventSource.close();
            }

            eventSource = AppApiRepo.eventSource("/monitor/stream/applications/" + selectedApplication + "/info",
                {
                    'Authorization': AppApiRepo.getToken()
                }, function (event) {

                    const eventData = JSON.parse(event.data);
                    const instanceBody = [];

                    var selected = self.state.selectedApplicationInfo;


                    var values = [];
                    var items = [];
                    var legends = [];

                    eventData.memory.forEach(memory => {

                        var data = [];

                        memory.matrics.forEach(matric => {
                            values.push({ date: self.formatDate(matric.time), value: matric.value });
                            data.push({ date: self.formatDate(matric.time), value: matric.value });
                        });

                        var color = "green";

                        if (memory.name == "jvm.memory.max") {
                            color = "green";
                        } else {
                            color = "red";
                        }

                        legends.push({
                            value: memory.display,
                            fill: color,
                            stroke: color,
                            strokeWidth: 1
                        })

                        items.push(
                            {
                                fill: color,
                                stroke: color,
                                strokeWidth: 1,
                                data: data,
                                x: function (d) { return d.date },
                                y: function (d) { return d.value }
                            }
                        )
                    });

                    self.populateMemory({
                        items: items,
                        values: values,
                        legends: legends
                    },
                        "#chart-memroy-heap",
                        function (d) {
                            return self.fileSizeFormat(d);
                        });

                    var values = [];
                    var items = [];
                    var legends = [];

                    eventData.buffer.forEach(buffer => {

                        var data = [];

                        buffer.matrics.forEach(matric => {
                            values.push({ date: self.formatDate(matric.time), value: matric.value });
                            data.push({ date: self.formatDate(matric.time), value: matric.value });
                        });

                        var color = "green";

                        if (buffer.name == "jvm.buffer.total.capacity") {
                            color = "green";
                        } else {
                            color = "red";
                        }

                        legends.push({
                            value: buffer.display,
                            fill: color,
                            stroke: color,
                            strokeWidth: 1
                        })


                        items.push(
                            {
                                fill: color,
                                stroke: color,
                                strokeWidth: 1,
                                data: data,
                                x: function (d) { return d.date },
                                y: function (d) { return d.value }
                            }
                        )
                    });

                    self.populateMemory({
                        items: items,
                        values: values,
                        legends: legends
                    },
                        "#chart-memroy-non-heap",
                        function (d) {
                            return self.fileSizeFormat(d);
                        });


                    var values = [];
                    var items = [];
                    var legends = [];

                    eventData.thread.forEach(thread => {

                        var data = [];

                        thread.matrics.forEach(matric => {
                            values.push({ date: self.formatDate(matric.time), value: matric.value });
                            data.push({ date: self.formatDate(matric.time), value: matric.value });
                        });

                        var color = "blue";

                        if (thread.name == "jvm.threads.peak") {
                            color = "green";
                        } else if (thread.name == "jvm.threads.live") {
                            color = "yellow";
                        } else {
                            color = "blue";
                        }

                        legends.push({
                            value: thread.display,
                            fill: color,
                            stroke: color,
                            strokeWidth: 1
                        })

                        items.push(
                            {
                                fill: color,
                                stroke: color,
                                strokeWidth: 1,
                                data: data,
                                x: function (d) { return d.date },
                                y: function (d) { return d.value }
                            }
                        )
                    });

                    self.populateMemory({
                        items: items,
                        values: values,
                        legends: legends
                    },
                        "#chart-thread",
                        function (d) {
                            return d;
                        });


                    var values = [];
                    var items = [];
                    var legends = [];
                    var color = "";

                    var data = [];

                    data = [];
                    eventData.diskSpace.total.forEach(diskSpace => {

                        values.push({ date: self.formatDate(diskSpace.time), value: diskSpace.value });
                        data.push({ date: self.formatDate(diskSpace.time), value: diskSpace.value });

                    });

                    color = "green";
                    legends.push({
                        value: "Total",
                        fill: color,
                        stroke: color,
                        strokeWidth: 1
                    })

                    items.push(
                        {
                            fill: color,
                            stroke: color,
                            strokeWidth: 1,
                            data: data,
                            x: function (d) { return d.date },
                            y: function (d) { return d.value }
                        }
                    )


                    data = [];
                    eventData.diskSpace.free.forEach(diskSpace => {

                        values.push({ date: self.formatDate(diskSpace.time), value: diskSpace.value });
                        data.push({ date: self.formatDate(diskSpace.time), value: diskSpace.value });

                    });

                    color = "red";
                    legends.push({
                        value: "Free",
                        fill: color,
                        stroke: color,
                        strokeWidth: 1
                    })

                    items.push(
                        {
                            fill: color,
                            stroke: color,
                            strokeWidth: 1,
                            data: data,
                            x: function (d) { return d.date },
                            y: function (d) { return d.value }
                        }
                    )

                    data = [];
                    eventData.diskSpace.threshold.forEach(diskSpace => {

                        values.push({ date: self.formatDate(diskSpace.time), value: diskSpace.value });
                        data.push({ date: self.formatDate(diskSpace.time), value: diskSpace.value });

                    });

                    color = "yellow";
                    legends.push({
                        value: "Threshold",
                        fill: color,
                        stroke: color,
                        strokeWidth: 1
                    })

                    items.push(
                        {
                            fill: color,
                            stroke: color,
                            strokeWidth: 1,
                            data: data,
                            x: function (d) { return d.date },
                            y: function (d) { return d.value }
                        }
                    )


                    self.populateMemory({
                        items: items,
                        values: values,
                        legends: legends
                    },
                        "#chart-disk-space",
                        function (d) {
                            return self.fileSizeFormat(d);
                        });

                    selected.id = selectedApplication;
                    selected.status = row[3];
                    selected.instanceTotal = row[1];
                    selected.instanceRunning = row[2];
                    selected.lastUpdated = row[4];

                    self.setState({
                        selectedApplicationInfo: selected
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

    fileSizeFormat(bytes) {
        var i = Math.floor(Math.log(bytes) / Math.log(1024));
        var value = (bytes / Math.pow(1024, i)).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
        return (value == undefined) ? 0 : value;
    }

    populateMemory(data, id, funTickFormat) {

        let self = this;

        window.d3LinesChart({
            id: id,
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            type: 'area',
            items: data.items,
            axis: {
                x: {
                    transform: function (width, height) {
                        return "translate(0," + height + ")";
                    },
                    range: function (width, height) {
                        return [0, width - 70];
                    }
                },
                y: {
                    range: function (width, height) {
                        return [height, 0];
                    },
                    tickFormat: function (d) {
                        return funTickFormat(d);
                    }
                },
                stroke: "#818896",
                fill: "none",
                text: "#818896",
                font: "8px Arial",
                fontWeight: "1px"
            },
            legend: {
                transform: function (d, i, width, height) {
                    return 'translate(' + 0 + ',' + (i * 20) + ')';
                },
                rect: {
                    width: 10,
                    height: 10,
                    x: function (width, height) {
                        return width - 60;
                    },
                    y: function (width, height) {
                        return 0;
                    }
                },
                text: {
                    x: function (width, height) {
                        return width - 40;
                    },
                    y: function (width, height) {
                        return 10;
                    },
                    value: function (d) {
                        return d.value;
                    },
                    font: "10px arial",
                    fill: "#818896"
                },
                data: data.legends,
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
        let selectedApplicationInfo = this.state.selectedApplicationInfo;
        let selectedApplicationInstances = this.state.selectedApplicationInstances;

        var statusLabelClass = "label-warning";
        if (selectedApplicationInfo.status == "UP") {
            statusLabelClass = 'label-success';
        } else if (selectedApplicationInfo.status == "DOWN") {
            statusLabelClass = 'label-danger';
        }

        // var statusDiskLabelClass = "label-warning";
        // if (selected.diskSpace.status == "UP") {
        //     statusDiskLabelClass = 'label-success';
        // } else if (selected.diskSpace.status == "DOWN") {
        //     statusDiskLabelClass = 'label-danger';
        // }

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
                                    {/* <div class="row">
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
                                    </div> */}

                                    <div class="row">
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Application</div>
                                                    <div class="bd-highlight pr-10"><Label class={statusLabelClass}> {selectedApplicationInfo.status} </Label></div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">ID</div>
                                                            <div class="bd-highlight">{selectedApplicationInfo.id}</div>
                                                        </div>
                                                    </div>

                                                    <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Instance</div>
                                                            <div class="bd-highlight">{selectedApplicationInfo.instanceRunning}/{selectedApplicationInfo.instanceTotal}</div>
                                                        </div>
                                                    </div>

                                                    <div class="d-flex flex-row bd-highlight mb-2 ml-3 mr-3 border-bottom item-body">
                                                        <div class="d-flex flex-row bd-highlight">
                                                            <div class="bd-highlight item-label">Last Updated</div>
                                                            <div class="bd-highlight">{selectedApplicationInfo.lastUpdated}</div>
                                                        </div>
                                                    </div>


                                                </div>

                                            </div>
                                        </div>
                                        <div class="col-xxl-6 col-lg-6">
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Memory : Heap</div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div id="chart-memroy-heap" style={{ height: "150px" }}></div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Memory : Non heap</div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div id="chart-memroy-non-heap" style={{ height: "150px" }}></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Thread</div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div id="chart-thread" style={{ height: "150px" }}></div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xxl-6 col-lg-6">
                                            <div class="d-flex flex-column bd-highlight border info-box">

                                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                                    <div class="bd-highlight pr-10 mr-auto">Disk Space</div>
                                                </div>

                                                <div class="bd-highlight">
                                                    <div id="chart-disk-space" style={{ height: "150px" }}></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </PageTabPane>
                            <PageTabPane id="instance" labelledby="instance-tab">
                                <div class="container-fluid monitor">
                                    <div class="row">
                                        <div class="col-xxl-12 col-lg-12">
                                            <DataTable id="table-instance" width="100%" data={selectedApplicationInstances.instance}></DataTable>
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

}

export default MonitorPage;