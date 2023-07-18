var colorDonuts = 'rgba(119,221,119,1)'

//Used to identify the search, between image and emojis, images and text, and full search
var searchType = 'Choose one';

//Used to identify wheter we want to get the Sentimental, Grotesque or Gray tweets
var classification = 'Sentimental';

//Used to identify if we want to get the tweets or the replies
var belongsTo = 'Tweet'

var organization = 'Greenpeace';


var identifies = '';

var cardFragmentUrl = '/sentiment/fragments/cards_fragment';
var tableFragmentUrl = '/sentiment/fragments/table_info';

//This two variables are used to change the classes of each navigation button
var activeClass = 'bg-transparent nav-link active';
var inactiveClass = 'bg-transparent nav-link';

var buttonNavSentimental = '';
var buttonNavGrey = '';
var buttonNavDisruptive = '';

var buttonNavTweet = '';
var buttonNavReplies = '';

var comboboxIndex = '';

var maxPages = 1;
var currentPage = 1;
var buttonNext = '';
var buttonPrevious = '';
var pageDescriber = '';


/*
    This function calls the printTableMethod into the Charts.js so it prints the three different charts
    with different colors depending of the Sentimental/Disruptive/Grey classification
*/
function printOrientationCharts(sentimentImg,sentimentEmo ,fullMatch,text, fullcountpositive){
    printTables('sentimentImageChart',sentimentImg,fullcountpositive, 'Img & Sentiment');
    printTables('sentimentEmojiChart',sentimentEmo,fullcountpositive, 'Emoji & Sentiment');
    printTables('fullSearchChart',fullMatch,fullcountpositive, 'Full matches');
    printTables('textChart',text,fullcountpositive, 'Full matches');
}







/*
    This method initializes all the events of the page
*/
function loadComponents(){

buttonNavSentimental = document.getElementById('buttonNavSentimental');
buttonNavGrey = document.getElementById('buttonNavGray');
buttonNavDisruptive = document.getElementById('buttonNavDisruptive');

buttonNavTweet = document.getElementById('buttonSeeTweets');
buttonNavReplies = document.getElementById('buttonSeeReplies');

buttonNavSentimental.addEventListener('click', changeClassificationNavs);
buttonNavGrey.addEventListener('click', changeClassificationNavs);
buttonNavDisruptive.addEventListener('click', changeClassificationNavs);

buttonNavTweet.addEventListener('click', changeBelongs);
buttonNavReplies.addEventListener('click', changeBelongs);

comboboxIndex = document.getElementById('comboboxIndex');
comboboxIndex.addEventListener('change', changeOrganization);
callCardFragment();
}

function loadComponentTable(){
    buttonPrevious = document.getElementById('buttonPrevious');
    buttonNext = document.getElementById('buttonNext');
    pageDescriber = document.getElementById('pageDescriber');
    buttonPrevious.addEventListener('click', changePaginationPage);
    buttonNext.addEventListener('click', changePaginationPage);
    changeButtonsByPageNumber();
}

function changeMaxPages(maxPages){
    if(typeof maxPages === 'undefined'){
        this.maxPages = 1;
    }else if (maxPages === 0){
        this.maxPages = 1;
    }else{
        this.maxPages = maxPages;
    }
   changeButtonsByPageNumber();
}

function changePaginationPage(){
    if(this.id === 'buttonNext'){
    currentPage = currentPage + 1;
    }else{
        currentPage = currentPage - 1;
    }
    changeButtonsByPageNumber();
    changeFooterTable();
}

function changeButtonsByPageNumber(){
        pageDescriber.innerHTML = 'Page ' + currentPage + ' of ' + maxPages;
        if(currentPage === 1 && maxPages === 1){
            buttonPrevious.style=  'display: none;';
            buttonNext.style = 'display: none;';
        }else if(currentPage === 1 && maxPages > 1){
            buttonPrevious.style = 'display: none;';
            buttonNext.style = '';
        }else if(currentPage === maxPages && maxPages > 1){
            buttonNext.style = 'display: none;';
            buttonPrevious.style = '';
        }else{
           buttonNext.style = '';
           buttonPrevious.style = '';
        }
}



/*
    This function is used to print all the charts that shows the sentiment analysis of the tweets with
    the three levels of analysis: Sentimental, Grey (Text, emojis or Images do not match) and Disruptive
*/

