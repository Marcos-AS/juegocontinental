package modelo;

import java.util.ArrayList;

class ReglaMismoPalo extends ReglaJuego{
    @Override
    boolean esValido(ArrayList<Carta> juego) {
        boolean mismoPalo = false;
        for (int i = 0; i < juego.size() - 1; i++) {
            Carta.Palo palo = juego.get(i).getPalo();
            mismoPalo = palo == juego.get(i + 1).getPalo();
        }
        return mismoPalo;
    }
}
