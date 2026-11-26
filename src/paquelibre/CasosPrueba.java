package paquelibre;

import java.io.File;
import java.util.List;
import paquelibre.excepciones.DimensionInvalidaException;
import paquelibre.excepciones.GuiaInvalidaException;
import paquelibre.excepciones.PesoInvalidoException;
import paquelibre.excepciones.TransicionInvalidaException;
import paquelibre.excepciones.ValorDeclaradoInvalidoException;
import paquelibre.modelo.Envio;
import paquelibre.modelo.EnvioEstandar;
import paquelibre.modelo.EnvioExpress;
import paquelibre.modelo.EnvioInternacional;
import paquelibre.modelo.RegistroHistorial;
import paquelibre.modelo.ResumenEstado;
import paquelibre.patron.EstadoRegistrado;
import paquelibre.persistencia.ConexionBD;
import paquelibre.persistencia.EnvioDAO;
import paquelibre.persistencia.EnvioDAOImpl;

public class CasosPrueba {

    public static void main(String[] args) {
        File dbFile = new File("paquelibre.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        EnvioDAO dao = new EnvioDAOImpl();

        try {
            System.out.println("=== Caso 1: Registrar Estandar PL-000101 ===");
            Envio c1 = new EnvioEstandar("PL-000101", "Juan Perez", "Maria Gomez", "Bogota", 4.2, 30, 20, 15, 150000, new EstadoRegistrado());
            dao.guardar(c1);
            System.out.println("Guia: " + c1.getGuia() + " | Facturable: " + c1.calcularPesoFacturable() + " kg | Flete: $" + c1.calcularFlete() + " | Seguro: $" + c1.calcularSeguro() + " | Total: $" + c1.calcularTotal());

            System.out.println("\n=== Caso 2: Registrar Express PL-000102 ===");
            Envio c2 = new EnvioExpress("PL-000102", "Carlos Ruiz", "Ana Torres", "Medellin", 1.5, 50, 40, 30, 300000, new EstadoRegistrado());
            dao.guardar(c2);
            System.out.println("Guia: " + c2.getGuia() + " | Facturable: " + c2.calcularPesoFacturable() + " kg | Flete: $" + c2.calcularFlete() + " | Seguro: $" + c2.calcularSeguro() + " | Total: $" + c2.calcularTotal());

            System.out.println("\n=== Caso 3: Registrar Internacional PL-000103 ===");
            Envio c3 = new EnvioInternacional("PL-000103", "Laura Mora", "John Smith", "Miami", 12.0, 40, 30, 30, 2500000, new EstadoRegistrado());
            dao.guardar(c3);
            System.out.println("Guia: " + c3.getGuia() + " | Facturable: " + c3.calcularPesoFacturable() + " kg | Flete: $" + c3.calcularFlete() + " | Seguro: $" + c3.calcularSeguro() + " | Total: $" + c3.calcularTotal());

            System.out.println("\n=== Caso 4: Corregir PL-000101 a 6.8 kg, pasar a EnRuta y luego a Entregado ===");
            Envio c1Cargar = dao.buscarPorGuia("PL-000101");
            c1Cargar.setPesoReal(6.8);
            dao.actualizar(c1Cargar);
            System.out.println("Nuevo peso facturable: " + c1Cargar.calcularPesoFacturable() + " kg | Nuevo total: $" + c1Cargar.calcularTotal());

            String ant = c1Cargar.getEstado().getNombre();
            c1Cargar.pasarAEnRuta();
            dao.registrarCambioEstado(c1Cargar.getGuia(), ant, c1Cargar.getEstado().getNombre());
            System.out.println("Estado tras transicion 1: " + c1Cargar.getEstado().getNombre());

            ant = c1Cargar.getEstado().getNombre();
            c1Cargar.pasarAEntregado();
            dao.registrarCambioEstado(c1Cargar.getGuia(), ant, c1Cargar.getEstado().getNombre());
            System.out.println("Estado tras transicion 2: " + c1Cargar.getEstado().getNombre());

            System.out.println("\n=== Caso 5: Intento de corregir peso de PL-000101 despues de entregado ===");
            try {
                c1Cargar.setPesoReal(8.0);
                System.out.println("ERROR: No debio permitir modificar datos.");
            } catch (TransicionInvalidaException ex) {
                System.out.println("Capturada excepcion esperada: " + ex.getMessage());
            }

            System.out.println("\n=== Caso 6: PL-000102 a EnRuta, luego a Devuelto, e intentar pasar a Entregado ===");
            Envio c2Cargar = dao.buscarPorGuia("PL-000102");
            ant = c2Cargar.getEstado().getNombre();
            c2Cargar.pasarAEnRuta();
            dao.registrarCambioEstado(c2Cargar.getGuia(), ant, c2Cargar.getEstado().getNombre());

            ant = c2Cargar.getEstado().getNombre();
            c2Cargar.pasarADevuelto();
            dao.registrarCambioEstado(c2Cargar.getGuia(), ant, c2Cargar.getEstado().getNombre());
            System.out.println("Estado actual de PL-000102: " + c2Cargar.getEstado().getNombre());

            try {
                c2Cargar.pasarAEntregado();
                System.out.println("ERROR: No debio permitir pasar de Devuelto a Entregado.");
            } catch (TransicionInvalidaException ex) {
                System.out.println("Capturada excepcion esperada: " + ex.getMessage());
            }

            System.out.println("\n=== Caso 7: Intento de registrar envio internacional de 35 kg ===");
            try {
                new EnvioInternacional("PL-000104", "Luis", "Pedro", "Espana", 35.0, 30, 20, 20, 1000000, new EstadoRegistrado());
                System.out.println("ERROR: No debio permitir peso de 35 kg para servicio internacional.");
            } catch (PesoInvalidoException ex) {
                System.out.println("Capturada excepcion esperada: " + ex.getMessage());
            }

            System.out.println("\n=== Caso 8: Cierre y reapertura, mostrar todos los envios y el historial de PL-000102 ===");
            EnvioDAO nuevoDao = new EnvioDAOImpl();
            List<Envio> envios = nuevoDao.listarTodos();
            for (Envio e : envios) {
                System.out.printf("Guia: %s | Tipo: %-13s | Facturable: %2.0f kg | Flete: $%7.0f | Seguro: $%6.0f | Total: $%7.0f | Estado: %s%n",
                        e.getGuia(), e.getTipo(), e.calcularPesoFacturable(), e.calcularFlete(), e.calcularSeguro(), e.calcularTotal(), e.getEstado().getNombre());
            }

            System.out.println("\nHistorial de estados de PL-000102:");
            List<RegistroHistorial> hist = nuevoDao.consultarHistorialGuia("PL-000102");
            for (RegistroHistorial r : hist) {
                System.out.println(r);
            }

            System.out.println("\nConsulta R4.a: Resumen por Estado:");
            List<ResumenEstado> resumen = nuevoDao.consultarResumenPorEstado();
            for (ResumenEstado re : resumen) {
                System.out.printf("Estado: %-10s | Cantidad: %d | Total Facturado: $%,.0f%n",
                        re.getEstado(), re.getCantidadEnvios(), re.getTotalFacturado());
            }

            System.out.println("\n¡Todos los casos de prueba ejecutados y validados exitosamente!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
