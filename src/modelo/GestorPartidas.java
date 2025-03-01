package modelo;

import serializacion.Serializador;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class GestorPartidas {
    private ArrayList<String> nombresElegidos;
    private final Serializador srlRanking =
            new Serializador("src/serializacion/ranking.dat");
    private final Serializador srlPartidas =
            new Serializador("src/serializacion/partidas.dat");
    private static GestorPartidas instancia;

    private GestorPartidas(){}

    static GestorPartidas getInstancia() {
        if(instancia==null) instancia = new GestorPartidas();
        return instancia;
    }

    void serializarGanador(Puntuacion puntuacion) throws RemoteException {
        Object guardar = puntuacion.getGanador().nombre + " --- puntos: " +
                puntuacion.getPuntosGanador();
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

    void guardar() {
        srlPartidas.writeOneObject(this);
    }

    Object cargarPartida() {
        return srlPartidas.readFirstObject();
    }

    int getNombresElegidosSize() {
        return nombresElegidos.size();
    }

    boolean agregarNombreElegido(String nombre) {
        boolean agregar = !nombresElegidos.contains(nombre);
        if (agregar) nombresElegidos.add(nombre);
        return agregar;
    }

    Object[] getRanking() {
        return srlRanking.readObjects();
    }
}