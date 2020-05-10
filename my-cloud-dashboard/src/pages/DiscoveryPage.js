
import React from 'react';
import Moment from 'react-moment';

import Label from '../components/dashboard/Label'
import PagePanel from '../components/dashboard/PagePanel'
import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'

import AppApiRepo from '../common/AppApiRepo'

import DataTable from '../components/table/DataTable'

class DiscoveryPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            tableData: {
                head: [
                    { title: "Name", width: "20%" },
                    { title: "Status", width: "10%" },
                    { title: "Instance", width: "30%" },
                    { title: "IP Address", width: "10%" },
                    { title: "Ren Int", width: "5%" },
                    { title: "Dur Int", width: "5%" },
                    { title: "Last Updated", width: "30%" },

                ],
                body: [
                    [
                        { value: "-" },
                        { value: "-" },
                        { value: "-" },
                        { value: "-" },
                        { value: "-" },
                        { value: "-" },
                        { value: "-" },
                    ]
                ]
            }
        }
    }

    async componentDidMount() {

        const response = await AppApiRepo.fetch('/discovery/applications', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const tableData = this.state.tableData;
        const body = [];

        response.data.application.forEach(app => {

            app.instance.forEach(instance => {
                const timestamp = Date(instance.lastUpdatedTimestamp);
                var labelClass = "label-warning";
                
                if(instance.status=="UP"){
                    labelClass = "label-success";
                } else if(instance.status=="DOWN"){
                    labelClass = "label-danger";
                }

                body.push([
                    { value: app.name },
                    { value: <Label class="label-success"> {instance.status} </Label>},
                    { value: instance.homePageUrl },
                    { value: instance.ipAddr },
                    { value: instance.leaseInfo.renewalIntervalInSecs },
                    { value: instance.leaseInfo.durationInSecs },
                    { value: <Moment format="YYYY/MM/DD hh:mm:ss">{timestamp}</Moment> },
                ]);
            })
        });

        tableData.body = body;

        this.setState({
            tableData: tableData
        })
    }

    render() {
        return (
            <PageContent>
                <Row>
                    <PagePanel title="Applications" cols="col-xxl-7 col-lg-12" >
                        <DataTable id="example1" width="100%" data={this.state.tableData}></DataTable>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default DiscoveryPage;