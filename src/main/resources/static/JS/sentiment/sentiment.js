var colorDonuts = 'rgba(119,221,119,1)'

//Used to identify the search, between image and emojis, images and text, and full search
var searchType = 'SentimentImage';

//Used to identify wheter we want to get the Sentimental, Grotesque or Gray tweets
var classification = 'Sentimental';

//Used to identify if we want to get the tweets or the replies
var belongsTo = 'Tweet'

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
                belongsTo: belongsTo
            },
            success: function (data) {
                /*<![CDATA[*/
                $('#layers_card').html(data);
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
}

function changeBelongsTo(id){
    if(id === 'buttonSeeTweets'){
        belongsTo = 'Tweet';
        buttonSeeTweets.className = activeClass;
        buttonSeeReplies.className = inactiveClass;
    }else{
        belongsTo = 'Reply';
        buttonSeeTweets.className = inactiveClass;
        buttonSeeReplies.className = activeClass;
    }
    alert(belongsTo);
}









