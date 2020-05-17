
function refreshDatatable(id) {

    $(id).DataTable();

};

function d3LineChart(value) {

    // reset svg
    d3.select("svg").remove();

    // set the dimensions and margins of the graph
    var width = $(value.id).width() - value.margin.left - value.margin.right;
    var height = $(value.id).height() - value.margin.top - value.margin.bottom;

    // append the svg object to the body of the page
    var svg = d3.select(value.id)
        .append("svg")
        .attr("width", width + value.margin.left + value.margin.right)
        .attr("height", height + value.margin.top + value.margin.bottom)

        //.attr("preserveAspectRatio", "xMinYMin meet")
        //.attr("viewBox", "0 0 1000 400")

        .append("g")
        .attr("transform",
            "translate(" + value.margin.left + "," + value.margin.top + ")");

    // Add X axis --> it is a date format
    var x = d3.scaleTime()
        .domain(d3.extent(value.data, value.extent))
        .range([0, width]);
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    // Add Y axis
    var y = d3.scaleLinear()
        .domain([0, d3.max(value.data, value.max)])
        .range([height, 0]);
    svg.append("g")
        .call(d3.axisLeft(y));

    // Add the line
    svg.append("path")
        .datum(value.data)
        .attr("fill", value.fill)
        .attr("stroke", value.stroke)
        .attr("stroke-width", value.strokeWidth)
        .attr("d", d3.line()
            .x(function (d) { return x(value.x(d)) })
            .y(function (d) { return y(value.y(d)) })
        )
}


function d3LinesChart(value) {

    // reset svg
    d3.select("svg").remove();

    // set the dimensions and margins of the graph
    var width = $(value.id).width() - value.margin.left - value.margin.right;
    var height = $(value.id).height() - value.margin.top - value.margin.bottom;

    // append the svg object to the body of the page
    var svg = d3.select(value.id)
        .append("svg")
        .attr("width", width + value.margin.left + value.margin.right)
        .attr("height", height + value.margin.top + value.margin.bottom)

        //.attr("preserveAspectRatio", "xMinYMin meet")
        //.attr("viewBox", "0 0 1000 400")

        .append("g")
        .attr("transform",
            "translate(" + value.margin.left + "," + value.margin.top + ")");

    // Add X axis --> it is a date format

    value.items.forEach(item => {

        var x = d3.scaleTime()
            .domain(d3.extent(value.values, value.extent))
            .range([0, width]);
        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));

        // Add Y axis
        var y = d3.scaleLinear()
            .domain([0, d3.max(value.values, value.max)])
            .range([height, 0]);
        svg.append("g")
            .call(d3.axisLeft(y));

        // Add the line
        svg.append("path")
            .datum(item.data)
            .attr("fill", item.fill)
            .attr("stroke", item.stroke)
            .attr("stroke-width", item.strokeWidth)
            .attr("d", d3.line()
                .x(function (d) { return x(item.x(d)) })
                .y(function (d) { return y(item.y(d)) })
            )
    });


}