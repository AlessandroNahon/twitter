function printHorizontal(fullDataSet, name, title, color){
    try{
        var labels = []
        var data = []
        fullDataSet.forEach(function(word){
            labels.push(word.word)
            data.push(word.count)
        })
        $(document).ready(function() {
                var ctx = document.getElementById(name).getContext('2d');
                var myLineChart = new Chart(ctx, {
                    type: 'horizontalBar',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
                            label: "Appearances",
                            borderColor: color,
                            backgroundColor: color,
                            fill: false
                        }]
                    },
                    options: {
                        title: {
                            display: true,
                            text: 'Appearances of ' + title + " words in your last search"
                        }
                    }
                });
            });
    } catch(e){
        console.log(e)
    }
}

