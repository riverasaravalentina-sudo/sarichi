# Fase 1 — Análisis del SRS

**Proyecto:** Sarichi Crocheting — Sistema Web de Gestión y Venta de Tejidos Artesanales
**Documento analizado:** `SRS_Sarichi_Crocheting_v2 DEFINITIVA.docx` (1.208 líneas / 48.059 caracteres)
**Estándar declarado por el SRS:** IEEE 830
**Analista QA:** equipo de QA Sarichi Crocheting · **Fecha:** 19/10/2026 · **Versión:** 1.0
**Nota de formato:** el prompt mencionaba un PDF; el archivo real disponible es un `.docx`. No se localizó versión PDF.

> Este documento **describe** el contenido del SRS (Fase 1). El contraste con el sistema real, las contradicciones, dependencias y riesgos se desarrollan en la **Fase 2** (`00b-inventario-requisitos.md`).

---

## 1. Resumen ejecutivo

El SRS especifica una plataforma de comercio electrónico artesanal para Crocheting Sarichi (Bucaramanga), que centraliza tienda, pedidos personalizados, comunidad, blog y administración. Se organiza en **9 módulos funcionales** (sección 3.0 a 3.8), **67 requisitos funcionales (RF)**, **9 requisitos no funcionales (RNF)**, **16 historias de usuario** agrupadas en **5 épicas**, **13 colecciones MongoDB** y un plan de **5 sprints de una semana**.

El stack declarado es **Spring Boot 3.x (Java 17) + MongoDB Atlas + React/TypeScript/Tailwind + Docker + GitHub Actions + Render**.

---

## 2. Propósito y alcance (secciones 1.1 y 1.2)

- **Propósito:** guiar el desarrollo de la plataforma para digitalizar ventas, gestionar pedidos personalizados, administrar inventario y visualizar métricas desde un dashboard centralizado.
- **Alcance declarado:**
  - Aplicación web responsiva *mobile-first* (React + Spring Boot + MongoDB Atlas + Render).
  - Tienda en línea con catálogo, carrito y pasarela de pagos colombiana (Wompi / PSE / Nequi).
  - Personalizador visual interactivo en tiempo real.
  - Pedidos personalizados con chat artesana-cliente y galería del proceso.
  - Dashboard por rol y panel de administración con reportes exportables.
  - Gestión ágil con Scrum: 5 sprints de 1 semana y 4 gráficas de seguimiento.
- **Exclusiones explícitas:** no usa Firebase, Vercel ni base de datos relacional; toda la persistencia ocurre en MongoDB Atlas.

---

## 3. Actores del sistema (sección 1.3)

| Actor | Tipo | Responsabilidad |
|---|---|---|
| Cliente Registrado | Externo | Compra, pide personalizados, participa en la comunidad, gestiona su perfil |
| Artesana (sarichi) | Interno | Atiende pedidos custom, actualiza estados, sube fotos del proceso |
| Administrador | Interno | Gestiona catálogo, usuarios, contenido y métricas |
| Responsable de Mercadeo | Interno | Campañas, análisis de tráfico, blog y redes |
| Responsable de Logística | Interno | Despachos, zonas de envío, transportadoras (Interrapidísimo/Coordinadora) |
| Encargado de Bodega | Interno | Inventario de hilos y materiales, entradas/salidas, alertas |
| QA / Tester | Interno | Valida funcionalidades, documenta defectos, aprueba historias |

**Roles de seguridad del sistema (RF-AUTH-07):** `CLIENTE, ARTESANA, ADMIN, MERCADEO, LOGISTICA, BODEGA`.

---

## 4. Roles del equipo de desarrollo (sección 1.4)

Nueve roles: Product Owner, Project Manager/Scrum Master, Analista de Negocio, Arquitecto de Software, Diseñador UI/UX, Desarrollador Frontend, Desarrollador Backend, QA Engineer y DevOps. El SRS asigna a **QA Engineer** herramientas **JUnit 5 + Mockito + Cypress**.

---

## 5. Arquitectura tecnológica declarada (secciones 1.2, 2.2, 10.2)

