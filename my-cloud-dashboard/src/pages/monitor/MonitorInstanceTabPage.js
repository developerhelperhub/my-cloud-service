
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

        this.state = {
            selectedApplicationEventSource: null,
            selectedApplicationInstances: {
                id: "",
                status: "N/A",
                instance: {
                    columnDefs: [
                        {
                            render: function (data, type, row) {
                                return self.renderStatusOnTable(data, type, row);
                            },
                            targets: 2
                        }
                    ],
                    head: [
                        { title: "Instance", width: "30%" },
                        { title: "IP Address", width: "10%" },
                        { title: "Status", width: "10%" },
                        { title: "Ren Int", width: "5%" },
                        { title: "Dur Int", width: "5%" },
                        { title: "Last Updated", width: "30%" },

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

        if(selectedTab != "instance-tab"){
            return "";
        }

        console.log("MonitorInstanceTabPage selectedApplication: " + selectedApplication + " " + selectedTab);
    }

    render() {
        let selectedApplicationInstances = this.state.selectedApplicationInstances;

        return (
            <PageTabPane id="instance" labelledby="instance-tab">
                <div class="container-fluid monitor">
                    <div class="row">
                        <div class="col-xxl-12 col-lg-12">
                            <DataTable id="table-instance" width="100%" data={selectedApplicationInstances.instance}></DataTable>
                        </div>
                    </div>
                </div>
            </PageTabPane>
        );

    }

}

export default MonitorInstanceTabPage;