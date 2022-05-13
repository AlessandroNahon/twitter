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

var maxPages = 1;
var currentPage = 1;
var buttonNext = '';
var buttonPrevious = '';
var pageDescriber = '';

var organization = '';



var listOfUsers = ['Greenpeace','peta','WWF']
var listOfSentiments = ['Very Positive','Positive','Neutral','Negative','Very Negative'];
var listOfImageType = ['Kistch','Grotesque']


function loadComponents(){
    document.getElementById('organizationSelector').innerHTML = '';
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
    getNewTableContent();
    changeButtonsByPageNumber();
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
        changeDropDown(listOfUsers);
        enable(dropdownMenu);
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
    organization = this.value;
}

function search(){
    currentPage = 1;
    changeButtonsByPageNumber();
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
                searchValue: searchValue,
                currentPage: currentPage,
                organization: organization
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
