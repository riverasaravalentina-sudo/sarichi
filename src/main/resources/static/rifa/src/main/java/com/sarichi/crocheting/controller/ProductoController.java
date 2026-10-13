package com.sarichi.crocheting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sarichi.crocheting.dto.ProductoDTO;
import com.sarichi.crocheting.dto.ProductoFiltroDTO;
import com.sarichi.crocheting.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de productos — CRUD y filtros")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * GET /api/productos
     * Público — catálogo con filtros opcionales
     */
    @GetMapping
    @Operation(summary = "Listar productos con filtros",
               description = "Filtra por categoría, color, precio y búsqueda de texto.")
    public ResponseEntity<List<ProductoDTO>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String colorId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0")  int pagina,
            @RequestParam(defaultValue = "12") int tamano) {

        ProductoFiltroDTO filtro = ProductoFiltroDTO.builder()
                .categoria(categoria)
                .colorId(colorId)
                .precioMin(precioMin)
                .precioMax(precioMax)
                .busqueda(busqueda)
                .pagina(pagina)
                .tamano(tamano)
                .build();

        return ResponseEntity.ok(productoService.listarConFiltros(filtro));
    }

    /**
     * GET /api/productos/{id}
     * Público — detalle de un producto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    /**
     * GET /api/productos/admin/todos
     * Solo ADMIN — lista todos incluyendo inactivos
     */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todos los productos (Admin)",
               description = "Incluye productos INACTIVOS. Solo ADMIN.")
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    /**
     * POST /api/productos
     * Solo ADMIN — crear producto
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto",
               description = "Crea un nuevo producto en el catálogo. Solo ADMIN.")
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crear(dto));
    }

    /**
     * PUT /api/productos/{id}
     * Solo ADMIN — actualizar producto
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto",
               description = "Actualiza los datos de un producto existente. Solo ADMIN.")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }

    /**
     * DELETE /api/productos/{id}
     * Solo ADMIN — eliminación lógica
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto",
               description = "Eliminación lógica (estado=INACTIVO). Solo ADMIN.")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/productos/stock-critico
     * ADMIN y BODEGA
     */
    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMIN','BODEGA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Productos con stock crítico",
               description = "Retorna productos con stock menor o igual al umbral (default 2).")
    public ResponseEntity<List<ProductoDTO>> stockCritico(
            @RequestParam(defaultValue = "2") int umbral) {
        return ResponseEntity.ok(productoService.obtenerStockCritico(umbral));
    }
}