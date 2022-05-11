var tweetId = '';


function callFragment() {
    $.ajax({
        type: 'get',
        url: '/tweet/fragments/display_image_modal',
        data: {
            id: this.id
        },
        success: function (data) {
            /*<![CDATA[*/
            $('#seeModal').html(data);
            /*]]>*/
        },
    });
}


function addEventToButton(buttonid) {
    document.getElementById('organizationSelector').innerHTML = '';
    var button = document.getElementById(buttonid)
    button.addEventListener("click", callFragment);
}

function addEventToButtonReply(buttonid) {
    var button = document.getElementById(buttonid)
    button.addEventListener("click", callFragmentReply);
    console.log(button.id);
}

function changeTitleText(textTitle){
    var title = document.getElementById('sentimentText')
    text = toTitleCase(textTitle)
    title.innerText = text
}

function toTitleCase(str) {
  return str.replace(
    /\w\S*/g,
    function(txt) {
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    }
  ).replace('_',' ');
}

function callFragmentReply() {
    $.ajax({
        type: 'get',
        url: '/tweet/fragments/show_reply_images',
        data: {
            id: this.id
        },
        success: function (data) {
            /*<![CDATA[*/
            $('#show_reply_images').html(data);
            /*]]>*/
        },
    });
}

function changePictureClassification(){
    console.log('entra al metodo')
    var id = $('#imageIdInput').val();
    var tweetId = parseInt(window.location.href.split('/')[5])
    var classification = $('#classificationInput').val()
    changeButtonText()
    setTimeout(1000, changeButtonText(),$.ajax({
           type: 'get',
           url: '/tweet/showTweetInformation/'+tweetId,
           data: {
               editImage: true,
               imageId: id,
               classification: classification
           },
           success: function (data) {
               /*<![CDATA[*/
               document.location.reload(true)
               /*]]>*/
           },
       })) ;
}

function changeButtonText(){
    var button = document.getElementById('changeButton')
    button.innerText = 'Hold on...'
    button.className = 'btn bg-light text-black p-3 d-inline-block'
}

function getSentimentSelected(){
    var newSentiment = document.getElementById('dropdownSentiment').value;
    updateTweet(true, newSentiment)
}

function updateTweet(changeSentiment, newSentiment){
    $.ajax({
            type: 'get',
            url: '/tweet/fragments/fooHtml',
            data: {
                id: tweetId,
                changeSentiment: changeSentiment,
                sentiment: newSentiment
            },
            success: function (data) {
                /*<![CDATA[*/
                if(changeSentiment){
                    window.location.reload()
                }else{
                    window.location.replace("/tweet/searchIndex");
                }
                /*]]>*/
            },
        });
}
