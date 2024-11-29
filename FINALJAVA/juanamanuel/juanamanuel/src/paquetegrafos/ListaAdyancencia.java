package paquetegrafos;

public class ListaAdyancencia {
    Arco primero;
    Arco ultimo;

    public ListaAdyancencia() {
        primero = null;
        ultimo = null;
    }

    public boolean listaVacia() {
        return primero == null;
    }

    public void nuevaAdyancencia(Object destino, float peso) {
        if (!adyancente(destino)) {
            Arco nodo = new Arco(destino, peso);
            if (listaVacia()) {
                primero = nodo;
                ultimo = nodo;
            } else {
                ultimo.siguiente = nodo;
                ultimo = nodo;
            }
        }
    }

    public void eliminarAdyancencia(Object destino) {
        if (!listaVacia()) {
            Arco actual = primero;
            Arco anterior = null;

            while (actual != null) {
                if (actual.destino.equals(destino)) {
                    if (anterior == null) { // Si es el primer arco
                        primero = actual.siguiente;
                    } else {
                        anterior.siguiente = actual.siguiente;
                    }
                    if (actual == ultimo) { // Si es el último arco
                        ultimo = anterior;
                    }
                    break;
                }
                anterior = actual;
                actual = actual.siguiente;
            }
        }
    }


    public boolean adyancente(Object destino) {
        Arco temp = primero;
        while (temp != null) {
            if (temp.destino.equals(destino)) {
                return true;
            }
            temp = temp.siguiente;
        }
        return false;
    }
}
