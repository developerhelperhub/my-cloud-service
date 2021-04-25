import React from 'react';

import Form from '../../components/form/Form'
import Input from '../../components/form/Input'
import Checkbox from '../../components/form/Checkbox'
import Select from '../../components/form/Select'

import AppApiRepo from '../../common/AppApiRepo'

class UserPage extends React.Component {

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
                        { title: "Username", width: "30%" },
                        { title: "Authorities", width: "40%" },
                        { title: "Account Non Expired", width: "20%" },
                        { title: "Account Non Locked", width: "20%" },
                        { title: "Credentials Non Expired", width: "20%" },
                        { title: "Enabled", width: "20%" },
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

        const response = await AppApiRepo.fetch('/identity/user/', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        });

        const formData = this.state.formData;
        const body = [];

        if (response.status == 200) {

            response.data.forEach(data => {
                var colums = [];

                colums.push(data.username);
                colums.push(data.authorities);
                colums.push(this.convertBoolString(data.accountNonExpired));
                colums.push(this.convertBoolString(data.accountNonLocked));
                colums.push(this.convertBoolString(data.credentialsNonExpired));
                colums.push(this.convertBoolString(data.enabled));
                colums.push(data.username);

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

    convertBoolString(value) {
        return value ? 'True' : 'False';
    }

    render() {

        return (
            <Form title="User" data={this.state.formData} >

                <div class="row">
                    <div class="col">
                        <Input type="text" id="username" label="Username" />
                    </div>
                    <div class="col">
                        <Input type="password" id="password" label="Password" />
                    </div>
                </div>

                <div class="row">
                    <div class="col">
                        <Checkbox id="accountNonExpired" label="Account Non Expired" />
                    </div>
                    <div class="col">
                        <Checkbox id="accountNonLocked" label="Account Non Locked" />
                    </div>
                </div>

                <div class="row">
                    <div class="col">
                        <Checkbox id="credentialsNonExpired" label="Credentials Non Expired" />
                    </div>
                    <div class="col">
                        <Checkbox id="enabled" label="Enabled" />
                    </div>
                </div>

                <div class="row">
                    <div class="col">
                        <Select id="grantedAuthorities" label="Granted Authorities" values={["USER", "ADMIN"]} />
                    </div>
                    <div class="col">
                        
                    </div>
                </div>

            </Form>
        )

    }
}

export default UserPage;