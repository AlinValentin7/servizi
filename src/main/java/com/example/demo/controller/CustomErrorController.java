package com.example.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller personalizzato per gestire errori HTTP con messaggi tematici
 * sul mondo dell'edilizia e dei cantieri.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404:
                    model.addAttribute("errorCode", "404");
                    model.addAttribute("errorTitle", "Pagina in Costruzione");
                    model.addAttribute("errorMessage", "Questa pagina non è stata ancora costruita!");
                    model.addAttribute("errorDetails", "Il cantiere è ancora aperto. Torna alla home page e scegli un'altra destinazione.");
                    model.addAttribute("errorGif", "https://media.giphy.com/media/3o7btPCcdNniyf0ArS/giphy.gif");
                    model.addAttribute("errorEmoji", "🏗️");
                    break;
                    
                case 500:
                    model.addAttribute("errorCode", "500");
                    model.addAttribute("errorTitle", "Crollo Strutturale");
                    model.addAttribute("errorMessage", "Il server ha avuto un cedimento!");
                    model.addAttribute("errorDetails", "I nostri muratori digitali stanno lavorando per rinforzare le fondamenta. Riprova tra qualche minuto.");
                    model.addAttribute("errorGif", "https://media.giphy.com/media/l2JdTkHW1KZPdvPQA/giphy.gif");
                    model.addAttribute("errorEmoji", "🧱");
                    break;
                    
                case 403:
                    model.addAttribute("errorCode", "403");
                    model.addAttribute("errorTitle", "Area Riservata");
                    model.addAttribute("errorMessage", "Zona Cantiere - Accesso Vietato!");
                    model.addAttribute("errorDetails", "Quest'area è riservata solo ai lavoratori autorizzati. Serve casco e badge per entrare!");
                    model.addAttribute("errorGif", "https://media.giphy.com/media/3oriO0OEd9QIDdllqo/giphy.gif");
                    model.addAttribute("errorEmoji", "⛔");
                    break;
                    
                case 400:
                    model.addAttribute("errorCode", "400");
                    model.addAttribute("errorTitle", "Misure Sbagliate");
                    model.addAttribute("errorMessage", "I dati forniti non sono corretti!");
                    model.addAttribute("errorDetails", "Controlla i parametri inviati. Un muratore non costruisce con misure sbagliate!");
                    model.addAttribute("errorGif", "https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif");
                    model.addAttribute("errorEmoji", "📏");
                    break;
                    
                case 503:
                    model.addAttribute("errorCode", "503");
                    model.addAttribute("errorTitle", "Pausa Cantiere");
                    model.addAttribute("errorMessage", "Servizio temporaneamente non disponibile");
                    model.addAttribute("errorDetails", "Il cantiere è in pausa pranzo. Torna tra qualche minuto!");
                    model.addAttribute("errorGif", "https://media.giphy.com/media/l0HlBO7eyXzSZkJri/giphy.gif");
                    model.addAttribute("errorEmoji", "☕");
                    break;
                    
                default:
                    model.addAttribute("errorCode", statusCode);
                    model.addAttribute("errorTitle", "Imprevisto in Cantiere");
                    model.addAttribute("errorMessage", "Si è verificato un problema tecnico");
                    model.addAttribute("errorDetails", "Qualcosa è andato storto. I nostri tecnici sono al lavoro per risolvere.");
                    model.addAttribute("errorGif", "https://media.giphy.com/media/xTiTnGeUsWOEwsGoG4/giphy.gif");
                    model.addAttribute("errorEmoji", "🔧");
            }
        } else {
            model.addAttribute("errorCode", "ERROR");
            model.addAttribute("errorTitle", "Errore Sconosciuto");
            model.addAttribute("errorMessage", "Si è verificato un errore imprevisto");
            model.addAttribute("errorDetails", "Il nostro team è al lavoro per risolvere il problema.");
            model.addAttribute("errorGif", "https://media.giphy.com/media/xTiTnGeUsWOEwsGoG4/giphy.gif");
            model.addAttribute("errorEmoji", "🔧");
        }
        
        return "error";
    }
}
