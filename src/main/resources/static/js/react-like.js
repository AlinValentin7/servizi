/**
 * Sistema React-Like per Componenti Dinamici
 * Rendering dinamico, state management e reattività
 */

class ComponentSystem {
    constructor() {
        this.components = new Map();
        this.state = new Proxy({}, {
            set: (target, property, value) => {
                target[property] = value;
                this.updateComponents(property);
                return true;
            }
        });
    }

    /**
     * Registra un nuovo componente
     */
    registerComponent(name, componentFn) {
        this.components.set(name, componentFn);
    }

    /**
     * Monta un componente nel DOM
     */
    mountComponent(name, elementId, props = {}) {
        const component = this.components.get(name);
        if (!component) {
            console.error(`Component ${name} not found`);
            return;
        }

        const element = document.getElementById(elementId);
        if (!element) {
            console.error(`Element #${elementId} not found`);
            return;
        }

        const html = component(props, this.state);
        element.innerHTML = html;
        this.attachEventListeners(element);
    }

    /**
     * Aggiorna lo state
     */
    setState(key, value) {
        this.state[key] = value;
    }

    /**
     * Ottiene lo state
     */
    getState(key) {
        return this.state[key];
    }

    /**
     * Aggiorna i componenti quando cambia lo state
     */
    updateComponents(changedKey) {
        document.querySelectorAll(`[data-reactive="${changedKey}"]`).forEach(el => {
            if (el.dataset.reactiveProperty) {
                el[el.dataset.reactiveProperty] = this.state[changedKey];
            } else {
                el.textContent = this.state[changedKey];
            }
        });
    }

    /**
     * Attacca event listeners dinamici
     */
    attachEventListeners(container) {
        container.querySelectorAll('[data-click]').forEach(el => {
            const handlerName = el.dataset.click;
            el.addEventListener('click', () => {
                if (window[handlerName]) {
                    window[handlerName](el);
                }
            });
        });
    }
}

// Inizializza il sistema globale
const App = new ComponentSystem();

/**
 * Componente Card Dinamica con animazioni
 */
App.registerComponent('AnimatedCard', (props) => {
    const { title, description, icon, animation = 'zoom' } = props;
    return `
        <div class="card card-hover-effect" data-animate="${animation}">
            <div class="card-body">
                ${icon ? `<i class="${icon} card-icon float-animation"></i>` : ''}
                <h3 class="card-title">${title}</h3>
                <p class="card-text">${description}</p>
            </div>
        </div>
    `;
});

/**
 * Componente Alert con animazioni
 */
App.registerComponent('Alert', (props) => {
    const { type = 'info', message, dismissible = true } = props;
    const icons = {
        success: 'bi-check-circle-fill',
        error: 'bi-x-circle-fill',
        warning: 'bi-exclamation-triangle-fill',
        info: 'bi-info-circle-fill'
    };
    
    return `
        <div class="alert alert-${type} ${dismissible ? 'alert-dismissible' : ''} fade-in-animation" role="alert">
            <i class="bi ${icons[type]}"></i> ${message}
            ${dismissible ? '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' : ''}
        </div>
    `;
});

/**
 * Componente Loading Spinner
 */
App.registerComponent('LoadingSpinner', (props) => {
    const { text = 'Caricamento...', size = 'medium' } = props;
    const sizeClasses = {
        small: 'spinner-sm',
        medium: 'spinner-md',
        large: 'spinner-lg'
    };
    
    return `
        <div class="loading-spinner ${sizeClasses[size]}">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">${text}</span>
            </div>
            <p class="mt-2">${text}</p>
        </div>
    `;
});

/**
 * Componente Modal Dinamico
 */
App.registerComponent('DynamicModal', (props) => {
    const { id, title, body, footer = '' } = props;
    return `
        <div class="modal fade" id="${id}" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered slide-up-panel">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${title}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">${body}</div>
                    ${footer ? `<div class="modal-footer">${footer}</div>` : ''}
                </div>
            </div>
        </div>
    `;
});

/**
 * Utilità per animazioni
 */
