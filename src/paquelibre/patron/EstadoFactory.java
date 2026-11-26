package paquelibre.patron;

public class EstadoFactory {

    public static EstadoEnvio desdeNombre(String nombre) {
        if (nombre == null) {
            return new EstadoRegistrado();
        }
        String normalizado = nombre.trim().replace(" ", "").toLowerCase();
        switch (normalizado) {
            case "enruta":
                return new EstadoEnRuta();
            case "entregado":
                return new EstadoEntregado();
            case "devuelto":
                return new EstadoDevuelto();
            case "registrado":
            default:
                return new EstadoRegistrado();
        }
    }
}
