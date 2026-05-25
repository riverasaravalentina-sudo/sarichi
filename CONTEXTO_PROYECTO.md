# 🧵 Crocheting Sarichi - Contexto Completo del Proyecto

**Último actualizado:** 2026-05-25  
**Estado actual:** Sprint 2 ✅ | Sprint 3 ⏳ | Sprint 4 ⏳  
**Arquitecto Senior:** Java Spring Boot 3.4.5 + MongoDB + JWT + Vanilla JS

---

## 📋 CONTEXTO DEL PROYECTO

**Nombre:** Crocheting Sarichi  
**Descripción:** Plataforma web de tejidos artesanales (amigurumis, ropa, accesorios, decoración)  
**Objetivo:** Vender productos tejidos a mano, gestionar inventario de hilos y realizar seguimiento de pedidos  
**Metodología:** Scrum con sprints enfocados en features incrementales

---

## 🏗️ STACK TÉCNICO (INMUTABLE)

```
Backend:
  - Spring Boot 3.4.5
  - Java 21
  - MongoDB Atlas (empresarialessara.vmeolxj.mongodb.net)
  - Spring Data MongoDB
  - Spring Security 6
  - JWT (JJWT 0.12.3) → accessToken 15min, refreshToken 7 días
  - Swagger/OpenAPI 2.8.3
  - Lombok (procesado con maven-compiler-plugin)
  - BCrypt PasswordEncoder (strength=12)
  - Spring Mail (para recuperación de contraseña)
  - Spring Actuator (health check)
  - Spring DevTools (hot reload)

Frontend:
  - HTML5 / CSS3 / Vanilla JavaScript (NO frameworks)
  - SessionStorage para tokens
  - Fetch API para requests
  - Auto-refresh cada 30 segundos (dashboard)

Package Base:     com.sarichi.crocheting
Context Path:     /api
Server Port:      8080
Database:         crocheting (MongoDB Atlas)
```

---

## 📦 ESTRUCTURA DE CARPETAS

```
src/main/java/com/sarichi/crocheting/
├── SarichiCrochetingApplication.java
├── config/
│   ├── SecurityConfig.java                    ✅ COMPLETO
│   ├── CustomUserDetailsService.java          ✅ COMPLETO
│   ├── GlobalExceptionHandler.java            ✅ COMPLETO
│   └── OpenApiConfig.java                     ✅ COMPLETO
├── controller/
│   ├── AutenticacionController.java           ✅ COMPLETO
│   ├── DashboardController.java               ✅ PARCIAL (Sprint 3 pendiente)
│   ├── ProductoController.java                ✅ COMPLETO
│   └── ColorHiloController.java               ✅ COMPLETO
├── service/
│   ├── AutenticacionService.java              ✅ COMPLETO
│   ├── DashboardService.java                  ✅ PARCIAL (Sprint 3 pendiente)
│   ├── ProductoService.java                   ✅ COMPLETO
│   ├── ColorHiloService.java                  ✅ COMPLETO
│   ├── ConfiguracionService.java              ✅ COMPLETO
│   └── EmailService.java                      ✅ COMPLETO
├── repository/
│   ├── UsuarioRepository.java                 ✅ COMPLETO
│   ├── ProductoRepository.java                ✅ COMPLETO
│   ├── ColorHiloRepository.java               ✅ COMPLETO
│   └── ConfiguracionRepository.java           ✅ COMPLETO
├── entity/
│   ├── Usuario.java                           ✅ COMPLETO
│   ├── Producto.java                          ✅ COMPLETO
│   ├── ColorHilo.java                         ✅ COMPLETO
│   ├── Configuracion.java                     ✅ COMPLETO
│   └── UserRole.java (enum)                   ✅ COMPLETO
├── dto/
│   ├── LoginDTO.java                          ✅ COMPLETO
│   ├── RegistroDTO.java                       ✅ COMPLETO
│   ├── TokenResponseDTO.java                  ✅ COMPLETO
│   ├── UsuarioResponseDTO.java                ✅ COMPLETO
│   ├── RecuperarContrasenaDTO.java            ✅ COMPLETO
│   ├── RestablecerContrasenaDTO.java          ✅ COMPLETO
│   ├── DashboardKpisDTO.java                  ✅ COMPLETO
│   ├── ProductoDTO.java                       ✅ COMPLETO
│   ├── ProductoFiltroDTO.java                 ✅ COMPLETO
│   ├── ColorHiloDTO.java                      ✅ COMPLETO
│   └── PerfilCompletoDTO.java                 ✅ COMPLETO
└── security/
    └── JwtTokenProvider.java                  ✅ COMPLETO

src/main/resources/
├── application.properties                     ✅ COMPLETO
├── templates/                                 (vacío - no usar)
└── static/
    ├── index.html                             ✅ COMPLETO
    ├── login.html                             ✅ COMPLETO
    ├── register.html                          ✅ COMPLETO
    ├── dashboard.html                         ✅ COMPLETO
    ├── tienda.html                            ✅ COMPLETO
    ├── productos-admin.html                   ✅ COMPLETO
    ├── inventario.html                        ✅ COMPLETO
    ├── despachos.html                         ⏳ Sprint 3
    ├── analiticas.html                        ⏳ Sprint 3
    ├── sarichi.js                             ✅ COMPLETO
    ├── styles.css                             ✅ COMPLETO
    └── recuperar.html, restablecer.html       ✅ COMPLETO

src/test/java/com/sarichi/crocheting/
└── SarichiCrochetingApplicationTests.java     ✅ 2/2 tests PASSING
```