| Capa | Tecnología declarada | Notas del SRS |
|---|---|---|
| Frontend | Spring Boot 3.x (Java 17) *[texto del SRS]* — en realidad describe React/TS/Tailwind | SPA mobile-first, consumo de API REST |
| Backend | Spring Boot 3.x (Java 17) | Patrón MVC, JWT, Swagger/OpenAPI 3 |
| Base de datos | MongoDB Atlas (Cloud) | Toda la persistencia en Atlas |
| Contenedores | Docker (`openjdk:17-jdk-slim`, ~200 MB) | Imagen del backend |
| CI/CD | GitHub Actions → Render | checkout, `./mvnw test`, `./mvnw package`, docker build/push, deploy |
| Frontend build | `npm run build` + Nginx | Servido en contenedor Docker o servicio aparte |
| Health check | `GET /actuator/health` | Spring Boot Actuator |

**Variables de entorno declaradas:** `MONGODB_URI`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `WOMPI_API_KEY`.

> La tabla 2.2 rotula la fila *Frontend* con "Spring Boot 3.x (Java 17)", lo que es incoherente con el resto del documento (React). Se documenta como contradicción en Fase 2.

---

## 6. Mapa de módulos (sección 3)

El SRS afirma "8 módulos funcionales, incluyendo el Dashboard" pero enumera **9** (3.0 a 3.8).

| # | Módulo | Sección | Rol responsable | RF |
|---|---|---|---|---|
| 3.0 | Autenticación y Login | 3.0 | Backend + Frontend | 7 |
| 3.1 | Dashboard | 3.1 | Admin, Mercadeo, Logística, Bodega, Artesana | 10 |
| 3.2 | Catálogo y Tienda | 3.2 | Frontend + Backend | 8 |
| 3.3 | Personalizador Visual | 3.3 | Frontend + UI/UX | 7 |
| 3.4 | Pedidos Personalizados | 3.4 | Backend + Artesana | 7 |
| 3.5 | Inventario y Bodega | 3.5 | Encargado de Bodega | 5 |
| 3.6 | Logística y Despachos | 3.6 | Responsable de Logística | 5 |
| 3.7 | Mercadeo y Analíticas | 3.7 | Responsable de Mercadeo | 5 |
| 3.8 | Administración | 3.8 | Administrador | 7 |

---

## 7. Inventario de requisitos funcionales (67 RF)

### 3.0 Autenticación y Login (7)
| Código | Nombre |
|---|---|
| RF-AUTH-01 | Registro de Usuario |
| RF-AUTH-02 | Login con Correo/Contraseña |
| RF-AUTH-03 | Login con Google OAuth2 |
| RF-AUTH-04 | Renovación de Token |
| RF-AUTH-05 | Cierre de Sesión |
| RF-AUTH-06 | Recuperación de Contraseña |
| RF-AUTH-07 | Control de Acceso por Rol |

### 3.1 Dashboard (10)
| Código | Nombre |
|---|---|
| RF-DASH-01 | Dashboard por Rol |
| RF-DASH-02 | KPIs en Tiempo Real |
| RF-DASH-03 | Gráfica de Ventas por Período |
| RF-DASH-04 | Gráfica de Ventas por Categoría |
| RF-DASH-05 | Tabla de Últimos Pedidos |
| RF-DASH-06 | Top Productos Vendidos |
| RF-DASH-07 | Alertas de Stock Crítico |
| RF-DASH-08 | Panel de Despachos del Día |
| RF-DASH-09 | Métricas de Tráfico (Mercadeo) |
| RF-DASH-10 | Accesos Rápidos por Rol |

### 3.2 Catálogo y Tienda (8)
| Código | Nombre |
|---|---|
| RF-CAT-01 | Catálogo de Productos |
| RF-CAT-02 | Filtros y Búsqueda |
| RF-CAT-03 | Detalle del Producto |
| RF-CAT-04 | Carrito de Compras |
| RF-CAT-05 | Proceso de Pago |
| RF-CAT-06 | Gestión de Inventario |
| RF-CAT-07 | Productos Relacionados |
| RF-CAT-08 | Wishlist |

### 3.3 Personalizador Visual (7)
| Código | Nombre |
|---|---|
| RF-PER-01 | Selector de Colores |
| RF-PER-02 | Vista Previa en Tiempo Real |
| RF-PER-03 | Selector de Tamaño |
| RF-PER-04 | Mensaje Bordado |
| RF-PER-05 | Precio Dinámico |
| RF-PER-06 | Guardar Diseño |
| RF-PER-07 | Compartir Diseño |

