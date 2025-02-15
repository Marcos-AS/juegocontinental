package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Puntuacion {
    Map<Jugador, Integer> puntuacion;
    Jugador ganador = null;

    public Puntuacion(ArrayList<Jugador> jugadores) {
        puntuacion = new HashMap<>();
        for (Jugador j : jugadores) {
            puntuacion.put(j,0);
        }
    }

    public Jugador getGanador() {
        return ganador;
    }

    int getPuntosGanador() {
        return puntuacion.get(ganador);
    }

    void determinarGanador() {
        ganador = puntuacion.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    int[] getPuntosJugadores() {
        return puntuacion.values().stream().mapToInt(Integer::intValue).toArray();
    }

    void sumarPuntos(Jugador j) {
        final int PUNTOS_FIGURA = 10;
        final int PUNTOS_AS = 20;
        final int PUNTOS_COMODIN = 50;
        int puntosPartida = 0;
        for (Carta c : j.getMano().get()) {
            int num = c.getNumero();
            switch (num) {
                case 1:
                    puntosPartida += PUNTOS_AS;
                    break;
                case 11, 12, 13:
                    puntosPartida += PUNTOS_FIGURA;
                    break;
                case -1:
                    puntosPartida += PUNTOS_COMODIN;
                    break;
                case 2, 3, 4, 5, 6, 7, 8, 9, 10:
                    puntosPartida += num;
            }
        }
        puntuacion.put(j, puntuacion.getOrDefault(j,0)+puntosPartida);
    }
}
