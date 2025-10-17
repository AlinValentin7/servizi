/**
 * Sistema di selezione Paese-Città con bandiere
 * @author Firmato $₿420
 */

// Database paesi con città principali e codici bandiera (emoji flag)
const paesiDatabase = {
    "Italia": {
        flag: "🇮🇹",
        citta: ["Roma", "Milano", "Napoli", "Torino", "Palermo", "Genova", "Bologna", "Firenze", "Bari", "Catania", "Venezia", "Verona", "Messina", "Padova", "Trieste", "Brescia", "Parma", "Prato", "Modena", "Reggio Calabria", "Reggio Emilia", "Perugia", "Livorno", "Ravenna", "Cagliari", "Foggia", "Rimini", "Salerno", "Ferrara", "Sassari"]
    },
    "Romania": {
        flag: "🇷🇴",
        citta: ["Bucuresti", "Cluj-Napoca", "Timișoara", "Iași", "Constanța", "Craiova", "Brașov", "Galați", "Ploiești", "Oradea", "Brăila", "Arad", "Pitești", "Sibiu", "Bacău", "Târgu Mureș", "Baia Mare", "Buzău", "Botoșani", "Satu Mare", "Râmnicu Vâlcea", "Drobeta-Turnu Severin", "Suceava", "Piatra Neamț", "Târgu Jiu"]
    },
    "Albania": {
        flag: "🇦🇱",
        citta: ["Tirana", "Durazzo", "Valona", "Scutari", "Elbasan", "Fier", "Coriza", "Saranda", "Berat", "Lushnje", "Kavaja", "Pogradec", "Shkodër", "Gjirokastër", "Kukës", "Laç", "Krujë"]
    },
    "Bangladesh": {
        flag: "🇧🇩",
        citta: ["Dhaka", "Chittagong", "Khulna", "Rajshahi", "Sylhet", "Barisal", "Rangpur", "Comilla", "Narayanganj", "Gazipur", "Mymensingh", "Jessore", "Bogra", "Dinajpur", "Cox's Bazar"]
    },
    "Marocco": {
        flag: "🇲🇦",
        citta: ["Casablanca", "Rabat", "Fès", "Marrakech", "Agadir", "Tangeri", "Meknès", "Oujda", "Kenitra", "Tétouan", "Safi", "Temara", "Mohammedia", "Khouribga", "El Jadida", "Beni Mellal", "Nador"]
    },
    "Senegal": {
        flag: "🇸🇳",
        citta: ["Dakar", "Touba", "Thiès", "Kaolack", "Saint-Louis", "Ziguinchor", "Mbour", "Rufisque", "Diourbel", "Louga", "Tambacounda", "Kolda", "Richard-Toll", "Sédhiou"]
    },
    "Pakistan": {
        flag: "🇵🇰",
        citta: ["Karachi", "Lahore", "Faisalabad", "Rawalpindi", "Multan", "Gujranwala", "Hyderabad", "Peshawar", "Islamabad", "Quetta", "Bahawalpur", "Sargodha", "Sialkot", "Sukkur"]
    },
    "India": {
        flag: "🇮🇳",
        citta: ["Mumbai", "Delhi", "Bangalore", "Hyderabad", "Ahmedabad", "Chennai", "Kolkata", "Surat", "Pune", "Jaipur", "Lucknow", "Kanpur", "Nagpur", "Indore", "Thane", "Bhopal", "Visakhapatnam", "Pimpri-Chinchwad", "Patna", "Vadodara", "Ghaziabad", "Ludhiana", "Agra", "Nashik", "Faridabad"]
    },
    "Ucraina": {
        flag: "🇺🇦",
        citta: ["Kyiv", "Kharkiv", "Odessa", "Dnipro", "Donetsk", "Zaporizhzhia", "Lviv", "Kryvyi Rih", "Mykolaiv", "Mariupol", "Luhansk", "Vinnytsia", "Makiivka", "Simferopol", "Sevastopol", "Kherson", "Poltava", "Chernihiv"]
    },
    "Tunisia": {
        flag: "🇹🇳",
        citta: ["Tunisi", "Sfax", "Sousse", "Kairouan", "Bizerte", "Gabès", "Ariana", "La Goulette", "Monastir", "Nabeul", "Ben Arous", "Kasserine", "Mahdia", "Médenine"]
    },
    "Moldavia": {
        flag: "🇲🇩",
        citta: ["Chișinău", "Tiraspol", "Bălți", "Bender", "Rîbnița", "Cahul", "Ungheni", "Soroca", "Orhei", "Comrat"]
    },
    "Egitto": {
        flag: "🇪🇬",
        citta: ["Il Cairo", "Alessandria", "Giza", "Shubra El-Kheima", "Port Said", "Suez", "Luxor", "al-Mansura", "El-Mahalla El-Kubra", "Tanta", "Asyut", "Ismailia", "Fayyum", "Zagazig", "Aswan", "Damietta"]
    },
    "Nigeria": {
        flag: "🇳🇬",
        citta: ["Lagos", "Kano", "Ibadan", "Abuja", "Port Harcourt", "Benin City", "Kaduna", "Maiduguri", "Zaria", "Aba", "Jos", "Ilorin", "Oyo", "Enugu", "Abeokuta", "Onitsha"]
    },
    "Filippine": {
        flag: "🇵🇭",
        citta: ["Manila", "Quezon City", "Davao", "Caloocan", "Cebu City", "Zamboanga", "Taguig", "Antipolo", "Pasig", "Cagayan de Oro", "Parañaque", "Valenzuela", "Dasmariñas", "Las Piñas", "Makati"]
    },
    "Ghana": {
        flag: "🇬🇭",
        citta: ["Accra", "Kumasi", "Tamale", "Sekondi-Takoradi", "Ashaiman", "Sunyani", "Cape Coast", "Obuasi", "Teshie", "Tema", "Madina", "Koforidua"]
    },
    "Serbia": {
        flag: "🇷🇸",
        citta: ["Belgrado", "Novi Sad", "Niš", "Kragujevac", "Subotica", "Zrenjanin", "Pančevo", "Čačak", "Kruševac", "Kraljevo", "Smederevo", "Leskovac"]
    },
    "Polonia": {
        flag: "🇵🇱",
        citta: ["Varsavia", "Cracovia", "Łódź", "Breslavia", "Poznań", "Danzica", "Stettino", "Bydgoszcz", "Lublino", "Katowice", "Białystok", "Gdynia", "Częstochowa", "Radom", "Sosnowiec", "Toruń", "Kielce", "Gliwice", "Zabrze"]
    },
    "Bulgaria": {
        flag: "🇧🇬",
        citta: ["Sofia", "Plovdiv", "Varna", "Burgas", "Ruse", "Stara Zagora", "Pleven", "Sliven", "Dobrich", "Shumen", "Pernik", "Haskovo", "Yambol", "Pazardzhik", "Blagoevgrad", "Veliko Tarnovo"]
    },
    "Cina": {
        flag: "🇨🇳",
        citta: ["Pechino", "Shanghai", "Guangzhou", "Shenzhen", "Chengdu", "Chongqing", "Tianjin", "Wuhan", "Xi'an", "Hangzhou", "Nanjing", "Shenyang", "Harbin", "Zhengzhou", "Changchun", "Dalian", "Qingdao", "Jinan"]
    },
    "Sri Lanka": {
        flag: "🇱🇰",
        citta: ["Colombo", "Dehiwala-Mount Lavinia", "Moratuwa", "Jaffna", "Negombo", "Pita Kotte", "Kandy", "Kalmunai", "Trincomalee", "Galle", "Batticaloa", "Matara"]
    }
};

