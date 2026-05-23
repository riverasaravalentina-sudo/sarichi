// ── Sarichi Crocheting — API Utility ─────────────────────────────────────
const API = 'http://localhost:8080/api';

const Auth = {
  getToken:   () => sessionStorage.getItem('sarichi_token'),
  getRefresh: () => sessionStorage.getItem('sarichi_refresh'),
  getUser:    () => JSON.parse(sessionStorage.getItem('sarichi_user') || 'null'),
  getRol:     () => sessionStorage.getItem('sarichi_rol'),

  save(data) {
    sessionStorage.setItem('sarichi_token',   data.accessToken);
    sessionStorage.setItem('sarichi_refresh', data.refreshToken);
    sessionStorage.setItem('sarichi_rol',     data.usuario.rol);
    sessionStorage.setItem('sarichi_user',    JSON.stringify(data.usuario));
  },

  clear() {
    ['sarichi_token','sarichi_refresh','sarichi_rol','sarichi_user']
      .forEach(k => sessionStorage.removeItem(k));
  },

  isLoggedIn() { return !!this.getToken(); },

  redirectByRol(rol) {
    const map = {
      ADMIN:     'dashboard.html',
      ARTESANA:  'pedidos.html',
      LOGISTICA: 'despachos.html',
      BODEGA:    'inventario.html',
      MERCADEO:  'analiticas.html',
      CLIENTE:   'tienda.html',
    };
    window.location.href = map[rol] || 'tienda.html';
  }
};

async function apiPost(path, body, auth = false) {
  const headers = { 'Content-Type': 'application/json' };
  if (auth) headers['Authorization'] = 'Bearer ' + Auth.getToken();
  const res = await fetch(API + path, { method: 'POST', headers, body: JSON.stringify(body) });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(json.mensaje || json.error || 'Error ' + res.status);
  return json;
}

async function apiGet(path) {
  const res = await fetch(API + path, {
    headers: { 'Authorization': 'Bearer ' + Auth.getToken() }
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(json.mensaje || 'Error ' + res.status);
  return json;
}

function showToast(msg, type = 'success') {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.className = 'toast show ' + type;
  setTimeout(() => t.className = 'toast', 3200);
}

function requireAuth() {
  if (!Auth.isLoggedIn()) window.location.href = 'login.html';
}

function renderNav(rol) {
  const labels = { ADMIN:'Administrador', ARTESANA:'Artesana', LOGISTICA:'Logistica',
                   BODEGA:'Bodega', MERCADEO:'Mercadeo', CLIENTE:'Cliente' };
  const user = Auth.getUser();
  const nav = document.getElementById('topnav');
  if (!nav) return;
  nav.innerHTML = `
    <div class="nav-brand">🐱 Sarichi Crocheting</div>
    <div class="nav-right">
      <span class="nav-rol">${labels[rol]||rol}</span>
      <span class="nav-name">${user?.nombre || ''}</span>
      <button onclick="logout()" class="btn-logout">Salir</button>
    </div>`;
}

async function logout() {
  try {
    const user = Auth.getUser();
    if (user?.id) {
      await fetch(API + '/auth/logout', {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + Auth.getToken(), 'X-User-Id': user.id }
      });
    }
  } catch(e) {}
  Auth.clear();
  window.location.href = 'login.html';
}

async function apiFetch(path, method = 'GET', body = null) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + Auth.getToken()
  };
  const options = { method, headers };
  if (body) options.body = JSON.stringify(body);
  const res = await fetch(API + path, options);
  if (res.status === 204) return null;
  const json = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(json.mensaje || json.error || 'Error ' + res.status);
  return json;
}
// Auto-refresh del token cada 12 minutos (antes de que expire en 15)
async function refrescarTokenAutomatico() {
  const refreshToken = Auth.getRefresh();
  if (!refreshToken || !Auth.isLoggedIn()) return;

  try {
    const data = await fetch(API + '/auth/refresh', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + refreshToken,
        'Content-Type': 'application/json'
      }
    });
    if (data.ok) {
      const json = await data.json();
      sessionStorage.setItem('sarichi_token', json.accessToken);
    }
  } catch(e) {
    console.warn('No se pudo refrescar el token:', e.message);
  }
}

// Refrescar cada 12 minutos automáticamente
setInterval(refrescarTokenAutomatico, 12 * 60 * 1000);