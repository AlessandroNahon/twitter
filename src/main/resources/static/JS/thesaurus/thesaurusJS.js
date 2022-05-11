var comboboxHeader = '';
var buttonNavTweet = '';
var buttonNavReplies = '';
var buttonNavWords = '';
var buttonNavEmoji = '';

var buttonNoun = '';
var buttonAdverb = '';
var buttonAdjective = '';
var buttonEmoji = '';

var mainOrganization = 'Greenpeace';
var belongsTo = 'Tweet';
var lookingFor = 'Word';

var navActive = 'bg-transparent nav-link active';
var navInactive = 'bg-transparent nav-link';

function loadComponentIndex(){
    comboboxHeader = document.getElementById('comboboxIndex');
    buttonNavTweet = document.getElementById('buttonNavTweet');
    buttonNavReplies = document.getElementById('buttonNavReplies');
    comboboxHeader.addEventListener("change", changeIndex);
    buttonNavTweet.addEventListener("click", changeNavButtons);
    buttonNavReplies.addEventListener("click", changeNavButtons);

    buttonNoun = document.getElementById('buttonNoun');
    buttonAdverb = document.getElementById('buttonAdverb');
    buttonAdjective = document.getElementById('buttonAdjective');
    buttonEmoji = document.getElementById('buttonEmoji');



    getThesaurusLookup();
}

function addEventToButtonsFromFragment(){
    buttonNavWord = document.getElementById('buttonNavWord');
    buttonNavEmoji = document.getElementById('buttonNavEmoji');
    buttonNavWord.addEventListener("click", selectWordsOrEmojis);
    buttonNavEmoji.addEventListener("click", selectWordsOrEmojis);
}

function changeNavButtons(){
    if(this.id === 'buttonNavTweet'){
        buttonNavTweet.className=navActive
        buttonNavReplies.className=navInactive
        belongsTo = 'Tweet'
    }else{
        buttonNavReplies.className=navActive
        buttonNavTweet.className=navInactive
        belongsTo = 'Reply'
    }
    mainOrganization = comboboxHeader.value
    getThesaurusLookup();
}

function selectWordsOrEmojis(){
    if(this.id === 'buttonNavWord'){
        buttonNavWord.className=navActive
        buttonNavEmoji.className=navInactive
        lookingFor = 'Word'
    }else{
        buttonNavEmoji.className=navActive
        buttonNavWord.className=navInactive
        lookingFor = 'Emoji'
    }
    mainOrganization = comboboxHeader.value
}

function changeIndex(){
    mainOrganization = comboboxHeader.value
    belongsTo = 'Tweet'
    buttonNavTweet.className=navActive
    buttonNavReplies.className=navInactive
    getThesaurusLookup();
}

function getThesaurusLookup(){
    var fragment = '#thesaurus_lookup'
      $.ajax({
            type: 'get',
            url: '/thesaurus/fragment/whole_display_info',
            data: {
                organization: mainOrganization,
                belongsTo: belongsTo,
                lookingFor: lookingFor
            },
            success: function (data) {
                /*<![CDATA[*/
                $(fragment).html(data);
                /*]]>*/
            },
        });
}

function getJustSpecialWords(){
var fragment = '#thesaurus_lookup'
      $.ajax({
            type: 'get',
            url: '/thesaurus/fragment/whole_display_info',
            data: {
                organization: mainOrganization,
                belongsTo: belongsTo,
                lookingFor: lookingFor
            },
            success: function (data) {
                /*<![CDATA[*/
                $(fragment).html(data);
                /*]]>*/
            },
        });
}



