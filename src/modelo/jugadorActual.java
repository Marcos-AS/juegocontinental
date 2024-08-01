package modelo;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class jugadorActual extends Jugador implements Serializable, ifJugador {
    public boolean turnoActual = false;
    public int numeroJugador;
    private ArrayList<Carta> mano = new ArrayList<>();
    public boolean roboDelMazo = false;
    public boolean roboConCastigo = false;
    public ArrayList<ArrayList<Carta>> juegos = new ArrayList<>();
    public int puedeBajar = 0;
    public int triosBajados;
    public int escalerasBajadas;
    public int puntosPartida = 0;
    public boolean ganador = false;

    public jugadorActual(String nombre) throws RemoteException {
        super(nombre);
    }

    public void setTurnoActual(boolean turnoActual) throws RemoteException {
        this.turnoActual = turnoActual;
    }

    public void setNumeroJugador(int numeroJugador) throws RemoteException {
        this.numeroJugador = numeroJugador;
    }

    public void agregarCarta(Carta c) throws RemoteException {
        mano.add(c);
    }

    public void setRoboDelMazo(boolean roboDelMazo) throws RemoteException {
        this.roboDelMazo = roboDelMazo;
    }

    public void setRoboConCastigo(boolean roboConCastigo) throws RemoteException {
        this.roboConCastigo = roboConCastigo;
    }

    public ArrayList<Carta> getMano() throws RemoteException {
        return mano;
    }

    public int getManoSize() throws RemoteException {
        return mano.size();
    }

    public void moverCartaEnMano(int indCarta, int destino) throws RemoteException {
        Mano.moverCartaEnMano(mano, indCarta, destino);
    }

    public boolean acomodarCartaJuegoPropio(int numCarta, int numJuego, int ronda) throws RemoteException {
        return JuegoBajado.acomodarCartaJuegoPropio(juegos, mano, numCarta, numJuego, ronda);
    }

    public void setPuedeBajar(int puedeBajar) throws RemoteException {
        this.puedeBajar = puedeBajar;
    }

    public void incrementarPuedeBajar() throws RemoteException {
        puedeBajar++;
    }

    public boolean comprobarAcomodarCarta(int numCarta, Palo paloCarta, int numJuego, int ronda) throws RemoteException {
        boolean acomodo = false;
        ArrayList<Carta> juegoElegido = (ArrayList<Carta>) juegos.get(numJuego).clone();
        Carta c = new Carta(numCarta, paloCarta);
        juegoElegido.add(c);
        int tipoJuego = ifJuego.comprobarJuego(juegoElegido, ronda);
        if(tipoJuego != ifJuego.JUEGO_INVALIDO) {
            if (tipoJuego == ifJuego.TRIO) {
                if (ifJuego.comprobarAcomodarEnTrio(juegoElegido) == ifJuego.TRIO) {
                    acomodo = true;
                }
            } else {
                if (ifJuego.comprobarAcomodarEnEscalera(juegoElegido) == ifJuego.ESCALERA) {
                    acomodo = true;
                }
            }
        }
        return acomodo;
    }

    public ArrayList<Carta> seleccionarCartasABajar(int[] cartasABajar) throws RemoteException {
        return Mano.seleccionarCartasABajar(mano, cartasABajar);
    }

    public void bajarJuego(int[] cartasABajar, int tipoJuego) throws RemoteException {
        JuegoBajado.addJuego(juegos, seleccionarCartasABajar(cartasABajar), tipoJuego);
        eliminarDeLaMano(juegos.get(juegos.size() - 1));
        if (tipoJuego == ifJuego.TRIO) {
            incrementarTriosBajados();
        } else if (tipoJuego == ifJuego.ESCALERA) {
            incrementarEscalerasBajadas();
        }
    }

    public void eliminarDeLaMano(ArrayList<Carta> cartasABajar) throws RemoteException {
        for(Carta c : cartasABajar) {
            mano.remove(c);
        }
    }

    public void incrementarEscalerasBajadas() throws RemoteException {
        escalerasBajadas++;
    }

    public void incrementarTriosBajados() throws RemoteException {
        triosBajados++;
    }

    public int[] comprobarQueFaltaParaCortar(int ronda) throws RemoteException {
        int trios = 0;
        int escaleras = 0;
        int[] faltante = new int[2];
        switch (ronda) {
            case 1:
                trios = 2 - triosBajados;
                break;
            case 2:
                trios = 1 - triosBajados;
                escaleras = 1 - escalerasBajadas;
                break;
            case 3:
                escaleras = 2 - escalerasBajadas;
                break;
            case 4:
                trios = 3 - triosBajados;
                break;
            case 5:
                trios = 2 - triosBajados;
                escaleras = 1 - escalerasBajadas;
                break;
            case 6:
                trios = 1 - triosBajados;
                escaleras = 2 - escalerasBajadas;
                break;
            case 7:
                escaleras = 3 - escalerasBajadas;
                break;
        }
        faltante[0] = trios;
        faltante[1] = escaleras;
        return faltante;
    }

    public void setTriosBajados(int triosBajados) throws RemoteException {
        this.triosBajados = triosBajados;
    }

    public void setEscalerasBajadas(int escalerasBajadas) throws RemoteException {
        this.escalerasBajadas = escalerasBajadas;
    }

    public void setPuntosPartida(int puntosPartida) throws RemoteException {
        this.puntosPartida = puntosPartida;
    }

    public void setGanador(boolean ganador) throws RemoteException {
        this.ganador = ganador;
    }
}