const AnimationUtils = {
    /**
     * Aggiunge animazione a un elemento
     */
    animate(element, animationClass, duration = 1000) {
        return new Promise((resolve) => {
            element.classList.add(animationClass);
            setTimeout(() => {
                element.classList.remove(animationClass);
                resolve();
            }, duration);
        });
    },

    /**
     * Effetto parallax al movimento del mouse
     */
    enableParallax(selector, intensity = 0.05) {
        const elements = document.querySelectorAll(selector);
        document.addEventListener('mousemove', (e) => {
            const mouseX = e.clientX / window.innerWidth;
            const mouseY = e.clientY / window.innerHeight;

            elements.forEach((el) => {
                const speedX = (mouseX - 0.5) * intensity * 100;
                const speedY = (mouseY - 0.5) * intensity * 100;
                el.style.transform = `translate(${speedX}px, ${speedY}px)`;
            });
        });
    },

    /**
     * Scroll progressivo con callback
     */
    onScrollProgress(callback) {
        window.addEventListener('scroll', () => {
            const winScroll = document.documentElement.scrollTop;
            const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
            const scrolled = (winScroll / height) * 100;
            callback(scrolled);
        });
    },

    /**
     * Counter animato
     */
    animateCounter(element, target, duration = 2000) {
        const start = 0;
        const increment = target / (duration / 16);
        let current = start;

        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                element.textContent = target;
                clearInterval(timer);
            } else {
                element.textContent = Math.floor(current);
            }
        }, 16);
    },

    /**
     * Typewriter effect
     */
    typeWriter(element, text, speed = 100) {
        let i = 0;
        element.textContent = '';
        
        const type = () => {
            if (i < text.length) {
                element.textContent += text.charAt(i);
                i++;
                setTimeout(type, speed);
            }
        };
        
        type();
    }
};

/**
 * Form Validation con animazioni
 */
class FormValidator {
    constructor(formId) {
        this.form = document.getElementById(formId);
        if (this.form) {
            this.init();
        }
    }

    init() {
        this.form.addEventListener('submit', (e) => {
            if (!this.validate()) {
                e.preventDefault();
                this.showErrors();
            }
        });
    }

    validate() {
        const inputs = this.form.querySelectorAll('input[required], textarea[required], select[required]');
        let isValid = true;

        inputs.forEach(input => {
            if (!input.value.trim()) {
                isValid = false;
                input.classList.add('is-invalid');
                AnimationUtils.animate(input, 'shake-animation', 500);
            } else {
                input.classList.remove('is-invalid');
                input.classList.add('is-valid');
            }
        });

        return isValid;
    }

    showErrors() {
        const firstInvalid = this.form.querySelector('.is-invalid');
        if (firstInvalid) {
            firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
            firstInvalid.focus();
        }
    }
}

/**
 * Lazy Loading per immagini
 */
class LazyLoader {
    constructor(selector = 'img[data-src]') {
        this.images = document.querySelectorAll(selector);
        this.init();
    }

    init() {
        if ('IntersectionObserver' in window) {
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const img = entry.target;
                        img.src = img.dataset.src;
                        img.classList.add('fade-in-animation');
                        observer.unobserve(img);
                    }
                });
            });

            this.images.forEach(img => observer.observe(img));
        } else {
            // Fallback per browser vecchi
            this.images.forEach(img => {
                img.src = img.dataset.src;
            });
        }
    }
}

/**
 * Toast Notifications
 */
const Toast = {
    show(message, type = 'info', duration = 3000) {
        const toast = document.createElement('div');
        toast.className = `toast-notification toast-${type} slide-in-animation`;
        toast.innerHTML = `
            <i class="bi ${this.getIcon(type)}"></i>
            <span>${message}</span>
        `;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    getIcon(type) {
        const icons = {
            success: 'bi-check-circle-fill',
            error: 'bi-x-circle-fill',
            warning: 'bi-exclamation-triangle-fill',
            info: 'bi-info-circle-fill'
        };
        return icons[type] || icons.info;
    }
};

// Esporta per uso globale
window.App = App;
window.AnimationUtils = AnimationUtils;
window.FormValidator = FormValidator;
window.LazyLoader = LazyLoader;
window.Toast = Toast;

// Auto-inizializzazione
document.addEventListener('DOMContentLoaded', () => {
    // Inizializza lazy loading
    new LazyLoader();
    
    // Attiva parallax sugli elementi specificati
    if (window.innerWidth > 768) {
        AnimationUtils.enableParallax('.parallax-element');
    }
    
    console.log('🚀 Sistema di componenti dinamici caricato!');
});
