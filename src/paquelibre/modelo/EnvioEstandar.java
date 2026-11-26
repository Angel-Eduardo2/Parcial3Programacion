package paquelibre.modelo;

import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.patron.EstadoEnvio;

public class EnvioEstandar extends Envio {

    public EnvioEstandar(String guia, String remitente, String destinatario, String destino,
                         double pesoReal, double largo, double ancho, double alto,
                         double valorDeclarado, EstadoEnvio estado)
            throws GuiaInvalidaException, PesoInvalidoException,
                   DimensionInvalidaException, ValorDeclaradoInvalidoException {
        super(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);
    }

    @Override
    public double calcularFlete() {
        return 8000.0 + (calcularPesoFacturable() * 2000.0);
    }

    @Override
    public double calcularSeguro() {
        return 0.0;
    }

    @Override
    public String getTipo() {
        return "Estandar";
    }
}