---

## ✅ SPRINT 1 COMPLETADO - AUTENTICACIÓN

### Entities
```java
Usuario (@Document "usuarios")
├── id (ObjectId)
├── nombre
├── correo (unique index)
├── passwordHash (BCrypt)
├── telefono
├── fotoUrl
├── rol (CLIENTE, ADMIN, ARTESANA, BODEGA, LOGISTICA, MERCADEO)
├── estado (ACTIVO, SUSPENDIDO)
├── fechaRegistro
├── ultimoLogin
├── googleId (para OAuth - Sprint 3)
├── refreshToken (null después de logout)
├── tokenRecuperacion
├── tokenRecuperacionExpira
├── direcciones (List<String>)
└── modoOscuro (boolean)

UserRole (enum)
├── CLIENTE
├── ARTESANA
├── ADMIN
├── MERCADEO
├── LOGISTICA
└── BODEGA
```

### Repositories
```java
UsuarioRepository extends MongoRepository
  • findByCorreo(correo) → Optional<Usuario>
  • findByGoogleId(googleId) → Optional<Usuario>
  • findByTokenRecuperacion(token) → Optional<Usuario>
  • existsByCorreo(correo) → boolean
  • countByRol(rol) → long
```

### Services
```java
AutenticacionService
  • registrar(RegistroDTO) → UsuarioResponseDTO (201)
  • login(LoginDTO) → TokenResponseDTO (200)
  • refreshToken(tokenRefresh) → TokenResponseDTO (200)
  • logout(usuarioId) → void (200)
  • solicitarRecuperacion(correo) → void (200)
  • restablecerContrasena(token, newPass) → void (200)
  • obtenerPorId(usuarioId) → UsuarioResponseDTO (200)

EmailService
  • enviarCorreoRecuperacion(usuario, link) → void
  • enviarBienvenida(usuario) → void
```

### Controllers
```java
AutenticacionController (/auth)
  POST   /register                  → 201 Created {usuario}
  POST   /login                     → 200 OK {accessToken, refreshToken, usuario}
  POST   /refresh                   → 200 OK {accessToken}
  POST   /logout (X-User-Id header) → 200 OK
  POST   /recuperar                 → 200 OK
  POST   /restablecer               → 200 OK
  GET    /me (Bearer token)         → 200 OK {usuario}
  GET    /perfil-completo (Bearer)  → 200 OK {usuario, cantidadWishlist, fechas}
```

### Security
```java
SecurityConfig
  • STATELESS session management
  • BCryptPasswordEncoder(12)
  • JwtAuthenticationFilter (OncePerRequestFilter)
  • Rutas públicas: /, /*.html, /*.css, /*.js, /auth/**, /v3/api-docs/**, /swagger-ui/**
  • @EnableMethodSecurity(prePostEnabled = true)

JwtTokenProvider
  • generarAccessToken(usuarioId) → 15 minutos
  • generarRefreshToken(usuarioId) → 7 días
  • validarToken(token) → boolean
  • getUsuarioIdFromToken(token) → String
  • getRolFromToken(token) → String
  • Algoritmo: HMAC-SHA512 (configurado en pom.xml)

CustomUserDetailsService
  • loadUserByUsername(correo) → UserDetails
  • loadUserById(usuarioId) → UserDetails
```

### Frontend (Sprint 1)
```
login.html
  → Form: correo, contrasena
  → POST /auth/login
  → Auth.save() en sessionStorage
  → redirectByRol() → dashboard.html (ADMIN) o tienda.html (CLIENTE)

register.html
  → Form: nombre, correo, telefono, contrasena, rol
  → POST /auth/register
  → Redirige a login.html

recuperar.html
  → Form: correo
  → POST /auth/recuperar → Envía email HTML

restablecer.html
  → Lee ?token= de URL
  → POST /auth/restablecer con nuevaContrasena

sarichi.js (Global)
  Auth object → getToken(), save(token), logout()
  apiGet(url) → GET request con Bearer token
  apiPost(url, data) → POST request con Bearer token
  showToast(msg, type) → Notificaciones
  requireAuth() → Redirige a login si no hay token
  refrescarTokenAutomatico() → Cada 12 minutos
```

### Tests (Sprint 1)
```java
SarichiCrochetingApplicationTests
  ✓ testRegistroUsuario() → POST /register crea usuario con BCrypt
  ✓ testLoginUsuario() → POST /login retorna accessToken válido
```

