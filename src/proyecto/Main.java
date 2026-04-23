package proyecto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Segunda clase ejecutable del proyecto entrega final.
 *
 * <p>
 * Esta clase lee los archivos de productos, vendedores y ventas ubicados en la
 * carpeta de trabajo, procesa la información y genera dos reportes:
 * </p>
 *
 * <ul>
 * <li>reporte_vendedores.csv: vendedores ordenados de mayor a menor por dinero
 * recaudado.</li>
 * <li>reporte_productos.csv: productos ordenados de mayor a menor por cantidad
 * vendida.</li>
 * </ul>
 *
 * <p>
 * Es de anotar que el programa no solicita datos al usuario, tal como lo exige
 * la guía del proyecto.
 * </p>
 *
 * Subgrupo 8
 *
 * @author Carlos Borja Mora
 * @author Carlos Cano Fonseca
 * @author Daniela Osorio Redondo
 * @author Samir Otero Chala
 */
public class Main {

    /** Carpeta donde están los archivos de entrada y se generan los reportes. */
    private static final String CARPETA = "archivos_prueba";

    /** Nombre del archivo de productos. */
    private static final String ARCHIVO_PRODUCTOS = CARPETA + File.separator + "productos.txt";

    /** Nombre del archivo de vendedores. */
    private static final String ARCHIVO_VENDEDORES = CARPETA + File.separator + "vendedores.txt";

    /**
     * Método principal. Ejecuta la carga, el procesamiento y la generación de
     * reportes.
     *
     * @param args argumentos de línea de comandos no utilizados.
     */
    public static void main(String[] args) {
        try {
            Map<String, ProductoInfo> productos = new HashMap<String, ProductoInfo>();
            Map<String, String> vendedores = new HashMap<String, String>();
            Map<String, Integer> ventasPorVendedor = new HashMap<String, Integer>();
            Map<String, Integer> cantidadesPorProducto = new HashMap<String, Integer>();

            cargarProductos(productos);
            cargarVendedores(vendedores);
            procesarArchivosDeVentas(productos, vendedores, ventasPorVendedor, cantidadesPorProducto);
            generarReporteVendedores(ventasPorVendedor, vendedores);
            generarReporteProductos(cantidadesPorProducto, productos);

            System.out.println("Finalización exitosa: reportes generados correctamente.");
        } catch (IOException e) {
            System.out.println("Error: no fue posible procesar los archivos.");
            System.out.println("Detalle del error: " + e.getMessage());
        }
    }

