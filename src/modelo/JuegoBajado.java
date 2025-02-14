package modelo;

import java.util.ArrayList;
import java.util.Iterator;

public class JuegoBajado {
    static boolean acomodarCarta(ArrayList<ArrayList<Carta>> juegos,
           Carta carta, int numJuego, int ronda) {
        boolean acomodo;
        ArrayList<Carta> juegoElegido = new ArrayList<>(juegos.get(numJuego));
        //necesito saber si el juego bajado es un trio o una escalera
        if (Comprobar.comprobarJuego(juegoElegido, ronda) == Comprobar.TRIO) {
            acomodo = comprobarAcomodarEnTrio(juegoElegido, carta.getNumero())
                    == Comprobar.TRIO;
        } else {
            juegoElegido.add(carta);
            acomodo = comprobarAcomodarEnEscalera(juegoElegido) == Comprobar.ESCALERA;
        }
        return acomodo;
    }

    static int comprobarAcomodarEnTrio(ArrayList<Carta> juego,
                                                 int valorCarta) {
        int resp = Comprobar.JUEGO_INVALIDO;
        boolean noBuscar = valorCarta == -1;
        if (noBuscar){
            resp = Comprobar.TRIO;
        } else {
            int i = 0;
            Carta c;
            do {
                c = juego.get(i);
                noBuscar = c.getNumero() != -1;
                if (!noBuscar)
                    i++;
                else {
                    if (c.getNumero() == valorCarta)
                        resp = Comprobar.TRIO;
                }
            }
            while (!noBuscar && i < juego.size());
        }
        return resp;
    }

    static int comprobarAcomodarEnEscalera(ArrayList<Carta> juego) {
        int resp = Comprobar.JUEGO_INVALIDO;
        if (Comprobar.comprobarMismoPalo(juego)) {
            Carta cartaAcomodar = juego.get(juego.size()-1);
            Carta ultimaCarta = juego.get(juego.size()-2);
            Carta primeraCarta = juego.get(0);
            //valida si se acomoda la carta al final o al principio
            if (cartaAcomodar.getNumero() == ultimaCarta.getNumero()+1 ||
                    cartaAcomodar.getNumero() == primeraCarta.getNumero()-1) {
                resp = Comprobar.ESCALERA;
            }
        }
        return resp;
    }

    static ArrayList<Carta> ordenarJuego(ArrayList<Carta> juego) {
        ArrayList<Carta> comodines = Comprobar.extraerComodines(juego);
        Comprobar.ordenarCartas(juego);
        ArrayList<Carta> juegoOrdenado = new ArrayList<>();
        int numCartaActual;
        int numCartaSiguiente;
        Iterator<Carta> iterador = juego.iterator();
        while (iterador.hasNext()) {
            Carta cActual = iterador.next();
            numCartaActual = cActual.getNumero();
            juegoOrdenado.add(cActual);
            iterador.remove();
            if (iterador.hasNext()) {
                numCartaSiguiente = juego.get(0).getNumero();
                if (numCartaActual != numCartaSiguiente - 1) {
                    juegoOrdenado.add(comodines.get(0));
                    comodines.remove(0);
                }
            }
        }
        if (!comodines.isEmpty()) juegoOrdenado.add(comodines.get(0));
        return juegoOrdenado;
    }
}