/**
 * Generatore automatico Codice Fiscale Italiano
 * Calcola il CF dai dati anagrafici secondo l'algoritmo ufficiale
 * 
 * @author Firmato $₿420
 * @since 2025
 */

(function() {
    'use strict';

    // Tabelle per il calcolo del codice fiscale
    const MESI = {
        '01': 'A', '02': 'B', '03': 'C', '04': 'D',
        '05': 'E', '06': 'H', '07': 'L', '08': 'M',
        '09': 'P', '10': 'R', '11': 'S', '12': 'T'
    };

    const VOCALI = ['A', 'E', 'I', 'O', 'U'];
    
    const CARATTERI_PARI = {
        '0': 0, '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9,
        'A': 0, 'B': 1, 'C': 2, 'D': 3, 'E': 4, 'F': 5, 'G': 6, 'H': 7, 'I': 8, 'J': 9,
        'K': 10, 'L': 11, 'M': 12, 'N': 13, 'O': 14, 'P': 15, 'Q': 16, 'R': 17, 'S': 18,
        'T': 19, 'U': 20, 'V': 21, 'W': 22, 'X': 23, 'Y': 24, 'Z': 25
    };

    const CARATTERI_DISPARI = {
        '0': 1, '1': 0, '2': 5, '3': 7, '4': 9, '5': 13, '6': 15, '7': 17, '8': 19, '9': 21,
        'A': 1, 'B': 0, 'C': 5, 'D': 7, 'E': 9, 'F': 13, 'G': 15, 'H': 17, 'I': 19, 'J': 21,
        'K': 2, 'L': 4, 'M': 18, 'N': 20, 'O': 11, 'P': 3, 'Q': 6, 'R': 8, 'S': 12, 'T': 14,
        'U': 16, 'V': 10, 'W': 22, 'X': 25, 'Y': 24, 'Z': 23
    };

    const CARATTERI_CONTROLLO = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

    // Codici catastali comuni (solo alcuni esempi, andrà integrato con API)
    const COMUNI_ESEMPIO = {
        'ROMA': 'H501',
        'MILANO': 'F205',
        'NAPOLI': 'F839',
        'TORINO': 'L219',
        'PALERMO': 'G273',
        'GENOVA': 'D969',
        'BOLOGNA': 'A944',
        'FIRENZE': 'D612',
        'BARI': 'A662',
        'CATANIA': 'C351',
        'VENEZIA': 'L736',
        'VERONA': 'L781'
    };

    /**
     * Estrae consonanti da una stringa
     */
    function estraiConsonanti(str) {
        return str.toUpperCase().split('').filter(c => 
            /[BCDFGHJKLMNPQRSTVWXYZ]/.test(c)
        );
    }

    /**
     * Estrae vocali da una stringa
     */
    function estraiVocali(str) {
        return str.toUpperCase().split('').filter(c => 
            VOCALI.includes(c)
        );
    }

    /**
     * Calcola il codice del cognome
     */
    function calcolaCognome(cognome) {
        if (!cognome) return 'XXX';
        
        cognome = cognome.toUpperCase().replace(/[^A-Z]/g, '');
        const consonanti = estraiConsonanti(cognome);
        const vocali = estraiVocali(cognome);
        
        let codice = '';
        
        // Aggiungi consonanti
        for (let i = 0; i < consonanti.length && codice.length < 3; i++) {
            codice += consonanti[i];
        }
        
        // Aggiungi vocali se necessario
        for (let i = 0; i < vocali.length && codice.length < 3; i++) {
            codice += vocali[i];
        }
        
        // Aggiungi X se necessario
        while (codice.length < 3) {
            codice += 'X';
        }
        
        return codice.substring(0, 3);
    }

    /**
     * Calcola il codice del nome
     */
    function calcolaNome(nome) {
        if (!nome) return 'XXX';
        
        nome = nome.toUpperCase().replace(/[^A-Z]/g, '');
        const consonanti = estraiConsonanti(nome);
        const vocali = estraiVocali(nome);
        
        let codice = '';
        
        // Se ci sono almeno 4 consonanti, prendi la 1a, 3a e 4a
        if (consonanti.length >= 4) {
            codice = consonanti[0] + consonanti[2] + consonanti[3];
        } else {
            // Altrimenti prendi le prime 3 consonanti
            for (let i = 0; i < consonanti.length && codice.length < 3; i++) {
                codice += consonanti[i];
            }
            
            // Aggiungi vocali se necessario
            for (let i = 0; i < vocali.length && codice.length < 3; i++) {
                codice += vocali[i];
            }
            
            // Aggiungi X se necessario
            while (codice.length < 3) {
                codice += 'X';
            }
        }
        
        return codice.substring(0, 3);
    }

    /**
     * Calcola il codice della data di nascita e sesso
     */
    function calcolaDataSesso(dataNascita, sesso) {
        if (!dataNascita) return 'XXXXX';
        
        const data = new Date(dataNascita);
        const anno = data.getFullYear().toString().substring(2);
        const mese = MESI[('0' + (data.getMonth() + 1)).slice(-2)];
        let giorno = data.getDate();
        
        // Per le donne si aggiunge 40 al giorno
        if (sesso === 'F' || sesso === 'f') {
            giorno += 40;
        }
        
        const giornoStr = ('0' + giorno).slice(-2);
        
        return anno + mese + giornoStr;
    }

    /**
     * Cerca il codice catastale del comune
     */
    function cercaCodiceCatastale(luogoNascita) {
        if (!luogoNascita) return 'X000';
        
        const luogo = luogoNascita.toUpperCase().trim();
        
        // Controlla se è nei comuni di esempio
        if (COMUNI_ESEMPIO[luogo]) {
            return COMUNI_ESEMPIO[luogo];
        }
        
        // Se non trovato, genera un codice placeholder
        // In produzione andrebbe usata un'API per i codici catastali
        return 'Z000';
    }

    /**
     * Calcola il carattere di controllo
     */
    function calcolaCarattereControllo(codiceParziale) {
        let somma = 0;
        
        for (let i = 0; i < codiceParziale.length; i++) {
            const carattere = codiceParziale[i];
            if (i % 2 === 0) {
                // Posizione dispari (1, 3, 5, ...)
                somma += CARATTERI_DISPARI[carattere];
            } else {
                // Posizione pari (2, 4, 6, ...)
                somma += CARATTERI_PARI[carattere];
            }
        }
        
        const resto = somma % 26;
        return CARATTERI_CONTROLLO[resto];
    }

    /**
     * Genera il codice fiscale completo
     */
    function generaCodiceFiscale(dati) {
        const { nome, cognome, dataNascita, luogoNascita, sesso } = dati;
        
        // Valida i dati obbligatori
        if (!nome || !cognome || !dataNascita) {
            return null;
        }
        
        const codiceCognome = calcolaCognome(cognome);
        const codiceNome = calcolaNome(nome);
        const codiceDataSesso = calcolaDataSesso(dataNascita, sesso);
        const codiceCatastale = cercaCodiceCatastale(luogoNascita);
        
        const codiceParziale = codiceCognome + codiceNome + codiceDataSesso + codiceCatastale;
        const carattereControllo = calcolaCarattereControllo(codiceParziale);
        
        return codiceParziale + carattereControllo;
    }

    /**
     * Rileva il sesso dalla data di nascita e giorno
     */
    function rilevaSessoDaData(dataNascita, codiceFiscale) {
        if (!codiceFiscale || codiceFiscale.length < 11) return null;
        
        const giornoStr = codiceFiscale.substring(9, 11);
        const giorno = parseInt(giornoStr);
        
        return giorno > 40 ? 'F' : 'M';
    }

    /**
     * Valida un codice fiscale
     */
    function validaCodiceFiscale(codiceFiscale) {
        if (!codiceFiscale || codiceFiscale.length !== 16) {
            return false;
        }
        
        const cf = codiceFiscale.toUpperCase();
        const pattern = /^[A-Z]{6}\d{2}[A-Z]\d{2}[A-Z]\d{3}[A-Z]$/;
        
        if (!pattern.test(cf)) {
            return false;
        }
        
        // Verifica carattere di controllo
        const codiceParziale = cf.substring(0, 15);
        const carattereControlloCalcolato = calcolaCarattereControllo(codiceParziale);
        const carattereControlloEffettivo = cf.charAt(15);
        
        return carattereControlloCalcolato === carattereControlloEffettivo;
    }

    // Esporta le funzioni globalmente
    window.CodiceFiscaleGenerator = {
        genera: generaCodiceFiscale,
        valida: validaCodiceFiscale,
        comuni: COMUNI_ESEMPIO
    };

    // Auto-inizializzazione se siamo nella pagina candidatura
    if (window.location.pathname.includes('lavora-con-noi')) {
        document.addEventListener('DOMContentLoaded', inizializzaGeneratoreCF);
    }

    /**
     * Inizializza il generatore nella pagina
     */
    function inizializzaGeneratoreCF() {
        const form = document.querySelector('form');
        if (!form) return;

        const nomeInput = document.querySelector('input[name="nome"]');
        const cognomeInput = document.querySelector('input[name="cognome"]');
        const dataNascitaInput = document.querySelector('input[name="dataNascita"]');
        const luogoNascitaInput = document.querySelector('input[name="luogoNascita"]');
        const sessoSelect = document.querySelector('#sessoSelect');
        const codiceFiscaleInput = document.querySelector('input[name="codiceFiscale"]');

        if (!nomeInput || !cognomeInput || !dataNascitaInput || !codiceFiscaleInput) {
            return;
        }

        // Crea il pulsante per generare il CF
        const btnGenera = document.createElement('button');
        btnGenera.type = 'button';
        btnGenera.className = 'btn btn-secondary btn-sm mt-2';
        btnGenera.innerHTML = '<i class="bi bi-magic me-2"></i>Genera Automaticamente';
        btnGenera.style.width = '100%';

        // Inserisci il pulsante dopo il campo CF
        codiceFiscaleInput.parentElement.appendChild(btnGenera);

        // Aggiungi tooltip informativo
        const tooltip = document.createElement('div');
        tooltip.className = 'alert alert-info mt-2';
        tooltip.style.fontSize = '0.85rem';
        tooltip.innerHTML = '<i class="bi bi-info-circle me-2"></i><strong>Tip:</strong> Compila Nome, Cognome, Data di Nascita e Luogo di Nascita, poi clicca "Genera Automaticamente"';
        codiceFiscaleInput.parentElement.appendChild(tooltip);

        // Evento click sul pulsante
        btnGenera.addEventListener('click', function() {
            const nome = nomeInput.value.trim();
            const cognome = cognomeInput.value.trim();
            const dataNascita = dataNascitaInput.value;
            const luogoNascita = luogoNascitaInput ? luogoNascitaInput.value.trim() : 'ROMA';
            const sesso = sessoSelect ? sessoSelect.value : 'M';

            // Valida i dati
            if (!nome) {
                alert('Inserisci il nome');
                nomeInput.focus();
                return;
            }
            if (!cognome) {
                alert('Inserisci il cognome');
                cognomeInput.focus();
                return;
            }
            if (!dataNascita) {
                alert('Inserisci la data di nascita');
                dataNascitaInput.focus();
                return;
            }
            if (!sesso || sesso === '') {
                alert('Seleziona il sesso');
                if (sessoSelect) sessoSelect.focus();
                return;
            }

            // Genera il codice fiscale
            const cf = generaCodiceFiscale({
                nome: nome,
                cognome: cognome,
                dataNascita: dataNascita,
                luogoNascita: luogoNascita,
                sesso: sesso
            });

            if (cf) {
                codiceFiscaleInput.value = cf;
                
                // Mostra messaggio di successo
                const successMsg = document.createElement('div');
                successMsg.className = 'alert alert-success mt-2';
                successMsg.innerHTML = '<i class="bi bi-check-circle me-2"></i>Codice Fiscale generato! Verifica che sia corretto.';
                codiceFiscaleInput.parentElement.appendChild(successMsg);
                
                // Rimuovi il messaggio dopo 3 secondi
                setTimeout(() => {
                    successMsg.remove();
                }, 3000);

                // Nascondi il tooltip
                tooltip.style.display = 'none';
            } else {
                alert('Errore nella generazione del codice fiscale. Verifica i dati inseriti.');
            }
        });

        // Auto-genera quando tutti i campi sono compilati
        const autoGenera = () => {
            if (nomeInput.value && cognomeInput.value && dataNascitaInput.value && 
                luogoNascitaInput && luogoNascitaInput.value && !codiceFiscaleInput.value) {
                
                // Mostra suggerimento
                btnGenera.classList.add('btn-warning');
                btnGenera.innerHTML = '<i class="bi bi-magic me-2"></i>Clicca per Generare CF';
            }
        };

        [nomeInput, cognomeInput, dataNascitaInput].forEach(input => {
            input.addEventListener('blur', autoGenera);
        });

        if (luogoNascitaInput) {
            luogoNascitaInput.addEventListener('blur', autoGenera);
        }
    }

})();