### Base de Datos (Sprint 1)
```json
// usuarios collection
{
  "_id": ObjectId,
  "nombre": "Admin Sarichi",
  "correo": "admin@sarichi.com",
  "passwordHash": "$2a$12$...",
  "rol": "ADMIN",
  "estado": "ACTIVO",
  "fechaRegistro": ISODate,
  "ultimoLogin": ISODate,
  "refreshToken": null
}

// Índice único en usuarios.correo
db.usuarios.createIndex({ "correo": 1 }, { "unique": true })
```

---

## ✅ SPRINT 2 COMPLETADO - DASHBOARD Y CATÁLOGO

### Entities (Nuevas)
```java
Producto (@Document "productos")
├── id (ObjectId)
├── nombre
├── descripcion
├── precioBase
├── categoria (Amigurumis, Accesorios, Ropa, Hogar)
├── stock
├── coloresDisponibles (List<String>)
├── fotosUrls (List<String>)
├── tiempoElaboracionDias
├── estado (ACTIVO, INACTIVO - eliminación lógica)
└── fechaCreacion

ColorHilo (@Document "coloresHilo")
├── id (ObjectId)
├── nombre
├── codigoHex (#FF0000)
├── descripcion
├── stockMetros
├── stockMinimo (umbral para crítico)
├── proveedor
├── precioMetro
└── imagenUrl

Configuracion (@Document "configuracion")
├── id (ObjectId)
├── stockMinimoAlerta (default: 2)
├── umbralVIPCompras (default: 5)
└── zonaEnvio (List<ZonaEnvio> - Sprint 3)
```

### Repositories (Sprint 2)
```java
ProductoRepository extends MongoRepository
  • findByEstado(estado) → List<Producto>
  • findByCategoriaAndEstado(categoria, estado) → List<Producto>
  • findByEstadoAndStockGreaterThan(estado, stock) → List<Producto>
  • buscarPorTexto(busqueda) → @Query regex en nombre y descripcion
  • findByEstadoAndStockLessThanEqual(estado, stock) → List (críticos)
  • countByEstadoAndStockLessThanEqual(estado, stock) → long

ColorHiloRepository extends MongoRepository
  • findByStockMetrosLessThanEqual(stockMinimo) → List (críticos)

ConfiguracionRepository extends MongoRepository
  • findById(id) → Optional<Configuracion>
```

### Services (Sprint 2)
```java
DashboardService
  obtenerKpis() → DashboardKpisDTO
  {
    "ventasHoy": 0.0,
    "ventasAyer": 0.0,
    "variacionVentas": 0.0,
    "pedidosPendientes": 0,
    "pedidosEnProduccion": 0,
    "productosParaDespachar": 0,
    "stockCritico": count(productos críticos + colores críticos),
    "totalClientes": count(Usuario con rol=CLIENTE),
    "totalProductos": count(Producto con estado=ACTIVO)
  }

ProductoService
  • crear(ProductoDTO) → ProductoDTO (201)
  • listarConFiltros(ProductoFiltroDTO) → List<ProductoDTO>
  • obtenerPorId(id) → ProductoDTO (200)
  • listarTodos() → List<ProductoDTO> (admin)
  • actualizar(id, dto) → ProductoDTO (200)
  • eliminar(id) → void (eliminación lógica → estado=INACTIVO)
  • obtenerStockCritico(umbral) → List<ProductoDTO>

ColorHiloService
  • crear(ColorHiloDTO) → ColorHiloDTO (201)
  • listarTodos() → List<ColorHiloDTO> (público)
  • obtenerPorId(id) → ColorHiloDTO (público)
  • actualizar(id, dto) → ColorHiloDTO (200)
  • eliminar(id) → void
  • obtenerCriticos() → List<ColorHiloDTO> (por debajo stockMinimo)

ConfiguracionService
  • obtenerConfiguracion() → Configuracion (crea default si no existe)
  • actualizarConfiguracion(config) → Configuracion
```

### Controllers (Sprint 2)
```java
DashboardController (/dashboard)
  GET  /kpis                 @PreAuthorize(ADMIN+ARTESANA+BODEGA+LOGISTICA+MERCADEO)
  GET  /ventas               @PreAuthorize(ADMIN+MERCADEO) → [] (Sprint 3)
  GET  /categorias           @PreAuthorize(ADMIN+MERCADEO) → [] (Sprint 3)
  GET  /pedidos-recientes    @PreAuthorize(ADMIN+ARTESANA) → [] (Sprint 4)
  GET  /despachos-hoy        @PreAuthorize(ADMIN+LOGISTICA) → [] (Sprint 4)
  GET  /stock-critico        @PreAuthorize(ADMIN+BODEGA)

ProductoController (/productos)
  GET    /                        público (con filtros)
  GET    /{id}                    público
  GET    /admin/todos             @PreAuthorize(ADMIN)
  POST   /                        @PreAuthorize(ADMIN)
  PUT    /{id}                    @PreAuthorize(ADMIN)
  DELETE /{id}                    @PreAuthorize(ADMIN)
  GET    /stock-critico           @PreAuthorize(ADMIN+BODEGA)

ColorHiloController (/colores-hilo)
  GET    /                        público
  GET    /{id}                    público
  GET    /criticos                @PreAuthorize(ADMIN+BODEGA)
  POST   /                        @PreAuthorize(ADMIN+BODEGA)
  PUT    /{id}                    @PreAuthorize(ADMIN+BODEGA)
  DELETE /{id}                    @PreAuthorize(ADMIN)
```

