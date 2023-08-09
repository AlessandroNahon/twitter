var comboboxHeader = '';
var buttonNavTweet = '';
var buttonNavReplies = '';

var mainOrganization = 'Greenpeace';
var belongsTo = 'Tweet';

var navActive = 'bg-transparent nav-link active'
var navInactive = 'bg-transparent nav-link'

var nameClassActive = 'loader';
var nameClassInactive = 'loader-inactive';
var loading = '';
var general_lookup_card = '';

function loadComponentIndex(){
    comboboxHeader = document.getElementById('comboboxIndex');
    buttonNavTweet = document.getElementById('buttonNavTweet');
    buttonNavReplies = document.getElementById('buttonNavReplies');
    comboboxHeader.addEventListener("change", changeIndex);
    buttonNavTweet.addEventListener("click", changeNavButtons);
    buttonNavReplies.addEventListener("click", changeNavButtons);
    loading = document.getElementById('loaderDiv');
    general_lookup_card = document.getElementById('general_lookup_card');
    callFragmentMain();
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
  loading.className = nameClassActive;
  general_lookup_card.style = 'visibility: hidden;';
    callFragmentMain();
}

function changeIndex(){
    mainOrganization = comboboxHeader.value
    belongsTo = 'Tweet'
    buttonNavTweet.className=navActive
    buttonNavReplies.className=navInactive
      loading.className = nameClassActive;
      general_lookup_card.style = 'visibility: hidden;';
    callFragmentMain();


}

function callFragmentMain(){
 var fragment = '#general_lookup_card'
    $.ajax({
            type: 'get',
            url: '/indexFragments/whole_index_fragment',
            data: {
                organization: mainOrganization,
                belongsTo: belongsTo
            },
            success: function (data) {
            console.log(data)
                /*<![CDATA[*/
                $(fragment).html(data);
                  loading.className = nameClassInactive;
                  general_lookup_card.style = 'visibility: visible;';
                /*]]>*/
            },
        })

    }


function printOrientationCharts(positiveCount, ambiguousCount, negativeCount, totalCount){
    printSentimentAnalysis('postiveChart',positiveCount,totalCount,'Positive')
    printSentimentAnalysis('ambiguousChart',ambiguousCount,totalCount,'Ambiguous')
    printSentimentAnalysis('negativeChart',negativeCount,totalCount,'Negative')
}



