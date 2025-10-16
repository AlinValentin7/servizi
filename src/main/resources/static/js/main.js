// Main UI interactions: sticky header, reveal, counters, carousel, floating actions
(function(){
  const header = document.querySelector('header');
  const scrollTopBtn = document.createElement('div');
  scrollTopBtn.className = 'scrolltop';
  // Use Bootstrap Icons instead of emoji
  scrollTopBtn.innerHTML = '<i class="bi bi-arrow-up"></i>';
  document.body.appendChild(scrollTopBtn);

  const onScroll = () => {
    if (header && !header.classList.contains('sticky')) header.classList.add('sticky');
    const y = window.scrollY || document.documentElement.scrollTop;
    if (y > 240) scrollTopBtn.classList.add('show'); else scrollTopBtn.classList.remove('show');
  };
  window.addEventListener('scroll', onScroll, { passive: true });

  scrollTopBtn.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });

  // Reveal on scroll
  const revealEls = document.querySelectorAll('[data-animate]');
  if (revealEls.length) {
    const io = new IntersectionObserver((entries) => {
      entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('in-view'); });
    }, { threshold: 0.15 });
    revealEls.forEach(el => io.observe(el));
  }

  // Counters
  const counters = document.querySelectorAll('[data-counter]');
  if (counters.length) {
    const runCounter = (el) => {
      const target = parseInt(el.getAttribute('data-counter'), 10) || 0;
      let cur = 0; const dur = 1200; const start = performance.now();
      const step = (t) => {
        const p = Math.min((t - start) / dur, 1);
        cur = Math.floor(target * (0.5 - Math.cos(Math.PI * p)/2));
        el.textContent = cur.toLocaleString('it-IT');
        if (p < 1) requestAnimationFrame(step);
      };
      requestAnimationFrame(step);
    };
    const io2 = new IntersectionObserver((entries, obs) => {
      entries.forEach(e => { if (e.isIntersecting) { runCounter(e.target); obs.unobserve(e.target); } });
    }, { threshold: 0.4 });
    counters.forEach(el => io2.observe(el));
  }

  // Simple carousel
  document.querySelectorAll('.carousel').forEach(carousel => {
    const track = carousel.querySelector('.carousel-track');
    const items = carousel.querySelectorAll('.carousel-item');
    const prev = carousel.querySelector('[data-prev]');
    const next = carousel.querySelector('[data-next]');
    let index = 0;
    const update = () => { if (track) track.style.transform = `translateX(-${index * 100}%)`; };
    if (next) next.addEventListener('click', () => { index = (index + 1) % items.length; update(); });
    if (prev) prev.addEventListener('click', () => { index = (index - 1 + items.length) % items.length; update(); });
    // auto-play
    if (items.length > 1) setInterval(() => { index = (index + 1) % items.length; update(); }, 5000);
  });

  // Inject floating WhatsApp button on public pages only (not /admin)
  const isAdmin = window.location && typeof window.location.pathname === 'string' && window.location.pathname.startsWith('/admin');
  if (!isAdmin && !document.querySelector('.floating-whatsapp')) {
    const wa = document.createElement('a');
    wa.href = 'https://wa.me/393801590128';
    wa.className = 'floating-whatsapp';
    wa.title = 'Chatta su WhatsApp';
    wa.innerHTML = '<i class="bi bi-whatsapp"></i>';
    wa.setAttribute('aria-label', 'Apri WhatsApp');
    document.body.appendChild(wa);
  }
})();