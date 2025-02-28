package modelo;

import excepciones.FaltanJugadoresException;
import excepciones.JugadorDesconectadoException;
import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static modelo.EntradasUsuario.*;
import static modelo.Eventos.*;

public class Partida extends ObservableRemoto implements Serializable, ifPartida {
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private final Serializador srlRanking =
            new Serializador("src/serializacion/ranking.dat");
    private final Serializador srlPartidas =
            new Serializador("src/serializacion/partidas.dat");
    private ArrayList<String> nombresElegidos;
    private Ronda ronda = Ronda.getInstancia();
    private Puntuacion puntuacion;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private static Partida instancia;

    private Partida() {}

    public static Partida getInstancia() throws RemoteException {
        if (Partida.instancia == null) {
            Partida.instancia = new Partida();
        }
        return Partida.instancia;
    }

    private static void setInstancia(Partida instancia) {
        Partida.instancia = instancia;
    }

    private void sincronizarCon(Partida partidaCargada) {
        jugadores = partidaCargada.jugadores;
        ronda = partidaCargada.ronda;
        puntuacion = partidaCargada.puntuacion;
        numJugadorQueEmpiezaRonda = partidaCargada.numJugadorQueEmpiezaRonda;
        numTurno = partidaCargada.numTurno;
        cantJugadoresDeseada = partidaCargada.cantJugadoresDeseada;
        enCurso = true;
        nombresElegidos = new ArrayList<>();
    }

