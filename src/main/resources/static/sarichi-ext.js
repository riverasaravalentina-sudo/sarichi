// Extensions for Sprint 5: sanitization, sharing, analytics, file downloads, pagination

(function(){

  function sanitizarHTML(html) {
    // Parse HTML and strip dangerous elements/attributes
    const parser = new DOMParser();
    const doc = parser.parseFromString(html || '', 'text/html');

    const blacklist = ['script','style','iframe','object','embed','link'];
    blacklist.forEach(tag => doc.querySelectorAll(tag).forEach(n => n.remove()));

    const walker = doc.createTreeWalker(doc.body, NodeFilter.SHOW_ELEMENT, null, false);
    const toRemove = [];
    while(walker.nextNode()) {
      const el = walker.currentNode;
      Array.from(el.attributes || []).forEach(attr => {
        const name = attr.name.toLowerCase();
        const value = (attr.value || '').toLowerCase();
        if (name.startsWith('on') || value.startsWith('javascript:') || value.startsWith('data:')) {
          toRemove.push({el, name});
        }
      });
    }
    toRemove.forEach(a => a.el.removeAttribute(a.name));
    return doc.body.innerHTML;
  }

  function compartirWhatsApp(texto, url) {
    const payload = encodeURIComponent((texto || '') + ' ' + (url || window.location.href));
    window.open('https://api.whatsapp.com/send?text=' + payload, '_blank');
  }

  function compartirInstagram(url) {
    const web = 'https://www.instagram.com/';
    // Attempt deep link, then fallback
    try { window.open('instagram://share?url=' + encodeURIComponent(url || window.location.href), '_blank'); }
    catch(e) { window.open(web, '_blank'); }
  }

  async function registrarVisita(sessionId, fuente, url, usuarioId, userAgent, ipAddress) {
    try {
      await fetch('/api/analitica/visita', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: sessionId || sessionStorage.getItem('sarichi_session') || null,
          fuente: fuente || 'DIRECTO',
          url: url || window.location.pathname + window.location.search,
          usuarioId: usuarioId || (sessionStorage.getItem('sarichi_user') ? JSON.parse(sessionStorage.getItem('sarichi_user')).id : null),
          userAgent: userAgent || navigator.userAgent,
          ipAddress: ipAddress || null
        })
      });
    } catch(e) {
      console.warn('registrarVisita failed', e.message);
    }
  }

  async function registrarClick(productoId) {
    try {
      await fetch('/api/analitica/click', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: sessionStorage.getItem('sarichi_session') || null, url: window.location.pathname + window.location.search, productoId })
      });
    } catch(e) { console.warn('registrarClick failed', e.message); }
  }

  async function descargarArchivo(url, nombreArchivo) {
    try {
      const token = sessionStorage.getItem('sarichi_token');
      const headers = token ? { 'Authorization': 'Bearer ' + token } : {};
      const res = await fetch(url, { headers });
      if (!res.ok) throw new Error('Error ' + res.status);
      const blob = await res.blob();
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = nombreArchivo || 'archivo';
      document.body.appendChild(a);
      a.click();
      a.remove();
    } catch(e) { console.error('descargarArchivo error', e); throw e; }
  }

  async function cargarMas(selector, url, offset = 0, limit = 12) {
    try {
      const container = document.querySelector(selector);
      if (!container) return 0;
      const res = await fetch(url + (url.includes('?') ? '&' : '?') + 'offset=' + offset + '&limit=' + limit);
      if (!res.ok) throw new Error('Error cargando más');
      const items = await res.json();
      if (!Array.isArray(items)) return 0;
      items.forEach(it => {
        const div = document.createElement('div');
        div.className = 'card-mini';
        div.innerHTML = `<h4>${it.titulo || it.nombre}</h4>`;
        container.appendChild(div);
      });
      return items.length;
    } catch(e) { console.warn('cargarMas error', e); return 0; }
  }

  // Export
  window.sarichiExt = {
    sanitizarHTML,
    compartirWhatsApp,
    compartirInstagram,
    registrarVisita,
    registrarClick,
    descargarArchivo,
    cargarMas
  };

})();
