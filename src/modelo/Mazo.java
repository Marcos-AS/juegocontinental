package modelo;

import java.util.ArrayList;
import java.util.Random;

public class Mazo {
    private ArrayList<Carta> mazo;

    void iniciarMazo(int cantJugadores) {
        mazo = new ArrayList<>();
        int numBarajas = determinarNumBarajas(cantJugadores);
        int i = 0;
        while(i < numBarajas) {
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.PICAS));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.DIAMANTES));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.TREBOL));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.CORAZONES));
            for(int j = 0; j < 2; j++)
                mazo.add(new Carta(-1, Palo.COMODIN));
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

    void repartirCartas(ArrayList<Jugador> jugadores, int numRonda) {
        int numCartasARepartir = 6 + numRonda -1;
        for(Jugador j: jugadores) {
            while (j.getMano().getSize() < numCartasARepartir) {
                j.getMano().agregarCarta(sacarPrimeraDelMazo());
            }
        }
    }

    public static void repartirCartasPrueba(ArrayList<Jugador> jugadoresActuales,
                                            int numRonda, ArrayList<Carta> mazo) {
        for (Jugador j : jugadoresActuales) {
            switch (numRonda) {
                case 1:
                    asignarTrio(j, mazo);
                    asignarTrio(j, mazo);
                    break;
                case 2:
                    asignarTrio(j, mazo);
                    asignarEscalera(j, mazo);
                    break;
                case 3:
                    asignarEscalera(j, mazo);
                    asignarEscalera(j, mazo);
                    break;
                case 4:
                    asignarTrio(j, mazo);
                    asignarTrio(j, mazo);
                    asignarTrio(j, mazo);
                    break;
                case 5:
                    asignarTrio(j, mazo);
                    asignarTrio(j, mazo);
                    asignarEscalera(j, mazo);
                    break;
                case 6:
                    asignarTrio(j, mazo);
                    asignarEscalera(j, mazo);
                    asignarEscalera(j, mazo);
                    break;
                case 7:
                    asignarEscalera(j, mazo);
                    asignarEscalera(j, mazo);
                    asignarEscalera(j, mazo);
                    break;
                default:
                    throw new IllegalArgumentException("Ronda no válida: " + numRonda);
            }
        }
    }

    //para función de prueba
    private static void asignarTrio(Jugador j, ArrayList<Carta> mazo) {
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
    private static void asignarEscalera(Jugador jugador, ArrayList<Carta> mazo) {
        for (Palo palo : Palo.values()) {
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