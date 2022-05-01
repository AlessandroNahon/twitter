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
                     scales: {
                            yAxes: [{
                                ticks: {
                                    beginAtZero: true
                                }
                            }],
                            xAxes: [{
                                    ticks: {
                                        beginAtZero: true
                                    }
                                }]
                        },
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

function loadNewTable(tableFragmentName, titleTableFragmentName, word){
    if(word === ""){
        alert("Please enter a search term")
    }else{
        printTable(tableFragmentName,word)
        printTitle(titleTableFragmentName,word)
    }


}


function printTable(fragmentName,word){
     $.ajax({
        type: 'get',
        url: '/thesaurus/fragment/words_table_tweets',
        data : {
            word: word
        },
        success: function(data){
        console.log(data)
          /*<![CDATA[*/
          $(fragmentName).html(data);
          /*]]>*/
        },
    })
}

function printTitle(fragmentName, word){
     $.ajax({
        type: 'get',
        url: '/thesaurus/fragment/table_title',
        data : {
            word: word
        },
        success: function(data){
          /*<![CDATA[*/
          $(fragmentName).html(data);
          /*]]>*/
        },
    })
}