function changeOrganization(){
    belongsTo = 'Tweet';
    classification = 'Sentimental';
    buttonNavTweet.className = activeClass;
    buttonNavReplies.className = inactiveClass;
    buttonNavSentimental.className = activeClass;
    buttonNavGray.className = inactiveClass;
    buttonNavDisruptive.className = inactiveClass;
    organization = comboboxIndex.value;
    colorDonuts = 'rgba(119,221,119,1)'
    callCardFragment();
}


function changeBelongs(){
    if(this.id === buttonNavTweet.id){
        buttonNavTweet.className = activeClass;
        buttonNavReplies.className = inactiveClass;
        belongsTo = 'Tweet';
    }else{
        buttonNavTweet.className = inactiveClass;
        buttonNavReplies.className = activeClass;
        belongsTo = 'Replies';
    }
    callCardFragment();
}

function changeClassificationNavs(){
    if(this.id === buttonNavSentimental.id){
        buttonNavSentimental.className = activeClass;
        buttonNavGray.className = inactiveClass;
        buttonNavDisruptive.className = inactiveClass;
        classification = 'Sentimental';
        colorDonuts = 'rgba(119,221,119,1)'
    }else if(this.id === buttonNavGray.id){
        buttonNavSentimental.className = inactiveClass;
        buttonNavGray.className = activeClass;
        buttonNavDisruptive.className = inactiveClass;
        classification = 'Grey';
    }else{
        buttonNavSentimental.className = inactiveClass;
        buttonNavGray.className = inactiveClass;
        buttonNavDisruptive.className = activeClass;
        classification = 'Disruptive';
        colorDonuts = "rgba(255,105,97,1)";
    }
    callCardFragment();
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
    var buttonText = document.getElementById('text');
    buttonSentimentImage.addEventListener('click', groupChangesListOfTweets);
    buttonSentimentEmoji.addEventListener('click', groupChangesListOfTweets);
    buttonFullSearch.addEventListener('click', groupChangesListOfTweets);
    buttonText.addEventListener('click', groupChangesListOfTweets);

}

function groupChangesListOfTweets(){
    currentPage = 1
    changeSearchType(this.id);

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
    }else if(id === 'fullSearch'){
        searchType = 'Full';
    }else{
        searchType = 'Text';
    }
    changeFooterTable();
}


function changeFooterTable(){
    var fragmentName = '#table_fragment';
    if(classification === 'Grey'){
        fragmentName = '#cards_fragment';
    }
    $.ajax({
            type: 'get',
            url: '/sentiment/fragments/table_info',
            data: {
                searchType: searchType,
                classification: classification,
                belongsTo: belongsTo,
                organization: organization,
                page: currentPage
            },
            success: function (data) {
                /*<![CDATA[*/
                console.log(data);
                $(fragmentName).html(data);
                $(fragmentName).css('display', '');
                changeButtonsByPageNumber();
                console.log(data);
                /*]]>*/
            },
        })
    }

    function callCardFragment(){
        currentPage = 1;
        var fragment = '#cards_fragment'
        $.ajax({
                type: 'get',
                url: '/sentiment/fragments/cards_fragment',
                data: {
                    organization: organization,
                    belongsTo: belongsTo,
                    classification: classification
                },
                success: function (data) {
                    /*<![CDATA[*/
                    $(fragment).html(data);
                     $('#table_fragment').css('display', 'none');
                    changeButtonsByPageNumber();
                    /*]]>*/
                },
            })
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
    currentPage = 1
    changeButtonsByPageNumber();
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





/* ======================================================
    METHODS TO CHANGE THE LIST OF DISPLAYED TWEETS
====================================================== */

/*
    This method changes the Type of search and display the correct set of tweets,
    it will always attack to the tweet set and not the replies one
*/



/*
    This function is used to make the call to the controller and change the table with the tweets
*/



/* ======================================================
    METHODS TO CHANGE THE LIST OF TWEETS/REPLIES
====================================================== */


function groupChangesTweetReply(){
    changeBelongsTo(this.id);
    changeLowerTable();
    changeAfterFragment();
    changeButtonsByPageNumber();
    currentPage = 1
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
    currentPage = 1
    changeButtonsByPageNumber();
}





