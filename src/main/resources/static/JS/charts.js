function printThesaurus(fullDataSet) {
  var labels = []
  var data = []
  var colors = []
  fullDataSet.forEach(function (word) {
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

function printSentiment(fullDataSet, name) {
  var labels = [];
  var data = [];
  fullDataSet.forEach(function (sentiment) {
    var labelVariable = toTitleCase(sentiment.sentiment);
    labels.push(labelVariable.replace("_"," "));
    data.push(sentiment.appearances);
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
          "rgba(255,105,97,1)",
            'rgba(236,236,236,1)',
          "rgba(119,221,119,1)"
        ],
        borderColor: [
          "rgba(255,105,97,1)",
            'rgba(236,236,236,1)',
        "rgba(119,221,119,1)"
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

function printHorizontal(fullDataSet, name, title, color) {
  try {
    var labels = [];
    var data = [];
    fullDataSet.forEach(function (word) {
      labels.push(word.word);
      data.push(word.count);
    });
    $(document).ready(function () {
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
  } catch (e) {
    console.log(e);
  }
}

function toTitleCase(str) {
  return str.replace(
    /\w\S*/g,
    function(txt) {
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    }
  );
}

function printSentimentAnalysis(chartName, value, totalCount, type){
     var color = '';
     if(type === "Positive"){
        color = "rgba(119,221,119,1)";
     }else if(type === "Negative"){
        color = "rgba(255,105,97,1)";
     }else{
        color = "rgba(108,122,137,1)";
     }
     var ctx = document.getElementById(chartName).getContext('2d');
     var myChart = new Chart(ctx, {
       type: 'doughnut',
       data: {
         labels: [type,"Other"],
         datasets: [{
           label: 'Sentiment appearance',
           data: [value,totalCount-value],
           backgroundColor: [
             color,
             'rgba(236,236,236,1)'
           ],
           borderColor: [
             color,
             'rgba(236,236,236,1)'
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

function printTables(chartName, value, totalCount, type){
     var ctx = document.getElementById(chartName).getContext('2d');
     var myChart = new Chart(ctx, {
       type: 'doughnut',
       data: {
         labels: [type,"Other"],
         datasets: [{
           label: 'Sentiment appearance',
           data: [value,totalCount-value],
           backgroundColor: [
             colorDonuts,
             'rgba(236,236,236,1)'
           ],
           borderColor: [
             colorDonuts,
             'rgba(236,236,236,1)'
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



