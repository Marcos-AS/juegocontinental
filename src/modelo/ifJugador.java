package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ifJugador {
    String getNombre() throws RemoteException;
    void setNumeroJugador(int numeroJugador) throws RemoteException;
    void setRoboDelMazo(boolean roboDelMazo) throws RemoteException;
    void moverCartaEnMano(int indCarta, int destino) throws RemoteException;
    ArrayList<Carta> seleccionarCartasABajar(int[] cartasABajar) throws RemoteException;
    void bajarJuego(int[] cartasABajar, int tipoJuego) throws RemoteException;
    void setPuedeBajar(int puedeBajar) throws RemoteException;
    int[] comprobarQueFaltaParaCortar(int ronda) throws RemoteException;
    void setTurnoActual(boolean turnoActual) throws RemoteException;
    void incrementarPuedeBajar() throws RemoteException;
    boolean isRoboDelMazo();
    boolean isTurnoActual();
    int getPuedeBajar();
    ArrayList<ArrayList<Carta>> getJuegos();
    int getTriosBajados();
    int getEscalerasBajadas();
    int getNumeroJugador();
}
