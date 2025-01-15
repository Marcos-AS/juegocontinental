package modelo;

import rmimvc.src.observer.ObservableRemoto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Jugador extends ObservableRemoto implements Serializable, ifJugador {
    protected String nombre;
    private int puntosAlFinalizar;
    private final ArrayList<Partida> partidas = new ArrayList<>();
    public boolean turnoActual = false;
    public int numeroJugador;
    private ArrayList<Carta> mano = new ArrayList<>();
    public boolean roboDelMazo = false;
    public ArrayList<ArrayList<Carta>> juegos = new ArrayList<>();
    public int puedeBajar = 0;
    public int triosBajados;
    public int escalerasBajadas;
    public int puntosPartida = 0;
    public boolean ganador = false;

    public Jugador(String nombre) throws RemoteException {
        this.nombre = nombre;
    }

    protected void resetMano() {
        mano = new ArrayList<>();
    }

    public void setTurnoActual(boolean turnoActual) throws RemoteException {
        this.turnoActual = turnoActual;
    }

    public boolean isTurnoActual() {
        return turnoActual;
    }

    public void setNumeroJugador(int numeroJugador) throws RemoteException {
        this.numeroJugador = numeroJugador;
    }

    public int getNumeroJugador() {
        return numeroJugador;
    }

    public void agregarCarta(Carta c) throws RemoteException {
        mano.add(c);
    }

    public void setRoboDelMazo(boolean roboDelMazo) throws RemoteException {
        this.roboDelMazo = roboDelMazo;
    }

    public boolean isRoboDelMazo() {
        return roboDelMazo;
    }

    public int getPuedeBajar() {
        return puedeBajar;
    }

    protected ArrayList<Carta> getMano() throws RemoteException {
        return mano;
    }

    public ArrayList<ArrayList<Carta>> getJuegos() {
        return juegos;
    }

    public int getTriosBajados() {
        return triosBajados;
    }

    public int getEscalerasBajadas() {
        return escalerasBajadas;
    }

    public int getPuntosPartida() {
        return puntosPartida;
    }

    public boolean isGanador() {
        return ganador;
    }

    public void moverCartaEnMano(int indCarta, int destino) throws RemoteException {
        Mano.moverCartaEnMano(mano, indCarta, destino);
    }

    public void setPuedeBajar(int puedeBajar) throws RemoteException {
        this.puedeBajar = puedeBajar;
    }

    public void incrementarPuedeBajar() throws RemoteException {
        puedeBajar++;
    }

    public ArrayList<Carta> seleccionarCartasABajar(int[] cartasABajar) throws RemoteException {
        return Mano.seleccionarCartasABajar(mano, cartasABajar);
    }

    public void bajarJuego(int[] cartasABajar, int tipoJuego) throws RemoteException {
        JuegoBajado.addJuego(juegos, seleccionarCartasABajar(cartasABajar), tipoJuego);
        eliminarDeLaMano(juegos.get(juegos.size() - 1));
        if (tipoJuego == Comprobar.TRIO) {
            incrementarTriosBajados();
        } else if (tipoJuego == Comprobar.ESCALERA) {
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

    public String getNombre() throws RemoteException {
        return nombre;
    }

    public int getPuntosAlFinalizar() throws RemoteException {
        return puntosAlFinalizar;
    }

    public void setPuntosAlFinalizar(int puntosAlFinalizar) throws RemoteException {
        this.puntosAlFinalizar = puntosAlFinalizar;
    }

    public void sumarPartida(Partida p) throws RemoteException {
        partidas.add(p);
    }

    public void resetJuegos() {
        juegos = new ArrayList<>();
    }
}
