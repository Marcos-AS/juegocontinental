package modelo;

import rmimvc.src.observer.ObservableRemoto;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public class JuegoBajado extends ObservableRemoto {
    protected static boolean acomodarCarta(ArrayList<ArrayList<Carta>> juegos,
           Carta carta, int numJuego, int ronda) throws RemoteException {
        boolean acomodo;
        ArrayList<Carta> juegoElegido = new ArrayList<>(juegos.get(numJuego));
        //necesito saber si el juego bajado es un trio o una escalera
        if (Comprobar.comprobarJuego(juegoElegido, ronda) == Comprobar.TRIO) {
            acomodo = comprobarAcomodarEnTrio(juegoElegido, carta.getNumero())
                    == Comprobar.TRIO;
        } else {
            juegoElegido.add(carta);
            acomodo = comprobarAcomodarEnEscalera(juegoElegido) == Comprobar.ESCALERA;
        }
        return acomodo;
    }

    protected static int comprobarAcomodarEnTrio(ArrayList<Carta> juego,
                                                 int valorCarta) throws RemoteException {
        int resp = Comprobar.JUEGO_INVALIDO;
        boolean noBuscar = valorCarta == Carta.COMODIN;
        if (noBuscar){
            resp = Comprobar.TRIO;
        } else {
            int i = 0;
            Carta c;
            do {
                c = juego.get(i);
                noBuscar = c.getNumero() != Carta.COMODIN;
                if (!noBuscar)
                    i++;
                else {
                    if (c.getNumero() == valorCarta)
                        resp = Comprobar.TRIO;
                }
            }
            while (!noBuscar && i < juego.size());
        }
        return resp;
    }

    protected static int comprobarAcomodarEnEscalera(ArrayList<Carta> juego)
            throws RemoteException {
        int resp = Comprobar.JUEGO_INVALIDO;
        if (Comprobar.comprobarMismoPalo(juego)) {
            Carta cartaAcomodar = juego.get(juego.size()-1);
            Carta ultimaCarta = juego.get(juego.size()-2);
            Carta primeraCarta = juego.get(0);
            //valida si se acomoda la carta al final o al principio
            if (cartaAcomodar.getNumero() == ultimaCarta.getNumero()+1 ||
                    cartaAcomodar.getNumero() == primeraCarta.getNumero()-1) {
                resp = Comprobar.ESCALERA;
            }
        }
        return resp;
    }

    protected static ArrayList<Carta> ordenarJuego(ArrayList<Carta> juego)
            throws RemoteException{
        ArrayList<Carta> comodines = Comprobar.extraerComodines(juego);
        Comprobar.ordenarCartas(juego);
        ArrayList<Carta> juegoOrdenado = new ArrayList<>();
        int numCActual;
        int numCSiguiente;
        Iterator<Carta> iterador = juego.iterator();
        while (iterador.hasNext()) {
            Carta cActual = iterador.next();
            numCActual = cActual.getNumero();
            juegoOrdenado.add(cActual);
            iterador.remove();
            if (iterador.hasNext()) {
                numCSiguiente = juego.get(0).getNumero();
                if (numCActual != numCSiguiente - 1) {
                    juegoOrdenado.add(comodines.get(0));
                    comodines.remove(0);
                }
            }
        }
        if (!comodines.isEmpty()) juegoOrdenado.add(comodines.get(0));
        return juegoOrdenado;
    }
}
