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
    var button = document.getElementById(buttonid)
    button.addEventListener("click", callFragment);
}

function addEventToButtonReply(buttonid) {
    var button = document.getElementById(buttonid)
    button.addEventListener("click", callFragmentReply);
    console.log(button.id);
}

function callFragmentReply() {
    console.log("llama al m�todo con id: " + this.id);
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

