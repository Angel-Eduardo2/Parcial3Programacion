package paquelibre.patron;

import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.modelo.Envio;

public class EstadoEntregado implements EstadoEnvio {

    @Override
    public void pasarAEnRuta(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete ya fue Entregado.");
    }

    @Override
    public void pasarAEntregado(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete ya se encuentra Entregado.");
    }

    @Override
    public void pasarADevuelto(Envio envio) throws TransicionInvalidaException {
        throw new TransicionInvalidaException("Transicion invalida: el paquete ya fue Entregado y no puede devolverse.");
    }

    @Override
    public void validarPermisoModificacion() throws TransicionInvalidaException {
        throw new TransicionInvalidaException("No se pueden corregir datos: el paquete ya fue Entregado.");
    }

    @Override
    public String getNombre() {
        return "Entregado";
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
