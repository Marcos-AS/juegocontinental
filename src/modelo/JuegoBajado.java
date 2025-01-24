package modelo;

import rmimvc.src.observer.ObservableRemoto;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class JuegoBajado extends ObservableRemoto {
    public static boolean acomodarCarta(ArrayList<ArrayList<Carta>> juegos,
           Carta carta, int numJuego, int ronda) throws RemoteException {
        boolean acomodo;
        ArrayList<Carta> juegoElegido = new ArrayList<>(juegos.get(numJuego));
        //necesito saber si el juego bajado es un trio o una escalera
        if (Comprobar.comprobarJuego(juegoElegido, ronda) == Comprobar.TRIO) {
            acomodo = Comprobar.comprobarAcomodarEnTrio(juegoElegido, carta.getNumero())
                    == Comprobar.TRIO;
        } else {
            juegoElegido.add(carta);
            acomodo = Comprobar.comprobarAcomodarEnEscalera(juegoElegido) == Comprobar.ESCALERA;
        }
        return acomodo;
    }

    public static void addJuego(ArrayList<ArrayList<Carta>> juegos, ArrayList<Carta> juego, int tipoJuego) throws RemoteException {
        if (tipoJuego == Comprobar.ESCALERA) {
            juegos.add(Comprobar.ordenarJuego(juego));
        } else {
            juegos.add(juego);
        }
    }
}
