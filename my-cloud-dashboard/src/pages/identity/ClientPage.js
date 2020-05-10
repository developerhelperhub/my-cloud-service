import React from 'react';

import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'

import PagePanel from '../../components/dashboard/panel/PagePanel'
import PagePanelHead from '../../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../../components/dashboard/panel/PagePanelBody'
import PagePanelIcon from '../../components/dashboard/panel/PagePanelIcon'

import DataTable from '../../components/table/DataTable'

import AppApiRepo from '../../common/AppApiRepo'


class ClientPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            tableData: {
                head: [
                    { title: "Id", width: "20%" },
                    { title: "Grand Types", width: "20%" },
                    { title: "Scops", width: "20%" },
                    { title: "Redirect URI", width: "40%" },
                    { title: "Access Token", width: "20%" },
                    { title: "Refresh Token", width: "20%" },

                ],
                body: [
                    [
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

        const response = await AppApiRepo.fetch('/identity/clients/', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const tableData = this.state.tableData;
        const body = [];

        if (response.status == 200) {

            response.data.forEach(data => {

                body.push([
                    { value: data.clientId },
                    { value: data.authorizedGrantTypes },
                    { value: data.scope },
                    { value: data.registeredRedirectUri },
                    { value: data.accessTokenValiditySeconds },
                    { value: data.refreshTokenValiditySeconds },
                ]);

            });

            tableData.body = body;
        }



        this.setState({
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
                    <PagePanel cols="col-xxl-7 col-lg-12" >
                        <PagePanelHead title="Client">
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

export default ClientPage;