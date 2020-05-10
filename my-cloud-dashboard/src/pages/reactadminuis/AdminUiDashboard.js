import React from 'react';

import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'
import Widget from '../../components/dashboard/Widget'

import Table from '../../components/table/Table'
import TableHead from '../../components/table/TableHead'
import TableBody from '../../components/table/TableBody'
import TableTr from '../../components/table/TableTr'
import TableTd from '../../components/table/TableTd'
import TableTh from '../../components/table/TableTh'
import ProgressBar from '../../components/ProgressBar'

import PagePanel from '../../components/dashboard/panel/PagePanel'
import PagePanelHead from '../../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../../components/dashboard/panel/PagePanelBody'
import PagePanelIcon from '../../components/dashboard/panel/PagePanelIcon'


class AdminUiDashboard extends React.Component {
    render() {
        return (
            <PageContent>
                <Row>

                    <Widget icon="fa fa-envelope" numcount="48" title="New messages" subtitle="In your mailbox" cols="col-xxl-7 col-lg-3" />
                    <Widget icon="fa fa-user" numcount="356" title="REGISTRED USERS" subtitle="On your website" cols="col-xxl-7 col-lg-3" />

                </Row>
                <Row>
                    <PagePanel cols="col-xxl-7 col-lg-4">
                        <PagePanelHead title="Users Activity">
                            <PagePanelIcon icon="fa fa-refresh"></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody height={{ value: 200, unit: "px" }} id="dashboard-bar-1">
                        </PagePanelBody>
                    </PagePanel>

                    <PagePanel cols="col-xxl-7 col-lg-4">
                        <PagePanelHead title="Visitors">
                            <PagePanelIcon icon="fa fa-refresh"></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody height={{ value: 200, unit: "px" }} id="dashboard-donut-1">
                        </PagePanelBody>
                    </PagePanel>

                    <PagePanel cols="col-xxl-7 col-lg-4" height={{ value: 200, unit: "px" }}>
                        <PagePanelHead title="Sales Events">
                            <PagePanelIcon icon="fa fa-refresh"></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody height={{ value: 200, unit: "px" }} id="dashboard-line-1">
                        </PagePanelBody>
                    </PagePanel>

                </Row>
                <Row>
                    <PagePanel cols="col-xxl-7 col-lg-12" >
                        <PagePanelHead title="Projects">
                            <PagePanelIcon icon="fa fa-refresh"></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody >
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
                        </PagePanelBody>
                    </PagePanel>
                </Row>

            </PageContent>
        );
    }
}

export default AdminUiDashboard;