### DTOs (Sprint 2)
```java
DashboardKpisDTO
ProductoDTO
ProductoFiltroDTO
ColorHiloDTO
PerfilCompletoDTO
```

### Frontend (Sprint 2)
```
dashboard.html
  → Requiere autenticación (ADMIN+ARTESANA+BODEGA+LOGISTICA+MERCADEO)
  → GET /dashboard/kpis
  → Muestra KPIs reales: ventasHoy, ventasAyer, pedidosPendientes, etc.
  → Auto-refresh cada 30 segundos
  → Menú: Dashboard, Tienda, Inventario, Despachos, Analíticas

tienda.html
  → Pública (cualquiera puede verla)
  → GET /productos
  → Filtro por categoría
  → Búsqueda por texto
  → Carrito (Sprint 3)

productos-admin.html
  → @PreAuthorize(ADMIN)
  → CRUD: crear, editar, eliminar productos
  → GET /productos/admin/todos
  → POST /productos
  → PUT /productos/{id}
  → DELETE /productos/{id}

inventario.html
  → @PreAuthorize(ADMIN+BODEGA)
  → Tabla de colores hilo con stock real
  → Formulario para crear/editar colores
  → GET /colores-hilo
  → POST /colores-hilo
  → Muestra colores críticos (rojo)

despachos.html
  → @PreAuthorize(ADMIN+LOGISTICA)
  → GET /dashboard/despachos-hoy (Sprint 4)
  → Tabla de despachos con estado

analiticas.html
  → @PreAuthorize(ADMIN+MERCADEO)
  → GET /dashboard/ventas (Sprint 3)
  → GET /dashboard/categorias (Sprint 3)
  → Gráficos con Chart.js o similar
```

### Security (Sprint 2)
```
SecurityConfig actualizado
  • GET /productos y GET /colores-hilo permitidos públicamente
  • Otros endpoints protected con @PreAuthorize
  • Patron: @PreAuthorize("hasAnyRole('ADMIN','BODEGA')")
```

### Tests (Sprint 2)
```java
testRegistroUsuario() ✓
testProductoFiltroCategoria() ✓
  → Crea 2 productos, filtra por categoría, verifica count
```

### Base de Datos (Sprint 2)
```json
// productos collection - 6 activos
{
  "_id": ObjectId,
  "nombre": "Osito Amigurumi",
  "categoria": "Amigurumis",
  "precioBase": 45000,
  "stock": 5,
  "estado": "ACTIVO"
}

// coloresHilo collection - 4 documentos
{
  "_id": ObjectId,
  "nombre": "Rosa Pastel",
  "stockMetros": 3,
  "stockMinimo": 20,
  "estado": "CRÍTICO"  // Mostrar visualmente
}

// configuracion collection
{
  "_id": ObjectId,
  "stockMinimoAlerta": 2,
  "umbralVIPCompras": 5
}
```

---

## ⏳ SPRINT 3 PENDIENTE - PEDIDOS Y VENTAS

### Nuevas Entities (Sprint 3)
```java
Pedido (@Document "pedidos")
├── id (ObjectId)
├── usuarioId (referencia a Usuario)
├── fechaPedido
├── items (List<ItemPedido>)
├── total
├── estado (PENDIENTE, EN_PRODUCCION, LISTO, DESPACHADO, ENTREGADO, CANCELADO)
├── direccionEnvio
├── fechaEntregaEstimada
├── notaProductor
└── notas

ItemPedido
├── productoId
├── cantidad
├── colorSolicitado
├── precioUnitario
└── subtotal

Resena (@Document "resenas")
├── id
├── pedidoId
├── usuarioId
├── calificacion (1-5)
├── comentario
├── fechaCreacion
└── producto_referencias

VentasPorPeriodo (collection analytics)
├── fecha
├── totalVentas
├── cantidadPedidos
├── categoriasVendidas
└── top_productos
```

### Nuevos Repositories (Sprint 3)
```java
PedidoRepository
  • findByUsuarioId(usuarioId) → List<Pedido>
  • findByEstado(estado) → List<Pedido>
  • findByEstadoAndFechaPedidoBetween(estado, desde, hasta) → List
  • countByEstado(estado) → long

ResenaRepository
  • findByProductoId(productoId) → List<Resena>
  • findByUsuarioId(usuarioId) → List<Resena>
  • promedioCalificacion(productoId) → Double
```

