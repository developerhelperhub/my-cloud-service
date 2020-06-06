
import React from 'react';

import Label from '../../components/dashboard/Label'
import PageTabPane from '../../components/dashboard/panel/PageTabPane'
import DataTable from '../../components/table/DataTable'
import InfoBox from '../../components/dashboard/InfoBox'
import InfoBoxItem from '../../components/dashboard/InfoBoxItem'

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
                application: "",
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
                },
                detail: {
                    instanceId: "",
                    appGroupName: "N/A",
                    ipAddr: "",
                    sid: "",
                    homePageUrl: "",
                    vipAddress: "",
                    secureVipAddress: "",
                    countryId: "",
                    hostName: "",
                    overriddenStatus: "",
                    leaseInfo: {
                        renewalIntervalInSecs: 0,
                        durationInSecs: 0,
                        registrationTimestamp: 0,
                        lastRenewalTimestamp: 0,
                        evictionTimestamp: 0,
                        serviceUpTimestamp: 0
                    },
                    lastUpdatedTimestamp: 0,
                    lastDirtyTimestamp: 0,
                    actionType: "",
                    asgName: "",
                    metadata: {
                        managementport: 0
                    },
                    coordinatingDiscoveryServer: false
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

                selected.id = eventData.id;
                selected.status = eventData.status;
                selected.application = eventData.application;
                selected.lastUpdated = eventData.lastUpdated;

                selected.build.version = eventData.build.version;
                selected.build.artifact = eventData.build.artifact;
                selected.build.name = eventData.build.name;
                selected.build.group = eventData.build.group;
                selected.build.time = eventData.build.time;

                selected.diskSpace.free = self.fileSizeFormat(eventData.diskSpace.data.free);
                selected.diskSpace.total = self.fileSizeFormat(eventData.diskSpace.data.total);
                selected.diskSpace.threshold = self.fileSizeFormat(eventData.diskSpace.data.threshold);

                selected.detail.instanceId = eventData.detail.instanceId;
                selected.detail.appGroupName = eventData.detail.appGroupName;
                selected.detail.ipAddr = eventData.detail.ipAddr;
                selected.detail.sid = eventData.detail.sid;
                selected.detail.homePageUrl = eventData.detail.homePageUrl;
                selected.detail.vipAddress = eventData.detail.vipAddress;
                selected.detail.secureVipAddress = eventData.detail.secureVipAddress;
                selected.detail.countryId = eventData.detail.countryId;
                selected.detail.hostName = eventData.detail.hostName;
                selected.detail.overriddenStatus = eventData.detail.overriddenStatus;
                selected.detail.leaseInfo.renewalIntervalInSecs = eventData.detail.leaseInfo.renewalIntervalInSecs;
                selected.detail.leaseInfo.durationInSecs = eventData.detail.leaseInfo.durationInSecs;
                selected.detail.leaseInfo.registrationTimestamp = eventData.detail.leaseInfo.registrationTimestamp;
                selected.detail.leaseInfo.lastRenewalTimestamp = eventData.detail.leaseInfo.lastRenewalTimestamp;
                selected.detail.leaseInfo.evictionTimestamp = eventData.detail.leaseInfo.evictionTimestamp;
                selected.detail.leaseInfo.serviceUpTimestamp = eventData.detail.leaseInfo.serviceUpTimestamp;
                selected.detail.lastUpdatedTimestamp = eventData.detail.lastUpdatedTimestamp;
                selected.detail.lastDirtyTimestamp = eventData.detail.lastDirtyTimestamp;
                selected.detail.actionType = eventData.detail.actionType;
                selected.detail.asgName = eventData.detail.asgName;
                selected.detail.metadata.managementport = eventData.detail.metadata.managementport;
                selected.detail.coordinatingDiscoveryServer = eventData.detail.coordinatingDiscoveryServer;
                
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
                            <InfoBox title="Application Info" label={{ value: selected.status, class: statusLabelClass }}>
                                <InfoBoxItem label="ID">{selected.id}</InfoBoxItem>
                                <InfoBoxItem label="Application">{selected.application}</InfoBoxItem>
                                <InfoBoxItem label="Last Updated">{selected.lastUpdated}</InfoBoxItem>
                                <InfoBoxItem label="Build">

                                    <p class="m-0">Artifact: {selected.build.artifact}</p>
                                    <p class="m-0">Name: {selected.build.name}</p>
                                    <p class="m-0">Group: {selected.build.group}</p>
                                    <p class="m-0">Version: {selected.build.version}</p>
                                    <p class="m-0">Time: {selected.build.time}</p>

                                </InfoBoxItem>
                            </InfoBox>

                            <div class="mb-3"></div>

                            <InfoBox title="Memory : Heap">
                                <div id="chart-instance-memroy-heap" style={{ height: "150px" }}></div>
                            </InfoBox>

                            <div class="mb-3"></div>

                            <InfoBox title="Memory : Non Heap">
                                <div id="chart-instance-memroy-non-heap" style={{ height: "150px" }}></div>
                            </InfoBox>

                        </div>

                        <div class="col-xxl-6 col-lg-6">
                            <InfoBox title="Disk Space">
                                <InfoBoxItem label="Total">{selected.diskSpace.total}</InfoBoxItem>
                                <InfoBoxItem label="Free">{selected.diskSpace.free}</InfoBoxItem>
                                <InfoBoxItem label="Threshold">{selected.diskSpace.threshold}</InfoBoxItem>
                            </InfoBox>

                            <div class="mb-3"></div>

                            <InfoBox title="Instance">
                                <InfoBoxItem label="Id">{selected.detail.instanceId}</InfoBoxItem>
                                <InfoBoxItem label="App Group Name">{selected.detail.appGroupName}</InfoBoxItem>
                                <InfoBoxItem label="IP Address">{selected.detail.ipAddr}</InfoBoxItem>
                                <InfoBoxItem label="SID">{selected.detail.sid}</InfoBoxItem>
                                <InfoBoxItem label="Home Page Url">{selected.detail.homePageUrl}</InfoBoxItem>
                                <InfoBoxItem label="VIP Address">{selected.detail.vipAddress}</InfoBoxItem>
                                <InfoBoxItem label="Country Id">{selected.detail.countryId}</InfoBoxItem>
                                <InfoBoxItem label="Host Name">{selected.detail.hostName}</InfoBoxItem>
                                <InfoBoxItem label="Overridden Status">{selected.detail.overriddenStatus}</InfoBoxItem>
                                <InfoBoxItem label="Lease Info">
                                    <p class="m-0">Renewal Interval (Secs): {selected.detail.leaseInfo.renewalIntervalInSecs}</p>
                                    <p class="m-0">Duration (Secs): {selected.detail.leaseInfo.durationInSecs}</p>
                                    <p class="m-0">Registration Time: {selected.detail.leaseInfo.registrationTimestamp}</p>
                                    <p class="m-0">Last Renewal Time: {selected.detail.leaseInfo.lastRenewalTimestamp}</p>
                                    <p class="m-0">Eviction Time: {selected.detail.leaseInfo.evictionTimestamp}</p>
                                    <p class="m-0">Service Up Time: {selected.detail.leaseInfo.serviceUpTimestamp}</p>
                                </InfoBoxItem>
                                <InfoBoxItem label="Last Updated Time">{selected.detail.lastUpdatedTimestamp}</InfoBoxItem>
                                <InfoBoxItem label="Last Dirty Time">{selected.detail.lastDirtyTimestamp}</InfoBoxItem>
                                <InfoBoxItem label="Action Type">{selected.detail.actionType}</InfoBoxItem>
                                <InfoBoxItem label="ASG Name">{selected.detail.asgName}</InfoBoxItem>
                                <InfoBoxItem label="Coordinating Discovery Server">{selected.detail.coordinatingDiscoveryServer}</InfoBoxItem>
                            </InfoBox>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xxl-6 col-lg-6">

                        </div>
                        <div class="col-xxl-6 col-lg-6">

                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xxl-6 col-lg-6">
                            <InfoBox title="Thread">
                                <div id="chart-instance-thread" style={{ height: "150px" }}></div>
                            </InfoBox>
                        </div>
                        <div class="col-xxl-6 col-lg-6">
                            <InfoBox title="Disk Space">
                                <div id="chart-instance-disk-space" style={{ height: "150px" }}></div>
                            </InfoBox>
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