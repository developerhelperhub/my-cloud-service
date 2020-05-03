$(function () {

    /* Bar dashboard chart */
    Morris.Bar({
        element: 'dashboard-bar-1',
        data: [
            { y: 'Oct 10', a: 75, b: 35 },
            { y: 'Oct 11', a: 64, b: 26 },
            { y: 'Oct 12', a: 78, b: 39 },
            { y: 'Oct 13', a: 82, b: 34 },
            { y: 'Oct 14', a: 86, b: 39 },
            { y: 'Oct 15', a: 94, b: 40 },
            { y: 'Oct 16', a: 96, b: 41 }
        ],
        xkey: 'y',
        ykeys: ['a', 'b'],
        labels: ['New Users', 'Returned'],
        barColors: ['#33414E', '#1caf9a'],
        gridTextSize: '10px',
        hideHover: true,
        resize: true,
        gridLineColor: '#E5E5E5'
    });
    /* END Bar dashboard chart */

    /* Donut dashboard chart */
    Morris.Donut({
        element: 'dashboard-donut-1',
        data: [
            { label: "Returned", value: 2513 },
            { label: "New", value: 764 },
            { label: "Registred", value: 311 }
        ],
        colors: ['#33414E', '#1caf9a', '#FEA223'],
        resize: true
    });
    /* END Donut dashboard chart */

    /* Line dashboard chart */
    Morris.Line({
        element: 'dashboard-line-1',
        data: [
            { y: '2014-10-10', a: 2, b: 4 },
            { y: '2014-10-11', a: 4, b: 6 },
            { y: '2014-10-12', a: 7, b: 10 },
            { y: '2014-10-13', a: 5, b: 7 },
            { y: '2014-10-14', a: 6, b: 9 },
            { y: '2014-10-15', a: 9, b: 12 },
            { y: '2014-10-16', a: 18, b: 20 }
        ],
        xkey: 'y',
        ykeys: ['a', 'b'],
        labels: ['Sales', 'Event'],
        resize: true,
        hideHover: true,
        xLabels: 'day',
        gridTextSize: '10px',
        lineColors: ['#1caf9a', '#33414E'],
        gridLineColor: '#E5E5E5'
    });
    /* EMD Line dashboard chart */

})