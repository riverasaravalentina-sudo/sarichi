const { test, expect } = require('@playwright/test');

const RIFA = '/rifa/index.html';
const ROSA = 'rgb(232, 82, 122)';
const VERDE = 'rgb(220, 246, 228)';
const GRIS = 'rgb(243, 244, 246)';
const COLS = { desktop: 10, tablet: 8, mobile: 5 };

function num(page, n) {
  return page.locator(`#grid button[data-num="${n}"]`);
}

async function seleccionar(page, ...nums) {
  for (const n of nums) await num(page, n).click();
}

async function irARifa(page) {
  await page.goto(RIFA);
  await expect(num(page, 0)).toBeVisible();
}

// ── 1. Carga general ─────────────────────────────────────────────
test('P01 carga: título, 100 botones 00-99, sin errores JS', async ({ page }) => {
  const errores = [];
  page.on('pageerror', e => errores.push(e.message));

  await page.goto(RIFA);
  await expect(page).toHaveTitle(/Rifa Garfield/);
  await expect(page.locator('#grid button')).toHaveCount(100);
  await expect(num(page, 0)).toHaveText('00');
  await expect(num(page, 99)).toHaveText('99');
  await expect(num(page, 7)).toBeVisible();

  const heroCargada = await page.locator('#heroImg').evaluate(img => img.naturalWidth > 0);
  expect(heroCargada, 'la foto del hero debe estar cargada').toBe(true);
  expect(errores, 'no debe haber errores JS en consola').toEqual([]);
});

// ── 2. Selección ─────────────────────────────────────────────────
test('P02 clic selecciona: color rosita, lista y total correctos', async ({ page }) => {
  await irARifa(page);
  await num(page, 7).click();

  await expect(num(page, 7)).toHaveClass(/seleccionado/);
  await expect(num(page, 7)).toHaveCSS('background-color', ROSA);
  await expect(num(page, 7)).toHaveCSS('color', 'rgb(255, 255, 255)');

  await expect(page.locator('#listaSeleccionados')).toHaveText('07');
  await expect(page.locator('#total')).toHaveText('$2.500');
  await expect(page.locator('#btnReservar')).toBeEnabled();
});

test('P03 clic de nuevo deselecciona: verde, total $0 y botón deshabilitado', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 7);
  await num(page, 7).click();

  await expect(num(page, 7)).toHaveClass(/disponible/);
  await expect(num(page, 7)).not.toHaveClass(/seleccionado/);
  await expect(num(page, 7)).toHaveCSS('background-color', VERDE);
  await expect(page.locator('#listaSeleccionados')).toHaveText('ninguno');
  await expect(page.locator('#total')).toHaveText('$0');
  await expect(page.locator('#btnReservar')).toBeDisabled();
});

test('P04 varios números: lista ordenada y total multiplicado', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 10, 5, 77);

  await expect(page.locator('#listaSeleccionados')).toHaveText('05, 10, 77');
  await expect(page.locator('#total')).toHaveText('$7.500');
});

test('P05 la selección sobrevive a recargar la página (localStorage)', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 3, 21);
  await page.reload();

  await expect(num(page, 3)).toHaveCSS('background-color', ROSA);
  await expect(num(page, 21)).toHaveCSS('background-color', ROSA);
  await expect(page.locator('#listaSeleccionados')).toHaveText('03, 21');
  await expect(page.locator('#total')).toHaveText('$5.000');
});

// ── 3. Resumen sticky ────────────────────────────────────────────
test('P06 resumen sticky: visible en móvil y oculto en escritorio', async ({ page }) => {
  const proyecto = test.info().project.name;
  await irARifa(page);

  const sticky = page.locator('#stickyResumen');
  await expect(sticky, 'con 0 números no debe verse').toBeHidden();

  await seleccionar(page, 4, 5);
  if (proyecto === 'mobile') {
    await expect(sticky).toBeVisible();
    await expect(page.locator('#stickyCount')).toHaveText('2');
    await expect(page.locator('#stickyTotal')).toHaveText('$5.000');
    await expect(page.locator('#stickyLista')).toHaveText('04, 05');
  } else {
    await expect(sticky, 'en tablet/escritorio no debe verse').toBeHidden();
  }
});

// ── 4. Tablero responsive ────────────────────────────────────────
test('P07 el tablero usa la cantidad de columnas según el dispositivo', async ({ page }) => {
  const proyecto = test.info().project.name;
  await irARifa(page);
  const columnas = await page.locator('#grid').evaluate(el =>
    getComputedStyle(el).gridTemplateColumns.split(' ').length);
  expect(columnas).toBe(COLS[proyecto]);
});

test('P08 sin desbordamiento horizontal de la página', async ({ page }) => {
  await irARifa(page);
  const desborde = await page.evaluate(() =>
    document.documentElement.scrollWidth - document.documentElement.clientWidth);
  expect(desborde, 'no debe haber scroll horizontal').toBeLessThanOrEqual(1);
});

