package paquelibre.patron;

import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.modelo.Envio;

public interface EstadoEnvio {
    void pasarAEnRuta(Envio envio) throws TransicionInvalidaException;
    void pasarAEntregado(Envio envio) throws TransicionInvalidaException;
    void pasarADevuelto(Envio envio) throws TransicionInvalidaException;
    void validarPermisoModificacion() throws TransicionInvalidaException;
    String getNombre();
}
