# Fase 2 — Inventario de Requisitos, Inconsistencias, Dependencias y Riesgos

**Proyecto:** Sarichi Crocheting · **Documento base:** `00a-analisis-srs.md` (Fase 1)
**Alcance:** 67 RF + 9 RNF + 16 HU · **Fecha:** 19/10/2026 · **Versión:** 1.0

> **Reglas de clasificación**
> - **Estado real:** `I` Implementado · `P` Parcial · `N` No implementado · `D` Implementado pero **difiere** del SRS · `?` Por verificar en código.
> - **Riesgo:** Crítico / Alto / Medio / Bajo (impacto de la brecha o ambigüedad sobre las pruebas).
> - El estado real se marca cuando hubo verificación de código; en lo no verificado se indica `?` y se anota la acción de verificación.
> - Las incidencias consolidadas se codifican `C-xx` (contradicción), `A-xx` (ambigüedad), `G-xx` (vacío), `D-xx` (dependencia) y se referencian al hallazgo `H-xx` cuando ya existen.

---

## 1. Resumen cuantitativo

| Métrica | Valor |
|---|---|
| RF inventariados | 67 |
| RNF inventariados | 9 |
| HU inventariadas | 16 (5 épicas) |
| Contradicciones SRS↔SRS o SRS↔implementación | 12 |
| Ambigüedades | 12 |
| Vacíos del SRS | 7 |
| Dependencias críticas identificadas | 8 |
| RF con estado real **D** (difieren) | verificado en Auth + integraciones |
| RF con estado `?` pendientes de verificación en código | mayoría de bloques 2–9 |

---

## 2. Inventario detallado por módulo

### 3.0 Autenticación y Login

| Código | Requisito | Descripción (literal resumida) | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|---|
| RF-AUTH-01 | Registro de Usuario | Nombre, correo, contraseña (BCrypt factor 12), rol asignado; envía correo de verificación con token | **C-07:** "rol asignado" + "selector de rol (si aplica)" contradice un registro público; no define política de contraseñas | `usuarios`, EmailService, BCrypt | **D** — permite elegir rol de escalada; sin verificación por correo (**H-05**, **H-13**) | Crítico |
| RF-AUTH-02 | Login correo/contraseña | JWT access **15 min** + refresh **7 días** guardado en MongoDB | **C-04:** duración real 1 h / 1 d; **C-03:** RS256 vs HS256 | usuarios, JwtTokenProvider | **D** — 1 h/1 d, HS256 (**H-02**, **H-03**) | Medio |
| RF-AUTH-03 | Login Google OAuth2 | Login federado; guarda `googleId` | ninguno en el SRS | Google Cloud, `usuarios.googleId` | **N** — `oauth2Login` comentado (`SecurityConfig.java:142`) (**H-04**) | Alto |
| RF-AUTH-04 | Renovación de token | `/auth/refresh` valida refresh en MongoDB y emite nuevo access | **C-10:** ruta sin `/api`; no define expiración del nuevo token ni rotación | JwtTokenProvider, usuarios | **D** — existe `/api/auth/refresh`; verificar rotación | Medio |
| RF-AUTH-05 | Cierre de sesión | `/auth/logout` invalida refresh en MongoDB (**blacklist**) y limpia cookies | no define si el access token en curso queda inválido | usuarios, cookies | **D** — endpoint público + `X-User-Id` sin validar (IDOR); no invalida el access token vivo (**H-07**) | Alto |
| RF-AUTH-06 | Recuperación de contraseña | Enlace temporal (15 min) por correo; Spring MailSender | no define si el token es de un solo uso | EmailService, usuarios | **D** — sin SMTP la API devuelve el enlace/token en la respuesta (**H-06**, **H-17**) | Crítico |
| RF-AUTH-07 | Control de acceso por rol | 6 roles: CLIENTE, ARTESANA, ADMIN, MERCADEO, LOGISTICA, BODEGA | no define matriz completa ruta↔rol | SecurityConfig | **P** — no existen cuentas MERCADEO/BODEGA; hay endpoints `permitAll` sensibles (**H-19**, **H-26**) | Crítico |

