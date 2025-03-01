package modelo;

import java.io.Serializable;
import java.util.ArrayList;

class Jugador implements Serializable {
    String nombre;
    private int numeroJugador;
    //private final ArrayList<Partida> partidas = new ArrayList<>();
    private boolean turnoActual = false;
    private final Mano mano = new Mano();
    ArrayList<JuegoBajado> juegos = new ArrayList<>();

    Jugador(String nombre) {
        this.nombre = nombre;
    }

    private void getJuegosBajados(int[] juegosBajados) {
        for (JuegoBajado j : juegos) {
            if (j instanceof TrioBajado) juegosBajados[0]++;
            else if (j instanceof EscaleraBajada) juegosBajados[1]++;
        }
    }

    boolean comprobarPosibleCorte() {
        int[] juegosBajados = new int[2];
        getJuegosBajados(juegosBajados);
        boolean puedeCortar = false;
        switch (Ronda.getInstancia().getNumRonda()) {
            case 1:
                puedeCortar = juegosBajados[0] == 2;
                break;
            case 2:
                puedeCortar = juegosBajados[0] == 1 && juegosBajados[1] == 1;
                break;
            case 3:
                puedeCortar = juegosBajados[1] == 2;
                break;
            case 4:
                puedeCortar = juegosBajados[0] == 3;
                break;
            case 5:
                puedeCortar = juegosBajados[0] == 2 && juegosBajados[1] == 1;
                break;
            case 6:
                puedeCortar = juegosBajados[0] == 1 && juegosBajados[1] == 2;
                break;
            case 7:
                puedeCortar = juegosBajados[1] == 3;
                break;
            default:
                break;
        }
        return puedeCortar;
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

    public ArrayList<ArrayList<Carta>> getJuegos() {
        ArrayList<ArrayList<Carta>> a = new ArrayList<>();
        for (JuegoBajado juego : juegos) {
            a.add(new ArrayList<>(juego.juego));
        }
        return a;
    }
}