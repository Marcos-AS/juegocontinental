package modelo;

import java.util.ArrayList;

public class ReglaTrio implements ReglaJuego {

    @Override
    public boolean esValido(ArrayList<Carta> juego) {
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

}
