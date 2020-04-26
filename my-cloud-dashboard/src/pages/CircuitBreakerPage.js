
import React from 'react';


import PagePanel from '../components/dashboard/PagePanel'
import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'

import Table from '../components/table/Table'
import TableHead from '../components/table/TableHead'
import TableBody from '../components/table/TableBody'
import TableTr from '../components/table/TableTr'
import TableTd from '../components/table/TableTd'
import TableTh from '../components/table/TableTh'
import ProgressBar from '../components/ProgressBar'

class CircuitBreakerPage extends React.Component {
    render() {
        return (
            <PageContent>
            <Row>
                <PagePanel title="Users Activity" cols="col-xxl-7 col-lg-4" height={{ value: 200, unit: "px" }} id="dashboard-bar-1">

                </PagePanel>
                <PagePanel title="Visitors" cols="col-xxl-7 col-lg-4" height={{ value: 200, unit: "px" }} id="dashboard-donut-1">

                </PagePanel>

                <PagePanel title="Sales Events" cols="col-xxl-7 col-lg-4" height={{ value: 200, unit: "px" }} id="dashboard-line-1" >

                </PagePanel>
            </Row>

            <Row>
                <PagePanel title="Projects" cols="col-xxl-7 col-lg-12" >
                    <Table>
                        <TableHead>
                            <TableTr>
                                <TableTh width="50%">Project</TableTh>
                                <TableTh width="20%">Status</TableTh>
                                <TableTh width="30%">Activity</TableTh>
                            </TableTr>
                        </TableHead>
                        <TableBody>
                            <TableTr>
                                <TableTd>Admin</TableTd>
                                <TableTd value={{ type: "label", label: "label-danger" }}>Developing</TableTd>
                                <TableTd>
                                    <ProgressBar style="progress-bar-danger progress-bar-striped" percentage="40%">40%</ProgressBar>
                                </TableTd>
                            </TableTr>
                            <TableTr>
                                <TableTd>Gemini</TableTd>
                                <TableTd value={{ type: "label", label: "label-warning" }}>Updating</TableTd>
                                <TableTd>
                                    <ProgressBar style="progress-bar-warning progress-bar-striped" percentage="50%">50%</ProgressBar>
                                </TableTd>
                            </TableTr>
                            <TableTr>
                                <TableTd>Taurus</TableTd>
                                <TableTd value={{ type: "label", label: "label-warning" }}>Updating</TableTd>
                                <TableTd>
                                    <ProgressBar style="progress-bar-warning progress-bar-striped" percentage="80%">80%</ProgressBar>
                                </TableTd>
                            </TableTr>
                            <TableTr>
                                <TableTd>Taurus</TableTd>
                                <TableTd value={{ type: "label", label: "label-success" }}>Support</TableTd>
                                <TableTd>
                                    <ProgressBar style="progress-bar-success progress-bar-striped" percentage="100%">100%</ProgressBar>
                                </TableTd>
                            </TableTr>
                            <TableTr>
                                <TableTd>Virgo</TableTd>
                                <TableTd value={{ type: "label", label: "label-info" }}>Holding</TableTd>

                                <TableTd>
                                    <ProgressBar style="progress-bar-info progress-bar-striped" percentage="90%">90%</ProgressBar>
                                </TableTd>
                            </TableTr>

                        </TableBody>
                    </Table>

                </PagePanel>
            </Row>

        </PageContent>
        );
    }
}

export default CircuitBreakerPage;