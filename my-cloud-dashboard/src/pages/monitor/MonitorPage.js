
import React from 'react';

import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'

import PageTabPanel from '../../components/dashboard/panel/PageTabPanel'
import PageTabHead from '../../components/dashboard/panel/PageTabHead'
import PageTab from '../../components/dashboard/panel/PageTab'
import PageTabContent from '../../components/dashboard/panel/PageTabContent'

import MonitorInfoTabPage from './MonitorInfoTabPage';
import MonitorInstanceTabPage from './MonitorInstanceTabPage';
import MonitorApplicationPage from './MonitorApplicationPage';
import MonitorLogTabPage from './MonitorLogTabPage';



import './MonitorPage.css'

class MonitorPage extends React.Component {

    constructor(props) {
        super(props);

        this.fileSizeFormat = this.fileSizeFormat.bind(this);
        this.onclickApplicationTable = this.onclickApplicationTable.bind(this);
        this.onClickTab = this.onClickTab.bind(this);

        this.state = {
            selectedApplication: null,
            selectedTab: "info-tab"
        }
    }

    fileSizeFormat(bytes) {
        var i = Math.floor(Math.log(bytes) / Math.log(1024));
        var value = (bytes / Math.pow(1024, i)).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
        return (value == undefined) ? 0 : value;
    }

    onClickTab(id) {
        
        this.setState({
            selectedTab: id
        });
    }

    onclickApplicationTable(row) {
        
        if (row != null) {
            
            console.log("MonitorPage onclickApplicationTable: " + row);

            this.setState({
                selectedApplication: row
            });
        }
    }


    render() {

        return (
            <PageContent>
                <Row>
                    <MonitorApplicationPage onclickApplicationTable={this.onclickApplicationTable} ></MonitorApplicationPage>
                </Row>

                <Row>
                    <PageTabPanel cols="col-xxl-12 col-lg-12" >
                        <PageTab id="applicationInfoTab">
                            <PageTabHead onClickTab={this.onClickTab} id="info-tab" active="true" href="#info" controls="info" selected="true" title="Info"></PageTabHead>
                            <PageTabHead onClickTab={this.onClickTab} id="instance-tab" href="#instance" controls="instance" title="Instance"></PageTabHead>
                            <PageTabHead onClickTab={this.onClickTab} id="logs-tab" href="#logs" controls="logs" title="Logs"></PageTabHead>
                        </PageTab>

                        <PageTabContent id="applicationInfoTabContent">
                            <MonitorInfoTabPage selectedTab = {this.state.selectedTab} selectedApplication = {this.state.selectedApplication}></MonitorInfoTabPage>
                            <MonitorInstanceTabPage selectedTab = {this.state.selectedTab} selectedApplication = {this.state.selectedApplication}></MonitorInstanceTabPage>
                            <MonitorLogTabPage selectedTab = {this.state.selectedTab} selectedApplication = {this.state.selectedApplication}></MonitorLogTabPage>
                        </PageTabContent>
                    </PageTabPanel>
                </Row>
            </PageContent>
        );

    }

}

export default MonitorPage;