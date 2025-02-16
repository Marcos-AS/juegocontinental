package modelo;

import java.io.Serializable;

public class Carta implements ifCarta, Serializable {
    private final int numero;
    private final Palo palo;

    public enum Palo implements Serializable {
        PICAS, DIAMANTES, TREBOL, CORAZONES, COMODIN
    }

    public Carta(int num, Palo palo) {
        numero = num;
        this.palo = palo;
    }

    public Palo getPalo() {
        return palo;
    }


    public int getNumero() {
        return numero;
    }
}