package modelo;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

abstract class JuegoBajado implements Serializable {
    ArrayList<Carta> juego;

    JuegoBajado(ArrayList<Carta> juego) {
        this.juego = juego;
    }

    abstract boolean acomodarCarta(Carta c);

    public static JuegoBajado crearInstancia(ArrayList<Carta> juego)
            throws RemoteException {
        int ronda = Partida.getInstancia().getNumRonda();
        switch (ronda) {
            case 1:
            case 4:
                return TrioBajado.crearInstancia(juego);
            case 2:
            case 5:
            case 6:
                TrioBajado trio = TrioBajado.crearInstancia(juego);
                if (trio==null) {
                    return EscaleraBajada.crearInstancia(juego);
                }
                break;
            case 3:
            case 7:
                return EscaleraBajada.crearInstancia(juego);
        }
        return null;
    }
}