// ── 5. Carrusel y navegación ─────────────────────────────────────
test('P09 carrusel: las flechas cambian de foto, dan la vuelta y actualizan el contador', async ({ page }) => {
  await irARifa(page);
  const img = page.locator('#heroImg');
  const contador = page.locator('#fotoContador');

  await expect(page.locator('.thumb-btn'), 'ya no deben existir botones "Foto N"').toHaveCount(0);
  await expect(img).toHaveAttribute('src', /garfiel-1\.jpeg$/);
  await expect(contador).toHaveText('1 / 4');

  await page.locator('#btnFotoSiguiente').click();
  await expect(img).toHaveAttribute('src', /garfiel-2\.jpeg$/);
  await expect(contador).toHaveText('2 / 4');

  await page.locator('#btnFotoAnterior').click();
  await expect(contador).toHaveText('1 / 4');

  await page.locator('#btnFotoAnterior').click();
  await expect(img).toHaveAttribute('src', /garfiel-4\.jpeg$/);
  await expect(contador).toHaveText('4 / 4');

  await expect.poll(() => img.evaluate(i => i.naturalWidth > 0), {
    timeout: 20_000, message: 'la foto debe cargarse',
  }).toBe(true);
});

test('P10 el botón del hero lleva al tablero', async ({ page }) => {
  await irARifa(page);
  await page.locator('a[href="#tablero"]').first().click();
  await expect.poll(async () => page.evaluate(() => window.scrollY), {
    timeout: 10_000,
  }).toBeGreaterThan(200);
});

// ── 6. Modal de pago ─────────────────────────────────────────────
test('P11 el modal abre con header fijo, botón de WhatsApp y aviso final', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 7, 8);
  await page.locator('#btnReservar').click();

  const modal = page.locator('#modalPago');
  await expect(modal).toBeVisible();
  await expect(page.locator('.modal-content')).toBeVisible();
  await expect(page.locator('.modal-header')).toContainText('Reservar mis números');
  await expect(page.locator('#modalNumeros')).toHaveText('07, 08');
  await expect(page.locator('#modalTotal')).toHaveText('$5.000');
  await expect(page.locator('#btnWhatsApp')).toBeVisible();
  await expect(page.locator('#btnWhatsApp')).toContainText('Enviar comprobante por WhatsApp');
  await expect(page.locator('.modal-body p', { hasText: 'Los números quedan' })).toBeAttached();

  const overflow = await page.evaluate(() => document.body.style.overflow);
  expect(overflow, 'al abrir el modal la página deja de scrollear').toBe('hidden');
});

test('P12 el modal se cierra con la ×, con Escape y con clic fuera, y libera el scroll', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 11);
  const modal = page.locator('#modalPago');
  const cerrar = page.locator('.modal-header button');

  await page.locator('#btnReservar').click();
  await expect(modal).toBeVisible();
  await cerrar.click();
  await expect(modal).toBeHidden();
  expect(await page.evaluate(() => document.body.style.overflow)).toBe('');

  await page.locator('#btnReservar').click();
  await page.keyboard.press('Escape');
  await expect(modal).toBeHidden();

  await page.locator('#btnReservar').click();
  await modal.click({ position: { x: 5, y: 5 } });
  await expect(modal).toBeHidden();
  expect(await page.evaluate(() => document.body.style.overflow)).toBe('');
});

test('P13 el enlace de WhatsApp trae los números y el total', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 7, 8);

  const esperado = 'https://wa.me/573214394862?text=' + encodeURIComponent(
    'Hola, quiero los números 07, 08 para la rifa de Garfield. Total: $5.000. Adjunto comprobante.');
  await expect(page.locator('#btnWhatsApp')).toHaveAttribute('href', esperado);

  await page.locator('#btnReservar').click();
  await expect(page.locator('#btnWhatsApp')).toHaveAttribute('href', esperado);
  await expect(page.locator('#btnWhatsApp')).toHaveAttribute('target', '_blank');
});

test('P14 copiar Nequi / Bre-B al portapapeles', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 1);
  await page.locator('#btnReservar').click();

  const botones = page.locator('.modal-body button');
  await expect(botones).toHaveCount(2);

  const botonNequi = botones.first();
  await botonNequi.click();
  await expect(botonNequi).toContainText('¡Copiado!');
  expect(await page.evaluate(() => navigator.clipboard.readText())).toBe('3178113232');

  const botonBreb = botones.last();
  await botonBreb.click();
  await expect(botonBreb).toContainText('¡Copiado!');
  expect(await page.evaluate(() => navigator.clipboard.readText())).toBe('3178113232');
});

