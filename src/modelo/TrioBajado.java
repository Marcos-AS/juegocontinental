package modelo;

import java.util.ArrayList;

public class TrioBajado extends JuegoBajado{
    public TrioBajado(ArrayList<Carta> juego) {
        super(juego);
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
