package modelo;

import rmimvc.src.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class JuegoBajado extends ObservableRemoto {
    public static boolean acomodarCartaJuegoPropio(ArrayList<ArrayList<Carta>> juegos, ArrayList<Carta> mano, int numCarta, int numJuego, int ronda) throws RemoteException {
        boolean acomodo = false;
        ArrayList<Carta> juegoElegido = (ArrayList<Carta>) juegos.get(numJuego).clone();
        juegoElegido.add(mano.get(numCarta));
        int tipoJuego = ifPartida.comprobarJuego(juegoElegido, ronda);
        if(tipoJuego != ifPartida.JUEGO_INVALIDO) {
            if (tipoJuego == ifPartida.TRIO) {
                acomodo = ifPartida.comprobarAcomodarEnTrio(juegoElegido) == ifPartida.TRIO;
            } else {
                acomodo = ifPartida.comprobarAcomodarEnEscalera(juegoElegido) == ifPartida.ESCALERA;
            }
            if(acomodo) juegos.get(numJuego).add(Mano.removeCartaFromMano(mano, numCarta)); //hace el acomodo
        }
        return acomodo;
    }

    public static void addJuego(ArrayList<ArrayList<Carta>> juegos, ArrayList<Carta> juego, int tipoJuego) throws RemoteException {
        if (tipoJuego == ifPartida.ESCALERA) {
            juegos.add(ifPartida.ordenarJuego(juego));
        } else {
            juegos.add(juego);
        }
    }
}
