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

        this.state = {
            table: null
        }
    }

    componentDidMount() {

        let self = this;

        if (this.props.id != null) {

            const table = window.loadDataTable("#" + this.props.id, {
                data: this.props.data.body,
                columns: this.props.data.head,
                columnDefs: this.props.data.columnDefs
            });

            table
                .on('click', 'tbody tr', function () {
                    if (self.props.onclick != undefined) {
                        self.props.onclick(table.row(this).data());
                    }
                });


            this.setState({
                table: table
            })
        }
    }

    render() {

        if (this.state.table != null) {
            this.state.table.clear();
            this.state.table.rows.add(this.props.data.body);
            this.state.table.draw();
        }

        return (
            <table id={this.props.id} class="table table-bordered table-striped" ></table>
        );
    }
}

export default DataTable;