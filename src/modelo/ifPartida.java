package modelo;

import rmimvc.src.observer.IObservableRemoto;
import rmimvc.src.observer.IObservadorRemoto;
import serializacion.Serializador;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ifPartida extends IObservableRemoto, Serializable {
    ArrayList<ArrayList<ifCarta>> getJuegos(int numJugador) throws RemoteException;
    boolean comprobarAcomodarAjeno(int numJugador, int numJugadorAcomodar,
                        int numCarta, int numJuego) throws RemoteException;
    boolean comprobarAcomodarCartaPropio(int numJugador, int numCarta, int numJuego)
            throws RemoteException;
    void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException;
    void finTurno() throws RemoteException;
    boolean isTurnoActual(int numJugador) throws RemoteException;
    int[] comprobarQueFaltaParaCortar(int numJugador) throws RemoteException;
    int getObservadorIndex(IObservadorRemoto o) throws RemoteException;
    Serializador getRanking() throws RemoteException;
    ArrayList<Jugador> getJugadores() throws RemoteException;
    int getNumRonda() throws RemoteException;
    Carta getPozo() throws RemoteException;
    Jugador getJugador(String nombreJugador) throws RemoteException;
    int getNumTurno() throws RemoteException;
    int getNumJugadorRoboCastigo() throws RemoteException;
    int getNumJugadorCorte() throws RemoteException;
    int[] getPuntosJugadores() throws RemoteException;
    void actualizarMano(int numJugador) throws RemoteException;
    Jugador getGanador() throws RemoteException;
    void crearYAgregarJugador(String nombre, int numObservador)
            throws RemoteException;
    boolean comprobarBajarse(int numJugador, int[] cartasABajar) throws RemoteException;
    boolean cortar(int numJugador) throws RemoteException;
    void crearPartida(int observadorIndex,int cantJugadoresDeseada)
            throws RemoteException;
    void acomodarAjeno(int numJugador,int numJugadorAcomodar, int iCarta, int numJuego)
            throws RemoteException;
    int getCantJugadoresDeseada() throws RemoteException;
    void ponerJugadoresEnOrden() throws RemoteException;
    boolean isEnCurso() throws RemoteException;
    void robarDelMazo() throws RemoteException;
    void robarDelPozo() throws RemoteException;
    void robarConCastigo() throws RemoteException;
    void empezarRonda() throws RemoteException;
    void setTurnoJugador(int numJugador, boolean valor) throws RemoteException;
    void tirarAlPozo(int numJugador, int cartaATirar) throws RemoteException;
    void moverCartaEnMano(int numJugador, int i, int i1) throws RemoteException;
    String getNombreJugador(int numJugador) throws RemoteException;
    int getPuedeBajar(int numJugadorRoboCastigo) throws RemoteException;
    void incPuedeBajar(int numJugador) throws RemoteException;
    void guardarPartida() throws RemoteException;
    int getNumJugador(String nombreJugador) throws RemoteException;
    void acomodarPropio(int numJugador,
                               int iCarta, int numJuego) throws RemoteException;
    ArrayList<Integer> getJugadoresQuePuedenRobarConCastigo() throws RemoteException;
    Eventos comprobarPosibleCorte(int numJugador) throws RemoteException;
    boolean cargarPartida() throws RemoteException;
    ArrayList<String> getNombreJugadores() throws RemoteException;
    boolean agregarNombreElegido(String nombre) throws RemoteException;
    void setJugadoresQuePuedenRobarConCastigo() throws RemoteException;
}