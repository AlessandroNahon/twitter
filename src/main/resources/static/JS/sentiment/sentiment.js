var colorDonuts = 'rgba(119,221,119,1)'

//Used to identify the search, between image and emojis, images and text, and full search
var searchType = 'Choose one';

//Used to identify wheter we want to get the Sentimental, Grotesque or Gray tweets
var classification = 'Sentimental';

//Used to identify if we want to get the tweets or the replies
var belongsTo = 'Tweet'

var pageNumber = 1;

var maxPageNumber = 1;

var identifies = '';

//This two variables are used to change the classes of each navigation button
var activeClass = 'bg-transparent nav-link active';
var inactiveClass = 'bg-transparent nav-link';



/*
    This function calls the printTableMethod into the Charts.js so it prints the three different charts
    with different colors depending of the Sentimental/Disruptive/Grey classification
*/
function printOrientationCharts(sentimentImg,sentimentEmo ,fullMatch){
    var fullCount = sentimentEmo + sentimentImg + fullMatch;
    printTables('sentimentImageChart',sentimentImg,fullCount, 'Img & Sentiment');
    printTables('sentimentEmojiChart',sentimentEmo,fullCount, 'Emoji & Sentiment');
    printTables('fullSearchChart',fullMatch,fullCount, 'Full matches');
}




/*
    This method initializes all the events of the page
*/
function loadComponents(){

var buttonSentimental = document.getElementById('buttonSentimental');
var buttonGrey = document.getElementById('buttonGrey');
var buttonDisruptive = document.getElementById('buttonDisruptive');
buttonSentimental.addEventListener('click', groupChangesFullFragment);
buttonGrey.addEventListener('click', groupChangesFullFragment);
buttonDisruptive.addEventListener('click', groupChangesFullFragment);


var buttonTweet = document.getElementById('buttonSeeTweets');
var buttonReply = document.getElementById('buttonSeeReplies');
buttonSeeTweets.addEventListener('click', groupChangesTweetReply);
buttonSeeReplies.addEventListener('click', groupChangesTweetReply);


var buttonPrevious = document.getElementById('buttonPrevious');
var buttonNext = document.getElementById('buttonNext');
var pageDescriber = document.getElementById('pageDescriber');
buttonPrevious.addEventListener('click', changePaginationPage);
buttonNext.addEventListener('click', changePaginationPage);
changeButtonsByPageNumber();
}

/*
 There is a distinction between the buttons of the sentimentImage etc, because in the gray table, these buttons
 does not exists, so in order to avoid the program fail, we call this method in the index and full fragment but
 not on the table (The fragment that is going to be shown in this case)
*/
function addEventToInnerCars(){
    var buttonSentimentImage = document.getElementById('SentimentImage');
    var buttonSentimentEmoji = document.getElementById('SentimentEmoji');
    var buttonFullSearch = document.getElementById('fullSearch');
    buttonSentimentImage.addEventListener('click', groupChangesListOfTweets);
    buttonSentimentEmoji.addEventListener('click', groupChangesListOfTweets);
    buttonFullSearch.addEventListener('click', groupChangesListOfTweets);

}



/* ======================================================
    METHODS TO ACTUATE OVER THE TOP NAVIGATION BAR
====================================================== */

/*
    This function groups the two method that changes both de top navigation
    and the full fragment itself, in order to show the correct information
*/
function groupChangesFullFragment(){
    changeTopNavigation(this.id);
    changeWholeFragment();
}

/*
This function allows to change the top active navigation button, and the color of the table that is going
to be shown
*/
function changeTopNavigation(id){
    if(id === 'buttonSentimental'){
        buttonSentimental.className = activeClass;
        buttonGrey.className = inactiveClass;
        buttonDisruptive.className = inactiveClass;
        colorDonuts = 'rgba(119,221,119,1)'
        classification = 'Sentimental';
        searchType = 'Img & Sentiment';
    }else if(id === 'buttonGrey'){
        buttonSentimental.className = inactiveClass;
        buttonGrey.className = activeClass;
        buttonDisruptive.className = inactiveClass;
        classification = 'Grey';
        searchType = 'Grey';
    }else{
        buttonSentimental.className = inactiveClass;
        buttonGrey.className = inactiveClass;
        buttonDisruptive.className = activeClass;
        colorDonuts = 'rgba(255, 105, 97,1)';
        classification = 'Disruptive';
        searchType = 'Img & Sentiment';
    }
}

