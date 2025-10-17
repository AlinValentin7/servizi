/**
 * Sistema di navigazione responsive con menu hamburger
 * @author Firmato $₿420
 */

document.addEventListener('DOMContentLoaded', function() {
    // Crea il pulsante hamburger se non esiste già
    const nav = document.querySelector('nav');
    if (!nav) return;

    // Verifica se hamburger esiste già
    let hamburger = document.querySelector('.hamburger');
    if (!hamburger) {
        // Crea il pulsante hamburger
        hamburger = document.createElement('div');
        hamburger.className = 'hamburger';
        hamburger.innerHTML = '<span></span><span></span><span></span>';
        
        // Inserisci l'hamburger dopo il logo
        const logo = nav.querySelector('.logo');
        if (logo) {
            logo.parentNode.insertBefore(hamburger, logo.nextSibling);
        }
    }

    // Crea overlay per mobile
    let overlay = document.querySelector('.nav-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.className = 'nav-overlay';
        document.body.appendChild(overlay);
    }

    const navLinks = document.querySelector('.nav-links');

    // Toggle menu
    function toggleMenu() {
        hamburger.classList.toggle('active');
        navLinks.classList.toggle('active');
        overlay.classList.toggle('active');
        document.body.style.overflow = navLinks.classList.contains('active') ? 'hidden' : '';
    }

    // Event listeners
    hamburger.addEventListener('click', toggleMenu);
    overlay.addEventListener('click', toggleMenu);

    // Chiudi menu quando si clicca su un link
    const links = navLinks.querySelectorAll('a');
    links.forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 768) {
                toggleMenu();
            }
        });
    });

    // Chiudi menu quando si ridimensiona la finestra oltre i 768px
    window.addEventListener('resize', () => {
        if (window.innerWidth > 768 && navLinks.classList.contains('active')) {
            toggleMenu();
        }
    });

    // Previeni scroll quando il menu è aperto
    document.addEventListener('touchmove', function(e) {
        if (navLinks.classList.contains('active') && !navLinks.contains(e.target)) {
            e.preventDefault();
        }
    }, { passive: false });
});
