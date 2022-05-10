var selectOptionClass = 'dropdown-item text-dark';

var radioChecked = '';
var dropdownMenu = '';
var inputBox = '';
var searchButton = '';
var tableTitle = '';

var radioUsername = '';
var radioSentiment = '';
var radioImage = '';
var radioWord = '';

var searchValue = '';

var listOfUsers = ['Greenpeace','Peta','WWF']
var listOfSentiments = ['Very Positive','Positive','Neutral','Negative','Very Negative'];
var listOfImageType = ['Kistch','Grotesque']


function loadComponents(){
    dropdownMenu = document.getElementById('dropdownMenuButton');
    dropdownMenu.addEventListener('change', onChangeDropdown);
    radioUsername = document.getElementById('radioUsername');
    radioSentiment = document.getElementById('radioSentiment');
    radioImage = document.getElementById('radioImage');
    radioWord = document.getElementById('radioWord');
    inputBox = document.getElementById('textSearch');

    disable(inputBox);

    radioUsername.addEventListener("click", behaviorRadioButtons);
    radioSentiment.addEventListener("click", behaviorRadioButtons);
    radioWord.addEventListener("click", behaviorRadioButtons);
    radioImage.addEventListener("click", behaviorRadioButtons);
    searchButton = document.getElementById('searchButton');
    searchButton.addEventListener("click", search);
    tableTitle = document.getElementById('tableTitle');
}

function behaviorRadioButtons(){
   enable(dropdownMenu);
    if(this.id == radioUsername.id){
        changeDropDown(listOfUsers);
        radioChecked = 'username'
        searchValue = 'Greenpeace'
        disable(inputBox);
    }else if(this.id == radioSentiment.id){
        changeDropDown(listOfSentiments);
        searchValue = 'Very positive'
        radioChecked = 'sentiment'
         disable(inputBox);
    }else if(this.id == radioImage.id){
        changeDropDown(listOfImageType);
        searchValue = 'Kistch'
        radioChecked = 'image'
    disable(inputBox);
    }else{
        dropdownMenu.innerHTML = '';
        disable(dropdownMenu);
        enable(inputBox);
        searchValue = '';
        radioChecked = 'word'
    }
}

function changeDropDown(list){
    dropdownMenu.innerHTML = '';
    list.forEach(element => {
    var option = document.createElement('option');
            option.value = element;
            option.innerText = element
            option.className = selectOptionClass;
            dropdownMenu.appendChild(option);
    });
    inputBox.value = '';
}

function onChangeDropdown(){
    searchValue = this.value;
}

function search(){
    if(radioChecked === 'word'){
        searchValue = inputBox.value;
        if(searchValue === ''){
            alert('Please, write a word to search');
        }else{
            getNewTableContent();
        }
    }else{
        searchValue = dropdownMenu.value;
        getNewTableContent();
    }
    tableTitle.innerHTML = 'Search results for ' + radioChecked+ ': ' + searchValue;
}

function getNewTableContent(){
    var fragment = '#table_search_tweet'
    $.ajax({
            type: 'get',
            url: '/tweet/fragments/table_search_tweet',
            data: {
                searchBy: radioChecked,
                searchValue: searchValue
            },
            success: function (data) {
            console.log(data)
                /*<![CDATA[*/
                $(fragment).html(data);
                /*]]>*/
            },
        })
    }


/*
    Methods to modify the enable/disable state of the different tags
*/

function disable(element) {
    element.disabled = true;
}
function enable(element) {
    element.disabled = false;
}
