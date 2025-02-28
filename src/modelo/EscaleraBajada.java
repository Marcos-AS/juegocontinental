package modelo;

import java.util.ArrayList;
import java.util.Iterator;

public class EscaleraBajada extends JuegoBajado{
    private EscaleraBajada(ArrayList<Carta> juego) {
        super(juego);
    }

    public static EscaleraBajada crearInstancia(ArrayList<Carta> juego){
        if (esValido(juego)) {
            return new EscaleraBajada(juego);
        }
        return null;
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

    private static boolean esValido(ArrayList<Carta> juego) {
        boolean esEscalera = false;
        ArrayList<Carta> comodines = extraerComodines(juego);
        if (mismoPalo(juego)) {
            int contadorEscalera = 1;
            ordenarCartas(juego);
            for (int i = 0; i < juego.size()-1; i++) {
                int numCartaActual = juego.get(i).getNumero();
                int numCartaSiguiente = juego.get(i + 1).getNumero();
                if (numCartaActual == 13 && numCartaSiguiente == 1) {
                    contadorEscalera++;
                }
                else if (numCartaSiguiente == numCartaActual + 1) {
                    contadorEscalera++;
                } else {
                    if (!comodines.isEmpty()) {
                        if (numCartaActual == numCartaSiguiente - 2) {
                            contadorEscalera += 2;
                            comodines.remove(0);
                        }
                    } else {
                        contadorEscalera = 1;
                    }
                }
            }
            if (!comodines.isEmpty())  contadorEscalera += comodines.size();
            esEscalera = contadorEscalera >= 4;
        }
        return esEscalera;
    }

    private static ArrayList<Carta> extraerComodines(ArrayList<Carta> juego) {
        ArrayList<Carta> comodines = new ArrayList<>();
        Iterator<Carta> iterador = juego.iterator();
        while (iterador.hasNext()) {
            Carta c = iterador.next();
            if (c.getPalo()== Carta.Palo.COMODIN) {
                comodines.add(c);
                iterador.remove();
            }
        }
        return comodines;
    }

    private static void ordenarCartas(ArrayList<Carta> cartas) { //metodo de insercion
        boolean intercambio = true, contieneK = false,contieneAs = false;
        while (intercambio) {
            intercambio = false;
            for (int i = 0; i < cartas.size() - 1; i++) {
                Carta cartaActual = cartas.get(i);
                Carta cartaSiguiente = cartas.get(i+1);
                //valido si hay una k y un as, entonces el as debe ser la ultima carta
                if (!contieneK) contieneK = cartaActual.getNumero() == 13 ||
                        cartaSiguiente.getNumero() == 13;
                if (!contieneAs) contieneAs = cartaActual.getNumero() == 1 ||
                        cartaSiguiente.getNumero() == 1;

                if (cartaActual.getNumero() > cartaSiguiente.getNumero()) {
                    intercambio = true;
                    cartas.set(i, cartaSiguiente); //muevo la siguiente un lugar hacia atras
                    cartas.set(i + 1, cartaActual); //o lo mismo, muevo la actual un lugar hacia delante
                }
            }
        }
        if (contieneAs&&contieneK) {
            Carta as = cartas.remove(0);
            cartas.add(as);
        }
    }

    private static boolean mismoPalo(ArrayList<Carta> juego){
        boolean mismoPalo = false;
        for (int i = 0; i < juego.size() - 1; i++) {
            Carta.Palo palo = juego.get(i).getPalo();
            mismoPalo = palo == juego.get(i + 1).getPalo();
        }
        return mismoPalo;
    }
}