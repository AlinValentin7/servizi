/**
 * ESEMPI DI UTILIZZO - Sistema React-Like e Animazioni
 * Copia questi esempi nei tuoi file per usare le nuove funzionalità
 */

// ========================================
// ESEMPIO 1: Toast Notifications
// ========================================
document.getElementById('btn-success').addEventListener('click', () => {
    Toast.show('Prenotazione confermata con successo!', 'success');
});

document.getElementById('btn-error').addEventListener('click', () => {
    Toast.show('Si è verificato un errore', 'error');
});

// ========================================
// ESEMPIO 2: Animazione Counter
// ========================================
const statsCounters = document.querySelectorAll('.stat-number');
statsCounters.forEach(counter => {
    const target = parseInt(counter.dataset.target);
    AnimationUtils.animateCounter(counter, target, 2000);
});

// ========================================
// ESEMPIO 3: Typewriter Effect
// ========================================
const heroTitle = document.querySelector('.hero-title');
if (heroTitle) {
    AnimationUtils.typeWriter(heroTitle, 'Servizi Edili Elvis SRL', 100);
}

// ========================================
// ESEMPIO 4: Form Validation con Animazioni
// ========================================
new FormValidator('form-prenotazione');

// ========================================
// ESEMPIO 5: Componente Card Dinamica
// ========================================
const cardContainer = document.getElementById('services-cards');
if (cardContainer) {
    const services = [
        {
            title: 'Ristrutturazioni',
            description: 'Ristrutturazioni complete di appartamenti e uffici',
            icon: 'bi bi-hammer',
            animation: 'zoom'
        },
        {
            title: 'Costruzioni',
            description: 'Nuove costruzioni con materiali di qualità',
            icon: 'bi bi-building',
            animation: 'slide-left'
        },
        {
            title: 'Manutenzioni',
            description: 'Servizi di manutenzione ordinaria e straordinaria',
            icon: 'bi bi-tools',
            animation: 'slide-right'
        }
    ];

    services.forEach(service => {
        const html = App.components.get('AnimatedCard')(service);
        cardContainer.innerHTML += html;
    });
}

// ========================================
// ESEMPIO 6: Scroll Progress Bar
// ========================================
AnimationUtils.onScrollProgress((progress) => {
    const progressBar = document.getElementById('scroll-progress');
    if (progressBar) {
        progressBar.style.width = progress + '%';
    }
});

// ========================================
// ESEMPIO 7: Parallax Effect (solo desktop)
// ========================================
if (window.innerWidth > 768) {
    AnimationUtils.enableParallax('.hero-background');
}

// ========================================
// ESEMPIO 8: Lazy Loading Immagini
// ========================================
// Cambia src con data-src nelle immagini:
// <img data-src="percorso/immagine.jpg" alt="..." />
new LazyLoader('img[data-src]');

// ========================================
// ESEMPIO 9: Animazione al Click
// ========================================
document.querySelectorAll('.btn-animated').forEach(btn => {
    btn.addEventListener('click', function() {
        AnimationUtils.animate(this, 'bounce-animation', 500);
    });
});

// ========================================
// ESEMPIO 10: Loading Dinamico
// ========================================
function showLoading() {
    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'dynamic-loading';
    document.body.appendChild(loadingDiv);
    
    App.mountComponent('LoadingSpinner', 'dynamic-loading', {
        text: 'Caricamento dati...',
        size: 'large'
    });
}

function hideLoading() {
    const loading = document.getElementById('dynamic-loading');
    if (loading) {
        loading.remove();
    }
}

// ========================================
// ESEMPIO 11: Modal Dinamico
// ========================================
function showConfirmModal(title, message, onConfirm) {
    const modalId = 'dynamic-modal-' + Date.now();
    const modalHTML = App.components.get('DynamicModal')({
        id: modalId,
        title: title,
        body: message,
        footer: `
            <button class="btn btn-secondary" data-bs-dismiss="modal">Annulla</button>
            <button class="btn btn-primary" id="confirm-btn">Conferma</button>
        `
    });
    
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    const modal = new bootstrap.Modal(document.getElementById(modalId));
    
    document.getElementById('confirm-btn').addEventListener('click', () => {
        onConfirm();
        modal.hide();
    });
    
    modal.show();
}

// Uso:
// showConfirmModal('Conferma', 'Vuoi procedere?', () => {
//     Toast.show('Confermato!', 'success');
// });

// ========================================
// ESEMPIO 12: State Management Reattivo
// ========================================
// Imposta uno state
App.setState('userCount', 0);

// Incrementa
document.getElementById('increment').addEventListener('click', () => {
    const current = App.getState('userCount');
    App.setState('userCount', current + 1);
});

// Nell'HTML usa data-reactive per aggiornamento automatico:
// <span data-reactive="userCount">0</span>

// ========================================
// ESEMPIO 13: Fetch con Loading e Toast
// ========================================
async function fetchData(url) {
    showLoading();
    
    try {
        const response = await fetch(url);
        const data = await response.json();
        hideLoading();
        Toast.show('Dati caricati con successo!', 'success');
        return data;
    } catch (error) {
        hideLoading();
        Toast.show('Errore nel caricamento: ' + error.message, 'error');
        throw error;
    }
}

// ========================================
// ESEMPIO 14: Animazione Stagger per Liste
// ========================================
const listContainer = document.querySelector('.services-list');
if (listContainer) {
    listContainer.classList.add('stagger-animation');
}

// ========================================
// ESEMPIO 15: Card 3D Hover Effect
// ========================================
document.querySelectorAll('.project-card').forEach(card => {
    card.classList.add('card-3d');
    
    card.addEventListener('mousemove', (e) => {
        const rect = card.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        
        const centerX = rect.width / 2;
        const centerY = rect.height / 2;
        
        const rotateX = (y - centerY) / 10;
        const rotateY = (centerX - x) / 10;
        
        card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
    });
    
    card.addEventListener('mouseleave', () => {
        card.style.transform = 'perspective(1000px) rotateX(0) rotateY(0)';
    });
});
