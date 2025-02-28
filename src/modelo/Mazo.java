package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

class Mazo implements Serializable {
    private ArrayList<Carta> mazo;

    void iniciarMazo(int cantJugadores) {
        mazo = new ArrayList<>();
        int numBarajas = determinarNumBarajas(cantJugadores);
        int i = 0;
        while(i < numBarajas) {
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Carta.Palo.PICAS));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Carta.Palo.DIAMANTES));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Carta.Palo.TREBOL));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Carta.Palo.CORAZONES));
            for(int j = 0; j < 2; j++)
                mazo.add(new Carta(-1, Carta.Palo.COMODIN));
            i++;
        }
        mezclarCartas();
    }

    private void mezclarCartas() {
        ArrayList<Carta> mazoMezclado = new ArrayList<>();
        Random random = new Random();
        while(!mazo.isEmpty()) {
            Carta c = mazo.remove(random.nextInt(mazo.size()));
            mazoMezclado.add(c);
        }
        mazo = mazoMezclado;
    }

    Carta sacarPrimeraDelMazo() {
        return mazo.remove(mazo.size()-1);
    }

    private int determinarNumBarajas(int cantJugadores) {
        int cantBarajas = 2;
        if (cantJugadores >= 4 && cantJugadores <= 6) {
            cantBarajas = 3;
        } else if(cantJugadores >= 6 && cantJugadores <= 8) {
            cantBarajas = 4;
        }
        return cantBarajas;
    }

    void repartirCartas(ArrayList<Jugador> jugadores) {
        int numCartasARepartir = 6 + Ronda.getInstancia().getNumRonda() -1;
        for(Jugador j: jugadores) {
            while (j.getMano().getSize() < numCartasARepartir) {
                j.getMano().agregarCarta(sacarPrimeraDelMazo());
            }
        }
    }

    void repartirCartasPrueba(ArrayList<Jugador> jugadores, int numRonda) {
        for (Jugador j : jugadores) {
            switch (numRonda) {
                case 1:
                    asignarTrio(j);
                    asignarTrio(j);
                    break;
                case 2:
                    asignarTrio(j);
                    asignarEscalera(j);
                    break;
                case 3:
                    asignarEscalera(j);
                    asignarEscalera(j);
                    break;
                case 4:
                    asignarTrio(j);
                    asignarTrio(j);
                    asignarTrio(j);
                    break;
                case 5:
                    asignarTrio(j);
                    asignarTrio(j);
                    asignarEscalera(j);
                    break;
                case 6:
                    asignarTrio(j);
                    asignarEscalera(j);
                    asignarEscalera(j);
                    break;
                case 7:
                    asignarEscalera(j);
                    asignarEscalera(j);
                    asignarEscalera(j);
                    break;
                default:
                    throw new IllegalArgumentException("Ronda no válida: " + numRonda);
            }
        }
    }

    //para función de prueba
    private void asignarTrio(Jugador j) {
        // Elegir un valor aleatorio para el trío (1 a 13)
        int valorTrio = (int) (Math.random() * 13) + 1;

        // Buscar tres cartas del mismo valor en el mazo
        ArrayList<Carta> cartasTrio = new ArrayList<>();
        for (int i = 0; i < mazo.size() && cartasTrio.size() < 3; i++) {
            Carta carta = mazo.get(i);
            if (carta.getNumero() == valorTrio) {
                cartasTrio.add(carta);
            }
        }

        for (Carta c : cartasTrio) {
            j.getMano().agregarCarta(c);
            mazo.remove(c);
        }
    }

    //para función de prueba
    private void asignarEscalera(Jugador jugador) {
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (int i = 1; i <= 10; i++) {
                ArrayList<Carta> escalera = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    int valorCarta = i + j;
                    for (Carta carta : mazo) {
                        if (carta.getNumero() == valorCarta && carta.getPalo() == palo) {
                            escalera.add(carta);
                            break;
                        }
                    }
                }

                // Si encontramos una escalera completa
                if (escalera.size() == 4) {
                    for (Carta carta : escalera) {
                        jugador.getMano().agregarCarta(carta);
                        mazo.remove(carta); // Eliminar las cartas del mazo
                    }
                    return; // Salir al encontrar una escalera
                }
            }
        }
    }
}