### 3.4 Pedidos Personalizados (7)
| Código | Nombre |
|---|---|
| RF-PED-01 | Solicitud de Pedido |
| RF-PED-02 | Chat Artesana-Cliente |
| RF-PED-03 | Cotización Formal |
| RF-PED-04 | Seguimiento de Estado |
| RF-PED-05 | Pago en Dos Partes |
| RF-PED-06 | Galería del Proceso |
| RF-PED-07 | Historial de Pedidos |

### 3.5 Inventario y Bodega (5)
| Código | Nombre |
|---|---|
| RF-BOD-01 | Registro de Hilos |
| RF-BOD-02 | Entradas y Salidas de Stock |
| RF-BOD-03 | Alerta de Stock Mínimo |
| RF-BOD-04 | Inventario de Productos |
| RF-BOD-05 | Reporte de Inventario |

### 3.6 Logística y Despachos (5)
| Código | Nombre |
|---|---|
| RF-LOG-01 | Gestión de Zonas de Envío |
| RF-LOG-02 | Panel de Despachos del Día |
| RF-LOG-03 | Generación de Guía |
| RF-LOG-04 | Seguimiento de Envíos |
| RF-LOG-05 | Historial de Despachos |

### 3.7 Mercadeo y Analíticas (5)
| Código | Nombre |
|---|---|
| RF-MKT-01 | Gestión de Blog y Tutoriales |
| RF-MKT-02 | Galería y Colecciones |
| RF-MKT-03 | Métricas de Tráfico |
| RF-MKT-04 | Gestión del Reto Mensual |
| RF-MKT-05 | Reportes de Campañas |

### 3.8 Administración (7)
| Código | Nombre |
|---|---|
| RF-ADM-01 | Dashboard de Ventas |
| RF-ADM-02 | Gestión de Productos |
| RF-ADM-03 | Gestión de Pedidos |
| RF-ADM-04 | Gestión de Clientes |
| RF-ADM-05 | Gestión de Usuarios del Sistema |
| RF-ADM-06 | Reportes Exportables |
| RF-ADM-07 | Configuración de Envíos |

### 8.1 Requisitos funcionales globales (6)
| Código | Nombre |
|---|---|
| RF-G-01 | Autenticación |
| RF-G-02 | Notificaciones |
| RF-G-03 | Búsqueda Global |
| RF-G-04 | Auditoría |
| RF-G-05 | Exportación |
| RF-G-06 | Modo Oscuro |

**Total: 7 + 10 + 8 + 7 + 7 + 5 + 5 + 5 + 7 + 6 = 67 RF.**

### Detalles relevantes de RF (lo que el SRS pide textualmente)

- **RF-AUTH-01:** registro con nombre, correo, contraseña (BCrypt factor 12) y **rol asignado**; **envío de correo de verificación con token**. La sección 3.0.2 añade "selector de rol (si aplica)" en la pantalla de registro.
- **RF-AUTH-02:** JWT con **access token de 15 min** + **refresh token de 7 días** almacenado en MongoDB.
- **RF-AUTH-04:** endpoint `/auth/refresh` valida el refresh token **en MongoDB** y emite nuevo access token.
- **RF-AUTH-05:** `/auth/logout` invalida el refresh token en MongoDB (**blacklist**) y limpia cookies.
- **RF-AUTH-06:** enlace temporal de **15 min** por correo; restablecimiento con Spring MailSender.
- **Redirección por rol (3.0.2):** ADMIN → `/dashboard`, ARTESANA → `/pedidos`, LOGISTICA → `/despachos`, BODEGA → `/inventario`, MERCADEO → `/analiticas`, CLIENTE → `/tienda`.
- **RF-DASH-02:** KPIs con *polling* cada **30 s**.
- **RF-DASH-07:** alerta cuando un **producto** tiene stock **≤ 2 unidades**.
- **RF-DASH-03/04/05/06/09:** gráficas y tablas con rol ADMIN/MERCADEO.
- **RF-CAT-05:** Wompi con tarjeta débito/crédito, PSE y Nequi; confirmación por correo.
- **RF-CAT-06:** alerta automática cuando el stock baja de **2 unidades**.
- **RF-CAT-08:** wishlist con **notificación si el precio baja**.
- **RF-PER-01/02:** actualización de la vista previa en **< 300 ms**; **mínimo 2 zonas de color independientes**.
- **RF-PER-04:** mensaje bordado opcional, **máx. 20 caracteres**.
- **RF-PED-01:** **máx. 5** fotos de referencia.
- **RF-PED-02:** chat en tiempo real con **WebSocket + STOMP** e historial persistente.
- **RF-PED-04:** timeline `Solicitado → Cotizado → Aprobado → En Producción → Listo → Enviado → Entregado`.
- **RF-PED-05:** anticipo **50 %** al aprobar y saldo **50 %** antes del envío, ambos por Wompi.
- **RF-BOD-03:** alerta por **correo + dashboard** bajo el **umbral configurado**.
- **RF-LOG-03:** guía con Interrapidísimo/Coordinadora; el estado del pedido pasa a "Enviado".
- **RF-MKT-01:** blog con editor enriquecido, categorías, etiquetas, SEO y **publicación programada**.
- **RF-ADM-02:** productos con **importación masiva por CSV**.
- **RF-ADM-06:** reportes en **PDF (iText)** y **Excel (Apache POI)**.
- **RF-G-04:** auditoría en `bitacoraAuditoria` con usuario, fecha, IP y datos previos/nuevos.

