package modelo;

import java.util.ArrayList;

public class PartidaJugadores {

    private static final ArrayList<String> nombresElegidos = new ArrayList<>();

    public static ArrayList<String> getNombresElegidos() {
        return nombresElegidos;
    }

    static ArrayList<String> getNombreJugadores(ArrayList<Jugador> jugadores) {
        ArrayList<String> nombreJugadores = new ArrayList<>();
        for (Jugador j : jugadores) {
            nombreJugadores.add(j.getNombre());
        }
        return nombreJugadores;
    }

    static boolean agregarNombreElegido(String nombre) {
        if (!nombresElegidos.contains(nombre)) {
            nombresElegidos.add(nombre);
            return true;
        }
        return false;
    }

}