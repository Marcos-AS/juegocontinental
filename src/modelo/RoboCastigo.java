package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class RoboCastigo implements Serializable {
    private ArrayList<Integer> jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
    private static RoboCastigo instancia;
    private RoboCastigo(){}

    public static RoboCastigo getInstancia() {
        if (instancia==null) instancia = new RoboCastigo();
        return instancia;
    }

    public ArrayList<Integer> getJugadores() {
        return jugadoresQuePuedenRobarConCastigo;
    }

    public int getNumJugadorRoboCastigo() {
        return jugadoresQuePuedenRobarConCastigo.get(0);
    }

    public void setJugadoresQuePuedenRobarConCastigo() {
        jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
    }

}
