package modelo;

import rmimvc.src.observer.ObservableRemoto;
import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class Partida extends ObservableRemoto implements Serializable, ifPartida {
    private int numRonda;
    private ArrayList<Carta> pozo;
    private ArrayList<Carta> mazo;
    private ArrayList<jugadorActual> jugadoresActuales = new ArrayList<>();
    private boolean rondaEmpezada = false;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private int numJugadorRoboCastigo;
    private boolean corteRonda = false;
    private int numJugadorCorte;
    private static final int TOTAL_RONDAS = 7;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private int numJugadorQueEmpezoPartida;
    @Serial
    private static final long serialVersionUID = 1L;
    private static Partida partidaActual;

    private Partida() {}

    public static Partida getInstanciaPartida() throws RemoteException {
        if (partidaActual == null) {
            partidaActual = new Partida();
            //System.out.println("new partida " + partidaActual);
        }
        return partidaActual;
    }

    @Override
    public int getTotalRondas() throws RemoteException {
        return TOTAL_RONDAS;
    }

    @Override
    public int getNumRonda() throws RemoteException {
        return numRonda;
    }

    @Override
    public ArrayList<Carta> getPozo() throws RemoteException {
        return pozo;
    }

    @Override
    public Carta sacarPrimeraDelPozo() throws RemoteException {
        return pozo.remove(pozo.size()-1);
    }

    @Override
    public ArrayList<jugadorActual> getJugadoresActuales() throws RemoteException {
        return jugadoresActuales;
    }

    @Override
    public jugadorActual getJugador(String nombreJugador)  throws RemoteException{
        jugadorActual j = null;
        for (jugadorActual jugadorActual : jugadoresActuales) {
            if (jugadorActual.getNombre().equals(nombreJugador)) {
                j = jugadorActual;
            }
        }
        return j;
    }

    @Override
    public boolean isRondaEmpezada() throws RemoteException {
        return rondaEmpezada;
    }

    @Override
    public void setRondaEmpezada(boolean rondaEmpezada) throws RemoteException {
        this.rondaEmpezada = rondaEmpezada;
    }

    @Override
    public void crearMazo() throws RemoteException {
        iniciarMazo(determinarNumBarajas());
        mezclarCartas();
    }

    @Override
    public void iniciarMazo(int numBarajas)  throws RemoteException{
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

    @Override
    public void mezclarCartas()  throws RemoteException{
        ArrayList<Carta> mazoMezclado = new ArrayList<>();
        Random random = new Random();
        while(!mazo.isEmpty()) {
            Carta c = mazo.remove(random.nextInt(mazo.size()));
            mazoMezclado.add(c);
        }
        mazo = mazoMezclado;
    }

    @Override
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

    @Override
    public void iniciarPozo()  throws RemoteException{
        pozo = new ArrayList<>();
        pozo.add(sacarPrimeraDelMazo());
        mazo.remove(mazo.size()-1);
    }

    @Override
    public Carta sacarPrimeraDelMazo() throws RemoteException {
        return mazo.remove(mazo.size()-1);
    }

    @Override
    public int getNumJugadorQueEmpiezaRonda() throws RemoteException {
        return numJugadorQueEmpiezaRonda;
    }

    @Override
    public void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException {
        numJugadorQueEmpiezaRonda++;
    }

    @Override
    public int getNumTurno() throws RemoteException {
        return numTurno;
    }

    @Override
    public void setNumTurno(int numTurno) throws RemoteException {
        this.numTurno = numTurno;
    }

    @Override
    public int getNumJugadorRoboCastigo()  throws RemoteException {
        return numJugadorRoboCastigo;
    }

    @Override
    public void setNumJugadorRoboCastigo(int numJugadorRoboCastigo)  throws RemoteException {
        this.numJugadorRoboCastigo = numJugadorRoboCastigo;
    }

    @Override
    public void resetearRoboConCastigo() throws RemoteException {
        for (jugadorActual j : jugadoresActuales) {
            j.setRoboConCastigo(false);
        }
    }

    @Override
    public void agregarAlPozo(Carta c)  throws RemoteException {
        pozo.add(c);
    }

    @Override
    public boolean isCorteRonda()  throws RemoteException {
        return corteRonda;
    }

    @Override
    public void setCorteRonda()  throws RemoteException {
        corteRonda = !corteRonda;
    }

    @Override
    public int getNumJugadorCorte()  throws RemoteException {
        return numJugadorCorte;
    }

    @Override
    public void setNumJugadorCorte(int numJugadorCorte)  throws RemoteException {
        this.numJugadorCorte = numJugadorCorte;
    }

    @Override
    public void finRonda() throws RemoteException {
        numRonda++;
        resetearJuegosJugadores();
        sumarPuntos();
        setCorteRonda();
    }

    @Override
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

    @Override
    public int[] getPuntosJugadores() throws RemoteException {
        int[] arrayPuntos = new int[jugadoresActuales.size()];
        int i = 0;
        for (jugadorActual jugadorActual : jugadoresActuales) {
            arrayPuntos[i] = jugadorActual.getPuntosPartida();
            i++;
        }
        return arrayPuntos;
    }

    @Override
    public jugadorActual determinarGanador() throws RemoteException {
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

    @Override
    public jugadorActual getGanador()  throws RemoteException{
        jugadorActual ganador = null;
        for (jugadorActual j : jugadoresActuales) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    @Override
    public void finTurno() throws RemoteException{
        numTurno++;
        if (numTurno>jugadoresActuales.size()-1) {
            numTurno = 0;
        }
    }

    @Override
    public void agregarJugador(String nombre) throws RemoteException {
        jugadorActual nuevoJugador = new jugadorActual(nombre);
        nuevoJugador.sumarPartida(this);
        nuevoJugador.setNumeroJugador(jugadoresActuales.size());
        jugadoresActuales.add(nuevoJugador);
    }

    @Override
    public void setEnCurso() throws RemoteException {
        enCurso = !enCurso;
    }

    @Override
    public void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida) throws RemoteException {
        this.numJugadorQueEmpezoPartida = numJugadorQueEmpezoPartida;
    }

    @Override
    public int getNumJugadorQueEmpezoPartida() throws RemoteException {
        return numJugadorQueEmpezoPartida;
    }

    @Override
    public int getCantJugadoresDeseada() throws RemoteException {
        return cantJugadoresDeseada;
    }

    @Override
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

    @Override
    public void setCantJugadoresDeseada(int cantJugadoresDeseada) {
        this.cantJugadoresDeseada = cantJugadoresDeseada;
    }

    private int determinarNumBarajas() throws RemoteException {
        int cantBarajas = BARAJAS_HASTA_4_JUGADORES;
        if (jugadoresActuales.size() >= 4 && jugadoresActuales.size() <= 6) {
            cantBarajas = BARAJAS_MAS_4_JUGADORES;
            //} else if(this.jugadoresActuales.size() >= 6 && this.jugadoresActuales.size() <= 8) {
            //  cantBarajas = BARAJAS_MAS_6_JUGADORES;
        }
        return cantBarajas;
    }

    private void resetearJuegosJugadores() throws RemoteException {
        for (jugadorActual jugadorActual : jugadoresActuales) {
            jugadorActual.setTriosBajados(0);
            jugadorActual.setEscalerasBajadas(0);
            jugadorActual.setPuedeBajar(0);
        }
    }
}