### 3.1 Dashboard

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-DASH-01 | Dashboard por rol | Rutas del SRS (`/dashboard`, `/pedidos`, …) no son las reales | usuarios.rol, WebController | **D** — redirección real por `/api/web/dashboard/{rol}` | Medio |
| RF-DASH-02 | KPIs en tiempo real | **A-12:** "tiempo real" pero definido como polling cada 30 s; **HU-D1** exige < 2 s | pedidos, pagos | ? | Medio |
| RF-DASH-03 | Ventas por período | no define rangos ni zona horaria | pagos, pedidos | ? | Bajo |
| RF-DASH-04 | Ventas por categoría | no define "categoría" origen (producto o hilo) | productos.categoria | ? | Bajo |
| RF-DASH-05 | Últimos pedidos | no define cuántos ni estado | pedidos | ? | Bajo |
| RF-DASH-06 | Top productos | no define período ni "top N" | pedidos, productos | ? | Bajo |
| RF-DASH-07 | Alertas stock crítico | **C-05:** "≤ 2 unidades" contradice HU-D3 (5 m) y RF-BOD-03 (umbral configurable) | productos, coloresHilo, configuracion | ? | Alto |
| RF-DASH-08 | Despachos del día | solapa con **RF-LOG-02** (mismo panel) | despachos | ? | Bajo |
| RF-DASH-09 | Métricas de tráfico | **G-05:** colección de eventos de tráfico no declarada en el SRS | (colección no modelada) | ? | Alto |
| RF-DASH-10 | Accesos rápidos por rol | no define acciones habilitadas por rol | todos | ? | Bajo |

### 3.2 Catálogo y Tienda

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-CAT-01 | Catálogo de productos | no define paginación ni orden por defecto | productos | ? | Bajo |
| RF-CAT-02 | Filtros y búsqueda | solapa con **RF-G-03** búsqueda global; no define campos de filtro | productos | ? | Medio |
| RF-CAT-03 | Detalle del producto | no define galería ni variantes | productos, fotosUrls | ? | Bajo |
| RF-CAT-04 | Carrito de compras | no define persistencia (sesión vs BD) ni expiración | `/api/carrito` | ? | Medio |
| RF-CAT-05 | Proceso de pago | Wompi (tarjeta/PSE/Nequi) + correo de confirmación; no define manejo de fallos/reintentos | Wompi, EmailService, pagos | **D** — llave Wompi vacía → pago simulado; correo solo en logs (**H-15**, **H-17**) | Alto |
| RF-CAT-06 | Gestión de inventario | **C-05:** "baja de 2 unidades" contradice RF-BOD-03 y HU-D3 | productos, coloresHilo | ? | Alto |
| RF-CAT-07 | Productos relacionados | no define criterio de relación | productos | ? | Bajo |
| RF-CAT-08 | Wishlist | notificación al bajar el precio; **G-01:** colección `wishlist` no declarada | wishlist, productos | **D** — endpoints `/api/wishlist/**` `permitAll` (**H-19**) | Alto |

### 3.3 Personalizador Visual

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-PER-01 | Selector de colores | mínimo **2 zonas** independientes; no define si aplica a colores de catálogo | coloresHilo | ? | Medio |
| RF-PER-02 | Vista previa en tiempo real | **< 300 ms**; no define navegador/SO de referencia | frontend | ? | Medio |
| RF-PER-03 | Selector de tamaño | **A-02:** no define las tallas válidas | productos | ? | Medio |
| RF-PER-04 | Mensaje bordado | máx. **20 caracteres**; no define caracteres permitidos (emojis/acentos) | pedidosPersonalizados | ? | Bajo |
| RF-PER-05 | Precio dinámico | **A-01:** no define la fórmula base+tamaño+colores+mensaje | productos, configuracion | ? | Alto |
| RF-PER-06 | Guardar diseño | **G-04:** colección de diseños guardados no declarada | (colección no modelada) | ? | Medio |
| RF-PER-07 | Compartir diseño | no define si el enlace es público ni su caducidad | personalizador | **D** — endpoints `/api/personalizador/**` `permitAll` (**H-19**) | Medio |