---

## 8. Historias de usuario (sección 4)

Formato: *Como [rol], quiero [acción], para [beneficio]* con criterios Given-When-Then y prioridad MoSCoW (Must/Should).

| Épica | Sección | IDs | Prioridad |
|---|---|---|---|
| 0 — Autenticación y Login (Sprint 1) | 4.1 | HU-00, HU-00B, HU-00C | Must |
| 1 — Dashboard | 4.2 | HU-D1, HU-D2, HU-D3, HU-D4 | Must, Must, Must, Should |
| 2 — Catálogo y Tienda | 4.3 | HU-01, HU-02, HU-03 | Must |
| 3 — Pedidos Personalizados | 4.4 | HU-09, HU-10, HU-12 | Must |
| 4 — Inventario y Logística | 4.5 | HU-BOD1, HU-LOG1, HU-MKT1 | Must, Must, Should |

**Total: 16 HU.** Criterios destacados:
- **HU-D1:** dashboard ADMIN con KPIs en **menos de 2 s**.
- **HU-D2:** LOGISTICA ve **solo** el panel de despachos del día ordenado por ciudad.
- **HU-D3:** cuando el stock de un hilo baja de **5 metros**, aparece alerta y **envía correo**.
- **HU-00B:** quien inicia comme LOGISTICA no puede acceder a `/dashboard/admin`.
- **HU-12:** el cliente puede **Aprobar o Proponer cambios** sobre la cotización.
- **HU-LOG1:** al generar la guía el sistema **consulta la API de la transportadora** y guarda el número.

> Huecos de numeración: no existen HU-04 a HU-08 ni HU-11 en el documento. Ver Fase 2.

---

## 9. Modelo de datos (sección 5)

13 colecciones, relacionadas por referencias ObjectId con Spring Data MongoDB:

`usuarios`, `productos`, `coloresHilo`, `inventarioMovimientos`, `pedidos`, `pedidosPersonalizados`, `mensajesPedido`, `despachos`, `galeria`, `articulosBlog`, `pagos`, `bitacoraAuditoria`, `configuracion`.