test('P15 el modal no se desborda de la pantalla', async ({ page }) => {
  await irARifa(page);
  await seleccionar(page, 1, 2, 3, 4, 5, 6, 7, 8);
  await page.locator('#btnReservar').click();

  const vp = page.viewportSize();
  const caja = await page.locator('.modal-content').boundingBox();
  expect(caja.height).toBeLessThanOrEqual(vp.height);
  expect(caja.y).toBeGreaterThanOrEqual(0);
  expect(caja.y + caja.height).toBeLessThanOrEqual(vp.height + 2);

  const cuerpo = page.locator('.modal-body');
  await cuerpo.evaluate(el => { el.scrollTop = el.scrollHeight; });
  await expect(page.locator('#btnWhatsApp')).toBeInViewport();
  await expect(page.locator('.modal-body p', { hasText: 'Los números quedan' })).toBeInViewport();
});

// ── 7. Números vendidos ──────────────────────────────────────────
test('P16 un número vendido se ve gris, tachado, deshabilitado y no se puede elegir', async ({ page }) => {
  await page.route('**/rifa/index.html', async route => {
    const respuesta = await route.fetch();
    const cuerpo = (await respuesta.text())
      .replace('const numerosVendidos = [];', 'const numerosVendidos = [7, 42];');
    await route.fulfill({ response: respuesta, body: cuerpo });
  });

  await irARifa(page);
  const vendido = num(page, 7);
  await expect(vendido).toHaveClass(/vendido/);
  await expect(vendido).toBeDisabled();
  await expect(vendido).toHaveCSS('background-color', GRIS);
  await expect(vendido).toHaveCSS('text-decoration-line', 'line-through');

  await vendido.click({ force: true });
  await expect(page.locator('#listaSeleccionados')).toHaveText('ninguno');
  await expect(page.locator('#btnReservar')).toBeDisabled();

  await num(page, 42).click({ force: true });
  await expect(page.locator('#listaSeleccionados')).toHaveText('ninguno');
});

// ── 8. Acceso desde el sitio principal ───────────────────────────
test('P17 desde el home el enlace "Rifa" del navbar llega a la rifa', async ({ page }) => {
  await page.goto('/');
  const enlace = page.locator('a.nav-link[href="/rifa/index.html"]').first();
  if (!(await enlace.isVisible())) {
    await page.locator('button[data-bs-target="#navMain"], button[data-bs-target="#navbarNav"]').first().click();
    await page.waitForTimeout(600);
    if (!(await enlace.isVisible())) {
      await enlace.click({ force: true });
      await page.waitForURL(/\/rifa\/index\.html/);
      expect(page.url()).toContain('/rifa/index.html');
      return;
    }
  }
  await enlace.click();
  await expect(page).toHaveURL(/\/rifa\/index\.html/);
  await expect(num(page, 0)).toBeVisible();
});

test('P18 la rifa y la salud del servicio responden bien (sin login)', async ({ request }) => {
  for (const ruta of ['/rifa', '/rifa/']) {
    const resp = await request.get(ruta);
    expect(resp.status(), `${ruta} debe responder 200`).toBe(200);
    expect(resp.url(), `${ruta} debe redirigir a index.html`).toContain('/rifa/index.html');
  }
  const salud = await request.get('/api/health/ready');
  expect(salud.status()).toBe(200);
});

test('P19 una ruta estática inexistente responde 404 (no 500)', async ({ request }) => {
  const resp = await request.get('/rifa/no-existe-esto.html');
  expect(resp.status()).toBe(404);
});

// ── 9. Enlaces externos ──────────────────────────────────────────
test('P20 footer: Instagram abre en pestaña nueva y el botón hero existe', async ({ page }) => {
  await irARifa(page);
  const insta = page.locator('footer a[href*="instagram.com/sarichi_crocheting"]');
  await expect(insta).toBeVisible();
  await expect(insta).toHaveAttribute('target', '_blank');
  await expect(page.locator('footer')).toContainText('@sarichi_crocheting');
  await expect(page.locator('footer')).toContainText('31 de Octubre de 2026');
});

test('P21 sticky móvil puede abrir el modal (flujo completo en celular)', async ({ page }) => {
  test.skip(test.info().project.name !== 'mobile', 'solo móvil');
  await irARifa(page);
  await seleccionar(page, 9, 12);

  const sticky = page.locator('#stickyResumen');
  await expect(sticky).toBeVisible();
  await sticky.locator('button', { hasText: 'PAGAR' }).click();

  await expect(page.locator('#modalPago')).toBeVisible();
  await expect(page.locator('#modalNumeros')).toHaveText('09, 12');
  await expect(page.locator('#modalTotal')).toHaveText('$5.000');

  const cuerpo = page.locator('.modal-body');
  await cuerpo.evaluate(el => { el.scrollTop = el.scrollHeight; });
  await expect(page.locator('#btnWhatsApp')).toBeInViewport();
});
