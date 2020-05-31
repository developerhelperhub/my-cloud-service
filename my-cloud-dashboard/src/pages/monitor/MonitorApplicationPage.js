
import React from 'react';
import Moment from 'react-moment';
import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';

import PagePanel from '../../components/dashboard/panel/PagePanel'
import PagePanelHead from '../../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../../components/dashboard/panel/PagePanelBody'


import AppApiRepo from '../../common/AppApiRepo'

import DataTable from '../../components/table/DataTable'

import './MonitorPage.css'

class MonitorApplicationPage extends React.Component {

    constructor(props) {
        super(props);

        this.streamApplications = this.streamApplications.bind(this);
        this.onclickApplicationTable = this.onclickApplicationTable.bind(this);
        this.renderStatusOnTable = this.renderStatusOnTable.bind(this);

        let self = this;

        this.state = {
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

                self.state.applicationData = applicationData;

                self.setState({
                    applicationData: applicationData
                })

            }, function (err) {
                console.log("Stream Applications Event Source Error : " + err.error);
            }
        );


    }

    onclickApplicationTable(row) {
        this.props.onclickApplicationTable(row);
    }

    render() {

        return (
            <PagePanel cols="col-xxl-12 col-lg-12" >
                <PagePanelHead title="Applications">
                </PagePanelHead>
                <PagePanelBody>
                    <DataTable id="table-application" width="100%" data={this.state.applicationData} onclick={this.onclickApplicationTable}></DataTable>
                </PagePanelBody>
            </PagePanel>
        );

    }

}

export default MonitorApplicationPage;