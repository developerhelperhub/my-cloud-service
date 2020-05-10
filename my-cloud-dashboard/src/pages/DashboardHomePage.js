
import React from 'react';

import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'
import Widget from '../components/dashboard/Widget'

import AppApiRepo from '../common/AppApiRepo'

class DashboardHomePage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            data : {
                totalApplications: "0",
                totalInstances: "0",
            }
        }
    }

    async componentDidMount() {

        const response = await AppApiRepo.fetch('/discovery/summary', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        console.log(response.data);

        this.setState({
            data: {
                totalApplications: "" + response.data.totalApplications,
                totalInstances: "" + response.data.totalInstances,
            }
        })
    }

    render() {
        return (
            <PageContent>
                <Row>
                    <Widget icon="fa fa-cube" numcount={this.state.data.totalApplications} title="Applications" subtitle="Running applications" cols="col-xxl-7 col-lg-3" />
                    <Widget icon="fa fa-cubes" numcount={this.state.data.totalInstances} title="Instances" subtitle="Running instances" cols="col-xxl-7 col-lg-3" />
                </Row>
            </PageContent>
        );

    }
}

export default DashboardHomePage;