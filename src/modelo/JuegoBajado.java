package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class JuegoBajado {
    public static boolean acomodarCartaJuegoPropio(ArrayList<ArrayList<Carta>> juegos, ArrayList<Carta> mano, int numCarta, int numJuego, int ronda) throws RemoteException {
        boolean acomodo = false;
        ArrayList<Carta> juegoElegido = (ArrayList<Carta>) juegos.get(numJuego).clone();
        juegoElegido.add(mano.get(numCarta));
        int tipoJuego = ifJuego.comprobarJuego(juegoElegido, ronda);
        if(tipoJuego != ifJuego.JUEGO_INVALIDO) {
            if (tipoJuego == ifJuego.TRIO) {
                acomodo = ifJuego.comprobarAcomodarEnTrio(juegoElegido) == ifJuego.TRIO;
            } else {
                acomodo = ifJuego.comprobarAcomodarEnEscalera(juegoElegido) == ifJuego.ESCALERA;
            }
            if(acomodo) juegos.get(numJuego).add(Mano.removeCartaFromMano(mano, numCarta)); //hace el acomodo
        }
        return acomodo;
    }

    public static void addJuego(ArrayList<ArrayList<Carta>> juegos, ArrayList<Carta> juego, int tipoJuego) throws RemoteException {
        if (tipoJuego == ifJuego.ESCALERA) {
            juegos.add(ifJuego.ordenarJuego(juego));
        } else {
            juegos.add(juego);
        }
    }
}
