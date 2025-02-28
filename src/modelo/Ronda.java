package modelo;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Ronda implements Serializable {
    private int numRonda = 1;
    private int numJugadorQueEmpiezaRonda;
    private Carta pozo;
    private Mazo mazo;
    private static Ronda instancia;

    private Ronda(){};
    static Ronda getInstancia() {
        if(instancia==null) instancia = new Ronda();
        return instancia;
    }

    void fin() {
        incrementarNumJugadorQueEmpiezaRonda();
        numRonda++;
    }

    private void incrementarNumJugadorQueEmpiezaRonda() {
        numJugadorQueEmpiezaRonda++;
        try {
            if (numJugadorQueEmpiezaRonda >= Partida.getInstancia().getCantJugadores()) {
                numJugadorQueEmpiezaRonda = 0;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    int getNumRonda() {
        return numRonda;
    }

    void finPartida() {
        numRonda = 1;
    }

    void empezar(ArrayList<Jugador> jugadores) {
        mazo = new Mazo();
        mazo.iniciarMazo(jugadores.size());
        //mazo.repartirCartasPrueba(jugadores,numRonda); //prueba
        mazo.repartirCartas(jugadores);
        pozo = mazo.sacarPrimeraDelMazo();
    }

    Carta sacarPrimeraDelMazo() {
        return mazo.sacarPrimeraDelMazo();
    }

    Carta getPozo() {
        return pozo;
    }

    Carta robarDelPozo() {
        Carta pozo = this.pozo;
        this.pozo = null;
        return pozo;
    }

    void setPozo(Carta carta) {
        pozo = carta;
    }
}
