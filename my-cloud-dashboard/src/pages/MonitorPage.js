
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

        AppApiRepo.eventSource("/monitor/jvm-memory-used/stream",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const data=[];
                const eventData = JSON.parse(event.data);
                eventData.forEach(d => {
                    const val = {
                        date: window.d3.timeParse("%Y-%m-%d %H:%M:%S")(d.time),
                        value: (d.value / 1024/ 1024)
                    };
                    data.push(val);

                    //console.log(d.time + ": " + val.date);
                });

                slef.populate(data);

            }, function (err) {
                slef.populate([]);

                console.log("Monitor Event Source Error : " + err.error);
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
            data: data,
            extent: function (d) { return d.date; },
            max: function (d) { return d.value; },
            x: function (d) { return d.date },
            y: function (d) { return d.value }
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