package paquelibre.modelo;

import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.patron.EstadoEnvio;
import paquelibre.patron.EstadoRegistrado;

public abstract class Envio {

    private String guia;
    private String remitente;
    private String destinatario;
    private String destino;
    private double pesoReal;
    private double largo;
    private double ancho;
    private double alto;
    private double valorDeclarado;
    private EstadoEnvio estado;

    public Envio(String guia, String remitente, String destinatario, String destino,
                 double pesoReal, double largo, double ancho, double alto,
                 double valorDeclarado, EstadoEnvio estado)
            throws GuiaInvalidaException, PesoInvalidoException,
                   DimensionInvalidaException, ValorDeclaradoInvalidoException {
        validarGuia(guia);
        validarDimensiones(largo, ancho, alto);
        validarValorDeclarado(valorDeclarado);
        validarPeso(pesoReal);

        this.guia = guia.trim().toUpperCase();
        this.remitente = remitente != null ? remitente.trim() : "";
        this.destinatario = destinatario != null ? destinatario.trim() : "";
        this.destino = destino != null ? destino.trim() : "";
        this.pesoReal = pesoReal;
        this.largo = largo;
        this.ancho = ancho;
        this.alto = alto;
        this.valorDeclarado = valorDeclarado;
        this.estado = (estado != null) ? estado : new EstadoRegistrado();
    }

    public static void validarGuia(String guia) throws GuiaInvalidaException {
        if (guia == null || !guia.trim().toUpperCase().matches("^PL-\\d{6}$")) {
            throw new GuiaInvalidaException("La guia " + guia + " es invalida. Debe tener formato PL- seguido de 6 digitos.");
        }
    }

    public static void validarDimensiones(double largo, double ancho, double alto) throws DimensionInvalidaException {
        if (largo <= 0 || ancho <= 0 || alto <= 0) {
            throw new DimensionInvalidaException("Las dimensiones deben ser mayores que 0 cm.");
        }
    }

    public static void validarValorDeclarado(double valorDeclarado) throws ValorDeclaradoInvalidoException {
        if (valorDeclarado < 0) {
            throw new ValorDeclaradoInvalidoException("El valor declarado debe ser mayor o igual a 0.");
        }
    }

    public void validarPeso(double peso) throws PesoInvalidoException {
        if (peso <= 0) {
            throw new PesoInvalidoException("El peso real debe ser mayor que 0 kg.");
        }
        if (peso > getPesoMaximoPermitido()) {
            throw new PesoInvalidoException("El peso (" + peso + " kg) supera el limite maximo permitido para servicio " 
                    + getTipo() + " (" + getPesoMaximoPermitido() + " kg).");
        }
    }

    public double getPesoMaximoPermitido() {
        return 50.0;
    }

    public double calcularPesoVolumetrico() {
        return (largo * ancho * alto) / 5000.0;
    }

    public double calcularPesoFacturable() {
        return Math.ceil(Math.max(pesoReal, calcularPesoVolumetrico()));
    }

    public abstract double calcularFlete();

    public abstract double calcularSeguro();

    public double calcularTotal() {
        return calcularFlete() + calcularSeguro();
    }

    public abstract String getTipo();

    public void pasarAEnRuta() throws TransicionInvalidaException {
        estado.pasarAEnRuta(this);
    }

    public void pasarAEntregado() throws TransicionInvalidaException {
        estado.pasarAEntregado(this);
    }

    public void pasarADevuelto() throws TransicionInvalidaException {
        estado.pasarADevuelto(this);
    }

    public void setEstadoInterno(EstadoEnvio nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void setPesoReal(double nuevoPeso) throws TransicionInvalidaException, PesoInvalidoException {
        estado.validarPermisoModificacion();
        validarPeso(nuevoPeso);
        this.pesoReal = nuevoPeso;
    }

    public void setDimensiones(double nuevoLargo, double nuevoAncho, double nuevoAlto) 
            throws TransicionInvalidaException, DimensionInvalidaException {
        estado.validarPermisoModificacion();
        validarDimensiones(nuevoLargo, nuevoAncho, nuevoAlto);
        this.largo = nuevoLargo;
        this.ancho = nuevoAncho;
        this.alto = nuevoAlto;
    }

    public void setRemitente(String remitente) throws TransicionInvalidaException {
        estado.validarPermisoModificacion();
        this.remitente = remitente != null ? remitente.trim() : "";
    }

    public void setDestinatario(String destinatario) throws TransicionInvalidaException {
        estado.validarPermisoModificacion();
        this.destinatario = destinatario != null ? destinatario.trim() : "";
    }

    public void setDestino(String destino) throws TransicionInvalidaException {
        estado.validarPermisoModificacion();
        this.destino = destino != null ? destino.trim() : "";
    }

    public void setValorDeclarado(double valorDeclarado) throws TransicionInvalidaException, ValorDeclaradoInvalidoException {
        estado.validarPermisoModificacion();
        validarValorDeclarado(valorDeclarado);
        this.valorDeclarado = valorDeclarado;
    }

    public String getGuia() {
        return guia;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getDestino() {
        return destino;
    }

    public double getPesoReal() {
        return pesoReal;
    }

    public double getLargo() {
        return largo;
    }

    public double getAncho() {
        return ancho;
    }

    public double getAlto() {
        return alto;
    }

    public double getValorDeclarado() {
        return valorDeclarado;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }
}
