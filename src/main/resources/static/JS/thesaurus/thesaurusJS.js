var comboboxHeader = '';
var buttonNavTweet = '';
var buttonNavReplies = '';
var buttonNavWords = '';
var buttonNavEmoji = '';
var buttonSearch = '';

var navSeeChart = '';
var navSeeCloud = '';
var navSeeRaw = '';

var words = [];
var freq = [];

var buttonNoun = '';
var buttonAdverb = '';
var buttonAdjective = '';
var buttonEmoji = '';
var lookingBySearchBar = false;

var mainOrganization = 'Greenpeace';
var belongsTo = 'Tweet';
var lookingFor = 'Word';
var searchWord = '';

var navActive = 'bg-transparent nav-link active';
var navInactive = 'bg-transparent nav-link';

var wholeFragmentName = '#thesaurus_lookup';
var kistchGrotesqueTable = '#special_words';
var lowerTable = '#words_table_tweets';

var wholeFragmentUrl = '/thesaurus/fragment/whole_display_info';
var kistchGrotesqueUrl = '/thesaurus/fragment/special_words';
var lowerTableUrl = '/thesaurus/fragment/words_table_tweets';
var cloudUrl = '/thesaurus/fragment/word_cloud';
var rawUrl = '/thesaurus/fragment/raw_words_table';

var maxPages = 1;
var currentPage = 1;
var buttonNext = '';
var buttonPrevious = '';
var tableTitle = '';
var pageDescriber = '';

var nameClassActive = 'loader';
var nameClassInactive = 'loader-inactive';
var loading = '';
var whole_display_info = '';


function loadComponentIndex(){
    comboboxHeader = document.getElementById('comboboxIndex');
    buttonNavTweet = document.getElementById('buttonNavTweet');
    buttonNavReplies = document.getElementById('buttonNavReplies');
    comboboxHeader.addEventListener("change", changeIndex);
    buttonNavTweet.addEventListener("click", changeNavButtons);
    buttonNavReplies.addEventListener("click", changeNavButtons);
    navSeeChart = document.getElementById('buttonSeeCharts');
    navSeeCloud = document.getElementById('buttonSeeCloud');
    navSeeRaw = document.getElementById('buttonSeeRaw');

    navSeeChart.addEventListener("click", changeCloudChart);
    navSeeCloud.addEventListener("click", changeCloudChart);
    navSeeRaw.addEventListener("click", changeCloudChart);
    updateContent(wholeFragmentName, wholeFragmentUrl);

    loading = document.getElementById('loaderDiv');

}

function addEventToButtonsFromFragment(){
    buttonNavWord = document.getElementById('buttonNavWord');
    buttonNavEmoji = document.getElementById('buttonNavEmoji');
    buttonNavWord.addEventListener("click", selectWordsOrEmojis);
    buttonNavEmoji.addEventListener("click", selectWordsOrEmojis);

    buttonSearch = document.getElementById('buttonSearch');
    buttonSearch.addEventListener("click", search);

    tableTitle = document.getElementById('searchTitle');
    pageDescriber = document.getElementById('pageDescriber');
    buttonNext = document.getElementById('buttonNext');
    buttonPrevious = document.getElementById('buttonPrevious');

    buttonNext.addEventListener("click", changePage);
    buttonPrevious.addEventListener("click", changePage);
    updateTableTitle();

    buttonNoun = document.getElementById('buttonNoun');
    buttonAdverb = document.getElementById('buttonAdverb');
    buttonAdjective = document.getElementById('buttonAdjective');
    buttonEmoji = document.getElementById('buttonEmoji');
    buttonNoun.addEventListener("click", searchByCard);
    buttonAdverb.addEventListener("click", searchByCard);
    buttonAdjective.addEventListener("click", searchByCard);
    buttonEmoji.addEventListener("click", searchByCard);
        whole_display_info = document.getElementById('whole_display_info');

}

