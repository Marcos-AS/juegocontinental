package controlador;

import modelo.*;
import rmimvc.src.cliente.IControladorRemoto;
import rmimvc.src.observer.IObservableRemoto;
import vista.ifVista;
import java.rmi.RemoteException;
import java.util.ArrayList;

import static modelo.Eventos.*;


public class Controlador implements IControladorRemoto {
    ifVista vista;
    ifJuego juego;
    private int partidasJugadas = 0;


    public Controlador(ifVista vista) {
        this.vista = vista;
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        juego = (ifJuego)modeloRemoto;
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object o) throws RemoteException {
        if (o instanceof Eventos e) {
            switch (e) {
                case NOTIFICACION_ROBO: {
                    desarrolloRobo();
                    break;
                }
                case NOTIFICACION_DESARROLLO_TURNO: {
                    desarrolloTurno();
                    break;
                }
                case NOTIFICACION_ROBO_CASTIGO: {
                    roboCastigo();
                    break;
                }
                case NOTIFICACION_HUBO_ROBO_CASTIGO: {
                    String nom = getJugadorPartida(juego.getPartidaActual().getNumJugadorRoboCastigo()).getNombre();
                    vista.mostrarInfo("El jugador "+ nom + " ha robado con castigo.");
                    break;
                }
                case NOTIFICACION_COMIENZO_TURNO:
                    vista.comienzoTurno(getJugadorPartida((int) o));
                    break;
                case NOTIFICACION_COMIENZO_RONDA:
                    vista.mostrarInfo("Comienza la ronda " + getRonda());
                    break;
                case NOTIFICACION_NUEVO_JUGADOR:
                    String nombreJugador = getJugadorPartida(juego.getPartidaActual().getJugadoresActuales().size()).getNombre();
                    vista.mostrarInfo("El jugador " + nombreJugador + " ha ingresado.");
                    break;
                case NOTIFICACION_NUEVA_PARTIDA_PROPIO: {
                    vista.mostrarInfo("Se ha iniciado una partida.");
                    break;
                }
                case NOTIFICACION_COMIENZO_PARTIDA:
                    int cantJugadores = juego.getPartidaActual().getJugadoresActuales().size();
                    String[] jugadores = new String[cantJugadores];
                    for (int i = 0; i < cantJugadores-1; i++) {
                        jugadores[i] = juego.getPartidaActual().getJugadoresActuales().get(i).getNombre();
                    }
                    vista.mostrarComienzaPartida(jugadores);
                    break;
                case NOTIFICACION_AGREGAR_OBSERVADOR:
                    int observadorIndex = juego.getObservadorIndex(this);
                    int numJugador = juego.getPartidaActual().getJugador(vista.getNombreVista()).getNumeroJugador();
                    if (numJugador != observadorIndex) {
                        getJugadorPartida(numJugador).setNumeroJugador(observadorIndex);
                    }
                    break;
                case NOTIFICACION_NUEVA_VENTANA:
                    //vista.actualizar(null, indice);
                    break;
                case NOTIFICACION_FIN_PARTIDA:
                    vista.mostrarInfo("La partida ha finalizado.");
                    break;
                case NOTIFICACION_CORTE_RONDA: {
                    String nombreJ = getJugadorPartida(juego.getPartidaActual().getNumJugadorCorte()).getNombre();
                    if (!nombreJ.equals(vista.getNombreVista())) {
                        vista.mostrarInfo("El jugador " + nombreJ + " ha cortado.");
                    } else {
                        vista.mostrarInfo("Has cortado. Felicitaciones!");
                    }
                    break;
                }
                case NOTIFICACION_PUNTOS: {
                    vista.mostrarPuntosRonda(juego.getPartidaActual().getPuntosJugadores());
                    break;
                }
                case NOTIFICACION_GANADOR: {
                    String ganador = juego.getPartidaActual().getGanador().getNombre();
                    vista.mostrarInfo("El jugador " + ganador + " es el ganador!");
                    break;
                }
                case NOTIFICACION_FIN_TURNO: {
                    String nombreJ = getJugadorPartida(juego.getPartidaActual().getNumTurno()-1).getNombre();
                    vista.mostrarInfo("Finalizó el turno del jugador " + nombreJ);
                    partida();
                    break;
                }
                case NOTIFICACION_NUEVA_PARTIDA: {
                    String nombre = getJugadorPartida(juego.getPartidaActual().getNumJugadorQueEmpezoPartida()).getNombre();
                    vista.mostrarInfo("El jugador " + nombre + " ha iniciado una partida nueva");
                    break;
                }
            }
        }
    }

