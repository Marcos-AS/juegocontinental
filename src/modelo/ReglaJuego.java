package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public interface ReglaJuego extends Serializable {
    boolean esValido(ArrayList<Carta> juego);

    static ArrayList<Carta> extraerComodines(ArrayList<Carta> juego) {
        ArrayList<Carta> comodines = new ArrayList<>();
        Iterator<Carta> iterador = juego.iterator();
        while (iterador.hasNext()) {
            Carta c = iterador.next();
            if (c.getPalo()==Palo.COMODIN) {
                comodines.add(c);
                iterador.remove();
            }
        }
        return comodines;
    }

    static void ordenarCartas(ArrayList<Carta> cartas) { //metodo de insercion
        boolean intercambio = true, contieneK = false,contieneAs = false;
        while (intercambio) {
            intercambio = false;
            for (int i = 0; i < cartas.size() - 1; i++) {
                Carta cartaActual = cartas.get(i);
                Carta cartaSiguiente = cartas.get(i+1);
                //valido si hay una k y un as, entonces el as debe ser la ultima carta
                if (!contieneK) contieneK = cartaActual.getNumero() == 13 ||
                        cartaSiguiente.getNumero() == 13;
                if (!contieneAs) contieneAs = cartaActual.getNumero() == 1 ||
                        cartaSiguiente.getNumero() == 1;

                if (cartaActual.getNumero() > cartaSiguiente.getNumero()) {
                    intercambio = true;
                    cartas.set(i, cartaSiguiente); //muevo la siguiente un lugar hacia atras
                    cartas.set(i + 1, cartaActual); //o lo mismo, muevo la actual un lugar hacia delante
                }
            }
        }
        if (contieneAs&&contieneK) {
            Carta as = cartas.remove(0);
            cartas.add(as);
        }
    }
}
