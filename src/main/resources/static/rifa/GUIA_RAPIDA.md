# ⚡ Guía Rápida - Crocheting Sarichi

## 🚀 Startup rápido

```bash
# Terminal 1: Compilar y ejecutar
cd C:\TrabajosSprint\SarichiCrocheting
.\mvnw.cmd spring-boot:run

# Terminal 2: Tests en paralelo
.\mvnw.cmd test

# Acceso
Frontend:  http://localhost:8080
Swagger:   http://localhost:8080/api/swagger-ui.html
```

## 🔓 Credenciales de Prueba

```
Email:    admin@sarichi.com
Password: Admin@2024
Role:     ADMIN

Email:    cliente@test.com
Password: Cliente@2024
Role:     CLIENTE
```

## 📊 KPIs Actuales (datos reales)

```
totalClientes:    3
totalProductos:   7 (6 activos)
stockCritico:     4
ventasHoy:        0 (Sprint 3)
pedidosPendientes: 0 (Sprint 3)
```

## 🔑 JWT Token (Ejemplo)

```
Header:   Authorization: Bearer <token>
Duration: 15 minutos (accessToken)
Refresh:  7 días (refreshToken)
```

## 📋 Estructura de Carpetas

```
src/main/java/com/sarichi/crocheting/
├── entity/       → Entidades MongoDB (@Document)
├── repository/   → Interfaces MongoDB
├── service/      → Lógica de negocio
├── controller/   → Endpoints REST
├── dto/          → Objetos de transferencia
├── config/       → Configuración Spring
└── security/     → JWT y autenticación

src/main/resources/static/
├── index.html          → Home
├── login.html          → Login
├── dashboard.html      → Dashboard admin
├── tienda.html         → Catálogo público
├── productos-admin.html → CRUD productos
├── inventario.html     → Gestión hilos
└── sarichi.js          → API client global
```

## 🛠️ Comandos Más Usados

```bash
# Compilar
.\mvnw.cmd clean compile

# Tests
.\mvnw.cmd test

# Ejecutar app
.\mvnw.cmd spring-boot:run

# Ejecutar test específico
.\mvnw.cmd test -Dtest=SarichiCrochetingApplicationTests#testRegistroUsuario

# Build JAR
.\mvnw.cmd clean package -DskipTests

# Ver dependencias
.\mvnw.cmd dependency:tree

# Limpiar todo
rm -r target && .\mvnw.cmd clean
```

## 🔄 Flujo de Desarrollo

```
1. Modifica código en src/main/java/...
   ↓ (DevTools hot reload automático)
2. Verifica en navegador o Postman
   ↓
3. Agrega tests en src/test/...
4. Ejecuta: .\mvnw.cmd test
   ↓
5. Git commit: git add -A && git commit -m "..."
   ↓
6. Push: git push origin main
```

## 🧪 Testing en Postman

### Registrar Usuario
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nombre": "TestUser",
  "correo": "test@example.com",
  "telefono": "3001234567",
  "contrasena": "Test@2024",
  "confirmarContrasena": "Test@2024",
  "rol": "ADMIN"
}
```

### Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "correo": "admin@sarichi.com",
  "contrasena": "Admin@2024"
}
```

### KPIs (requiere token)
```
GET http://localhost:8080/api/dashboard/kpis
Authorization: Bearer <accessToken>
```

### Productos (público)
```
GET http://localhost:8080/api/productos
GET http://localhost:8080/api/productos?categoria=Amigurumis
GET http://localhost:8080/api/productos?busqueda=osito
```

### Crear Producto (ADMIN)
```
POST http://localhost:8080/api/productos
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Nuevo Producto",
  "descripcion": "...",
  "precioBase": 50000,
  "categoria": "Amigurumis",
  "stock": 10,
  "tiempoElaboracionDias": 3
}
```

## 📊 Base de Datos

### Conectar MongoDB Compass
```
URI: mongodb+srv://Sara:***@empresarialessara.vmeolxj.mongodb.net
Database: crocheting
```

### Collections
```
usuarios       → Usuarios del sistema
productos      → Catálogo de productos
coloresHilo    → Inventario de hilos
configuracion  → Configuración del sistema
```

## 🔐 Roles y Permisos (Quick Reference)

| Rol | Dashboard | Productos | Inventario | Admin |
|-----|-----------|-----------|-----------|-------|
| CLIENTE | ✗ | ✓ (ver) | ✗ | ✗ |
| ARTESANA | ✓ | ✓ | ✗ | ✗ |
| BODEGA | ✓ | ✓ | ✓ | ✗ |
| LOGISTICA | ✓ | ✗ | ✗ | ✗ |
| MERCADEO | ✓ | ✗ | ✗ | ✗ |
| ADMIN | ✓ | ✓ | ✓ | ✓ |

## 🐛 Debugging

```bash
# Ver logs en consola
.\mvnw.cmd spring-boot:run

# Debug a puerto específico
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"

# Ver requests HTTP
# En browser F12 → Network tab → Verificar XHR requests
```

## 📝 Git Workflow

```bash
# Ver estado
git status

# Agregar cambios
git add -A

# Crear commit
git commit -m "Descripción clara del cambio"

# Ver historial
git log --oneline

# Push
git push origin main
```

## ❌ Errores Comunes

### `PlaceholderResolutionException`
```
Solución: Verificar application.properties tiene app.jwt.secret
```

### `Cannot find symbol getEstado()`
```
Solución: Ejecutar .\mvnw.cmd clean compile (Lombok cache)
```

### `403 Forbidden en endpoint público`
```
Solución: Verificar SecurityConfig.permitAll() incluye la ruta
```

### `Token expired`
```
Solución: Usar refreshToken para obtener nuevo accessToken
```

## 🌐 URLs Importantes

```
Frontend:     http://localhost:8080/
API:          http://localhost:8080/api/
Swagger:      http://localhost:8080/api/swagger-ui.html
Health:       http://localhost:8080/api/actuator/health
MongoDB Atlas: https://cloud.mongodb.com/ (empresarialessara)
GitHub:       https://github.com/tu-usuario/SarichiCrocheting
```

## 📚 Archivos Clave

```
CONTEXTO_PROYECTO.md    ← LEER PRIMERO: Guía completa
SUSTENTACION.md         ← Checklist para presentación
GUIA_RAPIDA.md          ← Este archivo (referencia rápida)
pom.xml                 ← Dependencias Maven
application.properties  ← Configuración Spring
```

## 🎯 Próximo Paso

Para implementar **Sprint 3 (Pedidos y Ventas)**:

1. Lee: `CONTEXTO_PROYECTO.md` sección "SPRINT 3 PENDIENTE"
2. Sigue el template: "PASOS PARA AÑADIR NUEVA FEATURE"
3. Crea las 4 entities nuevas: Pedido, ItemPedido, Resena, VentasPorPeriodo
4. Implementa los servicios y controllers
5. Actualiza DashboardService para ventas reales
6. Agrega tests unitarios
7. Seed MongoDB con datos de prueba

## 💡 Tips

- Usa Swagger UI para explorar endpoints: `http://localhost:8080/api/swagger-ui.html`
- Postman collection facilita testing: importar endpoints de Swagger
- MongoDB Compass para visualizar datos en tiempo real
- DevTools hot reload: cambios en Java se aplican automáticamente
- Verificar logs en consola para debuggear

---

**Creado:** 2026-05-25  
**Versión:** Sprint 2 Completado ✅  
**Estado del Proyecto:** Listo para Sprint 3
