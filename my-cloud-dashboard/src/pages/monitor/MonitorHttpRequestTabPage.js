import React from 'react';

import PageTabPane from '../../components/dashboard/panel/PageTabPane'
import InfoBox from '../../components/dashboard/InfoBox'
import InfoBoxItem from '../../components/dashboard/InfoBoxItem'
import DataTable from '../../components/table/DataTable'

import AppApiRepo from '../../common/AppApiRepo'

import './MonitorPage.css'

class MonitorHttpRequestTabPage extends React.Component {

    constructor(props) {
        super(props);

        let self = this;

        this.state = {
            messages: [],
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
        this.populateGraph = this.populateGraph.bind(this);
        this.renderStatusOnTable = this.renderStatusOnTable.bind(this);
        this.refreshAccessLogs = this.refreshAccessLogs.bind(this);
        this.handleChangePageSize = this.handleChangePageSize.bind(this);
        this.handleChangeSearch = this.handleChangeSearch.bind(this);
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

        var path = '/monitor/access-logs/search?applicationId=' + this.state.selectedApplication + '&searchKey=' + this.state.search + '&size=' + pageSize + '&order=desc' + '&group=second';

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

            var requestValues = [];
            var requestData = [];
            var requestLegends = [];

            var methodValues = [];
            var methodsDataPost = [];
            var methodsDataPut = [];
            var methodsDataDelete = [];
            var methodsDataGet = [];
            var methodsDataPatch = [];
            var methodsDataOther = [];
            var methodsLegends = [];

            var statusValues = [];
            var status2x = [];
            var status3x = [];
            var status4x = [];
            var status5x = [];
            var statusx = [];
            var statusLegends = [];

            response.data.matrics.forEach(metric => {

                // Request
                requestValues.push({ time: metric.time, value: metric.request });
                requestData.push({ time: metric.time, value: metric.request });

                //Method
                methodValues.push({ time: metric.time, value: metric.methodPost });
                methodsDataPost.push({ time: metric.time, value: metric.methodPost });

                methodValues.push({ time: metric.time, value: metric.methodPut });
                methodsDataPut.push({ time: metric.time, value: metric.methodPut });

                methodValues.push({ time: metric.time, value: metric.methodDelete });
                methodsDataDelete.push({ time: metric.time, value: metric.methodDelete });

                methodValues.push({ time: metric.time, value: metric.methodGet });
                methodsDataGet.push({ time: metric.time, value: metric.methodGet });

                methodValues.push({ time: metric.time, value: metric.methodPatch });
                methodsDataPatch.push({ time: metric.time, value: metric.methodPatch });

                methodValues.push({ time: metric.time, value: metric.methodOther });
                methodsDataOther.push({ time: metric.time, value: metric.methodOther });

                //Status
                statusValues.push({ time: metric.time, value: metric.status2x });
                status2x.push({ time: metric.time, value: metric.status2x });

                statusValues.push({ time: metric.time, value: metric.status3x });
                status3x.push({ time: metric.time, value: metric.status3x });

                statusValues.push({ time: metric.time, value: metric.status4x });
                status4x.push({ time: metric.time, value: metric.status3x });

                statusValues.push({ time: metric.time, value: metric.status5x });
                status5x.push({ time: metric.time, value: metric.status5x });

                statusValues.push({ time: metric.time, value: metric.statusx });
                statusx.push({ time: metric.time, value: metric.statusx });

            });

            items.push(
                {
                    fill: "none",
                    stroke: "blue",
                    strokeWidth: 1,
                    data: requestData,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            self.populateGraph('line',
                {
                    items: items,
                    values: requestValues,
                    legends: requestLegends
                },
                "#chart-http-request-request",
                function (d) {
                    return d;
                });

            items = [];

            methodsLegends.push({
                value: "POST",
                fill: "#49cc90",
                stroke: "#49cc90",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#49cc90",
                    strokeWidth: 1,
                    data: methodsDataPost,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            methodsLegends.push({
                value: "PUT",
                fill: "#fca130",
                stroke: "#fca130",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#fca130",
                    strokeWidth: 1,
                    data: methodsDataPut,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )


            methodsLegends.push({
                value: "DELETE",
                fill: "#f93e3e",
                stroke: "#f93e3e",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#f93e3e",
                    strokeWidth: 1,
                    data: methodsDataDelete,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            methodsLegends.push({
                value: "GET",
                fill: "#61affe",
                stroke: "#61affe",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#61affe",
                    strokeWidth: 1,
                    data: methodsDataGet,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            methodsLegends.push({
                value: "PATCH",
                fill: "#50e3c2",
                stroke: "#50e3c2",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#50e3c2",
                    strokeWidth: 1,
                    data: methodsDataPatch,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            methodsLegends.push({
                value: "OTHER",
                fill: "#4532ea",
                stroke: "#4532ea",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#4532ea",
                    strokeWidth: 1,
                    data: methodsDataOther,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            self.populateGraph('line',
                {
                    items: items,
                    values: methodValues,
                    legends: methodsLegends
                },
                "#chart-http-request-method",
                function (d) {
                    return d;
                });


            items = [];

            statusLegends.push({
                value: "2x",
                fill: "#49cc90",
                stroke: "#49cc90",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#49cc90",
                    strokeWidth: 1,
                    data: status2x,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            statusLegends.push({
                value: "3x",
                fill: "#fca130",
                stroke: "#fca130",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#fca130",
                    strokeWidth: 1,
                    data: status3x,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            statusLegends.push({
                value: "4x",
                fill: "#61affe",
                stroke: "#61affe",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#61affe",
                    strokeWidth: 1,
                    data: status4x,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            statusLegends.push({
                value: "5x",
                fill: "#f93e3e",
                stroke: "#f93e3e",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#f93e3e",
                    strokeWidth: 1,
                    data: status5x,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            statusLegends.push({
                value: "x",
                fill: "#4532ea",
                stroke: "#4532ea",
                strokeWidth: 1
            })
            items.push(
                {
                    fill: "none",
                    stroke: "#4532ea",
                    strokeWidth: 1,
                    data: statusx,
                    x: function (d) { return d.time },
                    y: function (d) { return d.value }
                }
            )

            self.populateGraph('line',
                {
                    items: items,
                    values: statusValues,
                    legends: statusLegends
                },
                "#chart-http-request-status",
                function (d) {
                    return d;
                });
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

        return (
            <PageTabPane id="http-request" labelledby="http-request-tab">
                <div class="container-fluid monitor">
                    <div class="row">
                        <div class="d-flex flex-row bd-highlight flex-grow-1 logs-inputs-box">
                            <div class="logs-inputs flex-grow-1">
                                <input type="text" onChange={this.handleChangeSearch} class="form-control" id="inputSearch" placeholder="Search"></input>
                            </div>

                            <div class="logs-inputs">
                                <button style={{ width: "70px", height: "32px" }} onClick={this.refreshAccessLogs}>Refresh</button>
                            </div>

                            <div class="logs-inputs space" width="400px">

                            </div>

                            <div class="logs-inputs">
                                <input type="text" onChange={this.handleChangePageSize} style={{ width: "70px" }} class="form-control" id="inputPagesize" placeholder="Page" ></input>
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

    populateGraph(type, data, id, funTickFormat) {

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