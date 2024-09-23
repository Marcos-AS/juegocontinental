package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Mano {
    public static Carta removeCartaFromMano(ArrayList<Carta> mano, int indiceCarta) throws RemoteException {
        Carta cartaATirar = mano.get(indiceCarta);
        mano.remove(indiceCarta);
        return cartaATirar;
    }

    public static void moverCartaEnMano(ArrayList<Carta> mano, int indCarta, int destino) {
        Carta c = mano.get(indCarta);
        mano.remove(indCarta);
        mano.add(destino, c);
    }

    public static ArrayList<Carta> seleccionarCartasABajar(ArrayList<Carta> mano, int[] cartasABajar) {
        ArrayList<Carta> juego = new ArrayList<>();
        for (int carta : cartasABajar) juego.add(mano.get(carta));
        return juego;
    }
}
