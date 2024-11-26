
function getSprite() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200){
            document.getElementById("sprite").src = this.responseText;
        }
    };
    xhttp.open("GET", "/card/wild/sprite", true);
    xhttp.send();
}

getSprite();

console.log('Pokemon spawn script loaded');
const stompClient = new StompJs.Client({
    //brokerURL: 'ws://localhost:8080/pokemon-websocket'
    brokerURL: 'wss://kpopcardbot.onrender.com/pokemon-websocket'
});

stompClient.activate();

stompClient.onConnect = function(frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/pokemon/wild', function (message) {
        console.log('Got message: ' + message);
        if (message.body === 'null') {
            document.getElementById("sprite").src = '';
            return;
        }
        const pokemon = JSON.parse(message.body);
        document.getElementById("sprite").style.display = 'block';
        document.getElementById("sprite").src = pokemon.photo;
    })

    stompClient.subscribe('/topic/pokemon/catch', function (message) {
        console.log('Got message: ' + message);
        // message.body is a JSON string with the ball and the capture true/false
        const capture = JSON.parse(message.body);
        capturePokemon(capture.caught, capture.pokeball);

    })
}

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};


function capturePokemon(capture, ball) {
    console.log('Capture', capture);

    if (capture === "true") {
        console.log('Pokemon caught');
        document.getElementById("sprite").style.display = 'none';
    } else {
        console.log('Pokemon escaped');
        document.getElementById("sprite").style.display = 'block';
    }
}