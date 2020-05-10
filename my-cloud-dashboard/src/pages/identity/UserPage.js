
import React from 'react';
import Moment from 'react-moment';

import Label from '../../components/dashboard/Label'
import PagePanel from '../../components/dashboard/PagePanel'
import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'
import Widget from '../../components/dashboard/Widget'

import AppApiRepo from '../../common/AppApiRepo'

import DataTable from '../../components/table/DataTable'

class UserPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            tableData: {
                head: [
                    { title: "Username", width: "30%" },
                    { title: "Authorities", width: "40%" },
                    { title: "Account Non Expired", width: "20%" },
                    { title: "Account Non Locked", width: "20%" },
                    { title: "Credentials Non Expired", width: "20%" },
                    { title: "Enabled", width: "20%" },

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

    convertBoolString(value){
        return value? 'True': 'False';
    }

    async componentDidMount() {

        const response = await AppApiRepo.fetch('/identity/user/', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const tableData = this.state.tableData;
        const body = [];

        console.log(response);

        if (response.status == 200) {

            

            response.data.forEach(data => {

                body.push([
                    { value: data.username },
                    { value: data.authorities},
                    { value: this.convertBoolString(data.accountNonExpired) },
                    { value: this.convertBoolString(data.accountNonLocked) },
                    { value: this.convertBoolString(data.credentialsNonExpired) },
                    { value: this.convertBoolString(data.enabled) },
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
                    <PagePanel title="Applications" cols="col-xxl-7 col-lg-12" >
                        <DataTable id="example1" width="100%" data={this.state.tableData}></DataTable>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default UserPage;