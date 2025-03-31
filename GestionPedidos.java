import java.util.List;

public interface GestionPedidos {
    Pedido crearPedido(Cliente cliente, List<ItemPedido> items);
    Pedido consultarPedido(String idPedido);
}

