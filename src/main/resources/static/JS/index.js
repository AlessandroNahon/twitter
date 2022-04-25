function printSentiment(data){
                  var ctx = document.getElementById('myChart').getContext('2d');
                  var myChart = new Chart(ctx, {
                      type: 'pie',
                      data: {
                          labels: ['Very positive', 'Positive', 'Neutral', 'Negative', 'Very negative'],
                          datasets: [{
                              label: '# of Votes',
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

function printThesaurus(words,values){
                var ctx = document.getElementById('bar').getContext('2d');
                var myChart = new Chart(ctx, {
                      type: 'bar',
                      data: {
                          labels: words,
                          datasets: [{
                              label: '   Most used words',
                              data: values,
                              backgroundColor: [
                                  'rgba(255, 99, 132, 1)',
                                  'rgba(54, 162, 235, 1)',
                                  'rgba(255, 206, 86, 1)',
                                  'rgba(75, 192, 192, 1)',
                                  'rgba(153, 102, 255, 1)',
                                  'rgba(255, 159, 64, 1)'
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

function scrollDown(paginationNumber){
    if(paginationNumber != 1 ){
        $(document).ready(function() {
                $(document).scrollTop($(document).height());
        });
    }
}

function prueba(words,values){
                var ctx = document.getElementById('bar').getContext('2d');
                var myChart = new Chart(ctx, {
                      type: 'bar',
                      data: {
                          labels: [86, 114, 106, 106, 107, 111, 133, 221, 783, 2478,86, 114, 106, 106, 107, 111, 133, 221, 783, 2478],
                          datasets: [{
                              label: '   Most used words',
                              data: [86, 114, 106, 106, 107, 111, 133, 221, 783, 2478,86, 114, 106, 106, 107, 111, 133, 221, 783, 2478],
                              backgroundColor: [
                                  'rgba(255, 99, 132, 1)',
                                  'rgba(54, 162, 235, 1)',
                                  'rgba(255, 206, 86, 1)',
                                  'rgba(75, 192, 192, 1)',
                                  'rgba(153, 102, 255, 1)',
                                  'rgba(255, 159, 64, 1)'
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
