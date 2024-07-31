package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ifPartida {
    int BARAJAS_HASTA_4_JUGADORES = 2;
    int BARAJAS_MAS_4_JUGADORES = 3;

    int getTotalRondas() throws RemoteException;

    int getNumRonda() throws RemoteException;

    ArrayList<Carta> getPozo() throws RemoteException;

    Carta sacarPrimeraDelPozo() throws RemoteException;

    ArrayList<jugadorActual> getJugadoresActuales() throws RemoteException;

    jugadorActual getJugador(String nombreJugador) throws RemoteException;

    boolean isRondaEmpezada() throws RemoteException;

    void setRondaEmpezada(boolean rondaEmpezada) throws RemoteException;

    void crearMazo() throws RemoteException;

    void iniciarMazo(int numBarajas) throws RemoteException;

    void mezclarCartas() throws RemoteException;

    void repartirCartas() throws RemoteException;

    void iniciarPozo() throws RemoteException;

    Carta sacarPrimeraDelMazo() throws RemoteException;

    int getNumJugadorQueEmpiezaRonda() throws RemoteException;

    void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException;

    int getNumTurno() throws RemoteException;

    void setNumTurno(int numTurno) throws RemoteException;

    int getNumJugadorRoboCastigo() throws RemoteException;

    void setNumJugadorRoboCastigo(int numJugadorRoboCastigo) throws RemoteException;

    void resetearRoboConCastigo() throws RemoteException;

    void agregarAlPozo(Carta c) throws RemoteException;

    boolean isCorteRonda() throws RemoteException;

    void setCorteRonda() throws RemoteException;

    int getNumJugadorCorte() throws RemoteException;

    void setNumJugadorCorte(int numJugadorCorte) throws RemoteException;

    void finRonda() throws RemoteException;

    void sumarPuntos() throws RemoteException;

    int[] getPuntosJugadores() throws RemoteException;

    jugadorActual determinarGanador() throws RemoteException;

    jugadorActual getGanador() throws RemoteException;

    void finTurno() throws RemoteException;

    void agregarJugador(String nombre) throws RemoteException;

    void setEnCurso() throws RemoteException;

    void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida) throws RemoteException;

    int getNumJugadorQueEmpezoPartida() throws RemoteException;

    int getCantJugadoresDeseada() throws RemoteException;

    void ponerJugadoresEnOrden() throws RemoteException;

    void setCantJugadoresDeseada(int cantJugadoresDeseada);
}