### 3.4 Pedidos Personalizados

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-PED-01 | Solicitud de pedido | máx. **5** fotos de referencia; no define formatos/tamaño | pedidosPersonalizados | ? | Bajo |
| RF-PED-02 | Chat artesana-cliente | **A-09:** SRS dice WebSocket+STOMP tiempo real; verificar polling | mensajesPedido | ? (verificar) | Medio |
| RF-PED-03 | Cotización formal | no define documento ni validez de la cotización | pedidosPersonalizados | ? | Medio |
| RF-PED-04 | Seguimiento de estado | timeline de 7 estados; **C-08** el paso "Enviado" depende del anticipo | pedidosPersonalizados, pagos | **D** — estado real almacenado como `String` sin enum (**H-25**) | Medio |
| RF-PED-05 | Pago en dos partes | anticipo **50 %** + saldo **50 %**; no define qué pasa si no se paga el saldo | Wompi, pagos | **N** — no implementado (**H-23**) | Alto |
| RF-PED-06 | Galería del proceso | no define quién sube las fotos (artesana) ni moderación | galeria, mensajesPedido | ? | Bajo |
| RF-PED-07 | Historial de pedidos | solapa con RF-ADM-03 (vista admin) | pedidos | ? | Bajo |

### 3.5 Inventario y Bodega

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-BOD-01 | Registro de hilos | no define unidad base (metros vs unidades) | coloresHilo | ? | Bajo |
| RF-BOD-02 | Entradas y salidas | no define ajustes negativos ni reversos | inventarioMovimientos | ? | Medio |
| RF-BOD-03 | Alerta de stock mínimo | **C-05:** "umbral configurado" contradice "≤2" y "5 m"; correo + dashboard | configuracion, EmailService | ? | Alto |
| RF-BOD-04 | Inventario de productos | no define sincronización stock hilo ↔ producto | productos, coloresHilo | ? | Medio |
| RF-BOD-05 | Reporte de inventario | **A-06:** no define formato ni columnas | productos, coloresHilo | ? | Medio |

### 3.6 Logística y Despachos

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-LOG-01 | Gestión de zonas de envío | **A-03:** no define tarifas ni ciudades; solapa con RF-ADM-07 | configuracion.zonaEnvio | ? | Medio |
| RF-LOG-02 | Panel de despachos del día | solapa con **RF-DASH-08** | despachos | ? | Bajo |
| RF-LOG-03 | Generación de guía | consulta API transportadora; guarda número y pasa pedido a "Enviado" | TransportadoraService | **D** — transportadora en mock (**H-16**) | Alto |
| RF-LOG-04 | Seguimiento de envíos | no define integración de tracking real | despachos | ? | Medio |
| RF-LOG-05 | Historial de despachos | no define filtros/exportación | despachos | ? | Bajo |

### 3.7 Mercadeo y Analíticas

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-MKT-01 | Blog y tutoriales | editor enriquecido, SEO, **publicación programada**; no define sanitización del HTML | articulosBlog | ? | Medio |
| RF-MKT-02 | Galería y colecciones | **G-06:** `idColeccion` sin colección de colecciones declarada | galeria | ? | Medio |
| RF-MKT-03 | Métricas de tráfico | solapa con **RF-DASH-09**; **G-05:** sin colección de eventos | (no modelada) | ? | Alto |
| RF-MKT-04 | Reto mensual | **G-03:** colección de retos/participaciones no declarada | (no modelada) | ? | Medio |
| RF-MKT-05 | Reportes de campañas | no define métricas de campaña | (no modelada) | ? | Medio |

### 3.8 Administración

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-ADM-01 | Dashboard de ventas | solapa con RF-DASH-03/04 | pedidos, pagos | ? | Bajo |
| RF-ADM-02 | Gestión de productos | importación masiva **CSV**; **A-07:** no define columnas/validaciones | productos | ? | Medio |
| RF-ADM-03 | Gestión de pedidos | no define cambios de estado permitidos | pedidos | ? | Medio |
| RF-ADM-04 | Gestión de clientes | no define eliminar (Ley 1581) vs desactivar | usuarios | ? | Medio |
| RF-ADM-05 | Usuarios del sistema | alta/baja de roles internos | usuarios | **P** — faltan cuentas MERCADEO/BODEGA (**H-26**) | Medio |
| RF-ADM-06 | Reportes exportables | PDF (iText) + Excel (Apache POI); no define set de reportes | pedidos, inventario | ? | Medio |
| RF-ADM-07 | Configuración de envíos | solapa con **RF-LOG-01** | configuracion | ? | Bajo |

### 8.1 Requisitos funcionales globales

