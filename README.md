# Proyecto Java - Generación y Clasificación de Datos

## Descripción

Este proyecto fue desarrollado para el módulo de Conceptos Fundamentales de Programación.
El programa permite generar archivos de prueba, procesar información de ventas y generar reportes a partir de archivos de texto plano.

---

## Funcionalidades

* Generación de archivos de prueba:

  * Productos
  * Vendedores
  * Ventas
* Lectura de archivos de texto plano
* Procesamiento de información de ventas
* Generación de reportes:

  * Reporte de ventas por vendedor (ordenado de mayor a menor)
  * Reporte de productos vendidos por cantidad (ordenado de forma descendente)

---

## Estructura del proyecto

El proyecto cuenta con dos clases principales:

* **GenerateInfoFiles:** genera los archivos de prueba necesarios para la ejecución.
* **Main:** lee los archivos, procesa la información y genera los reportes finales.

Además, se utiliza la carpeta `archivos_prueba`, donde se almacenan los archivos de entrada y salida.

---

## Archivos generados

Al ejecutar el proyecto se generan los siguientes archivos:

* productos.txt
* vendedores.txt
* archivos de ventas por vendedor (ventas_*.txt)
* reporte_vendedores.csv
* reporte_productos.csv

---

## Cómo ejecutar el proyecto

1. Ejecutar la clase `GenerateInfoFiles` para generar los archivos de prueba.
2. Ejecutar la clase `Main` para procesar la información.
3. Revisar los reportes generados en la carpeta `archivos_prueba`.

---

## Consideraciones

* El programa no solicita datos al usuario.
* Se implementan validaciones básicas para evitar datos incorrectos.
* El proyecto está desarrollado en Java 8.

---

## Autor(es)

Subgrupo 8
Carlos Borja Mora
Carlos Cano Fonseca
Daniela Osorio Redondo
Samir Otero Chala
