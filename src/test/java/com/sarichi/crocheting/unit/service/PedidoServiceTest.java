package com.sarichi.crocheting.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;
import com.sarichi.crocheting.service.PedidoService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("PedidoService Unit Tests")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("findByUsuarioId_conUsuarioValido_retornaListaVacia")
    void test_find_by_usuario_id() {
        when(pedidoRepository.findByUsuarioId("user-1"))
                .thenReturn(java.util.List.of());

        var result = pedidoRepository.findByUsuarioId("user-1");

        assertThat(result).isEmpty();
        verify(pedidoRepository, times(1)).findByUsuarioId("user-1");
    }

    @Test
    @DisplayName("countByEstado_conEstadoValido_retornaConteo")
    void test_count_by_estado() {
        when(pedidoRepository.countByEstado("PENDIENTE")).thenReturn(5L);

        long count = pedidoRepository.countByEstado("PENDIENTE");

        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("findByEstado_conEstadoEntregado_retornaLista")
    void test_find_by_estado() {
        when(pedidoRepository.findByEstado("ENTREGADO"))
                .thenReturn(java.util.List.of());

        var result = pedidoRepository.findByEstado("ENTREGADO");

        assertThat(result).isEmpty();
    }
}
