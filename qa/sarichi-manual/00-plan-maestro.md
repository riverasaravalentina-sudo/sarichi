# Plan maestro de pruebas manuales — Sarichi Crocheting

**Documento:** QA-SARICHI-00 · **Versión:** 2.0 · **Fecha:** 19/10/2026
**Alcance:** página principal / sistema completo · **SRS de referencia:** `SRS_Sarichi_Crocheting_v2 DEFINITIVA.docx`
**Código:** `C:\TrabajosSprint\SarichiCrocheting` (rama `master`) · **Despliegue:** `https://sarichi-crocheting.onrender.com`

---

## 0. Documentos de fase

| Fase | Documento | Contenido |
|---|---|---|
| 1 | `00a-analisis-srs.md` | Análisis completo del SRS (actores, módulos, 67 RF, 9 RNF, 16 HU, colecciones, sprints, criterios) |
| 2 | `00b-inventario-requisitos.md` | Inventario RF/RNF/HU con estado real, 12 contradicciones, 12 ambigüedades, 7 vacíos, 8 dependencias y 10 riesgos |
| 3 | `00-plan-maestro.md` (este) | Estrategia, entornos, roles, plantilla de caso, 11 bloques y orden de ejecución |
| 4 | `01-matriz-trazabilidad.csv` | 76 requisitos (67 RF + 9 RNF) → casos → estado |
| 5 | `02-casos-bloque-N.csv` | Casos por bloque en la plantilla de 19 columnas |
| 6 | `03-hallazgos.csv` / `04-defectos.csv` | Desviaciones detectadas / defectos de ejecución |
| 7 | `datos-prueba.md` | Cuentas, endpoints, comandos (PowerShell/Invoke-RestMethod/Mongo) |
| 8 | `informe-qa.html` | Resumen navegable del avance |

---

## 1. Objetivo

Verificar de forma **manual** el comportamiento real de la plataforma frente a lo exigido en el SRS, priorizando riesgos de seguridad y los flujos críticos de negocio. El trabajo se entrega **bloque por bloque**, con aprobación entre cada uno.

## 2. Entorno de ejecución

| Tipo de prueba | Producción (Render) | Local |
|---|---|---|
| Smoke, navegación, responsive, SEO, cabeceras | Sí | Sí |
| Login / registro / recuperación con cuentas semilla | Sí (solo lectura) | Sí (completo) |
| Permisos por rol (lectura) | Sí | Sí |
| IDOR, escalada de privilegios, JWT manipulado/expirado, fuerza bruta | No | Sí |
| Pagos (mock/sandbox), stock, cambios de estado | No destructivo | Sí |
| Correos | No (SMTP sin credenciales) | Sí (leer logs) |

- **Producción:** `https://sarichi-crocheting.onrender.com`
- **Local:** `.\mvnw.cmd spring-boot:run` (Mongo Atlas, Wompi y transportadora en mock, correo solo en logs).
- **Restricción:** ninguna prueba destructiva se ejecuta en producción.
- Herramientas de apoyo: navegador (DevTools), `Invoke-RestMethod` (PowerShell 7), cliente Mongo (para verificar colecciones).

## 3. Criterio de evaluación (mixto)

1. Si el sistema se aparta de una exigencia del SRS → **hallazgo** con severidad.
2. Nombres, rutas e interfaz se describen según el **sistema real** (el SRS asume un frontend React que no existe).

**Prioridad:** Crítica · Alta · Media · Baja. **Severidad del defecto:** Crítica / Alta / Media / Baja.
**Estado de caso:** *Pendiente · En ejecución · Aprobada · Fallida · Bloqueada · No aplicable*.

## 4. Plantilla de caso (20 columnas)

`ID · Modulo · RF · Rol_Ejecutor · Tipo · Prioridad · Entorno · Titulo · Objetivo · Precondiciones · Datos · Pasos · Validaciones_API_BD · Resultado_Esperado · Resultado_Esperado_por_Accion · Resultado_Real · Estado · Severidad · Evidencia · Herramienta`
*(20 columnas: las 18 del prompt maestro + `Entorno` + `Herramienta`.)*

