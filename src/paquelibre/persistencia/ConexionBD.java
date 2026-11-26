package paquelibre.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static String url = "jdbc:sqlite:paquelibre.db";

    public static void setUrl(String nuevaUrl) {
        url = nuevaUrl;
    }

    public static String getUrl() {
        return url;
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite no encontrado: " + e.getMessage());
        }
        return DriverManager.getConnection(url);
    }

    public static void inicializarBaseDatos() {
        String sqlEnvios = "CREATE TABLE IF NOT EXISTS envios (" +
                "guia TEXT PRIMARY KEY, " +
                "tipo TEXT NOT NULL, " +
                "remitente TEXT NOT NULL, " +
                "destinatario TEXT NOT NULL, " +
                "destino TEXT NOT NULL, " +
                "peso_real REAL NOT NULL, " +
                "largo REAL NOT NULL, " +
                "ancho REAL NOT NULL, " +
                "alto REAL NOT NULL, " +
                "valor_declarado REAL NOT NULL, " +
                "estado TEXT NOT NULL" +
                ");";

        String sqlHistorial = "CREATE TABLE IF NOT EXISTS historial_estados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "guia TEXT NOT NULL, " +
                "estado_anterior TEXT, " +
                "estado_nuevo TEXT NOT NULL, " +
                "fecha_hora TEXT NOT NULL, " +
                "FOREIGN KEY (guia) REFERENCES envios(guia) ON DELETE CASCADE" +
                ");";

        try (Connection conn = obtenerConexion();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlEnvios);
            stmt.execute(sqlHistorial);
        } catch (SQLException e) {
            System.err.println("Error inicializando base de datos: " + e.getMessage());
        }
    }
}
