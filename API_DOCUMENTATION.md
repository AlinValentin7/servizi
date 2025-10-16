# 📚 Documentazione API REST - Servizi Edili Elvis SRL

## 🔗 Base URL
```
http://localhost:8080/api
```

---

## 📦 **LAVORI / PORTFOLIO**

### 1. Lista tutti i lavori
```http
GET /api/lavori
```
**Risposta:**
```json
[
  {
    "id": 1,
    "titolo": "Ristrutturazione Villa",
    "descrizione": "Completa ristrutturazione...",
    "categoria": "Ristrutturazione",
    "dataInizio": "2024-01-15",
    "dataFine": "2024-03-20",
    "immagineUrl": "/uploads/abc123.jpg",
    "visibile": true
  }
]
```

### 2. Solo lavori visibili pubblicamente
```http
GET /api/lavori/visibili
```

### 3. Dettaglio singolo lavoro
```http
GET /api/lavori/{id}
```

### 4. Filtra per categoria
```http
GET /api/lavori/categoria/Ristrutturazione
```

### 5. Crea nuovo lavoro
```http
POST /api/lavori
Content-Type: application/json

{
  "titolo": "Ristrutturazione Villa",
  "descrizione": "Completa ristrutturazione...",
  "categoria": "Ristrutturazione",
  "dataInizio": "2024-01-15",
  "dataFine": "2024-03-20",
  "visibile": true
}
```

### 6. Aggiorna lavoro
```http
PUT /api/lavori/{id}
Content-Type: application/json

{
  "titolo": "Nuovo titolo",
  "descrizione": "Nuova descrizione"
}
```

### 7. Cambia visibilità
```http
PATCH /api/lavori/{id}/visibilita
Content-Type: application/json

{
  "visibile": true
}
```

### 8. Elimina lavoro
```http
DELETE /api/lavori/{id}
```

### 9. Statistiche lavori
```http
GET /api/lavori/stats
```
**Risposta:**
```json
{
  "totale": 15,
  "visibili": 12,
  "nascosti": 3,
  "perCategoria": {
    "Ristrutturazione": 8,
    "Muratura": 4,
    "Tinteggiatura": 3
  }
}
```

---

## 📅 **APPUNTAMENTI**

### 1. Lista tutti gli appuntamenti
```http
GET /api/appuntamenti
```

### 2. Dettaglio appuntamento
```http
GET /api/appuntamenti/{id}
```

### 3. Filtra per stato
```http
GET /api/appuntamenti/stato/CONFERMATO
```
**Stati possibili:** `IN_ATTESA`, `CONFERMATO`, `COMPLETATO`, `ANNULLATO`

### 4. Appuntamenti per data
```http
GET /api/appuntamenti/data/2025-01-15
```

### 5. Prossimi appuntamenti
```http
GET /api/appuntamenti/prossimi
```

### 6. Controlla disponibilità
```http
GET /api/appuntamenti/disponibilita?dataOra=2025-01-15T10:00:00
```
**Risposta:**
```json
{
  "disponibile": true
}
```

### 7. Crea nuovo appuntamento
```http
POST /api/appuntamenti
Content-Type: application/json

{
  "nomeCliente": "Mario Rossi",
  "email": "mario@example.com",
  "telefono": "+39 333 1234567",
  "dataOra": "2025-01-15T10:00:00",
  "tipoServizio": "Preventivo Ristrutturazione",
  "note": "Interessato a ristrutturazione bagno"
}
```

**Risposta successo (201 Created):**
```json
{
  "success": true,
  "message": "Appuntamento creato con successo",
  "appuntamento": { ... }
}
```

**Risposta errore (409 Conflict):**
```json
{
  "success": false,
  "message": "Fascia oraria non disponibile",
  "error": "FASCIA_ORARIA_NON_DISPONIBILE"
}
```

### 8. Cambia stato appuntamento
```http
PATCH /api/appuntamenti/{id}/stato
Content-Type: application/json

{
  "stato": "CONFERMATO"
}
```

### 9. Elimina appuntamento
```http
DELETE /api/appuntamenti/{id}
```

### 10. Statistiche appuntamenti
```http
GET /api/appuntamenti/stats
```
**Risposta:**
```json
{
  "totale": 50,
  "inAttesa": 5,
  "confermati": 10,
  "completati": 30,
  "annullati": 5
}
```

---

## 💬 **CONTATTI / MESSAGGI**

### 1. Lista tutti i contatti
```http
GET /api/contatti
```

### 2. Solo messaggi non letti
```http
GET /api/contatti/non-letti
```

### 3. Dettaglio contatto
```http
GET /api/contatti/{id}
```

### 4. Crea nuovo contatto (pubblico)
```http
POST /api/contatti
Content-Type: application/json

{
  "nome": "Mario Rossi",
  "email": "mario@example.com",
  "telefono": "+39 333 1234567",
  "messaggio": "Vorrei un preventivo per ristrutturazione bagno"
}
```

