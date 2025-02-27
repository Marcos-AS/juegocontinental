package modelo;

import java.util.ArrayList;

public class EscaleraBajada extends JuegoBajado{
    public EscaleraBajada(ArrayList<Carta> juego) {
        super(juego);
    }

    boolean acomodarCarta(Carta c) {
        boolean resp = false, mismoPalo = false;
        int i = 0;
        Carta ultimaCarta = juego.get(juego.size()-2);
        Carta primeraCarta = juego.get(0);
        if (c.getPalo()!= Carta.Palo.COMODIN) {
            while (!mismoPalo && i < juego.size()) {
                mismoPalo = juego.get(i).getPalo() == c.getPalo();
                i++;
            }
            if (mismoPalo) {
                //valida si se acomoda la carta al final o al principio
                resp = c.getNumero() == ultimaCarta.getNumero()+1 ||
                        c.getNumero() == primeraCarta.getNumero()-1;
            }
        } else {
            if (ultimaCarta.getPalo()!= Carta.Palo.COMODIN) {
                resp = c.getNumero() == ultimaCarta.getNumero()+1;
            } else if (primeraCarta.getPalo()!= Carta.Palo.COMODIN) {
                resp =  c.getNumero() == primeraCarta.getNumero()-1;
            }
        }
        return resp;
    }
}