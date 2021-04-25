import React from 'react';
import Form from '../../components/form/Form'
import Input from '../../components/form/Input'
import Checkbox from '../../components/form/Checkbox'
import Select from '../../components/form/Select'


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

        this.actionRefresh = this.actionRefresh.bind(this);
        this.actionAdd = this.actionAdd.bind(this);
        this.actionSubmit = this.actionSubmit.bind(this);
        this.actionClear = this.actionClear.bind(this);

        this.state = {
            formData: {

                actions: {
                    add: this.actionAdd,
                    refresh: this.actionRefresh,
                    delete: this.actionDelete,
                    edit: this.actionEdit,
                    submit: this.actionSubmit,
                    clear: this.actionClear,
                },

                tableData: {
                    head: [
                        { title: "Id", width: "20%" },
                        { title: "Grand Types", width: "20%" },
                        { title: "Scops", width: "20%" },
                        { title: "Redirect URI", width: "40%" },
                        { title: "Access Token", width: "20%" },
                        { title: "Refresh Token", width: "20%" }
                    ],
                    body: []
                }
            }
        }
    }

    async actionSubmit(e) {
        console.log('submit');
    }

    async actionClear(e) {
        console.log('clear');
    }

    async actionRefresh(e) {


        const response = await AppApiRepo.fetch('/identity/clients/', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const formData = this.state.formData;
        const body = [];

        if (response.status == 200) {

            response.data.forEach(data => {
                var colums = [];

                colums.push(data.clientId);
                colums.push(data.authorizedGrantTypes);
                colums.push(data.scope);
                colums.push(data.registeredRedirectUri);
                colums.push(data.accessTokenValiditySeconds);
                colums.push(data.refreshTokenValiditySeconds);

                colums.push(data.clientId);

                body.push(colums);
            });

            formData.tableData.body = body;
        }

        this.setState({
            formData: formData
        });
    }

    async componentDidMount() {
        this.actionRefresh();
    }

    actionAdd(e) {
        console.log('Add');
    }

    actionEdit(e, id) {
        console.log('Edit: ' + id);
    }

    actionDelete(e, id) {
        console.log('Delete: ' + id);
    }

    render() {
        return (<Form title="Client" data={this.state.formData} >
            <div class="row">
                <div class="col">
                    <Input type="text" id="id" label="Id" />
                </div>
                <div class="col">
                    <Checkbox id="secretRequired" label="Secret Required" />
                </div>
                <div class="col">
                    <Input type="password" id="id" label="Secret" />
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <Select id="resourceIds" label="Resource Ids" values={["my_cloud_identity_id", "my_cloud_api_gateway_id",
				"my_cloud_discovery_id", "my_cloud_circuit_breaker_id", "inventory_service_resource_id",
				"api_gateway_resource_id", "sales_service_resource_id", "my_cloud_monitor_id"]}/>
                </div>
                <div class="col">
                    <Checkbox id="scop" label="Scop" />
                </div>
                <div class="col">
                    <Select id="scops" label="Scops" values={["user_info", "ADMIN"]}/>
                </div>
            </div>

            <div class="row">
                <div class="col">
                    <Select id="authorizedGrantTypes" label="Authorized Grant Types" values={["authorization_code", "password", "refresh_token", "client_credentials"]} />
                </div>
                <div class="col">
                    <Input type="text" id="registeredRedirectUri" label="Registered Redirect Uri (Comma separator)" />
                </div>
                <div class="col">
                    <Select id="grantedAuthorities" label="Granted Authorities" values={["USER", "ADMIN"]}/>
                </div>
            </div>

            <div class="row">
                <div class="col">
                    <Input type="text" id="accessTokenValiditySeconds" label="Access Token Validity Seconds" />
                </div>
                <div class="col">
                    <Input type="text" id="refreshTokenValiditySeconds" label="Refresh Validity Seconds" />
                </div>
                <div class="col">
                    <Checkbox id="autoApprove" label="Auto Approve" />
                </div>
            </div>


            
        </Form>)
    }
}

export default ClientPage;