Campos clave:
- **usuarios:** `_id, nombre, correo(unique), passwordHash, telefono, fotoUrl, rol(enum), fechaRegistro, estado, googleId, refreshToken`.
- **productos:** `_id, nombre, descripcion, precioBase, categoria(enum), stock, coloresDisponibles[ObjectId], tiempoElaboracionDias, fotosUrls[], estado, fechaCreacion`.
- **coloresHilo:** `_id, nombre, codigoHex, descripcion, stockMetros, stockMinimo, proveedor, precioMetro, imagenUrl`.
- **inventarioMovimientos:** `_id, idHilo(ref), tipo(entrada|salida), cantidad, motivo, idUsuario(ref), fecha`.
- **pedidos:** `_id, idUsuario(ref), fecha, estado(enum), items[{idProducto, cantidad, precioUnitario, personalizacionJson}], subtotal, costoEnvio, total, metodoPago, numeroGuia, transportadora`.
- **pedidosPersonalizados:** `_id, idUsuario(ref), descripcion, referenciasUrls[], coloresJson, talla, mensajeBordado, estado(enum), precioAcordado, anticipoPagado, fechaEntregaEstimada`.
- **mensajesPedido:** `_id, idPedidoCustom(ref), idUsuario(ref), texto, adjuntoUrl, fecha, leido`.
- **despachos:** `_id, idPedido(ref), fechaDespacho, transportadora, numeroGuia, ciudadDestino, estado, idResponsable(ref)`.
- **galeria:** `_id, idColeccion(ref), idProducto(ref, nullable), urlFoto, titulo, historia, tiempoElaboracionHoras, fechaPublicacion`.
- **articulosBlog:** `_id, titulo, slug(unique), contenidoHtml, categorias[], etiquetas[], idAutor(ref), estado, fechaPublicacion, metaSeo`.
- **pagos:** `_id, idPedido(ref, nullable), idPedidoCustom(ref, nullable), monto, tipo(total|anticipo|saldo), metodo, referenciaPasarela, estado, fecha`.
- **bitacoraAuditoria:** `_id, idUsuario(ref), accion, coleccion, documentoId, datosAnteriores, datosNuevos, fecha, ip`.
- **configuracion:** `_id, stockMinimoAlerta, zonaEnvio[{ciudad, tarifa, diasEstimados}], umbralVIPCompras`.

---

## 10. Endpoints documentados en el SRS

| Método | Ruta | Módulo | Rol |
|---|---|---|---|
| POST | `/auth/register` | Autenticación | público |
| POST | `/auth/login` | Autenticación | público |
| POST | `/auth/refresh` | Autenticación | público |
| POST | `/auth/logout` | Autenticación | autenticado |
| GET | `/api/dashboard/kpis` | Dashboard | ADMIN, ARTESANA |
| GET | `/api/dashboard/ventas` | Dashboard | ADMIN, MERCADEO |
| GET | `/api/dashboard/categorias` | Dashboard | ADMIN, MERCADEO |
| GET | `/api/dashboard/pedidos-recientes` | Dashboard | ADMIN, ARTESANA |
| GET | `/api/dashboard/despachos-hoy` | Dashboard | LOGISTICA, ADMIN |
| GET | `/api/dashboard/stock-critico` | Dashboard | BODEGA, ADMIN |
| POST | `/api/pagos/iniciar` (+ webhook) | Catálogo/Pagos | cliente |
| POST / DELETE | `/api/carrito` | Catálogo | cliente |
| GET | `/api/galeria` (paginación por cursor) | Mercadeo/Galería | público |
| GET | `/actuator/health` | Despliegue | monitoreo |

> Los endpoints de autenticación se documentan sin el prefijo `/api`. El SRS no documenta endpoints para pedidos custom, chat, inventario, blog ni administración, aunque sí describe las funcionalidades. Ver Fase 2.

---

## 11. Requisitos no funcionales (sección 8.2)

| Código | Nombre | Meta declarada |
|---|---|---|
| RNF-G-01 | Disponibilidad | 99,5 % mensual (máx. 3,6 h caída/mes) |
| RNF-G-02 | Rendimiento | 95 % de peticiones < 2 s con 100 usuarios concurrentes (JMeter) |
| RNF-G-03 | Responsividad | Interfaz funcional desde 360 px; mobile-first |
| RNF-G-04 | Seguridad OWASP | Mitigación OWASP Top 10 (inyección, XSS, CSRF, autenticación rota) |
| RNF-G-05 | Cifrado | BCrypt factor 12; **JWT firmado con RS256**; TLS 1.3 |
| RNF-G-06 | Protección de Datos | Ley 1581/2012; consentimiento; opción de eliminar cuenta |
| RNF-G-07 | Accesibilidad | WCAG 2.1 AA: contraste, teclado, ARIA |
| RNF-G-08 | Mantenibilidad | Cobertura ≥ 70 % (JUnit 5 + Cypress); ESLint + Prettier; SonarCloud |
| RNF-G-09 | Backups | Backups diarios de Atlas con retención 30 días; restauración < 1 h |

---

## 12. Plan de entregas por sprint (sección 7)

