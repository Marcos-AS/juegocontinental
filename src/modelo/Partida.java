package modelo;

import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

import static modelo.Eventos.*;

public class Partida extends ObservableRemoto implements ifPartida, Serializable {
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private Map<UUID, Integer> idsJugadores = new HashMap<>();
    private final Serializador srlRanking = new Serializador("src/serializacion/ranking.dat");
    protected static final int BARAJAS_HASTA_4_JUGADORES = 2;
    protected static final int BARAJAS_MAS_4_JUGADORES = 3;
    private int numRonda = 1;
    private ArrayList<Carta> pozo;
    private ArrayList<Carta> mazo;
    private boolean rondaEmpezada = false;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private ArrayList<Integer> jugadoresQuePuedenRobarConCastigo;
    private int numJugadorCorte;
    private static final int TOTAL_RONDAS = 1;
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

    public void desarrolloPartida() throws RemoteException {
        if (!isRondaEmpezada()) {
            empezarRonda();
        }
        int i = getNumTurno(); //cambio de número de jugador, cambio de turno
        //partida.notificarObservadores(NOTIFICACION_COMIENZO_TURNO);
        //System.out.println("numero de jugador: " + i);

        //UTILIZA notificarObservador, NO observadores
        notificarObservador(i, NOTIFICACION_ROBO);
        //System.out.println("numero de jugador: " + i);
        if (isRoboDelMazo(i) && !isPozoEmpty()) {
            roboCastigo();
        }
        notificarObservador(i, NOTIFICACION_DESARROLLO_TURNO);
    }

