package project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

	public static void main(String[] args) {

		Random random = new Random();

		for (int i = 1; i <= 5; i++) {

			try {

				FileWriter writer = new FileWriter("vendedor" + i + ".txt");

				int cedula = 10000000 + random.nextInt(90000000);
				writer.write("CC;" + cedula + "\n");

				writer.write("P01;" + random.nextInt(10) + "\n");
				writer.write("P02;" + random.nextInt(10) + "\n");
				writer.write("P03;" + random.nextInt(10) + "\n");

				writer.close();

				System.out.println("Archivo vendedor" + i + " creado");

			} catch (IOException e) {

				System.out.println("Error al crear archivo");

			}

		}

	}

}
