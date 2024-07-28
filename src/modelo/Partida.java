package modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class Partida {
    private int numRonda;
    private ArrayList<Carta> pozo;
    private ArrayList<Carta> mazo;
    private ArrayList<jugadorActual> jugadoresActuales = new ArrayList<>();
    private boolean rondaEmpezada = false;
    private static final int BARAJAS_HASTA_4_JUGADORES = 2;
    private static final int BARAJAS_MAS_4_JUGADORES = 3;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private int numJugadorRoboCastigo;
    private boolean corteRonda = false;
    private int numJugadorCorte;
    private static final int TOTAL_RONDAS = 7;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private int numJugadorQueEmpezoPartida;

    public Partida(int cantJugadoresDeseada) {
        this.cantJugadoresDeseada = cantJugadoresDeseada;
    }

    public int getTotalRondas() {
        return TOTAL_RONDAS;
    }

    public int getNumRonda() {
        return numRonda;
    }

    public void setNumRonda(int numRonda) {
        this.numRonda = numRonda;
    }

    public ArrayList<Carta> getPozo() {
        return pozo;
    }

    public Carta sacarPrimeraDelPozo() {
        return pozo.remove(pozo.size()-1);
    }

    public ArrayList<jugadorActual> getJugadoresActuales() {
        return jugadoresActuales;
    }

    public jugadorActual getJugador(String nombreJugador) {
        jugadorActual j = null;
        for (jugadorActual jugadorActual : jugadoresActuales) {
            if (jugadorActual.getNombre().equals(nombreJugador)) {
                j = jugadorActual;
            }
        }
        return j;
    }

    public boolean isRondaEmpezada() {
        return rondaEmpezada;
    }

    public void setRondaEmpezada(boolean rondaEmpezada) {
        this.rondaEmpezada = rondaEmpezada;
    }

    private int determinarNumBarajas() {
        int cantBarajas = BARAJAS_HASTA_4_JUGADORES;
        if (jugadoresActuales.size() >= 4 && jugadoresActuales.size() <= 6) {
            cantBarajas = BARAJAS_MAS_4_JUGADORES;
            //} else if(this.jugadoresActuales.size() >= 6 && this.jugadoresActuales.size() <= 8) {
            //  cantBarajas = BARAJAS_MAS_6_JUGADORES;
        }
        return cantBarajas;
    }

    public void crearMazo() throws RemoteException {
        iniciarMazo(determinarNumBarajas());
        mezclarCartas();
    }

    public void iniciarMazo(int numBarajas) {
        mazo = new ArrayList<>();
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
    }

    public void mezclarCartas() {
        ArrayList<Carta> mazoMezclado = new ArrayList<>();
        Random random = new Random();
        while(!mazo.isEmpty()) {
            Carta c = mazo.remove(random.nextInt(mazo.size()));
            mazoMezclado.add(c);
        }
        mazo = mazoMezclado;
    }

    public void repartirCartas() throws RemoteException {
        int numCartasARepartir = ifJuego.cartasPorRonda(numRonda);
        for(jugadorActual j: jugadoresActuales) {
            for(int i = 0; i < numCartasARepartir; i++) {
                Carta c = sacarPrimeraDelMazo();
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

    public void iniciarPozo() {
        pozo = new ArrayList<>();
        pozo.add(sacarPrimeraDelMazo());
        mazo.remove(mazo.size()-1);
    }

    public Carta sacarPrimeraDelMazo() {
        return mazo.remove(mazo.size()-1);
    }

    public int getNumJugadorQueEmpiezaRonda() {
        return numJugadorQueEmpiezaRonda;
    }

    public void setNumJugadorQueEmpiezaRonda(int numJugadorQueEmpiezaRonda) {
        this.numJugadorQueEmpiezaRonda = numJugadorQueEmpiezaRonda;
    }

    public void incrementarNumJugadorQueEmpiezaRonda() {
        numJugadorQueEmpiezaRonda++;
    }

    public int getNumTurno() {
        return numTurno;
    }

    public void setNumTurno(int numTurno) {
        this.numTurno = numTurno;
    }

    public int getNumJugadorRoboCastigo() {
        return numJugadorRoboCastigo;
    }

    public void setNumJugadorRoboCastigo(int numJugadorRoboCastigo) {
        this.numJugadorRoboCastigo = numJugadorRoboCastigo;
    }

    public void resetearRoboConCastigo() throws RemoteException {
        for (jugadorActual j : jugadoresActuales) {
            j.setRoboConCastigo(false);
        }
    }

    public void agregarAlPozo(Carta c) {
        pozo.add(c);
    }

    public boolean isCorteRonda() {
        return corteRonda;
    }

    public void setCorteRonda() {
        corteRonda = !corteRonda;
    }

    public int getNumJugadorCorte() {
        return numJugadorCorte;
    }

    public void setNumJugadorCorte(int numJugadorCorte) {
        this.numJugadorCorte = numJugadorCorte;
    }

    public void finRonda() throws RemoteException {
        numRonda++;
        resetearJuegosJugadores();
        sumarPuntos();
        setCorteRonda();
    }

    private void resetearJuegosJugadores() {
        for (jugadorActual jugadorActual : jugadoresActuales) {
            jugadorActual.setTriosBajados(0);
            jugadorActual.setEscalerasBajadas(0);
            jugadorActual.setPuedeBajar(0);
        }
    }

    public void sumarPuntos() throws RemoteException {
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

    public int[] getPuntosJugadores() {
        int[] arrayPuntos = new int[jugadoresActuales.size()];
        int i = 0;
        for (jugadorActual jugadorActual : jugadoresActuales) {
            arrayPuntos[i] = jugadorActual.getPuntosPartida();
            i++;
        }
        return arrayPuntos;
    }

    public jugadorActual determinarGanador() {
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

    public jugadorActual getGanador() {
        jugadorActual ganador = null;
        for (jugadorActual j : jugadoresActuales) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    public void finTurno() {
        numTurno++;
        if (numTurno>jugadoresActuales.size()-1) {
            numTurno = 0;
        }
    }

    public void agregarJugador(String nombre) throws RemoteException {
        jugadorActual nuevoJugador = new jugadorActual(nombre);
        nuevoJugador.sumarPartida(this);
        nuevoJugador.setNumeroJugador(jugadoresActuales.size());
        jugadoresActuales.add(nuevoJugador);
    }

    public boolean isEnCurso() {
        return enCurso;
    }

    public void setEnCurso() {
        enCurso = !enCurso;
    }

    public void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida) {
        this.numJugadorQueEmpezoPartida = numJugadorQueEmpezoPartida;
    }

    public int getNumJugadorQueEmpezoPartida() {
        return numJugadorQueEmpezoPartida;
    }

    public int getCantJugadoresDeseada() {
        return cantJugadoresDeseada;
    }

    public void ponerJugadoresEnOrden() throws RemoteException {
        ArrayList<jugadorActual> jugadores = jugadoresActuales;
        ArrayList<jugadorActual> jugadoresNuevo = new ArrayList<>();
        int[] numJugadores = new int[jugadoresActuales.size()];
        int i = 0;
        for (jugadorActual j : jugadores) {
            int numJugador = j.getNumeroJugador();
            numJugadores[i] = numJugador;
            i++;
        }

        for (int num : numJugadores) {
            jugadoresNuevo.add(jugadores.get(num));
        }
        jugadoresActuales = jugadoresNuevo;
    }
}
