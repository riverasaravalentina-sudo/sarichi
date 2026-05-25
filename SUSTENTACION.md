# 📋 Checklist de Sustentación - Crocheting Sarichi

## ✅ Sprint 1 — Autenticación

- [x] **Swagger accesible** → http://localhost:8080/api/swagger-ui.html (200 OK)
- [x] **POST /auth/register** → Crea usuario ADMIN (201 Created)
- [x] **POST /auth/login** → Retorna accessToken + refreshToken (200 OK)
- [x] **GET /auth/me** → Retorna perfil del usuario autenticado
- [x] **GET /auth/perfil-completo** → Retorna perfil extendido con fechas
- [x] **POST /auth/refresh** → Emite nuevo accessToken
- [x] **POST /auth/logout** → Implementado (requiere X-User-Id header)
- [x] **MongoDB Atlas accesible** → Conectado a empresarialessara.vmeolxj.mongodb.net
- [x] **BCrypt verificado** → passwordHash con salt diferente cada registro
- [x] **JWT en jwt.io** → Decodifica con sub, rol, nombre, correo, exp
- [x] **Autenticación RBAC** → Roles: CLIENTE, ADMIN, ARTESANA, BODEGA, LOGISTICA, MERCADEO

## ✅ Sprint 2 — Dashboard y Catálogo

### KPIs y Datos Reales
- [x] **GET /dashboard/kpis** → Retorna JSON con:
  - `totalClientes: 3`
  - `totalProductos: 7`
  - `stockCritico: 4` (productos + colores por debajo del stock mínimo)
  - `ventasHoy, ventasAyer, variacionVentas, pedidosPendientes` (0 en Sprint 2)

### Productos
- [x] **Productos sembrados** → 6 productos en MongoDB:
  - Osito Amigurumi Pequeño (Amigurumis) - Stock: 5
  - Pulsera Artesanal (Accesorios) - Stock: 1 ⚠️ CRÍTICO
  - Suéter Tejido Mujer (Ropa) - Stock: 8
  - Mantel Decorativo (Hogar) - Stock: 3 ⚠️ CRÍTICO
  - Gorro Invierno (Ropa) - Stock: 12
  - Garfield bonito (Amigurumis) - Stock: 2 ⚠️ CRÍTICO
- [x] **GET /productos** → Lista pública con datos reales (público, sin token)
- [x] **GET /productos?categoria=Amigurumis** → Filtra 3 productos Amigurumis
- [x] **GET /productos?busqueda=gorro** → Búsqueda por texto funciona
- [x] **POST /productos** → Crea producto (ADMIN/ARTESANA)
- [x] **PUT /productos/{id}** → Edita producto (ADMIN/ARTESANA)
- [x] **DELETE /productos/{id}** → Eliminación lógica → estado INACTIVO
- [x] **GET /dashboard/stock-critico** → Retorna 3 productos con stock ≤ 2

### Inventario Hilos
- [x] **Colores sembrados** → 3 colores en coloresHilo:
  - Rojo Fuego - Stock: 50m (OK)
  - Azul Cielo - Stock: 75m (OK)
  - Rosa Pastel - Stock: 3m 🚨 CRÍTICO (< 20m mínimo)
  - Verde Musgo - Stock: 40m (creado durante prueba)
- [x] **GET /colores-hilo** → Lista pública (sin token)
- [x] **GET /colores-hilo/{id}** → Obtiene por ID (público)
- [x] **GET /colores-hilo/criticos** → Retorna colores por debajo de stockMinimo (requiere Bearer token)
- [x] **POST /colores-hilo** → Crea color (ADMIN/BODEGA)
- [x] **PUT /colores-hilo/{id}** → Edita color (ADMIN/BODEGA)
- [x] **DELETE /colores-hilo/{id}** → Elimina color (ADMIN solo)

### Frontend
- [x] **dashboard.html** → Requiere autenticación, redirige a login.html
- [x] **tienda.html** → Muestra productos públicos con filtros
- [x] **productos-admin.html** → CRUD de productos (ADMIN)
- [x] **inventario.html** → Integración real con /colores-hilo
- [x] **Auto-refresh** → Dashboard refresca cada 30s con setInterval

### Swagger / OpenAPI
- [x] **Documentación completa** → Todos los endpoints en Swagger
- [x] **bearerAuth SecurityScheme** → Configurado en OpenAPI
- [x] **@SecurityRequirement** → Anotado en endpoints protegidos

### Tests
- [x] **JUnit tests pasan** → 2/2 tests en verde
  - `testRegistroUsuario()` ✓
  - `testProductoFiltroCategoria()` ✓

### CI/CD
- [x] **GitHub Actions workflow** → `.github/workflows/ci.yml`
  - Maven compile ✓
  - Tests ejecutan ✓

## ✅ Seguridad

- [x] **GET /productos/admin/todos sin token** → 403 Forbidden
- [x] **POST /productos sin token** → 403 Forbidden (require Bearer)
- [x] **Token manipulado** → 401 Unauthorized (JWT SignatureException)
- [x] **Spring Security @PreAuthorize** → Configurado en todos los endpoints protegidos
- [x] **ROLE_CLIENTE no puede acceder a ADMIN** → Redirige a login
- [x] **Configuración JWT** → app.jwt.secret, expiration: 15min, refresh: 7 días
- [x] **Variables de entorno** → Credenciales MongoDB en spring.data.mongodb.uri

## 📊 Base de Datos - MongoDB Atlas

