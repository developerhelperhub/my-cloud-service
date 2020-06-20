import React from 'react';

import PageTabPane from '../../components/dashboard/panel/PageTabPane'
import InfoBox from '../../components/dashboard/InfoBox'
import InfoBoxItem from '../../components/dashboard/InfoBoxItem'

import AppApiRepo from '../../common/AppApiRepo'

import './MonitorLogTabPage.css'

class MonitorLogTabPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            messages: [],
            pageSize: 100,
            selectedApplication: null,
            selectedTab: null
        }

        this.refreshLogs = this.refreshLogs.bind(this);
    }

    async refreshLogs () {

        const response = await AppApiRepo.fetch('/monitor/logs/search?applicationId=my-cloud-discovery&size=100', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        console.log("MonitorLogTabPage selectedApplication: " + this.state.selectedApplication + " " + this.state.selectedTab + " API status: " + response.status);

        var messages = [];

        if (response.status == 200) {

            response.data.forEach(item => {

                var levelClassname = "head-info level-" + item.logLevel.toLowerCase();

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

        this.setState({
            messages: [],
            selectedApplication: selectedApplication,
            selectedTab: selectedTab
        })

        this.refreshLogs();
        
    }


    render() {

        return (
            <PageTabPane id="logs" labelledby="logs-tab" show="false" active="false">
                <div class="container-fluid monitor">
                    <div class="row">
                        <div class="d-flex flex-row bd-highlight flex-grow-1 logs-inputs-box">
                            <div class="logs-inputs flex-grow-1">
                                <input type="text" class="form-control" id="inputSearch" placeholder="Search"></input>
                            </div>

                            <div class="logs-inputs">
                                <button style={{ width: "70px", height: "32px" }} onClick={this.refreshLogs}>Refresh</button>
                            </div>

                            <div class="logs-inputs space" width="400px">

                            </div>

                            <div class="logs-inputs">
                                <input type="text" style={{ width: "70px" }} class="form-control" id="inputSearch" placeholder="Page" value={this.state.pageSize}></input>
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