import java.util.List;

public interface GestionInventario {
    boolean reservarStock(List<ItemPedido> items);
    void revertirReserva(List<ItemPedido> items);
    void actualizarInventario(List<ItemPedido> items);

    int consultarStock(String productoId);
}
