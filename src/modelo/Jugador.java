package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Jugador implements Serializable, ifJugador {
    protected String nombre;
    private int numeroJugador;
    private int puntosAlFinalizar;
    private final ArrayList<Partida> partidas = new ArrayList<>();
    private boolean turnoActual = false;
    private ArrayList<Carta> mano = new ArrayList<>();
    protected ArrayList<ArrayList<Carta>> juegos = new ArrayList<>();
    private int puedeBajar = 0;
    private int triosBajados;
    private int escalerasBajadas;
    protected int puntosPartida = 0;
    private boolean ganador = false;

    protected Jugador(String nombre) {
        this.nombre = nombre;
    }

    protected void resetMano() {
        mano = new ArrayList<>();
    }

    protected void setTurnoActual(boolean turnoActual) {
        this.turnoActual = turnoActual;
    }

    protected boolean isTurnoActual() {
        return turnoActual;
    }

    protected void setNumeroJugador(int numeroJugador) {
        this.numeroJugador = numeroJugador;
    }

    public int getNumeroJugador() {
        return numeroJugador;
    }

    protected void agregarCarta(Carta c) {
        mano.add(c);
    }

    protected int getPuedeBajar() {
        return puedeBajar;
    }

    protected ArrayList<Carta> getMano() {
        return mano;
    }

    protected ArrayList<ArrayList<Carta>> getJuegos() {
        return juegos;
    }

    protected int getTriosBajados() {
        return triosBajados;
    }

    protected int getEscalerasBajadas() {
        return escalerasBajadas;
    }

    protected int getPuntosPartida() {
        return puntosPartida;
    }

    protected boolean isGanador() {
        return ganador;
    }

    protected void moverCartaEnMano(int indCarta, int destino) {
        Mano.moverCartaEnMano(mano, indCarta, destino);
    }

    protected void setPuedeBajar(int puedeBajar) {
        this.puedeBajar = puedeBajar;
    }

    protected void incrementarPuedeBajar() {
        puedeBajar++;
    }

    protected ArrayList<Carta> seleccionarCartasABajar(int[]
                                               cartasABajar) {
        return Mano.seleccionarCartasABajar(mano, cartasABajar);
    }

    protected void bajarJuego(int[] cartasABajar, int tipoJuego) {
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

    protected int[] comprobarQueFaltaParaCortar(int ronda) {
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

    protected void setTriosBajados(int triosBajados) {
        this.triosBajados = triosBajados;
    }

    protected void setEscalerasBajadas(int escalerasBajadas) {
        this.escalerasBajadas = escalerasBajadas;
    }

    protected void setPuntosPartida(int puntosPartida) {
        this.puntosPartida = puntosPartida;
    }

    protected void setGanador(boolean ganador) {
        this.ganador = ganador;
    }

    public String getNombre() {
        return nombre;
    }

    protected int getPuntosAlFinalizar() {
        return puntosAlFinalizar;
    }

    protected void setPuntosAlFinalizar(int puntosAlFinalizar) {
        this.puntosAlFinalizar = puntosAlFinalizar;
    }

    protected void sumarPartida(Partida p) {
        partidas.add(p);
    }

    protected void resetJuegos() {
        juegos = new ArrayList<>();
    }
}