    private void serializarGanador() throws RemoteException {
        Object guardar = puntuacion.getGanador().nombre + " --- puntos: " +
                puntuacion.getPuntosGanador();
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

    private void notificarRoboConCastigo(int numJugador) throws RemoteException {
        for (int i = 0; i < jugadores.size()-1; i++) {
            numJugador++; //al principio ya que el jugador del turno no lo tiene en cuenta
            if (numJugador > jugadores.size() - 1) numJugador = 0;
            if (jugadores.get(numJugador).juegos.isEmpty()) {
                RoboCastigo.getInstancia().getJugadores().add(numJugador);
            }
        }

        if (!RoboCastigo.getInstancia().getJugadores().isEmpty()) {
            notificarObservador(numTurno, NOTIFICACION_ESPERA);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    RoboCastigo.getInstancia().setNumJugadorRoboCastigo(RoboCastigo.getInstancia().getJugadores().get(0));
                    notificarObservadores(RoboCastigo.getInstancia().getJugadores(),
                            NOTIFICACION_ROBO_CASTIGO);
                    RoboCastigo.getInstancia().setJugadoresQuePuedenRobarConCastigo(); //se termina de borrar si no se borró desde el ctrl
                    notificarObservador(numTurno, NOTIFICACION_TERMINA_ESPERA);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            executorService.shutdown();
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

    private void notificacionesComienzoRonda() throws RemoteException {
        notificarObservadores(NOTIFICACION_PUNTOS);
        notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
        notificarObservadores(NOTIFICACION_POZO);
        actualizarManoJugadores();
    }

    private void finRonda() throws RemoteException {
        ronda.fin();
        for (Jugador j : jugadores) {
            puntuacion.sumarPuntos(j);
            j.resetFinRonda();
        }
        notificarObservadores(NOTIFICACION_PUNTOS);
        notificarObservadores(NOTIFICACION_CORTE_RONDA);
    }

    private void finPartida() throws RemoteException {
        enCurso = false;
        puntuacion.determinarGanador();
        serializarGanador();
        notificarObservadores(NOTIFICACION_GANADOR);
        notificarObservadores(NOTIFICACION_FIN_PARTIDA);
        jugadores = new ArrayList<>();
        ronda.finPartida();
    }

    public void guardar(int numJugadorQueLlamo) throws RemoteException {
        enCurso = false;
        srlPartidas.writeOneObject(this);
        if (numJugadorQueLlamo!=numTurno) {
            notificarPartidaGuardada(); //le avisa a todas las vistas menos la del turno actual
        } else {
            notificarObservadores(NOTIFICACION_PARTIDA_GUARDADA);
        }

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

    @Override
    public void incNumJugadorRoboCastigo() throws RemoteException {
        RoboCastigo.getInstancia().incNumJugadorRoboCastigo();
    }

    @Override
    public void comprobarEmpezarPartida() throws RemoteException {
        if (nombresElegidos.size()==jugadores.size()) {
            ponerJugadoresEnOrden();
            notificarObservadores(NOTIFICACION_BAJO_JUEGO); //muestra los juegos bajados si había
            notificacionesComienzoRonda();
            notificarObservadores(NOTIFICACION_NUMERO_JUGADOR);
            notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
        }
    }

    @Override
    public void robo(String eleccion) throws RemoteException,
            JugadorDesconectadoException {
        actualizarManoJugadores();
        if (Objects.equals(eleccion, ROBAR_DEL_MAZO) ||
            Objects.equals(eleccion,ROBO_DEL_MAZO)) {
            robarDelMazo();
        } else if (Objects.equals(eleccion, ROBAR_DEL_POZO) ||
                    Objects.equals(eleccion,ROBO_DEL_POZO)) {
            robarDelPozo();
        }
    }

    private boolean puedeBajar() throws RemoteException {
        boolean bajar = true;
        int cantVecesQueBajo = getCantJuegos(numTurno);
        if ((ronda.getNumRonda() <= 3 && cantVecesQueBajo > 2) ||
                (ronda.getNumRonda()>3 && cantVecesQueBajo > 3)) {
            bajar = false;
        }
        return bajar;
    }

    public boolean bajarse(int[] indicesCartas)
            throws RemoteException, JugadorDesconectadoException {
        boolean bajar = false;
        if (puedeBajar()) {
            bajar = bajarJuego(indicesCartas);
            if (bajar){
                notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                if(jugadores.get(numTurno).comprobarPosibleCorte()) {
                    cortar();
                }
            }
        }
        return bajar;
    }

    public ArrayList<ArrayList<ArrayList<Carta>>> enviarJuegosEnMesa() throws RemoteException{
        ArrayList<ArrayList<ArrayList<Carta>>> juegos = new ArrayList<>();
        for (Jugador j : jugadores) {
            ArrayList<ArrayList<Carta>> juegosJugador = new ArrayList<>();
            for (JuegoBajado juego : j.juegos) {
                juegosJugador.add(juego.juego);
            }
            juegos.add(juegosJugador);
        }
        return juegos;
    }

    @Override
    public String getTurnoDe() throws RemoteException {
        jugadores.get(numTurno).setTurnoActual(true);
        return getNombreJugador(numTurno);
    }

    @Override
    public void setCantJugadoresDeseada(int cant) throws RemoteException {
        if (cant<2 || cant>6) {
            throw new IllegalArgumentException("Debe haber al menos 2 jugadores y 6 como máximo");
        }
        cantJugadoresDeseada = cant;
    }

    public boolean hayRepetidos(int[] array) throws RemoteException {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] == array[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean acomodar(int cartaAcomodar, int iJuego, int numJugador) throws RemoteException {
        Carta c = jugadores.get(numTurno).getMano().get().get(cartaAcomodar);
        if (jugadores.get(numJugador).juegos.get(iJuego).acomodarCarta(c)) {
            jugadores.get(numTurno).getMano().removeCarta(cartaAcomodar);
            jugadores.get(numJugador).juegos.get(iJuego).juego.add(c);
            notificarObservadores(NOTIFICACION_BAJO_JUEGO);
            actualizarMano(numJugador);
            return true;
        }
        return false;
    }

    public ArrayList<String> getNombreJugadores() throws RemoteException{
        ArrayList<String> nombreJugadores = new ArrayList<>();
        for (Jugador j : jugadores) {
            nombreJugadores.add(j.getNombre());
        }
        return nombreJugadores;    }

    public boolean agregarNombreElegido(String nombre) throws RemoteException {
        boolean agregar = !nombresElegidos.contains(nombre);
        if (agregar) nombresElegidos.add(nombre);
        return agregar;
    }

    public String getGanador() throws RemoteException {
        return puntuacion.getGanador().nombre;
    }

    public boolean isTurnoActual(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).isTurnoActual();
    }

    public boolean isEnCurso() throws RemoteException {
        return enCurso;
    }

    public ArrayList<ArrayList<Carta>> getJuegos(int numJugador) throws RemoteException {
        ArrayList<ArrayList<Carta>> a = new ArrayList<>();
        for (JuegoBajado juego : jugadores.get(numJugador).juegos) {
            a.add(new ArrayList<>(juego.juego));
        }
        return a;
    }

    public void setNumeroJugador(int numJugador, int nuevoNumero) throws RemoteException {
        jugadores.get(numJugador).setNumeroJugador(nuevoNumero);
    }

    @Override
    public ArrayList<Integer> getJugadoresQuePuedenRobarConCastigo() throws RemoteException {
        return RoboCastigo.getInstancia().getJugadores();
    }

    private boolean bajarJuego(int[] cartasABajar)
            throws RemoteException{
        ArrayList<Carta> cartas = jugadores.get(numTurno).getMano().seleccionarCartasABajar(cartasABajar);
        JuegoBajado juego = JuegoBajado.crearInstancia(cartas);
        if (juego!=null) {
            Jugador j = jugadores.get(numTurno);
            j.juegos.add(juego);
            j.getMano().eliminarDeLaMano(j.juegos.get(j.juegos.size()-1).juego);
            actualizarMano(numTurno);
        }
        return juego!=null;
    }

    private void cortar() throws RemoteException, JugadorDesconectadoException {
        int sizeMano = jugadores.get(numTurno).getMano().getSize();
        if (sizeMano <= 1) {
            if (sizeMano == 1) {
                tirarAlPozo(0);
            } else {
                jugadores.get(numTurno).setTurnoActual(false);
            }
        }
        finTurno();
    }

    public int getNumJugador(String nombreJugador) throws RemoteException {
        int i;
        for (i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getNombre().equals(nombreJugador)) break;
        }
        return i;
    }

    public void inicializarPartida(int observadorIndex) throws RemoteException {
        enCurso = true;
        numJugadorQueEmpiezaRonda = observadorIndex;
        numTurno = numJugadorQueEmpiezaRonda;
        notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
    }

    public void jugarPartida() throws RemoteException,
            FaltanJugadoresException {
        if (jugadores.size() == cantJugadoresDeseada) {
            notificarObservadores(NOTIFICACION_AGREGAR_OBSERVADOR);
            ponerJugadoresEnOrden();
            notificarObservadores(NOTIFICACION_NUMERO_JUGADOR);
            puntuacion = new Puntuacion(jugadores);
            empezarRonda();
            notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
        } else {
            throw new FaltanJugadoresException();
        }
    }

    public int getObservadorIndex(IObservadorRemoto o) throws RemoteException {
        return getObservadores().indexOf(o);
    }

    public Serializador getRanking() throws RemoteException {
        return srlRanking;
    }

    public int getCantJugadores() throws RemoteException {
        return jugadores.size();
    }

    public int getNumRonda() throws RemoteException {
        return ronda.getNumRonda();
    }

    public ifCarta getPozo() throws RemoteException {
        return ronda.getPozo();
    }

    public int getNumeroJugador(String nombreJugador)  throws RemoteException{
        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombreJugador)) return j.getNumeroJugador();
        }
        return -1;
    }