### Nuevos Services (Sprint 3)
```java
PedidoService
  • crearPedido(UsuarioId, items) → PedidoDTO (201)
  • listarMisPedidos(usuarioId) → List<PedidoDTO>
  • listarTodos() → List<PedidoDTO> @PreAuthorize(ADMIN+ARTESANA)
  • obtenerPorId(pedidoId) → PedidoDTO
  • cambiarEstado(pedidoId, nuevoEstado) → PedidoDTO
  • cancelarPedido(pedidoId) → void
  • obtenerPedidosPorEstado(estado) → List<PedidoDTO>

ResenaService
  • crearResena(pedidoId, resenaDTO) → ResenaDTO
  • listarResenas(productoId) → List<ResenaDTO>
  • obtenerPromedioCalificacion(productoId) → Double
  • eliminarResena(resenaId) → void

DashboardService (ACTUALIZAR Sprint 3)
  • ventasHoy() → sumar pedidos de hoy
  • ventasAyer() → sumar pedidos de ayer
  • variacionVentas() → (ventasHoy - ventasAyer) / ventasAyer
  • pedidosPendientes() → count(estado=PENDIENTE)
  • pedidosEnProduccion() → count(estado=EN_PRODUCCION)
  • obtenerVentasPorCategoria() → Map<String, Double>
  • obtenerTopProductos() → List<String> top 5
```

### Nuevos Controllers (Sprint 3)
```java
PedidoController (/pedidos)
  GET    /mis-pedidos              @PreAuthorize(isAuthenticated)
  POST   /                         @PreAuthorize(isAuthenticated)
  GET    /                         @PreAuthorize(ADMIN+ARTESANA)
  GET    /{id}                     @PreAuthorize(isAuthenticated)
  PUT    /{id}/estado              @PreAuthorize(ADMIN+ARTESANA)
  DELETE /{id}                     @PreAuthorize(isAuthenticated)

ResenaController (/resenas)
  GET    /producto/{productoId}    público
  POST   /                         @PreAuthorize(isAuthenticated)
  DELETE /{id}                     @PreAuthorize(isAuthenticated)
```

### DTOs Sprint 3
```java
PedidoDTO
ItemPedidoDTO
ResenaDTO
EstadoPedidoDTO
```

### Frontend (Sprint 3)
```
carrito.html (NUEVA)
  → GET /productos
  → Agregar/quitar items
  → POST /pedidos para crear pedido
  → Resumen de compra

mis-pedidos.html (NUEVA)
  → GET /pedidos/mis-pedidos
  → Tabla de pedidos con estado
  → Click para ver detalles
  → Botón cancelar (si estado=PENDIENTE)

despachos.html (ACTUALIZAR)
  → GET /dashboard/despachos-hoy
  → Tabla de despachos listos para enviar
  → Filtro por estado
  → Botón "Marcar como despachado"

analiticas.html (ACTUALIZAR)
  → GET /dashboard/ventas
  → GET /dashboard/categorias
  → Gráficos con Chart.js:
    - Ventas por período
    - Top 5 productos
    - Distribución por categoría
    - Cliente VIP destacados

producto-detalle.html (NUEVA)
  → GET /productos/{id}
  → GET /resenas/producto/{id}
  → Mostrar reseñas con calificación
  → Formulario para escribir reseña
  → Agregar al carrito
```

### Base de Datos (Sprint 3)
```json
// pedidos collection
{
  "_id": ObjectId,
  "usuarioId": ObjectId,
  "fechaPedido": ISODate,
  "items": [
    {
      "productoId": ObjectId,
      "cantidad": 2,
      "colorSolicitado": "Rojo",
      "precioUnitario": 45000,
      "subtotal": 90000
    }
  ],
  "total": 90000,
  "estado": "PENDIENTE",
  "direccionEnvio": "Calle 123",
  "fechaEntregaEstimada": ISODate
}

// resenas collection
{
  "_id": ObjectId,
  "productoId": ObjectId,
  "usuarioId": ObjectId,
  "calificacion": 5,
  "comentario": "Excelente producto!",
  "fechaCreacion": ISODate
}
```

---

## ⏳ SPRINT 4 PENDIENTE - LOGÍSTICA Y ENTREGAS

### Nuevas Entities (Sprint 4)
```java
Despacho (@Document "despachos")
├── id
├── pedidoId
├── fechaDespacho
├── transportista
├── numeroSeguimiento
├── estado (PENDIENTE, EN_TRANSITO, ENTREGADO, DEVUELTO)
├── fechaEstimadaEntrega
└── observaciones

Devolucion (@Document "devoluciones")
├── id
├── pedidoId
├── motivo
├── estado (SOLICITADA, APROBADA, EN_TRANSITO, RECIBIDA)
├── fechaSolicitud
└── fechaAprobacion

ActividadProductor (@Document "actividades")
├── id
├── productoId
├── producidoPor (usuarioId con rol=ARTESANA)
├── cantidad
├── fechaInicio
├── fechaFinalizacion
└── estado (EN_PROGRESO, COMPLETADO)
```

