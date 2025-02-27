package modelo;

import excepciones.FaltanJugadoresException;
import rmimvc.src.observer.IObservableRemoto;
import rmimvc.src.observer.IObservadorRemoto;
import serializacion.Serializador;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface ifPartida extends IObservableRemoto {
    ArrayList<ArrayList<Carta>> getJuegos(int numJugador) throws RemoteException;
    void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException;
    void finTurno() throws RemoteException;
    boolean isTurnoActual(int numJugador) throws RemoteException;
    int[] comprobarQueFaltaParaCortar(int numJugador) throws RemoteException;
    int getObservadorIndex(IObservadorRemoto o) throws RemoteException;
    Serializador getRanking() throws RemoteException;
    int getCantJugadores() throws RemoteException;
    int getNumRonda() throws RemoteException;
    ifCarta getPozo() throws RemoteException;
    int getNumeroJugador(String nombreJugador) throws RemoteException;
    int getNumTurno() throws RemoteException;
    int getNumJugadorRoboCastigo() throws RemoteException;
    Map<String, Integer> getPuntosJugadores() throws RemoteException;
    void actualizarMano(int numJugador) throws RemoteException;
    String getGanador() throws RemoteException;
    void jugarPartida() throws RemoteException, FaltanJugadoresException;
    void crearJugador(String nombre, int numObservador)
            throws RemoteException;
    void inicializarPartida(int observadorIndex)
            throws RemoteException;
    int getCantJugadoresDeseada() throws RemoteException;
    boolean isEnCurso() throws RemoteException;
    void robarDelMazo() throws RemoteException;
    void robarDelPozo() throws RemoteException;
    void robarConCastigo() throws RemoteException;
    void empezarRonda() throws RemoteException;
    void tirarAlPozo(int cartaATirar) throws RemoteException;
    void moverCartaEnMano(int i, int i1) throws RemoteException;
    String getNombreJugador(int numJugador) throws RemoteException;
    void guardar() throws RemoteException;
    int getNumJugador(String nombreJugador) throws RemoteException;
    ArrayList<Integer> getJugadoresQuePuedenRobarConCastigo() throws RemoteException;
    boolean cargarPartida() throws RemoteException;
    ArrayList<String> getNombreJugadores() throws RemoteException;
    boolean agregarNombreElegido(String nombre) throws RemoteException;
    int getCantJuegos(int numJugador) throws RemoteException;
    void setJugadoresQuePuedenRobarConCastigo() throws RemoteException;
    void notificarSalir() throws RemoteException;
    void setEjecutarFinTurno(boolean ejecutarFinTurno)
            throws RemoteException;
    void incNumJugadorRoboCastigo() throws RemoteException;
    void comprobarEmpezarPartida() throws RemoteException;
    void desarrolloRobo(String eleccion)
            throws RemoteException;
    boolean hayRepetidos(int[] array) throws RemoteException;
    boolean acomodar(int cartaAcomodar, int iJuego, int numJugador) throws RemoteException;
    boolean bajarse(int[] indicesCartas) throws RemoteException;
    void setCantJugadoresDeseada(int cant) throws RemoteException;
    ArrayList<ArrayList<ArrayList<Carta>>> enviarJuegosEnMesa()
            throws RemoteException;

    String getTurnoDe() throws RemoteException;
}