    public int getRonda() throws RemoteException {
        return juego.getPartidaActual().getNumRonda();
    }

    public ifCarta getPozo() {
        ifCarta c = null;
        if (!juego.getPartidaActual().getPozo().isEmpty())
            c = juego.getPartidaActual().sacarPrimeraDelPozo();
        return c;
    }

    public ifJugador getJugadorPartida(int numJugadorPartida) throws RemoteException {
        return (ifJugador) juego.getPartidaActual().getJugadoresActuales().get(numJugadorPartida);
    }

    public void partida() throws RemoteException {
        int i;
        if (!juego.getPartidaActual().isRondaEmpezada()) {
            juego.getPartidaActual().setRondaEmpezada(true);
            juego.notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
            iniciarCartasPartida();
            i = juego.getPartidaActual().getNumJugadorQueEmpiezaRonda();
        } else {
            i = juego.getPartidaActual().getNumTurno();
        }
        juego.notificarObservadores(NOTIFICACION_COMIENZO_TURNO);
        //if (getJugadorPartida(i).getNombre().equals(vista.getNombreVista())) {
        juego.notificarObservador(i, NOTIFICACION_ROBO);
        if (getJugadorPartida(i).isRoboDelMazo()) {
            getJugadorPartida(i).setRoboDelMazo(false);
            notificarRoboConCastigo(i);
            juego.getPartidaActual().resetearRoboConCastigo();
        }
        juego.notificarObservador(i, NOTIFICACION_DESARROLLO_TURNO);
        //}
    }

    public void iniciarCartasPartida() throws RemoteException{
        juego.getPartidaActual().crearMazo();
        juego.getPartidaActual().repartirCartas();
        juego.getPartidaActual().iniciarPozo();
    }

    public void notificarRoboConCastigo(int numJugador) throws RemoteException {
        int numJugadorRoboCastigo = numJugador+1;
        int cantJugadoresPartida = juego.getPartidaActual().getJugadoresActuales().size();
        if (numJugadorRoboCastigo > cantJugadoresPartida-1) {
            numJugadorRoboCastigo = 0;
        }
        juego.getPartidaActual().setNumJugadorRoboCastigo(numJugadorRoboCastigo);
        int[] jugadoresQuePuedenRobarConCastigo = new int[cantJugadoresPartida-1];
        for (int i = 0; i < cantJugadoresPartida-1; i++) {
            jugadoresQuePuedenRobarConCastigo[i] = numJugadorRoboCastigo;
            numJugadorRoboCastigo++;
            if (numJugadorRoboCastigo>cantJugadoresPartida-1) numJugadorRoboCastigo = 0;
        }
        juego.notificarObservadores(jugadoresQuePuedenRobarConCastigo, NOTIFICACION_ROBO_CASTIGO);
    }

