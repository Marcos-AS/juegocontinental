package modelo;

import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Partida extends ObservableRemoto implements ifPartida, Serializable {
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private static final String NOMBRE_ARCHIVO_RANKING = "ranking.dat";
    private final Serializador srlRanking = new Serializador(NOMBRE_ARCHIVO_RANKING);
    private static final int BARAJAS_HASTA_4_JUGADORES = 2;
    private static final int BARAJAS_MAS_4_JUGADORES = 3;
    private int numRonda = 1;
    private ArrayList<Carta> pozo;
    private ArrayList<Carta> mazo;
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
    //singleton
    private static Partida instancia;
    private Partida() {
    }

    public static Partida getInstancia() throws RemoteException {
        if (Partida.instancia == null) {
            Partida.instancia = new Partida();
        }
        return Partida.instancia;
    }

    public boolean isEnCurso() throws RemoteException {
        return enCurso;
    }

    @Override
    public int getObservadorIndex(IObservadorRemoto o) throws RemoteException {
        return getObservadores().indexOf(o);
    }

    @Override
    public void serializarGanador() throws RemoteException {
        Object guardar = getGanador().nombre + " --- puntos: " + getGanador().getPuntosAlFinalizar();
        if (srlRanking.readFirstObject()==null) {
            srlRanking.writeOneObject(guardar);
        } else {
            Object[] jugadores = srlRanking.readObjects();
            ArrayList<String> listaJugadores = new ArrayList<>();

            for (Object jugador : jugadores) {
                listaJugadores.add(jugador.toString());
            }
            listaJugadores.add(guardar.toString());

            listaJugadores.sort((j1, j2) -> {
                int puntos1 = Integer.parseInt(j1.split(" --- puntos: ")[1]);
                int puntos2 = Integer.parseInt(j2.split(" --- puntos: ")[1]);
                return Integer.compare(puntos2, puntos1); // Orden descendente
            });

            int i = 0;
            srlRanking.writeOneObject(listaJugadores.get(i));
            for (i = 1; i < listaJugadores.size(); i++) {
                srlRanking.addOneObject(listaJugadores.get(i)); //revisar tema cabecera
            }
        }
    }

    @Override
    public void removerObservadores() throws RemoteException {
        int cantObservadores = getObservadores().size();
        for (int i = cantObservadores-1; i >= 0; i--) {
            removerObservador(getObservadores().get(i));
        }
    }

    @Override
    public Serializador getRanking() throws RemoteException {
        return srlRanking;
    }

    @Override
    public ArrayList<Jugador> getJugadores() throws RemoteException {
        return jugadores;
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
        return pozo.remove(pozo.size() - 1);
    }

    public Carta getPrimeraDelPozo() throws RemoteException {
        return pozo.get(pozo.size()-1);
    }

    @Override
    public Jugador getJugador(String nombreJugador)  throws RemoteException{
        return PartidaJugadores.getJugador(jugadores, nombreJugador);
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
        mazo = Mazo.mezclarCartas(Mazo.iniciarMazo(determinarNumBarajas()));
    }


    @Override
    public void repartirCartas() throws RemoteException {
        PartidaJugadores.repartirCartas(jugadores, numRonda, mazo);
    }


    @Override
    public void iniciarPozo()  throws RemoteException{
        pozo = new ArrayList<>();
        pozo.add(sacarPrimeraDelMazo());
    }

    @Override
    public Carta sacarPrimeraDelMazo() throws RemoteException {
        return mazo.remove(mazo.size()-1);
    }

    public void robarDelMazo(int numJugador) throws RemoteException {
        PartidaJugadores.robarDelMazo(jugadores, numJugador, mazo);
    }

    public void setRoboDelMazo(int i, boolean b) throws RemoteException{
        jugadores.get(i).setRoboDelMazo(b);
    }

    public void robarDelPozo(int numJugador) throws RemoteException {
        jugadores.get(numJugador).getMano().add(sacarPrimeraDelPozo());
    }

    public void robarConCastigo(int numJugadorRoboCastigo)
            throws RemoteException {
        robarDelMazo(numJugadorRoboCastigo);
        robarDelPozo(numJugadorRoboCastigo);
        jugadores.get(numJugadorRoboCastigo).setRoboConCastigo(true);
    }

    public void setTurnoJugador(int numJugador, boolean valor) throws RemoteException {
        jugadores.get(numJugador).setTurnoActual(valor);
    }

    public void tirarAlPozo(int numJugador, int cartaATirar) throws RemoteException {
        pozo.add(jugadores.get(numJugador).getMano().remove(cartaATirar));
        jugadores.get(numJugador).setTurnoActual(false);
    }

    public void moverCartaEnMano(int numJugador, int i, int i1) throws RemoteException {
        PartidaJugadores.moverCartaEnMano(jugadores, numJugador, i, i1);
    }

    public void bajarJuego(int numJugador, int[] cartasABajar, int tipoJuego) throws RemoteException {
        PartidaJugadores.bajarJuego(jugadores, numJugador, cartasABajar, tipoJuego);
    }

    public void setPuedeBajar(int numJugador, int i) throws RemoteException {
        jugadores.get(numJugador).setPuedeBajar(i);
    }

    public void incPuedeBajar(int numJugador) throws RemoteException {
        jugadores.get(numJugador).incrementarPuedeBajar();
    }

    public int getPuedeBajar(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getPuedeBajar();
    }

    @Override
    public String getNombreJugador(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getNombre();
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
        PartidaJugadores.resetearRoboConCastigo(jugadores);
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
        PartidaJugadores.resetearJuegosJugadores(jugadores);
        PartidaJugadores.sumarPuntos(jugadores);
        setCorteRonda();
    }

    @Override
    public int[] getPuntosJugadores() throws RemoteException {
        return PartidaJugadores.getPuntosJugadores(jugadores);
    }

    @Override
    public Jugador determinarGanador() throws RemoteException {
        return PartidaJugadores.determinarGanador(jugadores);
    }

    @Override
    public Jugador getGanador()  throws RemoteException{
        return PartidaJugadores.getGanador(jugadores);
    }

    @Override
    public void incTurno() throws RemoteException{
        numTurno++;
        if (numTurno>jugadores.size()-1) {
            numTurno = 0;
        }
    }

    @Override
    public void crearYAgregarJugador(String nombre, int numObservador) throws RemoteException {
        Jugador nuevoJugador = new Jugador(nombre);
        nuevoJugador.setNumeroJugador(numObservador);
        jugadores.add(nuevoJugador);
        jugadores.get(jugadores.size()-1).sumarPartida(this);
    }

    @Override
    public void setEnCurso() throws RemoteException {
        enCurso = !enCurso;
    }

    @Override
    public void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida)
            throws RemoteException {
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
        jugadores = PartidaJugadores.ponerJugadoresEnOrden(jugadores);
    }

    @Override
    public void setCantJugadoresDeseada(int cantJugadoresDeseada) {
        this.cantJugadoresDeseada = cantJugadoresDeseada;
    }

    @Override
    public int determinarNumBarajas() throws RemoteException {
        int cantBarajas = BARAJAS_HASTA_4_JUGADORES;
        if (jugadores.size() >= 4 && jugadores.size() <= 6) {
            cantBarajas = BARAJAS_MAS_4_JUGADORES;
            //} else if(this.jugadores.size() >= 6 && this.jugadores.size() <= 8) {
            //  cantBarajas = BARAJAS_MAS_6_JUGADORES;
        }
        return cantBarajas;
    }

    @Override
    public int getCantJugadores() throws RemoteException {
        return jugadores.size();
    }
}