### Nuevos Services (Sprint 4)
```java
DespachoService
  • crearDespacho(pedidoId) → DespachoDTO
  • actualizarEstado(despachoId, estado) → DespachoDTO
  • obtenerPorPedido(pedidoId) → DespachoDTO
  • listarDespachosPendientes() → List<DespachoDTO>

DevolucionService
  • solicitarDevolucion(pedidoId, motivo) → DevolucionDTO
  • aprobarDevolucion(devolucionId) → DevolucionDTO
  • listarDevoluciones() → List<DevolucionDTO>

ActividadProductorService
  • crearActividad(productoId, cantidad) → ActividadDTO
  • completarActividad(actividadId) → ActividadDTO
  • listarActividades() → List<ActividadDTO>
  • obtenerCapacidadProductor(productoId) → capacidadDisponible
```

### Nuevos Controllers (Sprint 4)
```java
DespachoController (/despachos)
  GET    /                    @PreAuthorize(ADMIN+LOGISTICA)
  POST   /                    @PreAuthorize(ADMIN+LOGISTICA)
  PUT    /{id}/estado         @PreAuthorize(ADMIN+LOGISTICA)

DevolucionController (/devoluciones)
  GET    /                    @PreAuthorize(isAuthenticated)
  POST   /                    @PreAuthorize(isAuthenticated)

ActividadController (/actividades)
  GET    /                    @PreAuthorize(ADMIN+ARTESANA+BODEGA)
  POST   /                    @PreAuthorize(ADMIN+ARTESANA)
  PUT    /{id}                @PreAuthorize(ADMIN+ARTESANA)
```

### Frontend (Sprint 4)
```
logistica.html (NUEVA)
  → @PreAuthorize(ADMIN+LOGISTICA)
  → GET /despachos
  → Tabla de despachos con filtro por estado
  → Formulario para actualizar estado
  → Ver detalles de envío

devoluciones.html (NUEVA)
  → GET /devoluciones
  → Tabla de devoluciones
  → POST /devoluciones/solicitar (usuario)
  → PUT /devoluciones/{id} (admin solo)

productor.html (NUEVA)
  → @PreAuthorize(ARTESANA)
  → GET /actividades
  → Mis tareas de producción
  → Marcar como completado
  → Calendario de producción
```

---

## 📋 PATRONES Y CONVENCIONES (INMUTABLES)

### Nomenclatura
```
Clases:            UsuarioService, ColorHiloController, ProductoRepository
Métodos:           crearProducto(), obtenerPorId(), listarTodos()
Variables:         usuarioId, productoDTO, colorHiloList
Colecciones:       usuarios, productos, coloresHilo, pedidos (snake_case lowercase)
Campos JSON:       usuarioId, nombreProducto, categoriaId (camelCase)
```

### Decoradores Spring
```
@Service              → Servicios con lógica de negocio
@Controller           → Controllers (NO REST - uso @RestController)
@RestController       → Controllers que retornan JSON
@RequestMapping       → Mapear rutas
@GetMapping/@PostMapping/@PutMapping/@DeleteMapping
@PreAuthorize         → Control de roles: hasRole('ADMIN'), hasAnyRole('A','B')
@Document             → Entidades MongoDB
@Data @Builder        → Lombok para DTOs
@Valid                → Validación de entrada
@AuthenticationPrincipal → Usuario autenticado en parámetro
```

### Estructura de Respuesta
```json
SUCCESS (200):
{
  "id": "...",
  "nombre": "...",
  "...": "..."
}

CREATED (201):
{
  "id": "...",
  "nombre": "..."
}

ERROR (400, 403, 404, 500):
{
  "timestamp": "2026-05-25T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Correo ya existe",
  "path": "/api/auth/register"
}
```

### Convención de Servicios
```java
// Template OBLIGATORIO
@Service
public class XService {
  @Autowired
  private XRepository xRepository;
  
  public XDTO crear(XDTO dto) {
    // Validar
    // Mapear DTO → Entity
    // Guardar
    // Retornar Entity → DTO
  }
  
  public List<XDTO> listarTodos() {
    return xRepository.findAll()
        .stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
  }
  
  public XDTO obtenerPorId(String id) {
    return xRepository.findById(id)
        .map(this::entityToDto)
        .orElseThrow(() -> new ResourceNotFoundException("X no encontrado"));
  }
  
  private XDTO entityToDto(X entity) {
    return X.builder()
        .id(entity.getId())
        .nombre(entity.getNombre())
        .build();
  }
}
```

