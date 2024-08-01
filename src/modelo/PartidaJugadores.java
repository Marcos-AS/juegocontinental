package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class PartidaJugadores {

    public static jugadorActual getJugador(ArrayList<jugadorActual> jugadores, String nombreJugador) throws RemoteException {
        jugadorActual j = null;
        for (jugadorActual jugadorActual : jugadores) {
            if (jugadorActual.getNombre().equals(nombreJugador)) {
                j = jugadorActual;
            }
        }
        return j;
    }

    public static void repartirCartas(ArrayList<jugadorActual> jugadoresActuales, int numRonda, ArrayList<Carta> mazo) throws RemoteException {
        int numCartasARepartir = ifJuego.cartasPorRonda(numRonda);
        for(jugadorActual j: jugadoresActuales) {
            for(int i = 0; i < numCartasARepartir; i++) {
                Carta c = Mazo.sacarPrimeraDelMazo(mazo);
                j.agregarCarta(c);
            }
        }
        //             jugadoresActuales.get(0).agregarCarta(new Carta(-1, Palo.COMODIN));
//             jugadoresActuales.get(0).agregarCarta(new Carta(5, Palo.TREBOL));
//             jugadoresActuales.get(0).agregarCarta(new Carta(5, Palo.PICAS));
//             jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.PICAS));
//             jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.TREBOL));
//             jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.DIAMANTES));
//             jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.PICAS));
//             jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.DIAMANTES));
//             jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.TREBOL));
//             jugadoresActuales.get(1).agregarCarta(new Carta(-1, Palo.COMODIN));
//             jugadoresActuales.get(1).agregarCarta(new Carta(8, Palo.PICAS));
//             jugadoresActuales.get(1).agregarCarta(new Carta(8, Palo.TREBOL));
//             jugadoresActuales.get(2).agregarCarta(new Carta(9, Palo.PICAS));
//             jugadoresActuales.get(2).agregarCarta(new Carta(9, Palo.DIAMANTES));
//             jugadoresActuales.get(2).agregarCarta(new Carta(9, Palo.CORAZONES));
//             jugadoresActuales.get(2).agregarCarta(new Carta(-1, Palo.COMODIN));
//             jugadoresActuales.get(2).agregarCarta(new Carta(2, Palo.PICAS));
//             jugadoresActuales.get(2).agregarCarta(new Carta(2, Palo.TREBOL));
//        jugadoresActuales.get(0).agregarCarta(new Carta(3, Palo.PICAS));
//        jugadoresActuales.get(0).agregarCarta(new Carta(4, Palo.PICAS));
//        jugadoresActuales.get(0).agregarCarta(new Carta(5, Palo.PICAS));
//        jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.PICAS));
//        jugadoresActuales.get(0).agregarCarta(new Carta(-1, Palo.COMODIN));
//        jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.TREBOL));
//        jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.DIAMANTES));
//        jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.PICAS));
//        jugadoresActuales.get(1).agregarCarta(new Carta(-1, Palo.COMODIN));
//        jugadoresActuales.get(1).agregarCarta(new Carta(5, Palo.PICAS));
//        jugadoresActuales.get(1).agregarCarta(new Carta(6, Palo.PICAS));
//        jugadoresActuales.get(1).agregarCarta(new Carta(-1, Palo.COMODIN));
//        jugadoresActuales.get(1).agregarCarta(new Carta(8, Palo.PICAS));
//        jugadoresActuales.get(1).agregarCarta(new Carta(8, Palo.TREBOL));

    }

    public static void resetearRoboConCastigo(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        for (jugadorActual j : jugadoresActuales) {
            j.setRoboConCastigo(false);
        }
    }

    public static void sumarPuntos(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        int n = 0;
        int puntos;
        while(n < jugadoresActuales.size()) {
            jugadorActual j = jugadoresActuales.get(n);
            puntos = 0;
            for(Carta c: j.getMano()) {
                int num = c.getNumero();
                switch(num) {
                    case 1:
                        puntos += ifJuego.PUNTOS_AS;
                        break;
                    case 11, 12, 13:
                        puntos += ifJuego.PUNTOS_FIGURA;
                        break;
                    case Carta.COMODIN:
                        puntos += ifJuego.PUNTOS_COMODIN;
                        break;
                    case 2,3,4,5,6,7,8,9,10:
                        puntos += num;
                }
            }
            j.setPuntosPartida(puntos);
            n++;
        }
    }

    public static int[] getPuntosJugadores(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        int[] arrayPuntos = new int[jugadoresActuales.size()];
        int i = 0;
        for (jugadorActual jugadorActual : jugadoresActuales) {
            arrayPuntos[i] = jugadorActual.getPuntosPartida();
            i++;
        }
        return arrayPuntos;
    }

    public static jugadorActual determinarGanador(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        jugadorActual ganador = jugadoresActuales.get(0);
        int menosPuntos = ganador.getPuntosPartida();
        for(jugadorActual j: jugadoresActuales) {
            if(j.getPuntosPartida() < menosPuntos) {
                menosPuntos = j.getPuntosPartida();
                ganador = j;
            }
        }
        ganador.setPuntosAlFinalizar(ganador.getPuntosPartida());
        ganador.setGanador(true);
        return ganador;
    }

    public static jugadorActual getGanador(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        jugadorActual ganador = null;
        for (jugadorActual j : jugadoresActuales) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    public static void agregarJugador(ArrayList<jugadorActual> jugadoresActuales, String nombre) throws RemoteException {
        jugadorActual nuevoJugador = new jugadorActual(nombre);
        nuevoJugador.setNumeroJugador(jugadoresActuales.size());
        jugadoresActuales.add(nuevoJugador);
    }

    public static void resetearJuegosJugadores(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        for (jugadorActual jugadorActual : jugadoresActuales) {
            jugadorActual.setTriosBajados(0);
            jugadorActual.setEscalerasBajadas(0);
            jugadorActual.setPuedeBajar(0);
        }
    }

    public static ArrayList<jugadorActual> ponerJugadoresEnOrden(ArrayList<jugadorActual> jugadoresActuales) throws RemoteException {
        ArrayList<jugadorActual> jugadoresNuevo = new ArrayList<>();
        int[] numJugadores = new int[jugadoresActuales.size()];
        int i = 0;
        for (jugadorActual j : jugadoresActuales) {
            int numJugador = j.getNumeroJugador();
            numJugadores[i] = numJugador;
            i++;
        }

        for (int num : numJugadores) {
            jugadoresNuevo.add(jugadoresActuales.get(num));
        }
        return jugadoresNuevo;
    }
}
