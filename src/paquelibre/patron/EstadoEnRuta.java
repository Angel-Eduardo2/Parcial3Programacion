package paquelibre.patron;

import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.modelo.Envio;

public class EstadoEnRuta implements EstadoEnvio {

    @Override
    public void pasarAEnRuta(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete ya se encuentra EnRuta.");
    }

    @Override
    public void pasarAEntregado(Envio envio) {
        envio.setEstadoInterno(new EstadoEntregado());
    }

    @Override
    public void pasarADevuelto(Envio envio) {
        envio.setEstadoInterno(new EstadoDevuelto());
    }

    @Override
    public void validarPermisoModificacion() throws TransicionInvalidaException {
        throw new TransicionInvalidaException("No se pueden corregir datos: el paquete ya se encuentra EnRuta.");
    }

    @Override
    public String getNombre() {
        return "EnRuta";
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
