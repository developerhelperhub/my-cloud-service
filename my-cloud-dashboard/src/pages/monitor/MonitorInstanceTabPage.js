
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

        this.state = {
            selectedApplicationEventSource: null,
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
                            targets: 3
                        }
                    ],
                    head: [
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

        if(selectedTab != "instance-tab" || selectedApplication ==null){
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

    render() {
        let selected = this.state.selectedApplicationInstances;

        return (
            <PageTabPane id="instance" labelledby="instance-tab">
                <div class="container-fluid monitor">
                    <div class="row">
                        <div class="col-xxl-12 col-lg-12">
                            <DataTable id="table-instance" width="100%" data={selected.instances}></DataTable>
                        </div>
                    </div>
                </div>
            </PageTabPane>
        );

    }

}

export default MonitorInstanceTabPage;