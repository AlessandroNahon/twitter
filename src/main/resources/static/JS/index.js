function printSentiment(fullDataSet, name){
                var labels = []
                var data = []
                fullDataSet.forEach(function(sentiment){
                    labels.push(toTitleCase(sentiment.sentiment))
                    data.push(sentiment.appearances)
                })
                  var ctx = document.getElementById(name).getContext('2d');
                  var myChart = new Chart(ctx, {
                      type: 'pie',
                      data: {
                          labels: labels,
                          datasets: [{
                              label: 'Sentiment appearance',
                              data: data,
                              backgroundColor: [
                                  'rgba(255, 99, 132, 1)',
                                  'rgba(54, 162, 235, 1)',
                                  'rgba(255, 206, 86, 1)',
                                  'rgba(75, 192, 192, 1)',
                                  'rgba(153, 102, 255, 1)'
                              ],
                              borderColor: [
                                  'rgba(255, 99, 132, 1)',
                                  'rgba(54, 162, 235, 1)',
                                  'rgba(255, 206, 86, 1)',
                                  'rgba(75, 192, 192, 1)',
                                  'rgba(153, 102, 255, 1)'
                              ],
                              borderWidth: 1
                          }]
                      },
                      options: {
                          scales: {
                              y: {
                                  beginAtZero: true
                              }
                          }
                      }
                  });
              }

function toTitleCase(str) {
    return str.replace(
      /\w\S*/g,
      function(txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      }
    ).replace('_', ' ');
  }

function printThesaurus(fullDataSet){
                var labels = []
                var data = []
                var colors = []
                fullDataSet.forEach(function(word){
                    labels.push(word.word)
                    data.push(word.count)
                    colors.push('rgba(54, 162, 235, 1)')
                })
                var ctx = document.getElementById('bar').getContext('2d');
                var myChart = new Chart(ctx, {
                      type: 'bar',
                      data: {
                          labels: labels,
                          datasets: [{
                              label: '   Appearances',
                              data: data,
                              backgroundColor: colors,
                              borderColor: colors,
                              borderWidth: 1
                          }]
                      },
                      options: {
                          scales: {
                               yAxes: [{
                                          ticks: {
                                              beginAtZero: true
                                          }
                                      }]
                          }
                      }
                  });
              }


function scrollDown(paginationNumber){
    if(paginationNumber != 1 ){
        $(document).ready(function() {
                $(document).scrollTop($(document).height());
        });
    }
}

function printTweetTable(pageNumber, maxPageNumber){
    console.log('Actual page: '+pageNumber+', taget page: '+maxPageNumber)

    console.log("Page " + pageNumber + " of " + maxPageNumber)
        $("#previous").show()
        $("#next").show()
        $.ajax({
                type: 'get',
                url: '/indexFragments/tweet_table',
                data : {
                    page: parseInt(pageNumber)-1,
                    totalPages: maxPageNumber
                },
                success: function(data){
                  /*<![CDATA[*/
                  $('#tweet_table').html(data);
                  /*]]>*/
                },
            })
    document.getElementById('currentPageText').innerHTML = "Page " + pageNumber + " of " + maxPageNumber
    if(pageNumber == maxPageNumber){
            $("#next").hide()
    }else if(pageNumber == 1){
            $("#previous").hide()
        }
    }


function loadComponents(){
    var buttonNext = document.getElementById('next')
    var buttonPrevious = document.getElementById('previous')


    buttonNext.addEventListener("click", function() {
        var actualPage = document.getElementById('actualPage').value
        var maxPageNumber = document.getElementById('maxPageNumber').value
        var nextPage = parseInt(actualPage) + 1

        printTweetTable(nextPage, maxPageNumber, '#tweetTable')
        document.getElementById('actualPage').value = nextPage
    });

    buttonPrevious.addEventListener("click", function() {
        var actualPage = document.getElementById('actualPage').value
        var maxPageNumber = document.getElementById('maxPageNumber').value
        var previousPage = parseInt(actualPage) - 1

        printTweetTable(previousPage, maxPageNumber, '#tweetTable')
        document.getElementById('actualPage').value = previousPage
    });
}


