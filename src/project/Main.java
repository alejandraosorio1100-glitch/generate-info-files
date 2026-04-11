package project;

import java.io.*;
import java.util.*;

/**
 * Clase principal del proyecto GenerateInfoFiles.
 * Esta clase se encarga de procesar archivos de ventas, calcular totales de ventas por vendedor
 * y cantidades vendidas por producto, y generar reportes correspondientes.
 * <p>
 * El programa lee archivos de productos y vendedores, procesa archivos de ventas individuales
 * para cada vendedor, y genera dos reportes: uno de vendedores ordenado por total de ventas
 * descendente, y otro de productos ordenado por cantidad vendida descendente.
 * </p>
 * <p>
 * Archivos de entrada esperados en la carpeta "archivos_prueba":
 * <ul>
 * <li>productos.txt: ID;Nombre;Precio</li>
 * <li>vendedores.txt: TipoDoc;ID;Nombre;Apellido</li>
 * <li>ventas_[Nombre]_[ID].txt: Primera línea TipoDoc;ID, luego líneas IDProducto;Cantidad</li>
 * </ul>
 * </p>
 * <p>
 * Archivos de salida generados:
 * <ul>
 * <li>reporte_vendedores.txt: Nombre;TotalVentas</li>
 * <li>reporte_productos.txt: ID;Precio;CantidadVendida</li>
 * </ul>
 * </p>
 *
 * Subgrupo 8
 * @author Carlos Borja Mora
 * @author Carlos Cano Fonseca
 * @author Daniela Osorio Redondo
 * @author Samir Otero Chala
 * @version 1.0
 * @since 2023
 */
public class Main {

    /**
     * Constante que define el nombre de la carpeta donde se encuentran los archivos de datos.
     */
    private static final String CARPETA = "archivos_prueba";

    /**
     * Método principal que ejecuta el proceso completo de carga de datos, procesamiento de ventas
     * y generación de reportes.
     * <p>
     * Carga los productos y vendedores desde archivos, procesa todos los archivos de ventas
     * encontrados en la carpeta especificada, calcula totales y genera los reportes ordenados.
     * </p>
     *
     * @param args argumentos de línea de comandos (no utilizados en esta implementación)
     */
    public static void main(String[] args) {

        try {

            // Cargar datos
            Map<String, Integer> preciosProductos = new HashMap<>();
            Map<String, String> nombresVendedores = new HashMap<>();

            cargarProductos(preciosProductos);
            cargarVendedores(nombresVendedores);

            Map<String, Integer> ventasPorVendedor = new HashMap<>();
            Map<String, Integer> productosVendidos = new HashMap<>();

            File carpeta = new File(CARPETA);
            File[] archivos = carpeta.listFiles();

            if (archivos == null) {
                System.out.println("No hay archivos");
                return;
            }

            // Leer archivos de ventas
            for (int i = 0; i < archivos.length; i++) {

                File archivo = archivos[i];

                if (archivo.getName().startsWith("ventas_")) {
                    procesarVentas(
                            archivo,
                            preciosProductos,
                            ventasPorVendedor,
                            productosVendidos);
                }
            }

            // Generar reportes
            generarReporteVendedores(ventasPorVendedor, nombresVendedores);
            generarReporteProductos(productosVendidos, preciosProductos);

            System.out.println("Proceso terminado correctamente");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ============================
    // CARGAR PRODUCTOS
    // ============================
    /**
     * Carga los productos desde el archivo "productos.txt" en el mapa proporcionado.
     * <p>
     * El archivo debe tener el formato: IDProducto;NombreProducto;PrecioPorUnidad
     * Solo se almacena el ID como clave y el precio como valor.
     * </p>
     *
     * @param productos mapa donde se almacenarán los productos con ID como clave y precio como valor
     * @throws IOException si ocurre un error al leer el archivo
     */
    public static void cargarProductos(Map<String, Integer> productos) throws IOException {

        BufferedReader br = new BufferedReader(
                new FileReader(CARPETA + "/productos.txt"));

        String linea;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(";");

            String id = partes[0];
            int precio = Integer.parseInt(partes[2]);

            productos.put(id, precio);
        }

        br.close();
    }

    // ============================
    // CARGAR VENDEDORES
    // ============================
    /**
     * Carga los vendedores desde el archivo "vendedores.txt" en el mapa proporcionado.
     * <p>
     * El archivo debe tener el formato: TipoDocumento;NumeroDocumento;Nombres;Apellidos
     * Se almacena el ID (NumeroDocumento) como clave y el nombre completo (Nombres + Apellidos) como valor.
     * </p>
     *
     * @param vendedores mapa donde se almacenarán los vendedores con ID como clave y nombre completo como valor
     * @throws IOException si ocurre un error al leer el archivo
     */
    public static void cargarVendedores(Map<String, String> vendedores) throws IOException {

        BufferedReader br = new BufferedReader(
                new FileReader(CARPETA + "/vendedores.txt"));

        String linea;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(";");

            String id = partes[1];
            String nombre = partes[2] + " " + partes[3];

            vendedores.put(id, nombre);
        }

        br.close();
    }

