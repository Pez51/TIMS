import java.util.List;
import java.util.Objects;

public class SistemaGestionPedidos {
    private final ModuloPedidos moduloPedidos;
    private final ModuloInventario moduloInventario;
    private final ModuloPagos moduloPagos;

    public SistemaGestionPedidos() {
        this.moduloInventario = new ModuloInventario();
        this.moduloPagos = new ModuloPagos();
        this.moduloPedidos = new ModuloPedidos(moduloInventario, moduloPagos);
    }

    public Pedido crearPedido(Cliente cliente, List<ItemPedido> items) {
        return moduloPedidos.crearPedido(cliente, items);
    }

    public void setNotificador(NotificationService notificador) {
        moduloPedidos.setNotificador(notificador);
    }
}