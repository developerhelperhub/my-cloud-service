import React, { useState } from 'react';
import DatePicker from 'react-datepicker';

import PageTabPane from '../../components/dashboard/panel/PageTabPane'
import InfoBox from '../../components/dashboard/InfoBox'
import InfoBoxItem from '../../components/dashboard/InfoBoxItem'
import DataTable from '../../components/table/DataTable'

import AppApiRepo from '../../common/AppApiRepo'

import './MonitorPage.css'
import "react-datepicker/dist/react-datepicker.css";

class MonitorHttpRequestTabPage extends React.Component {

    constructor(props) {
        super(props);

        let self = this;

        var toDate = new Date();
        var fromDate = new Date();
        fromDate.setHours(fromDate.getHours() - 1);

        this.state = {
            messages: [],
            fromDate: fromDate,
            toDate: toDate,
            timeGroup: 'minute',
            selectedApplication: null,
            selectedTab: null,
            pageSize: 100,
            search: "",
            table: {
                columnDefs: [
                    {
                        render: function (data, type, row) {
                            return self.renderStatusOnTable(data, type, row);
                        },
                        targets: 1
                    }
                ],
                head: [
                    { title: "Time", width: "10%" },
                    { title: "Method", width: "10%" },
                    { title: "URL", width: "60%" },
                    { title: "Status", width: "10%" },
                    { title: "Process Request", width: "10%" },
                    { title: "Commit Response", width: "10%" }
                ],
                body: []
            }
        }

        this.formatDate = this.formatDate.bind(this);
        this.populateLineGraph = this.populateLineGraph.bind(this);
        this.populateBarGraph = this.populateBarGraph.bind(this);
        this.d3BarChartStacked = this.d3BarChartStacked.bind(this);
        this.renderStatusOnTable = this.renderStatusOnTable.bind(this);
        this.refreshAccessLogs = this.refreshAccessLogs.bind(this);
        this.handleChangePageSize = this.handleChangePageSize.bind(this);
        this.handleChangeSearch = this.handleChangeSearch.bind(this);
        this.handleTimeGroup = this.handleTimeGroup.bind(this);
        this.setFromDate = this.setFromDate.bind(this);
        this.setToDate = this.setToDate.bind(this);

    }

    setFromDate(date) {
        this.setState({
            fromDate: date
        })
    }

    setToDate(date) {
        this.setState({
            toDate: date
        })
    }

    handleTimeGroup(e) {
        this.setState({ timeGroup: e.target.value });
    }

    renderStatusOnTable(data, type, row) {
        if (data == "POST") {
            return '<span class="label label-http-method-post">' + data + '</span>';
        } else if (data == "PUT") {
            return '<span class="label label-http-method-put">' + data + '</span>';
        } else if (data == "GET") {
            return '<span class="label label-http-method-get">' + data + '</span>';
        } else if (data == "DELETE") {
            return '<span class="label label-http-method-delete">' + data + '</span>';
        } else if (data == "PATCH") {
            return '<span class="label label-http-method-patch">' + data + '</span>';
        } else {
            return '<span class="label label-http-method-other">' + data + '</span>';
        }
    }

    handleChangePageSize(e) {
        this.setState({ pageSize: e.target.value });
    }

    handleChangeSearch(e) {
        this.setState({ search: e.target.value });
    }

    async refreshAccessLogs() {

        let self = this;
        var pageSize = 100;
        var tableBody = [];

        if (!isNaN(this.state.pageSize) && this.state.pageSize <= 5000) {
            pageSize = this.state.pageSize;
        }

        console.log('this.state.fromDate:' + this.state.fromDate.getTime());
        console.log('this.state.toDate:' + this.state.toDate.getTime());

        var path = '/monitor/access-logs/search?applicationId=' + this.state.selectedApplication
            + '&searchKey=' + this.state.search
            + '&size=' + pageSize
            + '&fromDate=' + this.state.fromDate.getTime()
            + '&toDate=' + this.state.toDate.getTime()
            + '&order=desc'
            + '&group=' + this.state.timeGroup;

        const response = await AppApiRepo.fetch(path, 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        });

        var table = this.state.table;
        table.body = [];

