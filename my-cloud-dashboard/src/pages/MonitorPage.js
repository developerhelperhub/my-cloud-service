
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

        this.formatDate = this.formatDate.bind(this);
        this.populateAll = this.populateAll.bind(this);
        this.sampleGroupedData = this.sampleGroupedData.bind(this);
        this.populateGrouped = this.populateGrouped.bind(this);
        this.jvmMemoryUsedStreamAll = this.jvmMemoryUsedStreamAll.bind(this);
        this.jvmMemoryUsedStreamGrouped = this.jvmMemoryUsedStreamGrouped.bind(this);
        this.colours = this.colours.bind(this);

    }

    componentDidMount() {
        // this.jvmMemoryUsedStreamAll();
        this.jvmMemoryUsedStreamGrouped();
    }

    formatDate(date) {
        return window.d3.timeParse("%Y-%m-%d %H:%M:%S")(date);
    }

    jvmMemoryUsedStreamAll() {
        let self = this;

        AppApiRepo.eventSource("/monitor/jvm-memory-used/stream-all",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const data = [];
                const eventData = JSON.parse(event.data);
                eventData.forEach(d => {
                    const val = {
                        date: self.formatDate(d.time),
                        value: (d.value / 1024 / 1024)
                    };
                    data.push(val);

                    //console.log(d.time + ": " + val.date);
                });

                self.populateAll(data);

            }, function (err) {
                self.populateAll([]);

                console.log("Monitor Event Source Error : " + err.error);
            }
        );
    }

    jvmMemoryUsedStreamGrouped() {

        let self = this;

        let values = [];
        let items = [];
        var legends = [];

        let data = {
            items: items,
            values: values,
            legends: legends
        }

        let colours = self.colours();

        // data = this.sampleGroupedData();
        // self.populateGrouped(data);

        AppApiRepo.eventSource("/monitor/jvm-memory-used/stream-grouped",
            {
                'Authorization': AppApiRepo.getToken()
            }, function (event) {

                const eventData = JSON.parse(event.data);

                var colourIndex = 0;

                legends = [];

                eventData.forEach(app => {

                    legends.push({ value: app.application, fill: colours[colourIndex], stroke: colours[colourIndex], strokeWidth: 1.5 });

                    let itemData = []

                    app.data.forEach(d => {

                        let dataObj = {
                            date: self.formatDate(d.time),
                            value: (d.value / 1024 / 1024)
                        };

                        itemData.push(dataObj);
                        data.values.push(dataObj);

                    });

                    data.items.push({
                        fill: 'none',
                        stroke: colours[colourIndex],
                        strokeWidth: 1.5,
                        data: itemData,
                        x: function (d) { return d.date },
                        y: function (d) { return d.value }
                    })

                    colourIndex = colourIndex + 1;
                });

                data = {
                    items: items,
                    values: values,
                    legends: legends
                }

                self.populateGrouped(data);

            }, function (err) {
                self.populateGrouped(data);

                console.log("Monitor Event Source Error : " + err.error);
            }
        );
    }



    populateGrouped(data) {

        window.d3LinesChart({
            id: "#my_dataviz",
            margin: { top: 10, right: 30, bottom: 30, left: 60 },
            items: data.items,
            axis: {
                stroke: "#818896",
                fill: "none",
                text: "#818896",
                font: "10px Arial"
            },
            legend: {
                rect: {
                    width: 10,
                    height: 10,
                    x: function (width, height) {
                        return width - 180;
                    },
                    y: function (width, height) {
                        return 0;
                    }
                },
                text: {
                    x: function (width, height) {
                        return width - 160;
                    },
                    y: function (width, height) {
                        return 10;
                    },
                    value: function (d) {
                        return d.value;
                    },
                    font: "10px arial",
                    fill: "#818896"
                },
                data: data.legends,
            },
            values: data.values,
            extent: function (d) { return d.date; },
            max: function (d) { return d.value; },
        });

    }

    populateAll(data) {

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

    colours() {
        return ["ORCHID", "GOLD", "ORANGERED", "LIMEGREEN", "HOTPINK", "MEDIUMVIOLETRED", "DARKVIOLET"];
    }

    sampleGroupedData() {

        let self = this;

        var legends = [
            { value: "Application 1", fill: 'steelblue', stroke: "steelblue", strokeWidth: 1.5 },
            { value: "Application 2", fill: 'red', stroke: "steelblue", strokeWidth: 1.5 }
        ]

        var values = [
            { date: self.formatDate("2020-05-17 09:10:20"), value: 200 },
            { date: self.formatDate("2020-05-17 09:12:20"), value: 100 },
            { date: self.formatDate("2020-05-17 09:40:20"), value: 300 },
            { date: self.formatDate("2020-05-17 09:55:20"), value: 600 },
            { date: self.formatDate("2020-05-17 10:10:20"), value: 150 },
            { date: self.formatDate("2020-05-17 09:10:20"), value: 50 },
            { date: self.formatDate("2020-05-17 09:12:20"), value: 70 },
            { date: self.formatDate("2020-05-17 09:40:20"), value: 90 },
            { date: self.formatDate("2020-05-17 09:55:20"), value: 400 },
            { date: self.formatDate("2020-05-17 10:10:20"), value: 40 }
        ]

        var items = [
            {
                fill: 'none',
                stroke: "steelblue",
                strokeWidth: 1.5,
                data: [
                    { date: self.formatDate("2020-05-17 09:10:20"), value: 200 },
                    { date: self.formatDate("2020-05-17 09:12:20"), value: 100 },
                    { date: self.formatDate("2020-05-17 09:40:20"), value: 300 },
                    { date: self.formatDate("2020-05-17 09:55:20"), value: 600 },
                    { date: self.formatDate("2020-05-17 10:10:20"), value: 150 },
                ],
                x: function (d) { return d.date },
                y: function (d) { return d.value }
            },
            {
                fill: 'none',
                stroke: "red",
                strokeWidth: 1.5,
                data: [
                    { date: self.formatDate("2020-05-17 09:10:20"), value: 50 },
                    { date: self.formatDate("2020-05-17 09:12:20"), value: 70 },
                    { date: self.formatDate("2020-05-17 09:40:20"), value: 90 },
                    { date: self.formatDate("2020-05-17 09:55:20"), value: 400 },
                    { date: self.formatDate("2020-05-17 10:10:20"), value: 40 },
                ],
                x: function (d) { return d.date },
                y: function (d) { return d.value }
            }
        ]

        return { values: values, items: items, legends: legends };
    }
}

export default MonitorPage;