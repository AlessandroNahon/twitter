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
            /*<![CDATA[*/
            $(fragmentName).html(data);
            /*]]>*/
        },
    })
}
