package com.sarichi.crocheting.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sarichi.crocheting.dto.CrearPedidoDTO;
import com.sarichi.crocheting.dto.ColorHiloDTO;
import com.sarichi.crocheting.dto.DashboardKpisDTO;
import com.sarichi.crocheting.dto.ItemPedidoRequest;
import com.sarichi.crocheting.dto.ProductoDTO;
import com.sarichi.crocheting.dto.ProductoFiltroDTO;
import com.sarichi.crocheting.dto.RegistroDTO;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.entity.UserRole;
import com.sarichi.crocheting.repository.UsuarioRepository;
import com.sarichi.crocheting.service.AutenticacionService;
import com.sarichi.crocheting.service.ChatService;
import com.sarichi.crocheting.service.ColorHiloService;
import com.sarichi.crocheting.service.DashboardService;
import com.sarichi.crocheting.service.DespachoService;
import com.sarichi.crocheting.service.PedidoService;
import com.sarichi.crocheting.service.ProductoService;
import com.sarichi.crocheting.service.WishlistService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador WEB (Thymeleaf) — Capa MVC server-side.
 * Rutas bajo /web/** — usan sesión HTTP (independiente del JWT).
 * Los @RestController de /api/** NO se modifican.
 */
@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired private ProductoService   productoService;
    @Autowired private ColorHiloService  colorHiloService;
    @Autowired private DashboardService  dashboardService;
    @Autowired private PedidoService     pedidoService;
    @Autowired private DespachoService   despachoService;
    @Autowired private AutenticacionService autenticacionService;
    @Autowired private ChatService       chatService;
    @Autowired private WishlistService   wishlistService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder   passwordEncoder;

    // ── Inicio público ─────────────────────────────────────────────────

    @GetMapping({"", "/"})
    public String inicio(Model model, HttpSession session) {
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb",     session.getAttribute("usuarioWebRol"));

        // Cargar productos públicos para la landing
        try {
            ProductoFiltroDTO filtro = new ProductoFiltroDTO();
            List<ProductoDTO> productos = productoService.listarConFiltros(filtro);
            // Mostrar solo los primeros 6 en la landing
            model.addAttribute("productosDestacados",
                productos.size() > 6 ? productos.subList(0, 6) : productos);
        } catch (Exception e) {
            model.addAttribute("productosDestacados", List.of());
        }
        return "web/index";
    }

    // ── Tienda pública (sin login) ─────────────────────────────────────

    @GetMapping("/tienda")
    public String tienda(@RequestParam(required = false) String categoria,
                         @RequestParam(required = false) String busqueda,
                         @RequestParam(required = false, name = "buscar") String buscar,
                         Model model, HttpSession session) {
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb",     session.getAttribute("usuarioWebRol"));
        ProductoFiltroDTO filtro = new ProductoFiltroDTO();
        filtro.setCategoria(categoria);
        filtro.setBusqueda(busqueda != null ? busqueda : buscar);
        try {
            model.addAttribute("productos", productoService.listarConFiltros(filtro));
        } catch (Exception e) {
            model.addAttribute("productos", List.of());
        }
        model.addAttribute("categoria",  categoria);
        model.addAttribute("busqueda",   busqueda);
        model.addAttribute("categorias", List.of("Amigurumis", "Accesorios", "Ropa", "Hogar"));
        return "web/tienda";
    }

    @GetMapping("/blog")
    public String blog(Model model, HttpSession session) {
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb", session.getAttribute("usuarioWebRol"));
        return "web/blog";
    }

    @GetMapping("/galeria")
    public String galeria(Model model, HttpSession session) {
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb", session.getAttribute("usuarioWebRol"));
        return "web/galeria";
    }

    @GetMapping("/acerca-de")
    public String acercaDe(Model model, HttpSession session) {
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb", session.getAttribute("usuarioWebRol"));
        return "web/acerca-de";
    }

    // ── Login web (sesión HTTP, sin JWT) ───────────────────────────────

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("usuarioWeb") != null) {
            return redirigirPorRol((String) session.getAttribute("usuarioWebRol"));
        }
        return "web/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contrasena,
                                HttpSession session,
                                Model model) {
        try {
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

            if (!passwordEncoder.matches(contrasena, usuario.getPasswordHash())) {
                throw new RuntimeException("Correo o contraseña incorrectos");
            }
            if (!"ACTIVO".equals(usuario.getEstado())) {
                throw new RuntimeException("La cuenta está " + usuario.getEstado().toLowerCase());
            }

            // Guardar en sesión HTTP
            session.setAttribute("usuarioWeb",    usuario.getNombre());
            session.setAttribute("usuarioWebRol", usuario.getRol().name());
            session.setAttribute("usuarioWebId",  usuario.getId());

            // ✅ CORRECCIÓN CLAVE: redirigir según el rol real del usuario
            return redirigirPorRol(usuario.getRol().name());

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "web/login";
        }
    }

    /** Mapea cada rol a su ruta de dashboard correspondiente */
    private String redirigirPorRol(String rol) {
        if (rol == null) return "redirect:/web/login";
        return switch (rol) {
            case "ADMIN"     -> "redirect:/web/dashboard/admin";
            case "ARTESANA"  -> "redirect:/web/dashboard/artesana";
            case "LOGISTICA" -> "redirect:/web/dashboard/logistica";
            case "BODEGA"    -> "redirect:/web/dashboard/bodega";
            case "MERCADEO"  -> "redirect:/web/dashboard/mercadeo";
            case "CLIENTE"   -> "redirect:/web/dashboard/cliente";
            default          -> "redirect:/web/";
        };
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/web/login";
    }

    // ── Registro público ───────────────────────────────────────────────

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registro", new RegistroDTO());
        return "web/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String correo,
                                   @RequestParam(required = false) String telefono,
                                   @RequestParam String contrasena,
                                   @RequestParam String confirmarContrasena,
                                   RedirectAttributes ra) {
        try {
            RegistroDTO dto = RegistroDTO.builder()
                    .nombre(nombre)
                    .correo(correo)
                    .telefono(telefono)
                    .contrasena(contrasena)
                    .confirmarContrasena(confirmarContrasena)
                    .rol(UserRole.CLIENTE)
                    .build();
            autenticacionService.registrar(dto);
            ra.addFlashAttribute("success", "Cuenta creada correctamente. Ya puedes iniciar sesión.");
            return "redirect:/web/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/registro";
        }
    }

    // ── Dashboards por ROL ─────────────────────────────────────────────
    // Cada rol tiene su propio endpoint y template — no comparten vista.

    /** ADMIN: KPIs + acceso a gestión completa */
    @GetMapping("/dashboard/admin")
    public String dashboardAdmin(Model model, HttpSession session) {
        if (!tieneRol(session, "ADMIN")) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        try {
            DashboardKpisDTO kpis = dashboardService.obtenerKpis();
            model.addAttribute("kpis", kpis);
        } catch (Exception e) {
            model.addAttribute("errorKpis", "No se pudieron cargar los KPIs");
        }
        return "web/dashboard/admin";
    }

    /** ARTESANA: pedidos activos + acceso a producción */
    @GetMapping("/dashboard/artesana")
    public String dashboardArtesana(Model model, HttpSession session) {
        if (!tieneRol(session, "ARTESANA")) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        try {
            model.addAttribute("pedidos", pedidoService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("pedidos", List.of());
            model.addAttribute("errorPedidos", "No se pudieron cargar los pedidos");
        }
        return "web/dashboard/artesana";
    }

    /** LOGISTICA: despachos pendientes + gestión de envíos */
    @GetMapping("/dashboard/logistica")
    public String dashboardLogistica(Model model, HttpSession session) {
        if (!tieneRol(session, "LOGISTICA")) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        try {
            model.addAttribute("despachos", despachoService.listarDespachosPendientes());
        } catch (Exception e) {
            model.addAttribute("despachos", List.of());
            model.addAttribute("errorDespachos", "No se pudieron cargar los despachos");
        }
        return "web/dashboard/logistica";
    }

    /** BODEGA: inventario de hilos y productos */
    @GetMapping("/dashboard/bodega")
    public String dashboardBodega(Model model, HttpSession session) {
        if (!tieneRol(session, "BODEGA")) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        try {
            model.addAttribute("colores",    colorHiloService.listarTodos());
            DashboardKpisDTO kpis = dashboardService.obtenerKpis();
            model.addAttribute("stockCritico", kpis.getStockCritico());
        } catch (Exception e) {
            model.addAttribute("colores", List.of());
            model.addAttribute("errorInventario", "No se pudo cargar el inventario");
        }
        return "web/dashboard/bodega";
    }

    /** MERCADEO: analíticas + blog + galería */
    @GetMapping("/dashboard/mercadeo")
    public String dashboardMercadeo(Model model, HttpSession session) {
        if (!tieneRol(session, "MERCADEO")) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        return "web/dashboard/mercadeo";
    }

    /** CLIENTE: catálogo + carrito + mis pedidos */
    @GetMapping("/dashboard/cliente")
    public String dashboardCliente(Model model, HttpSession session) {
        if (!tieneRol(session, "CLIENTE")) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("usuarioWebId", session.getAttribute("usuarioWebId"));
        try {
            ProductoFiltroDTO filtro = new ProductoFiltroDTO();
            List<ProductoDTO> productos = productoService.listarConFiltros(filtro);
            model.addAttribute("productos", productos);
        } catch (Exception e) {
            model.addAttribute("productos", List.of());
        }
        return "web/dashboard/cliente";
    }

    // ── Redirección /web/dashboard (compatibilidad) ────────────────────
    // Redirige al dashboard correcto según el rol en sesión

    @GetMapping("/dashboard")
    public String dashboardGeneral(HttpSession session) {
        String rol = (String) session.getAttribute("usuarioWebRol");
        if (rol == null) return "redirect:/web/login";
        return redirigirPorRol(rol);
    }

    // ── Productos ──────────────────────────────────────────────────────

    @GetMapping("/productos")
    public String listarProductos(@RequestParam(required = false) String categoria,
                                  @RequestParam(required = false) String busqueda,
                                  Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb",     session.getAttribute("usuarioWebRol"));
        ProductoFiltroDTO filtro = new ProductoFiltroDTO();
        filtro.setCategoria(categoria);
        filtro.setBusqueda(busqueda);
        List<ProductoDTO> productos = productoService.listarConFiltros(filtro);
        model.addAttribute("productos",  productos);
        model.addAttribute("categoria",  categoria);
        model.addAttribute("busqueda",   busqueda);
        model.addAttribute("categorias", List.of("Amigurumis", "Accesorios", "Ropa", "Hogar"));
        return "web/productos/listar";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(Model model, HttpSession session) {
        if (!tieneRolAdminOArtesana(session)) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("producto",   new ProductoDTO());
        model.addAttribute("categorias", List.of("Amigurumis", "Accesorios", "Ropa", "Hogar"));
        model.addAttribute("accion",     "Crear");
        return "web/productos/form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute ProductoDTO productoDTO,
                                  HttpSession session, RedirectAttributes ra) {
        if (!tieneRolAdminOArtesana(session)) return redirigirSegunSesion(session);
        try {
            if (productoDTO.getId() != null && !productoDTO.getId().isBlank()) {
                productoService.actualizar(productoDTO.getId(), productoDTO);
                ra.addFlashAttribute("success", "Producto actualizado correctamente.");
            } else {
                productoService.crear(productoDTO);
                ra.addFlashAttribute("success", "Producto creado correctamente.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
        }
        return "redirect:/web/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable String id, Model model, HttpSession session) {
        if (!tieneRolAdminOArtesana(session)) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        try {
            ProductoDTO producto = productoService.obtenerPorId(id);
            model.addAttribute("producto",   producto);
            model.addAttribute("categorias", List.of("Amigurumis", "Accesorios", "Ropa", "Hogar"));
            model.addAttribute("accion",     "Editar");
            return "web/productos/form";
        } catch (Exception e) {
            return "redirect:/web/productos";
        }
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable String id,
                                   HttpSession session, RedirectAttributes ra) {
        if (!tieneRol(session, "ADMIN")) return redirigirSegunSesion(session);
        try {
            productoService.eliminar(id);
            ra.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/web/productos";
    }

    // ── Colores de Hilo ────────────────────────────────────────────────

    @GetMapping("/colores")
    public String listarColores(Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb",     session.getAttribute("usuarioWebRol"));
        List<ColorHiloDTO> colores = colorHiloService.listarTodos();
        model.addAttribute("colores", colores);
        return "web/colores/listar";
    }

    @GetMapping("/colores/nuevo")
    public String nuevoColorForm(Model model, HttpSession session) {
        if (!tieneRolBodegaOAdmin(session)) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("color",  new ColorHiloDTO());
        model.addAttribute("accion", "Crear");
        return "web/colores/form";
    }

    @PostMapping("/colores/guardar")
    public String guardarColor(@ModelAttribute ColorHiloDTO colorDTO,
                               HttpSession session, RedirectAttributes ra) {
        if (!tieneRolBodegaOAdmin(session)) return redirigirSegunSesion(session);
        try {
            if (colorDTO.getId() != null && !colorDTO.getId().isBlank()) {
                colorHiloService.actualizar(colorDTO.getId(), colorDTO);
                ra.addFlashAttribute("success", "Color actualizado correctamente.");
            } else {
                colorHiloService.crear(colorDTO);
                ra.addFlashAttribute("success", "Color creado correctamente.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el color: " + e.getMessage());
        }
        return "redirect:/web/colores";
    }

    @GetMapping("/colores/editar/{id}")
    public String editarColor(@PathVariable String id, Model model, HttpSession session) {
        if (!tieneRolBodegaOAdmin(session)) return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        try {
            ColorHiloDTO color = colorHiloService.obtenerPorId(id);
            model.addAttribute("color",  color);
            model.addAttribute("accion", "Editar");
            return "web/colores/form";
        } catch (Exception e) {
            return "redirect:/web/colores";
        }
    }

    @GetMapping("/colores/eliminar/{id}")
    public String eliminarColor(@PathVariable String id,
                                HttpSession session, RedirectAttributes ra) {
        if (!tieneRolBodegaOAdmin(session)) return redirigirSegunSesion(session);
        try {
            colorHiloService.eliminar(id);
            ra.addFlashAttribute("success", "Color eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/web/colores";
    }

    // ── Pedidos ────────────────────────────────────────────────────────

    @GetMapping("/pedidos")
    public String listarPedidos(Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb",     session.getAttribute("usuarioWebRol"));
        try {
            model.addAttribute("pedidos", pedidoService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("error",   "Error al cargar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of());
        }
        return "web/pedidos/listar";
    }

    // ── Despachos ──────────────────────────────────────────────────────

    @GetMapping("/despachos")
    public String listarDespachos(Model model, HttpSession session) {
        if (!tieneRol(session, "LOGISTICA") && !tieneRol(session, "ADMIN"))
            return redirigirSegunSesion(session);
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb",     session.getAttribute("usuarioWebRol"));
        try {
            model.addAttribute("despachos", despachoService.listarDespachosPendientes());
        } catch (Exception e) {
            model.addAttribute("error",     "Error al cargar despachos: " + e.getMessage());
            model.addAttribute("despachos", List.of());
        }
        return "web/despachos/listar";
    }

    // ── Wishlist ────────────────────────────────────────────────────────

    @GetMapping("/wishlist")
    public String verWishlist(Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        String usuarioWebId = (String) session.getAttribute("usuarioWebId");
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb", session.getAttribute("usuarioWebRol"));
        try {
            model.addAttribute("items", wishlistService.listarPorUsuario(usuarioWebId));
        } catch (Exception e) {
            model.addAttribute("items", List.of());
        }
        return "web/cliente/wishlist";
    }

    @PostMapping("/wishlist/agregar/{productoId}")
    public String agregarWishlist(@PathVariable String productoId,
                                   RedirectAttributes ra, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        try {
            wishlistService.agregar((String) session.getAttribute("usuarioWebId"), productoId);
            ra.addFlashAttribute("success", "Producto agregado a tu lista de deseos");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/wishlist";
    }

    @GetMapping("/wishlist/eliminar/{productoId}")
    public String eliminarWishlist(@PathVariable String productoId,
                                    RedirectAttributes ra, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        wishlistService.eliminar((String) session.getAttribute("usuarioWebId"), productoId);
        ra.addFlashAttribute("success", "Producto eliminado de tu lista de deseos");
        return "redirect:/web/wishlist";
    }

    // ── Chat de pedidos ─────────────────────────────────────────────────

    @GetMapping("/chat/{pedidoId}")
    public String chatPedido(@PathVariable String pedidoId, Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        String usuarioWebId = (String) session.getAttribute("usuarioWebId");
        String usuarioWeb = (String) session.getAttribute("usuarioWeb");
        String rolWeb = (String) session.getAttribute("usuarioWebRol");
        model.addAttribute("usuarioWeb", usuarioWeb);
        model.addAttribute("rolWeb", rolWeb);
        model.addAttribute("usuarioWebId", usuarioWebId);
        model.addAttribute("pedidoId", pedidoId);
        try {
            model.addAttribute("mensajes", chatService.obtenerMensajes(pedidoId));
        } catch (Exception e) {
            model.addAttribute("mensajes", List.of());
        }
        return "web/cliente/chat";
    }

    // ── Carrito y checkout web ─────────────────────────────────────────

    @GetMapping("/carrito")
    public String verCarrito(Model model, HttpSession session) {
        agregarModeloCarrito(model, session);
        return "web/carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam String productoId,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   RedirectAttributes ra,
                                   HttpSession session) {
        try {
            ProductoDTO producto = productoService.obtenerPorId(productoId);
            List<CarritoItem> carrito = obtenerCarrito(session);
            CarritoItem existente = carrito.stream()
                    .filter(item -> item.getProductoId().equals(productoId))
                    .findFirst()
                    .orElse(null);

            int cantidadSegura = Math.max(1, cantidad);
            if (existente != null) {
                existente.setCantidad(existente.getCantidad() + cantidadSegura);
            } else {
                carrito.add(new CarritoItem(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getPrecioBase() != null ? producto.getPrecioBase() : 0.0,
                        cantidadSegura));
            }
            session.setAttribute("carrito", carrito);
            ra.addFlashAttribute("success", "Producto agregado al carrito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo agregar el producto: " + e.getMessage());
        }
        return "redirect:/web/carrito";
    }

    @PostMapping("/carrito/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable String id, RedirectAttributes ra, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getProductoId().equals(id));
        session.setAttribute("carrito", carrito);
        ra.addFlashAttribute("success", "Producto eliminado del carrito.");
        return "redirect:/web/carrito";
    }

    @GetMapping("/carrito/vaciar")
    public String vaciarCarrito(RedirectAttributes ra, HttpSession session) {
        session.setAttribute("carrito", new ArrayList<CarritoItem>());
        ra.addFlashAttribute("success", "Carrito vaciado correctamente.");
        return "redirect:/web/carrito";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        List<CarritoItem> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) return "redirect:/web/carrito";
        agregarModeloCarrito(model, session);
        return "web/checkout";
    }

    @PostMapping("/checkout")
    public String confirmarCheckout(@RequestParam String direccion,
                                    @RequestParam String ciudad,
                                    @RequestParam(required = false) String departamento,
                                    RedirectAttributes ra,
                                    HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        List<CarritoItem> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) {
            ra.addFlashAttribute("error", "Tu carrito está vacío.");
            return "redirect:/web/carrito";
        }
        try {
            List<ItemPedidoRequest> items = carrito.stream()
                    .map(item -> ItemPedidoRequest.builder()
                            .productoId(item.getProductoId())
                            .cantidad(item.getCantidad())
                            .colorSolicitado("Sin especificar")
                            .build())
                    .toList();
            String direccionCompleta = direccion + ", " + ciudad
                    + (departamento != null && !departamento.isBlank() ? ", " + departamento : "");
            CrearPedidoDTO dto = CrearPedidoDTO.builder()
                    .items(items)
                    .direccionEnvio(direccionCompleta)
                    .build();
            pedidoService.crearPedido((String) session.getAttribute("usuarioWebId"), dto);
            session.setAttribute("carrito", new ArrayList<CarritoItem>());
            ra.addFlashAttribute("success", "Pedido creado correctamente.");
            return "redirect:/web/mis-pedidos";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo confirmar el pedido: " + e.getMessage());
            return "redirect:/web/checkout";
        }
    }

    @GetMapping("/mis-pedidos")
    public String misPedidos(Model model, HttpSession session) {
        if (!estaAutenticado(session)) return "redirect:/web/login";
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb", session.getAttribute("usuarioWebRol"));
        try {
            model.addAttribute("pedidos",
                    pedidoService.listarMisPedidos((String) session.getAttribute("usuarioWebId")));
        } catch (Exception e) {
            model.addAttribute("pedidos", List.of());
            model.addAttribute("error", "No se pudieron cargar tus pedidos.");
        }
        return "web/pedidos/listar";
    }

    // ── Helpers de seguridad ───────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<CarritoItem> obtenerCarrito(HttpSession session) {
        Object carrito = session.getAttribute("carrito");
        if (carrito instanceof List<?>) {
            return (List<CarritoItem>) carrito;
        }
        List<CarritoItem> nuevoCarrito = new ArrayList<>();
        session.setAttribute("carrito", nuevoCarrito);
        return nuevoCarrito;
    }

    private void agregarModeloCarrito(Model model, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        double total = carrito.stream().mapToDouble(CarritoItem::getSubtotal).sum();
        model.addAttribute("usuarioWeb", session.getAttribute("usuarioWeb"));
        model.addAttribute("rolWeb", session.getAttribute("usuarioWebRol"));
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
    }

    /** ¿Hay alguien logueado en la sesión web? */
    private boolean estaAutenticado(HttpSession session) {
        return session.getAttribute("usuarioWebRol") != null;
    }

    /** ¿El usuario en sesión tiene exactamente este rol? */
    private boolean tieneRol(HttpSession session, String rol) {
        return rol.equals(session.getAttribute("usuarioWebRol"));
    }

    /** ADMIN o ARTESANA pueden gestionar productos */
    private boolean tieneRolAdminOArtesana(HttpSession session) {
        String rol = (String) session.getAttribute("usuarioWebRol");
        return "ADMIN".equals(rol) || "ARTESANA".equals(rol);
    }

    /** ADMIN o BODEGA pueden gestionar colores/hilos */
    private boolean tieneRolBodegaOAdmin(HttpSession session) {
        String rol = (String) session.getAttribute("usuarioWebRol");
        return "ADMIN".equals(rol) || "BODEGA".equals(rol);
    }

    /**
     * Si el usuario está autenticado, lo redirige a su dashboard correcto.
     * Si no está autenticado, lo manda al login.
     */
    private String redirigirSegunSesion(HttpSession session) {
        String rol = (String) session.getAttribute("usuarioWebRol");
        if (rol == null) return "redirect:/web/login";
        return redirigirPorRol(rol);
    }
}
