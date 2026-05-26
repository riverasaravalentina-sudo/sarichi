# Test Suite - Sarichi Crocheting Sprint 3 & 4

## 📋 Overview

Complete test suite for Sarichi Crocheting platform covering Sprint 3 (Orders, Sales, Reviews) and Sprint 4 (Logistics, Production, Returns).

**Total Test Files:** 8  
**Total Test Methods:** 18+  
**Test Framework:** JUnit 5 + Mockito + Spring Test  

## 📁 Test Structure

```
src/test/java/com/sarichi/crocheting/
├── integration/
│   ├── controller/
│   │   ├── PedidoControllerIntegrationTest.java
│   │   ├── ResenaControllerIntegrationTest.java
│   │   ├── DashboardControllerIntegrationTest.java
│   │   ├── DespachoControllerIntegrationTest.java
│   │   ├── DevolucionControllerIntegrationTest.java
│   │   └── ActividadProduccionControllerIntegrationTest.java
│   └── e2e/
│       └── E2EFlujosTest.java
└── unit/
    └── service/
        └── PedidoServiceTest.java
```

## 🚀 Running Tests

### Run All Tests
```bash
./mvnw clean test
```

### Run Specific Test Types
```bash
# Unit tests only
./mvnw test -Dtest=*ServiceTest

# Integration tests only
./mvnw test -Dtest=*IntegrationTest

# E2E tests only
./mvnw test -Dtest=E2EFlujosTest
```

### Generate Coverage Report
```bash
./mvnw clean test jacoco:report
# Report will be at: target/site/jacoco/index.html
```

## 📊 Test Coverage

### Integration Tests (6 files)

Each controller test verifies:
- ✅ Authentication (401 Unauthorized)
- ✅ Authorization (403 Forbidden for wrong roles)
- ✅ Happy path (200/201 for valid requests)
- ✅ Error cases (404 Not Found)

**Controllers Tested:**
- `PedidoController` - Order endpoints
- `ResenaController` - Review endpoints
- `DashboardController` - Analytics endpoints
- `DespachoController` - Shipment endpoints
- `DevolucionController` - Returns endpoints
- `ActividadProduccionController` - Production activity endpoints

### Unit Tests (1 file)

`PedidoServiceTest.java` demonstrates:
- ✅ Repository mocking with Mockito
- ✅ Service method testing
- ✅ List filtering and counting operations

### E2E Tests (1 file)

`E2EFlujosTest.java` contains 3 complete user workflows:

#### 1. Compra y Reseña (Sprint 3)
```
Cliente → Crea Pedido → Admin Entrega → Cliente Reseña
```

#### 2. Despacho y Devolución (Sprint 4)
```
LOGISTICA → Crea Despacho → Marca Entregado → Cliente Devuelve → Admin Aprueba
```

#### 3. Producción (Sprint 4)
```
Admin → EN_PRODUCCION → ARTESANA → Inicia → Fotos → Completa → Cliente Ve Fotos
```

## 🔒 Security Tests

All tests verify role-based access control:

| Endpoint | Public | AUTH | ADMIN | ARTESANA | LOGISTICA | MERCADEO |
|----------|--------|------|-------|----------|-----------|----------|
| GET /pedidos | ✗ | ✓ | ✓ | ✓ | ✗ | ✗ |
| POST /pedidos | ✗ | ✓ | ✓ | ✓ | ✓ | ✗ |
| GET /resenas/producto/{id} | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| GET /dashboard/kpis | ✗ | ✗ | ✓ | ✓ | ✓ | ✓ |
| POST /despachos | ✗ | ✗ | ✓ | ✗ | ✓ | ✗ |
| GET /despachos/{id}/seguimiento | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| GET /actividades/mis-actividades | ✗ | ✗ | ✓ | ✓ | ✗ | ✗ |
| POST /devoluciones/solicitar | ✗ | ✓ | ✓ | ✓ | ✓ | ✗ |

## 💡 Test Patterns

### Integration Test Pattern
```java
@SpringBootTest
@AutoConfigureMockMvc
class ControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/endpoint"))
                .andExpect(status().isUnauthorized());
    }
}
```

### Unit Test Pattern
```java
class ServiceTest {
    
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceMethod() {
        when(repository.find("id")).thenReturn(List.of());
        // Test assertion
    }
}
```

### E2E Test Pattern
```java
@Transactional  // Auto-rollback after test
class E2EFlujosTest {
    
    @Test
    void testCompleteUserFlow() throws Exception {
        // Multiple steps across different user roles
        mockMvc.perform(post("/api/resource"))
                .andExpect(status().isCreated());
    }
}
```

## 📝 Test Naming Convention

Tests follow the pattern: `methodName_Condition_ExpectedResult`

Example:
- ✅ `test_get_pedidos_sin_auth()` - clear expectation
- ✅ `@DisplayName("getPedidos_sinAutenticacion_retorna401")` - human readable

## ✅ Validation Checklist

- [x] All tests compile without errors
- [x] Tests follow JUnit 5 standards
- [x] Authentication tests validate 401 responses
- [x] Authorization tests validate 403 responses
- [x] Happy path tests validate 200/201 responses
- [x] Error path tests validate 404 responses
- [x] E2E tests cover complete user workflows
- [x] Mockito used for service layer mocking
- [x] AssertJ used for fluent assertions
- [x] All tests ready for CI/CD pipeline

## 🔧 Technologies

- **Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito 4.x
- **Spring Test:** MockMvc for HTTP testing
- **Assertions:** AssertJ (fluent style)
- **Build Tool:** Maven with surefire plugin

## 📚 Documentation

For more information:
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Test Documentation](https://spring.io/guides/gs/testing-web/)
- [AssertJ Documentation](https://assertj.github.io/assertj-core-features-highlight.html)

---

**Last Updated:** 2025-05-26  
**Status:** ✅ Complete and Ready for CI/CD
