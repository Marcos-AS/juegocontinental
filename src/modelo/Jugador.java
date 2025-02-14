package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Jugador implements Serializable, ifJugador {
    String nombre;
    private int numeroJugador;
    private int puntosAlFinalizar;
    private final ArrayList<Partida> partidas = new ArrayList<>();
    private boolean turnoActual = false;
    private ArrayList<Carta> mano = new ArrayList<>();
    ArrayList<ArrayList<Carta>> juegos = new ArrayList<>();
    private int puedeBajar = 0;
    private int triosBajados;
    private int escalerasBajadas;
    int puntosPartida = 0;
    private boolean ganador = false;

    Jugador(String nombre) {
        this.nombre = nombre;
    }

    void resetMano() {
        mano = new ArrayList<>();
    }

    void agregarCarta(Carta c) {
        mano.add(c);
    }

    void moverCartaEnMano(int indCarta, int destino) {
        Mano.moverCartaEnMano(mano, indCarta, destino);
    }

    void incrementarPuedeBajar() {
        puedeBajar++;
    }

    ArrayList<Carta> seleccionarCartasABajar(int[]
                                               cartasABajar) {
        return Mano.seleccionarCartasABajar(mano, cartasABajar);
    }

    void bajarJuego(int[] cartasABajar, int tipoJuego) {
        addJuego(seleccionarCartasABajar(cartasABajar), tipoJuego);
        eliminarDeLaMano(juegos.get(juegos.size() - 1));
        if (tipoJuego == Comprobar.TRIO) {
            incrementarTriosBajados();
        } else if (tipoJuego == Comprobar.ESCALERA) {
            incrementarEscalerasBajadas();
        }
    }

    private void addJuego(ArrayList<Carta> juego, int tipoJuego) {
        if (tipoJuego == Comprobar.ESCALERA) {
            juegos.add(JuegoBajado.ordenarJuego(juego));
        } else {
            juegos.add(juego);
        }
    }

    private void eliminarDeLaMano(ArrayList<Carta> cartasABajar) {
        for(Carta c : cartasABajar) {
            mano.remove(c);
        }
    }

    private void incrementarEscalerasBajadas() {
        escalerasBajadas++;
    }

    private void incrementarTriosBajados() {
        triosBajados++;
    }

    int[] comprobarQueFaltaParaCortar(int ronda) {
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

    void sumarPartida(Partida p) {
        partidas.add(p);
    }

    void resetJuegos() {
        juegos = new ArrayList<>();
    }

    void setTriosBajados(int triosBajados) {
        this.triosBajados = triosBajados;
    }

    void setEscalerasBajadas(int escalerasBajadas) {
        this.escalerasBajadas = escalerasBajadas;
    }

    void setPuntosPartida(int puntosPartida) {
        this.puntosPartida = puntosPartida;
    }

    void setGanador(boolean ganador) {
        this.ganador = ganador;
    }

    public String getNombre() {
        return nombre;
    }

    int getPuntosAlFinalizar() {
        return puntosAlFinalizar;
    }

    void setPuntosAlFinalizar(int puntosAlFinalizar) {
        this.puntosAlFinalizar = puntosAlFinalizar;
    }

    void setPuedeBajar(int puedeBajar) {
        this.puedeBajar = puedeBajar;
    }

    boolean isGanador() {
        return ganador;
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

    int getPuedeBajar() {
        return puedeBajar;
    }

    ArrayList<Carta> getMano() {
        return mano;
    }

    ArrayList<ArrayList<Carta>> getJuegos() {
        return juegos;
    }

    int getTriosBajados() {
        return triosBajados;
    }

    int getEscalerasBajadas() {
        return escalerasBajadas;
    }

    int getPuntosPartida() {
        return puntosPartida;
    }
}