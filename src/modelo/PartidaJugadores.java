package modelo;

import rmimvc.src.observer.ObservableRemoto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class PartidaJugadores extends ObservableRemoto implements Serializable {

    public static Jugador getJugador(ArrayList<Jugador> jugadores, String nombreJugador) throws RemoteException {
        Jugador j = null;
        for (Jugador Jugador : jugadores) {
            if (Jugador.getNombre().equals(nombreJugador)) {
                j = Jugador;
            }
        }
        return j;
    }

    public static void repartirCartas(ArrayList<Jugador> jugadoresActuales, int numRonda, ArrayList<Carta> mazo) throws RemoteException {
//        int numCartasARepartir = ifPartida.cartasPorRonda(numRonda);
//        for(Jugador j: jugadoresActuales) {
//            for(int i = 0; i < numCartasARepartir; i++) {
//                Carta c = Mazo.sacarPrimeraDelMazo(mazo);
//                j.agregarCarta(c);
//            }
//        }
        jugadoresActuales.get(0).agregarCarta(new Carta(-1, Palo.COMODIN));
        jugadoresActuales.get(0).agregarCarta(new Carta(5, Palo.TREBOL));
        jugadoresActuales.get(0).agregarCarta(new Carta(5, Palo.PICAS));
        jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.PICAS));
        jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.TREBOL));
        jugadoresActuales.get(0).agregarCarta(new Carta(6, Palo.DIAMANTES));
        jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.PICAS));
        jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.DIAMANTES));
        jugadoresActuales.get(1).agregarCarta(new Carta(3, Palo.TREBOL));
        jugadoresActuales.get(1).agregarCarta(new Carta(-1, Palo.COMODIN));
        jugadoresActuales.get(1).agregarCarta(new Carta(8, Palo.PICAS));
        jugadoresActuales.get(1).agregarCarta(new Carta(8, Palo.TREBOL));
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

    public static void resetearRoboConCastigo(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        for (Jugador j : jugadoresActuales) {
            j.setRoboConCastigo(false);
        }
    }

    public static void sumarPuntos(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        int n = 0;
        int puntos;
        while(n < jugadoresActuales.size()) {
            Jugador j = jugadoresActuales.get(n);
            puntos = 0;
            for(Carta c: j.getMano()) {
                int num = c.getNumero();
                switch(num) {
                    case 1:
                        puntos += ifPartida.PUNTOS_AS;
                        break;
                    case 11, 12, 13:
                        puntos += ifPartida.PUNTOS_FIGURA;
                        break;
                    case Carta.COMODIN:
                        puntos += ifPartida.PUNTOS_COMODIN;
                        break;
                    case 2,3,4,5,6,7,8,9,10:
                        puntos += num;
                }
            }
            j.setPuntosPartida(puntos);
            n++;
        }
    }

    public static int[] getPuntosJugadores(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        int[] arrayPuntos = new int[jugadoresActuales.size()];
        int i = 0;
        for (Jugador Jugador : jugadoresActuales) {
            arrayPuntos[i] = Jugador.getPuntosPartida();
            i++;
        }
        return arrayPuntos;
    }

    public static Jugador determinarGanador(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
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
        return ganador;
    }

    public static Jugador getGanador(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        Jugador ganador = null;
        for (Jugador j : jugadoresActuales) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    public static void resetearJuegosJugadores(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
        for (Jugador Jugador : jugadoresActuales) {
            Jugador.setTriosBajados(0);
            Jugador.setEscalerasBajadas(0);
            Jugador.setPuedeBajar(0);
        }
    }

    public static ArrayList<Jugador> ponerJugadoresEnOrden(ArrayList<Jugador> jugadoresActuales) throws RemoteException {
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
       jugadores.get(numJugador).setRoboDelMazo(true);
    }

    public static void moverCartaEnMano(ArrayList<Jugador> jugadores, int numJugador,
                                        int i, int i1) throws RemoteException {
        jugadores.get(numJugador).moverCartaEnMano(i, i1);
    }

    public static void bajarJuego(ArrayList<Jugador> jugadores, int numJugador,
                                  int[] cartasABajar, int tipoJuego) throws RemoteException {
        jugadores.get(numJugador).bajarJuego(cartasABajar, tipoJuego);
    }
}