### Convención de Controllers
```java
// Template OBLIGATORIO
@RestController
@RequestMapping("/x")
@Tag(name = "X", description = "Gestión de X")
public class XController {
  @Autowired
  private XService xService;
  
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN')")
  @SecurityRequirement(name = "bearerAuth")
  @Operation(summary = "Crear X", description = "...")
  public ResponseEntity<XDTO> crear(@Valid @RequestBody XDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(xService.crear(dto));
  }
  
  @GetMapping
  @Operation(summary = "Listar X")
  public ResponseEntity<List<XDTO>> listar() {
    return ResponseEntity.ok(xService.listarTodos());
  }
}
```

### Validación
```java
@NotBlank(message = "El correo es requerido")
private String correo;

@Email(message = "Formato de correo inválido")
private String email;

@Min(1) @Max(100)
private int cantidad;

// En Controller:
public ResponseEntity<XDTO> crear(@Valid @RequestBody XDTO dto) {
  // Si @Valid falla → GlobalExceptionHandler captura MethodArgumentNotValidException
}
```

### Manejo de Errores
```java
// GlobalExceptionHandler
@ExceptionHandler(UsuarioYaExisteException.class)
public ResponseEntity<ErrorResponse> handleUsuarioYaExiste(UsuarioYaExisteException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(409)
            .message(e.getMessage())
            .build());
}

// Custom Exceptions
public class UsuarioYaExisteException extends RuntimeException { }
public class ResourceNotFoundException extends RuntimeException { }
```

### DTOs (Siempre usar)
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private String id;
    private String nombre;
    private String correo;
    private UserRole rol;
    // NO incluir: passwordHash, refreshToken, tokenRecuperacion
}
```

### Timestamps
```java
@Builder.Default
private LocalDateTime fechaCreacion = LocalDateTime.now();

// En DTOs, nunca enviar:
// - passwordHash
// - refreshToken
// - tokenRecuperacion
// - tokenRecuperacionExpira
```

---

## 🔐 SEGURIDAD (CRÍTICO)

### JWT Flow
```
1. POST /auth/register → Usuario creado con passwordHash
2. POST /auth/login → accessToken (15min) + refreshToken (7 días)
3. Cliente guarda tokens en sessionStorage
4. Cliente envía: Authorization: Bearer <accessToken>
5. JwtAuthenticationFilter valida token en cada request
6. Si expirado → Cliente usa refreshToken → POST /auth/refresh
7. POST /auth/logout → refreshToken = null en MongoDB
```

### Roles y Permisos
```
CLIENTE
  ✓ Ver tienda pública
  ✓ Ver mis pedidos
  ✓ Crear pedido
  ✓ Escribir reseña
  ✗ Ver dashboard admin
  ✗ Crear productos

ARTESANA
  ✓ Ver dashboard
  ✓ Ver mis actividades de producción
  ✓ Actualizar actividades
  ✗ Acceder a logística
  ✗ Ver ventas totales

BODEGA
  ✓ Ver inventario de hilos
  ✓ Crear/editar colores hilo
  ✓ Ver stock crítico
  ✗ Crear productos

LOGISTICA
  ✓ Ver despachos
  ✓ Actualizar estado de envío
  ✗ Acceder a inventario

MERCADEO
  ✓ Ver analíticas
  ✓ Ver ventas por categoría
  ✗ Crear productos

ADMIN
  ✓ TODO (acceso total)
```

### NO Hacer Nunca
```
❌ Enviar passwordHash en respuesta
❌ Guardar tokens en localStorage
❌ Hard-codear secrets en application.properties
❌ Usar Basic Auth en lugar de JWT
❌ Confiar en client-side validation solo
❌ Crear usuarios sin BCrypt
❌ Permitir CORS * en producción
```

---

## 🗄️ BASE DE DATOS

### Connection String
```
MongoDB URI: mongodb+srv://Sara:***@empresarialessara.vmeolxj.mongodb.net/crocheting?retryWrites=true&w=majority
Database: crocheting
Cluster: empresarialessara.vmeolxj
```

### Colecciones Requeridas
```
✅ usuarios          (Sprint 1)
✅ productos         (Sprint 2)
✅ coloresHilo       (Sprint 2)
✅ configuracion     (Sprint 2)
⏳ pedidos           (Sprint 3)
⏳ resenas           (Sprint 3)
⏳ despachos         (Sprint 4)
⏳ devoluciones      (Sprint 4)
⏳ actividades       (Sprint 4)
```

### Índices
```
usuarios:
  db.usuarios.createIndex({ "correo": 1 }, { "unique": true })

productos:
  db.productos.createIndex({ "estado": 1, "stock": 1 })
  db.productos.createIndex({ "categoria": 1 })

pedidos:
  db.pedidos.createIndex({ "usuarioId": 1 })
  db.pedidos.createIndex({ "estado": 1 })