    public void robarDelMazo() throws RemoteException,
            JugadorDesconectadoException {
        jugadores.get(numTurno).getMano().agregarCarta(ronda.sacarPrimeraDelMazo()); //robo del mazo
        actualizarMano(numTurno);
        if (ronda.getPozo()!=null) {
            if (enCurso) {
                notificarRoboConCastigo(numTurno);
            } else {
                throw new JugadorDesconectadoException();
            }
        }
    }

    public void robarDelPozo() throws RemoteException {
        jugadores.get(numTurno).getMano().agregarCarta(ronda.robarDelPozo());
        actualizarMano(numTurno);
        notificarObservadores(NOTIFICACION_POZO);
    }

    @Override
    public int getCantJuegos(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).juegos.size();
    }

    @Override
    public void setJugadoresQuePuedenRobarConCastigo() throws RemoteException {
        RoboCastigo.getInstancia().setJugadoresQuePuedenRobarConCastigo();
    }

    @Override
    public void notificarPartidaGuardada() throws RemoteException {
        ArrayList<Integer> avisar = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.getNumeroJugador() != numTurno)
                avisar.add(j.getNumeroJugador());
        }
        notificarObservadores(avisar, NOTIFICACION_PARTIDA_GUARDADA);
    }

    public void robarConCastigo() throws RemoteException {
        Mano jugadorRoboMano = jugadores.get(getNumJugadorRoboCastigo()).getMano();
        jugadorRoboMano.agregarCarta(ronda.robarDelPozo());
        jugadorRoboMano.agregarCarta(ronda.sacarPrimeraDelMazo()); //robo del mazo
        actualizarMano(getNumJugadorRoboCastigo());
        notificarObservadores(NOTIFICACION_POZO);
        notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
    }

    public void tirarAlPozo(int cartaATirar)
            throws RemoteException, JugadorDesconectadoException {
        ronda.setPozo(jugadores.get(numTurno).getMano().removeCarta(cartaATirar));
        jugadores.get(numTurno).setTurnoActual(false);
        notificarObservadores(NOTIFICACION_POZO);
        actualizarMano(numTurno);
        finTurno();
    }

    public void finTurno() throws RemoteException, JugadorDesconectadoException {
        final int TOTAL_RONDAS = 7;
        if (enCurso) {
            //si termina la ronda (porque corta) o sigue
            if (jugadores.get(numTurno).getMano().getSize()==0) {
                finRonda();
                //se incrementa la ronda y se comprueba si fue la ultima
                if (ronda.getNumRonda()> TOTAL_RONDAS) {
                    finPartida();
                } else {
                    notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                    empezarRonda();
                    notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
                }
            } else {
                numTurno++;
                if (numTurno>jugadores.size()-1) {
                    numTurno = 0;
                }
                actualizarManoJugadores();
                notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
            }
        } else {
            throw new JugadorDesconectadoException();
        }
    }

    public void empezarRonda() throws RemoteException {
        ronda.empezar(jugadores);
        notificacionesComienzoRonda();
    }

    public void actualizarMano(int numJugador) throws RemoteException {
        notificarObservador(numJugador, jugadores.get(numJugador).getMano());
    }

    public void moverCartaEnMano(int i, int i1) throws RemoteException {
        jugadores.get(numTurno).getMano().moverCartaEnMano(i,i1);
        actualizarMano(numTurno);
    }

    public int[] comprobarQueFaltaParaCortar(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).comprobarQueFaltaParaCortar();
    }

    public String getNombreJugador(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).getNombre();
    }

    public int getNumTurno() throws RemoteException {
        return numTurno;
    }

    @Override
    public int getNumJugadorRoboCastigo() throws RemoteException {
        return RoboCastigo.getInstancia().getNumJugadorRoboCastigo();
    }

    public Map<String, Integer> getPuntosJugadores() throws RemoteException {
        return puntuacion.getPuntosJugadores();
    }

    public void crearJugador(String nombre, int numObservador)
            throws RemoteException {
        int i = 0;
        boolean encontrado = false;
        while (i < jugadores.size() && !encontrado) {
            String nombreJugador = getNombreJugador(i);
            if (nombreJugador.equals(nombre)) {
                encontrado = true;
            }
            i++;
        }
        if (!encontrado) {
            Jugador nuevoJugador = new Jugador(nombre);
            nuevoJugador.setNumeroJugador(numObservador);
            jugadores.add(nuevoJugador);
        } else {

        }
    }

    public int getCantJugadoresDeseada() throws RemoteException {
        return cantJugadoresDeseada;
    }

    private void ponerJugadoresEnOrden() throws RemoteException {
        ArrayList<Jugador> jugadoresOrdenado = new ArrayList<>(jugadores.size());
        for (int i = 0; i < jugadores.size(); i++) {
            jugadoresOrdenado.add(null); //para evitar exception
        }

        for (Jugador j : jugadores) {
            jugadoresOrdenado.set(j.getNumeroJugador(),j);
        }

        jugadores.clear();
        jugadores.addAll(jugadoresOrdenado);
    }
}