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
    protected static final int BARAJAS_HASTA_4_JUGADORES = 2;
    protected static final int BARAJAS_MAS_4_JUGADORES = 3;
    private static final int TOTAL_RONDAS = 7;
    @Serial
    private static final long serialVersionUID = 1L;
    private final Serializador srlRanking = new Serializador("src/serializacion/ranking.dat");
    private final Serializador srlPartidas = new Serializador("src/serializacion/partidas.dat");
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private int numRonda = 1;
    private Carta pozo;
    private ArrayList<Carta> mazo;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private ArrayList<Integer> jugadoresQuePuedenRobarConCastigo;
    private int numJugadorCorte;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private int numJugadorQueEmpezoPartida;
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

    private static void setInstancia(Partida instancia) {
        Partida.instancia = instancia;
    }

    public void guardarPartida() throws RemoteException {
        srlPartidas.writeOneObject(this);
        System.out.println("guardada partida");
    }

    public boolean cargarPartida() throws RemoteException{
        Object partidaCargada = srlPartidas.readFirstObject();
        if (partidaCargada != null) {
            setInstancia((Partida) partidaCargada);
            sincronizarCon((Partida) partidaCargada);
            return true;
        }
        return false;
    }

    private void sincronizarCon(Partida partidaCargada) {
        jugadores = partidaCargada.jugadores;
        numRonda = partidaCargada.numRonda;
        pozo = partidaCargada.pozo;
        mazo = partidaCargada.mazo;
        numJugadorQueEmpiezaRonda = partidaCargada.numJugadorQueEmpiezaRonda;
        numTurno = partidaCargada.numTurno;
        jugadoresQuePuedenRobarConCastigo = partidaCargada.jugadoresQuePuedenRobarConCastigo;
        numJugadorCorte = partidaCargada.numJugadorCorte;
        cantJugadoresDeseada = partidaCargada.cantJugadoresDeseada;
        enCurso = partidaCargada.enCurso;
        numJugadorQueEmpezoPartida = partidaCargada.numJugadorQueEmpezoPartida;
    }


    @Override
    public ArrayList<String> getNombreJugadores() throws RemoteException{
        return PartidaJugadores.getNombreJugadores(jugadores);
    }

    @Override
    public void agregarNombreElegido(String nombre) throws RemoteException {
        PartidaJugadores.agregarNombreElegido(nombre);
    }

    @Override
    public ArrayList<String> getNombresElegidos() throws RemoteException {
        return PartidaJugadores.getNombresElegidos();
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

    public ArrayList<Integer> getJugadoresQuePuedenRobarConCastigo() throws RemoteException{
        return jugadoresQuePuedenRobarConCastigo;
    }

    @Override
    public Eventos comprobarPosibleCorte(int numJugador) throws RemoteException{
        Eventos puedeCortar = NO_PUEDE_CORTAR;
        int cartasEnMano = jugadores.get(numJugador).getMano().size();
        if(Comprobar.comprobarPosibleCorte(numRonda,getTriosBajados(numJugador),
                getEscalerasBajadas(numJugador))) {
            if (cartasEnMano == 1 || cartasEnMano == 0) {
                puedeCortar = PUEDE_CORTAR;
            } else {
                puedeCortar = SOBRAN_CARTAS;
            }
        }
        return puedeCortar;
    }

    public boolean isTurnoActual(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).isTurnoActual();
    }

    public boolean isEnCurso() throws RemoteException {
        return enCurso;
    }

    public ArrayList<ArrayList<Carta>> getJuegos(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getJuegos();
    }

    public void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException {
        jugadores.get(numJugador).setNumeroJugador(nuevoNumero);
    }

    public boolean comprobarAcomodarCartaAjeno(int numJugador, int numJugadorAcomodar,
                           int numCarta, int numJuego) throws RemoteException {
        Carta c = jugadores.get(numJugador).getMano().get(numCarta);
        return JuegoBajado.acomodarCarta(jugadores.get(numJugadorAcomodar).juegos,c,numJuego,numRonda);
    }

    public boolean comprobarAcomodarCartaPropio(int numJugador, int numCarta, int numJuego)
            throws RemoteException {
        Carta c = jugadores.get(numJugador).getMano().get(numCarta);
        return JuegoBajado.acomodarCarta(jugadores.get(numJugador).juegos,c,numJuego,numRonda);
    }

    public void acomodarEnJuegoAjeno(int numJugador, int numJugadorAcomodar,
                             int iCarta, int numJuego) throws RemoteException {
        Carta c = jugadores.get(numJugador).getMano().remove(iCarta);
        jugadores.get(numJugadorAcomodar).getJuegos().get(numJuego).add(c);
    }

    public void acomodarEnJuegoPropio(int numJugador,
                                     int iCarta, int numJuego) throws RemoteException {
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

    public int getNumJugador(String nombreJugador) throws RemoteException {
        int i;
        for (i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getNombre().equals(nombreJugador)) break;
        }
        return i;
    }

    public void crearPartida(int observadorIndex, int cantJugadoresDeseada)
            throws RemoteException {
        setCantJugadoresDeseada(cantJugadoresDeseada);
        setEnCurso(true);
        setNumTurno(observadorIndex);
        setNumJugadorQueEmpezoPartida(observadorIndex);
        numJugadorQueEmpiezaRonda = observadorIndex;
        notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
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
        setJugadoresQuePuedenRobarConCastigo();
    }

    public void setJugadoresQuePuedenRobarConCastigo() throws RemoteException {
        jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
    }

    public void robarConCastigo()
            throws RemoteException {
        int num = getNumJugadorRoboCastigo();
        jugadores.get(num).getMano().add(pozo);
        pozo = null;
        PartidaJugadores.robarDelMazo(jugadores,num,mazo);
        System.out.println("robo castigo");
        actualizarMano(num);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
    }

    public void actualizarManoJugadores() throws RemoteException {
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
        actualizarMano(numJugador);
    }

    public void finTurno() throws RemoteException {
        if (isFinRonda()) {
            finRonda(numTurno);
            if (numRonda > TOTAL_RONDAS) {
                finPartida();
            } else {
                empezarRonda();
            }
        } else {
            incTurno();
        }
    }

    private boolean isFinRonda() throws RemoteException {
        return getMano(numTurno).isEmpty();
    }

    public void empezarRonda() throws RemoteException {
        mazo = Mazo.mezclarCartas(Mazo.iniciarMazo(Mazo.determinarNumBarajas(jugadores)));
        //PartidaJugadores.repartirCartasPrueba(jugadores,numRonda,mazo);
        PartidaJugadores.repartirCartas(jugadores, numRonda, mazo);
        pozo = Mazo.sacarPrimeraDelMazo(mazo);
        numTurno = numJugadorQueEmpiezaRonda;
        System.out.println(jugadores.get(0).getNombre());
        System.out.println(jugadores.get(1).getNombre());
        notificarObservadores(NOTIFICACION_PUNTOS);
        actualizarManoJugadores();
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_JUEGOS);
        notificarObservadores(NOTIFICACION_NUMERO_JUGADOR);
        notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
    }

    private void finRonda(int numJugador) throws RemoteException {
        setNumJugadorCorte(numJugador);
        incrementarNumJugadorQueEmpiezaRonda();
        numRonda++;
        for (Jugador j : jugadores) {
            j.setPuntosPartida(PartidaJugadores.sumarPuntos(j));
        }
        PartidaJugadores.resetearJuegosJugadores(jugadores);
        Mano.resetearMano(jugadores);
        notificarObservadores(NOTIFICACION_PUNTOS);
        notificarObservadores(NOTIFICACION_CORTE_RONDA);
    }

    private void finPartida() throws RemoteException {
        setEnCurso(false);
        determinarGanador();
        serializarGanador();
        notificarObservadores(NOTIFICACION_GANADOR);
        notificarObservadores(NOTIFICACION_FIN_PARTIDA);
        jugadores = new ArrayList<>();
        numRonda = 1;
    }

    public void incPuedeBajar(int numJugador) throws RemoteException {
        jugadores.get(numJugador).incrementarPuedeBajar();
    }

    public void actualizarMano(int numJugador) throws RemoteException {
        notificarObservador(numJugador, new NotificacionActualizarMano(
                jugadores.get(numJugador).getMano()));
    }

    public void moverCartaEnMano(int numJugador, int i, int i1) throws RemoteException {
        jugadores.get(numJugador).moverCartaEnMano(i,i1);
        actualizarMano(numJugador);
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
    }

    @Override
    public void crearYAgregarJugador(String nombre, int numObservador) throws RemoteException {
        Jugador nuevoJugador = new Jugador(nombre);
        nuevoJugador.setNumeroJugador(numObservador);
        nuevoJugador.sumarPartida(this);
        jugadores.add(nuevoJugador);
    }

    @Override
    public void setEnCurso(boolean enCurso) throws RemoteException {
        this.enCurso = enCurso;
    }

    @Override
    public void setNumJugadorQueEmpezoPartida(int numJugadorQueEmpezoPartida)
            throws RemoteException {
        this.numJugadorQueEmpezoPartida = numJugadorQueEmpezoPartida;
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
}
