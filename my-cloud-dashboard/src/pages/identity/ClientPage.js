
import React from 'react';
import Moment from 'react-moment';

import Label from '../../components/dashboard/Label'
import PagePanel from '../../components/dashboard/PagePanel'
import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'
import Widget from '../../components/dashboard/Widget'

import AppApiRepo from '../../common/AppApiRepo'

import DataTable from '../../components/table/DataTable'

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
    }

    async componentDidMount() {

        const response = await AppApiRepo.fetch('/identity/clients/', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const tableData = this.state.tableData;
        const body = [];

        console.log(response);

        if (response.status == 200) {

            

            response.data.forEach(data => {

                body.push([
                    { value: data.clientId },
                    { value: data.authorizedGrantTypes},
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

    render() {
        return (
            <PageContent>
                <Row>
                    <PagePanel title="Clients" cols="col-xxl-7 col-lg-12" >
                        <DataTable id="example1" width="100%" data={this.state.tableData}></DataTable>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default ClientPage;