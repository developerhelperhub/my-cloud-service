import React from 'react';

import Table from './Table'
import TableHead from './TableHead'
import TableBody from './TableBody'
import TableTr from './TableTr'
import TableTd from './TableTd'
import TableTh from './TableTh'



class DataTable extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        if (this.props.id != null) {
            window.refreshDatatable("#" + this.props.id);
        }
    }

    render() {

        const tableHeads = [];

        this.props.data.head.forEach(head => {

            tableHeads.push(<TableTh width={head.width}> {head.title} </TableTh>);

        });

        const tableRaws = [];

        this.props.data.body.forEach(row => {

            const tableCols = [];

            row.forEach(col => {
                tableCols.push(<TableTd> {col.value} </TableTd>);
            });

            tableRaws.push(<TableTr>

                {tableCols}

            </TableTr>);

        });

        return (
            <Table {...this.props}>
                <TableHead>
                    <TableTr>
                        {tableHeads}
                    </TableTr>
                </TableHead>
                <TableBody>
                    {tableRaws}
                </TableBody>
            </Table>
        );
    }
}

export default DataTable;