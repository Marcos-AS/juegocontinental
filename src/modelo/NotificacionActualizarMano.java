package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public record NotificacionActualizarMano(ArrayList<Carta> cartas) implements Serializable {
}

