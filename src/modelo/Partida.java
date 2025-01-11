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
    private final Serializador srlRanking = new Serializador("src/serializacion/ranking.dat");
    protected static final int BARAJAS_HASTA_4_JUGADORES = 2;
    protected static final int BARAJAS_MAS_4_JUGADORES = 3;
    private int numRonda = 1;
    private Carta pozo;
    private ArrayList<Carta> mazo;
    private boolean rondaEmpezada = false;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private ArrayList<Integer> jugadoresQuePuedenRobarConCastigo;
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

    public boolean cortar(int numJugador) throws RemoteException {
        boolean corte = false;
        int sizeMano = jugadores.get(numJugador).getMano().size();
        if (sizeMano <= 1) {
            if (sizeMano == 1) {
                tirarAlPozo(numJugador, 0);
            } else {
                jugadores.get(numJugador).setTurnoActual(false);
            }
            setPuedeBajar(numJugador, 0);
            corte = true;
        }
        return corte;
    }

    public void crearPartida(String vista, int observadorIndex, int cantJugadoresDeseada) throws RemoteException {
        crearYAgregarJugador(vista, observadorIndex);
        //partida.notificarObservadores(NOTIFICACION_NUEVO_JUGADOR);
        setCantJugadoresDeseada(cantJugadoresDeseada);
        setEnCurso();
        //partida.notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
        setNumTurno(observadorIndex);
        setNumJugadorQueEmpezoPartida(observadorIndex);
        numJugadorQueEmpiezaRonda = observadorIndex;
        notificarObservador(observadorIndex, NOTIFICACION_NUEVA_PARTIDA_PROPIO);
    }

    private ArrayList<ifCarta> getMano(int numJugador) throws RemoteException {
        ArrayList<Carta> mano = jugadores.get(numJugador).getMano();
        return new ArrayList<>(mano);
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
    public int getNumRonda() throws RemoteException {
        return numRonda;
    }


    @Override
    public Carta getPozo() throws RemoteException {
        return pozo;
    }

    @Override
    public Jugador getJugador(String nombreJugador)  throws RemoteException{
        return PartidaJugadores.getJugador(jugadores, nombreJugador);
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
        pozo = Mazo.sacarPrimeraDelMazo(mazo);
    }

    public void robarDelMazo() throws RemoteException {
        PartidaJugadores.robarDelMazo(jugadores, numTurno, mazo);
        actualizarMano(numTurno);
        if (pozo!=null) {
            notificarRoboConCastigo(numTurno);
        }
    }

    public void setRoboDelMazo(int i, boolean b) throws RemoteException{
        jugadores.get(i).setRoboDelMazo(b);
    }

    public void robarDelPozo() throws RemoteException {
        jugadores.get(numTurno).getMano().add(pozo);
        pozo = null;
        actualizarMano(numTurno);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
    }

    public void notificarRoboConCastigo(int numJugador) throws RemoteException {
        setRoboDelMazo(numTurno, false);
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
        PartidaJugadores.robarDelMazo(jugadores,num,mazo);
        jugadores.get(num).getMano().add(pozo);
        pozo = null;
        actualizarMano(num);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
    }

    public void empezarRonda() throws RemoteException {
        if (!rondaEmpezada) {
            rondaEmpezada = true;
            crearMazo();
            repartirCartas();
            iniciarPozo();
            numTurno = numJugadorQueEmpiezaRonda;
            notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
            actualizarManoJugadores();
            notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
            notificarObservadores(NOTIFICACION_ACTUALIZAR_JUEGOS);
        }
    }

    private void actualizarManoJugadores() throws RemoteException {
        int i = numTurno;
        int n = 0;
        while (n < jugadores.size()) {
            actualizarMano(i);
            i++;
            if (i>jugadores.size()-1) {
                i = 0;
            }
            n++;
        }
    }

    public void setTurnoJugador(int numJugador, boolean valor) throws RemoteException {
        jugadores.get(numJugador).setTurnoActual(valor);
    }

    public void tirarAlPozo(int numJugador, int cartaATirar) throws RemoteException {
        pozo = (jugadores.get(numJugador).getMano().remove(cartaATirar));
        jugadores.get(numJugador).setTurnoActual(false);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
    }

    public void finTurno() throws RemoteException {
        if (!isFinRonda()) {
            incTurno();
        }
    }

    public boolean isFinRonda() throws RemoteException {
        boolean fin = false;
        if (getMano(numTurno).isEmpty()) {
            fin = true;
            finRonda(numTurno);
            if (numRonda >= TOTAL_RONDAS) {
                finPartida();
            }
        }
        return fin;
    }

    private void finRonda(int numJugador) throws RemoteException {
        rondaEmpezada = false;
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

    private void finPartida() throws RemoteException {
        setEnCurso();
        determinarGanador(); //al finalizar las rondas
        serializarGanador();
        notificarObservadores(NOTIFICACION_GANADOR);
        //lo siguiente es para poder seguir jugando otras partidas
        removerObservadores();
    }

    public void incPuedeBajar(int numJugador) throws RemoteException {
        jugadores.get(numJugador).incrementarPuedeBajar();
    }

    public void actualizarMano(int numJugador) throws RemoteException {
        Object[] o = new Object[2];
        o[0] = NOTIFICACION_ACTUALIZAR_MANO;
        o[1] = jugadores.get(numJugador).getMano();
        notificarObservador(numJugador, o);
    }

    public void moverCartaEnMano(int numJugador, int i, int i1) throws RemoteException {
        PartidaJugadores.moverCartaEnMano(jugadores, numJugador, i, i1);
    }

    public void bajarJuego(int numJugador, int[] cartasABajar, int tipoJuego)
            throws RemoteException {
        PartidaJugadores.bajarJuego(jugadores, numJugador, cartasABajar, tipoJuego);
        actualizarMano(numJugador);
    }

    public void setPuedeBajar(int numJugador, int i) throws RemoteException {
        jugadores.get(numJugador).setPuedeBajar(i);
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
    public void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException {
        numJugadorQueEmpiezaRonda++;
        if (numJugadorQueEmpiezaRonda >= jugadores.size()) {
            numJugadorQueEmpiezaRonda = 0;
        }
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
        return pozo == null;
    }

    @Override
    public void crearYAgregarJugador(String nombre, int numObservador) throws RemoteException {
        Jugador nuevoJugador = new Jugador(nombre);
        nuevoJugador.setNumeroJugador(numObservador);
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
