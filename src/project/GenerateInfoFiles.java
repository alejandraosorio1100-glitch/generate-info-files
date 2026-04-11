package proyecto;

	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.util.Random;

	/**
	 * Clase encargada de generar archivos planos de prueba
	 * para el proyecto segun ventas.
	 * 
	 * Subgrupo 8
	 * @author Carlos Borja Mora
	 * @author Carlos Cano Fonseca
	 * @author Daniela Osorio Redondo
	 * @author Samir Otero Chala
	 *  
	 */
	public class GenerateInfoFiles {

	    // Carpeta donde se guardaran todos los archivos generados
	    private static final String NOMBRE_CARPETA = "archivos_prueba";

	    // Cantidades base para las pruebas a realizar
	    private static final int CANTIDAD_PRODUCTOS = 12;
	    private static final int CANTIDAD_VENDEDORES = 6;
	    private static final int CANTIDAD_VENTAS_POR_VENDEDOR = 10;

	    // Objeto para generar números aleatorios
	    private static final Random aleatorio = new Random();

	    // Listas cortas para generar nombres para la prueba
	    private static final String[] nombres = {
	        "Juan", "Ana", "Carlos", "Luisa", "Pedro", "Camila",
	        "Sofia", "Andres", "Valentina", "Diego", "Marta", "Daniel"
	    };

	    private static final String[] apellidos = {
	        "Perez", "Gomez", "Rodriguez", "Lopez", "Martinez", "Garcia",
	        "Torres", "Hernandez", "Diaz", "Ramirez", "Morales", "Castro"
	    };

	    private static final String[] nombresProductos = {
	        "Arroz", "Azucar", "Cafe", "Leche", "Pan", "Huevos",
	        "Queso", "Harina", "Pasta", "Aceite", "Chocolate", "Sal",
	        "Jabon", "Galletas", "Mantequilla", "Lentejas"
	    };

	    /**
	     * Método principal que ejecuta la generación de todos los archivos de prueba.
	     * <p>
	     * Crea la carpeta de archivos si no existe, genera el archivo de productos,
	     * el archivo de vendedores, y archivos de ventas para cada vendedor con datos aleatorios.
	     * </p>
	     *
	     * @param args argumentos de línea de comandos (no utilizados)
	     */
	    public static void main(String[] args) {
	        try {
	            crearCarpetaSiNoExiste();

	            createProductsFile(CANTIDAD_PRODUCTOS);
	            createSalesManInfoFile(CANTIDAD_VENDEDORES);

	            for (int i = 0; i < CANTIDAD_VENDEDORES; i++) {
	                String nombreVendedor = nombres[i % nombres.length];
	                long idVendedor = 10000000L + i;
	                createSalesMenFile(CANTIDAD_VENTAS_POR_VENDEDOR, nombreVendedor, idVendedor);
	            }

	            System.out.println("Finalización exitosa: archivos generados correctamente.");
	        } catch (IOException e) {
	            System.out.println("Error: no fue posible generar los archivos.");
	            System.out.println("Detalle del error: " + e.getMessage());
	        }
	    }

	    /**
	     * Crea la carpeta donde se guardarán los archivos,
	     * en caso de que todavía no exista esa carpeta.
	     */
	    private static void crearCarpetaSiNoExiste() {
	        File carpeta = new File(NOMBRE_CARPETA);

	        if (!carpeta.exists()) {
	            carpeta.mkdirs();
	        }
	    }

	    /**
	     * Genera el archivo de productos.
	     * Formato:
	     * IDProducto;NombreProducto;PrecioPorUnidadProducto
	     *
	     * @param productsCount cantidad de productos a generar
	     * @throws IOException error de escritura
	     */
	    public static void createProductsFile(int productsCount) throws IOException {
	        String ruta = NOMBRE_CARPETA + File.separator + "productos.txt";
	        BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta));

	        for (int i = 1; i <= productsCount; i++) {
	            String idProducto = generarIdProducto(i);
	            String nombreProducto = nombresProductos[(i - 1) % nombresProductos.length];
	            int precioPorUnidad = generarNumeroAleatorio(1000, 50000);

	            String linea = idProducto + ";" + nombreProducto + ";" + precioPorUnidad;
	            escritor.write(linea);
	            escritor.newLine();
	        }

	        escritor.close();
	    }

	    /**
	     * Genera el archivo con la información de los vendedores.
	     * Formato:
	     * TipoDocumento;NumeroDocumento;NombresVendedor;ApellidosVendedor
	     *
	     * @param salesmanCount cantidad de vendedores a generar
	     * @throws IOException error de escritura
	     */
	    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
	        String ruta = NOMBRE_CARPETA + File.separator + "vendedores.txt";
	        BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta));

	        for (int i = 0; i < salesmanCount; i++) {
	            String tipoDocumento = "CC";
	            long numeroDocumento = 10000000L + i;
	            String nombre = nombres[i % nombres.length];
	            String apellido = apellidos[i % apellidos.length];

	            String linea = tipoDocumento + ";" + numeroDocumento + ";" + nombre + ";" + apellido;
	            escritor.write(linea);
	            escritor.newLine();
	        }

	        escritor.close();
	    }

	    /**
	     * Genera el archivo de ventas de un vendedor.
	     * Formato:
	     * TipoDocumentoVendedor;NumeroDocumentoVendedor
	     * IDProducto;CantidadProductoVendido;
	     * IDProducto;CantidadProductoVendido;
	     *
	     * @param randomSalesCount cantidad de ventas aleatorias para la prueba
	     * @param name nombre del vendedor
	     * @param id documento del vendedor
	     * @throws IOException error de escritura
	     */
	    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
	        String nombreArchivo = "ventas_" + name + "_" + id + ".txt";
	        String ruta = NOMBRE_CARPETA + File.separator + nombreArchivo;
	        BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta));

	        String tipoDocumento = "CC";

	        // Primera línea: datos del vendedor
	        escritor.write(tipoDocumento + ";" + id);
	        escritor.newLine();

	        // Ventas del vendedor
	        for (int i = 0; i < randomSalesCount; i++) {
	            String idProducto = generarIdProducto(generarNumeroAleatorio(1, CANTIDAD_PRODUCTOS));
	            int cantidadVendida = generarNumeroAleatorio(1, 15);

	            String lineaVenta = idProducto + ";" + cantidadVendida;
	            escritor.write(lineaVenta);
	            escritor.newLine();
	        }

	        escritor.close();
	    }

	    /**
	     * Genera un ID de producto con formato tipo P01, P02, P03...
	     *
	     * @param numero número del producto
	     * @return id del producto en formato texto
	     */
	    private static String generarIdProducto(int numero) {
	        if (numero < 10) {
	            return "P0" + numero;
	        }
	        return "P" + numero;
	    }

	    /**
	     * Devuelve un número entero aleatorio entre mínimo y máximo, incluidos.
	     *
	     * @param minimo valor mínimo
	     * @param maximo valor máximo
	     * @return número aleatorio dentro del rango
	     */
	    private static int generarNumeroAleatorio(int minimo, int maximo) {
	        return aleatorio.nextInt(maximo - minimo + 1) + minimo;
	    }
	}

