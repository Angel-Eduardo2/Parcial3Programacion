package paquelibre.modelo;

public class ResumenEstado {
    private final String estado;
    private final int cantidadEnvios;
    private final double totalFacturado;

    public ResumenEstado(String estado, int cantidadEnvios, double totalFacturado) {
        this.estado = estado;
        this.cantidadEnvios = cantidadEnvios;
        this.totalFacturado = totalFacturado;
    }

    public String getEstado() {
        return estado;
    }

    public int getCantidadEnvios() {
        return cantidadEnvios;
    }

    public double getTotalFacturado() {
        return totalFacturado;
    }
}
