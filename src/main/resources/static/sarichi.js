// Cliente global para el frontend estático de Crocheting Sarichi.
const API = '/api';
const API_BASE_URL = API;
const TOKEN_KEY = 'sarichi_token';
const REFRESH_TOKEN_KEY = 'sarichi_refresh';
const USER_KEY = 'sarichi_user';

function apiUrl(url) {
  if (!url) return API;
  if (/^https?:\/\//i.test(url)) return url;
  if (url.startsWith(API + '/')) return url;
  return API + (url.startsWith('/') ? url : '/' + url);
}

const Auth = {
  save(data) {
    const token = data.accessToken || data.token;
    const refresh = data.refreshToken || data.refresh;
    const usuario = data.usuario || data.user;
    if (token) sessionStorage.setItem(TOKEN_KEY, token);
    if (refresh) sessionStorage.setItem(REFRESH_TOKEN_KEY, refresh);
    if (usuario) sessionStorage.setItem(USER_KEY, JSON.stringify(usuario));
  },
  getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
  },
  getRefresh() {
    return sessionStorage.getItem(REFRESH_TOKEN_KEY);
  },
  getUser() {
    const value = sessionStorage.getItem(USER_KEY);
    if (!value) return null;
    try {
      return JSON.parse(value);
    } catch {
      return null;
    }
  },
  get() {
    return Auth.getUser();
  },
  getRol() {
    return Auth.getUser()?.rol || null;
  },
  isLoggedIn() {
    return !!Auth.getToken();
  },
  logout() {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    window.location.href = '/login.html';
  },
  requireAuth() {
    if (!Auth.isLoggedIn()) {
      window.location.href = '/login.html';
      return false;
    }
    return true;
  },
  redirectByRol(rol) {
    const rutas = {
      ADMIN: '/dashboard.html',
      ARTESANA: '/pedidos.html',
      LOGISTICA: '/despachos.html',
      BODEGA: '/inventario.html',
      MERCADEO: '/analiticas.html',
      CLIENTE: '/tienda.html'
    };
    window.location.href = rutas[rol] || '/tienda.html';
  }
};

function authHeaders(extraHeaders = {}) {
  const headers = { ...extraHeaders };
  const token = Auth.getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  return headers;
}

async function parseResponse(response) {
  const contentType = response.headers.get('content-type') || '';
  const isJson = contentType.includes('application/json');
  const data = isJson ? await response.json().catch(() => ({})) : await response.text();

  if (!response.ok) {
    const message = isJson
      ? (data.message || data.mensaje || data.error || 'Error en la solicitud')
      : (data || 'Error en la solicitud');
    throw new Error(message);
  }
  return data;
}

async function apiRequest(method, url, body, options = {}) {
  const headers = authHeaders(options.headers || {});
  const config = { method, headers };

  if (body instanceof FormData) {
    config.body = body;
  } else if (body !== undefined && body !== null) {
    headers['Content-Type'] = 'application/json';
    config.body = JSON.stringify(body);
  }

  return parseResponse(await fetch(apiUrl(url), config));
}

async function apiGet(url) {
  return apiRequest('GET', url);
}

async function apiPost(url, body) {
  return apiRequest('POST', url, body);
}

async function apiPut(url, body) {
  return apiRequest('PUT', url, body);
}

async function apiDelete(url) {
  return apiRequest('DELETE', url);
}

async function fetchAutenticado(url, options = {}) {
  options.headers = authHeaders(options.headers || {});
  return fetch(apiUrl(url), options);
}

function showToast(mensaje, tipo = 'success') {
  const toast = document.getElementById('toast') || crearToast();
  toast.textContent = mensaje;
  toast.className = `toast toast-${tipo} show`;
  toast.style.display = 'block';
  setTimeout(() => {
    toast.classList.remove('show');
    toast.style.display = 'none';
  }, 3500);
}

function crearToast() {
  const toast = document.createElement('div');
  toast.id = 'toast';
  toast.className = 'toast';
  document.body.appendChild(toast);
  return toast;
}

function requireAuth() {
  return Auth.requireAuth();
}

function logout() {
  Auth.logout();
}

function renderNav(rol) {
  const targets = document.querySelectorAll('[data-user-name]');
  const user = Auth.getUser();
  targets.forEach(el => {
    el.textContent = user?.nombre || 'Usuario';
  });

  document.querySelectorAll('[data-role]').forEach(el => {
    const roles = (el.dataset.role || '').split(',').map(r => r.trim());
    el.style.display = roles.includes(rol) ? '' : 'none';
  });
}

