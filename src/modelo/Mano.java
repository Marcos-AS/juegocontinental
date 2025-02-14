package modelo;

import java.util.ArrayList;

public class Mano {

    static void moverCartaEnMano(ArrayList<Carta> mano,
                                        int indCarta, int destino) {
        Carta c = mano.get(indCarta);
        mano.remove(indCarta);
        mano.add(destino, c);
    }

    static ArrayList<Carta> seleccionarCartasABajar(ArrayList<Carta> mano,
                                                           int[] cartasABajar) {
        ArrayList<Carta> juego = new ArrayList<>();
        for (int carta : cartasABajar) juego.add(mano.get(carta));
        return juego;
    }

    static void resetearMano(ArrayList<Jugador> jugadores) {
        for (Jugador j : jugadores) {
            j.resetMano();
        }
    }
}