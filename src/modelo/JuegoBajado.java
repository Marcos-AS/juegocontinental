package modelo;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import static modelo.TipoJuego.*;

abstract class JuegoBajado implements Serializable {
    ArrayList<Carta> juego;

    JuegoBajado(ArrayList<Carta> juego) {
        this.juego = juego;
    }

    abstract boolean acomodarCarta(Carta c);

    public static JuegoBajado crearInstancia(ArrayList<Carta> juego) throws RemoteException {
        TipoJuego tipo = comprobarJuego(juego, Partida.getInstancia().getNumRonda());
        if (tipo == TRIO) {
            return new TrioBajado(juego);
        } else if (tipo == ESCALERA) return new EscaleraBajada(juego);
        return null;
    }

    private static TipoJuego comprobarJuego(ArrayList<Carta> juego, int ronda)
            throws RemoteException {
        TipoJuego tipoJuego = TipoJuego.JUEGO_INVALIDO;
        switch (ronda) {
            case 1:
            case 4:
                if (getRegla("trio").esValido(juego)) tipoJuego = TRIO;
                break;
            case 2:
            case 5:
            case 6:
                if (getRegla("trio").esValido(juego)) {
                    tipoJuego = TRIO;
                } else {
                    if (getRegla("escalera").esValido(juego)) {
                        tipoJuego = ESCALERA;
                    }
                }
                break;
            case 3:
            case 7:
                if (getRegla("escalera").esValido(juego)) {
                    tipoJuego = ESCALERA;
                }
                break;
        }
        return tipoJuego;
    }

    private static ReglaJuego getRegla(String tipo) throws RemoteException {
        return switch (tipo) {
            case "escalera" -> new ReglaEscalera();
            case "trio" -> new ReglaTrio();
            default -> throw new IllegalArgumentException("Tipo de regla no válido");
        };
    }
}