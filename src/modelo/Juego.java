package modelo;

import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;
import static modelo.Eventos.*;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Juego extends ObservableRemoto implements ifJuego{
    private Partida partidaActual;
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    protected static final int CANT_CARTAS_INICIAL = 6;
    private static final String NOMBRE_ARCHIVO_RANKING = "ranking.dat";
    private final Serializador srlRanking = new Serializador(NOMBRE_ARCHIVO_RANKING);

    //singleton
    private static Juego instancia;
    private Juego() {}

    public static Juego getInstancia() throws RemoteException {
        if (instancia == null) instancia = new Juego();
        return instancia;
    }


    public Partida getPartidaActual() throws RemoteException {
        return partidaActual;
    }

    public int getObservadorIndex(IObservadorRemoto o) throws RemoteException {
        return getObservadores().indexOf(o);
    }

    public void serializarGanador() throws RemoteException {
        Object guardar = partidaActual.getGanador().nombre + " --- puntos: " + partidaActual.getGanador().getPuntosAlFinalizar();
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

    public void crearPartida(String nombreVista, int cantJugadoresDeseada, int numJugador) throws RemoteException{
        partidaActual = new Partida(cantJugadoresDeseada); //creacion de partida
        partidaActual.agregarJugador(nombreVista);
        partidaActual.setEnCurso();
        partidaActual.setNumJugadorQueEmpezoPartida(numJugador);
        notificarObservador(numJugador, NOTIFICACION_NUEVA_PARTIDA_PROPIO);
        notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
    }

    public void agregarJugador(String nombreJugador) throws RemoteException {
        jugadores.add(new Jugador(nombreJugador));
        notificarObservadores(NOTIFICACION_NUEVO_JUGADOR);
    }

    public Serializador getRanking() throws RemoteException {
        return srlRanking;
    }
}
