console.log('Pokemon combat script loaded');
const stompClient = new StompJs.Client({
    //brokerURL: 'ws://localhost:8080/pokemon-websocket'
    brokerURL: 'wss://kpopcardbot.onrender.com/pokemon-websocket'
});

stompClient.activate();

stompClient.onConnect = function(frame) {
console.log('Connected: ' + frame);
stompClient.subscribe('/topic/card/combat', function(message) {
    console.log('Got message: ' + message);
    if (message.body === 'null') {
        clearScreen();
        return;
    }
    const combatData = JSON.parse(message.body);

    if (combatData.end === true) {
        clearScreen();
        return;
    }

    updateCombatData(combatData);
})
}

function showScreen() {
    document.getElementById('pokemonArena').style.display = 'flex';
}

function clearScreen() {
    document.getElementById('pokemonArena').style.display = 'none';

    const mensajeText = document.getElementById('mensaje');
    mensajeText.textContent = '';
}

function updatePokemonInfo(pokemonIndex, pokemon) {
    const pokemonInfo = document.getElementById(`pokemonInfo${pokemonIndex}`);
    const pokemonImage = pokemonInfo.querySelector('.pokemon');
    const pokemonHP = pokemonInfo.querySelector('#pokemonHP' + pokemonIndex);

    const hpPercentage = (pokemon.currentHp / pokemon.hp) * 100;

    pokemonHP.textContent = `HP: ${pokemon.currentHp}/${pokemon.hp}`;

    const hpBar = pokemonInfo.querySelector('.hp-bar');
    hpBar.style.width = hpPercentage + '%';

    hpBar.style.backgroundColor = getColorForHP(hpPercentage);

    pokemonImage.src = pokemon.photo;
}

function updateCombatData(combatData) {
    showScreen();
    updatePokemonInfo(1, combatData.card1);
    updatePokemonInfo(2, combatData.card2);

    if (combatData.attack) {
        const attackText = document.getElementById('attackText');
        attackText.textContent = `${combatData.pokemon1} ha hecho ${combatData.attack} de daño!`;

        setTimeout(function() {
            attackText.textContent = '';
        }, 2000);
    }

    if (combatData.winner) {
        const winnerText = document.getElementById('winnerText');
        winnerText.textContent = `${combatData.winner.name} ha ganado!`;

        setTimeout(clearScreen, 10000);
    }
}


function getColorForHP(percentage) {
    if (percentage > 70) {
        return 'green';
    } else if (percentage > 30) {
        return 'yellow';
    } else {
        return 'red';
    }
}

clearScreen();