```

---

## 🚀 COMPILACIÓN Y EJECUCIÓN

### Compilar
```bash
.\mvnw.cmd clean compile
```

### Tests
```bash
.\mvnw.cmd test
```

### Run
```bash
.\mvnw.cmd spring-boot:run
```

### URLs
```
Frontend:       http://localhost:8080/
API:            http://localhost:8080/api/
Swagger:        http://localhost:8080/api/swagger-ui.html
Health:         http://localhost:8080/api/actuator/health
```

---

## 📝 PASOS PARA AÑADIR NUEVA FEATURE (TEMPLATE)

### 1. Entity (src/main/java/.../entity/)
```java
@Document(collection = "micoleccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiEntity {
    @Id
    private String id;
    private String nombre;
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
```

### 2. Repository (src/main/java/.../repository/)
```java
@Repository
public interface MiRepository extends MongoRepository<MiEntity, String> {
    List<MiEntity> findByNombre(String nombre);
}
```

### 3. DTO (src/main/java/.../dto/)
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiDTO {
    private String id;
    private String nombre;
}
```

### 4. Service (src/main/java/.../service/)
```java
@Service
public class MiService {
    @Autowired
    private MiRepository miRepository;
    
    public MiDTO crear(MiDTO dto) { ... }
    public List<MiDTO> listarTodos() { ... }
    public MiDTO obtenerPorId(String id) { ... }
    public MiDTO actualizar(String id, MiDTO dto) { ... }
    public void eliminar(String id) { ... }
}
```

### 5. Controller (src/main/java/.../controller/)
```java
@RestController
@RequestMapping("/mi-endpoint")
@Tag(name = "Mi Feature")
public class MiController {
    @Autowired
    private MiService miService;
    
    @GetMapping
    public ResponseEntity<List<MiDTO>> listar() {
        return ResponseEntity.ok(miService.listarTodos());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MiDTO> crear(@Valid @RequestBody MiDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(miService.crear(dto));
    }
}
```

### 6. Test (src/test/.../SarichiCrochetingApplicationTests.java)
```java
@Test
public void testMiFeature() {
    // Arrange
    // Act
    // Assert
}
```

### 7. Frontend (src/main/resources/static/)
```html
<html>
<head>
    <script src="sarichi.js"></script>
</head>
<body>
    <script>
        Auth.requireAuth();
        async function cargarDatos() {
            const data = await Auth.apiGet('/api/mi-endpoint');
            // Renderizar
        }
    </script>
</body>
</html>
```

---

## 📚 COMANDOS ÚTILES

```bash
# Compilar sin tests
.\mvnw.cmd clean compile -DskipTests

# Ejecutar un test específico
.\mvnw.cmd test -Dtest=SarichiCrochetingApplicationTests#testNombreTest

# Ver dependencias
.\mvnw.cmd dependency:tree

# Limpiar caché
rm -r target

# Format code (si tienes plugin)
.\mvnw.cmd spring-javaformat:apply

# Build JAR (para producción)
.\mvnw.cmd clean package -DskipTests
```

---

## 🔗 REFERENCIAS IMPORTANTES

**Archivo de Configuración:**  
`src/main/resources/application.properties`
```properties
server.port=8080
server.servlet.context-path=/api

spring.data.mongodb.uri=mongodb+srv://...
spring.data.mongodb.database=crocheting

app.jwt.secret=your-secret-key
app.jwt.expiration=900000
app.jwt.refresh-expiration=604800000

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

**Archivo de Sustentación:**  
`SUSTENTACION.md` - Checklist para presentación en vivo

**Git Commits Importantes:**
```
f00530d - Sprint 2 completo: KPIs reales, productos, colores hilo, seguridad
34d0760 - Sprint 1 completo: Autenticación, JWT, Bootstrap
```

---

## ✅ CHECKLIST ANTES DE CADA SPRINT

- [ ] Compilación limpia: `mvn clean compile` sin errores
- [ ] Tests en verde: `mvn test` con 100% passing
- [ ] Swagger actualizado con nuevos endpoints
- [ ] MongoDB seeded con datos de prueba
- [ ] Postman collection (opcional) con endpoints
- [ ] Git commit con descripción clara
- [ ] Frontend integrado (no hardcoded)
- [ ] @PreAuthorize en endpoints protegidos
- [ ] DTOs sin campos sensibles
- [ ] GlobalExceptionHandler maneja nueva excepción
- [ ] README/SUSTENTACION.md actualizado

---

## 🎯 SIGUIENTE PASO

**Sprint 3 - Pedidos y Ventas:**

1. Crear Pedido entity con ItemPedido
2. Crear PedidoService con métodos CRUD
3. Crear PedidoController con @PreAuthorize
4. Actualizar DashboardService para calcular ventas reales
5. Frontend: carrito.html, mis-pedidos.html
6. Añadir Resena entity para reseñas de productos
7. Tests: verificar que pedidos se crean correctamente
8. MongoDB: crear índices en pedidos.usuarioId y pedidos.estado

---

**Arquitecto:** Copilot AI (Especializado en Spring Boot)  
**Última Actualización:** 2026-05-25  
**Estado:** Listo para Sprint 3 ✅
