package modelo;

import rmimvc.src.observer.IObservadorRemoto;
import rmimvc.src.observer.ObservableRemoto;
import serializacion.Serializador;
import vista.ifVista;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static modelo.EntradasUsuario.*;
import static modelo.Eventos.*;

public class Partida extends ObservableRemoto implements Serializable, ifPartida {
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private final Serializador srlRanking = new Serializador("src/serializacion/ranking.dat");
    private final Serializador srlPartidas = new Serializador("src/serializacion/partidas.dat");
    private ArrayList<String> nombresElegidos;
    private int numRonda = 1;
    private Carta pozo;
    private Mazo mazo;
    private Puntuacion puntuacion;
    private int numJugadorQueEmpiezaRonda;
    private int numTurno;
    private int cantJugadoresDeseada;
    private boolean enCurso = false;
    private boolean ejecutarFinTurno = false;
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
        numRonda = partidaCargada.numRonda;
        pozo = partidaCargada.pozo;
        mazo = partidaCargada.mazo;
        puntuacion = partidaCargada.puntuacion;
        numJugadorQueEmpiezaRonda = partidaCargada.numJugadorQueEmpiezaRonda;
        numTurno = partidaCargada.numTurno;
        cantJugadoresDeseada = partidaCargada.cantJugadoresDeseada;
        enCurso = partidaCargada.enCurso;
        ejecutarFinTurno = partidaCargada.ejecutarFinTurno;
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

    private ReglaJuego getRegla(String tipo) throws RemoteException {
        return switch (tipo) {
            case "escalera" -> new ReglaEscalera();
            case "trio" -> new ReglaTrio();
            case "mismoPalo" -> new ReglaMismoPalo();
            default -> throw new IllegalArgumentException("Tipo de regla no válido");
        };
    }

    private void notificarRoboConCastigo(int numJugador) throws RemoteException {
        ArrayList<Integer> jugadoresQueNoPuedenRobarConCastigo = new ArrayList<>();
        for (int i = 0; i < jugadores.size()-1; i++) {
            numJugador++; //al principio ya que el jugador del turno no lo tiene en cuenta
            if (numJugador > jugadores.size() - 1) numJugador = 0;
            if (jugadores.get(numJugador).juegos.isEmpty()) {
                RoboCastigo.getInstancia().getJugadores().add(numJugador);
            } else {
                jugadoresQueNoPuedenRobarConCastigo.add(numJugador);
            }
        }
        if (!jugadoresQueNoPuedenRobarConCastigo.isEmpty()) {
            notificarObservadores(jugadoresQueNoPuedenRobarConCastigo,
                    NOTIFICACION_NO_PUEDE_ROBO_CASTIGO);
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
        actualizarManoJugadores();
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_JUEGOS);
        notificarObservadores(NOTIFICACION_NUMERO_JUGADOR);
        notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
    }

    private void finRonda() throws RemoteException {
        incrementarNumJugadorQueEmpiezaRonda();
        numRonda++;
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
        numRonda = 1;
    }

    private void incrementarNumJugadorQueEmpiezaRonda() throws RemoteException {
        numJugadorQueEmpiezaRonda++;
        if (numJugadorQueEmpiezaRonda >= jugadores.size()) {
            numJugadorQueEmpiezaRonda = 0;
        }
    }

    private void setNumTurno(int numTurno) throws RemoteException {
        this.numTurno = numTurno;
    }

    private void incTurno() throws RemoteException{
        numTurno++;
        if (numTurno>jugadores.size()-1) {
            numTurno = 0;
        }
    }