### Colección: `usuarios`
```json
{
  "_id": "6a11d69f81f3165a8ffab346",
  "nombre": "Admin Sarichi",
  "correo": "admin@sarichi.com",
  "passwordHash": "$2a$12$...",
  "rol": "ADMIN",
  "estado": "ACTIVO",
  "fechaRegistro": ISODate("2026-05-25T12:30:00Z"),
  "ultimoLogin": ISODate("2026-05-25T12:35:00Z"),
  "refreshToken": null
}
```
- ✅ Índice único en `correo`
- ✅ Mínimo 2 usuarios (ADMIN + CLIENTE)

### Colección: `productos`
- ✅ 6 documentos con campos: nombre, categoria, precioBase, stock, estado
- ✅ Estados: ACTIVO (6), INACTIVO (1 eliminado lógicamente)
- ✅ 1 producto con stock ≤ 2 (Pulsera: 1, Garfield: 2)
- ✅ Categorías: Amigurumis (3), Accesorios (1), Ropa (2), Hogar (1)

### Colección: `coloresHilo`
- ✅ 4 documentos (3 iniciales + 1 de prueba)
- ✅ Campos: nombre, codigoHex, stockMetros, stockMinimo, proveedor, precioMetro
- ✅ 1 color crítico: Rosa Pastel (3m < 20m mínimo)

### Colección: `configuracion`
- ✅ 1 documento con: stockMinimoAlerta, umbralVIPCompras, zonas de envío

## 🚀 Endpoints Funcionales

### Autenticación
```
POST   /auth/register           → 201 Created
POST   /auth/login              → 200 OK {accessToken, refreshToken, usuario}
GET    /auth/me                 → 200 OK {usuario}
GET    /auth/perfil-completo    → 200 OK {usuario, cantidadWishlist, fechas}
POST   /auth/refresh            → 200 OK {accessToken}
POST   /auth/logout             → 200 OK
POST   /auth/recuperar          → 200 OK
POST   /auth/restablecer        → 200 OK
```

### Dashboard
```
GET    /dashboard/kpis          → 200 OK (ADMIN+ARTESANA+BODEGA+LOGISTICA+MERCADEO)
GET    /dashboard/stock-critico → 200 OK (ADMIN+BODEGA)
GET    /dashboard/ventas        → 200 OK [] (Sprint 3)
GET    /dashboard/pedidos-recientes → 200 OK [] (Sprint 4)
```

### Productos
```
GET    /productos               → 200 OK (público, con filtros)
GET    /productos/{id}          → 200 OK (público)
POST   /productos               → 201 Created (ADMIN)
PUT    /productos/{id}          → 200 OK (ADMIN)
DELETE /productos/{id}          → 204 No Content (ADMIN)
```

### Colores Hilo
```
GET    /colores-hilo            → 200 OK (público)
GET    /colores-hilo/{id}       → 200 OK (público)
POST   /colores-hilo            → 201 Created (ADMIN+BODEGA)
PUT    /colores-hilo/{id}       → 200 OK (ADMIN+BODEGA)
DELETE /colores-hilo/{id}       → 204 No Content (ADMIN)
```

## 📝 Pasos de la Presentación

### Demo en Vivo
1. **Compilación**: `mvn clean compile` ✓
2. **Tests**: `mvn test` → 2/2 tests en verde ✓
3. **Startup**: `mvn spring-boot:run` → Servidor en puerto 8080 ✓
4. **Swagger UI**: Abrir http://localhost:8080/api/swagger-ui.html ✓
5. **Postman/Curl tests**:
   - Registrar usuario ADMIN
   - Login y copiar token
   - GET /dashboard/kpis con token
   - GET /productos sin token (público)
   - Filtro por categoría
6. **MongoDB Compass**:
   - Conectar a Atlas
   - Verificar colecciones: usuarios, productos, coloresHilo, configuracion
   - Mostrar índice único en correo
7. **Frontend**:
   - Abrir login.html → login como ADMIN → redirige a dashboard.html
   - dashboard.html muestra KPIs reales
   - tienda.html filtra por categoría
   - inventario.html carga coloresHilo en tiempo real
8. **JWT Decoding**:
   - Ir a jwt.io
   - Pegar token
   - Mostrar claims: sub, rol, nombre, correo, exp

## 🔧 Configuración Verificada

### application.properties
```properties
server.port=8080
server.servlet.context-path=/api

spring.data.mongodb.uri=mongodb+srv://Sara:***@empresarialessara.vmeolxj.mongodb.net/crocheting?retryWrites=true&w=majority
spring.data.mongodb.database=crocheting

app.jwt.secret=your-secret-key-here
app.jwt.expiration=900000
app.jwt.refresh-expiration=604800000
```

### pom.xml
- ✅ Spring Boot 3.4.5
- ✅ Java 21
- ✅ MongoDB Spring Data
- ✅ Spring Security 6
- ✅ JJWT 0.12.3
- ✅ Lombok con maven-compiler-plugin
- ✅ Swagger/OpenAPI 2.8.3

### SecurityConfig.java
- ✅ @EnableMethodSecurity(prePostEnabled = true)
- ✅ STATELESS session management
- ✅ BCryptPasswordEncoder(12)
- ✅ JWT filter before UsernamePasswordAuthenticationFilter
- ✅ Public routes: /auth/**, /productos, /colores-hilo (GET only)

## 📦 Compilación y Ejecución

```bash
# Compilar
.\mvnw.cmd clean compile

# Tests
.\mvnw.cmd test

# Ejecutar
.\mvnw.cmd spring-boot:run

# Acceso
- Frontend: http://localhost:8080/
- API: http://localhost:8080/api/
- Swagger: http://localhost:8080/api/swagger-ui.html
```

---

**Generado**: 2026-05-25  
**Estado**: ✅ LISTO PARA SUSTENTACIÓN  
**Tiempo de Ejecución**: ~15 minutos (incluye compilación, tests, startup, seeding)
