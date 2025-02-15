package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Jugador implements Serializable, ifJugador {
    String nombre;
    private int numeroJugador;
    private final ArrayList<Partida> partidas = new ArrayList<>();
    private boolean turnoActual = false;
    private Mano mano = new Mano();
    ArrayList<JuegoBajado> juegos = new ArrayList<>();

    Jugador(String nombre) {
        this.nombre = nombre;
    }

    private class Conteo {
        int trios;
        int escaleras;
    }

    int[] comprobarQueFaltaParaCortar(int ronda) {
        Conteo conteo = new Conteo();
        getTriosYEscalerasBajadas(conteo);
        int trios = 0;
        int escaleras = 0;
        int[] faltante = new int[2];
        switch (ronda) {
            case 1:
                trios = 2 - conteo.trios;
                break;
            case 2:
                trios = 1 - conteo.trios;
                escaleras = 1 - conteo.escaleras;
                break;
            case 3:
                escaleras = 2 - conteo.escaleras;
                break;
            case 4:
                trios = 3 - conteo.trios;
                break;
            case 5:
                trios = 2 - conteo.trios;
                escaleras = 1 - conteo.escaleras;
                break;
            case 6:
                trios = 1 - conteo.trios;
                escaleras = 2 - conteo.escaleras;
                break;
            case 7:
                escaleras = 3 - conteo.escaleras;
                break;
        }
        faltante[0] = trios;
        faltante[1] = escaleras;
        return faltante;
    }

    private void getTriosYEscalerasBajadas(Conteo conteo) {
        for (JuegoBajado j : juegos) {
            if (j.tipo==TipoJuego.TRIO) conteo.trios++;
            else if (j.tipo==TipoJuego.ESCALERA) conteo.escaleras++;
        }
    }

    boolean comprobarPosibleCorte(int ronda) {
        int trios = 0, escaleras = 0;
        Conteo conteo = new Conteo();
        getTriosYEscalerasBajadas(conteo);
        boolean puedeCortar = false;
        switch (ronda) {
            case 1:
                puedeCortar = conteo.trios == 2;
                break;
            case 2:
                puedeCortar = conteo.trios == 1 && conteo.escaleras == 1;
                break;
            case 3:
                puedeCortar = conteo.escaleras == 2;
                break;
            case 4:
                puedeCortar = conteo.trios == 3;
                break;
            case 5:
                puedeCortar = conteo.trios == 2 && conteo.escaleras == 1;
                break;
            case 6:
                puedeCortar = conteo.trios == 1 && conteo.escaleras == 2;
                break;
            case 7:
                puedeCortar = conteo.escaleras == 3;
                break;
            default:
                break;
        }
        return puedeCortar;
    }

    void sumarPartida(Partida p) {
        partidas.add(p);
    }

    void resetFinRonda() {
        juegos = new ArrayList<>();
        mano.resetMano();
    }

    public String getNombre() {
        return nombre;
    }

    void setTurnoActual(boolean turnoActual) {
        this.turnoActual = turnoActual;
    }

    boolean isTurnoActual() {
        return turnoActual;
    }

    void setNumeroJugador(int numeroJugador) {
        this.numeroJugador = numeroJugador;
    }

    public int getNumeroJugador() {
        return numeroJugador;
    }

    Mano getMano() {
        return mano;
    }
}