        if (response.status == 200) {

            response.data.messages.forEach(item => {

                var colums = [];

                colums.push(item.datetimeFormatted);
                colums.push(item.requestMethod);
                colums.push(item.requestUrl);
                colums.push(item.statusCode);
                colums.push(item.timeProcessRequestMills);
                colums.push(item.timeCommitResponseMills);

                table.body.push(colums);
            });


            var items = [];

            var requestData = [];
            var methods = [];
            var methodsBars = [];
            var methodMax = 0;

            var status = [];
            var statusBars = [];
            var statusMax = 0;

            response.data.matrics.forEach(metric => {

                // Request
                requestData.push({ time: metric.time, value: metric.request });

                //Method
                var max = 0;

                if (metric.methodPost > max) {
                    max = metric.methodPost;
                }
                if (metric.methodPut > max) {
                    max = metric.methodPut;
                }
                if (metric.methodDelete > max) {
                    max = metric.methodDelete;
                }
                if (metric.methodGet > max) {
                    max = metric.methodGet;
                }
                if (metric.methodPatch > max) {
                    max = metric.methodPatch;
                }
                if (metric.methodOther > max) {
                    max = metric.methodOther;
                }

                if (max > methodMax) {
                    methodMax = max;
                }

                methods.push({
                    time: metric.time,
                    methodPost: metric.methodPost,
                    methodPut: metric.methodPut,
                    methodDelete: metric.methodDelete,
                    methodGet: metric.methodGet,
                    methodPatch: metric.methodPatch,
                    methodOther: metric.methodOther
                })

                //Status
                max = 0;
                if (metric.status1x > max) {
                    max = metric.status1x;
                }
                if (metric.status2x > max) {
                    max = metric.status2x;
                }
                if (metric.status3x > max) {
                    max = metric.status3x;
                }
                if (metric.status4x > max) {
                    max = metric.status4x;
                }
                if (metric.status5x > max) {
                    max = metric.status5x;
                }
                if (metric.statusx > max) {
                    max = metric.statusx;
                }

                if (max > statusMax) {
                    statusMax = max;
                }

                status.push({
                    time: metric.time,
                    status1x: metric.status1x,
                    status2x: metric.status2x,
                    status3x: metric.status3x,
                    status4x: metric.status4x,
                    status5x: metric.status5x,
                    statusx: metric.statusx,
                });

            });

            self.populateBarGraph(
                requestData,
                "#chart-http-request-request",
                function (d) {
                    return d;
                });

            methodsBars.push({
                value: "POST",
                name: "methodPost",
                fill: "#49cc90",
                stroke: "#49cc90",
                strokeWidth: 1
            })

            methodsBars.push({
                value: "PUT",
                name: "methodPut",
                fill: "#fca130",
                stroke: "#fca130",
                strokeWidth: 1
            })

            methodsBars.push({
                value: "DELETE",
                name: "methodDelete",
                fill: "#f93e3e",
                stroke: "#f93e3e",
                strokeWidth: 1
            })

            methodsBars.push({
                value: "GET",
                name: "methodGet",
                fill: "#61affe",
                stroke: "#61affe",
                strokeWidth: 1
            })

            methodsBars.push({
                value: "PATCH",
                name: "methodPatch",
                fill: "#990099",
                stroke: "#990099",
                strokeWidth: 1
            })

            methodsBars.push({
                value: "OTHER",
                name: "methodOther",
                fill: "#4532ea",
                stroke: "#4532ea",
                strokeWidth: 1
            })

            self.d3BarChartStacked(
                {
                    items: methods,
                    bars: methodsBars,
                    yDomainMax: methodMax
                },
                "#chart-http-request-method");


            statusBars.push({
                value: "1x",
                name: "status1x",
                fill: "#990099",
                stroke: "#990099",
                strokeWidth: 1
            })
            statusBars.push({
                value: "2x",
                name: "status2x",
                fill: "#49cc90",
                stroke: "#49cc90",
                strokeWidth: 1
            })
            statusBars.push({
                value: "3x",
                name: "status3x",
                fill: "#fca130",
                stroke: "#fca130",
                strokeWidth: 1
            })
            statusBars.push({
                value: "4x",
                name: "status4x",
                fill: "#61affe",
                stroke: "#61affe",
                strokeWidth: 1
            })
            statusBars.push({
                value: "5x",
                name: "status5x",
                fill: "#f93e3e",
                stroke: "#f93e3e",
                strokeWidth: 1
            })
            statusBars.push({
                value: "x",
                name: "statusx",
                fill: "#4532ea",
                stroke: "#4532ea",
                strokeWidth: 1
            })

            self.d3BarChartStacked(
                {
                    items: status,
                    bars: statusBars,
                    yDomainMax: statusMax
                },
                "#chart-http-request-status");

        }

