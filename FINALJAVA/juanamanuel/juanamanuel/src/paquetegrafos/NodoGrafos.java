package paquetegrafos;

public class NodoGrafos {
    Object dato;
    ListaAdyancencia lista;
    NodoGrafos siguiente;

    // Propiedades para el cálculo de la ruta crítica
    int inicioTemprano;
    int inicioTardio;
    int holgura;

    public NodoGrafos(Object x) {
        dato = x;
        lista = new ListaAdyancencia();
        siguiente = null;
        inicioTemprano = 0;
        inicioTardio = Integer.MAX_VALUE; // Inicializado con un valor alto
        holgura = 0;
    }

    public void calcularHolgura() {
        holgura = inicioTardio - inicioTemprano;
    }

    public String toString() {
        return "NodoGrafos{" +
                "dato=" + dato +
                ", inicioTemprano=" + inicioTemprano +
                ", inicioTardio=" + inicioTardio +
                ", holgura=" + holgura +
                '}';
    }
}
