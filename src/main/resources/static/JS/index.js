
function printTweetTable(pageNumber, maxPageNumber) {
    if(pageNumber == 1 && maxPageNumber == 1){
        $("#previous").hide()
        $("#next").hide()
    }else{
        $("#previous").show()
        $("#next").show()
        $.ajax({
            type: 'get',
            url: '/indexFragments/tweet_table',
            data: {
                page: parseInt(pageNumber) - 1,
                totalPages: maxPageNumber
            },
            success: function (data) {
                /*<![CDATA[*/
                $('#tweet_table').html(data);
                /*]]>*/
            },
        })
        document.getElementById('currentPageText').innerHTML = "Page " + pageNumber + " of " + maxPageNumber
        if (pageNumber == maxPageNumber) {
            $("#next").hide()
        } else if (pageNumber == 1) {
            $("#previous").hide()
        }
    }
}


function loadComponents() {
    var buttonNext = document.getElementById('next')
    var buttonPrevious = document.getElementById('previous')


    buttonNext.addEventListener("click", function () {
        var actualPage = document.getElementById('actualPage').value
        var maxPageNumber = document.getElementById('maxPageNumber').value
        var nextPage = parseInt(actualPage) + 1

        printTweetTable(nextPage, maxPageNumber, '#tweetTable')
        document.getElementById('actualPage').value = nextPage
    });

    buttonPrevious.addEventListener("click", function () {
        var actualPage = document.getElementById('actualPage').value
        var maxPageNumber = document.getElementById('maxPageNumber').value
        var previousPage = parseInt(actualPage) - 1

        printTweetTable(previousPage, maxPageNumber, '#tweetTable')
        document.getElementById('actualPage').value = previousPage
    });
}

function printOrientationCharts(positiveCount, ambiguousCount, negativeCount){
    var totalCount = positiveCount + ambiguousCount + negativeCount
    printSentimentAnalysis('postiveChart',positiveCount,totalCount,'Positive')
    printSentimentAnalysis('ambiguousChart',ambiguousCount,totalCount,'Ambiguous')
    printSentimentAnalysis('negativeChart',negativeCount,totalCount,'Negative')
}

