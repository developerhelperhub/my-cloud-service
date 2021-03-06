import React from 'react';

import PageContent from '../dashboard/PageContent'
import Row from '../dashboard/Row'


import PagePanel from '../dashboard/panel/PagePanel'
import PagePanelHead from '../dashboard/panel/PagePanelHead'
import PagePanelBody from '../dashboard/panel/PagePanelBody'
import PagePanelIcon from '../dashboard/panel/PagePanelIcon'

import DataTable from '../table/DataTable'

import './Form.css'

class Form extends React.Component {

    constructor(props) {
        super(props);

        this.actionRefresh = this.actionRefresh.bind(this);
        this.actionTable = this.actionTable.bind(this);
        this.actionAdd = this.actionAdd.bind(this);
        this.actionEdit = this.actionEdit.bind(this);
        this.actionDelete = this.actionDelete.bind(this);
        this.actionSubmit = this.actionSubmit.bind(this);
        this.actionClear = this.actionClear.bind(this);

        this.props.data.tableData.head.push(
            { title: "Actions", width: "20%" }
        );

        window.formScreen = this;

        this.state = {
            showAddScreen: false,
            tableData: {
                columnDefs: [
                    {
                        render: function (data, type, row) {
                            return '<div class="panel-icon">'
                                + '<span class="panel-icon-font fa fa-edit" onclick="formDataTableActionEdit(event, \'' + data + '\')" ></span>'
                                + '</div>'
                                + '<div class="panel-icon">'
                                + '<span class="panel-icon-font fa fa-remove" onclick="formDataTableActionDelete(event, \'' + data + '\')" ></span>'
                                + '</div>';
                        },
                        targets: 6
                    }
                ],
                head: this.props.data.tableData.head,
                body: this.props.data.tableData.body
            }
        }


    }

    async actionSubmit(e) {
        if (this.props.data != undefined && this.props.data.actions != undefined) {

            this.props.data.actions.submit(e);
        }
    }


    async actionClear(e) {
        if (this.props.data != undefined && this.props.data.actions != undefined) {

            this.props.data.actions.clear(e);
        }
    }

    async actionRefresh(e) {

        if (!this.state.showAddScreen && this.props.data != undefined && this.props.data.actions != undefined) {

            this.props.data.actions.refresh(e);
        }

    }

    actionAdd(e) {

        this.setState({
            showAddScreen: true
        });

        if (this.props.data != undefined && this.props.data.actions != undefined) {
            this.props.data.actions.add(e);
        }


    }

    actionEdit(e, id) {

        if (this.props.data != undefined && this.props.data.actions != undefined) {
            this.props.data.actions.edit(e, id);
        }

    }

    actionDelete(e, id) {

        if (this.props.data != undefined && this.props.data.actions != undefined) {
            this.props.data.actions.delete(e, id);
        }

    }

    actionTable(e) {
        this.setState({
            showAddScreen: false
        });
    }

    componentDidCatch() {
        this.forceUpdate();
    }

    render() {

        var screen = "";

        if (!this.state.showAddScreen) {

            this.state.tableData.body = this.props.data.tableData.body;

            screen = <DataTable id="example1" width="100%" data={this.state.tableData} />;

        } else {

            screen = React.Children.map(this.props.children, children =>
                React.cloneElement(children)
            );

        }



        var detailHeight = "100%";

        return (

            <PageContent>
                <Row>
                    <PagePanel cols="col-xxl-12 col-lg-12" height={this.props.height}>
                        <PagePanelHead title={this.props.title}>
                            <PagePanelIcon icon="fa fa-table" event={this.actionTable}></PagePanelIcon>
                            <PagePanelIcon icon="fa fa-plus" event={this.actionAdd}></PagePanelIcon>
                            <PagePanelIcon icon="fa fa-refresh" event={this.actionRefresh}></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody>

                            {screen}

                            {(this.state.showAddScreen == true) ? (<div class="row">
                                <div class="col d-flex flex-row-reverse bd-highlight">

                                    <span class="p-2 bd-highlight">
                                        <button type="button" class="btn btn-primary" onClick={e=> this.actionClear(e)}>Clear</button>
                                    </span>

                                    <span class="p-2 bd-highlight" style={{ marginRight: "5px" }}>
                                        <button type="button" class="btn btn-primary" onClick={e=> this.actionSubmit(e)}>Submit</button>
                                    </span>

                                </div>

                            </div>) : (<div></div>)}

                        </PagePanelBody>
                    </PagePanel>
                </Row>
            </PageContent>
        );
    }
}

export default Form;