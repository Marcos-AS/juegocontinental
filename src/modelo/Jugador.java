package modelo;

import java.io.Serializable;
import java.util.ArrayList;

class Jugador implements Serializable {
    String nombre;
    private int numeroJugador;
    private final ArrayList<Partida> partidas = new ArrayList<>();
    private boolean turnoActual = false;
    private Mano mano = new Mano();
    ArrayList<JuegoBajado> juegos = new ArrayList<>();

    Jugador(String nombre) {
        this.nombre = nombre;
    }

    int[] comprobarQueFaltaParaCortar(int ronda) {
        int triosBajados = getTriosBajados();
        int escalerasBajadas = getEscalerasBajadas();
        int trios = 0;
        int escaleras = 0;
        int[] faltante = new int[2];
        switch (ronda) {
            case 1:
                trios = 2 - triosBajados;
                break;
            case 2:
                trios = 1 - triosBajados;
                escaleras = 1 - escalerasBajadas;
                break;
            case 3:
                escaleras = 2 - escalerasBajadas;
                break;
            case 4:
                trios = 3 - triosBajados;
                break;
            case 5:
                trios = 2 - triosBajados;
                escaleras = 1 - escalerasBajadas;
                break;
            case 6:
                trios = 1 - triosBajados;
                escaleras = 2 - escalerasBajadas;
                break;
            case 7:
                escaleras = 3 - escalerasBajadas;
                break;
        }
        faltante[0] = trios;
        faltante[1] = escaleras;
        return faltante;
    }

    private int getTriosBajados() {
        int trios = 0;
        for (JuegoBajado j : juegos) {
            if (j.tipo==TipoJuego.TRIO) trios++;
        }
        return trios;
    }

    private int getEscalerasBajadas() {
        int escaleras = 0;
        for (JuegoBajado j : juegos) {
            if (j.tipo==TipoJuego.ESCALERA) escaleras++;
        }
        return escaleras;
    }

    boolean comprobarPosibleCorte(int ronda) {
        int trios = getTriosBajados();
        int escaleras = getEscalerasBajadas();
        boolean puedeCortar = false;
        switch (ronda) {
            case 1:
                puedeCortar = trios == 2;
                break;
            case 2:
                puedeCortar = trios == 1 && escaleras == 1;
                break;
            case 3:
                puedeCortar = escaleras == 2;
                break;
            case 4:
                puedeCortar = trios == 3;
                break;
            case 5:
                puedeCortar = trios == 2 && escaleras == 1;
                break;
            case 6:
                puedeCortar = trios == 1 && escaleras == 2;
                break;
            case 7:
                puedeCortar = escaleras == 3;
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