package modelo;

import java.io.Serializable;

public interface ifCarta extends Serializable {
    Carta.Palo getPalo();
    int getNumero();
}
