# Datos de pruebas — Bloque 1 (Autenticación)

**Proyecto:** Sarichi Crocheting · **Entorno local:** `.\mvnw.cmd spring-boot:run` · **Producción:** https://sarichi-crocheting.onrender.com

---

## 1. Cuentas semilla (creadas por `DataInitializer`)

| Rol | Correo | Clave |
|---|---|---|
| ADMIN | admin@sarichi.com | 123456 |
| ARTESANA | artesana@sarichi.com | 123456 |
| LOGISTICA | logistica@sarichi.com | 123456 |
| CLIENTE | ana@email.com | 123456 |
| CLIENTE | carlos@email.com | 123456 |

- **MERCADEO y BODEGA no existen**: los casos que los requieren quedan **Bloqueados** (H-26).
- Ningún caso del Bloque 1 altera estas cuentas.

## 2. Cuentas de prueba del bloque

Crear durante la sesión con sufijo `.qa` y **eliminar al terminar**:

| Correo | Uso |
|---|---|
| qa.bloque1.01@test.com | AUTH-01 / AUTH-03 |
| qa.admin.test@test.com | AUTH-15 (rol ADMIN) |
| qa.suspendido@test.com | AUTH-06 |
| qa.inactivo@test.com | AUTH-07 |
| qa.usuariob@test.com | AUTH-11 (víctima del IDOR) |
| qa.masivo.NN@test.com | AUTH-27 (registro masivo) |

Endpoints adicionales de seguridad (solo local): `/api/wishlist/**`, `/api/mensajes/pedido/**`, `/api/personalizador/**` (AUTH-25) y `/actuator`, `/actuator/configprops`, `/actuator/env` (AUTH-26).

Comando de apoyo para borrarlas:

```javascript
db.usuarios.deleteMany({ correo: { $regex: "qa\\..*\\.(test|qa)\\." } })
```

## 3. Rutas del Bloque 1

| Acción | Ruta | Notas |
|---|---|---|
| Login web | `GET/POST /api/web/login` | sesión HTTP |
| Registro web | `GET/POST /api/web/registro` | fuerza rol CLIENTE |
| Logout web | `GET /api/web/logout` | invalida la sesión |
| Login API | `POST /api/auth/login` | devuelve JWT |
| Registro API | `POST /api/auth/register` | acepta `rol` (H-05) |
| Refresh | `POST /api/auth/refresh` | cabecera `Authorization` |
| Logout API | `POST /api/auth/logout` | cabecera `X-User-Id` |
| Recuperar | `POST /api/auth/recuperar` | puede devolver `enlace` (H-06) |
| Restablecer | `POST /api/auth/restablecer` | token de 15 min, un solo uso |
| Perfil | `GET /api/auth/me` · `GET /api/auth/perfil-completo` | |
| Páginas públicas | `/login.html` · `/recuperar.html` · `/restablecer.html` | |

## 4. Comandos de apoyo

PowerShell contra local:

```powershell
# Login
$login = Invoke-RestMethod -Uri http://localhost:8080/api/auth/login -Method Post -ContentType 'application/json' `
  -Body '{"correo":"ana@email.com","contrasena":"123456"}'

# Petición autenticada
Invoke-RestMethod -Uri http://localhost:8080/api/auth/me -Headers @{ Authorization = "Bearer $($login.accessToken)" }

# Refresh
Invoke-RestMethod -Uri http://localhost:8080/api/auth/refresh -Method Post `
  -Headers @{ Authorization = "Bearer $($login.refreshToken)" }

# Registro con rol arbitrario (AUTH-15, solo local)
Invoke-RestMethod -Uri http://localhost:8080/api/auth/register -Method Post -ContentType 'application/json' `
  -Body '{"nombre":"QA Admin","correo":"qa.admin.test@test.com","contrasena":"Abc12345","confirmarContrasena":"Abc12345","rol":"ADMIN"}'