    public void guardar() throws RemoteException {
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

    public void setEjecutarFinTurno(boolean ejecutarFinTurno)
    throws RemoteException{
        this.ejecutarFinTurno = ejecutarFinTurno;
    }

    @Override
    public void incNumJugadorRoboCastigo() throws RemoteException {
        RoboCastigo.getInstancia().incNumJugadorRoboCastigo();
    }

    @Override
    public void comprobarEmpezarPartida() throws RemoteException {
        if (nombresElegidos.size()==jugadores.size()) {
            ponerJugadoresEnOrden();
            notificacionesComienzoRonda();
            notificarObservadores(NOTIFICACION_BAJO_JUEGO); //muestra los juegos bajados si había
            if (ejecutarFinTurno) finTurno(); //cuando se guarda una partida puede que quede sin ejecutar esto
            notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
        }
    }

    @Override
    public void desarrolloRobo(String eleccion) throws RemoteException {
        if (Objects.equals(eleccion, ROBAR_DEL_MAZO)) {
            robarDelMazo();
        } else if (Objects.equals(eleccion, ROBAR_DEL_POZO)) {
            robarDelPozo();
        }
    }

    private Eventos puedeBajar() throws RemoteException {
        Eventos bajar = BAJAR;
        int cantVecesQueBajo = getCantJuegos(numTurno);
        if ((numRonda <= 3 && cantVecesQueBajo > 2) ||
                (numRonda>3 && cantVecesQueBajo > 3)) {
            bajar = NO_PUEDE_BAJAR;
        }
        return bajar;
    }

    public boolean bajarse(int[] indicesCartas) throws RemoteException {
        boolean bajar = false;
        if (puedeBajar()==BAJAR) {
            if(bajarJuego(indicesCartas)) { //si se comprueba, procede a bajar
                bajar = true;
                notificarObservadores(NOTIFICACION_BAJO_JUEGO);
            } else {
                notificarObservador(numTurno, NOTIFICACION_JUEGO_INVALIDO);
            }
        }
        return bajar;
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
            jugadores.get(numJugador).getMano().removeCarta(cartaAcomodar);
            jugadores.get(numJugador).juegos.get(iJuego).juego.add(c);
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

    public Eventos comprobarPosibleCorte(int numJugador) throws RemoteException{
        Eventos puedeCortar = NO_PUEDE_CORTAR;
        if(jugadores.get(numJugador).comprobarPosibleCorte(numRonda)) {
            int cartasEnMano = jugadores.get(numJugador).getMano().getSize();
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
        Jugador j = jugadores.get(numTurno);
        j.juegos.add(juego);
        j.getMano().eliminarDeLaMano(j.juegos.get(j.juegos.size()-1).juego);
        actualizarMano(numTurno);
        return juego!=null;
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
        numJugadorQueEmpiezaRonda = observadorIndex;
        numTurno = numJugadorQueEmpiezaRonda;
        notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
    }

    public boolean jugarPartida() throws RemoteException {
        boolean inicio = true;
        if (jugadores.size()==cantJugadoresDeseada) {
            puntuacion = new Puntuacion(jugadores);
        } else {
            inicio = false;
        }
        return inicio;
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
        return numRonda;
    }

    public ifCarta getPozo() throws RemoteException {
        return pozo;
    }

    public int getNumeroJugador(String nombreJugador)  throws RemoteException{
        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombreJugador)) return j.getNumeroJugador();
        }
        return -1;
    }

    public void robarDelMazo() throws RemoteException {
        jugadores.get(numTurno).getMano().agregarCarta(mazo.sacarPrimeraDelMazo()); //robo del mazo
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

    @Override
    public int getCantJuegos(int numJugador) throws RemoteException {
        return jugadores.get(numJugador).juegos.size();
    }

    @Override
    public void setJugadoresQuePuedenRobarConCastigo() throws RemoteException {
        RoboCastigo.getInstancia().setJugadoresQuePuedenRobarConCastigo();
    }

    @Override
    public void notificarSalir() throws RemoteException {
        ArrayList<Integer> avisar = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.getNumeroJugador() != numTurno)
                avisar.add(j.getNumeroJugador());
        }
        notificarObservadores(avisar, NOTIFICACION_SALIR_MENU);
    }

    public void robarConCastigo() throws RemoteException {
        int num = getNumJugadorRoboCastigo();
        jugadores.get(num).getMano().agregarCarta(pozo);
        pozo = null;
        jugadores.get(num).getMano().agregarCarta(mazo.sacarPrimeraDelMazo()); //robo del mazo
        actualizarMano(num);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
    }

    public void setTurnoJugador(int numJugador, boolean valor) throws RemoteException {
        jugadores.get(numJugador).setTurnoActual(valor);
    }

    public void tirarAlPozo(int cartaATirar) throws RemoteException {
        pozo = (jugadores.get(numTurno).getMano().removeCarta(cartaATirar));
        jugadores.get(numTurno).setTurnoActual(false);
        notificarObservadores(NOTIFICACION_ACTUALIZAR_POZO);
        actualizarMano(numTurno);
    }

    public void finTurno() throws RemoteException {
        final int TOTAL_RONDAS = 7;
        if (jugadores.get(numTurno).getMano().getSize()==0) {
            finRonda();
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
        mazo = new Mazo();
        mazo.iniciarMazo(jugadores.size());
        //mazo.repartirCartasPrueba(jugadores,numRonda); //prueba
        mazo.repartirCartas(jugadores,numRonda);
        pozo = mazo.sacarPrimeraDelMazo();
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
        return jugadores.get(numJugador).comprobarQueFaltaParaCortar(numRonda);
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
        Jugador nuevoJugador = new Jugador(nombre);
        nuevoJugador.setNumeroJugador(numObservador);
        jugadores.add(nuevoJugador);
    }

    public int getCantJugadoresDeseada() throws RemoteException {
        return cantJugadoresDeseada;
    }

    public void ponerJugadoresEnOrden() throws RemoteException {
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