    // ============================
    // PROCESAR VENTAS
    // ============================
    /**
     * Procesa un archivo de ventas de un vendedor específico.
     * <p>
     * Lee el archivo, extrae el ID del vendedor de la primera línea, calcula el total de ventas
     * basado en los productos vendidos y sus precios, y actualiza los mapas de ventas por vendedor
     * y productos vendidos.
     * </p>
     * <p>
     * Formato del archivo: Primera línea TipoDoc;IDVendedor, líneas siguientes IDProducto;Cantidad
     * </p>
     *
     * @param archivo el archivo de ventas a procesar
     * @param precios mapa de precios de productos (ID -> precio)
     * @param ventas mapa de ventas totales por vendedor (ID -> total)
     * @param productos mapa de cantidades vendidas por producto (ID -> cantidad)
     * @throws IOException si ocurre un error al leer el archivo
     */
    public static void procesarVentas(
            File archivo,
            Map<String, Integer> precios,
            Map<String, Integer> ventas,
            Map<String, Integer> productos) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(archivo));

        String primeraLinea = br.readLine();
        String[] datos = primeraLinea.split(";");

        String idVendedor = datos[1];

        int total = 0;

        String linea;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(";");

            String idProducto = partes[0];
            int cantidad = Integer.parseInt(partes[1]);

            if (!precios.containsKey(idProducto)) {
                continue;
            }

            int precio = precios.get(idProducto);

            total = total + (precio * cantidad);

            if (productos.containsKey(idProducto)) {
                productos.put(idProducto, productos.get(idProducto) + cantidad);
            } else {
                productos.put(idProducto, cantidad);
            }
        }

        ventas.put(idVendedor, total);

        br.close();
    }

    // ============================
    // REPORTE VENDEDORES
    // ============================
    /**
     * Genera el reporte de vendedores ordenado por total de ventas descendente.
     * <p>
     * Ordena los vendedores por el total de ventas de mayor a menor usando un algoritmo
     * de ordenamiento burbuja simple, y escribe el reporte en "reporte_vendedores.txt"
     * con el formato: NombreVendedor;TotalVentas
     * </p>
     *
     * @param ventas mapa de ventas totales por vendedor (ID -> total)
     * @param nombres mapa de nombres de vendedores (ID -> nombre completo)
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void generarReporteVendedores(
            Map<String, Integer> ventas,
            Map<String, String> nombres) throws IOException {

        List<String> ids = new ArrayList<>(ventas.keySet());

        // Ordenar manual (burbuja simple - nivel básico)
        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < ids.size() - 1; j++) {

                String id1 = ids.get(j);
                String id2 = ids.get(j + 1);

                if (ventas.get(id1) < ventas.get(id2)) {

                    String temp = ids.get(j);
                    ids.set(j, ids.get(j + 1));
                    ids.set(j + 1, temp);
                }
            }
        }

        BufferedWriter bw = new BufferedWriter(
                new FileWriter(CARPETA + "/reporte_vendedores.txt"));

        for (int i = 0; i < ids.size(); i++) {

            String id = ids.get(i);
            String nombre = nombres.get(id);
            int total = ventas.get(id);

            bw.write(nombre + ";" + total);
            bw.newLine();
        }

        bw.close();
    }

    // ============================
    // REPORTE PRODUCTOS
    // ============================
    /**
     * Genera el reporte de productos ordenado por cantidad vendida descendente.
     * <p>
     * Ordena los productos por la cantidad total vendida de mayor a menor usando un algoritmo
     * de ordenamiento burbuja simple, y escribe el reporte en "reporte_productos.txt"
     * con el formato: IDProducto;PrecioPorUnidad;CantidadVendida
     * </p>
     *
     * @param productos mapa de cantidades vendidas por producto (ID -> cantidad)
     * @param precios mapa de precios de productos (ID -> precio)
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void generarReporteProductos(
            Map<String, Integer> productos,
            Map<String, Integer> precios) throws IOException {

        List<String> ids = new ArrayList<>(productos.keySet());

        // Ordenar manual
        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < ids.size() - 1; j++) {

                String id1 = ids.get(j);
                String id2 = ids.get(j + 1);

                if (productos.get(id1) < productos.get(id2)) {

                    String temp = ids.get(j);
                    ids.set(j, ids.get(j + 1));
                    ids.set(j + 1, temp);
                }
            }
        }

        BufferedWriter bw = new BufferedWriter(
                new FileWriter(CARPETA + "/reporte_productos.txt"));

        for (int i = 0; i < ids.size(); i++) {

            String id = ids.get(i);
            int cantidad = productos.get(id);
            int precio = precios.get(id);

            bw.write(id + ";" + precio + ";" + cantidad);
            bw.newLine();
        }

        bw.close();
    }
}