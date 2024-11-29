package paquetegrafos;

import java.io.*;
import java.util.*;

public class grafo {
    private NodoGrafos primero;
    private NodoGrafos ultimo;

    public grafo() {
        primero = null;
        ultimo = null;
    }

    public boolean grafoVacio() {
        return primero == null;
    }

    public int getNumeroDeVertices() {
        int contador = 0;
        NodoGrafos temp = primero;
        while (temp != null) {
            contador++;
            temp = temp.siguiente;
        }
        return contador;
    }


    public void guardarArchivo(String rutaArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            NodoGrafos tempFila = primero;

            while (tempFila != null) {
                StringBuilder linea = new StringBuilder();
                NodoGrafos tempColumna = primero;

                while (tempColumna != null) {
                    // Verificar si hay una arista entre tempFila y tempColumna
                    Arco arco = tempFila.lista.primero;
                    boolean encontrado = false;

                    while (arco != null) {
                        if (arco.destino.equals(tempColumna.dato)) {
                            linea.append((int) arco.peso).append(",");
                            encontrado = true;
                            break;
                        }
                        arco = arco.siguiente;
                    }

                    if (!encontrado) {
                        linea.append("0,");
                    }

                    tempColumna = tempColumna.siguiente;
                }

                // Eliminar la última coma y escribir la línea en el archivo
                bw.write(linea.toString().replaceAll(",$", ""));
                bw.newLine();
                tempFila = tempFila.siguiente;
            }

        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }

    public boolean existeVertice(Object dato) {
        NodoGrafos temp = primero;
        while (temp != null) {
            if (temp.dato.equals(dato)) {
                return true;
            }
            temp = temp.siguiente;
        }
        return false;
    }

    public void agregarVertice(Object dato) {
        if (!existeVertice(dato)) {
            NodoGrafos nuevo = new NodoGrafos(dato);
            if (grafoVacio()) {
                primero = nuevo;
                ultimo = nuevo;
            } else {
                ultimo.siguiente = nuevo;
                ultimo = nuevo;
            }
        }
    }

