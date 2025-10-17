# 🌍 Feature: Selezione Paese e Città di Provenienza

## ✅ Modifiche Implementate

### 1. **Model Candidatura** (`Candidatura.java`)
Aggiunti due nuovi campi obbligatori:
- `paese` - Paese di provenienza del candidato
- `cittaProvenienza` - Città di origine nel paese selezionato

Entrambi i campi sono validati e obbligatori.

---

### 2. **JavaScript Dinamico** (`paese-citta.js`)
Creato un nuovo file JavaScript con:
- **Database di 20+ paesi** con bandiere emoji (🇮🇹🇷🇴🇦🇱🇧🇩🇲🇦🇸🇳🇵🇰🇮🇳🇺🇦🇹🇳🇲🇩🇪🇬🇳🇬🇵🇭🇬🇭🇷🇸🇵🇱🇧🇬🇨🇳🇱🇰)
- **Liste di città** per ogni paese (es: Italia → Roma, Milano, Napoli...)
- **Selezione dinamica**: quando selezioni un paese, appare automaticamente la lista delle città disponibili
- **Validazione automatica**: il campo città diventa obbligatorio solo dopo aver selezionato un paese

#### Paesi Supportati:
- 🇮🇹 **Italia** (30 città)
- 🇷🇴 **Romania** (25 città)
- 🇦🇱 **Albania** (17 città)
- 🇧🇩 **Bangladesh** (15 città)
- 🇲🇦 **Marocco** (17 città)
- 🇸🇳 **Senegal** (14 città)
- 🇵🇰 **Pakistan** (14 città)
- 🇮🇳 **India** (25 città)
- 🇺🇦 **Ucraina** (18 città)
- 🇹🇳 **Tunisia** (14 città)
- 🇲🇩 **Moldavia** (10 città)
- 🇪🇬 **Egitto** (16 città)
- 🇳🇬 **Nigeria** (16 città)
- 🇵🇭 **Filippine** (15 città)
- 🇬🇭 **Ghana** (12 città)
- 🇷🇸 **Serbia** (12 città)
- 🇵🇱 **Polonia** (19 città)
- 🇧🇬 **Bulgaria** (16 città)
- 🇨🇳 **Cina** (18 città)
- 🇱🇰 **Sri Lanka** (12 città)

---

### 3. **Form Candidatura** (`lavora-con-noi.html`)
Aggiunta una nuova sezione "Provenienza" nei **Dati Anagrafici**:
```html
- 🏴 Campo "Paese di Provenienza" con icone bandiere
- 🏙️ Campo "Città di Provenienza" (si popola dinamicamente)
```

**Funzionamento:**
1. L'utente seleziona il paese dal menu a tendina (con bandiera)
2. Appare automaticamente il secondo campo con le città di quel paese
3. L'utente seleziona la città di provenienza
4. I dati vengono salvati nel database

---

### 4. **Pannello Admin**

#### **Lista Candidature** (`candidature.html`)
Aggiunto nella tabella:
- Visualizzazione del paese di provenienza con icona bandiera
- Città di origine tra parentesi

#### **Dettaglio Candidatura** (`dettaglio-candidatura.html`)
Aggiunta sezione nei "Dati Anagrafici":
```
Paese di Provenienza: [Nome Paese]
Città di Provenienza: [Nome Città]
```

---

## 🎨 Design e UX

### Caratteristiche:
- ✅ **Bandiere emoji** per identificazione visiva immediata
- ✅ **Transizione animata** quando appare il campo città
- ✅ **Validazione dinamica** - città obbligatoria solo se paese selezionato
- ✅ **Ordinamento alfabetico** dei paesi per facile ricerca
- ✅ **Design responsive** - funziona perfettamente su mobile
- ✅ **Focus styling** personalizzato con colore verde aziendale

---

## 🔧 Prossimi Passi

### Per avviare l'applicazione:
```bash
cd c:\create\servizi
java -jar target\servizi-0.0.1-SNAPSHOT.jar
```

### Per testare la feature:
1. Vai su: `http://localhost:8080/lavora-con-noi`
2. Compila il form fino alla sezione "Paese di Provenienza"
3. Seleziona un paese (vedrai la bandiera)
4. Il campo città apparirà automaticamente
5. Invia la candidatura
6. Verifica nell'admin che i dati siano salvati correttamente

---

## 📊 Database

I nuovi campi sono stati aggiunti alla tabella `candidature`:
- `paese` VARCHAR(100) NOT NULL
- `citta_provenienza` VARCHAR(100) NOT NULL

**Nota:** Se hai candidature esistenti nel database, potrebbe essere necessario aggiungere valori di default o eseguire una migrazione.

---

## 🌟 Vantaggi per il Business

1. **Raccolta dati demografici** - sapere la provenienza dei candidati
2. **Statistiche migliori** - analisi per paese/nazionalità
3. **Esperienza utente migliorata** - form più professionale
4. **Validazione dei dati** - città coerenti con il paese selezionato
5. **Identificazione visiva** - bandiere per riconoscimento immediato

---

## 📝 Note Tecniche

- Il file JavaScript è completamente standalone
- Nessuna dipendenza esterna (niente API di terze parti)
- Database paesi/città gestito localmente
- Facile aggiungere nuovi paesi/città modificando `paese-citta.js`

---

**Firmato $₿420 - 2025**
