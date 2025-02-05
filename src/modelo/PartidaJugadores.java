package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class PartidaJugadores {
    private static final int PUNTOS_FIGURA = 10;
    private static final int PUNTOS_AS = 20;
    private static final int PUNTOS_COMODIN = 50;
    private static final ArrayList<String> nombresElegidos = new ArrayList<>();

    public static ArrayList<String> getNombresElegidos() {
        return nombresElegidos;
    }

    protected static ArrayList<String> getNombreJugadores(ArrayList<Jugador> jugadores) {
        ArrayList<String> nombreJugadores = new ArrayList<>();
        for (Jugador j : jugadores) {
            nombreJugadores.add(j.getNombre());
        }
        return nombreJugadores;
    }

    protected static boolean agregarNombreElegido(String nombre) {
        if (!nombresElegidos.contains(nombre)) {
            nombresElegidos.add(nombre);
            return true;
        }
        return false;
    }

    protected static Jugador getJugador(ArrayList<Jugador> jugadores, String nombreJugador) {
        Jugador j = null;
        for (Jugador jug : jugadores) {
            if (jug.getNombre().equals(nombreJugador)) {
                j = jug;
            }
        }
        return j;
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


    protected static void repartirCartas(ArrayList<Jugador> jugadores,
                  int numRonda, ArrayList<Carta> mazo) throws RemoteException {
        int numCartasARepartir = ifPartida.cartasPorRonda(numRonda);
        for(Jugador j: jugadores) {
            while (j.getMano().size() < numCartasARepartir) {
                j.agregarCarta(Mazo.sacarPrimeraDelMazo(mazo));
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
            j.agregarCarta(c);
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
                        jugador.agregarCarta(carta);
                        mazo.remove(carta); // Eliminar las cartas del mazo
                    }
                    return; // Salir al encontrar una escalera
                }
            }
        }
    }

    protected static int sumarPuntos(Jugador j) {
        int puntos = j.puntosPartida;
        for (Carta c : j.getMano()) {
            int num = c.getNumero();
            switch (num) {
                case 1:
                    puntos += PUNTOS_AS;
                    break;
                case 11, 12, 13:
                    puntos += PUNTOS_FIGURA;
                    break;
                case Carta.COMODIN:
                    puntos += PUNTOS_COMODIN;
                    break;
                case 2, 3, 4, 5, 6, 7, 8, 9, 10:
                    puntos += num;
            }
        }
        return puntos;
    }

    protected static int[] getPuntosJugadores(ArrayList<Jugador> jugadores) {
        int[] arrayPuntos = new int[jugadores.size()];
        int i = 0;
        for (Jugador j : jugadores) {
            arrayPuntos[i] = j.getPuntosPartida();
            i++;
        }
        return arrayPuntos;
    }

    protected static void determinarGanador(ArrayList<Jugador> jugadores) {
        Jugador ganador = jugadores.get(0);
        int menosPuntos = ganador.getPuntosPartida();
        for(Jugador j: jugadores) {
            if(j.getPuntosPartida() < menosPuntos) {
                menosPuntos = j.getPuntosPartida();
                ganador = j;
            }
        }
        ganador.setPuntosAlFinalizar(ganador.getPuntosPartida());
        ganador.setGanador(true);
    }

    protected static Jugador getGanador(ArrayList<Jugador> jugadores) {
        Jugador ganador = null;
        for (Jugador j : jugadores) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    protected static void resetearJuegosJugadores(ArrayList<Jugador> jugadores) {
        for (Jugador jugador : jugadores) {
            jugador.setTriosBajados(0);
            jugador.setEscalerasBajadas(0);
            jugador.setPuedeBajar(0);
            jugador.resetJuegos();
        }
    }

    protected static ArrayList<Jugador> ponerJugadoresEnOrden(ArrayList<Jugador> jugadores) {
        ArrayList<Jugador> jugadoresNuevo = new ArrayList<>();
        int[] numJugadores = new int[jugadores.size()];
        int i = 0;
        for (Jugador j : jugadores) {
            int numJugador = j.getNumeroJugador();
            numJugadores[i] = numJugador;
            i++;
        }

        for (int num : numJugadores) {
            jugadoresNuevo.add(jugadores.get(num));
        }
        return jugadoresNuevo;
    }

    protected static void robarDelMazo(ArrayList<Jugador> jugadores, int numJugador,
                                    ArrayList<Carta> mazo) {
       jugadores.get(numJugador).getMano().add(Mazo.sacarPrimeraDelMazo(mazo));
    }
}