import React from 'react';
import Form from '../../components/Form'

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

        this.state = {
            formData: {

                actions: {
                    add: this.actionAdd,
                    refresh: this.actionRefresh,
                    delete: this.actionDelete,
                    edit: this.actionEdit,
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
        return (<Form title="Client" data={this.state.formData}>

            <div class="form-floating">
                <input type="text" class="form-control" placeholder="Client Id" />
                <label for="floatingInput">Client Id</label>
            </div>


            {/* <div class="form-group">
                <label for="clientId">Client Id</label>
                <input type="text" class="form-control" id="clientId" aria-describedby="clientIdHelp" placeholder="Enter client id" />
            </div>

            <div class="form-group form-check">
                <input type="checkbox" class="form-check-input" id="secretRequired" />
                <label class="form-check-label" for="secretRequired">Secret Required</label>
            </div>

            

            <div class="form-group">
                <label for="clientSecret">Client Secret</label>
                <input type="password" class="form-control" id="clientSecret" placeholder="Enter client secret" />
            </div>

            <div class="form-group">
                <label for="resourceIds">Resource Ids</label>
                <select class="form-control" id="resourceIds">
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                    <option>4</option>
                    <option>5</option>
                </select>
            </div>

            <div class="form-group form-check">
                <input type="checkbox" class="form-check-input" id="scoped" />
                <label class="form-check-label" for="scoped">Scoped</label>
            </div>

            <div class="form-group">
                <label for="resourceIds">Scopes</label>
                <select class="form-control" id="Sscopes">
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                    <option>4</option>
                    <option>5</option>
                </select>
            </div>

            

            <button type="submit" class="btn btn-primary">Submit</button> */}
        </Form>)
    }
}

export default ClientPage;