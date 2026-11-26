package paquelibre.persistencia;

import java.sql.SQLException;
import java.util.List;
import paquelibre.modelo.Envio;
import paquelibre.modelo.RegistroHistorial;
import paquelibre.modelo.ResumenEstado;

public interface EnvioDAO {

    void guardar(Envio envio) throws SQLException;

    void actualizar(Envio envio) throws SQLException;

    void registrarCambioEstado(String guia, String estadoAnterior, String estadoNuevo) throws SQLException;

    void eliminar(String guia) throws SQLException;

    Envio buscarPorGuia(String guia) throws SQLException;

    List<Envio> listarTodos() throws SQLException;

    List<ResumenEstado> consultarResumenPorEstado() throws SQLException;

    List<RegistroHistorial> consultarHistorialGuia(String guia) throws SQLException;
}
