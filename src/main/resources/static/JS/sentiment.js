
function printOrientationCharts(sentimentImg,sentimentEmo ,fullMatch){

}

function printTables(chartName, value, totalCount, type){
     var color = 'rgba(119,221,119,1);
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
