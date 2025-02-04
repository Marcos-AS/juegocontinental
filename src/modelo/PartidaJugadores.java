package modelo;

import rmimvc.src.observer.ObservableRemoto;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class PartidaJugadores extends ObservableRemoto implements Serializable {
    static int PUNTOS_FIGURA = 10;
    static int PUNTOS_AS = 20;
    static int PUNTOS_COMODIN = 50;
    private static final ArrayList<String> nombresElegidos = new ArrayList<>();

    public static ArrayList<String> getNombresElegidos() {
        return nombresElegidos;
    }

    public static ArrayList<String> getNombreJugadores(ArrayList<Jugador> jugadores) throws RemoteException{
        ArrayList<String> nombreJugadores = new ArrayList<>();
        for (Jugador j : jugadores) {
            nombreJugadores.add(j.getNombre());
        }
        return nombreJugadores;
    }

    public static boolean agregarNombreElegido(String nombre) {
        if (!nombresElegidos.contains(nombre)) {
            nombresElegidos.add(nombre);
            return true;
        }
        return false;
    }

    public static Jugador getJugador(ArrayList<Jugador> jugadores, String nombreJugador) throws RemoteException {
        Jugador j = null;
        for (Jugador jug : jugadores) {
            if (jug.getNombre().equals(nombreJugador)) {
                j = jug;
            }
        }
        return j;
    }

    public static void repartirCartasPrueba(ArrayList<Jugador> jugadoresActuales,
                int numRonda, ArrayList<Carta> mazo) throws RemoteException {
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


    public static void repartirCartas(ArrayList<Jugador> jugadoresActuales,
                  int numRonda, ArrayList<Carta> mazo) throws RemoteException {
        int numCartasARepartir = ifPartida.cartasPorRonda(numRonda);
        for(Jugador j: jugadoresActuales) {
            while (j.getMano().size() < numCartasARepartir) {
                j.agregarCarta(Mazo.sacarPrimeraDelMazo(mazo));
            }
        }
    }

    private static void asignarTrio(Jugador j, ArrayList<Carta> mazo) throws RemoteException {
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

    private static void asignarEscalera(Jugador jugador, ArrayList<Carta> mazo)
            throws RemoteException {
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

    public static int sumarPuntos(Jugador j) throws RemoteException {
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

    public static int[] getPuntosJugadores(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        int[] arrayPuntos = new int[jugadoresActuales.size()];
        int i = 0;
        for (Jugador j : jugadoresActuales) {
            arrayPuntos[i] = j.getPuntosPartida();
            i++;
        }
        return arrayPuntos;
    }

    public static void determinarGanador(ArrayList<Jugador> jugadoresActuales)
            throws RemoteException {
        Jugador ganador = jugadoresActuales.get(0);
        int menosPuntos = ganador.getPuntosPartida();
        for(Jugador j: jugadoresActuales) {
            if(j.getPuntosPartida() < menosPuntos) {
                menosPuntos = j.getPuntosPartida();
                ganador = j;
            }
        }
        ganador.setPuntosAlFinalizar(ganador.getPuntosPartida());
        ganador.setGanador(true);
    }

    public static Jugador getGanador(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        Jugador ganador = null;
        for (Jugador j : jugadoresActuales) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    public static void resetearJuegosJugadores(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        for (Jugador jugador : jugadoresActuales) {
            jugador.setTriosBajados(0);
            jugador.setEscalerasBajadas(0);
            jugador.setPuedeBajar(0);
            jugador.resetJuegos();
        }
    }

    public static ArrayList<Jugador> ponerJugadoresEnOrden(ArrayList<Jugador> jugadoresActuales)
            throws RemoteException {
        ArrayList<Jugador> jugadoresNuevo = new ArrayList<>();
        int[] numJugadores = new int[jugadoresActuales.size()];
        int i = 0;
        for (Jugador j : jugadoresActuales) {
            int numJugador = j.getNumeroJugador();
            numJugadores[i] = numJugador;
            i++;
        }

        for (int num : numJugadores) {
            jugadoresNuevo.add(jugadoresActuales.get(num));
        }
        return jugadoresNuevo;
    }

    public static void robarDelMazo(ArrayList<Jugador> jugadores, int numJugador,
                                    ArrayList<Carta> mazo) throws RemoteException {
       jugadores.get(numJugador).getMano().add(Mazo.sacarPrimeraDelMazo(mazo));
    }

    public static void bajarJuego(ArrayList<Jugador> jugadores, int numJugador,
                                  int[] cartasABajar, int tipoJuego) throws RemoteException {
        jugadores.get(numJugador).bajarJuego(cartasABajar, tipoJuego);
    }
}
