
import React from 'react';
import Moment from 'react-moment';
import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';


import Label from '../components/dashboard/Label'
import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'
import Widget from '../components/dashboard/Widget'

import PagePanel from '../components/dashboard/panel/PagePanel'
import PagePanelHead from '../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../components/dashboard/panel/PagePanelBody'
import PagePanelIcon from '../components/dashboard/panel/PagePanelIcon'

import AppApiRepo from '../common/AppApiRepo'

import DataTable from '../components/table/DataTable'

class MonitorPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {

        }

        this.refreshTable = this.refreshTable.bind(this);
        this.populate = this.populate.bind(this);
    }

    async refreshTable() {

    }




    async componentDidMount() {

        let slef = this;

        AppApiRepo.eventSource("/monitor/jvm-memory-used",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {
                slef.populate([
                    { date: window.d3.timeParse("%Y-%m-%d")("2013-04-28"), value: 135.98 },
                    { date: window.d3.timeParse("%Y-%m-%d")("2013-04-28"), value: 147.49 },
                    { date: window.d3.timeParse("%Y-%m-%d")("2013-04-29"), value: 146.93 },
                    { date: window.d3.timeParse("%Y-%m-%d")("2013-04-30"), value: 139.89 },
                    { date: window.d3.timeParse("%Y-%m-%d")("2013-05-01"), value: 108.13 }]);

            }, function (err) {
                slef.populate([]);
            }
        );

    }

    async populate(data) {

        window.d3LineChart({
            id: "#my_dataviz",
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            fill: 'none',
            stroke: "steelblue",
            strokeWidth: 1.5,
            data: data
        });

    }

    render() {
        return (
            <PageContent>
                <Row>
                    <PagePanel cols="col-xxl-7 col-lg-12" >
                        <PagePanelHead title="Monitor">
                        </PagePanelHead>
                        <PagePanelBody>
                            <div id="my_dataviz" style={{ height: "300px" }}></div>
                        </PagePanelBody>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default MonitorPage;