package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Mano implements Serializable {
    private ArrayList<Carta> mano = new ArrayList<>();

    void resetMano() {
        mano = new ArrayList<>();
    }

    void agregarCarta(Carta c) {
        mano.add(c);
    }

    int getSize() {
        return mano.size();
    }

    Carta getCarta(int iCarta) {
        return mano.get(iCarta);
    }

    Carta removeCarta(int iCarta) {
        return mano.remove(iCarta);
    }

    void moverCartaEnMano(int indCarta, int destino) {
        Carta c = mano.get(indCarta);
        mano.remove(indCarta);
        mano.add(destino, c);
    }

    ArrayList<Carta> seleccionarCartasABajar(int[] cartasABajar) {
        ArrayList<Carta> juego = new ArrayList<>();
        for (int carta : cartasABajar) juego.add(mano.get(carta));
        return juego;
    }

    void removerCartasBajadas(ArrayList<Carta> cartasABajar) {
        for(Carta c : cartasABajar) {
            mano.remove(c);
        }
    }

    public ArrayList<Carta> get() {
        return mano;
    }
}