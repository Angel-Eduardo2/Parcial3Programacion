package paquelibre.modelo;

import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.patron.EstadoEnvio;

public class EnvioInternacional extends Envio implements Asegurable {

    public EnvioInternacional(String guia, String remitente, String destinatario, String destino,
                              double pesoReal, double largo, double ancho, double alto,
                              double valorDeclarado, EstadoEnvio estado)
            throws GuiaInvalidaException, PesoInvalidoException,
                   DimensionInvalidaException, ValorDeclaradoInvalidoException {
        super(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);
    }

    @Override
    public double getPesoMaximoPermitido() {
        return 30.0;
    }

    public double calcularArancel() {
        return getValorDeclarado() * 0.10;
    }

    @Override
    public double calcularFlete() {
        return 60000.0 + (calcularPesoFacturable() * 12000.0) + calcularArancel();
    }

    @Override
    public double calcularSeguro() {
        return Math.max(5000.0, getValorDeclarado() * 0.01);
    }

    @Override
    public String getTipo() {
        return "Internacional";
    }
}