    /**
     * Carga los productos desde el archivo productos.txt.
     *
     * @param productos mapa donde se almacenan los productos por id.
     * @throws IOException si hay errores de lectura o formato.
     */
    public static void cargarProductos(Map<String, ProductoInfo> productos) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_PRODUCTOS));
        String linea;
        int numeroLinea = 0;

        while ((linea = lector.readLine()) != null) {
            numeroLinea++;
            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] partes = linea.split(";");
            if (partes.length != 3) {
                throw new IOException("Formato incorrecto en productos.txt, línea " + numeroLinea + ".");
            }

            String id = partes[0].trim();
            String nombre = partes[1].trim();
            int precio = Integer.parseInt(partes[2].trim());

            if (precio < 0) {
                throw new IOException("Precio negativo detectado para el producto " + id + ".");
            }

            productos.put(id, new ProductoInfo(nombre, precio));
        }

        lector.close();
    }

    /**
     * Carga los vendedores desde el archivo vendedores.txt.
     *
     * @param vendedores mapa donde se almacenan los nombres completos por id.
     * @throws IOException si hay errores de lectura o formato.
     */
    public static void cargarVendedores(Map<String, String> vendedores) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_VENDEDORES));
        String linea;
        int numeroLinea = 0;

        while ((linea = lector.readLine()) != null) {
            numeroLinea++;
            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] partes = linea.split(";");
            if (partes.length != 4) {
                throw new IOException("Formato incorrecto en vendedores.txt, línea " + numeroLinea + ".");
            }

            String id = partes[1].trim();
            String nombreCompleto = partes[2].trim() + " " + partes[3].trim();
            vendedores.put(id, nombreCompleto);
        }

        lector.close();
    }

    /**
     * Procesa todos los archivos de ventas que existan en la carpeta de trabajo.
     *
     * @param productos             productos cargados.
     * @param vendedores            vendedores cargados.
     * @param ventasPorVendedor     acumulado de dinero por vendedor.
     * @param cantidadesPorProducto acumulado de cantidades por producto.
     * @throws IOException si hay errores de lectura o datos incoherentes.
     */
    public static void procesarArchivosDeVentas(
            Map<String, ProductoInfo> productos,
            Map<String, String> vendedores,
            Map<String, Integer> ventasPorVendedor,
            Map<String, Integer> cantidadesPorProducto) throws IOException {

        File carpeta = new File(CARPETA);
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            throw new IOException("No fue posible acceder a la carpeta de trabajo.");
        }

        for (int i = 0; i < archivos.length; i++) {
            File archivoActual = archivos[i];
            if (archivoActual.isFile() && archivoActual.getName().startsWith("ventas_")) {
                procesarVentas(archivoActual, productos, vendedores, ventasPorVendedor, cantidadesPorProducto);
            }
        }
    }

    /**
     * Procesa un archivo de ventas individual.
     *
     * @param archivo               archivo de ventas a procesar.
     * @param productos             mapa de productos.
     * @param vendedores            mapa de vendedores.
     * @param ventasPorVendedor     acumulado de dinero por vendedor.
     * @param cantidadesPorProducto acumulado de cantidades por producto.
     * @throws IOException si el archivo tiene formato incorrecto o datos
     *                     incoherentes.
     */
    public static void procesarVentas(
            File archivo,
            Map<String, ProductoInfo> productos,
            Map<String, String> vendedores,
            Map<String, Integer> ventasPorVendedor,
            Map<String, Integer> cantidadesPorProducto) throws IOException {

        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        String primeraLinea = lector.readLine();

        if (primeraLinea == null || primeraLinea.trim().isEmpty()) {
            lector.close();
            throw new IOException("El archivo " + archivo.getName() + " está vacío.");
        }

        String[] datosVendedor = primeraLinea.split(";");
        if (datosVendedor.length != 2) {
            lector.close();
            throw new IOException("Formato incorrecto en la cabecera del archivo " + archivo.getName() + ".");
        }

        String idVendedor = datosVendedor[1].trim();
        if (!vendedores.containsKey(idVendedor)) {
            lector.close();
            throw new IOException("El vendedor con id " + idVendedor + " no existe en vendedores.txt.");
        }

        int totalArchivo = 0;
        String linea;
        int numeroLinea = 1;

        while ((linea = lector.readLine()) != null) {
            numeroLinea++;
            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] partes = linea.split(";");
            if (partes.length < 2) {
                lector.close();
                throw new IOException("Formato incorrecto en " + archivo.getName() + ", línea " + numeroLinea + ".");
            }

            String idProducto = partes[0].trim();
            int cantidadVendida = Integer.parseInt(partes[1].trim());

            if (!productos.containsKey(idProducto)) {
                lector.close();
                throw new IOException(
                        "Producto inexistente " + idProducto + " en el archivo " + archivo.getName() + ".");
            }

            if (cantidadVendida < 0) {
                lector.close();
                throw new IOException("Cantidad negativa detectada en el archivo " + archivo.getName() + ".");
            }

            ProductoInfo producto = productos.get(idProducto);
            totalArchivo += producto.getPrecio() * cantidadVendida;

            if (cantidadesPorProducto.containsKey(idProducto)) {
                cantidadesPorProducto.put(idProducto, cantidadesPorProducto.get(idProducto) + cantidadVendida);
            } else {
                cantidadesPorProducto.put(idProducto, cantidadVendida);
            }
        }

        if (ventasPorVendedor.containsKey(idVendedor)) {
            ventasPorVendedor.put(idVendedor, ventasPorVendedor.get(idVendedor) + totalArchivo);
        } else {
            ventasPorVendedor.put(idVendedor, totalArchivo);
        }

        lector.close();
    }

    /**
     * Genera el reporte de vendedores ordenado de mayor a menor por dinero
     * recaudado.
     *
     * @param ventasPorVendedor mapa con el dinero total por vendedor.
     * @param vendedores        mapa con los nombres completos por vendedor.
     * @throws IOException si ocurre un error al escribir el archivo.
     */
    public static void generarReporteVendedores(
            Map<String, Integer> ventasPorVendedor,
            Map<String, String> vendedores) throws IOException {

        List<String> ids = new ArrayList<String>(ventasPorVendedor.keySet());
        ordenarIdsPorValorDescendente(ids, ventasPorVendedor);

        BufferedWriter escritor = new BufferedWriter(
                new FileWriter(CARPETA + File.separator + "reporte_vendedores.csv"));

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            escritor.write(vendedores.get(id) + ";" + ventasPorVendedor.get(id));
            escritor.newLine();
        }

        escritor.close();
    }

    /**
     * Genera el reporte de productos ordenado de mayor a menor por cantidad
     * vendida.
     *
     * @param cantidadesPorProducto mapa con cantidades vendidas por producto.
     * @param productos             mapa con información completa de cada producto.
     * @throws IOException si ocurre un error al escribir el archivo.
     */
    public static void generarReporteProductos(
            Map<String, Integer> cantidadesPorProducto,
            Map<String, ProductoInfo> productos) throws IOException {

        List<String> ids = new ArrayList<String>(cantidadesPorProducto.keySet());
        ordenarIdsPorValorDescendente(ids, cantidadesPorProducto);

        BufferedWriter escritor = new BufferedWriter(
                new FileWriter(CARPETA + File.separator + "reporte_productos.csv"));

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            ProductoInfo producto = productos.get(id);
            int cantidad = cantidadesPorProducto.get(id);

            escritor.write(producto.getNombre() + ";" + producto.getPrecio() + ";" + cantidad);
            escritor.newLine();
        }

        escritor.close();
    }

    /**
     * Ordena una lista de claves de acuerdo con los valores enteros asociados, de
     * mayor a menor.
     * En caso de empate, ordena alfabéticamente por la clave.
     *
     * @param ids     lista de claves a ordenar.
     * @param valores mapa de valores usados para comparar.
     */
    public static void ordenarIdsPorValorDescendente(List<String> ids, Map<String, Integer> valores) {
        Collections.sort(ids, (id1, id2) -> {
            int comparacion = Integer.compare(valores.get(id2), valores.get(id1));
            if (comparacion != 0) {
                return comparacion;
            }
            return id1.compareTo(id2);
        });
    }

    /**
     * Clase auxiliar para almacenar nombre y precio de un producto.
     */
    private static class ProductoInfo {
        private final String nombre;
        private final int precio;

        ProductoInfo(String nombre, int precio) {
            this.nombre = nombre;
            this.precio = precio;
        }

        public String getNombre() {
            return nombre;
        }

        public int getPrecio() {
            return precio;
        }
    }
}
