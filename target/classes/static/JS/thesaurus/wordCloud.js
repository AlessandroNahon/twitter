function recollectData(words,title){
var data = [];
for(var i = 0; i < words.length; i++){
    data.push({"x": words[i].word, "value":words[i].count , category:words[i].syntax});
}
executeChart(data, title);
}

function executeChart(data,title){
    var chart = anychart.tagCloud(data);
    chart.title(title)
    chart.angles([0])
    chart.colorRange(true);
    chart.colorRange().length('100%');
    chart.container("container");
    chart.listen("pointClick", function(e){
      searchWord = e.point.get('x');
      currentPage = 1;
      updateTableTitle();
      lookingBySearchBar = false;
      updateLowerTable(lowerTable, lowerTableUrl);
    });
    chart.draw();
};