| Sprint | Semana | Entregable funcional | Historias priorizadas |
|---|---|---|---|
| 1 | 1 | Login + Autenticación JWT + redirección por rol | HU-00, HU-00B, HU-00C |
| 2 | 2 | Dashboard por rol + Catálogo con filtros + CI/CD | HU-D1, HU-D2, HU-D3, HU-01, HU-02 |
| 3 | 3 | Carrito + Pago Wompi + Personalizador + Inventario bodega | HU-03, HU-05, HU-06, HU-BOD1 |
| 4 | 4 | Pedidos custom + Chat WebSocket + Logística + Galería | HU-09, HU-10, HU-12, HU-LOG1 |
| 5 | 5 | Blog + Mercadeo + pruebas finales + Deploy Render | HU-MKT1 + pendientes |

Notas: Sprint 3 declara pruebas "mínimo 3 casos por HU"; Sprint 5 declara JMeter (100 usuarios), OWASP ZAP, Cypress E2E con cobertura ≥ 70 % y deploy final. La generación de guía en Sprint 4 se describe como **mock API transportadora**.

---

## 13. Criterios de evaluación (sección 9)

| Criterio | Peso |
|---|---|
| Cumplimiento funcional | 30 % |
| Calidad técnica (arquitectura, cobertura ≥70 %, CI/CD, deploy) | 20 % |
| Diseño e innovación UI/UX | 20 % |
| Documentación (SRS IEEE 830, C4/UML/BPMN, gráficas Scrum) | 15 % |
| Presentación final (demo en producción) | 15 % |

---

## 14. Despliegue, identidad visual y referencias (sección 10)

- **Render:** Docker con `openjdk:17-jdk-slim`; variables en el panel; health check `GET /actuator/health`; pipeline GitHub Actions (checkout, `./mvnw test`, `./mvnw package -DskipTests`, docker build/push, deploy).
- **MongoDB Atlas:** Network Access `0.0.0.0/0` en desarrollo, IPs de Render en producción.
- **Identidad visual:** rosa coral `#E8527A`, celeste `#2DB5C8`, crema `#FDFAF6`; tipografías Playfair Display + Inter; mascota "gatito negro de ojos verdes".
- **Referencias:** IEEE 830, Scrum Guide, C4 Model, Spring Boot, Spring Data MongoDB, MongoDB Atlas, Render, Wompi, OWASP Top 10, Ley 1581/2012.

---

## 15. Notas de lectura (preliminares; se detallan en Fase 2)

Detectadas durante la lectura, se profundizan y clasifican en `00b-inventario-requisitos.md`:

1. **Stack:** el SRS declara React/TS/Tailwind y Java 17; el sistema real usa Thymeleaf + Java 21.
2. **Conteo de módulos:** dice "8 módulos" pero enumera 9.
3. **Rol en el registro:** RF-AUTH-01 y 3.0.2 sugieren asignar/seleccionar rol al registrarse, lo que choca con la seguridad de un registro público.
4. **Umbral de stock contradictorio:** "≤ 2 unidades" (productos, RF-DASH-07/RF-CAT-06) vs "5 metros" (hilos, HU-D3) vs "umbral configurado" (RF-BOD-03/colección `configuracion`).
5. **JWT:** 15 min/7 días y RS256 en el SRS; verificar contra la configuración real.
6. **Huecos de HU:** faltan HU-04 a HU-08 y HU-11.
7. **HU-MKT1** aparece dentro de la Épica 4 (Inventario y Logística) siendo de Mercadeo.
8. **Endpoints incompletos:** varias funcionalidades (pedidos custom, chat, inventario, blog, admin, reportes) no tienen endpoint documentado.
9. **Colecciones faltantes frente a RF:** wishlist (RF-CAT-08), reseñas, retos/participaciones (RF-MKT-04), diseños guardados (RF-PER-06), eventos de tráfico (RF-DASH-09) no figuran en el modelo de datos.
10. **Integraciones externas posibles/mock:** transportadora (el propio Sprint 4 la declara mock), Wompi (requiere llaves), correo (requiere SMTP), Google OAuth2.
11. **RNF no verificables manualmente:** disponibilidad (RNF-G-01), rendimiento con 100 usuarios (RNF-G-02), cobertura de código (RNF-G-08), backups/restauración (RNF-G-09) requieren herramientas o acceso a infraestructura.

---

**Fuentes analizadas:** `SRS_Sarichi_Crocheting_v2 DEFINITIVA.docx` (texto extraído íntegro, 1.208 líneas).
**Siguiente:** Fase 2 — inventario de requisitos con contraste de implementación, inconsistencias, dependencias y riesgos.
