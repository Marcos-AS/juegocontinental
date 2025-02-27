package modelo;

import java.io.Serializable;
import java.util.ArrayList;

abstract class ReglaJuego implements Serializable {
    abstract boolean esValido(ArrayList<Carta> juego);
}
