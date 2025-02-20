package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class RoboCastigo implements Serializable {
    private ArrayList<Integer> jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
    private int numJugadorRoboCastigo;
    private static RoboCastigo instancia;
    private RoboCastigo(){}

    static RoboCastigo getInstancia() {
        if (instancia==null) instancia = new RoboCastigo();
        return instancia;
    }

    ArrayList<Integer> getJugadores() {
        return jugadoresQuePuedenRobarConCastigo;
    }

    int getNumJugadorRoboCastigo() {
        return numJugadorRoboCastigo;
    }

    void setNumJugadorRoboCastigo(int numJugadorRoboCastigo) {
        this.numJugadorRoboCastigo = numJugadorRoboCastigo;
    }

    void incNumJugadorRoboCastigo() {
        numJugadorRoboCastigo++;
        if (numJugadorRoboCastigo>=jugadoresQuePuedenRobarConCastigo.size())
            numJugadorRoboCastigo = 0;
    }

    void setJugadoresQuePuedenRobarConCastigo() {
        jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
    }

}