**Risposta (201 Created):**
```json
{
  "success": true,
  "message": "Messaggio inviato con successo! Ti risponderemo al più presto.",
  "contatto": { ... }
}
```

### 5. Segna come letto
```http
PATCH /api/contatti/{id}/letto
```

### 6. Rispondi via email
```http
POST /api/contatti/{id}/rispondi
Content-Type: application/json

{
  "risposta": "Grazie per averci contattato. Il preventivo è di €5000..."
}
```

### 7. Elimina contatto
```http
DELETE /api/contatti/{id}
```

### 8. Statistiche contatti
```http
GET /api/contatti/stats
```
**Risposta:**
```json
{
  "totale": 100,
  "nonLetti": 5,
  "letti": 95,
  "percentualeLetti": "95.0%"
}
```

---

## 📊 **STATISTICHE E DASHBOARD**

### 1. Dashboard completa
```http
GET /api/stats/dashboard
```
**Risposta:**
```json
{
  "appuntamentiTotali": 50,
  "appuntamentiInAttesa": 5,
  "appuntamentiConfermati": 10,
  "appuntamentiCompletati": 30,
  "contattiTotali": 100,
  "contattiNonLetti": 5,
  "lavoriTotali": 15,
  "lavoriVisibili": 12,
  "appuntamentiQuestaSettimana": 8,
  "appuntamentiQuestoMese": 25
}
```

### 2. Statistiche appuntamenti
```http
GET /api/stats/appuntamenti
```

### 3. Statistiche contatti
```http
GET /api/stats/contatti
```

### 4. Statistiche lavori
```http
GET /api/stats/lavori
```

### 5. Statistiche mese corrente
```http
GET /api/stats/mese
```

### 6. Riepilogo sintetico
```http
GET /api/stats/riepilogo
```
**Risposta:**
```json
{
  "appuntamentiInAttesa": 5,
  "contattiNonLetti": 3,
  "appuntamentiQuestaMese": 25
}
```

### 7. Health Check
```http
GET /api/stats/health
```
**Risposta:**
```json
{
  "status": "UP",
  "application": "Servizi Edili Elvis SRL",
  "version": "1.0.0",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## 🔐 **Autenticazione**

⚠️ **IMPORTANTE:** Attualmente le API sono aperte (CORS = *).  
Per produzione, implementare:
- Spring Security con JWT
- Rate limiting
- CORS restrittivo
- API Keys per client esterni

---

## 🧪 **Testing con cURL**

### Esempio: Crea appuntamento
```bash
curl -X POST http://localhost:8080/api/appuntamenti \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCliente": "Mario Rossi",
    "email": "mario@example.com",
    "telefono": "+39 333 1234567",
    "dataOra": "2025-01-15T10:00:00",
    "tipoServizio": "Preventivo",
    "note": "Ristrutturazione bagno"
  }'
```

### Esempio: Lista lavori visibili
```bash
curl http://localhost:8080/api/lavori/visibili
```

### Esempio: Controlla disponibilità
```bash
curl "http://localhost:8080/api/appuntamenti/disponibilita?dataOra=2025-01-15T10:00:00"
```

---

## 📱 **Integrazione JavaScript**

### Fetch API - Crea contatto
```javascript
async function inviaContatto(dati) {
  const response = await fetch('http://localhost:8080/api/contatti', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(dati)
  });
  
  const result = await response.json();
  
  if (result.success) {
    console.log('✅ Messaggio inviato!');
  } else {
    console.error('❌ Errore:', result.message);
  }
}

// Uso
inviaContatto({
  nome: 'Mario Rossi',
  email: 'mario@example.com',
  telefono: '+39 333 1234567',
  messaggio: 'Vorrei un preventivo'
});
```

### Axios - Prenota appuntamento
```javascript
import axios from 'axios';

async function prenotaAppuntamento(dati) {
  try {
    const response = await axios.post(
      'http://localhost:8080/api/appuntamenti',
      dati
    );
    
    console.log('✅ Appuntamento creato:', response.data.appuntamento);
  } catch (error) {
    if (error.response?.status === 409) {
      console.error('❌ Orario non disponibile');
    }
  }
}
```

---

## 🚀 **Prossimi Sviluppi**

- [ ] Autenticazione JWT
- [ ] Rate limiting (max 100 req/min)
- [ ] Paginazione risultati
- [ ] Filtri avanzati (date range, ricerca testo)
- [ ] WebSocket per notifiche real-time
- [ ] Upload immagini via API multipart
- [ ] Esportazione dati (CSV, PDF)
- [ ] Webhooks per eventi

---

## 📝 **Note**

- Tutte le date sono in formato ISO 8601
- Timezone: Europe/Rome
- Encoding: UTF-8
- Le risposte includono sempre HTTP status code appropriati
- Gli errori restituiscono JSON con campo "error" descrittivo

---

**Firmato $₿420 | 2025**
