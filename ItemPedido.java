import java.util.Objects;

public record ItemPedido(String productoId, String nombreProducto, int cantidad, double precioUnitario) {
    public ItemPedido {
        Objects.requireNonNull(productoId, "ID de producto no puede ser nulo");
        Objects.requireNonNull(nombreProducto, "Nombre de producto no puede ser nulo");
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        if (precioUnitario <= 0) throw new IllegalArgumentException("Precio debe ser positivo");
    }
}
