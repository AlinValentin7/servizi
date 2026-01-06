/**
 * ANIMAZIONI UNIVERSALI - Auto-attivazione su tutte le pagine
 * Questo script aggiunge automaticamente animazioni avanzate a tutti gli elementi
 */

document.addEventListener('DOMContentLoaded', function() {
    
    // ============================================
    // 1. PROGRESS BAR per tutti i FORM
    // ============================================
    const forms = document.querySelectorAll('form:not([data-no-progress])');
    
    forms.forEach(form => {
        // Crea progress bar se non esiste
        let progressBar = document.getElementById('form-progress-bar');
        if (!progressBar) {
            const container = document.createElement('div');
            container.className = 'progress-custom';
            container.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; z-index: 1000; height: 4px;';
            container.innerHTML = '<div id="form-progress-bar" class="progress-bar-custom" style="width: 0%"></div>';
            document.body.insertBefore(container, document.body.firstChild);
            progressBar = document.getElementById('form-progress-bar');
        }
        
        const inputs = form.querySelectorAll('input:not([type="hidden"]), textarea, select');
        
        function updateProgress() {
            let filledFields = 0;
            let totalFields = 0;
            
            inputs.forEach(input => {
                // Solo campi visibili
                if (input.offsetParent !== null) {
                    totalFields++;
                    if (input.value.trim() !== '') {
                        filledFields++;
                    }
                }
            });
            
            if (totalFields === 0) return;
            
            const progress = (filledFields / totalFields) * 100;
            progressBar.style.width = progress + '%';
            
            // Cambia colore in base al progresso
            if (progress < 33) {
                progressBar.style.background = 'linear-gradient(90deg, #dc3545, #e74c3c)';
            } else if (progress < 66) {
                progressBar.style.background = 'linear-gradient(90deg, #ffc107, #f39c12)';
            } else if (progress < 100) {
                progressBar.style.background = 'linear-gradient(90deg, #17a2b8, #3498db)';
            } else {
                progressBar.style.background = 'linear-gradient(90deg, #28a745, #2ecc71)';
            }
        }
        
        inputs.forEach(input => {
            input.addEventListener('input', updateProgress);
            input.addEventListener('change', updateProgress);
        });
        
        updateProgress();
    });
    
    // ============================================
    // 2. ANIMAZIONI AUTOMATICHE agli elementi
    // ============================================
    
    // Aggiungi animazioni ai titoli H1, H2, H3 se non hanno già data-animate
    document.querySelectorAll('h1:not([data-animate])').forEach(el => {
        el.setAttribute('data-animate', 'zoom');
        el.classList.add('gradient-text');
    });
    
    document.querySelectorAll('h2:not([data-animate])').forEach(el => {
        el.setAttribute('data-animate', 'fade');
        el.classList.add('gradient-text');
    });
    
    // Aggiungi animazioni alle card se non hanno già
    document.querySelectorAll('.card:not([data-animate])').forEach(el => {
        el.setAttribute('data-animate', 'zoom');
        el.classList.add('card-hover-effect');
    });
    
    // Aggiungi animazioni ai bottoni primari
    document.querySelectorAll('.btn-primary:not(.pulse-animation)').forEach(btn => {
        btn.classList.add('pulse-animation', 'glow-effect', 'ripple-effect');
    });
    
    // Aggiungi float animation alle icone grandi
    document.querySelectorAll('.bi').forEach(icon => {
        const size = parseFloat(window.getComputedStyle(icon).fontSize);
        if (size > 24 && !icon.classList.contains('float-animation')) {
            icon.classList.add('float-animation');
        }
    });
    
    // Aggiungi stagger animation alle liste
    document.querySelectorAll('ul:not(.nav):not(.navbar-nav)').forEach(list => {
        if (list.children.length > 3 && !list.classList.contains('stagger-animation')) {
            list.classList.add('stagger-animation');
        }
    });
    
    // Aggiungi animazioni alle tabelle
    document.querySelectorAll('table:not([data-animate])').forEach(table => {
        table.setAttribute('data-animate', 'fade');
        table.classList.add('card-hover-effect');
    });
    
    // Aggiungi animazioni ai modali
    document.querySelectorAll('.modal-content:not([data-animate])').forEach(modal => {
        modal.classList.add('slide-up-panel');
    });
    
    // ============================================
    // 3. INTERSECTION OBSERVER per reveal animations
    // ============================================
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('in-view');
                
                // Se è un counter, attiva l'animazione
                if (entry.target.hasAttribute('data-counter')) {
                    const target = parseInt(entry.target.getAttribute('data-counter'));
                    if (!entry.target.classList.contains('counted')) {
                        entry.target.classList.add('counted');
                        AnimationUtils.animateCounter(entry.target, target, 2000);
                    }
                }
            }
        });
    }, { 
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });
    
    // Osserva tutti gli elementi con data-animate
    document.querySelectorAll('[data-animate]').forEach(el => observer.observe(el));
    
    // ============================================
    // 4. FORM VALIDATION con animazioni
    // ============================================
    forms.forEach(form => {
        if (!form.hasAttribute('data-no-validation')) {
            new FormValidator(form.id || 'form-' + Math.random().toString(36).substr(2, 9));
            
            // Aggiungi animazione shake agli errori
            form.addEventListener('submit', function(e) {
                const invalids = form.querySelectorAll('.is-invalid, .error');
                if (invalids.length > 0) {
                    invalids.forEach(el => {
                        AnimationUtils.animate(el, 'shake-animation', 500);
                    });
                }
            });
        }
    });
    
    // ============================================
    // 5. ALERT auto-dismissibile con animazioni
    // ============================================
    document.querySelectorAll('.alert:not(.alert-dismissible)').forEach(alert => {
        alert.classList.add('fade-in-animation');
        
        // Auto-dismiss dopo 5 secondi
        setTimeout(() => {
            alert.classList.add('fade-out');
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
    
    // ============================================
    // 6. TOOLTIP e POPOVER animati
    // ============================================
    if (typeof bootstrap !== 'undefined') {
        document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
            new bootstrap.Tooltip(el, {
                animation: true,
                delay: { show: 100, hide: 100 }
            });
        });
        
        document.querySelectorAll('[data-bs-toggle="popover"]').forEach(el => {
            new bootstrap.Popover(el, {
                animation: true
            });
        });
    }
    
    // ============================================
    // 7. LAZY LOADING per immagini
    // ============================================
    if ('IntersectionObserver' in window) {
        const imgObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    if (img.dataset.src) {
                        img.src = img.dataset.src;
                        img.classList.add('fade-in-animation');
                        imgObserver.unobserve(img);
                    }
                }
            });
        });
        
        document.querySelectorAll('img[data-src]').forEach(img => imgObserver.observe(img));
    }
    
    // ============================================
    // 8. SMOOTH SCROLL per anchor links
    // ============================================
    document.querySelectorAll('a[href^="#"]:not([href="#"])').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });
    
    // ============================================
    // 9. PARALLAX effect su hero sections (solo desktop)
    // ============================================
    if (window.innerWidth > 768) {
        const heroSections = document.querySelectorAll('.hero, .jumbotron');
        document.addEventListener('mousemove', (e) => {
            const mouseX = e.clientX / window.innerWidth;
            const mouseY = e.clientY / window.innerHeight;
            
            heroSections.forEach(hero => {
                const speedX = (mouseX - 0.5) * 20;
                const speedY = (mouseY - 0.5) * 20;
                hero.style.transform = `translate(${speedX}px, ${speedY}px)`;
            });
        });
    }
    
    // ============================================
    // 10. SUCCESS MESSAGES con Toast
    // ============================================
    // Converti alert success in Toast (SOLO UNA VOLTA)
    const successMessages = document.querySelectorAll('.alert-success:not(.toast-shown)');
    successMessages.forEach(alert => {
        const message = alert.textContent.trim();
        if (message && typeof Toast !== 'undefined') {
            // Marca come già mostrato
            alert.classList.add('toast-shown');
            // Mostra il toast dopo un breve delay
            setTimeout(() => {
                Toast.show(message, 'success', 4000);
            }, 500);
        }
    });
    
    // ============================================
    // 11. TABELLE responsive con animazioni
    // ============================================
    document.querySelectorAll('table').forEach(table => {
        if (!table.closest('.table-responsive')) {
            const wrapper = document.createElement('div');
            wrapper.className = 'table-responsive';
            table.parentNode.insertBefore(wrapper, table);
            wrapper.appendChild(table);
        }
        
        // Aggiungi hover effect alle righe
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            row.classList.add('smooth-color-transition');
        });
    });
    
    // ============================================
    // 12. BADGE animati
    // ============================================
    document.querySelectorAll('.badge').forEach(badge => {
        badge.classList.add('badge-animated');
    });
    
    // ============================================
    // 13. LOADING states per bottoni form
    // ============================================
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
            if (submitBtn && !form.hasAttribute('data-no-loading')) {
                const originalText = submitBtn.innerHTML;
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Invio...';
                
                // Ripristina dopo 5 secondi (fallback)
                setTimeout(() => {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = originalText;
                }, 5000);
            }
        });
    });
    
    // ============================================
    // 14. SCROLL TO TOP button animato
    // ============================================
    const scrollTopBtn = document.querySelector('.scrolltop');
    if (scrollTopBtn) {
        scrollTopBtn.classList.add('pulse-animation');
    }
    
    // ============================================
    // 15. COUNTER auto-start per statistiche
    // ============================================
    document.querySelectorAll('[data-counter]').forEach(el => {
        el.textContent = '0'; // Reset iniziale
    });
    
    console.log('✨ Animazioni universali attivate!');
    console.log('📊 Form trovati:', forms.length);
    console.log('🎯 Elementi animati:', document.querySelectorAll('[data-animate]').length);
});

// ============================================
// UTILITY: Aggiungi effetto ripple ai bottoni
// ============================================
document.addEventListener('click', function(e) {
    const btn = e.target.closest('.btn, button');
    if (btn && btn.classList.contains('ripple-effect')) {
        const ripple = document.createElement('span');
        ripple.style.cssText = `
            position: absolute;
            border-radius: 50%;
            background: rgba(255,255,255,0.6);
            width: 100px;
            height: 100px;
            margin-top: -50px;
            margin-left: -50px;
            animation: ripple 0.6s;
            pointer-events: none;
        `;
        ripple.style.left = e.clientX - btn.getBoundingClientRect().left + 'px';
        ripple.style.top = e.clientY - btn.getBoundingClientRect().top + 'px';
        
        btn.style.position = 'relative';
        btn.style.overflow = 'hidden';
        btn.appendChild(ripple);
        
        setTimeout(() => ripple.remove(), 600);
    }
});

// ============================================
// ANIMAZIONE RIPPLE CSS
// ============================================
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        from {
            opacity: 1;
            transform: scale(0);
        }
        to {
            opacity: 0;
            transform: scale(2);
        }
    }
`;
document.head.appendChild(style);
