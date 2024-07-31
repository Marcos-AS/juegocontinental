package modelo;

import rmimvc.src.observer.ObservableRemoto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Jugador extends ObservableRemoto implements Serializable {
    protected String nombre;
    private int puntosAlFinalizar;
    private ArrayList<Partida> partidas = new ArrayList<>();

    public Jugador(String nombre) throws RemoteException {
        this.nombre = nombre;
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
}