- **Tipo:** Funcional · Negativo · Borde · Seguridad · Usabilidad · Responsive · Rendimiento · Integración · E2E.
- **Rol_Ejecutor:** CLIENTE · ARTESANA · ADMIN · MERCADEO · LOGISTICA · BODEGA · ANÓNIMO.
- **Validaciones_API_BD:** endpoint/HTTP esperado y colección/campo a inspeccionar (ej. `POST /api/auth/login → 200`; `usuarios.email`).

## 5. Bloques de trabajo (11)

| Bloque | Alcance | Sección SRS | Requisitos | Prioridad | Manual ahora |
|---|---|---|---|---|---|
| **1** | Autenticación, sesión, roles y seguridad | 3.0 / RF-AUTH | 7 RF | Crítica | **Sí** (27 casos listos) |
| **2** | Dashboard por rol | 3.1 / RF-DASH | 10 RF | Alta | **Sí** (24 casos listos) |
| **3** | Catálogo, carrito, checkout y pagos | 3.2 / RF-CAT | 8 RF | Crítica | **Sí** (34 casos listos) |
| **4** | Personalizador visual | 3.3 / RF-PER | 7 RF | Alta | **Sí** (24 casos listos) |
| **5** | Pedidos personalizados y chat | 3.4 / RF-PED | 7 RF | Alta | **Sí** (26 casos listos) |
| **6** | Inventario y bodega | 3.5 / RF-BOD | 5 RF | Media | **Sí** (25 casos listos) |
| **7** | Logística y despachos | 3.6 / RF-LOG | 5 RF | Media | **Sí** (21 casos listos) |
| **8** | Mercadeo, blog, galería y retos | 3.7 / RF-MKT | 5 RF | Media | **Sí** (25 casos listos) |
| 9 | Administración y configuración | 3.8 / RF-ADM | 7 RF | Media | **Sí** (30 casos listos) |
| 10 | Transversales (auth global, notificaciones, búsqueda, auditoría, export, modo oscuro) | 8.1 / RF-G | 6 RF | Media | **Sí** (23 casos listos) |
| 11 | NFR: seguridad, rendimiento, accesibilidad y despliegue | 8.2 / RNF-G | 9 RNF | Alta | **Sí** (13 casos: manual + herramienta) |

### 5.1 Bloques 1–10: verificables manualmente
Se ejecutan con navegador + API; cada bloque genera su `02-casos-bloque-N.csv`, se aprueba y se pasa al siguiente.

### 5.2 Bloque 11: manual vs requiere herramienta

| Requisito | Verificable manualmente | Requiere herramienta |
|---|---|---|
| RNF-G-01 Disponibilidad | — | Monitoreo / Uptime |
| RNF-G-02 Rendimiento 100 usuarios | — | JMeter / k6 |
| RNF-G-03 Responsividad 360px | Sí (DevTools) | — |
| RNF-G-04 Seguridad OWASP | Parcial (cabeceras, CSRF, sesión) | OWASP ZAP |
| RNF-G-05 Cifrado | Parcial (algoritmo del JWT, TLS) | Análisis de token |
| RNF-G-06 Ley 1581 | Sí (flujo de eliminación) | — |
| RNF-G-07 WCAG 2.1 AA | Sí (teclado, contraste, ARIA) | Lighthouse |
| RNF-G-08 Cobertura 70 % | — | JaCoCo |
| RNF-G-09 Backups / restore | — | Consola Atlas |

## 6. Orden de ejecución acordado

