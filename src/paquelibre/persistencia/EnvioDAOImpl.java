package paquelibre.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.modelo.Envio;
import paquelibre.modelo.EnvioEstandar;
import paquelibre.modelo.EnvioExpress;
import paquelibre.modelo.EnvioInternacional;
import paquelibre.modelo.RegistroHistorial;
import paquelibre.modelo.ResumenEstado;
import paquelibre.patron.EstadoEnvio;
import paquelibre.patron.EstadoFactory;

public class EnvioDAOImpl implements EnvioDAO {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EnvioDAOImpl() {
        ConexionBD.inicializarBaseDatos();
    }

    private String obtenerFechaHoraActual() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }

    @Override
    public void guardar(Envio envio) throws SQLException {
        String sqlEnvio = "INSERT INTO envios (guia, tipo, remitente, destinatario, destino, " +
                "peso_real, largo, ancho, alto, valor_declarado, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlHistorial = "INSERT INTO historial_estados (guia, estado_anterior, estado_nuevo, fecha_hora) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psEnvio = conn.prepareStatement(sqlEnvio)) {
                    psEnvio.setString(1, envio.getGuia());
                    psEnvio.setString(2, envio.getTipo());
                    psEnvio.setString(3, envio.getRemitente());
                    psEnvio.setString(4, envio.getDestinatario());
                    psEnvio.setString(5, envio.getDestino());
                    psEnvio.setDouble(6, envio.getPesoReal());
                    psEnvio.setDouble(7, envio.getLargo());
                    psEnvio.setDouble(8, envio.getAncho());
                    psEnvio.setDouble(9, envio.getAlto());
                    psEnvio.setDouble(10, envio.getValorDeclarado());
                    psEnvio.setString(11, envio.getEstado().getNombre());
                    psEnvio.executeUpdate();
                }

                try (PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial)) {
                    psHistorial.setString(1, envio.getGuia());
                    psHistorial.setString(2, null);
                    psHistorial.setString(3, envio.getEstado().getNombre());
                    psHistorial.setString(4, obtenerFechaHoraActual());
                    psHistorial.executeUpdate();
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void actualizar(Envio envio) throws SQLException {
        String sql = "UPDATE envios SET remitente = ?, destinatario = ?, destino = ?, " +
                "peso_real = ?, largo = ?, ancho = ?, alto = ?, valor_declarado = ?, estado = ? " +
                "WHERE guia = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, envio.getRemitente());
            ps.setString(2, envio.getDestinatario());
            ps.setString(3, envio.getDestino());
            ps.setDouble(4, envio.getPesoReal());
            ps.setDouble(5, envio.getLargo());
            ps.setDouble(6, envio.getAncho());
            ps.setDouble(7, envio.getAlto());
            ps.setDouble(8, envio.getValorDeclarado());
            ps.setString(9, envio.getEstado().getNombre());
            ps.setString(10, envio.getGuia());
            ps.executeUpdate();
        }
    }

    @Override
    public void registrarCambioEstado(String guia, String estadoAnterior, String estadoNuevo) throws SQLException {
        String sqlEnvio = "UPDATE envios SET estado = ? WHERE guia = ?";
        String sqlHistorial = "INSERT INTO historial_estados (guia, estado_anterior, estado_nuevo, fecha_hora) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psEnvio = conn.prepareStatement(sqlEnvio)) {
                    psEnvio.setString(1, estadoNuevo);
                    psEnvio.setString(2, guia);
                    psEnvio.executeUpdate();
                }

                try (PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial)) {
                    psHistorial.setString(1, guia);
                    psHistorial.setString(2, estadoAnterior);
                    psHistorial.setString(3, estadoNuevo);
                    psHistorial.setString(4, obtenerFechaHoraActual());
                    psHistorial.executeUpdate();
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void eliminar(String guia) throws SQLException {
        String sqlHistorial = "DELETE FROM historial_estados WHERE guia = ?";
        String sqlEnvios = "DELETE FROM envios WHERE guia = ?";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psH = conn.prepareStatement(sqlHistorial)) {
                    psH.setString(1, guia);
                    psH.executeUpdate();
                }
                try (PreparedStatement psE = conn.prepareStatement(sqlEnvios)) {
                    psE.setString(1, guia);
                    psE.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public Envio buscarPorGuia(String guia) throws SQLException {
        String sql = "SELECT guia, tipo, remitente, destinatario, destino, " +
                "peso_real, largo, ancho, alto, valor_declarado, estado " +
                "FROM envios WHERE guia = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guia != null ? guia.trim().toUpperCase() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirEnvioDesdeResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Envio> listarTodos() throws SQLException {
        List<Envio> lista = new ArrayList<>();
        String sql = "SELECT guia, tipo, remitente, destinatario, destino, " +
                "peso_real, largo, ancho, alto, valor_declarado, estado " +
                "FROM envios ORDER BY guia ASC";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Envio e = construirEnvioDesdeResultSet(rs);
                if (e != null) {
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    private Envio construirEnvioDesdeResultSet(ResultSet rs) {
        try {
            String guia = rs.getString("guia");
            String tipo = rs.getString("tipo");
            String remitente = rs.getString("remitente");
            String destinatario = rs.getString("destinatario");
            String destino = rs.getString("destino");
            double pesoReal = rs.getDouble("peso_real");
            double largo = rs.getDouble("largo");
            double ancho = rs.getDouble("ancho");
            double alto = rs.getDouble("alto");
            double valorDeclarado = rs.getDouble("valor_declarado");
            String estadoStr = rs.getString("estado");

            EstadoEnvio estado = EstadoFactory.desdeNombre(estadoStr);

            if (tipo != null) {
                String tipoNorm = tipo.trim().toLowerCase();
                if (tipoNorm.contains("estandar") || tipoNorm.contains("estándar")) {
                    return new EnvioEstandar(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);
                } else if (tipoNorm.contains("express")) {
                    return new EnvioExpress(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);
                } else if (tipoNorm.contains("internacional")) {
                    return new EnvioInternacional(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);
                }
            }
            return new EnvioEstandar(guia, remitente, destinatario, destino, pesoReal, largo, ancho, alto, valorDeclarado, estado);

        } catch (GuiaInvalidaException | PesoInvalidoException |
                 DimensionInvalidaException | ValorDeclaradoInvalidoException e) {
            System.err.println("Error reconstruyendo objeto desde BD: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Error SQL leyendo registro: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ResumenEstado> consultarResumenPorEstado() throws SQLException {
        List<Envio> envios = listarTodos();
        Map<String, Integer> conteo = new LinkedHashMap<>();
        Map<String, Double> totales = new LinkedHashMap<>();

        conteo.put("Registrado", 0);
        totales.put("Registrado", 0.0);
        conteo.put("EnRuta", 0);
        totales.put("EnRuta", 0.0);
        conteo.put("Entregado", 0);
        totales.put("Entregado", 0.0);
        conteo.put("Devuelto", 0);
        totales.put("Devuelto", 0.0);

        for (Envio e : envios) {
            String est = e.getEstado().getNombre();
            conteo.put(est, conteo.getOrDefault(est, 0) + 1);
            totales.put(est, totales.getOrDefault(est, 0.0) + e.calcularTotal());
        }

        List<ResumenEstado> resultado = new ArrayList<>();
        for (String est : conteo.keySet()) {
            resultado.add(new ResumenEstado(est, conteo.get(est), totales.get(est)));
        }
        return resultado;
    }

    @Override
    public List<RegistroHistorial> consultarHistorialGuia(String guia) throws SQLException {
        List<RegistroHistorial> historial = new ArrayList<>();
        String sql = "SELECT id, guia, estado_anterior, estado_nuevo, fecha_hora " +
                "FROM historial_estados WHERE guia = ? ORDER BY id ASC";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guia != null ? guia.trim().toUpperCase() : "");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String g = rs.getString("guia");
                    String ant = rs.getString("estado_anterior");
                    String nuevo = rs.getString("estado_nuevo");
                    String fecha = rs.getString("fecha_hora");
                    historial.add(new RegistroHistorial(id, g, ant, nuevo, fecha));
                }
            }
        }
        return historial;
    }
}