// Funzione per inizializzare il sistema
function initPaeseCittaSelector() {
    const paeseSelect = document.getElementById('paeseSelect');
    const cittaProvenienzaSelect = document.getElementById('cittaProvenienzaSelect');
    const cittaProvenienzaContainer = document.getElementById('cittaProvenienzaContainer');

    if (!paeseSelect || !cittaProvenienzaSelect) {
        console.error('Elementi paese/città non trovati');
        return;
    }

    // Popola il select dei paesi (ordinati alfabeticamente)
    const paesiOrdinati = Object.keys(paesiDatabase).sort();
    
    paesiOrdinati.forEach(paese => {
        const option = document.createElement('option');
        option.value = paese;
        option.textContent = `${paesiDatabase[paese].flag} ${paese}`;
        paeseSelect.appendChild(option);
    });

    // Nascondi inizialmente il campo città di provenienza
    cittaProvenienzaContainer.style.display = 'none';

    // Event listener per il cambio paese
    paeseSelect.addEventListener('change', function() {
        const paeseSelezionato = this.value;
        
        // Reset città provenienza
        cittaProvenienzaSelect.innerHTML = '<option value="">-- Seleziona città --</option>';
        
        if (paeseSelezionato && paesiDatabase[paeseSelezionato]) {
            // Mostra il campo città
            cittaProvenienzaContainer.style.display = 'block';
            
            // Popola le città
            const citta = paesiDatabase[paeseSelezionato].citta;
            citta.forEach(city => {
                const option = document.createElement('option');
                option.value = city;
                option.textContent = city;
                cittaProvenienzaSelect.appendChild(option);
            });
            
            // Rendi obbligatorio il campo città
            cittaProvenienzaSelect.required = true;
        } else {
            // Nascondi il campo se nessun paese selezionato
            cittaProvenienzaContainer.style.display = 'none';
            cittaProvenienzaSelect.required = false;
        }
    });

    // Se esiste un valore precompilato (in caso di errori di validazione)
    if (paeseSelect.value) {
        paeseSelect.dispatchEvent(new Event('change'));
    }
}

// Inizializza quando il DOM è pronto
document.addEventListener('DOMContentLoaded', initPaeseCittaSelector);