| Código | Requisito | Ambigüedad / Contradicción | Dependencias | Estado real | Riesgo |
|---|---|---|---|---|---|
| RF-G-01 | Autenticación | transversal; ver RF-AUTH-* | JWT | **P** | Alto |
| RF-G-02 | Notificaciones | **A-08:** no define canales ni eventos; correo depende de SMTP | EmailService | **D** — correo solo en logs (**H-17**) | Alto |
| RF-G-03 | Búsqueda global | no define alcance (productos/blog/usuarios) | todas | **N** — no implementada (**H-22**) | Medio |
| RF-G-04 | Auditoría | `bitacoraAuditoria` (usuario, IP, datos previos/nuevos); no define eventos auditados | (colección no implementada) | **N** — sin entidad ni interceptor (**H-18**) | Alto |
| RF-G-05 | Exportación | solapa con RF-ADM-06 | POI/iText | ? | Bajo |
| RF-G-06 | Modo oscuro | no define persistencia de la preferencia | frontend | ? | Bajo |

---

## 3. Historias de usuario — inventario y observaciones

| Épica | ID | Resumen | Observación |
|---|---|---|---|
| 0 Auth | HU-00 | Registro/ingreso con datos válidos | criterios G-W-T presentes |
| 0 Auth | HU-00B | Bloqueo de rutas no autorizadas por rol | sin matriz ruta↔rol en el SRS |
| 0 Auth | HU-00C | Recuperar contraseña por correo | − |
| 1 Dashboard | HU-D1 | Dashboard ADMIN con KPIs **< 2 s** | choca con polling 30 s (RF-DASH-02) |
| 1 Dashboard | HU-D2 | LOGISTICA solo panel de despachos | solapa RF-DASH-08 / RF-LOG-02 |
| 1 Dashboard | HU-D3 | Alerta stock hilo **≤ 5 m** + correo | **C-05** umbral vs RF-DASH-07/RF-CAT-06 |
| 1 Dashboard | HU-D4 | (Should) | − |
| 2 Catálogo | HU-01 | Filtrar catálogo por categoría/precio | − |
| 2 Catálogo | HU-02 | Buscar producto por nombre | solapa RF-G-03 |
| 2 Catálogo | HU-03 | Carrito y compra | − |
| 3 Pedidos | HU-09 | Solicitar pedido personalizado con referencia | − |
| 3 Pedidos | HU-10 | (chat / seguimiento) | − |
| 3 Pedidos | HU-12 | Aprobar o proponer cambios a cotización | − |
| 4 Inv/Log | HU-BOD1 | Registrar hilo con stock y umbral | **C-05** umbral |
| 4 Inv/Log | HU-LOG1 | Generar guía consultando API transportadora | **H-16** mock |
| 4 Inv/Log | HU-MKT1 | (Should, mercadeo) | **mal ubicada:** es de Mercadeo, no de Inv/Log |

**Huecos de HU (`G-02`):** los sprints (sección 7) referencian **HU-05** y **HU-06** (personalizador) que **no están definidas** en la sección 4, y faltan HU-04 a HU-08 y HU-11. HU-MKT1 está en la épica equivocada.

---

## 4. Requisitos no funcionales — inventario

| Código | Requisito | Meta | Ambigüedad / Verificabilidad | Estado real | Riesgo |
|---|---|---|---|---|---|
| RNF-G-01 | Disponibilidad | 99,5 % (≤ 3,6 h/mes) | no verificable manualmente; depende del plan Render | ? | Alto |
| RNF-G-02 | Rendimiento | 95 % < 2 s con 100 usuarios (JMeter) | no verificable manualmente | ? | Alto |
| RNF-G-03 | Responsividad | desde 360 px | verificable manualmente | ? | Bajo |
| RNF-G-04 | Seguridad OWASP | OWASP Top 10 | verificar con ZAP; hay hallazgos abiertos | **P** | Crítico |
| RNF-G-05 | Cifrado | BCrypt 12; **JWT RS256**; TLS 1.3 | **C-03** RS256 vs HS256 real | **D** | Alto |
| RNF-G-06 | Protección de datos | Ley 1581; eliminar cuenta | **C-11** no define flujo de eliminación | ? | Alto |
| RNF-G-07 | Accesibilidad | WCAG 2.1 AA | verificable manualmente | ? | Medio |
| RNF-G-08 | Mantenibilidad | cobertura ≥ 70 %; JUnit+Cypress; SonarCloud | **C-12** real: Playwright, no Cypress; tests base vacíos | **D** | Alto |
| RNF-G-09 | Backups | diarios, retención 30 d, restore < 1 h | **A-10** depende del plan Atlas/M0 | ? | Medio |

