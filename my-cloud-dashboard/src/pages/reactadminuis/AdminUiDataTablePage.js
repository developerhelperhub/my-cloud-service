
import React from 'react';

import PagePanel from '../../components/dashboard/PagePanel'
import PageContent from '../../components/dashboard/PageContent'
import Row from '../../components/dashboard/Row'

import DataTable from '../../components/table/DataTable'

class AdminUiDataTablePage extends React.Component {

    render() {

        const projectData1 = {
            head : [
                {title: "Name", width: "30%"},
                {title: "Position", width: "20%"},
                {title: "Office", width: "20%"},
                {title: "Age", width: "10%"},
                {title: "Start date", width: "10%"},
                {title: "Salary", width: "10%"}
            ],

            body: [
                [
                    {value: "Tiger Nixon"},
                    {value: "System Architect"},
                    {value: "Edinburgh"},
                    {value: "61"},
                    {value: "2011/04/25"},
                    {value: "$320,800"}
                ],
                [
                    {value: "Garrett Winters"},
                    {value: "Accountant"},
                    {value: "Tokyo"},
                    {value: "63"},
                    {value: "2011/07/25"},
                    {value: "$170,750"}
                ],
                [
                    {value: "Ashton Cox"},
                    {value: "Junior Technical Author"},
                    {value: "San Francisco"},
                    {value: "66"},
                    {value: "2009/01/12"},
                    {value: "$86,000"}
                ],
                [
                    {value: "Cedric Kelly"},
                    {value: "Senior Javascript Developer"},
                    {value: "Edinburgh"},
                    {value: "22"},
                    {value: "2012/03/29"},
                    {value: "$433,060"}
                ],
                [
                    {value: "Airi Satou"},
                    {value: "Accountant"},
                    {value: "Tokyo"},
                    {value: "33"},
                    {value: "2008/11/28"},
                    {value: "$162,700"}
                ],
                [
                    {value: "Brielle Williamson"},
                    {value: "Integration Specialist"},
                    {value: "New York"},
                    {value: "61"},
                    {value: "2012/12/02"},
                    {value: "$372,000"}
                ],
                [
                    {value: "Herrod Chandler"},
                    {value: "Sales Assistant"},
                    {value: "San Francisco"},
                    {value: "59"},
                    {value: "2012/08/06"},
                    {value: "$137,500"}
                ],
                [
                    {value: "Rhona Davidson"},
                    {value: "Integration Specialist"},
                    {value: "Tokyo"},
                    {value: "55"},
                    {value: "2010/10/14"},
                    {value: "$327,900"}
                ],
                [
                    {value: "Colleen Hurst"},
                    {value: "Javascript Developer"},
                    {value: "San Francisco"},
                    {value: "39"},
                    {value: "2009/09/15"},
                    {value: "$205,500"}
                ],
                [
                    {value: "Sonya Frost"},
                    {value: "Software Engineer"},
                    {value: "Edinburgh"},
                    {value: "23"},
                    {value: "2008/12/13"},
                    {value: "$103,600"}
                ],
                [
                    {value: "Jena Gaines"},
                    {value: "Office Manager"},
                    {value: "London"},
                    {value: "30"},
                    {value: "2008/12/19"},
                    {value: "$90,560"}
                ]
            ]

        };

        return (
            <PageContent>
                <Row>
                    <PagePanel title="Projects - 1" cols="col-xxl-7 col-lg-12">
                        <DataTable id="example1"  width="100%" data={projectData1}></DataTable>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default AdminUiDataTablePage;