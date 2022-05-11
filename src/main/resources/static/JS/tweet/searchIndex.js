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
var pageNumber = 1;
var maxPageNumber = 1;

var listOfUsers = ['Greenpeace','Peta','WWF']
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

    var buttonPrevious = document.getElementById('buttonPrevious');
    var buttonNext = document.getElementById('buttonNext');
    var pageDescriber = document.getElementById('pageDescriber');
    buttonPrevious.addEventListener('click', changePaginationPage);
    buttonNext.addEventListener('click', changePaginationPage);
    changeButtonsByPageNumber();


}

function changeMaxPages(maxPages){
    if(typeof maxPages === 'undefined'){
        this.maxPageNumber = 1;
    }else{
        this.maxPageNumber = maxPages;
    }
    changeButtonsByPageNumber();
}

function changePaginationPage(){
    if(this.id === buttonNext.id){
        pageNumber++;
    }else{
        pageNumber--;
    }
    changeButtonsByPageNumber();
}

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
    pageDescriber.innerHTML = 'Page ' + pageNumber + ' of ' + maxPageNumber;
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
                searchValue: searchValue,
                pageNumber: pageNumber
            },
            success: function (data) {
            console.log(data)
                /*<![CDATA[*/
                $(fragment).html(data);

                /*]]>*/
            },
        })
        pageNumber = 1;
        changeButtonsByPageNumber();
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