---

## 5. Matriz consolidada de contradicciones y ambigüedades

| ID | Tipo | Descripción | RF/HU afectados | Hallazgo |
|---|---|---|---|---|
| C-01 | Contradicción | SRS declara frontend React/TS/Tailwind; el sistema real usa Thymeleaf + JS | 2.2, todos los módulos | H-01 |
| C-02 | Contradicción | SRS: Java 17; real: Java 21 / Spring Boot 3.4.5 | 2.2, 10 | — |
| C-03 | Contradicción | SRS: JWT RS256; real: HS256 | RF-AUTH-02, RNF-G-05 | H-02 |
| C-04 | Contradicción | SRS: 15 min / 7 d; real: 1 h / 1 d | RF-AUTH-02 | H-03 |
| C-05 | Contradicción | Umbral de stock: "≤2 unidades" vs "5 m" vs "configurable" | RF-DASH-07, RF-CAT-06, RF-BOD-03, HU-D3 | — |
| C-06 | Contradicción | SRS dice "8 módulos" pero enumera 9 | 3 | — |
| C-07 | Contradicción | Rol asignado/seleccionable en registro público vs seguridad | RF-AUTH-01, 3.0.2 | H-05 |
| C-08 | Contradicción | Anticipo/saldo 50/50 especificado pero no implementado | RF-PED-05 | H-23 |
| C-09 | Contradicción | Sprints usan HU-05/HU-06 no definidas; faltan HU-04..08, HU-11 | sección 7 vs 4 | G-02 |
| C-10 | Contradicción | Rutas de auth sin `/api` en el SRS; reales con `/api` | RF-AUTH | — |
| C-11 | Vacío/Ambigüedad | No define flujo de eliminación de cuenta (Ley 1581) | RNF-G-06, RF-ADM-04 | — |
| C-12 | Contradicción | SRS: Cypress + cobertura 70 %; real: Playwright y tests base vacíos | RNF-G-08 | — |
| A-01 | Ambigüedad | Fórmula de precio dinámico del personalizador | RF-PER-05 | — |
| A-02 | Ambigüedad | Tallas válidas no enumeradas | RF-PER-03 | — |
| A-03 | Ambigüedad | Tarifas/ciudades de zona de envío no definidas | RF-LOG-01, RF-ADM-07 | — |
| A-04 | Ambigüedad | Umbral VIP de compras no definido | configuracion | — |
| A-05 | Ambigüedad | Interacción anticipo/saldo con paso a "Enviado" | RF-PED-04/05, RF-LOG-03 | — |
| A-06 | Ambigüedad | Formato y columnas del reporte de inventario | RF-BOD-05 | — |
| A-07 | Ambigüedad | Columnas/validaciones de la importación CSV | RF-ADM-02 | — |
| A-08 | Ambigüedad | Canales y eventos de notificación | RF-G-02 | H-17 |
| A-09 | Ambigüedad | Chat "tiempo real" WebSocket vs polling | RF-PED-02 | — |
| A-10 | Ambigüedad | Backups 30 d / restore < 1 h según plan Atlas | RNF-G-09 | — |
| A-11 | Ambigüedad | Nivel de contraste/accesibilidad aplicado | RNF-G-07 | — |
| A-12 | Ambigüedad | "Tiempo real" vs polling cada 30 s | RF-DASH-02, HU-D1 | — |
| G-01 | Vacío | Colección `wishlist` no declarada | RF-CAT-08 | H-19 |
| G-02 | Vacío | HU-05/HU-06 sin definir; huecos de numeración | sección 4/7 | — |
| G-03 | Vacío | Colección de retos/participaciones no declarada | RF-MKT-04 | — |
| G-04 | Vacío | Colección de diseños guardados no declarada | RF-PER-06 | — |
| G-05 | Vacío | Colección de eventos de tráfico no declarada | RF-DASH-09, RF-MKT-03 | — |
| G-06 | Vacío | `galeria.idColeccion` sin colección de colecciones | RF-MKT-02 | — |
| G-07 | Vacío | La Rifa no aparece en el SRS (existe en el sistema) | — | — |

