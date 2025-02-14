package modelo;

import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import static modelo.Eventos.*;

public class Partida extends ObservableRemoto implements Serializable, ifPartida {
    private static final int TOTAL_RONDAS = 7; //cambiar para probar
    private final Serializador srlRanking = new Serializador("src/serializacion/ranking.dat");
    private final Serializador srlPartidas = new Serializador("src/serializacion/partidas.dat");
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private int numRonda = 1;
    private Carta pozo;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private ArrayList<Integer> jugadoresQuePuedenRobarConCastigo;
    private int numJugadorCorte;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private int numJugadorQueEmpezoPartida;
    static final int PUNTOS_FIGURA = 10;
    static final int PUNTOS_AS = 20;
    static final int PUNTOS_COMODIN = 50;
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
        numJugadorQueEmpiezaRonda = partidaCargada.numJugadorQueEmpiezaRonda;
        numTurno = partidaCargada.numTurno;
        jugadoresQuePuedenRobarConCastigo = partidaCargada.jugadoresQuePuedenRobarConCastigo;
        numJugadorCorte = partidaCargada.numJugadorCorte;
        cantJugadoresDeseada = partidaCargada.cantJugadoresDeseada;
        enCurso = partidaCargada.enCurso;
        numJugadorQueEmpezoPartida = partidaCargada.numJugadorQueEmpezoPartida;
    }

    public ArrayList<String> getNombreJugadores() throws RemoteException{
        return PartidaJugadores.getNombreJugadores(jugadores);
    }

    public boolean agregarNombreElegido(String nombre) throws RemoteException {
        boolean agregado = PartidaJugadores.agregarNombreElegido(nombre);
        if (PartidaJugadores.getNombresElegidos().size()==jugadores.size()) {
            notificacionesComienzoRonda();
            notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
        }
        return agregado;
    }

    private void serializarGanador() throws RemoteException {
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

    public ArrayList<Integer> getJugadoresQuePuedenRobarConCastigo()
            throws RemoteException{
        return jugadoresQuePuedenRobarConCastigo;
    }

    public Eventos comprobarPosibleCorte(int numJugador) throws RemoteException{
        Eventos puedeCortar = NO_PUEDE_CORTAR;
        int cartasEnMano = jugadores.get(numJugador).getMano().getSize();
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

    public ArrayList<ArrayList<ifCarta>> getJuegos(int numJugador) throws RemoteException {
        ArrayList<ArrayList<ifCarta>> a = new ArrayList<>();
        for (ArrayList<Carta> listaCartas : jugadores.get(numJugador).getJuegos()) {
            ArrayList<ifCarta> listaIfCartas = new ArrayList<>(listaCartas);
            a.add(listaIfCartas);
        }
        return a;
    }

    public void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException {
        jugadores.get(numJugador).setNumeroJugador(nuevoNumero);
    }

    public boolean comprobarAcomodarAjeno(int numJ, int numJAcomodar,
                           int iCarta, int iJuego) throws RemoteException {
        Carta c = jugadores.get(numJ).getMano().getCarta(iCarta);
        return JuegoBajado.acomodarCarta(jugadores.get(numJAcomodar).juegos,c,iJuego,numRonda);
    }

    public boolean comprobarAcomodarCartaPropio(int numJugador, int numCarta, int numJuego)
            throws RemoteException {
        Carta c = jugadores.get(numJugador).getMano().getCarta(numCarta);
        return JuegoBajado.acomodarCarta(jugadores.get(numJugador).juegos,c,numJuego,numRonda);
    }

    public void acomodarAjeno(int numJ, int numJAcomodar,
                             int iCarta, int iJuego) throws RemoteException {
        Carta c = jugadores.get(numJ).getMano().removeCarta(iCarta);
        jugadores.get(numJAcomodar).getJuegos().get(iJuego).add(c);
    }

    public void acomodarPropio(int numJ,int iCarta, int iJuego) throws RemoteException {
        Carta c = jugadores.get(numJ).getMano().removeCarta(iCarta);
        jugadores.get(numJ).getJuegos().get(iJuego).add(c);
    }

    public boolean comprobarBajarse(int numJugador, int[] cartasABajar)
            throws RemoteException{
        boolean puedeBajarse = false;
        int tipoJuego = Comprobar.comprobarJuego(jugadores.get(numJugador).getMano()
                .seleccionarCartasABajar(cartasABajar),numRonda);
        if(tipoJuego != Comprobar.JUEGO_INVALIDO) {
            puedeBajarse = true;
            bajarJuego(numJugador, cartasABajar, tipoJuego);
        }
        return puedeBajarse;
    }

    public boolean cortar(int numJugador) throws RemoteException {
        boolean corte = false;
        int sizeMano = jugadores.get(numJugador).getMano().getSize();
        if (sizeMano <= 1) {
            if (sizeMano == 1) {
                tirarAlPozo(numJugador, 0);
            } else {
                jugadores.get(numJugador).setTurnoActual(false);
            }
            jugadores.get(numJugador).setPuedeBajar(0);
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
        this.cantJugadoresDeseada = cantJugadoresDeseada;
        enCurso = true;
        setNumTurno(observadorIndex);
        numJugadorQueEmpezoPartida = observadorIndex;
        numJugadorQueEmpiezaRonda = observadorIndex;
        notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
    }

    public int getObservadorIndex(IObservadorRemoto o) throws RemoteException {
        return getObservadores().indexOf(o);
    }

    public Serializador getRanking() throws RemoteException {
        return srlRanking;
    }

    public ArrayList<Jugador> getJugadores() throws RemoteException {
        return jugadores;
    }

    public int getNumRonda() throws RemoteException {
        return numRonda;
    }

    public Carta getPozo() throws RemoteException {
        return pozo;
    }

    public Jugador getJugador(String nombreJugador)  throws RemoteException{
        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombreJugador)) return j;
        }
        return null;
    }

    public void robarDelMazo() throws RemoteException {
        jugadores.get(numTurno).getMano().agregarCarta(Mazo.getInstancia().sacarPrimeraDelMazo()); //robo del mazo
        actualizarMano(numTurno);
        if (pozo!=null) {
            notificarRoboConCastigo(numTurno);
        }
    }

    public void robarDelPozo() throws RemoteException {
        jugadores.get(numTurno).getMano().agregarCarta(pozo);
        pozo = null;
        actualizarMano(numTurno);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
    }

    private void notificarRoboConCastigo(int numJugador) throws RemoteException {
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
        setJugadoresQuePuedenRobarConCastigo(); //se termina de borrar si no se borró desde el ctrl
    }

    public void setJugadoresQuePuedenRobarConCastigo() throws RemoteException{
        jugadoresQuePuedenRobarConCastigo = new ArrayList<>();
    }

    public void robarConCastigo() throws RemoteException {
        int num = getNumJugadorRoboCastigo();
        jugadores.get(num).getMano().agregarCarta(pozo);
        pozo = null;
        jugadores.get(num).getMano().agregarCarta(Mazo.getInstancia().sacarPrimeraDelMazo()); //robo del mazo
        actualizarMano(num);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
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
        pozo = (jugadores.get(numJugador).getMano().removeCarta(cartaATirar));
        jugadores.get(numJugador).setTurnoActual(false);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        actualizarMano(numJugador);
    }

    public void finTurno() throws RemoteException {
        if (jugadores.get(numTurno).getMano().getSize()==0) {
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

    public void empezarRonda() throws RemoteException {
        Mazo.getInstancia().iniciarMazo(jugadores.size());
        //PartidaJugadores.repartirCartasPrueba(jugadores,numRonda,mazo); //prueba
        Mazo.getInstancia().repartirCartas(jugadores,numRonda);
        pozo = Mazo.getInstancia().sacarPrimeraDelMazo();
        numTurno = numJugadorQueEmpiezaRonda;
        notificacionesComienzoRonda();
    }

    private void notificacionesComienzoRonda() throws RemoteException {
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
            Puntuacion.getInstancia().sumarPuntos(j);
            j.resetFinRonda();
        }
        notificarObservadores(NOTIFICACION_PUNTOS);
        notificarObservadores(NOTIFICACION_CORTE_RONDA);
    }

    private void finPartida() throws RemoteException {
        enCurso = false;
        Puntuacion.getInstancia().determinarGanador();
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
        Mano mano = jugadores.get(numJugador).getMano();
        notificarObservador(numJugador, mano);
    }

    public void moverCartaEnMano(int numJugador, int i, int i1) throws RemoteException {
        jugadores.get(numJugador).getMano().moverCartaEnMano(i,i1);
        actualizarMano(numJugador);
    }

    private void bajarJuego(int numJugador, int[] cartasABajar, int tipoJuego)
            throws RemoteException {
        jugadores.get(numJugador).bajarJuego(cartasABajar, tipoJuego);
        incPuedeBajar(numJugador);
        actualizarMano(numJugador);
    }

    public int getPuedeBajar(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getPuedeBajar();
    }

    private int getTriosBajados(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getTriosBajados();
    }

    public int[] comprobarQueFaltaParaCortar(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).comprobarQueFaltaParaCortar(numRonda);
    }

    private int getEscalerasBajadas(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getEscalerasBajadas();
    }

    public String getNombreJugador(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getNombre();
    }

    private void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException {
        numJugadorQueEmpiezaRonda++;
        if (numJugadorQueEmpiezaRonda >= jugadores.size()) {
            numJugadorQueEmpiezaRonda = 0;
        }
    }

    public int getNumTurno() throws RemoteException {
        return numTurno;
    }

    private void setNumTurno(int numTurno) throws RemoteException {
        this.numTurno = numTurno;
    }

    public int getNumJugadorRoboCastigo()  throws RemoteException {
        return jugadoresQuePuedenRobarConCastigo.get(0);
    }

    public int getNumJugadorCorte()  throws RemoteException {
        return numJugadorCorte;
    }

    private void setNumJugadorCorte(int numJugadorCorte)  throws RemoteException {
        this.numJugadorCorte = numJugadorCorte;
    }

    public int[] getPuntosJugadores() throws RemoteException {
        return Puntuacion.getInstancia().getPuntosJugadores();
    }

    public Jugador getGanador()  throws RemoteException{
        Jugador ganador = null;
        for (Jugador j : jugadores) {
            if (j.isGanador()) ganador = j;
        }
        return ganador;
    }

    private void incTurno() throws RemoteException{
        numTurno++;
        if (numTurno>jugadores.size()-1) {
            numTurno = 0;
        }
    }

    public void crearYAgregarJugador(String nombre, int numObservador) throws RemoteException {
        Jugador nuevoJugador = new Jugador(nombre);
        nuevoJugador.setNumeroJugador(numObservador);
        nuevoJugador.sumarPartida(this);
        jugadores.add(nuevoJugador);
    }

    public int getCantJugadoresDeseada() throws RemoteException {
        return cantJugadoresDeseada;
    }

    public void ponerJugadoresEnOrden() throws RemoteException {
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
        jugadores = jugadoresNuevo;
    }
}