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


// class ClientPage extends React.Component {

//     constructor(props) {
//         super(props);

//         this.refreshTable = this.refreshTable.bind(this);
//         this.actionAdd = this.showAddScreen.bind(this);
//         this.showEditScreen = this.showEditScreen.bind(this);
//         this.showDeleteScreen = this.showDeleteScreen.bind(this);

//         window.clientPage = this;

//         this.state = {
//             addScreen: false,
//             tableData: {
//                 columnDefs: [
//                     {
//                         render: function (data, type, row) {
//                             return '<div class="panel-icon">'
//                                     + '<span class="panel-icon-font fa fa-edit" onclick="formDataTableEdit(event)" ></span>'
//                                 +'</div>';
//                         },
//                         targets: 6
//                     }
//                 ],
//                 head: [
//                     { title: "Id", width: "20%" },
//                     { title: "Grand Types", width: "20%" },
//                     { title: "Scops", width: "20%" },
//                     { title: "Redirect URI", width: "40%" },
//                     { title: "Access Token", width: "20%" },
//                     { title: "Refresh Token", width: "20%" },
//                     { title: "Actions", width: "20%" },
//                 ],
//                 body: []
//             }
//         }


//     }

//     showDeleteScreen() {
//         console.log("delete");
//     }

//     showEditScreen() {
//         console.log("edit");
//     }

//     showAddScreen() {

//         this.setState({
//             addScreen: this.state.addScreen ? false : true
//         });
//     }

//     async refreshTable() {

//         const response = await AppApiRepo.fetch('/identity/clients/', 'GET', {
//             'Content-Type': 'application/json',
//             'Authorization': AppApiRepo.getToken(),
//         })

//         const tableData = this.state.tableData;
//         const body = [];

//         if (response.status == 200) {

//             response.data.forEach(data => {
//                 var colums = [];

//                 colums.push(data.clientId);
//                 colums.push(data.authorizedGrantTypes);
//                 colums.push(data.scope);
//                 colums.push(data.registeredRedirectUri);
//                 colums.push(data.accessTokenValiditySeconds);
//                 colums.push(data.refreshTokenValiditySeconds);
//                 colums.push(data.clientId);

//                 body.push(colums);
//             });

//             tableData.body = body;
//         }

//         this.setState({
//             tableData: tableData
//         })

//     }

//     async componentDidMount() {
//         this.refreshTable();
//     }

//     componentDidCatch() {
//         this.forceUpdate();
//     }

//     render() {

//         let screen = this.state.addScreen ? <div>hello</div> : <DataTable id="example1" width="100%" data={this.state.tableData} />;

//         return (
//             <PageContent>
//                 <Row>
//                     <PagePanel cols="col-xxl-7 col-lg-12" >
//                         <PagePanelHead title="Client">
//                             <PagePanelIcon icon="fa fa-plus" event={this.showAddScreen}></PagePanelIcon>
//                             <PagePanelIcon icon="fa fa-refresh" event={this.refreshTable}></PagePanelIcon>
//                         </PagePanelHead>
//                         <PagePanelBody>
//                             <div>{screen}</div>
//                         </PagePanelBody>
//                     </PagePanel>
//                 </Row>
//             </PageContent>
//         );

//     }
// }

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
            <div class="form-group">
                <label for="exampleInputEmail1">Email address</label>
                <input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email" />
                <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
            </div>
            <div class="form-group">
                <label for="exampleInputPassword1">Password</label>
                <input type="password" class="form-control" id="exampleInputPassword1" placeholder="Password" />
            </div>
            <div class="form-group form-check">
                <input type="checkbox" class="form-check-input" id="exampleCheck1" />
                <label class="form-check-label" for="exampleCheck1">Check me out</label>
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
        </Form>)
    }
}

export default ClientPage;