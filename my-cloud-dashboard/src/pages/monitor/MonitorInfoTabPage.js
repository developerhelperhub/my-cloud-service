import React from 'react';

import Label from '../../components/dashboard/Label'
import PageTabPane from '../../components/dashboard/panel/PageTabPane'

import AppApiRepo from '../../common/AppApiRepo'

import './MonitorPage.css'

class MonitorInfoTabPage extends React.Component {

    constructor(props) {
        super(props);

        this.formatDate = this.formatDate.bind(this);
        this.populateGraph = this.populateGraph.bind(this);
        this.fileSizeFormat = this.fileSizeFormat.bind(this);

        this.state = {
            selectedApplicationEventSource: null,
            selectedApplicationInfo: {
                id: "",
                status: "N/A",
                instanceTotal: 0,
                instanceRunning: 0,
                lastUpdated: ""
            }
        }
    }

    componentWillReceiveProps(nextProps) {

        let self = this;
        let selectedApplication = nextProps.selectedApplication;
        let selectedTab = nextProps.selectedTab;
        var eventSource = self.state.selectedApplicationEventSource;

        if(selectedTab != "info-tab" || selectedApplication == null){
            return "";
        }

        console.log("MonitorInfoTabPage selectedApplication: " + selectedApplication + " " + selectedTab);

        if (eventSource != null) {
            eventSource.close();
        }

        eventSource = AppApiRepo.eventSource("/monitor/stream/applications/" + selectedApplication[0] + "/info",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const eventData = JSON.parse(event.data);

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

                self.populateGraph({
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

                self.populateGraph({
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

                self.populateGraph({
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


                self.populateGraph({
                    items: items,
                    values: values,
                    legends: legends
                },
                    "#chart-disk-space",
                    function (d) {
                        return self.fileSizeFormat(d);
                    });

                selected.id = selectedApplication[0];
                selected.status = selectedApplication[3];
                selected.instanceTotal = selectedApplication[2];
                selected.instanceRunning = selectedApplication[1];
                selected.lastUpdated = selectedApplication[4];

                self.setState({
                    selectedApplicationInfo: selected,
                    selectedApplicationEventSource: eventSource
                });

            }, function (err) {
                console.log("Monitor Application " + selectedApplication + " Source Error : " + err.error);
            }
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


    render() {
        let selectedApplicationInfo = this.state.selectedApplicationInfo;

        var statusLabelClass = "label-warning";
        if (selectedApplicationInfo.status == "UP") {
            statusLabelClass = 'label-success';
        } else if (selectedApplicationInfo.status == "DOWN") {
            statusLabelClass = 'label-danger';
        }

        return (
            <PageTabPane id="info" labelledby="info-tab" show="true" active="true">
                <div class="container-fluid monitor">
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

        );

    }

}

export default MonitorInfoTabPage;