import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuloInventario implements GestionInventario {
    private final Map<String, AtomicInteger> stock = new ConcurrentHashMap<>();

    public ModuloInventario() {
        stock.put("PROD-2025-001", new AtomicInteger(15));
        stock.put("PROD-2025-002", new AtomicInteger(8));
        stock.put("PROD-2025-003", new AtomicInteger(25));
    }

    @Override
    public synchronized boolean reservarStock(List<ItemPedido> items) {
        for (ItemPedido item : items) {
            if (stock.getOrDefault(item.productoId(), new AtomicInteger(0)).get() < item.cantidad()) {
                return false;
            }
        }

        items.forEach(item ->
                stock.get(item.productoId()).addAndGet(-item.cantidad())
        );
        return true;
    }

    @Override
    public synchronized void revertirReserva(List<ItemPedido> items) {
        items.forEach(item ->
                stock.get(item.productoId()).addAndGet(item.cantidad())
        );
    }

    @Override
    public void actualizarInventario(List<ItemPedido> items) {
        items.forEach(item ->
                stock.compute(item.productoId(),
                        (k, v) -> new AtomicInteger(item.cantidad()))
        );
    }

    @Override
    public int consultarStock(String productoId) {
        return stock.getOrDefault(productoId, new AtomicInteger(0)).get();
    }
}