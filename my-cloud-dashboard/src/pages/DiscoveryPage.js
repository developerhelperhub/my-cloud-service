
import React from 'react';
import Moment from 'react-moment';

import Label from '../components/dashboard/Label'
import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'
import Widget from '../components/dashboard/Widget'

import PagePanel from '../components/dashboard/panel/PagePanel'
import PagePanelHead from '../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../components/dashboard/panel/PagePanelBody'
import PagePanelIcon from '../components/dashboard/panel/PagePanelIcon'

import AppApiRepo from '../common/AppApiRepo'

import DataTable from '../components/table/DataTable'

class DiscoveryPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            totalApplications: "0",
            totalInstances: "0",
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

        this.refreshTable = this.refreshTable.bind(this);
    }

    async refreshTable() {
        
        const response = await AppApiRepo.fetch('/discovery/details', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const tableData = this.state.tableData;
        const body = [];
        var totalApplications = "0";
        var totalInstances = "0"

        if (response.status == 200) {

            response.data.applications.application.forEach(app => {

                app.instance.forEach(instance => {
                    const timestamp = Date(instance.lastUpdatedTimestamp);
                    var labelClass = "label-warning";

                    if (instance.status == "UP") {
                        labelClass = "label-success";
                    } else if (instance.status == "DOWN") {
                        labelClass = "label-danger";
                    }

                    body.push([
                        { value: app.name },
                        { value: <Label class="label-success"> {instance.status} </Label> },
                        { value: instance.homePageUrl },
                        { value: instance.ipAddr },
                        { value: instance.leaseInfo.renewalIntervalInSecs },
                        { value: instance.leaseInfo.durationInSecs },
                        { value: <Moment format="YYYY/MM/DD hh:mm:ss">{timestamp}</Moment> },
                    ]);
                })
            });

            tableData.body = body;

            totalApplications = response.data.totalApplications;
            totalInstances = response.data.totalInstances;
        }



        this.setState({
            totalApplications: totalApplications,
            totalInstances: totalInstances,
            tableData: tableData
        })
    }

    async componentDidMount() {
        this.refreshTable();
    }

    render() {
        return (
            <PageContent>
                <Row>
                    <Widget icon="fa fa-cube" numcount={this.state.totalApplications} title="Applications" subtitle="Running applications" cols="col-xxl-7 col-lg-3" />
                    <Widget icon="fa fa-cubes" numcount={this.state.totalInstances} title="Instances" subtitle="Running instances" cols="col-xxl-7 col-lg-3" />
                </Row>
                <Row>
                    <PagePanel cols="col-xxl-7 col-lg-12" >
                        <PagePanelHead title="Applications">
                            <PagePanelIcon icon="fa fa-refresh" event={this.refreshTable}></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody>
                            <DataTable id="example1" width="100%" data={this.state.tableData}></DataTable>
                        </PagePanelBody>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default DiscoveryPage;