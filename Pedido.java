import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pedido {
    private final String id;
    private final Cliente cliente;
    private final List<ItemPedido> items;
    private String estado;

    public Pedido(Cliente cliente, List<ItemPedido> items) {
        this.id = UUID.randomUUID().toString();
        this.cliente = Objects.requireNonNull(cliente);
        this.items = List.copyOf(Objects.requireNonNull(items));
        this.estado = "EN_PROCESO";
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItems() { return items; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = Objects.requireNonNull(estado); }

    public double calcularTotal() {
        return items.stream()
                .mapToDouble(item -> item.cantidad() * item.precioUnitario())
                .sum();
    }
}
