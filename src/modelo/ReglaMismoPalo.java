package modelo;

import java.util.ArrayList;

public class ReglaMismoPalo implements ReglaJuego{
    @Override
    public boolean esValido(ArrayList<Carta> juego) {
        boolean mismoPalo = false;
        for (int i = 0; i < juego.size() - 1; i++) {
            Palo palo = juego.get(i).getPalo();
            mismoPalo = palo == juego.get(i + 1).getPalo();
        }
        return mismoPalo;
    }
}
