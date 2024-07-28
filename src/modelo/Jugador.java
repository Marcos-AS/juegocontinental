package modelo;

import java.util.ArrayList;

public class Jugador {
    protected String nombre;
    private int puntosAlFinalizar;
    private ArrayList<Partida> partidas = new ArrayList<>();

    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntosAlFinalizar() {
        return puntosAlFinalizar;
    }

    public void setPuntosAlFinalizar(int puntosAlFinalizar) {
        this.puntosAlFinalizar = puntosAlFinalizar;
    }

    public void sumarPartida(Partida p) {
        partidas.add(p);
    }
}
