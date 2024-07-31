package modelo;

import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;
import static modelo.Eventos.*;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Juego extends ObservableRemoto implements ifJuego{
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private static final String NOMBRE_ARCHIVO_RANKING = "ranking.dat";
    private final Serializador srlRanking = new Serializador(NOMBRE_ARCHIVO_RANKING);
    //singleton
    private static Juego instancia;
    private Juego() {}

    public static Juego getInstancia() throws RemoteException {
        if (instancia == null) {
            instancia = new Juego();
        }
        return instancia;
    }

    public int getObservadorIndex(IObservadorRemoto o) throws RemoteException {
        return getObservadores().indexOf(o);
    }

    public void serializarGanador(ifPartida p) throws RemoteException {
        Object guardar = p.getGanador().nombre + " --- puntos: " + p.getGanador().getPuntosAlFinalizar();
        if (srlRanking.readFirstObject()==null) {
            srlRanking.writeOneObject(guardar);
        } else {
            Object[] jugadores = srlRanking.readObjects();
            ArrayList<String> listaJugadores = new ArrayList<>();

            for (Object jugador : jugadores) {
                listaJugadores.add(jugador.toString());
            }
            listaJugadores.add(guardar.toString());

            listaJugadores.sort((j1, j2) -> {
                int puntos1 = Integer.parseInt(j1.split(" --- puntos: ")[1]);
                int puntos2 = Integer.parseInt(j2.split(" --- puntos: ")[1]);
                return Integer.compare(puntos2, puntos1); // Orden descendente
            });

            int i = 0;
            srlRanking.writeOneObject(listaJugadores.get(i));
            for (i = 1; i < listaJugadores.size(); i++) {
                srlRanking.addOneObject(listaJugadores.get(i)); //revisar tema cabecera
            }
        }
    }

    public void removerObservadores() throws RemoteException {
        int cantObservadores = getObservadores().size();
        for (int i = cantObservadores-1; i >= 0; i--) {
            removerObservador(getObservadores().get(i));
        }
    }


    public void agregarJugador(String nombreJugador) throws RemoteException {
        jugadores.add(new Jugador(nombreJugador));
        notificarObservadores(NOTIFICACION_NUEVO_JUGADOR);
    }

    public Serializador getRanking() throws RemoteException {
        return srlRanking;
    }

    public ArrayList<Jugador> getJugadores() throws RemoteException {
        return jugadores;
    }
}
