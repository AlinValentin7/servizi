/**
 * Sistema di gestione Cookie Banner GDPR-compliant
 * Servizi Edili Elvis SRL
 * 
 * Features:
 * - Banner con scelte granulari (Necessari, Analytics, Marketing)
 * - Salvataggio preferenze in localStorage
 * - Modal per gestione preferenze dettagliate
 * - Design integrato con il tema del sito
 * 
 * @author Firmato $₿420
 * @since 2025
 */

(function() {
    'use strict';

    const COOKIE_CONSENT_KEY = 'servizi_edili_cookie_consent';
    const COOKIE_CONSENT_VERSION = '1.0';

    // Configurazione categorie cookies
    const cookieCategories = {
        necessary: {
            name: 'Necessari',
            description: 'Cookie essenziali per il funzionamento del sito (sessione, sicurezza)',
            required: true,
            enabled: true
        },
        analytics: {
            name: 'Analytics',
            description: 'Cookie per analizzare come gli utenti usano il sito (Google Analytics)',
            required: false,
            enabled: false
        },
        marketing: {
            name: 'Marketing',
            description: 'Cookie per personalizzare annunci e contenuti promozionali',
            required: false,
            enabled: false
        }
    };

    // Controlla se il consenso è già stato dato
    function hasConsent() {
        const consent = localStorage.getItem(COOKIE_CONSENT_KEY);
        if (!consent) return false;
        
        try {
            const data = JSON.parse(consent);
            return data.version === COOKIE_CONSENT_VERSION && data.timestamp;
        } catch (e) {
            return false;
        }
    }

    // Salva le preferenze
    function saveConsent(preferences) {
        const consent = {
            version: COOKIE_CONSENT_VERSION,
            timestamp: new Date().toISOString(),
            preferences: preferences
        };
        localStorage.setItem(COOKIE_CONSENT_KEY, JSON.stringify(consent));
        
        // Applica le preferenze
        applyConsent(preferences);
    }

    // Applica il consenso (attiva/disattiva servizi esterni)
    function applyConsent(preferences) {
        // Analytics (Google Analytics, Matomo, etc.)
        if (preferences.analytics) {
            enableAnalytics();
        } else {
            disableAnalytics();
        }

        // Marketing (Facebook Pixel, Google Ads, etc.)
        if (preferences.marketing) {
            enableMarketing();
        } else {
            disableMarketing();
        }
    }

    // Funzioni per attivare/disattivare servizi
    function enableAnalytics() {
        console.log('✓ Analytics cookies abilitati');
        // Esempio: Google Analytics
        // window.dataLayer = window.dataLayer || [];
        // function gtag(){dataLayer.push(arguments);}
        // gtag('js', new Date());
        // gtag('config', 'GA_MEASUREMENT_ID');
    }

    function disableAnalytics() {
        console.log('✗ Analytics cookies disabilitati');
        // Disabilita Google Analytics
        window['ga-disable-GA_MEASUREMENT_ID'] = true;
    }

    function enableMarketing() {
        console.log('✓ Marketing cookies abilitati');
        // Esempio: Facebook Pixel
        // fbq('init', 'YOUR_PIXEL_ID');
    }

    function disableMarketing() {
        console.log('✗ Marketing cookies disabilitati');
    }

    // Crea il banner dei cookies
    function createCookieBanner() {
        const banner = document.createElement('div');
        banner.id = 'cookie-banner';
        banner.className = 'cookie-banner';
        banner.innerHTML = `
            <div class="cookie-banner-content">
                <div class="cookie-banner-icon">🍪</div>
                <div class="cookie-banner-text">
                    <h3>Questo sito usa i cookies</h3>
                    <p>Utilizziamo cookie per migliorare la tua esperienza. 
                    Puoi accettare tutti i cookie o gestire le tue preferenze.</p>
                </div>
                <div class="cookie-banner-actions">
                    <button id="cookie-settings-btn" class="cookie-btn cookie-btn-secondary">
                        <i class="bi bi-gear"></i> Gestisci
                    </button>
                    <button id="cookie-reject-btn" class="cookie-btn cookie-btn-secondary">
                        <i class="bi bi-x-circle"></i> Rifiuta
                    </button>
                    <button id="cookie-accept-btn" class="cookie-btn cookie-btn-primary">
                        <i class="bi bi-check-circle"></i> Accetta Tutti
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(banner);

        // Event listeners
        document.getElementById('cookie-accept-btn').addEventListener('click', acceptAll);
        document.getElementById('cookie-reject-btn').addEventListener('click', rejectAll);
        document.getElementById('cookie-settings-btn').addEventListener('click', openSettings);

        // Mostra il banner con animazione
        setTimeout(() => banner.classList.add('show'), 100);
    }

    // Crea il modal per le impostazioni
    function createSettingsModal() {
        const modal = document.createElement('div');
        modal.id = 'cookie-settings-modal';
        modal.className = 'cookie-modal';
        modal.innerHTML = `
            <div class="cookie-modal-overlay"></div>
            <div class="cookie-modal-content">
                <div class="cookie-modal-header">
                    <h2><i class="bi bi-shield-check"></i> Impostazioni Privacy</h2>
                    <button class="cookie-modal-close" aria-label="Chiudi">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>
                <div class="cookie-modal-body">
                    <p class="cookie-modal-intro">
                        Rispettiamo la tua privacy. Puoi scegliere quali cookie accettare.
                        I cookie necessari sono sempre attivi per garantire il funzionamento del sito.
                    </p>
                    
                    <div class="cookie-categories">
                        ${Object.entries(cookieCategories).map(([key, cat]) => `
                            <div class="cookie-category">
                                <div class="cookie-category-header">
                                    <div class="cookie-category-info">
                                        <h4>${cat.name}</h4>
                                        <p>${cat.description}</p>
                                    </div>
                                    <label class="cookie-switch">
                                        <input 
                                            type="checkbox" 
                                            id="cookie-${key}" 
                                            data-category="${key}"
                                            ${cat.required ? 'checked disabled' : ''}
                                            ${cat.enabled ? 'checked' : ''}
                                        >
                                        <span class="cookie-switch-slider"></span>
                                    </label>
                                </div>
                                ${cat.required ? '<div class="cookie-required-badge">Sempre attivo</div>' : ''}
                            </div>
                        `).join('')}
                    </div>

                    <div class="cookie-info-box">
                        <i class="bi bi-info-circle"></i>
                        <div>
                            Per maggiori informazioni consulta la nostra 
                            <a href="/cookie-policy" target="_blank">Cookie Policy</a> e 
                            <a href="/privacy-policy" target="_blank">Privacy Policy</a>.
                        </div>
                    </div>
                </div>
                <div class="cookie-modal-footer">
                    <button id="cookie-save-preferences" class="cookie-btn cookie-btn-primary">
                        <i class="bi bi-check2"></i> Salva Preferenze
                    </button>
                    <button id="cookie-accept-all-modal" class="cookie-btn cookie-btn-secondary">
                        Accetta Tutti
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        // Event listeners
        modal.querySelector('.cookie-modal-close').addEventListener('click', closeSettings);
        modal.querySelector('.cookie-modal-overlay').addEventListener('click', closeSettings);
        document.getElementById('cookie-save-preferences').addEventListener('click', savePreferences);
        document.getElementById('cookie-accept-all-modal').addEventListener('click', acceptAll);
    }

    // Funzioni di controllo
    function acceptAll() {
        const preferences = {
            necessary: true,
            analytics: true,
            marketing: true
        };
        saveConsent(preferences);
        hideBanner();
        closeSettings();
    }

    function rejectAll() {
        const preferences = {
            necessary: true,
            analytics: false,
            marketing: false
        };
        saveConsent(preferences);
        hideBanner();
    }

    function savePreferences() {
        const preferences = {
            necessary: true,
            analytics: document.getElementById('cookie-analytics')?.checked || false,
            marketing: document.getElementById('cookie-marketing')?.checked || false
        };
        saveConsent(preferences);
        closeSettings();
        hideBanner();
    }

    function openSettings() {
        let modal = document.getElementById('cookie-settings-modal');
        if (!modal) {
            createSettingsModal();
            modal = document.getElementById('cookie-settings-modal');
        }
        
        // Carica le preferenze correnti
        const consent = localStorage.getItem(COOKIE_CONSENT_KEY);
        if (consent) {
            try {
                const data = JSON.parse(consent);
                Object.entries(data.preferences).forEach(([key, value]) => {
                    const checkbox = document.getElementById(`cookie-${key}`);
                    if (checkbox && !checkbox.disabled) {
                        checkbox.checked = value;
                    }
                });
            } catch (e) {}
        }

        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
    }

    function closeSettings() {
        const modal = document.getElementById('cookie-settings-modal');
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        }
    }

    function hideBanner() {
        const banner = document.getElementById('cookie-banner');
        if (banner) {
            banner.classList.remove('show');
            setTimeout(() => banner.remove(), 300);
        }
    }

    // Funzione pubblica per riaprire le impostazioni (da chiamare da un link nel footer)
    window.openCookieSettings = openSettings;

    // Inizializzazione
    function init() {
        // Non mostrare il banner nelle pagine admin
        const isAdminPage = window.location.pathname.startsWith('/admin');
        if (isAdminPage) return;

        if (!hasConsent()) {
            // Mostra il banner se non c'è consenso
            createCookieBanner();
        } else {
            // Applica le preferenze salvate
            try {
                const data = JSON.parse(localStorage.getItem(COOKIE_CONSENT_KEY));
                applyConsent(data.preferences);
            } catch (e) {
                console.error('Errore nel caricamento preferenze cookie:', e);
            }
        }
    }

    // Avvia quando il DOM è pronto
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();
