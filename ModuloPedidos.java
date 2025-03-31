import java.util.List;
import java.util.Objects;

public class ModuloPedidos implements GestionPedidos {
    private final GestionInventario inventario;
    private final ProcesadorPagos pagos;
    private NotificationService notificador;

    public ModuloPedidos(GestionInventario inventario, ProcesadorPagos pagos) {
        this.inventario = Objects.requireNonNull(inventario);
        this.pagos = Objects.requireNonNull(pagos);
    }

    @Override
    public synchronized Pedido crearPedido(Cliente cliente, List<ItemPedido> items) {
        Objects.requireNonNull(cliente, "Cliente no puede ser nulo");
        Objects.requireNonNull(items, "Items no puede ser nulo");
        if (items.isEmpty()) throw new IllegalArgumentException("El pedido debe contener items");

        Pedido pedido = new Pedido(cliente, items);

        if (!inventario.reservarStock(items)) {
            pedido.setEstado("CANCELADO_STOCK");
            return pedido;
        }

        try {
            if (pagos.procesarPago(pedido)) {
                pedido.setEstado("COMPLETADO");
                if (notificador != null) {
                    notificador.enviarConfirmacion(
                            cliente.email(),
                            "Su pedido #" + pedido.getId() + " ha sido confirmado con éxito");
                }
            } else {
                inventario.revertirReserva(items);
                pedido.setEstado("CANCELADO_PAGO");
            }
        } catch (Exception e) {
            inventario.revertirReserva(items);
            pedido.setEstado("CANCELADO_ERROR");
            throw e;
        }

        return pedido;
    }

    @Override
    public Pedido consultarPedido(String idPedido) {
        return null; // Implementación simulada
    }

    public void setNotificador(NotificationService notificador) {
        this.notificador = notificador;
    }
}