package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

class JuegoBajado implements Serializable {
    ArrayList<Carta> juego;
    TipoJuego tipo;
    private final ReglaJuego MISMO_PALO = new ReglaMismoPalo();

    JuegoBajado(ArrayList<Carta> juego, TipoJuego tipo) {
        ArrayList<Carta> juegoOrdenado = juego;
        if (tipo==TipoJuego.ESCALERA) {
            juegoOrdenado = ordenarJuego(juego);
        }
        this.juego = juegoOrdenado;
        this.tipo = tipo;
    }

    boolean acomodarCarta(Carta carta) {
        boolean acomodo;
        if (tipo == TipoJuego.TRIO) {
            acomodo = comprobarAcomodarEnTrio(carta.getNumero());
        } else {
            acomodo = comprobarAcomodarEnEscalera(carta);
        }
        return acomodo;
    }

    private boolean comprobarAcomodarEnTrio(int valorCarta) {
        boolean resp = false;
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

    private boolean comprobarAcomodarEnEscalera(Carta c) {
        boolean resp = false;
        juego.add(c);
        if (MISMO_PALO.esValido(juego)) {
            Carta cartaAcomodar = juego.get(juego.size()-1);
            Carta ultimaCarta = juego.get(juego.size()-2);
            Carta primeraCarta = juego.get(0);
            //valida si se acomoda la carta al final o al principio
            resp = cartaAcomodar.getNumero() == ultimaCarta.getNumero()+1 ||
                    cartaAcomodar.getNumero() == primeraCarta.getNumero()-1;
        }
        return resp;
    }

    private ArrayList<Carta> ordenarJuego(ArrayList<Carta> juego) {
        ArrayList<Carta> comodines = ReglaJuego.extraerComodines(juego);
        ReglaJuego.ordenarCartas(juego);
        ArrayList<Carta> juegoOrdenado = new ArrayList<>();
        int numCartaActual;
        int numCartaSiguiente;
        Iterator<Carta> iterador = juego.iterator();
        while (iterador.hasNext()) {
            Carta cActual = iterador.next();
            numCartaActual = cActual.getNumero();
            juegoOrdenado.add(cActual);
            iterador.remove();
            if (iterador.hasNext()) {
                numCartaSiguiente = juego.get(0).getNumero();
                if (numCartaActual != numCartaSiguiente - 1) {
                    juegoOrdenado.add(comodines.get(0));
                    comodines.remove(0);
                }
            }
        }
        if (!comodines.isEmpty()) juegoOrdenado.add(comodines.get(0));
        return juegoOrdenado;
    }
}