        this.setState({
            table: table
        });

    }

    async componentWillReceiveProps(nextProps) {

        let self = this;
        let selectedApplication = nextProps.selectedApplication;
        let selectedTab = nextProps.selectedTab;

        console.log("MonitorHttpRequestTabPage selectedApplication: " + selectedApplication + " " + selectedTab);

        if (selectedTab != "http-request-tab" || selectedApplication == null) {
            return "";
        }

        var table = this.state.table;
        table.body = [];

        this.setState({
            table: table,
            selectedApplication: selectedApplication[0],
            selectedTab: selectedTab
        })
    }


    render() {

        let self = this;

        return (
            <PageTabPane id="http-request" labelledby="http-request-tab">
                <div class="container-fluid monitor">
                    <div class="row">
                        <div class="d-flex flex-row bd-highlight flex-grow-1 logs-inputs-box">
                            <div class="logs-inputs flex-grow-1">
                                <input type="text" onChange={this.handleChangeSearch} class="form-control" id="inputSearch" placeholder="Search"></input>
                            </div>

                            <div class="logs-inputs">
                                <label>From :</label>
                                <DatePicker
                                    selected={this.state.fromDate}
                                    onChange={date => self.setFromDate(date)}
                                    locale="pt-BR"
                                    showTimeSelect
                                    timeFormat="p"
                                    timeIntervals={15}
                                    dateFormat="Pp"
                                />
                            </div>

                            <div class="logs-inputs">
                                <label>To :</label>
                                <DatePicker
                                    selected={this.state.toDate}
                                    onChange={date => self.setToDate(date)}
                                    locale="pt-BR"
                                    showTimeSelect
                                    timeFormat="p"
                                    timeIntervals={15}
                                    dateFormat="Pp"
                                />
                            </div>

                            <div class="logs-inputs">
                                <button style={{ width: "70px", height: "32px" }} onClick={this.refreshAccessLogs}>Refresh</button>
                            </div>

                            <div class="logs-inputs">
                                <select class="form-control" id="time-group" onChange={this.handleTimeGroup} value={this.state.timeGroup}>
                                    <option>hour</option>
                                    <option>minute</option>
                                    <option>second</option>
                                </select>
                            </div>

                            <div class="logs-inputs">
                                <input type="text" onChange={this.handleChangePageSize} style={{ width: "70px" }} value={this.state.pageSize} class="form-control" id="inputPagesize" placeholder="Page" ></input>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col">
                            <InfoBox title="Http Requests">
                                <div id="chart-http-request-request" style={{ height: "150px" }}></div>
                            </InfoBox>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col">
                            <InfoBox title="Http Method">
                                <div id="chart-http-request-method" style={{ height: "150px" }}></div>
                            </InfoBox>
                        </div>
                        <div class="col">
                            <InfoBox title="Http Status">
                                <div id="chart-http-request-status" style={{ height: "150px" }}></div>
                            </InfoBox>
                        </div>
                    </div>
                    <DataTable id="table-http-request" width="100%" data={this.state.table}></DataTable>
                </div>
            </PageTabPane>

        );

    }

    d3BarChartStacked(data, id) {

        window.d3BarChartStacked({
            id: id,
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            fill: 'steelblue',
            stroke: "steelblue",
            strokeWidth: "1px",
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
                    domain: function (width, height) {
                        return [0, data.yDomainMax];
                    }
                },
                stroke: "#818896",
                fill: "none",
                text: "#818896",
                font: "10px Arial"
            },
            bars: {
                legend: {
                    transform: function (d, i, width, height) {
                        return 'translate(' + 0 + ',' + (i * 20) + ')';
                    },
                    rect: {
                        width: 10,
                        height: 10,
                        x: function (width, height) {
                            return width - 40;
                        },
                        y: function (width, height) {
                            return 0;
                        }
                    },
                    text: {
                        x: function (width, height) {
                            return width - 20;
                        },
                        y: function (width, height) {
                            return 10;
                        },
                        value: function (d) {
                            return d.value;
                        },
                        font: "10px arial",
                        fill: "#818896"
                    }
                },
                data: data.bars
            },
            x: function (d) { return d.time }
        });

    }

    populateBarGraph(data, id) {

        window.d3BarChart({
            id: id,
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            fill: 'steelblue',
            stroke: "steelblue",
            strokeWidth: "1px",
            data: data,
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
            extent: function (d) { return d.time; },
            max: function (d) { return d.value; },
            x: function (d) { return d.time },
            y: function (d) { return d.value }
        });

    }

    populateLineGraph(type, data, id, funTickFormat) {

        let self = this;

        window.d3LinesChart({
            id: id,
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            type: type,
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
            extent: function (d) { return d.time; },
            max: function (d) { return d.value; },
        });
    }

    formatDate(time) {
        return window.d3.timeParse("%Y-%m-%d %H:%M:%S")(time);
    }

}

export default MonitorHttpRequestTabPage;