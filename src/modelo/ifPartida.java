package modelo;

import rmimvc.src.observer.IObservableRemoto;
import rmimvc.src.observer.IObservadorRemoto;
import serializacion.Serializador;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public interface ifPartida extends IObservableRemoto, Serializable {
    int CANT_CARTAS_INICIAL = 6;

    static int cartasPorRonda(int ronda) throws RemoteException {
        return CANT_CARTAS_INICIAL + ronda -1;
    }

    ArrayList<ArrayList<Carta>> getJuegos(int numJugador) throws RemoteException;
    boolean comprobarAcomodarCarta(int numJugador, int numCarta, int numJuego, int ronda)
            throws RemoteException;
    void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException;
    boolean isRoboDelMazo(int numJugador) throws RemoteException;
    void desarrolloPartida() throws RemoteException;
    boolean isTurnoActual(int numJugador) throws RemoteException;
    int getTriosBajados(int numJugador) throws RemoteException;
    int getEscalerasBajadas(int numJugador) throws RemoteException;
    int[] comprobarQueFaltaParaCortar(int numJugador) throws RemoteException;
    int getObservadorIndex(IObservadorRemoto o) throws RemoteException;

    void serializarGanador() throws RemoteException;

    void removerObservadores() throws RemoteException;

    Serializador getRanking() throws RemoteException;

    ArrayList<Jugador> getJugadores() throws RemoteException;

    int getTotalRondas() throws RemoteException;

    int getNumRonda() throws RemoteException;

    ArrayList<Carta> getPozo() throws RemoteException;

    Carta sacarPrimeraDelPozo() throws RemoteException;

    Jugador getJugador(String nombreJugador) throws RemoteException;

    boolean isRondaEmpezada() throws RemoteException;

    void setRondaEmpezada(boolean rondaEmpezada) throws RemoteException;

    void crearMazo() throws RemoteException;

    void repartirCartas() throws RemoteException;

    void iniciarPozo() throws RemoteException;

    int getNumJugadorQueEmpiezaRonda() throws RemoteException;

    void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException;

    int getNumTurno() throws RemoteException;
    boolean isPozoEmpty() throws RemoteException;

    void setNumTurno(int numTurno) throws RemoteException;

    int getNumJugadorRoboCastigo() throws RemoteException;
    void removeJugadorRoboCastigo() throws RemoteException;

    int getNumJugadorCorte() throws RemoteException;

    void setNumJugadorCorte(int numJugadorCorte) throws RemoteException;

    void finRonda(int numJugador) throws RemoteException;
    void finTurno() throws RemoteException;

    int[] getPuntosJugadores() throws RemoteException;

    Jugador determinarGanador() throws RemoteException;

    Jugador getGanador() throws RemoteException;

    void incTurno() throws RemoteException;

    void crearYAgregarJugador(String nombre, int numObservador, UUID idJugador) throws RemoteException;
    int comprobarBajarse(int numJugador, int[] cartasABajar) throws RemoteException;
    void finPartida() throws RemoteException;
    boolean cortar(int numJugador) throws RemoteException;
    void crearPartida(String vista, int observadorIndex, UUID idJugador,
                      int cantJugadoresDeseada) throws RemoteException;
    ifCarta getCarta(int numJugador, int iCarta) throws RemoteException;
    void acomodarEnJuegoAjeno(int numJugador, int iCarta, int numJuego) throws RemoteException;
    ArrayList<ifCarta> getMano(UUID idSolicitante, int idObjetivo) throws RemoteException;
    void setEnCurso() throws RemoteException;
    boolean puedeRobarConCastigo() throws RemoteException;
    void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida)
            throws RemoteException;

    int getNumJugadorQueEmpezoPartida() throws RemoteException;

    int getCantJugadoresDeseada() throws RemoteException;

    void ponerJugadoresEnOrden() throws RemoteException;

    void setCantJugadoresDeseada(int cantJugadoresDeseada) throws RemoteException;
    int getCantJugadores() throws RemoteException;
    Carta getPrimeraDelPozo() throws RemoteException;
    boolean isEnCurso() throws RemoteException;
    void robarDelMazo(int numJugador) throws RemoteException;

    void setRoboDelMazo(int i, boolean b) throws RemoteException;

    void robarDelPozo(int numJugador) throws RemoteException;

    void robarConCastigo() throws RemoteException;
    void empezarRonda() throws RemoteException;
    void roboCastigo() throws RemoteException;

    void setTurnoJugador(int numJugador, boolean valor) throws RemoteException;

    void tirarAlPozo(int numJugador, int cartaATirar) throws RemoteException;

    void moverCartaEnMano(int numJugador, int i, int i1) throws RemoteException;

    void bajarJuego(int numJugador, int[] cartasABajar, int tipoJuego) throws RemoteException;

    void setPuedeBajar(int numJugador, int i) throws RemoteException;
    void incPuedeBajar(int numJugador) throws RemoteException;

    String getNombreJugador(int numJugador) throws RemoteException;

    int getPuedeBajar(int numJugadorRoboCastigo) throws RemoteException;
}
