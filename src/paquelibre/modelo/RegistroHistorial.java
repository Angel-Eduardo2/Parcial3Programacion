package paquelibre.modelo;

public class RegistroHistorial {
    private final int id;
    private final String guia;
    private final String estadoAnterior;
    private final String estadoNuevo;
    private final String fechaHora;

    public RegistroHistorial(int id, String guia, String estadoAnterior, String estadoNuevo, String fechaHora) {
        this.id = id;
        this.guia = guia;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHora = fechaHora;
    }

    public int getId() {
        return id;
    }

    public String getGuia() {
        return guia;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    @Override
    public String toString() {
        String anterior = (estadoAnterior == null || estadoAnterior.isEmpty()) ? "(Inicio)" : estadoAnterior;
        return "[" + fechaHora + "] " + anterior + " -> " + estadoNuevo;
    }
}