    public void eliminarVertice(Object dato) {
        if (!existeVertice(dato)) {
            System.out.println("El vértice " + dato + " no existe.");
            return;
        }

        // Eliminar todas las aristas que apuntan al vértice
        NodoGrafos temp = primero;
        while (temp != null) {
            if (temp.lista.adyancente(dato)) {
                temp.lista.eliminarAdyancencia(dato);
            }
            temp = temp.siguiente;
        }

        // Eliminar el vértice de la lista principal de nodos
        NodoGrafos actual = primero;
        NodoGrafos anterior = null;

        while (actual != null) {
            if (actual.dato.equals(dato)) {
                if (anterior == null) { // Si es el primer nodo
                    primero = actual.siguiente;
                    if (actual == ultimo) { // Si era el único nodo
                        ultimo = null;
                    }
                } else {
                    anterior.siguiente = actual.siguiente;
                    if (actual == ultimo) { // Si es el último nodo
                        ultimo = anterior;
                    }
                }
                System.out.println("Vértice " + dato + " eliminado.");
                return;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
    }


    public void agregarArista(Object origen, Object destino, float peso) {
        if (existeVertice(origen) && existeVertice(destino)) {
            NodoGrafos temp = primero;
            while (temp != null) {
                if (temp.dato.equals(origen)) {
                    temp.lista.nuevaAdyancencia(destino, peso);
                    break;
                }
                temp = temp.siguiente;
            }
        }
    }

    public void eliminarArista(Object origen, Object destino) {
        if (existeVertice(origen) && existeVertice(destino)) {
            NodoGrafos temp = primero;
            while (temp != null) {
                if (temp.dato.equals(origen)) {
                    if (temp.lista.adyancente(destino)) {
                        temp.lista.eliminarAdyancencia(destino);
                        System.out.println("Arista eliminada de " + origen + " a " + destino);
                    } else {
                        System.out.println("La arista de " + origen + " a " + destino + " no existe.");
                    }
                    break;
                }
                temp = temp.siguiente;
            }
        } else {
            System.out.println("Uno o ambos vértices no existen: " + origen + ", " + destino);
        }
    }


    public void llamarArchivo(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int fila = 0;

            // Crear los nodos (A, B, C, ...) según el número de columnas
            if ((linea = br.readLine()) != null) {
                String[] columnas = linea.split(",");
                for (int i = 0; i < columnas.length; i++) {
                    char nodo = (char) ('A' + i);
                    agregarVertice(String.valueOf(nodo));
                }
            }

            // Volver al inicio del archivo para leer las conexiones
            br.close();
            try (BufferedReader br2 = new BufferedReader(new FileReader(rutaArchivo))) {
                while ((linea = br2.readLine()) != null) {
                    String[] pesos = linea.split(",");
                    char origen = (char) ('A' + fila);

                    for (int columna = 0; columna < pesos.length; columna++) {
                        float peso = Float.parseFloat(pesos[columna]);
                        if (peso > 0) {
                            char destino = (char) ('A' + columna);
                            agregarArista(String.valueOf(origen), String.valueOf(destino), peso);
                        }
                    }
                    fila++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void calcularRutaCritica() {
        Map<Object, Integer> entradas = new HashMap<>();
        Map<Object, NodoGrafos> nodos = new HashMap<>();

        // Inicializar entradas y nodos
        NodoGrafos temp = primero;
        while (temp != null) {
            entradas.put(temp.dato, 0);
            nodos.put(temp.dato, temp);
            temp = temp.siguiente;
        }

        // Calcular grados de entrada
        temp = primero;
        while (temp != null) {
            Arco arco = temp.lista.primero;
            while (arco != null) {
                entradas.put(arco.destino, entradas.get(arco.destino) + 1);
                arco = arco.siguiente;
            }
            temp = temp.siguiente;
        }

        // Orden topológico con cálculo de inicio temprano
        Queue<NodoGrafos> cola = new LinkedList<>();
        Stack<NodoGrafos> pila = new Stack<>(); // Para almacenar el orden topológico inverso

        for (Map.Entry<Object, Integer> entry : entradas.entrySet()) {
            if (entry.getValue() == 0) {
                cola.add(nodos.get(entry.getKey()));
            }
        }

        while (!cola.isEmpty()) {
            NodoGrafos actual = cola.poll();
            pila.push(actual); // Guardamos el nodo en la pila para usarlo en el inicio tardío

            Arco arco = actual.lista.primero;
            while (arco != null) {
                NodoGrafos destino = nodos.get(arco.destino);
                destino.inicioTemprano = Math.max(destino.inicioTemprano, actual.inicioTemprano + (int) arco.peso);
                entradas.put(arco.destino, entradas.get(arco.destino) - 1);
                if (entradas.get(arco.destino) == 0) {
                    cola.add(destino);
                }
                arco = arco.siguiente;
            }
        }

        // Calcular inicio tardío (propagación hacia atrás)
        NodoGrafos nodoFinal = pila.peek();
        nodoFinal.inicioTardio = nodoFinal.inicioTemprano; // Nodo final: inicio tardío = inicio temprano

        while (!pila.isEmpty()) {
            NodoGrafos actual = pila.pop();

            Arco arco = actual.lista.primero;
            while (arco != null) {
                NodoGrafos destino = nodos.get(arco.destino);
                actual.inicioTardio = Math.min(actual.inicioTardio, destino.inicioTardio - (int) arco.peso);
                arco = arco.siguiente;
            }
            // Calcular la holgura del nodo
            actual.calcularHolgura();
        }

        // Mostrar solo los nodos en la ruta crítica con salto de línea
        StringBuilder resultado = new StringBuilder("Ruta crítica:\n");
        temp = primero;
        while (temp != null) {
            if (temp.holgura == 0) { // Solo incluir nodos con holgura 0
                resultado.append(temp.dato)
                        .append(" (Inicio temprano: ").append(temp.inicioTemprano)
                        .append(", Inicio tardío: ").append(temp.inicioTardio)
                        .append(")\n"); // Salto de línea en lugar de flecha
            }
            temp = temp.siguiente;
        }

        System.out.println(resultado.toString().trim());
    }

}