    public boolean isTurnoActual(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).isTurnoActual();
    }

    public boolean isEnCurso() throws RemoteException {
        return enCurso;
    }

    public ifCarta getCarta(int numJugador, int iCarta) throws RemoteException {
        return jugadores.get(numJugador).getMano().get(iCarta);
    }

    public ArrayList<ArrayList<Carta>> getJuegos(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getJuegos();
    }

    public void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException {
        jugadores.get(numJugador).setNumeroJugador(nuevoNumero);
    }

    public boolean isRoboDelMazo(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).isRoboDelMazo();
    }

    public boolean comprobarAcomodarCarta(int numJugador, int numCarta, int numJuego, int ronda)
        throws RemoteException {
        return jugadores.get(numJugador).comprobarAcomodarCarta(numCarta,numJuego,ronda);
    }

    public void acomodarEnJuegoAjeno(int numJugador, int iCarta, int numJuego) throws RemoteException {
        Carta c = jugadores.get(numJugador).getMano().remove(iCarta);
        jugadores.get(numJugador).getJuegos().get(numJuego).add(c);
    }

    public int comprobarBajarse(int numJugador, int[] cartasABajar) throws RemoteException{
        int tipoJuego = Comprobar.comprobarJuego(jugadores.get(numJugador)
                .seleccionarCartasABajar(cartasABajar),numRonda);
        if(tipoJuego != Comprobar.JUEGO_INVALIDO)
            bajarJuego(numJugador, cartasABajar, tipoJuego);
        return tipoJuego;
    }

    public void finPartida() throws RemoteException {
        setEnCurso();
        determinarGanador(); //al finalizar las rondas
        serializarGanador();
        notificarObservadores(NOTIFICACION_GANADOR);
        notificarObservadores(NOTIFICACION_FIN_PARTIDA);
        //lo siguiente es para poder seguir jugando otras partidas
        removerObservadores();
    }

    public boolean cortar(int numJugador) throws RemoteException {
        boolean corte = false;
        int sizeMano = jugadores.get(numJugador).getMano().size();
        if (sizeMano <= 1) {
            if (sizeMano == 1)
                tirarAlPozo(numJugador,0);
            setPuedeBajar(numJugador, 0);
            corte = true;
        }
        return corte;
    }

    public void crearPartida(String vista, int observadorIndex, UUID idJugador, int cantJugadoresDeseada) throws RemoteException {
        crearYAgregarJugador(vista, observadorIndex, idJugador);
        //partida.notificarObservadores(NOTIFICACION_NUEVO_JUGADOR);
        setCantJugadoresDeseada(cantJugadoresDeseada);
        setEnCurso();
        //partida.notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
        setNumTurno(observadorIndex);
        setNumJugadorQueEmpezoPartida(observadorIndex);
        notificarObservador(observadorIndex, NOTIFICACION_NUEVA_PARTIDA_PROPIO);

    }

    public ArrayList<ifCarta> getMano(UUID idSolicitante, int idObjetivo) throws RemoteException {
        if (isAccionValida(idSolicitante,idObjetivo)) {
            ArrayList<Carta> mano = jugadores.get(idObjetivo).getMano();
            return new ArrayList<>(mano);
        }
        return null;
    }

    private boolean isAccionValida(UUID idSolicitante, int idObjetivo) {
        return idsJugadores.get(idSolicitante)==idObjetivo;
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
        mazo = Mazo.mezclarCartas(Mazo.iniciarMazo(Mazo.determinarNumBarajas(jugadores)));
    }


    @Override
    public void repartirCartas() throws RemoteException {
        PartidaJugadores.repartirCartas(jugadores, numRonda, mazo);
    }


    @Override
    public void iniciarPozo()  throws RemoteException{
        pozo = new ArrayList<>();
        pozo.add(Mazo.sacarPrimeraDelMazo(mazo));
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

    public void roboCastigo() throws RemoteException {
        setRoboDelMazo(numTurno, false);
        notificarRoboConCastigo(numTurno);
    }

    public void notificarRoboConCastigo(int numJugador) throws RemoteException {
        jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
        ArrayList<Integer> jugadoresQueNoPuedenRobarConCastigo = new ArrayList<>();

        for (int i = 0; i < jugadores.size()-1; i++) {
            numJugador++;
            if (numJugador > jugadores.size() - 1) numJugador = 0;
            if (getPuedeBajar(numJugador)==0) {
                jugadoresQuePuedenRobarConCastigo.add(numJugador);
            } else {
                jugadoresQueNoPuedenRobarConCastigo.add(numJugador);
            }
        }
        notificarObservadores(jugadoresQueNoPuedenRobarConCastigo,
                NOTIFICACION_NO_PUEDE_ROBO_CASTIGO);
        notificarObservadores(jugadoresQuePuedenRobarConCastigo,
                NOTIFICACION_ROBO_CASTIGO);
    }

    public void robarConCastigo()
            throws RemoteException {
        int num = getNumJugadorRoboCastigo();
        robarDelMazo(num);
        robarDelPozo(num);
        notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
    }

    public void empezarRonda() throws RemoteException {
        setRondaEmpezada(true);
        //partida.notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
        crearMazo();
        repartirCartas();
        iniciarPozo();
        setNumTurno(numJugadorQueEmpiezaRonda);
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

    public int getTriosBajados(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getTriosBajados();
    }

    public int[] comprobarQueFaltaParaCortar(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).comprobarQueFaltaParaCortar(numRonda);
    }

    public int getEscalerasBajadas(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getEscalerasBajadas();
    }

    public boolean puedeRobarConCastigo() throws RemoteException {
        return jugadores.get(jugadoresQuePuedenRobarConCastigo.get(0)).getPuedeBajar()==0;
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
        return jugadoresQuePuedenRobarConCastigo.get(0);
    }

    public void removeJugadorRoboCastigo() throws RemoteException {
        jugadoresQuePuedenRobarConCastigo.remove(0);
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
    public void finRonda(int numJugador) throws RemoteException {
        setRondaEmpezada(false);
        setTurnoJugador(numJugador, false);
        setNumJugadorCorte(numJugador);
        incrementarNumJugadorQueEmpiezaRonda();
        numRonda++;
        for (Jugador j : jugadores) {
            j.setPuntosPartida(PartidaJugadores.sumarPuntos(j));
        }
        PartidaJugadores.resetearJuegosJugadores(jugadores);
        Mano.resetearMano(jugadores);
        notificarObservadores(NOTIFICACION_CORTE_RONDA);
        notificarObservadores(NOTIFICACION_PUNTOS);
    }

    public void finTurno() throws RemoteException {
        Object[] notif = new Object[2];
        notif[0] = NOTIFICACION_FIN_TURNO;
        notif[1] = getNumTurno();
        incTurno();
        notificarObservadores(notif);
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
        jugadores.get(numTurno).setTurnoActual(true);
    }

    public boolean isPozoEmpty() throws RemoteException {
        return pozo.isEmpty();
    }

    @Override
    public void crearYAgregarJugador(String nombre, int numObservador, UUID idJugador) throws RemoteException {
        Jugador nuevoJugador = new Jugador(nombre);
        nuevoJugador.setNumeroJugador(numObservador);
        idsJugadores.put(idJugador, numObservador);
        nuevoJugador.setTurnoActual(true);
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
    public int getCantJugadores() throws RemoteException {
        return jugadores.size();
    }
}
