
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

class DiscoveryPage extends React.Component {

    render() {
        return (
            <PageContent>
                <Row>
                    <PagePanel title="Applications" cols="col-xxl-7 col-lg-12" >
                        <Table>
                            <TableHead>
                                <TableTr>
                                    <TableTh width="20%">Name</TableTh>
                                    <TableTh width="10%">Status</TableTh>
                                    <TableTh width="70%">Instance</TableTh>
                                </TableTr>
                            </TableHead>
                            <TableBody>
                                <TableTr>
                                    <TableTd>INVENTORY-SERVICE</TableTd>
                                    <TableTd value={{ type: "label", label: "label-danger" }}>DOWN</TableTd>
                                    <TableTd>
                                    192.168.0.103:inventory-service:8084
                                    </TableTd>
                                </TableTr>
                                <TableTr>
                                    <TableTd>SALES-SERVICE</TableTd>
                                    <TableTd value={{ type: "label", label: "label-success" }}>UP</TableTd>
                                    <TableTd>
                                    192.168.0.103:sales-service:808
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

export default DiscoveryPage;