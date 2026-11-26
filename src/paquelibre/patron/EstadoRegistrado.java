package paquelibre.patron;

import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.modelo.Envio;

public class EstadoRegistrado implements EstadoEnvio {

    @Override
    public void pasarAEnRuta(Envio envio) {
        envio.setEstadoInterno(new EstadoEnRuta());
    }

    @Override
    public void pasarAEntregado(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: de Registrado no se puede pasar a Entregado.");
    }

    @Override
    public void pasarADevuelto(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: de Registrado no se puede pasar a Devuelto.");
    }

    @Override
    public void validarPermisoModificacion() {
    }

    @Override
    public String getNombre() {
        return "Registrado";
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
