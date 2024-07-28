package modelo;

public class Carta implements ifCarta{
    private final int numero;
    private final Palo palo;
    public static final int COMODIN = -1;

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
