/**
 * Gestione Loader Pagina con Mattoncini Animati
 * Mostra l'animazione solo la prima volta durante la sessione
 */

document.addEventListener('DOMContentLoaded', () => {
    const loader = document.getElementById('page-loader');
    const LOADER_DURATION = 2000; // 2 secondi
    const FADE_DURATION = 500;    // 0.5 secondi
    const STORAGE_KEY = 'homeLoaderShown';
    
    // Verifica se il loader è già stato mostrato in questa sessione
    const hasSeenLoader = sessionStorage.getItem(STORAGE_KEY);
    
    if (hasSeenLoader) {
        // Nasconde immediatamente il loader per le visite successive
        hideLoaderInstantly();
    } else {
        // Prima visita: mostra il loader completo
        showLoaderWithAnimation();
    }
    
    /**
     * Nasconde il loader immediatamente senza animazione
     */
    function hideLoaderInstantly() {
        loader.style.display = 'none';
        document.body.style.overflow = '';
    }
    
    /**
     * Mostra il loader con animazione completa per 2 secondi
     */
    function showLoaderWithAnimation() {
        // Blocca lo scroll durante il caricamento
        document.body.style.overflow = 'hidden';
        
        setTimeout(() => {
            // Avvia la dissolvenza
            loader.classList.add('fade-out');
            
            setTimeout(() => {
                // Rimuove completamente dal DOM dopo la transizione
                loader.style.display = 'none';
                document.body.style.overflow = '';
            }, FADE_DURATION);
            
            // Segna come già visto in questa sessione
            sessionStorage.setItem(STORAGE_KEY, 'true');
        }, LOADER_DURATION);
    }
});
