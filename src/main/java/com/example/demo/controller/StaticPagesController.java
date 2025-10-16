package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller per le pagine statiche e informative
 * (Cookie Policy, Privacy Policy, Termini e Condizioni, ecc.)
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Controller
public class StaticPagesController {

    /**
     * Pagina Cookie Policy
     * URL: /cookie-policy
     */
    @GetMapping("/cookie-policy")
    public String cookiePolicy() {
        return "cookie-policy";
    }

    /**
     * Pagina Privacy Policy
     * URL: /privacy-policy
     */
    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    /**
     * Pagina Termini e Condizioni (opzionale)
     * URL: /termini-condizioni
     */
    @GetMapping("/termini-condizioni")
    public String terminiCondizioni() {
        return "termini-condizioni";
    }
}