function changeCloudChart(){
var url = '';
    if(this.id === 'buttonSeeCloud'){
      loading.className = nameClassActive;
      whole_display_info.style = 'visibility: hidden;';
      navSeeChart.className=navInactive
      navSeeCloud.className=navActive
      navSeeRaw.className=navInactive
      url = cloudUrl;
    }else if(this.id == 'buttonSeeRaw'){
        navSeeRaw.className=navActive
        loading.className = nameClassActive;
        navSeeChart.className=navInactive
        navSeeCloud.className=navInactive
        url = rawUrl;
    }else{
      navSeeCloud.className=navInactive
      whole_display_info.style = 'visibility: visible;';
      navSeeChart.className=navActive
      navSeeRaw.className=navInactive
      url = wholeFragmentUrl;
    }
    lookingBySearchBar = false;
    updateContent(wholeFragmentName, url);
    currentPage = 1;
    updateTableTitle();

}





function changeMaxPages(maxPages){
    if(typeof maxPages == 'undefined'){
        maxPages = 1;
    }else{
    this.maxPages = maxPages;
    }
    updateTableTitle();
}

function updateTableTitle(){
    tableTitle.innerHTML = 'Searching for ' + lookingFor + ': '+ searchWord;
    pageDescriber.innerHTML = 'Page ' + currentPage + ' of ' + maxPages;
    if(currentPage === 1 && maxPages === 1){
        buttonPrevious.style = 'display: none;';
        buttonNext.style = 'display: none;';
    }else if(currentPage === 1 && maxPages > 1){
        buttonPrevious.style = 'display: none;';
        buttonNext.style = 'display: ;';
    }else if(currentPage === maxPages && maxPages > 1){
        buttonNext.style = 'display: none;';
        buttonPrevious.style = 'display: ;';
    }else{
       buttonNext.style.display = 'display: ;';
       buttonPrevious.style.display = 'display: ;';
    }
}

function changePage(){
    if(this.id === 'buttonNext'){
        currentPage = currentPage + 1;
    }else{
        currentPage = currentPage - 1;
    }
    if(lookingBySearchBar){
        searchWord = document.getElementById('textSearch').value;
    }
    updateLowerTable(lowerTable, lowerTableUrl);
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
    navSeeChart.className=navActive
    navSeeCloud.className=navInactive
    mainOrganization = comboboxHeader.value
    updateContent(wholeFragmentName, wholeFragmentUrl);
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
    updateContent(kistchGrotesqueTable, kistchGrotesqueUrl);
}

function changeIndex(){
    mainOrganization = comboboxHeader.value
    belongsTo = 'Tweet'
    buttonNavTweet.className=navActive
    buttonNavReplies.className=navInactive
    navSeeChart.className=navActive
    navSeeCloud.className=navInactive
    updateContent(wholeFragmentName, wholeFragmentUrl);
}

function searchByCard(){
    currentPage = 1;
    searchWord = this.textContent;
    updateLowerTable(lowerTable, lowerTableUrl);

}


function search(){
    searchWord = document.getElementById('textSearch').value;
    lookingBySearchBar = true;
    if(searchWord.length > 0){
        updateLowerTable(lowerTable, lowerTableUrl);
        currentPage = 1;
        updateTableTitle();
    }else{
        alert('Please enter a word to search');
    }
}

function updateContent(fragment, url){
  $.ajax({
        type: 'get',
        url: url,
        data: {
            organization: mainOrganization,
            belongsTo: belongsTo,
            lookingFor: lookingFor
        },
        success: function (data) {
            /*<![CDATA[*/
            $(fragment).html(data);
            loading.className = nameClassInactive;
            /*]]>*/
        },
    });
}



function updateLowerTable(fragment, url, word){
 $.ajax({
        type: 'get',
        url: url,
        data: {
            organization: mainOrganization,
            belongsTo: belongsTo,
            word: searchWord,
            page: currentPage
        },
        success: function (data) {
            /*<![CDATA[*/
            $(fragment).html(data);
            updateTableTitle();
                document.getElementById("words_table_tweets").scrollIntoView(false);
            /*]]>*/
        },
    });

}