/*
    This function is used to print all the charts that shows the sentiment analysis of the tweets with
    the three levels of analysis: Sentimental, Grey (Text, emojis or Images do not match) and Disruptive
*/
function changeWholeFragment(){
    var fragment = '#layers_card'

    $.ajax({
            type: 'get',
            url: '/sentiment/fragments/full_fragment',
            data: {
                type: searchType,
                classification: classification
            },
            success: function (data) {
                /*<![CDATA[*/
                $(fragment).html(data);
                /*]]>*/
            },
        })
    }






/* ======================================================
    METHODS TO CHANGE THE LIST OF DISPLAYED TWEETS
====================================================== */

/*
    This method changes the Type of search and display the correct set of tweets,
    it will always attack to the tweet set and not the replies one
*/
function groupChangesListOfTweets(){
    pageNumber = 1
    changeSearchType(this.id);
    changeLowerTable();
}

/*
    This method changes the Type of search and display the correct set of tweets,
    it will always attack to the tweet set and not the replies one unless you
    change it with the lower navigation bar (Tweets/replies)
*/
function changeSearchType(id){
    if(id === 'SentimentImage'){
        searchType = 'Img & Sentiment';
    }else if(id === 'SentimentEmoji'){
        searchType = 'Emoji & Sentiment';
    }else{
        searchType = 'Full';
    }
    belongsTo = 'Tweet';
}

/*
    This function is used to make the call to the controller and change the table with the tweets
*/
function changeLowerTable(){
    $.ajax({
            type: 'get',
            url: '/sentiment/fragments/tweet_table',
            data: {
                searchType: searchType,
                classification: classification,
                belongsTo: belongsTo,
                page: pageNumber
            },
            success: function (data) {
                /*<![CDATA[*/
                console.log(data);
                $('#layers_card').html(data);
                changeAfterFragment();
                /*]]>*/
            },
        })
    }




/* ======================================================
    METHODS TO CHANGE THE LIST OF TWEETS/REPLIES
====================================================== */


function groupChangesTweetReply(){
    changeBelongsTo(this.id);
    changeLowerTable();
    changeAfterFragment();
    changeButtonsByPageNumber();
    pageNumber = 1
}

function changeBelongsTo(id){
    if(id === 'buttonSeeTweets'){
        belongsTo = 'Tweet';
    }else{
        belongsTo = 'Reply';
    }
}

function changeAfterFragment(){
    if(belongsTo === 'Tweet'){
        buttonSeeTweets.className = activeClass;
        buttonSeeReplies.className = inactiveClass;
    }else{
        buttonSeeTweets.className = inactiveClass;
        buttonSeeReplies.className = activeClass;
    }
}



/* ======================================================
            METHODS FOR THE PAGINATION
====================================================== */

/*
    This method actuates over the buttons in the pagination nav, and changes the page number
*/
function changeButtonsByPageNumber(){
    if(pageNumber === 1 && maxPageNumber === 1){
        buttonPrevious.style.display = 'none';
         buttonNext.style.display = 'none';
    }else{
        if(pageNumber === 1){
                buttonPrevious.style.display = 'none';
        }else if(pageNumber == maxPageNumber){
                buttonNext.style.display = 'none';
        }else{
            buttonPrevious.style.display = '';
            buttonNext.style.display = '';
        }
    }
    pageDescriber.innerHTML = 'Page ' + pageNumber + ' of ' + maxPageNumber + '  ('+searchType+')';
}


/*
    This method is activated by te pagination buttons. It changes the page number and the content of the
    table itself, besides the navigation bar text
*/
function changePaginationPage(){
    if(this.id === buttonNext.id){
        pageNumber++;
    }else{
        pageNumber--;
    }
    changeLowerTable();
    changeButtonsByPageNumber();
}


/*
    This function is used to assign the correct value to the maxPageNumber variable
    when the page is loaded
*/
function changeMaxPages(maxPages){
    if(typeof maxPages === 'undefined'){
        maxPageNumber = 1;
    }else{
        maxPageNumber = maxPages;
    }
}



