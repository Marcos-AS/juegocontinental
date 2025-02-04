package modelo;

import java.rmi.RemoteException;

public interface ifJugador {
    String getNombre() throws RemoteException;
    int getNumeroJugador();
}
