package paquelibre.patron;

import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.modelo.Envio;

public class EstadoDevuelto implements EstadoEnvio {

    @Override
    public void pasarAEnRuta(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete esta Devuelto.");
    }

    @Override
    public void pasarAEntregado(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete esta Devuelto y no puede entregarse.");
    }

    @Override
    public void pasarADevuelto(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete ya se encuentra Devuelto.");
    }

    @Override
    public void validarPermisoModificacion() throws TransicionInvalidaException {
        throw new TransicionInvalidaException("No se pueden corregir datos: el paquete fue Devuelto.");
    }

    @Override
    public String getNombre() {
        return "Devuelto";
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
