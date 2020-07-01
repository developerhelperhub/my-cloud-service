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

        var pageSize = 100;
        var tableBody = [];

        if (!isNaN(this.state.pageSize) && this.state.pageSize <= 5000) {
            pageSize = this.state.pageSize;
        }

        var path = '/monitor/access-logs/search?applicationId=' + this.state.selectedApplication + '&searchKey=' + this.state.search + '&size=' + pageSize + '&order=desc';

        const response = await AppApiRepo.fetch(path, 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        });

        var table = this.state.table;
        table.body = [];

        if (response.status == 200) {

            response.data.forEach(item => {

                var colums = [];

                colums.push(item.datetime);
                colums.push(item.requestMethod);
                colums.push(item.requestUrl);
                colums.push(item.statusCode);
                colums.push(item.timeProcessRequestMills);
                colums.push(item.timeCommitResponseMills);

                table.body.push(colums);
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
                    <DataTable id="table-http-request" width="100%" data={this.state.table}></DataTable>
                </div>
            </PageTabPane>

        );

    }

}

export default MonitorHttpRequestTabPage;