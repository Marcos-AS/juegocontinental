package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ifJugador {
    String getNombre();

    int getNumeroJugador();
    void setNumeroJugador(int numeroJugador);
    boolean isRoboDelMazo();
    void setRoboDelMazo(boolean roboDelMazo);

    ArrayList<Carta> getMano();
    boolean isTurnoActual();
    void moverCartaEnMano(int indCarta, int destino);

    ArrayList<ArrayList<Carta>> getJuegos();
    boolean acomodarCartaJuegoPropio(int numCarta, int numJuego, int ronda) throws RemoteException;
    int getPuedeBajar();
    boolean comprobarAcomodarCarta(int numCarta, Palo paloCarta, int numJuego, int ronda) throws RemoteException;
    ArrayList<Carta> seleccionarCartasABajar(int[] cartasABajar);
    void bajarJuego(int[] cartasABajar, int tipoJuego) throws RemoteException;
    int getTriosBajados();
    int getEscalerasBajadas();
    void setPuedeBajar(int puedeBajar);
    int[] comprobarQueFaltaParaCortar(int ronda);
    void setTurnoActual(boolean turnoActual);
    void incrementarPuedeBajar();
    boolean isRoboConCastigo();
    void setRoboConCastigo(boolean roboConCastigo);
}
