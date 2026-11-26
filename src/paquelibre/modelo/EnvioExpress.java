package paquelibre.modelo;

import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.patron.EstadoEnvio;

public class EnvioExpress extends Envio implements Asegurable {

    public EnvioExpress(String guia, String remitente, String destinatario, String destino,
                        double pesoReal, double largo, double ancho, double alto,
                        double valorDeclarado, EstadoEnvio estado)
            throws GuiaInvalidaException, PesoInvalidoException,
                   DimensionInvalidaException, ValorDeclaradoInvalidoException {
        super(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);
    }

    @Override
    public double calcularFlete() {
        return 15000.0 + (calcularPesoFacturable() * 3500.0);
    }

    @Override
    public double calcularSeguro() {
        return Math.max(5000.0, getValorDeclarado() * 0.01);
    }

    @Override
    public String getTipo() {
        return "Express";
    }
}
