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



function generateRandomPastelColors(){
    var hue = Math.floor(Math.random() * 360);
    return 'hsl(' + hue + ', 100%, 80%)'
}
