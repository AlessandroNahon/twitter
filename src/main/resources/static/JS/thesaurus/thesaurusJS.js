function loadNewTable(tableFragmentName, titleTableFragmentName, word) {
    if (word === "") {
        alert("Please enter a search term")
    } else {
        printTable(tableFragmentName, word)
        printTitle(titleTableFragmentName, word)
    }


}



function printTable(fragmentName, word) {
    $.ajax({
        type: 'get',
        url: '/thesaurus/fragment/words_table_tweets',
        data: {
            word: word
        },
        success: function (data) {
            console.log(data)
            /*<![CDATA[*/
            $(fragmentName).html(data);
            /*]]>*/
        },
    })
}

function printTitle(fragmentName, word) {
    $.ajax({
        type: 'get',
        url: '/thesaurus/fragment/table_title',
        data: {
            word: word
        },
        success: function (data) {
        console.log(data)
            /*<![CDATA[*/
            $(fragmentName).html(data);
            /*]]>*/
        },
    })
}

function addEventsToNavButtons(){
    var button = document.getElementById("tweets_nav_button")
    button.addEventListener("click", probar)
}

function changeDataContent(message){
    var navActive = 'bg-transparent nav-link active'
    var navInactive = 'bg-transparent nav-link'
    if(message === 'Replies'){
        document.getElementById("button2").className=navActive
        document.getElementById("button1").className=navInactive
        getNewTableContent('Reply')
    }else{
        document.getElementById("button1").className=navActive
        document.getElementById("button2").className=navInactive
        getNewTableContent('Tweet')
    }
}

function getNewTableContent(wordType){
    var fragment = '#whole_display_info'
    $.ajax({
            type: 'get',
            url: '/thesaurus/fragment/whole_display_info',
            data: {
                type: wordType
            },
            success: function (data) {
            console.log(data)
                /*<![CDATA[*/
                $(fragment).html(data);
                /*]]>*/
            },
        })
    }