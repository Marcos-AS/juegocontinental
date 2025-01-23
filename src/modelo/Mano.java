package modelo;

import rmimvc.src.observer.ObservableRemoto;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Mano extends ObservableRemoto {

    public static void moverCartaEnMano(ArrayList<Carta> mano, int indCarta, int destino) {
        Carta c = mano.get(indCarta);
        mano.remove(indCarta);
        mano.add(destino, c);
    }

    public static ArrayList<Carta> seleccionarCartasABajar(ArrayList<Carta> mano,
                                                           int[] cartasABajar) {
        ArrayList<Carta> juego = new ArrayList<>();
        for (int carta : cartasABajar) juego.add(mano.get(carta));
        return juego;
    }

    public static void resetearMano(ArrayList<Jugador> jugadores) throws RemoteException {
        for (Jugador j : jugadores) {
            j.resetMano();
        }
    }
}
