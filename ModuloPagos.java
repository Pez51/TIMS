import java.util.function.Supplier;

public class ModuloPagos implements ProcesadorPagos {
    private final Supplier<Boolean> resultadoPagoAleatorio;

    // Constructor para producción (aleatorio por defecto)
    public ModuloPagos() {
        this.resultadoPagoAleatorio = () -> Math.random() < 0.9; // 90% éxito
    }

    // Constructor para pruebas (se puede inyectar un comportamiento controlado)
    public ModuloPagos(Supplier<Boolean> resultadoPagoCustom) {
        this.resultadoPagoAleatorio = resultadoPagoCustom;
    }

    @Override
    public boolean procesarPago(Pedido pedido) {
        return resultadoPagoAleatorio.get();
    }
}
