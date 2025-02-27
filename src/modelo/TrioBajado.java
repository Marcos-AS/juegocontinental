package modelo;

import java.util.ArrayList;

public class TrioBajado extends JuegoBajado{
    private TrioBajado(ArrayList<Carta> juego) {
        super(juego);
    }

    public static TrioBajado crearInstancia(ArrayList<Carta> juego) {
        if (esValido(juego)) {
           return new TrioBajado(juego);
        }
        return null;
    }

    private static boolean esValido(ArrayList<Carta> juego) {
        int formaTrio = 1;
        //igual a false, lo pongo en numero para despues saber si es una escalera o un trio
        boolean esTrio;
        int i = 0;
        int numCarta = juego.get(i).getNumero();
        while (numCarta == -1 && i < juego.size()-1) {
            formaTrio++;
            i++;
            numCarta = juego.get(i).getNumero();
        }
        while (i < juego.size()-1) {
            i++;
            int numCartaSig = juego.get(i).getNumero();
            if (numCarta == numCartaSig || numCartaSig == -1) {
                formaTrio++;
            } else {
                formaTrio = 0;
            }
        }
        esTrio = formaTrio >= 3;
        return esTrio;
    }

    boolean acomodarCarta(Carta carta) {
        boolean resp = false;
        int valorCarta = carta.getNumero();
        boolean noBuscar = valorCarta == -1;
        if (noBuscar){
            resp = true;
        } else {
            int i = 0;
            Carta c;
            do {
                c = juego.get(i);
                noBuscar = c.getNumero() != -1;
                if (!noBuscar)
                    i++;
                else {
                    resp = c.getNumero() == valorCarta;
                }
            }
            while (!noBuscar && i < juego.size());
        }
        return resp;
    }

}