function iniciarRefreshAutomatico() {
  if (!Auth.getRefresh()) return;
  setInterval(async () => {
    try {
      const response = await fetch(apiUrl('/auth/refresh'), {
        method: 'POST',
        headers: { Authorization: `Bearer ${Auth.getRefresh()}` }
      });
      const data = await parseResponse(response);
      Auth.save(data);
    } catch (error) {
      console.warn('No se pudo renovar la sesión:', error.message);
    }
  }, 12 * 60 * 1000);
}

function sanitizarHTML(html) {
  const template = document.createElement('template');
  template.innerHTML = html || '';
  template.content.querySelectorAll('script,iframe,object,embed').forEach(el => el.remove());
  template.content.querySelectorAll('*').forEach(el => {
    [...el.attributes].forEach(attr => {
      if (/^on/i.test(attr.name) || /^javascript:/i.test(attr.value)) {
        el.removeAttribute(attr.name);
      }
    });
  });
  return template.innerHTML;
}

function compartirWhatsApp(texto, url = window.location.href) {
  window.open(`https://wa.me/?text=${encodeURIComponent(`${texto} ${url}`)}`, '_blank');
}

function compartirFacebook(url = window.location.href) {
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`, '_blank');
}

async function registrarVisita(url = window.location.pathname) {
  try {
    await apiPost('/analitica/visita', {
      url,
      fuente: detectarFuente(),
      sessionId: obtenerSessionId(),
      userAgent: navigator.userAgent
    });
  } catch (error) {
    console.warn('No se pudo registrar visita:', error.message);
  }
}

async function registrarClick(elemento, url = window.location.pathname) {
  try {
    await apiPost('/analitica/click', {
      elemento,
      url,
      sessionId: obtenerSessionId()
    });
  } catch (error) {
    console.warn('No se pudo registrar click:', error.message);
  }
}

function detectarFuente() {
  const ref = document.referrer || '';
  if (!ref) return 'directo';
  if (ref.includes('instagram')) return 'instagram';
  if (ref.includes('google')) return 'google';
  if (ref.includes('facebook')) return 'facebook';
  return 'referido';
}

function obtenerSessionId() {
  let id = sessionStorage.getItem('sarichi_session_id');
  if (!id) {
    id = crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random()}`;
    sessionStorage.setItem('sarichi_session_id', id);
  }
  return id;
}

async function descargarArchivo(url, nombreArchivo = 'reporte') {
  const response = await fetch(apiUrl(url), { headers: authHeaders() });
  if (!response.ok) throw new Error('No se pudo descargar el archivo');
  const blob = await response.blob();
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = nombreArchivo;
  link.click();
  URL.revokeObjectURL(link.href);
}

async function cargarMas(selector, url, offset = 0, limit = 12) {
  const contenedor = document.querySelector(selector);
  if (!contenedor) return 0;
  const datos = await apiGet(`${url}${url.includes('?') ? '&' : '?'}offset=${offset}&limit=${limit}`);
  const items = Array.isArray(datos) ? datos : (datos.content || []);
  return items.length;
}

function formatearFecha(fecha) {
  if (!fecha) return '';
  return new Intl.DateTimeFormat('es-CO', {
    year: 'numeric',
    month: 'short',
    day: '2-digit'
  }).format(new Date(fecha));
}

function truncar(texto, max = 120) {
  if (!texto) return '';
  return texto.length > max ? texto.slice(0, max - 1) + '…' : texto;
}

window.API = API;
window.API_BASE_URL = API_BASE_URL;
window.Auth = Auth;
window.apiGet = apiGet;
window.apiPost = apiPost;
window.apiPut = apiPut;
window.apiDelete = apiDelete;
window.fetchAutenticado = fetchAutenticado;
window.showToast = showToast;
window.requireAuth = requireAuth;
window.logout = logout;
window.renderNav = renderNav;
window.iniciarRefreshAutomatico = iniciarRefreshAutomatico;
window.sanitizarHTML = sanitizarHTML;
window.compartirWhatsApp = compartirWhatsApp;
window.compartirFacebook = compartirFacebook;
window.registrarVisita = registrarVisita;
window.registrarClick = registrarClick;
window.detectarFuente = detectarFuente;
window.obtenerSessionId = obtenerSessionId;
window.descargarArchivo = descargarArchivo;
window.cargarMas = cargarMas;
window.formatearFecha = formatearFecha;
window.truncar = truncar;

iniciarRefreshAutomatico();
