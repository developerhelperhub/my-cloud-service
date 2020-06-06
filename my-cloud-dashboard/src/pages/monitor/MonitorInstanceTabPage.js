
import React from 'react';
import Moment from 'react-moment';
import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';


import Label from '../../components/dashboard/Label'
import PageTabPane from '../../components/dashboard/panel/PageTabPane'
import DataTable from '../../components/table/DataTable'

import AppApiRepo from '../../common/AppApiRepo'

import './MonitorPage.css'

class MonitorInstanceTabPage extends React.Component {

    constructor(props) {
        super(props);

        let self = this;

        this.renderStatusOnTable = this.renderStatusOnTable.bind(this);
        this.onclickInstanceTable = this.onclickInstanceTable.bind(this);

        this.formatDate = this.formatDate.bind(this);
        this.populateGraph = this.populateGraph.bind(this);
        this.fileSizeFormat = this.fileSizeFormat.bind(this);

        this.state = {
            selectedApplicationEventSource: null,
            selectedInstanceEventSource: null,
            selectedInstance: {
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
                instanceTotal: 0,
                instanceRunning: 0,
                lastUpdated: 0,
                instances: {
                    columnDefs: [
                        {
                            render: function (data, type, row) {
                                return self.renderStatusOnTable(data, type, row);
                            },
                            targets: 4
                        }
                    ],
                    head: [
                        { title: "Id", width: "20%" },
                        { title: "Instance Id", width: "40%" },
                        { title: "Application Name", width: "25%" },
                        { title: "Version", width: "10%" },
                        { title: "Status", width: "5%" },
                        { title: "Last Updated", width: "10%" }
                    ],
                    body: []
                }
            }
        }
    }


    componentWillReceiveProps(nextProps) {

        let self = this;
        let selectedApplication = nextProps.selectedApplication;
        let selectedTab = nextProps.selectedTab;
        var eventSource = self.state.selectedApplicationEventSource;

        if (selectedTab != "instance-tab" || selectedApplication == null) {
            return "";
        }

        if (eventSource != null) {
            eventSource.close();
        }

        console.log("MonitorInstanceTabPage selectedApplication: " + selectedApplication + " " + selectedTab);

        eventSource = AppApiRepo.eventSource("/monitor/stream/instances/" + selectedApplication[0],
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const eventData = JSON.parse(event.data);

                var selected = self.state.selectedApplicationInstances;
                var instanceBody = [];

                eventData.forEach(instance => {

                    var colums = [];

                    colums.push(instance.identifier);
                    colums.push(instance.instanceId);
                    colums.push(instance.name);
                    colums.push(instance.version);
                    colums.push(instance.status);
                    colums.push(instance.lastUpdated);

                    instanceBody.push(colums);

                });

                selected.id = selectedApplication[0];
                selected.status = selectedApplication[3];
                selected.instanceTotal = selectedApplication[2];
                selected.instanceRunning = selectedApplication[1];
                selected.lastUpdated = selectedApplication[4];
                selected.instances.body = instanceBody;

                self.setState({
                    selectedApplicationInstances: selected,
                    selectedApplicationEventSource: eventSource
                });

            }, function (err) {
                console.log("Monitor Application " + selectedApplication + " Source Error : " + err.error);
            }
        );
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

    onclickInstanceTable(row) {
        console.log("Selected instance : " + row);

        let self = this;
        let selectedInstance = row;
        var eventSource = self.state.selectedInstanceEventSource;

        if (selectedInstance == null) {
            return "";
        }

        if (eventSource != null) {
            eventSource.close();
        }

        eventSource = AppApiRepo.eventSource("/monitor/stream/instances/" + selectedInstance[0] + "/info",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const eventData = JSON.parse(event.data);


                var selected = self.state.selectedInstance;

                selected.id = "";
                selected.status = "N/A";
                selected.lastUpdated = "";

                selected.build.version = eventData.build.version;
                selected.build.artifact = eventData.build.artifact;
                selected.build.name = eventData.build.name;
                selected.build.group = eventData.build.group;
                selected.build.time = eventData.build.time;

                selected.diskSpace.status = "N/A";
                selected.diskSpace.free = "";
                selected.diskSpace.total = "";
                selected.diskSpace.threshold = "";


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

                self.populateGraph({
                    items: items,
                    values: values,
                    legends: legends
                },
                    "#chart-instance-memroy-heap",
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

                self.populateGraph({
                    items: items,
                    values: values,
                    legends: legends
                },
                    "#chart-instance-memroy-non-heap",
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

                self.populateGraph({
                    items: items,
                    values: values,
                    legends: legends
                },
                    "#chart-instance-thread",
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


                self.populateGraph({
                    items: items,
                    values: values,
                    legends: legends
                },
                    "#chart-instance-disk-space",
                    function (d) {
                        return self.fileSizeFormat(d);
                    });

                self.setState({
                    selectedInstance: selected,
                    selectedInstanceEventSource: eventSource
                });

            }, function (err) {
                console.log("Monitor Application " + selectedInstance + " Source Error : " + err.error);
            }
        );
    }

    render() {
        let selectedApplicationInstances = this.state.selectedApplicationInstances;
        let selected = this.state.selectedInstance;

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
            <PageTabPane id="instance" labelledby="instance-tab">
                <div class="container-fluid monitor">
                    <div class="row">
                        <div class="col-xxl-12 col-lg-12">
                            <DataTable id="table-instance" width="100%" data={selectedApplicationInstances.instances} onclick={this.onclickInstanceTable}></DataTable>
                        </div>
                    </div>

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
                                                <p class="m-0">Name: {selected.build.name}</p>
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
                                    <div class="bd-highlight pr-10 mr-auto">Memory : Heap</div>
                                </div>

                                <div class="bd-highlight">
                                    <div id="chart-instance-memroy-heap" style={{ height: "150px" }}></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xxl-6 col-lg-6">
                            <div class="d-flex flex-column bd-highlight border info-box">

                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                    <div class="bd-highlight pr-10 mr-auto">Memory : Non heap</div>
                                </div>

                                <div class="bd-highlight">
                                    <div id="chart-instance-memroy-non-heap" style={{ height: "150px" }}></div>
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
                                    <div id="chart-instance-thread" style={{ height: "150px" }}></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xxl-6 col-lg-6">
                            <div class="d-flex flex-column bd-highlight border info-box">

                                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                                    <div class="bd-highlight pr-10 mr-auto">Disk Space</div>
                                </div>

                                <div class="bd-highlight">
                                    <div id="chart-instance-disk-space" style={{ height: "150px" }}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </PageTabPane>
        );

    }

    fileSizeFormat(bytes) {
        var i = Math.floor(Math.log(bytes) / Math.log(1024));
        var value = (bytes / Math.pow(1024, i)).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
        return (value == undefined) ? 0 : value;
    }

    populateGraph(data, id, funTickFormat) {

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

}

export default MonitorInstanceTabPage;