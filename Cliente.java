import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Cliente(String id, String nombre, String email) {
    public Cliente {
        Objects.requireNonNull(id, "ID no puede ser nulo");
        Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        Objects.requireNonNull(email, "Email no puede ser nulo");
    }
}

