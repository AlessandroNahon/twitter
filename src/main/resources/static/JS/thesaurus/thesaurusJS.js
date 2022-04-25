function printHorizontal(name){
    try{
        $(document).ready(function() {
                var ctx = document.getElementById(name).getContext('2d');
                var myLineChart = new Chart(ctx, {
                    type: 'horizontalBar',
                    data: {
                        labels: [1500, 1600, 1700, 1750, 1800, 1850, 1900, 1950, 1999, 2050],
                        datasets: [{
                            data: [86, 114, 106, 106, 107, 111, 133, 221, 783, 2478],
                            label: "Africa",
                            borderColor: "#458af7",
                            backgroundColor: '#458af7',
                            fill: false
                        }, {
                            data: [168, 170, 178, 190, 203, 276, 408, 547, 675, 734],
                            label: "Europe",
                            borderColor: "#3cba9f",
                            fill: false,
                            backgroundColor: '#3cba9f'
                        }]
                    },
                    options: {
                        title: {
                            display: true,
                            text: 'World population per region (in millions)'
                        }
                    }
                });
            });
    } catch(e){

    }
}