    public void desarrolloTurno() throws RemoteException {
        int numJugador = juego.getPartidaActual().getNumTurno();
        vista.mostrarCartas(enviarManoJugador(numJugador));
        boolean bajoJuegos = false;
        boolean corte = false;
        while(getJugadorPartida(numJugador).isTurnoActual()) {
            int eleccion = vista.menuBajar();
            switch (eleccion) {
                case ifVista.ELECCION_ORDENAR_CARTAS:
                    int manoSize = getJugadorPartida(numJugador).getMano().size();
                    int[] cartas = vista.preguntarParaOrdenarCartas(manoSize);
                    getJugadorPartida(numJugador).moverCartaEnMano(cartas[0], cartas[1]);
                    break;
                case ifVista.ELECCION_ACOMODAR_JUEGO_PROPIO:
                    ArrayList<ArrayList<String>> juegos = enviarJuegosJugador(numJugador);
                    if (!juegos.isEmpty()) {
                        int cartaAcomodar = vista.preguntarCartaParaAcomodar();
                        vista.mostrarJuegos(juegos);
                        acomodarEnJuegoPropio(cartaAcomodar, numJugador, juegos, Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO))-1);
                    } else {
                        vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
                    }
                    break;
                case ifVista.ELECCION_ACOMODAR_JUEGO_AJENO:
                    if (hayJuegosEnMesa(numJugador)) {
                        enviarJuegosEnMesa(numJugador+1);
                        int iCartaAcomodar = vista.preguntarCartaParaAcomodar();
                        ifCarta c = getJugadorPartida(numJugador).getMano().get(iCartaAcomodar);
                        int numJugadorAcomodar = Integer.parseInt(vista.preguntarInput("Ingresa el número de jugador en cuyo juegos bajados quieres acomodar: "));
                        vista.mostrarJuegos(enviarJuegosJugador(numJugadorAcomodar));
                        acomodarEnJuegoAjeno(iCartaAcomodar, c.getNumero(), c.getPalo(), numJugador, numJugadorAcomodar, Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO))-1);
                    } else {
                        vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
                    }
                    break;
                case ifVista.ELECCION_BAJARSE:
                    int cantVecesQueBajo = getJugadorPartida(numJugador).getPuedeBajar();
                    if (cantVecesQueBajo == 0) {
                        vista.mostrarInfo(ifVista.ADVERTENCIA_BAJARSE);
                        bajoJuegos = bajarseYComprobarCortar(numJugador);
                    } else if (cantVecesQueBajo == 1) {
                        vista.mostrarInfo("Debes tener los juegos requeridos para la ronda y cortar si deseas bajar ahora.");
                        bajoJuegos = bajarseYComprobarCortar(numJugador);
                    } else {
                        vista.mostrarInfo(ifVista.YA_NO_PUEDE_BAJAR);
                    }
                    break;
                case ifVista.ELECCION_TIRAR_AL_POZO: {
                    tirarAlPozoTurno(numJugador, enviarManoJugador(numJugador));
                    getJugadorPartida(numJugador).setTurnoActual(false);
                    break;
                    }
                case ifVista.ELECCION_VER_JUEGOS_BAJADOS:
                    vista.mostrarJuegos(enviarJuegosJugador(numJugador));
                    break;
                case ifVista.ELECCION_VER_JUEGOS_BAJADOS_MESA:
                    enviarJuegosEnMesa(numJugador+1);
                    break;
                case ifVista.ELECCION_VER_POZO:
                    vista.mostrarPozo(getPozo());
                    break;
            }
            if (getJugadorPartida(numJugador).getMano().isEmpty()) {
                getJugadorPartida(numJugador).setTurnoActual(false);
                if (!juego.getPartidaActual().isCorteRonda()) {
                    juego.getPartidaActual().setCorteRonda();
                }
                corte = true;
                juego.getPartidaActual().setNumJugadorCorte(numJugador);
                juego.notificarObservadores(NOTIFICACION_CORTE_RONDA);
                juego.getPartidaActual().finRonda();
                juego.notificarObservadores(NOTIFICACION_PUNTOS);
                juego.getPartidaActual().incrementarNumJugadorQueEmpiezaRonda();
                if(getRonda()>=juego.getPartidaActual().getTotalRondas()) finPartida();
            } else {
                vista.mostrarCartas(enviarManoJugador(numJugador));
            }
        }
        if (bajoJuegos) getJugadorPartida(numJugador).incrementarPuedeBajar();
        if (!corte) {
            juego.getPartidaActual().finTurno();
            juego.notificarObservadores(NOTIFICACION_FIN_TURNO);
        }
    }

    public void setTurno(int numJugador, boolean valor) throws RemoteException {
        getJugadorPartida(numJugador).setTurnoActual(valor);
    }

    public ArrayList<String> enviarManoJugador(int numJugador) throws RemoteException {
        ArrayList<String> manoString = new ArrayList<>();
        try {
            ifJugador jA = getJugadorPartida(numJugador);
            ArrayList<ifCarta> cs = new ArrayList<>(jA.getMano());
            manoString = ifVista.cartasToStringArray(cs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manoString;
    }

    public ArrayList<ArrayList<String>> enviarJuegosJugador(int numJugador) throws RemoteException {
        ifJugador j = getJugadorPartida(numJugador);
        ArrayList<ArrayList<Carta>> juegos = j.getJuegos();
        ArrayList<ArrayList<String>> juegosString = new ArrayList<>();
        for (ArrayList<Carta> juego : juegos) {
            ArrayList<ifCarta> cs = new ArrayList<>(juego);
            juegosString.add(ifVista.cartasToStringArray(cs));
        }
        return juegosString;
    }

    public void acomodarEnJuegoPropio(int iCarta, int numJugador, ArrayList<ArrayList<String>> juegos, int numJuego) throws RemoteException {
        if(getJugadorPartida(numJugador).acomodarCartaJuegoPropio(iCarta, numJuego, getRonda())) {
            vista.mostrarInfo("Se acomodó la carta en el juego.");
            vista.mostrarCartas(enviarJuegosJugador(numJugador).get(numJuego));
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private boolean hayJuegosEnMesa(int numJugador) throws RemoteException {
        int i = 0;
        boolean hay;
        int cantJugadoresPartida = juego.getPartidaActual().getJugadoresActuales().size();
        do {
            numJugador++;
            if (numJugador > cantJugadoresPartida-1) {
                numJugador = 0;
            }
            hay = getJugadorPartida(numJugador).getPuedeBajar()>0;
            i++;
        } while (!hay && i < cantJugadoresPartida-1);
        return hay;
    }

    public void enviarJuegosEnMesa(int numJugador) throws RemoteException {
        int cantJugadoresPartida = juego.getPartidaActual().getJugadoresActuales().size();
        for (int j = 0; j < cantJugadoresPartida-1; j++) {
            if (numJugador>cantJugadoresPartida-1) {
                numJugador = 0;
            }
            vista.mostrarInfo("Juegos del jugador " + numJugador + ": ");
            vista.mostrarJuegos(enviarJuegosJugador(numJugador));
            numJugador++;
        }
    }

    public void acomodarEnJuegoAjeno(int iCarta, int numCarta, Palo paloCarta, int numJugador, int numJugadorAcomodar, int numJuego) throws RemoteException {
        boolean acomodo = getJugadorPartida(numJugadorAcomodar).comprobarAcomodarCarta(numCarta,paloCarta,numJuego,getRonda());
        if (acomodo) {
            Carta c = getJugadorPartida(numJugador).getMano().remove(iCarta);
            getJugadorPartida(numJugador).getJuegos().get(numJuego).add(c);
            vista.mostrarInfo("Se acomodó la carta en el juego.");
            vista.mostrarInfo("Juegos del jugador " + numJugadorAcomodar + ": ");
            vista.mostrarJuegos(enviarJuegosJugador(numJugadorAcomodar));
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private boolean bajarseYComprobarCortar(int numJugador) throws RemoteException {
        boolean puedeCortar = false;
        boolean bajoJuegos = false;
        while (!puedeCortar && vista.preguntarSiQuiereSeguirBajandoJuegos()) {
            vista.mostrarCartas(enviarManoJugador(numJugador));
            if (bajarse(numJugador, vista.preguntarQueBajarParaJuego(vista.preguntarCantParaBajar()))) {
                bajoJuegos = true;
                vista.mostrarCartas(enviarManoJugador(numJugador));
                puedeCortar = ifJuego.comprobarPosibleCorte(getRonda(), getJugadorPartida(numJugador).getTriosBajados(), getJugadorPartida(numJugador).getEscalerasBajadas());
            }
        }
        if (puedeCortar) {
            if(getJugadorPartida(numJugador).getMano().size() <= 1) {
                if (getJugadorPartida(numJugador).getMano().size() == 1)
                    juego.getPartidaActual().agregarAlPozo(getJugadorPartida(numJugador).getMano().remove(0));
                juego.getPartidaActual().setCorteRonda();
                getJugadorPartida(numJugador).setPuedeBajar(0);
            } else {
                vista.mostrarInfo("Para cortar debe quedarte en la mano 1 o 0 cartas");
            }
        } else {
            int[] faltante = getJugadorPartida(numJugador).comprobarQueFaltaParaCortar(getRonda());
            vista.mostrarInfo("Para cortar faltan " + faltante[0] + " trios y " + faltante[1] + " escaleras");
        }
        return bajoJuegos;
    }

    public boolean bajarse(int numJugador, int [] cartasABajar) throws RemoteException {
        boolean bajo = false;
        int tipoJuego =
                ifJuego.comprobarJuego(getJugadorPartida(numJugador).seleccionarCartasABajar(cartasABajar),getRonda());
        if(tipoJuego != ifJuego.JUEGO_INVALIDO) {
            getJugadorPartida(numJugador).bajarJuego(cartasABajar, tipoJuego);
            vista.mostrarJuegos(enviarJuegosJugador(numJugador));
            bajo = true;
        } else {
            vista.mostrarInfo(ifVista.MOSTRAR_JUEGO_INVALIDO);
        }
        return bajo;
    }

    public void tirarAlPozoTurno(int numJugador, ArrayList<String> mano) throws RemoteException {
        int cartaATirar = vista.preguntarQueBajarParaPozo(mano.size());
        juego.getPartidaActual().agregarAlPozo(getJugadorPartida(numJugador).getMano().remove(cartaATirar));
    }

    private void finPartida() throws RemoteException {
        juego.getPartidaActual().determinarGanador(); //al finalizar las rondas
        juego.serializarGanador();
        juego.notificarObservadores(NOTIFICACION_GANADOR);
        juego.notificarObservadores(NOTIFICACION_FIN_PARTIDA);
        //lo siguiente es para poder seguir jugando otras partidas
        juego.removerObservadores();
        partidasJugadas++;
    }

    public void desarrolloRobo() throws RemoteException {
        int numJugador = juego.getPartidaActual().getNumTurno();
        vista.mostrarCartas(enviarManoJugador(numJugador));
        int eleccion = Integer.parseInt(vista.preguntarInput(ifVista.MENU_ROBAR));
        if (eleccion == ifVista.ELECCION_ROBAR_DEL_POZO) {
            getJugadorPartida(numJugador).getMano().add(juego.getPartidaActual().sacarPrimeraDelPozo());
        } else if(eleccion == ifVista.ELECCION_ROBAR_DEL_MAZO) {
            getJugadorPartida(numJugador).getMano().add(juego.getPartidaActual().sacarPrimeraDelMazo());
            getJugadorPartida(numJugador).setRoboDelMazo(true);
        }
    }

    public void roboCastigo() throws RemoteException {
        int numJugadorRoboCastigo = juego.getPartidaActual().getNumJugadorRoboCastigo();
        if (!getJugadorPartida(numJugadorRoboCastigo).isRoboConCastigo()) {
            boolean roboConCastigo = false;
            ifJugador j = getJugadorPartida(numJugadorRoboCastigo);
            vista.mostrarInfo("El jugador " + j.getNombre() + " puede robar con castigo.");
            if (getJugadorPartida(numJugadorRoboCastigo).getPuedeBajar()==0) {
                vista.mostrarCartas(enviarManoJugador(numJugadorRoboCastigo));
                if (Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_ROBAR_CASTIGO)) == ifVista.ELECCION_ROBAR_CON_CASTIGO) {
                    getJugadorPartida(numJugadorRoboCastigo).getMano().add(juego.getPartidaActual().sacarPrimeraDelPozo());
                    getJugadorPartida(numJugadorRoboCastigo).getMano().add(juego.getPartidaActual().sacarPrimeraDelMazo());
                    juego.notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
                    getJugadorPartida(numJugadorRoboCastigo).setRoboConCastigo(true);
                    roboConCastigo = true;
                }
            }
            if (!roboConCastigo) {
                numJugadorRoboCastigo++;
                if (numJugadorRoboCastigo > juego.getPartidaActual().getJugadoresActuales().size() -1)
                    numJugadorRoboCastigo = 0;
                juego.getPartidaActual().setNumJugadorRoboCastigo(numJugadorRoboCastigo);
            }
        }
    }

    public void agregarNuevoJugador(String nombreJugador) throws RemoteException {
        juego.agregarJugador(nombreJugador);
    }

    public void crearPartida(ifVista vista, int cantJugadores) throws RemoteException {
        if (partidasJugadas>0)
            juego.agregarObservador(this); //porque al terminar la partida se remueven los observadores
        int observadorIndex = juego.getObservadorIndex(this);
        juego.getPartidaActual().setNumTurno(observadorIndex);
        juego.crearPartida(vista.getNombreVista(), cantJugadores, observadorIndex); //setea partida en juego y en ctrl
    }

    public Object[] getRanking() throws RemoteException {
        return juego.getRanking().readObjects();
    }

    public Eventos jugarPartidaRecienIniciada() throws RemoteException {
        int i = 0;
        Eventos inicio = PARTIDA_AUN_NO_CREADA;
        boolean encontrado = false;
        Partida p = juego.getPartidaActual();
        if (p != null) { //si ya se llamo a crearPartida()
            //para iniciar la partida, esta debe tener cant de jugadoresActuales = cantDeseada
            int cantJugadoresActuales = p.getJugadoresActuales().size();
            //si p ya esta creada entonces tiene un jugador
            //hay que averiguar si el que llamo a esta funcion es el mismo que el que la creó
            //si la creó entonces tiene que se un jugadorActual
            while (i < cantJugadoresActuales && !encontrado) {
                if (p.getJugadoresActuales().get(i).getNombre().equals(vista.getNombreVista())) {
                    encontrado = true; //significa que el creo la partida, llamo a esta funcion
                    inicio = FALTAN_JUGADORES;
                }
                i++;
            }
            if (!encontrado) { //significa que la vista llamo a esta funcion pero no creo la partida
                juego.getPartidaActual().agregarJugador(vista.getNombreVista());
            }
            p = juego.getPartidaActual();
            if (p.getJugadoresActuales().size() == p.getCantJugadoresDeseada()) {
                p.setEnCurso();
                inicio = INICIAR_PARTIDA;
            } else {
                inicio = FALTAN_JUGADORES;
            }
        }
        if (inicio==INICIAR_PARTIDA) {
            juego.notificarObservadores(NOTIFICACION_AGREGAR_OBSERVADOR);
            juego.getPartidaActual().ponerJugadoresEnOrden();
        }
        return inicio;
    }

    public void notificarComienzoPartida() throws RemoteException {
        juego.notificarObservadores(NOTIFICACION_COMIENZO_PARTIDA);
    }
}
