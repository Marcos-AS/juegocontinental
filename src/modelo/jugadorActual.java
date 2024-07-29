package modelo;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class jugadorActual extends Jugador implements Serializable, ifJugador {
    private boolean turnoActual = false;
    protected int numeroJugador;
    private ArrayList<Carta> mano = new ArrayList<>();
    private boolean roboDelMazo = false;
    private boolean roboConCastigo = false;
    private ArrayList<ArrayList<Carta>> juegos = new ArrayList<>();
    private int puedeBajar = 0;
    private int triosBajados;
    private int escalerasBajadas;
    private int puntosPartida = 0;
    private boolean ganador = false;

    public jugadorActual(String nombre) throws RemoteException {
        super(nombre);
    }

    public boolean isTurnoActual() throws RemoteException {
        return turnoActual;
    }

    public void setTurnoActual(boolean turnoActual) throws RemoteException {
        this.turnoActual = turnoActual;
    }

    public int getNumeroJugador() throws RemoteException {
        return numeroJugador;
    }

    public void setNumeroJugador(int numeroJugador) throws RemoteException {
        this.numeroJugador = numeroJugador;
    }

    public void agregarCarta(Carta c) throws RemoteException {
        mano.add(c);
    }

    public boolean isRoboDelMazo() throws RemoteException {
        return roboDelMazo;
    }

    public void setRoboDelMazo(boolean roboDelMazo) throws RemoteException {
        this.roboDelMazo = roboDelMazo;
    }

    public boolean isRoboConCastigo() throws RemoteException {
        return roboConCastigo;
    }

    public void setRoboConCastigo(boolean roboConCastigo) throws RemoteException {
        this.roboConCastigo = roboConCastigo;
    }

    public ArrayList<Carta> getMano() throws RemoteException {
        return mano;
    }
    public void moverCartaEnMano(int indCarta, int destino) throws RemoteException {
        Carta c = mano.get(indCarta);
        mano.remove(indCarta);
        mano.add(destino, c);
    }

    public boolean acomodarCartaJuegoPropio(int numCarta, int numJuego, int ronda) throws RemoteException {
        boolean acomodo = false;
        ArrayList<Carta> juegoElegido = (ArrayList<Carta>) juegos.get(numJuego).clone();
        juegoElegido.add(mano.get(numCarta));
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
            if(acomodo) juegos.get(numJuego).add(removeCartaFromMano(numCarta)); //hace el acomodo
        }
        return acomodo;
    }

    public Carta removeCartaFromMano(int indiceCarta) throws RemoteException {
        Carta cartaATirar = mano.get(indiceCarta);
        mano.remove(indiceCarta);
        return cartaATirar;
    }

    public int getPuedeBajar() throws RemoteException {
        return puedeBajar;
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
        ArrayList<Carta> juego = new ArrayList<>();
        for (int carta : cartasABajar) juego.add(mano.get(carta));
        return juego;
    }

    public void bajarJuego(int[] cartasABajar, int tipoJuego) throws RemoteException {
        addJuego(cartasABajar, tipoJuego);
        eliminarDeLaMano(juegos.get(juegos.size() - 1));
        if (tipoJuego == ifJuego.TRIO) {
            incrementarTriosBajados();
        } else if (tipoJuego == ifJuego.ESCALERA) {
            incrementarEscalerasBajadas();
        }
    }

    public void addJuego(int[] juego, int tipoJuego) throws RemoteException {
        if (tipoJuego == ifJuego.ESCALERA) {
            juegos.add(ifJuego.ordenarJuego(seleccionarCartasABajar(juego)));
        } else {
            juegos.add(seleccionarCartasABajar(juego));
        }
    }

    public void eliminarDeLaMano(ArrayList<Carta> cartasABajar) throws RemoteException {
        for(Carta c : cartasABajar) {
            mano.remove(c);
        }
    }

    public ArrayList<ArrayList<Carta>> getJuegos() throws RemoteException {
        return juegos;
    }

    public void incrementarEscalerasBajadas() throws RemoteException {
        escalerasBajadas++;
    }

    public void incrementarTriosBajados() throws RemoteException {
        triosBajados++;
    }

    public int getTriosBajados() throws RemoteException {
        return triosBajados;
    }

    public int getEscalerasBajadas() throws RemoteException {
        return escalerasBajadas;
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

    public int getPuntosPartida() throws RemoteException {
        return puntosPartida;
    }

    public void setPuntosPartida(int puntosPartida) throws RemoteException {
        this.puntosPartida = puntosPartida;
    }

    public boolean isGanador() throws RemoteException {
        return ganador;
    }

    public void setGanador(boolean ganador) throws RemoteException {
        this.ganador = ganador;
    }
}
