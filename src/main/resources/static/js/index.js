document.addEventListener('DOMContentLoaded', (event) => {
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    if (error) {
        const errorMessage = document.createElement('div');
        errorMessage.textContent = 'User not found. Please try again.';
        errorMessage.style.color = 'red';
        errorMessage.style.marginTop = '10px';
        document.querySelector('.search-container').appendChild(errorMessage);
    }
});

function handleSearch(event) {
    event.preventDefault();
    const searchTerm = event.target.searchterm.value;

    if (!searchTerm) {
        return;
    }

    window.location.href = `/photocards/${searchTerm}`;
}