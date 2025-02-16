package modelo;

import java.util.ArrayList;

class ReglaEscalera extends ReglaJuego{
    private final ReglaJuego MISMO_PALO = new ReglaMismoPalo();

    @Override
    boolean esValido(ArrayList<Carta> juego) {
        boolean esEscalera = false;
        ArrayList<Carta> comodines = ReglaJuego.extraerComodines(juego);
        if (MISMO_PALO.esValido(juego)) {
            int contadorEscalera = 1;
            ReglaJuego.ordenarCartas(juego);
            for (int i = 0; i < juego.size()-1; i++) {
                int numCartaActual = juego.get(i).getNumero();
                int numCartaSiguiente = juego.get(i + 1).getNumero();
                if (numCartaActual == 13 && numCartaSiguiente == 1) {
                    contadorEscalera++;
                }
                else if (numCartaSiguiente == numCartaActual + 1) {
                    contadorEscalera++;
                } else {
                    if (!comodines.isEmpty()) {
                        if (numCartaActual == numCartaSiguiente - 2) {
                            contadorEscalera += 2;
                            comodines.remove(0);
                        }
                    } else {
                        contadorEscalera = 1;
                    }
                }
            }
            if (!comodines.isEmpty())  contadorEscalera += comodines.size();
            esEscalera = contadorEscalera >= 4;
        }
        return esEscalera;
    }
}