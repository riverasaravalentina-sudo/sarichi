package com.sarichi.crocheting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sarichi.crocheting.dto.ProductoDTO;
import com.sarichi.crocheting.dto.ProductoFiltroDTO;
import com.sarichi.crocheting.entity.Producto;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.service.ProductoService;

@SpringBootTest
class SarichiCrochetingApplicationTests {

    @Autowired
    private ProductoService productoService;

    @MockBean
    private ProductoRepository productoRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("ProductoService: filtro por categoría retorna solo productos de esa categoría")
    void productoServiceFiltroCategoria() {
        Producto p1 = Producto.builder().id("1").categoria("Amigurumis").estado("ACTIVO").build();
        Producto p2 = Producto.builder().id("2").categoria("Amigurumis").estado("ACTIVO").build();

        Mockito.when(productoRepository.findByCategoriaAndEstado("Amigurumis", "ACTIVO"))
                .thenReturn(List.of(p1, p2));

        ProductoFiltroDTO filtro = ProductoFiltroDTO.builder().categoria("Amigurumis").build();
        List<ProductoDTO> resultado = productoService.listarConFiltros(filtro);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> "Amigurumis".equals(p.getCategoria())));
    }
}

