package modelo;

import java.util.ArrayList;
import java.util.Map;

import static modelo.Partida.*;

public class Puntuacion {
    Map<Jugador, Integer> puntuacion;
    Jugador ganador = null;
    private static Puntuacion instancia;
    private Puntuacion() {}

    public static Puntuacion getInstancia() {
        if (instancia== null) instancia = new Puntuacion();
        return instancia;
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
        for (Carta c : m) {
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
    }
}