# IDOR de logout (AUTH-11, solo local)
Invoke-RestMethod -Uri http://localhost:8080/api/auth/logout -Method Post -Headers @{ 'X-User-Id' = '<id de otra cuenta>' }
```

## 5. Reglas de ejecución

1. Las pruebas marcadas **Local** no se ejecutan en producción.
2. Antes de cada prueba de seguridad, usar datos propios y reponerlos al terminar.
3. Las capturas se guardan en `evidencias\` con el nombre `<ID>-<paso>.png`.
4. Si una prueba no se puede ejecutar, se marca **Bloqueada** con el motivo, nunca como aprobada.
5. El entorno de producción solo recibe las pruebas de las filas marcadas *Ambos*.

## 6. Bloque 2 — Dashboard

Rutas reales del módulo (todas bajo `/api/web`, control de rol dentro del controlador):

| Dashboard | Ruta web | Roles | Fuente de datos |
|---|---|---|---|
| ADMIN | `/api/web/dashboard/admin` | ADMIN | `DashboardService.obtenerKpis()` |
| ARTESANA | `/api/web/dashboard/artesana` | ARTESANA | `PedidoService.listarTodos()` |
| LOGISTICA | `/api/web/dashboard/logistica` | LOGISTICA | `DespachoService` |
| BODEGA | `/api/web/dashboard/bodega` | BODEGA | `ColorHiloService` + `ProductoService` |
| MERCADEO | `/api/web/dashboard/mercadeo` | MERCADEO | `AnaliticaService` |
| CLIENTE | `/api/web/dashboard/cliente` | CLIENTE | `ProductoService` |
| General | `/api/web/dashboard` | sesión | redirige por rol |

Endpoints REST (exigen JWT + `hasAnyRole`): `GET /api/dashboard/kpis | ventas | categorias | top-productos | pedidos-recientes | despachos-hoy | stock-critico | logistica | produccion`.
Analítica: `GET /api/analitica/metricas | paginas-populares` (MERCADEO/ADMIN); `POST /api/analitica/visita | click | conversion`.

**Estado verificado en código (hallazgos para el Bloque 2):**
- **Sin polling ni auto-refresco** de KPIs (0 `setInterval`): RF-DASH-02 no se cumple.
- **Sin librerías de gráficas**: RF-DASH-03/04/06 no tienen visualización (solo endpoints).
- `pedidos-recientes` y `despachos-hoy` devuelven `List.of()` fijo ("Sprint 4").
- **MERCADEO y BODEGA no tienen cuenta semilla** → DASH-15 y DASH-12 quedan Bloqueados parcialmente.
- `eventosTrafico` no se alimenta desde la web (nadie emite POST); los endpoints de registro requieren JWT.
- Umbral de stock crítico: productos ≤ 2 (`obtenerStockCritico(2)`) fijo; hilos por `stockMetros ≤ stockMinimo`.
- `/actuator/**` real queda tras JWT (solo `/api/actuator/**` está en `permitAll`, pero esa ruta no existe → 404). Ver AUTH-26.
- KPIs "falsos": `ventasAyer=0.0`, `variacionVentas=0.0`, `promedioEntregaDias=3.0` fijos; panel ARTESANA con 3 de 4 tarjetas en `0`.

## 7. Bloque 3 — Catálogo, carrito, checkout y pagos

Rutas reales:

| Acción | Ruta | Notas |
|---|---|---|
| Catálogo REST | `GET /api/productos` | params: categoria, colorId, precioMin/Max, busqueda, pagina=0, tamano=12 |
| Catálogo web | `GET /api/web/tienda` | solo categoria y busqueda; sin paginación |
| Detalle | `GET /api/web/tienda/{id}` · `GET /api/productos/{id}` | |
| Carrito | `GET/POST /api/web/carrito`, `/carrito/agregar`, `/carrito/eliminar/{id}`, `/carrito/vaciar` | vive en **sesión HTTP** (CarritoItem) |
| Checkout | `GET/POST /api/web/checkout` | crea pedido + descuenta stock |
| Mis pedidos | `GET /api/web/mis-pedidos` | |
| Wishlist web | `GET /api/web/wishlist`, `/wishlist/agregar/{productoId}`, `/wishlist/eliminar/{productoId}` | usa usuarioWebId de sesión |
| Wishlist REST | `/api/wishlist/**` | **permitAll** → IDOR |
| Pago REST | `POST /api/pagos/iniciar` · `POST /api/pagos/webhook` | JWT; webhook público |

**Estado verificado en código (hallazgos del Bloque 3):**
- **Carrito no persistente**: solo en sesión; sin colección Mongo; sin validación de stock al agregar.
- **Checkout ignora el método de pago y los datos de contacto**: no crea `Pago` ni redirige a Wompi; la plantilla lo admite.
- **Pago confía el monto del cliente** (`request.getMonto()` sin contrastar con el pedido) → Crítica (CAT-21).
- **Webhook sin verificación de firma** (`wompi.events.secret` sin uso) → Crítica (CAT-22).
- **Correo de confirmación no se envía**: métodos de `EmailService` sin llamadores.
- **RF-CAT-07 productos relacionados: no existe**.
- **Wishlist IDOR**: `/api/wishlist/**` público y sin validar dueño (CAT-30).
- **Notificación de bajada de precio: no implementada** (existe flag/`precioAlAgregar` sin uso).
- Filtro de categoría web en minúsculas vs datos capitalizados (posible 0 resultados); búsqueda y categoría no se combinan; rama de categoría no filtra stock>0.
- Descuento de stock no atómico (sin `@Transactional`) → riesgo de sobreventa.
- Redirect post-pago apunta a `/web/mis-pedidos` (ruta inexistente; la real es `/api/web/mis-pedidos`).

**Datos de prueba:** catálogo semilla (productos ACTIVO con stock); para pruebas destructivas crear productos/pedidos propios. **Sin WOMPI_PUBLIC_KEY** el pago es mock. **CAT-28** (BODEGA) queda parcialmente bloqueado sin cuenta BODEGA.

## 8. Bloque 4 — Personalizador visual

Rutas reales:

| Acción | Ruta | Notas |
|---|---|---|
| Personalizador (vista) | `GET /api/web/personalizador` | pública; carga catálogo de productos base |
| Solicitud personalizada (vista) | `GET/POST /api/web/pedido-personalizado` | requiere login; formulario alternativo |
| Guardar diseño | `POST /api/personalizador/guardar` | body: productoId, coloresSeleccionados, talla, mensajeBordado, usuarioId |
| Listar por usuario | `GET /api/personalizador/usuario/{usuarioId}` | |
| Compartir | `GET /api/personalizador/compartir/{id}` | público; 404 si no existe |
| Cotizar (artesana) | `POST /api/web/artesana/cotizar/{id}` | estado → COTIZADO |

**Estado verificado en código (hallazgos del Bloque 4):**
- **Colores y tallas hardcodeados** en `personalizador/index.html`; no provienen de `coloresHilo` ni de la configuración del producto (PER-02, PER-06).
- **Vista previa (canvas 2D) es un gatito fijo**: no cambia con el producto base; solo colores/mensaje. La talla no altera el dibujo (PER-04).
- **Precio duplicado y divergente**: el frontend usa base fija `25000`; el backend usa `producto.precioBase` → pueden diferir (PER-09, PER-11).
- **Fórmula backend** (`PersonalizadorService.calcularPrecio`): Grande +15000, Extra Grande +30000, mensaje +5000. Coincide con el frontend salvo la base (PER-10).
- **Mensaje bordado**: el límite de 20 es solo `maxlength` en el HTML; **el backend no valida longitud** (PER-07, PER-08).
- **`/api/personalizador/**` es `permitAll`** (SecurityConfig): guardar sin autenticación crea el diseño con `usuarioId` del body (null/anónimo) y **listar por usuario es IDOR** (PER-13, PER-15, PER-17).
- **Guardar sin producto** permitido por API (`productoId` null → `precioCalculado` null) (PER-14).
- **Botón "Guardar y añadir al carrito" solo guarda**: no integra carrito (PER-22).
- **Cotización sin validar coherencia** con `precioCalculado` (PER-23).
- Los colores van al backend con claves `cuerpo`/`accesorios`; el flujo del formulario alternativo (`pedido-personalizado`) mete los colores en la clave `descripcion` (inconsistencia menor).

**Datos de prueba:** catálogo semilla para producto base; crear diseños propios. Para PER-08/13/14/17 usar `Invoke-RestMethod` contra local con/ sin sesión y verificar en `diseniosGuardados` (Mongo).

## 9. Bloque 5 — Pedidos personalizados y chat

Rutas reales:

| Acción | Ruta | Notas |
|---|---|---|
| Solicitud personalizada | `GET/POST /api/web/pedido-personalizado` | multipart en el form; el controller no lee archivos |
| Chat (vista) | `GET /api/web/chat/{pedidoId}` | requiere login; sin validar pertenencia |
| Chat (STOMP) | endpoint `/ws-chat` (SockJS); app `/app/chat/{id}`; broker `/topic/chat/{id}` | `/ws-chat/**` permitAll |
| Crear pedido | `POST /api/pedidos` | isAuthenticated; descuenta stock |
| Mis pedidos | `GET /api/pedidos/mis-pedidos` · `GET /api/web/mis-pedidos` | |
| Listar todos | `GET /api/pedidos` | ADMIN/ARTESANA; devuelve TODOS |
| Pedido por id | `GET /api/pedidos/{id}` | ADMIN/ARTESANA (el dueño CLIENTE no accede) |
| Cambiar estado (REST) | `PUT /api/pedidos/{id}/estado` | ADMIN/ARTESANA |
| Cambiar estado (web) | `POST /api/web/pedidos/{id}/estado` | |
| Actividades producción | `/api/actividades/**` | ARTESANA/ADMIN |
| Fotos del proceso | `POST /api/actividades/{id}/fotos` (JSON urlFoto) · `GET .../fotos` | GET **retorna List.of()** |

**Estado verificado en código (hallazgos del Bloque 5):**
- **RF-PED-01 — fotos de referencia NO se procesan**: el form es `multipart/form-data` con `name="fotosReferencia"`, pero `procesarPedidoPersonalizado` no tiene parámetros `MultipartFile` → los archivos se descartan. Además SRS exige máx **5** y la UI dice máx **3** (PED-02, PED-03).
- **RF-PED-02 — chat sin autenticación ni autorización**: `/ws-chat/**` es `permitAll`; `remitenteId` y `remitenteRol` vienen del payload del cliente → **suplantación** (Crítica, PED-05); el historial `GET /api/web/chat/{pedidoId}` no valida pertenencia (PED-06).
- **RF-PED-03 — aprobación del cliente no existe**: `cotizar()` solo fija precio/tiempo y estado `COTIZADO`; no hay estado `Aprobado`, ni vista/acción de aprobación ni "proponer cambios" (PED-09).
- **RF-PED-04 — timeline difiere del SRS**: código `SOLICITADO→EN_PRODUCCION→LISTO→DESPACHADO→ENTREGADO`; SRS `Solicitado→Cotizado→Aprobado→En Producción→Listo→Enviado→Entregado` (faltan Cotizado/Aprobado/Enviado; "DESPACHADO" vs "Enviado") (PED-12). Transición a `LISTO` solo valida producción si existe actividad (PED-13).
- **RF-PED-05 — anticipo/saldo 50 % no implementado** (H-23): no hay dos pagos ni flujo Wompi (PED-15, PED-16).
- **RF-PED-06 — galería del proceso incompleta**: `POST /api/actividades/{id}/fotos` espera **JSON con `urlFoto`**, no un archivo (sin Cloudinary en este flujo) (PED-18); `GET .../fotos` **retorna `List.of()` fijo** → el cliente nunca ve fotos por API (PED-19); la vista `/api/web/artesana/proceso` solo **muestra** la galería, **sin formulario de subida** (PED-20); sin validar pertenencia de la actividad (PED-26).
- **RF-PED-07 — historial sin comprobantes ni fotos**: `pedidos/listar.html` solo muestra el badge de estado; sin fotos del proceso, mensajes ni comprobantes de pago (PED-22). `listarTodos()` expone todos los pedidos a cualquier ARTESANA (PED-24).

**Datos de prueba:** pedidoId existente (crear uno vía checkout o `POST /api/pedidos`); dos sesiones (CLIENTE + ARTESANA) para el chat. Para PED-05/17/18/26 usar un cliente STOMP y `Invoke-RestMethod`; verificar en las colecciones `mensajes`, `actividadesProduccion` y `pedidos`.

## 10. Bloque 6 — Inventario y bodega

Rutas reales:

| Acción | Ruta | Notas |
|---|---|---|
| Listar colores | `GET /api/colores-hilo` | **público** |
| Color por id | `GET /api/colores-hilo/{id}` | público |
| Críticos | `GET /api/colores-hilo/criticos` | ADMIN/BODEGA |
| Crear/actualizar | `POST|PUT /api/colores-hilo` | ADMIN/BODEGA (`@Valid`) |
| Eliminar (lógico) | `DELETE /api/colores-hilo/{id}` | solo ADMIN |
| Dashboard bodega | `GET /api/web/dashboard/bodega` | BODEGA |
| Hilos (vista) | `GET /api/web/bodega/hilos` | BODEGA/ADMIN; modales entrada/salida |
| Entrada/salida hilo | `POST /api/web/bodega/hilos/{entrada|salida}/{id}` | BODEGA/ADMIN |
| Inventario productos | `GET /api/web/bodega/inventario` | BODEGA/ADMIN |
| Entrada/salida producto | `POST /api/web/bodega/productos/{entrada|salida}/{id}` | BODEGA/ADMIN |
| Movimientos | `GET /api/web/bodega/movimientos` | BODEGA/ADMIN |
| Exportar | `GET /api/web/bodega/exportar/{pdf|excel}` · `GET /api/reportes/inventario/{excel|datos}` | |

**Estado verificado en código (hallazgos del Bloque 6):**
- **RF-BOD-01** — CRUD de hilos funciona (REST + web). `ColorHiloDTO` valida nombre/codigoHex (`@NotBlank`); `stockMinimo` por defecto **5.0**. Listado `GET` **público** (expone inventario). **Sin cuenta BODEGA semilla** (H-26) → dashboard solo accesible con ADMIN.
- **RF-BOD-02** — entradas/salidas funcionan y generan **auditoría** (`MovimientoBodega` con fecha, tipo, cantidad, responsable, stockAnterior/Posterior). **Sin endpoints REST** (solo rutas web). Salida con stock insuficiente se rechaza.
- **RF-BOD-03** — detección de críticos funciona (`findCriticos`: `stockMetros <= stockMinimo`) y se muestra en dashboard. **Sin correo de alerta** (EmailService sin llamadores, H-17); sin umbral global configurable.
- **RF-BOD-04** — entradas/salidas de producto funcionan y registran movimiento (itemTipo PRODUCTO). La venta descuenta stock (`PedidoService`) pero **sin operación atómica** → riesgo de sobreventa.
- **RF-BOD-05** — **Excel incompleto**: `generarReporteInventarioExcel` solo escribe "Total de productos: N" en una celda (sin filas por producto). `PDF` lista los productos ACTIVO con estado CRITICO/OK. `GET /api/reportes/inventario/datos` devuelve **valores hardcodeados en cero** (productosAgotados=0, stockCritico=0, valorTotalInventario=0.0). **Sin filtros** por categoría/estado (SRS).

**Datos de prueba:** crear colores de prueba vía API; para críticos, un color con `stockMetros <= stockMinimo`. Entradas/salidas web requieren sesión BODEGA/ADMIN (usar `admin@sarichi.com`). Verificar en `coloresHilo` y `movimientosBodega`.

## 11. Bloque 7 — Logística y despachos

Rutas reales:

| Acción | Ruta | Notas |
|---|---|---|
| Despachos pendientes | `GET /api/despachos/pendientes` | LOGISTICA/ADMIN; pedidos LISTO sin despacho |
| Despachos de hoy | `GET /api/despachos/hoy` · `GET /api/despachos` | LOGISTICA/ADMIN; GET devuelve solo hoy |
| Crear despacho | `POST /api/despachos` | genera guía (mock) |
| Seguimiento (REST) | `GET /api/despachos/seguimiento/{guia}` | LOGISTICA/ADMIN/**CLIENTE** |
| Actualizar estado | `PUT /api/despachos/{id}/estado` | LOGISTICA/ADMIN |
| Estadísticas | `GET /api/despachos/estadisticas` | LOGISTICA/ADMIN |
| Logística (web) | `GET /api/web/logistica/despachos` | LOGISTICA/ADMIN |
| Generar guía (web) | `POST /api/web/logistica/despachos/generar-guia/{pedidoId}` | |
| Guías (historial) | `GET /api/web/logistica/guias` | despachos con número de guía |
| Seguimiento (web) | `GET /api/web/logistica/seguimiento/{guia}` | **solo LOGISTICA/ADMIN** |

**Estado verificado en código (hallazgos del Bloque 7):**
- **RF-LOG-01 — zonas de envío sin uso**: `Configuracion.ZonaEnvio` (ciudad, tarifa, diasEstimados) existe en la entidad, pero **no hay UI ni endpoint** para gestionarlas y **no se consumen**; `TransportadoraService.obtenerTarifas` (mock) **no tiene llamadores**; el checkout no aplica tarifa por zona.
- **RF-LOG-02 — sin orden por ciudad/transportadora**: el panel lista pedidos LISTO sin despacho, pero **no los ordena/agrupa** por ciudad ni transportadora. La ciudad se extrae partiendo `direccionEnvio` por comas (frágil; sin comas → "N/A").
- **RF-LOG-03 — guía mock y estado DESPACHADO**: `generarGuia` es **mock** (`app.transportadora.mock-enabled=true`); la real lanza excepción. Requiere pedido `LISTO`. El pedido pasa a **`DESPACHADO`** (SRS pide **`Enviado`**). Sin bloqueo explícito de guía duplicada.
- **RF-LOG-04 — seguimiento del cliente incompleto**: `consultarEstado` es **mock** (estado alterna por hash de la guía; fecha fija 2026-05-26). El cliente solo puede seguir por el **endpoint REST**; la **vista web está restringida a LOGISTICA/ADMIN**, sin panel de seguimiento en el cliente. Sin validar pertenencia de la guía.
- **RF-LOG-05 — historial**: `/api/web/logistica/guias` lista despachos con guía; actualizar a ENTREGADO propaga al pedido. Pero `GET /api/despachos` devuelve **solo hoy** (no historial completo). Estadística `promedioEntregaDias=3.0` **fija**.

**Datos de prueba:** pedido en estado `LISTO` para despachar; cuenta `logistica@sarichi.com`. Guías mock con formato `GU-000XXX`. Verificar en `despachos` y `pedidos`.

## 12. Bloque 8 — Mercadeo, blog, galería y retos

Rutas reales:

| Acción | Ruta | Notas |
|---|---|---|
| Blog público | `GET /api/web/blog` · `/blog/{slug}` · `GET /api/blog/publicados` · `/{slug}` · `/categoria/{cat}` · `/recientes` | |
| Crear/editar/publicar artículo | `POST/PUT /api/blog` · `PUT /api/blog/{id}/publicar` | MERCADEO/ADMIN |
| Eliminar artículo | `DELETE /api/blog/{id}` | solo ADMIN (lógico → ARCHIVADO) |
| Galería pública | `GET /api/web/galeria` · `GET /api/galeria/colecciones` · `/recientes` · `/coleccion/{slug}` | |
| Crear colección / foto | `POST /api/galeria/colecciones` · `/colecciones/{id}/fotos` | MERCADEO/ADMIN |
| Vincular / like / borrar | `PUT /api/galeria/fotos/{id}/vincular-producto` · `POST /fotos/{id}/like` · `DELETE /fotos/{id}` | like público; borrar ADMIN |
| Subir imagen (web) | `POST /api/web/mercadeo/galeria/subir` | MultipartFile → Cloudinary |
| Retos | `GET /api/retos/activos` · `GET /api/retos` (MERCADEO) · `POST /api/retos` · `/{id}/participar` · `/{id}/participaciones` · `/votar` · `/{id}/finalizar` | |
| Mercadeo (web) | `/api/web/mercadeo/blog` · `/galeria` · `/retos` · `/analiticas` | MERCADEO/ADMIN |
| Analítica | `POST /api/analitica/{visita\|click\|conversion}` · `GET /metricas` · `/paginas-populares` | GET MERCADEO/ADMIN |
| Reportes campañas | `/api/web/mercadeo/reportes/exportar/{pdf\|excel}` | reutilizan otros reportes |

**Estado verificado en código (hallazgos del Bloque 8):**
- **RF-MKT-01** — CRUD de blog funciona (BORRADOR → PUBLICADO, slug único, SEO, categorías). **Sin publicación programada** (publica inmediato, MKT-03) y **sin editor enriquecido ni formulario de creación web** (solo REST, MKT-04). `obtenerPorSlug` incrementa visitas.
- **RF-MKT-02** — colecciones/fotos/vínculo/like funcionan. La **galería pública no tiene estilo masonry** (grid simple, MKT-11). Subida web depende de **Cloudinary** (credenciales).
- **RF-MKT-03** — endpoints de analítica existentes, pero **ninguna vista emite eventos** → `eventoTrafico` vacío (MKT-15); `MetricasTraficoDTO` **sin tasa de rebote** (MKT-16). Sin cuenta MERCADEO semilla (MKT-24).
- **RF-MKT-04** — crear/participar/votar funcionan. **Sin página pública de retos** (MKT-20); **votación ilimitada** (sin voto por usuario, MKT-21); **finalizar desde la web falla** (pasa `ganadorId=null` → excepción, MKT-22).
- **RF-MKT-05** — el **PDF de campañas reutiliza el reporte de ventas** y el **Excel reutiliza el de inventario**; no exporta métricas de campañas (MKT-23).

**Datos de prueba:** retos activos requieren fechas que engloben "hoy"; para MKT-14 usar `Invoke-RestMethod` con sessionId/fuente. Verificar en `articulosBlog`, `coleccionGaleria`, `fotoGaleria`, `retoMensual`, `participacionReto`, `eventoTrafico`.

## 13. Bloque 9 — Administración y configuración

**Rutas web (prefijo `/api/web`):**
- `/dashboard/admin` (RF-ADM-01) — KPIs vía `DashboardService.obtenerKpis()`.
- `/productos`, `/productos/nuevo`, `/productos/guardar`, `/productos/editar/{id}`, `/productos/eliminar/{id}` (RF-ADM-02) — CRUD; crear/editar permiten ARTESANA, eliminar solo ADMIN.
- `/pedidos` (RF-ADM-03) — listado de todos los pedidos.
- `/usuarios`, `/usuarios/nuevo`, `/usuarios/guardar`, `/usuarios/editar/{id}`, `/usuarios/estado/{id}`, `/usuarios/eliminar/{id}` (RF-ADM-05) — solo ADMIN; roles ADMIN/ARTESANA/LOGISTICA/BODEGA/MERCADEO.
- `/admin/reportes`, `/admin/reportes/exportar/pdf`, `/admin/reportes/exportar/excel` (RF-ADM-06).
- `/admin/configuracion` (RF-ADM-07).

**Rutas API:** `GET/POST/PUT/DELETE /api/productos` (ADMIN salvo GET); `GET /api/productos/admin/todos`, `GET /api/productos/stock-critico`; `GET/PUT /api/configuracion` (GET público, PUT ADMIN); `GET /api/reportes/ventas/pdf`, `/inventario/excel`, `/ventas/datos`, `/inventario/datos`.

**Estado verificado en código (hallazgos):**
- **RF-ADM-01/02** — dashboard admin usa los KPIs de `DashboardService` (ya señalados como frágiles en Bloque 2). CRUD de productos funcional (web + REST).
- **RF-ADM-02** — **no existe importación masiva de productos por CSV** (ni endpoint ni formulario) pese a que el SRS la exige (ADM-09, H-27).
- **RF-ADM-03** — el listado `/pedidos` **no tiene filtros** por estado/cliente/fecha (ADM-11); transiciones de estado vía `/api/web/pedidos/{id}/estado` o `PUT /api/pedidos/{id}/estado`.
- **RF-ADM-04** — **no existe gestión de clientes**: `/usuarios` filtra fuera a los CLIENTE y no hay historial ni segmentación (nuevo/recurrente/VIP); `umbralVIPCompras` está en config pero no se usa (ADM-14/15, H-28).
- **RF-ADM-05** — CRUD de usuarios internos funcional; protege CLIENTE (no editar/eliminar/cambiar estado); roles incluyen BODEGA/MERCADEO aunque no tengan cuenta semilla (H-26).
- **RF-ADM-06** — reportes PDF (iText) y Excel (POI) disponibles; el set de reportes es limitado.
- **RF-ADM-07** — `ConfiguracionController` expone GET público y PUT ADMIN; `configuracion.html` solo muestra campos readonly (tienda/correo/moneda), **sin UI de zonas/tarifas/transportadoras**; `zonaEnvio` sin uso (ADM-28, H-29).

**Cuentas:** ADMIN `admin@sarichi.com` / `123456`; ARTESANA `artesana@sarichi.com` / `123456`; CLIENTE `ana@email.com` / `123456`. Crear usuarios QA (BODEGA/MERCADEO) desde ADM-17.

**Datos de prueba:** verificar en `productos`, `pedidos`, `usuarios`, `configuracion`.

## 14. Bloque 10 — Transversales (RF-G)

**Rutas API:** `POST /api/auth/registro`, `POST /api/auth/login`, `POST /api/auth/refresh`, `POST /api/auth/recuperar`; `GET /api/reportes/ventas/pdf`, `GET /api/reportes/inventario/excel`; `GET /api/auth/me`, `GET /api/auth/perfil-completo`.

**Estado verificado en código (hallazgos):**
- **RF-G-01** — registro/login con correo funcionan; el **refresh token se persiste en `usuarios.refreshToken`** y `POST /api/auth/refresh` lo renueva. **Google OAuth2 está deshabilitado** (TRV-03, H-04).
- **RF-G-02** — `EmailService` define los 5 eventos (pedido, estado, mensaje, pago, stock), pero **solo `enviarCorreoRecuperacion` tiene llamador**; el resto son métodos muertos (`enviarConfirmacionPedido`, `enviarEstadoPedidoActualizado`, `enviarConfirmacionPago`, `enviarAlertaStockCritico`) y `ChatService` no notifica por correo (TRV-04..09, H-17).
- **RF-G-03** — **no existe búsqueda global**: ni endpoint `/buscar`, ni `@TextIndexed`/`TextCriteria`, ni campo de búsqueda unificada (TRV-10..12, H-22).
- **RF-G-04** — **no existe auditoría**: ni colección `bitacoraAuditoria`, ni captura de usuario/IP/datos previos-nuevos (TRV-13..15, H-18).
- **RF-G-05** — exportación PDF (iText) y Excel (POI) de ventas/inventario; **no hay exportación dedicada de pedidos**; rutas restringidas por rol (TRV-16..19).
- **RF-G-06** — `Usuario.modoOscuro` existe (default `false`, expuesto en DTO) pero **no hay UI ni endpoint** para cambiarlo ni CSS de tema oscuro (TRV-20..22, H-30).

**Datos de prueba:** verificar en `usuarios`, `pedidos`, `pagos`, `colorHilo`. Para notificaciones revisar logs del servidor (SMTP sin credenciales).

## 15. Bloque 11 — NFR / despliegue (RNF-G)

**Rutas:** `GET /actuator/health`, `GET /actuator/info`, `GET /actuator/mappings`, `GET /api/health/db`, `GET /api/health/ready`, `GET /api/health/info`. Producción: `https://sarichi-crocheting.onrender.com`.

**Estado verificado en código (hallazgos):**
- **RNF-G-01** — Actuator expone `health,info,mappings` con `show-details=always`; `HealthCheckController` aporta `/api/health/db` y `/ready`. **No hay monitoreo de uptime configurado**; la disponibilidad mensual (NFR-02) requiere herramienta.
- **RNF-G-02** — sin configuración de pruebas de carga; requiere JMeter/k6 (NFR-03).
- **RNF-G-03** — 48 plantillas incluyen meta viewport; verificable a 360px con DevTools (NFR-04).
- **RNF-G-04** — `server.error.include-stacktrace=always` e `include-message=always` **exponen detalles internos** (NFR-05, H-08); hay rutas `permitAll` (H-19) y sin rate limiting (H-21); análisis completo requiere OWASP ZAP.
- **RNF-G-05** — `BCryptPasswordEncoder(12)` correcto (NFR-07); el **JWT se firma con HS256** (`Keys.hmacShaKeyFor`) en vez de RS256 (NFR-08, H-02); TLS provisto por Render (NFR-09).
- **RNF-G-06** — **no hay consentimiento en `/registro` ni eliminación de cuenta** (NFR-10, H-31).
- **RNF-G-07** — uso limitado de ARIA/role (~10 atributos); requiere auditoría Lighthouse (NFR-11).
- **RNF-G-08** — ~23 archivos de test (≈18 clases); **sin JaCoCo/SonarCloud/ESLint/Prettier**; requiere herramienta (NFR-12).
- **RNF-G-09** — política de backup depende de Atlas; requiere consola Atlas (NFR-13).

**Datos de prueba:** ver `usuarios.passwordHash` (BCrypt), header del JWT, respuesta de error 500, política de Atlas.

## 16. Estado de servicios externos

| Servicio | Estado | Efecto en el bloque |
|---|---|---|
| MongoDB Atlas | Disponible | Base de datos común para local y producción |
| SMTP (Gmail) | Sin credenciales | Los correos van a logs; explicado en AUTH-12 |
| Wompi | Sin llaves en local | No aplica al Bloque 1 |
| Transportadora | `mock-enabled=true` | No aplica al Bloque 1 |
| Google OAuth2 | Deshabilitado | AUTH-14 se registra como no implementado |
