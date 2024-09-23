package modelo;

import rmimvc.src.observer.ObservableRemoto;
import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Partida extends ObservableRemoto implements Serializable {
    private static final int BARAJAS_HASTA_4_JUGADORES = 2;
    private static final int BARAJAS_MAS_4_JUGADORES = 3;
    private int numRonda;
    private ArrayList<Carta> pozo;
    private ArrayList<Carta> mazo;
    private ArrayList<jugadorActual> jugadoresActuales = new ArrayList<>();
    private boolean rondaEmpezada = false;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private int numJugadorRoboCastigo;
    private boolean corteRonda = false;
    private int numJugadorCorte;
    private static final int TOTAL_RONDAS = 7;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private int numJugadorQueEmpezoPartida;
    @Serial
    private static final long serialVersionUID = 1L;
    private static Partida partidaActual;

    private Partida() {}

    public static Partida getInstanciaPartida() throws RemoteException {
        if (partidaActual == null) {
            partidaActual = new Partida();
            //System.out.println("new partida " + partidaActual);
        }
        return partidaActual;
    }


    public int getTotalRondas() throws RemoteException {
        return TOTAL_RONDAS;
    }


    public int getNumRonda() throws RemoteException {
        return numRonda;
    }


    public ArrayList<Carta> getPozo() throws RemoteException {
        return pozo;
    }


    public Carta sacarPrimeraDelPozo() throws RemoteException {
        return pozo.remove(pozo.size()-1);
    }


    public ArrayList<jugadorActual> getJugadoresActuales() throws RemoteException {
        return jugadoresActuales;
    }


    public jugadorActual getJugador(String nombreJugador)  throws RemoteException{
        return PartidaJugadores.getJugador(jugadoresActuales, nombreJugador);
    }


    public boolean isRondaEmpezada() throws RemoteException {
        return rondaEmpezada;
    }


    public void setRondaEmpezada(boolean rondaEmpezada) throws RemoteException {
        this.rondaEmpezada = rondaEmpezada;
    }


    public void crearMazo() throws RemoteException {
        mazo = Mazo.mezclarCartas(Mazo.iniciarMazo(determinarNumBarajas()));
    }


    public void repartirCartas() throws RemoteException {
        PartidaJugadores.repartirCartas(jugadoresActuales, numRonda, mazo);
    }


    public void iniciarPozo()  throws RemoteException{
        pozo = new ArrayList<>();
        pozo.add(sacarPrimeraDelMazo());
        mazo.remove(mazo.size()-1);
    }

    public Carta sacarPrimeraDelMazo() throws RemoteException {
        return mazo.remove(mazo.size()-1);
    }

    public int getNumJugadorQueEmpiezaRonda() throws RemoteException {
        return numJugadorQueEmpiezaRonda;
    }

    public void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException {
        numJugadorQueEmpiezaRonda++;
    }

    public int getNumTurno() throws RemoteException {
        return numTurno;
    }

    public void setNumTurno(int numTurno) throws RemoteException {
        this.numTurno = numTurno;
    }

    public int getNumJugadorRoboCastigo()  throws RemoteException {
        return numJugadorRoboCastigo;
    }

    public void setNumJugadorRoboCastigo(int numJugadorRoboCastigo)  throws RemoteException {
        this.numJugadorRoboCastigo = numJugadorRoboCastigo;
    }

    public void resetearRoboConCastigo() throws RemoteException {
        PartidaJugadores.resetearRoboConCastigo(jugadoresActuales);
    }

    public void agregarAlPozo(Carta c)  throws RemoteException {
        pozo.add(c);
    }


    public boolean isCorteRonda()  throws RemoteException {
        return corteRonda;
    }


    public void setCorteRonda()  throws RemoteException {
        corteRonda = !corteRonda;
    }


    public int getNumJugadorCorte()  throws RemoteException {
        return numJugadorCorte;
    }


    public void setNumJugadorCorte(int numJugadorCorte)  throws RemoteException {
        this.numJugadorCorte = numJugadorCorte;
    }


    public void finRonda() throws RemoteException {
        numRonda++;
        PartidaJugadores.resetearJuegosJugadores(jugadoresActuales);
        PartidaJugadores.sumarPuntos(jugadoresActuales);
        setCorteRonda();
    }

    public int[] getPuntosJugadores() throws RemoteException {
        return PartidaJugadores.getPuntosJugadores(jugadoresActuales);
    }

    public jugadorActual determinarGanador() throws RemoteException {
        return PartidaJugadores.determinarGanador(jugadoresActuales);
    }

    public jugadorActual getGanador()  throws RemoteException{
        return PartidaJugadores.getGanador(jugadoresActuales);
    }

    public void finTurno() throws RemoteException{
        numTurno++;
        if (numTurno>jugadoresActuales.size()-1) {
            numTurno = 0;
        }
    }

    public void agregarJugador(String nombre) throws RemoteException {
        PartidaJugadores.agregarJugador(jugadoresActuales, nombre);
        jugadoresActuales.get(jugadoresActuales.size()-1).sumarPartida(this);
    }

    public void setEnCurso() throws RemoteException {
        enCurso = !enCurso;
    }

    public void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida) throws RemoteException {
        this.numJugadorQueEmpezoPartida = numJugadorQueEmpezoPartida;
    }

    public int getNumJugadorQueEmpezoPartida() throws RemoteException {
        return numJugadorQueEmpezoPartida;
    }

    public int getCantJugadoresDeseada() throws RemoteException {
        return cantJugadoresDeseada;
    }

    public void ponerJugadoresEnOrden() throws RemoteException {
        jugadoresActuales = PartidaJugadores.ponerJugadoresEnOrden(jugadoresActuales);
    }

    public void setCantJugadoresDeseada(int cantJugadoresDeseada) {
        this.cantJugadoresDeseada = cantJugadoresDeseada;
    }

    private int determinarNumBarajas() throws RemoteException {
        int cantBarajas = BARAJAS_HASTA_4_JUGADORES;
        if (jugadoresActuales.size() >= 4 && jugadoresActuales.size() <= 6) {
            cantBarajas = BARAJAS_MAS_4_JUGADORES;
            //} else if(this.jugadoresActuales.size() >= 6 && this.jugadoresActuales.size() <= 8) {
            //  cantBarajas = BARAJAS_MAS_6_JUGADORES;
        }
        return cantBarajas;
    }
}