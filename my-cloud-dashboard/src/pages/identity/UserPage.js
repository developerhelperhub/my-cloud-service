import React from 'react';

import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'

import PagePanel from '../../components/dashboard/panel/PagePanel'
import PagePanelHead from '../../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../../components/dashboard/panel/PagePanelBody'
import PagePanelIcon from '../../components/dashboard/panel/PagePanelIcon'

import DataTable from '../../components/table/DataTable'

import AppApiRepo from '../../common/AppApiRepo'

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
                body: []
            }
        }

        this.refreshTable = this.refreshTable.bind(this);
    }

    convertBoolString(value) {
        return value ? 'True' : 'False';
    }

    async refreshTable() {
        const response = await AppApiRepo.fetch('/identity/user/', 'GET', {
            'Content-Type': 'application/json',
            'Authorization': AppApiRepo.getToken(),
        })

        const tableData = this.state.tableData;
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

                body.push(colums);
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
                        <PagePanelHead title="User">
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

export default UserPage;