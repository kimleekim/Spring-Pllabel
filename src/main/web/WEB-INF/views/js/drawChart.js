function drawChart(chartID, chartType, chartLabel, chartValue, chartColor, additionalOptions) {
    var ctx = document.getElementById(chartID);

    var myChart = new Chart(ctx, {
        type: chartType,
        data: {
            labels: chartLabel,
            datasets: [{
                data: chartValue,
                backgroundColor: chartColor[1],
                borderColor: chartColor[0],
                borderWidth: 2,
                fill: true
            }]
        },
        options: {
            legend: {
                display: false
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    },
                    gridLines: {
                        labelWrap: additionalOptions,

                        display: additionalOptions
                    }
                }],
                xAxes: [{
                    gridLines: {
                        labelWrap: additionalOptions,
                        display: additionalOptions
                    }
                }]
            },
            elements: {
                line: {
                    tension: 0
                }
            }
        }
    });

    return myChart;
}