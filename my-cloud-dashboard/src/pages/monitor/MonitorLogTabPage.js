import React from 'react';
import DatePicker from 'react-datepicker';

import PageTabPane from '../../components/dashboard/panel/PageTabPane'
import InfoBox from '../../components/dashboard/InfoBox'
import InfoBoxItem from '../../components/dashboard/InfoBoxItem'

import AppApiRepo from '../../common/AppApiRepo'

import './MonitorPage.css'
import "react-datepicker/dist/react-datepicker.css";

class MonitorLogTabPage extends React.Component {

    constructor(props) {
        super(props);

        var toDate = new Date();
        var fromDate = new Date();
        fromDate.setHours(fromDate.getHours() - 1);

        this.state = {
            fromDate: fromDate,
            toDate: toDate,
            messages: [],
            selectedApplication: null,
            selectedTab: null,
            pageSize: 100,
            search: ""
        }

        this.refreshLogs = this.refreshLogs.bind(this);
        this.handleChangePageSize = this.handleChangePageSize.bind(this);
        this.handleChangeSearch = this.handleChangeSearch.bind(this);
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

    handleChangePageSize(e) {
        this.setState({ pageSize: e.target.value });
    }

    handleChangeSearch(e) {
        this.setState({ search: e.target.value });
    }

    async refreshLogs() {

        var pageSize = 100;

        if (!isNaN(this.state.pageSize) && this.state.pageSize <= 5000) {
            pageSize = this.state.pageSize;
        }

        var path = '/monitor/logs/search?applicationId=' + this.state.selectedApplication
            + '&searchKey=' + this.state.search 
            + '&size=' + pageSize
            + '&fromDate=' + this.state.fromDate.getTime()
            + '&toDate=' + this.state.toDate.getTime();

        const response = await AppApiRepo.fetch(path, 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        });

        var messages = [];

        if (response.status == 200) {

            response.data.forEach(item => {

                var levelClassname = "head-info level-info";

                if (item.logLevel != null) {
                    levelClassname = "head-info level-" + item.logLevel.toLowerCase().trim();
                }

                messages.push(
                    <div class="d-flex flex-row bd-highlight">
                        <div class="datetime">{item.datetime}</div>
                        <div class="d-flex flex-column bd-highlight logbox-detail">
                            <div class="d-flex flex-row bd-highlight">
                                <div class={levelClassname}>{item.logLevel}</div>
                                <div class="head-info thread">{item.threadName}</div>
                                <div class="head-info classname">{item.className}</div>
                            </div>
                            <p class="message">{item.message}</p>
                        </div>
                    </div>
                );

            });

        }

        this.setState({
            messages: messages
        })

    }

    async componentWillReceiveProps(nextProps) {

        let self = this;
        let selectedApplication = nextProps.selectedApplication;
        let selectedTab = nextProps.selectedTab;

        if (selectedTab != "logs-tab" || selectedApplication == null) {
            return "";
        }

        console.log("MonitorLogTabPage selectedApplication: " + selectedApplication + " " + selectedTab);


        this.setState({
            messages: [],
            selectedApplication: selectedApplication[0],
            selectedTab: selectedTab
        })
    }


    render() {
        let self = this;

        return (
            <PageTabPane id="logs" labelledby="logs-tab">
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
                                <button style={{ width: "70px", height: "32px" }} onClick={this.refreshLogs}>Refresh</button>
                            </div>

                            <div class="logs-inputs space" width="400px">

                            </div>

                            <div class="logs-inputs">
                                <input type="text" onChange={this.handleChangePageSize} style={{ width: "70px" }} value={this.state.pageSize} class="form-control" id="inputPagesize" placeholder="Page" ></input>
                            </div>
                        </div>
                    </div>
                    <div class="row logbox overflow-auto" style={{ height: "350px" }}>
                        <div class="d-flex flex-column bd-highlight">
                            {this.state.messages}
                        </div>
                    </div>
                </div>
            </PageTabPane>

        );

    }

}

export default MonitorLogTabPage;