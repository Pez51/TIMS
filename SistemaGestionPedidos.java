import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class SistemaGestionPedidos {
    private static final Logger LOGGER = Logger.getLogger(SistemaGestionPedidos.class.getName());

    private final ModuloPedidos moduloPedidos;
    private final ModuloInventario moduloInventario;
    private final ModuloPagos moduloPagos;

    public SistemaGestionPedidos() {
        this.moduloInventario = new ModuloInventario();
        this.moduloPagos = new ModuloPagos();
        this.moduloPedidos = new ModuloPedidos(moduloInventario, moduloPagos);
    }

    public static void main(String[] args) {
        new SistemaGestionPedidos().iniciar();
    }

    private void iniciar() {
        LOGGER.info("Sistema de Gestión de Pedidos iniciado - Versión 2025");
    }
}

interface GestionPedidos {
    Pedido crearPedido(Cliente cliente, List<ItemPedido> items);
    Pedido consultarPedido(String idPedido);
}

interface GestionInventario {
    boolean verificarDisponibilidad(List<ItemPedido> items);
    void actualizarInventario(List<ItemPedido> items);
    int consultarStock(String productoId);
}

interface ProcesadorPagos {
    boolean procesarPago(Pedido pedido);
}

class ModuloPedidos implements GestionPedidos {
    private static final String PEDIDO_COMPLETADO = "COMPLETADO";
    private static final String PEDIDO_CANCELADO_PAGO = "CANCELADO_PAGO";
    private static final String PEDIDO_CANCELADO_STOCK = "CANCELADO_STOCK";

    private final GestionInventario inventario;
    private final ProcesadorPagos pagos;

    public ModuloPedidos(GestionInventario inventario, ProcesadorPagos pagos) {
        this.inventario = Objects.requireNonNull(inventario);
        this.pagos = Objects.requireNonNull(pagos);
    }

    @Override
    public Pedido crearPedido(Cliente cliente, List<ItemPedido> items) {
        final Pedido pedido = new Pedido(cliente, items);

        if (!inventario.verificarDisponibilidad(items)) {
            pedido.actualizarEstado(PEDIDO_CANCELADO_STOCK);
            return pedido;
        }

        if (pagos.procesarPago(pedido)) {
            inventario.actualizarInventario(items);
            pedido.actualizarEstado(PEDIDO_COMPLETADO);
        } else {
            pedido.actualizarEstado(PEDIDO_CANCELADO_PAGO);
        }

        return pedido;
    }

    @Override
    public Pedido consultarPedido(String idPedido) {
        return null;
    }
}

class ModuloInventario implements GestionInventario {
    private final Map<String, Integer> stockProductos = new HashMap<>();
    public ModuloInventario() {
        inicializarInventario();
    }

    private void inicializarInventario() {
        stockProductos.put("PROD-2025-001", 15);
        stockProductos.put("PROD-2025-002", 8);
        stockProductos.put("PROD-2025-003", 25);
    }

    @Override
    public boolean verificarDisponibilidad(List<ItemPedido> items) {
        return items.stream().allMatch(item -> {
            int stockActual = stockProductos.getOrDefault(item.productoId(), 0);
            return stockActual >= item.cantidad();
        });
    }

    @Override
    public void actualizarInventario(List<ItemPedido> items) {
        items.forEach(item ->
                stockProductos.computeIfPresent(item.productoId(),
                        (k, v) -> v - item.cantidad())
        );
    }

    @Override
    public int consultarStock(String productoId) {
        return stockProductos.getOrDefault(productoId, 0);
    }
}

class ModuloPagos implements ProcesadorPagos {
    private static final double TASA_EXITO = 0.85; // 85% éxito
    @Override
    public boolean procesarPago(Pedido pedido) {
        validarPedido(pedido);
        return simularProcesoPago(pedido.calcularTotal());
    }
    private void validarPedido(Pedido pedido) {
        Objects.requireNonNull(pedido, "Pedido no puede ser nulo");
        if (pedido.calcularTotal() <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
    }

    private boolean simularProcesoPago(double monto) {
        if (monto > 1000) return Math.random() < TASA_EXITO - 0.15;
        if (monto > 500) return Math.random() < TASA_EXITO - 0.05;
        return Math.random() < TASA_EXITO;
    }
}

record Cliente(String id, String nombre, String email) implements Serializable {}

record ItemPedido(String productoId, String nombreProducto, int cantidad,
                  double precioUnitario) implements Serializable {
    public ItemPedido {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad inválida");
        if (precioUnitario <= 0) throw new IllegalArgumentException("Precio inválido");
    }

    public double subtotal() {
        return cantidad * precioUnitario;
    }
}

class Pedido implements Serializable {
    private final String id;
    private final Cliente cliente;
    private final List<ItemPedido> items;
    private String estado;
    private final LocalDateTime fechaCreacion;

    public Pedido(Cliente cliente, List<ItemPedido> items) {
        this.id = UUID.randomUUID().toString();
        this.cliente = Objects.requireNonNull(cliente);
        this.items = new ArrayList<>(items);
        this.estado = "EN_PROCESO";
        this.fechaCreacion = LocalDateTime.now();
    }
    public double calcularTotal() {
        return items.stream()
                .mapToDouble(ItemPedido::subtotal)
                .sum();
    }
    public void actualizarEstado(String nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado);
    }
    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItems() { return List.copyOf(items); }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}

class InventarioException extends RuntimeException {
    public InventarioException(String message) {
        super(message);
    }
}
class PagoException extends RuntimeException {
    public PagoException(String message) {
        super(message);
    }
}
