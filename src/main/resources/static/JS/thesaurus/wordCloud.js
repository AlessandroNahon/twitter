var words = '';
var cloud = '';
var traceCanvas = '';
var traceCanvasCtx = '';
var startPoint = '';
var wordsDown = [];
var colors = ["rgb(113, 43, 117)","rgb(49, 53, 82)","rgb(0, 110, 127)","rgb(238, 80, 7)","rgb(178, 39, 39)",
"rgb(46, 176, 134)","rgb(54, 174, 124)","rgb(54, 174, 124)","rgb(24, 116, 152)"]

var completeWordList = [];
var frequency =[];

var config = {
    trace: true,
    spiralResolution: 1, //Lower = better resolution
    spiralLimit: 360 * 5,
    lineHeight: 0.8,
    xWordPadding: 5,
    yWordPadding: 3,
    font: "Trebuchet MS",
}


function getWordSet(listOfWords){
    completeWordList = [];
    frequency = [];
    listOfWords.forEach(function(word){
        completeWordList.push(word.word);
        frequency.push(word.count);
    });
    var count = -1;
    words = completeWordList.map(function(word){
      return {
        word: word,
        freq: frequency[++count]
      }
     });
}

function wrapFunctionality(listOfWords){
    words = '';
    cloud = '';
    traceCanvas = '';
    traceCanvasCtx = '';
    startPoint = '';
    wordsDown = [];
    getWordSet(listOfWords);
    words.sort(function(a, b) {
        return -1 * (a.freq - b.freq);
    });

    //Get the cloud element
    cloud = document.getElementById("word-cloud");
    cloud.className = 'd-flex col-12';
    cloud.style.height = '800px';
    cloud.style.width = '100%';
    cloud.style.position = "relative";
    cloud.style.fontFamily = config.font;

    //Get the canvas element
    traceCanvas = document.createElement("canvas");
    traceCanvas.width = cloud.offsetWidth;
    traceCanvas.height = cloud.offsetHeight;


    //Get the context of the canvas
    traceCanvasCtx = traceCanvas.getContext("2d");
    cloud.appendChild(traceCanvas);

    //Print the start point
    startPoint = {
        x: cloud.offsetWidth / 2,
        y: cloud.offsetHeight / 2
    };

    placeWords();
    traceSpiral();
}

function getRandomColor(){
    return colors[Math.floor(Math.random() * colors.length)];
}


/* ======================= END GENERATOR ======================= */



/* =======================  PLACEMENT FUNCTIONS =======================  */
function createWordObject(word, freq) {
    var wordContainer = document.createElement("div");
    wordContainer.style.position = "absolute";
    wordContainer.style.fontSize = 11*freq + "px";
    wordContainer.style.lineHeight = config.lineHeight;
    wordContainer.style.color = getRandomColor();
/*    wordContainer.style.transform = "translateX(-50%) translateY(-50%)";*/
    wordContainer.appendChild(document.createTextNode(word));

    return wordContainer;
}

function placeWord(word, x, y) {

    cloud.appendChild(word);
    word.style.left = x - word.offsetWidth/3 + "px";
    word.style.top = y - word.offsetHeight/2 + "px";

    wordsDown.push(word.getBoundingClientRect());
}

function trace(x, y) {
//     traceCanvasCtx.lineTo(x, y);
//     traceCanvasCtx.stroke();
    traceCanvasCtx.fillRect(x, y, 1, 1);
}

function spiral(i, callback) {
    angle = config.spiralResolution * i;
    x = (1 + angle) * Math.cos(angle);
    y = (1 + angle) * Math.sin(angle);
    return callback ? callback() : null;
}

function intersect(word, x, y) {
    cloud.appendChild(word);

    word.style.left = x - word.offsetWidth/2 + "px";
    word.style.top = y - word.offsetHeight/2 + "px";

    var currentWord = word.getBoundingClientRect();

    cloud.removeChild(word);

    for(var i = 0; i < wordsDown.length; i+=1){
        var comparisonWord = wordsDown[i];

        if(!(currentWord.right + config.xWordPadding < comparisonWord.left - config.xWordPadding ||
             currentWord.left - config.xWordPadding > comparisonWord.right + config.wXordPadding ||
             currentWord.bottom + config.yWordPadding < comparisonWord.top - config.yWordPadding ||
             currentWord.top - config.yWordPadding > comparisonWord.bottom + config.yWordPadding)){

            return true;
        }
    }

    return false;
}
/* =======================  END PLACEMENT FUNCTIONS =======================  */





/* =======================  LETS GO! =======================  */
function placeWords() {
    for (var i = 0; i < words.length; i += 1) {

        var word = createWordObject(words[i].word, words[i].freq);

        for (var j = 0; j < config.spiralLimit; j++) {
            //If the spiral function returns true, we've placed the word down and can break from the j loop
            if (spiral(j, function() {
                    if (!intersect(word, startPoint.x + x, startPoint.y + y)) {
                        placeWord(word, startPoint.x + x, startPoint.y + y);
                        return true;
                    }
                })) {
                break;
            }
        }
    }
}

function traceSpiral() {

    traceCanvasCtx.beginPath();

    if (config.trace) {
        var frame = 1;

        function animate() {
            spiral(frame, function() {
                trace(startPoint.x + x, startPoint.y + y);
            });

            frame += 1;

            if (frame < config.spiralLimit) {
                window.requestAnimationFrame(animate);
            }
        }

        animate();
    }
};
/* ======================= WHEW. THAT WAS FUN. We should do that again sometime ... ======================= */