---

## 6. Dependencias entre módulos

| ID | Dependencia | Impacto si falla |
|---|---|---|
| D-01 | Todos los módulos dependen de **Auth/JWT** (RF-AUTH-01..07) | bloqueo total de pruebas de rol |
| D-02 | **Pagos** depende de pedido + pasarela Wompi (RF-CAT-05, RF-PED-05) | no hay flujo de compra end-to-end real |
| D-03 | **Logística** depende de pedidos + API transportadora (RF-LOG-03) | guías simuladas |
| D-04 | **Dashboard/Analíticas** dependen de pedidos, pagos y productos | KPIs sin datos reales |
| D-05 | **Notificaciones/Recuperación** dependen de SMTP (RF-G-02, RF-AUTH-06) | correos no enviados; token expuesto |
| D-06 | **Auditoría** depende de interceptor/AOP (RF-G-04) | sin trazabilidad |
| D-07 | **Reportes** dependen de iText/POI (RF-ADM-06, RF-G-05) | exportables no disponibles |
| D-08 | **Inventario ↔ Catálogo** (stock de hilos alimenta alertas de producto) | alertas inconsistentes |

---

## 7. Riesgos (para priorizar pruebas)

| ID | Riesgo | Prob. | Impacto | Nivel | RF afectados | Mitigación |
|---|---|---|---|---|---|---|
| R-01 | Escalada de privilegios por rol en registro | Alta | Alto | **Crítico** | RF-AUTH-01 | caso negativo de registro con rol ADMIN |
| R-02 | Exposición del token de recuperación sin SMTP | Alta | Alto | **Crítico** | RF-AUTH-06 | verificación de campo `enlace` |
| R-03 | IDOR en logout (`X-User-Id`) | Alta | Alto | **Alto** | RF-AUTH-05 | logout con id de otro usuario |
| R-04 | Credenciales de Mongo en claro | Media | Alto | **Crítico** | RNF-G-04/06 | inspección de `application.properties` |
| R-05 | OAuth2 no operativo | Alta | Medio | **Alto** | RF-AUTH-03 | login Google falla |
| R-06 | Umbral de stock contradictorio → alertas erróneas | Alta | Medio | **Alto** | RF-DASH-07, RF-BOD-03, HU-D3 | probar límites 2/5/configurable |
| R-07 | Pago/transportadora simulados | Alta | Medio | **Alto** | RF-CAT-05, RF-LOG-03 | marcar mock en resultados |
| R-08 | Sin auditoría ni búsqueda global | Alta | Medio | **Alto** | RF-G-04, RF-G-03 | verificar ausencia |
| R-09 | Endpoints sensibles `permitAll` | Media | Alto | **Alto** | RF-CAT-08, RF-PER-07, RNF-G-04 | acceso anónimo a wishlist/personalizador |
| R-10 | RNF de rendimiento/disponibilidad no verificables manualmente | Alta | Medio | **Medio** | RNF-G-01/02/08/09 | declarar como "requiere herramienta" |

---

## 8. Recomendaciones para la Fase 3 (estrategia de pruebas)

1. **Corregir la trazabilidad** antes de migrar casos: `RF-SEG-01` no existe; usar RF-AUTH-07, RF-G-* y RNF-G-*.
2. **Cobertura por bloques:** priorizar bloques con riesgo Crítico/Alto (1 Auth, 3 Catálogo/Pagos, 4 Personalizador, 8 integraciones, 11 NFR).
3. **Casos de borde obligatorios:** umbrales de stock (2/5/configurable), expiración de JWT, límites (5 fotos, 20 caracteres, 2 zonas de color), anticipo/saldo 50/50.
4. **Marcar como "no aplicable/bloqueado":** OAuth2, verificación de correo, pago real, anticipo, guía real, auditoría y búsqueda global (mientras no existan).
5. **Separar pruebas verificables manualmente** (RF-G-01..06, RNF-G-03/07) de las que **requieren herramienta** (RNF-G-01/02/08/09).
6. **Añadir la Rifa** al alcance de pruebas aunque no esté en el SRS (aceptada por el negocio).

---

**Siguiente:** Fase 3 — estrategia/plan de pruebas (migrar Bloque 1 al formato de 19 columnas y ajustar matriz y plan maestro a los 11 bloques).