1. ✅ Fase 1 — análisis del SRS.
2. ✅ Fase 2 — inventario de requisitos.
3. ✅ Fase 3 — este plan (re-baselizado a 11 bloques). Bloques 1 a 11 migrados (27 + 24 + 34 + 24 + 26 + 25 + 21 + 25 + 30 + 23 + 13 casos).
4. ✅ Migrar **Bloque 1** al formato de 20 columnas (27 casos: AUTH-01..AUTH-27) y corregir `01-matriz-trazabilidad.csv`.
5. ✅ Migrar **Bloque 2** al formato de 20 columnas (24 casos: DASH-01..DASH-24) con estado real verificado en código.
6. ✅ Migrar **Bloque 3** al formato de 20 columnas (34 casos: CAT-01..CAT-34) con estado real verificado en código.
7. ✅ Migrar **Bloque 4** al formato de 20 columnas (24 casos: PER-01..PER-24) con estado real verificado en código.
8. ⏳ **Ejecutar las pruebas manuales de los Bloques 1 a 4** y registrar resultados en `04-defectos.csv`.
9. ✅ Migrar **Bloque 5** al formato de 20 columnas (26 casos: PED-01..PED-26) con estado real verificado en código.
10. ✅ Migrar **Bloque 6** al formato de 20 columnas (25 casos: BOD-01..BOD-25) con estado real verificado en código.
11. ✅ Migrar **Bloque 7** al formato de 20 columnas (21 casos: LOG-01..LOG-21) con estado real verificado en código.
12. ✅ Migrar **Bloque 8** al formato de 20 columnas (25 casos: MKT-01..MKT-25) con estado real verificado en código.
13. ✅ Migrar **Bloque 9** al formato de 20 columnas (30 casos: ADM-01..ADM-30) con estado real verificado en código.
14. ✅ Migrar **Bloque 10** al formato de 20 columnas (23 casos: TRV-01..TRV-23) con estado real verificado en código.
15. ✅ Migrar **Bloque 11** al formato de 20 columnas (13 casos: NFR-01..NFR-13).
16. ⏳ **Ejecutar las pruebas manuales de los Bloques 1 a 11** y registrar resultados en `04-defectos.csv`. Los casos NFR con herramienta (NFR-02, NFR-03, NFR-12, NFR-13) se marcan *No aplicable* si no hay herramienta.

## 7. Entregables

| Archivo | Contenido |
|---|---|
| `00-plan-maestro.md` | Este documento |
| `00a-analisis-srs.md` / `00b-inventario-requisitos.md` | Fases 1 y 2 |
| `01-matriz-trazabilidad.csv` | 76 requisitos (67 RF + 9 RNF) |
| `02-casos-bloque-1.csv` | Casos del Bloque 1 — 27 casos (plantilla de 20 columnas) |
| `02-casos-bloque-2.csv` | Casos del Bloque 2 — 24 casos (plantilla de 20 columnas) |
| `02-casos-bloque-3.csv` | Casos del Bloque 3 — 34 casos (plantilla de 20 columnas) |
| `02-casos-bloque-4.csv` | Casos del Bloque 4 — 24 casos (plantilla de 20 columnas) |
| `02-casos-bloque-5.csv` | Casos del Bloque 5 — 26 casos (plantilla de 20 columnas) |
| `02-casos-bloque-6.csv` | Casos del Bloque 6 — 25 casos (plantilla de 20 columnas) |
| `02-casos-bloque-7.csv` | Casos del Bloque 7 — 21 casos (plantilla de 20 columnas) |
| `02-casos-bloque-8.csv` | Casos del Bloque 8 — 25 casos (plantilla de 20 columnas) |
| `02-casos-bloque-9.csv` | Casos del Bloque 9 — 30 casos (plantilla de 20 columnas) |
| `02-casos-bloque-10.csv` | Casos del Bloque 10 — 23 casos (plantilla de 20 columnas) |
| `02-casos-bloque-11.csv` | Casos del Bloque 11 — 13 casos (plantilla de 20 columnas) |
| `03-hallazgos.csv` | 31 desviaciones del análisis previo |
| `04-defectos.csv` | Defectos detectados en ejecución |
| `datos-prueba.md` | Cuentas, endpoints y comandos |
| `informe-qa.html` | Resumen navegable |

## 8. Limitaciones conocidas

- SMTP sin credenciales: la recuperación devuelve el enlace en la respuesta (H-06).
- Wompi y transportadora en modo mock.
- No existen rate limiting ni bloqueo de cuenta.
- La verificación de correo en el registro no está implementada.
- No hay cuentas semilla MERCADEO ni BODEGA (H-26).
- Sin sistema de notificaciones por correo activo salvo la recuperación (H-17).
- Sin búsqueda global (H-22), sin auditoría (H-18), sin modo oscuro funcional (H-30).
- Ley 1581: sin consentimiento en registro ni eliminación de cuenta (H-31).
- JWT firmado con HS256 en vez de RS256 (H-02).
- La Rifa existe en el sistema pero no figura en el SRS (fuera de alcance funcional, se prueba aparte).
- Cobertura RNF: disponibilidad, rendimiento, cobertura y backups no son verificables manualmente (ver 5.2).
