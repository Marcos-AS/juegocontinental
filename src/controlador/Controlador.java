package controlador;

import modelo.*;
import rmimvc.src.cliente.IControladorRemoto;
import rmimvc.src.observer.IObservableRemoto;
import vista.ifVista;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

import static modelo.Eventos.*;


public class Controlador implements IControladorRemoto {
    ifVista vista;
    ifPartida partida;
    private int partidasJugadas = 0;


    public Controlador(ifVista vista) {
        this.vista = vista;
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        partida = (ifPartida) modeloRemoto;
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
                    String nom = getJugadorPartida(partida.getNumJugadorRoboCastigo()).getNombre();
                    vista.mostrarInfo(nom + " ha robado con castigo.");
                    break;
                }
                case NOTIFICACION_NO_PUEDE_ROBO_CASTIGO: {
                    vista.mostrarInfo("No puede robar con castigo porque ya bajó uno o más juegos a la mesa");
                    break;
                }
                case NOTIFICACION_COMIENZO_TURNO:
                    int numJ = partida.getNumTurno();
                    vista.comienzoTurno(partida.getNombreJugador(numJ), numJ);
                    break;
                case NOTIFICACION_COMIENZO_RONDA:
                    vista.mostrarInfo("Comienza la ronda " + getRonda());
                    break;
                case NOTIFICACION_NUEVO_JUGADOR:
                    String nombreJugador = partida.getJugadores().get(partida.getJugadores().size()-1).getNombre();
                    vista.mostrarInfo(nombreJugador + " ha ingresado.");
                    break;
                case NOTIFICACION_NUEVA_PARTIDA_PROPIO: {
                    vista.mostrarInfo("Se ha iniciado una partida.");
                    break;
                }
                case NOTIFICACION_COMIENZO_PARTIDA:
                    int cantJugadores = getCantJugActuales();
                    String[] jugadores = new String[cantJugadores];
                    for (int i = 0; i < cantJugadores; i++) {
                        jugadores[i] = getJugadorPartida(i).getNombre();
                    }
                    vista.mostrarComienzaPartida(jugadores);
                    break;
                case NOTIFICACION_AGREGAR_OBSERVADOR:
                    int observadorIndex = partida.getObservadorIndex(this);
                    int numJugador = partida.getJugador(vista.getNombreVista()).getNumeroJugador();
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
                    String nombreJ = getJugadorPartida(partida.getNumJugadorCorte()).getNombre();
                    if (!nombreJ.equals(vista.getNombreVista())) {
                        vista.mostrarInfo(nombreJ + " ha cortado.");
                    } else {
                        vista.mostrarInfo("Has cortado. Felicitaciones!");
                    }
                    break;
                }
                case NOTIFICACION_PUNTOS: {
                    vista.mostrarPuntosRonda(partida.getPuntosJugadores());
                    break;
                }
                case NOTIFICACION_GANADOR: {
                    String ganador = partida.getGanador().getNombre();
                    vista.mostrarInfo(ganador + " es el ganador!");
                    break;
                }
                case NOTIFICACION_NUEVA_PARTIDA: {
                    String nombre = getJugadorPartida(partida.getNumJugadorQueEmpezoPartida()).getNombre();
                    vista.mostrarInfo(nombre + " ha iniciado una partida nueva");
                    break;
                }
                case NOTIFICACION_FIN_RONDA: {
                    vista.mostrarInfo("Finalizó la ronda " + (partida.getNumRonda()-1));
                    partida();
                    break;
                }
            }
        }
        else if (o instanceof Object[] obj) {
            if (obj[0] == NOTIFICACION_BAJO_JUEGO) {
                String nombre = partida.getNombreJugador((int)obj[1]);
                if (!vista.getNombreVista().equals(nombre)) {
                    vista.mostrarInfo("El jugador " + nombre + " bajó un juego.");
                }
            } else {
                String nombreJ = partida.getNombreJugador((int) obj[1]);
                vista.mostrarInfo("Finalizó el turno de " + nombreJ);
                partida();
            }
        }
    }

    public int getRonda() throws RemoteException {
        return partida.getNumRonda();
    }

    public ifCarta getPozo() throws RemoteException {
        ifCarta c = null;
        if (!partida.getPozo().isEmpty())
            c = partida.getPrimeraDelPozo();
        return c;
    }

    public ifJugador getJugadorPartida(int numJugadorPartida) throws RemoteException {
        return partida.getJugadores().get(numJugadorPartida);
    }

    public void partida() throws RemoteException {
        int i;
        if (!partida.isRondaEmpezada()) {
            partida.setRondaEmpezada(true);
            //partida.notificarObservadores(NOTIFICACION_COMIENZO_RONDA);
            iniciarCartasPartida();
            i = partida.getNumJugadorQueEmpiezaRonda();
            partida.setNumTurno(i);
        } else {
            i = partida.getNumTurno(); //cambio de número de jugador, cambio de turno
        }
        //partida.notificarObservadores(NOTIFICACION_COMIENZO_TURNO);
        System.out.println("numero de jugador: " + i);
        partida.notificarObservador(i, NOTIFICACION_ROBO);
        System.out.println("numero de jugador: " + i);
        if (getJugadorPartida(i).isRoboDelMazo()) {
            partida.setRoboDelMazo(i, false);
            notificarRoboConCastigo(i);
            partida.resetearRoboConCastigo();
        }
        partida.notificarObservador(i, NOTIFICACION_DESARROLLO_TURNO);
    }

    public void iniciarCartasPartida() throws RemoteException{
        partida.crearMazo();
        partida.repartirCartas();
        partida.iniciarPozo();
    }

    public void notificarRoboConCastigo(int numJugador) throws RemoteException {
        int numJugadorRoboCastigo = numJugador+1;
        int cantJugadoresPartida = getCantJugActuales();
        if (numJugadorRoboCastigo > cantJugadoresPartida-1) {
            numJugadorRoboCastigo = 0;
        }
        partida.setNumJugadorRoboCastigo(numJugadorRoboCastigo);
        int[] jugadoresQuePuedenRobarConCastigo = new int[cantJugadoresPartida-1];
        int[] jugadoresQueNoPuedenRobarConCastigo = new int[cantJugadoresPartida-1];
        Arrays.fill(jugadoresQuePuedenRobarConCastigo, -1);
        Arrays.fill(jugadoresQueNoPuedenRobarConCastigo, -1);
        for (int i = 0; i < cantJugadoresPartida-1; i++) {
            if (partida.getPuedeBajar(numJugadorRoboCastigo)==0) {
                jugadoresQuePuedenRobarConCastigo[i] = numJugadorRoboCastigo;
            } else {
                jugadoresQueNoPuedenRobarConCastigo[i] = numJugadorRoboCastigo;
            }
            numJugadorRoboCastigo++;
            if (numJugadorRoboCastigo > cantJugadoresPartida - 1) numJugadorRoboCastigo = 0;
        }
        partida.notificarObservadores(jugadoresQueNoPuedenRobarConCastigo,
                NOTIFICACION_NO_PUEDE_ROBO_CASTIGO);
        partida.notificarObservadores(jugadoresQuePuedenRobarConCastigo,
                NOTIFICACION_ROBO_CASTIGO);
    }

    public void desarrolloTurno() throws RemoteException {
        int numJugador = partida.getNumTurno();
        vista.mostrarInfo(vista.getCartasString(enviarManoJugador(numJugador)));
        boolean bajoJuegos = false;
        boolean corte = false;
        while(getJugadorPartida(numJugador).isTurnoActual()) {
            String cartasStr = vista.getCartasString(enviarManoJugador(numJugador));
            int eleccion = vista.menuBajar(cartasStr);
            switch (eleccion) {
                case ifVista.ELECCION_BAJARSE:
                    int cantVecesQueBajo = getJugadorPartida(numJugador).getPuedeBajar();
                    if (cantVecesQueBajo == 0) {
                        vista.mostrarInfo(ifVista.ADVERTENCIA_BAJARSE);
                        bajoJuegos = bajarseYComprobarCortar(numJugador);
                    } else if (cantVecesQueBajo == 1) {
                        vista.mostrarInfo("Debes tener los partidas requeridos para la" +
                                " ronda y cortar si deseas bajar ahora.");
                        bajoJuegos = bajarseYComprobarCortar(numJugador);
                    } else {
                        vista.mostrarInfo(ifVista.YA_NO_PUEDE_BAJAR);
                    }
                    break;
                case ifVista.ELECCION_TIRAR_AL_POZO:
                    tirarAlPozoTurno(numJugador, enviarManoJugador(numJugador));
                    break;
                case ifVista.ELECCION_ORDENAR_CARTAS:
                    ArrayList<String> cartas = enviarManoJugador(numJugador);
                    int[] cartasOrdenacion = vista.preguntarParaOrdenarCartas(cartas);
                    partida.moverCartaEnMano(numJugador, cartasOrdenacion[0], cartasOrdenacion[1]);
                    break;
                case ifVista.ELECCION_ACOMODAR_JUEGO_PROPIO:
                    ArrayList<ArrayList<String>> juegos = enviarJuegosJugador(numJugador);
                    if (!juegos.isEmpty()) {
                        int cartaAcomodar = vista.preguntarCartaParaAcomodar(enviarManoJugador(numJugador));
                        vista.mostrarJuegos(juegos);
//                        acomodarEnpartidaPropio(
//                                cartaAcomodar, numJugador,
//                                Integer.parseInt(vista
//                                  .preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO, cartas))-1);
                    } else {
                        vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
                    }
                    break;
                case ifVista.ELECCION_ACOMODAR_JUEGO_AJENO:
                    if (hayJuegosEnMesa(numJugador)) {
                        mostrarJuegosEnMesa(numJugador+1);
                        int iCartaAcomodar =
                                vista.preguntarCartaParaAcomodar(enviarManoJugador(numJugador));
                        ifCarta c = getJugadorPartida(numJugador).getMano().get(iCartaAcomodar);
                        int numJugadorAcomodar = vista.getNumJugadorAcomodar();
                        vista.mostrarJuegos(enviarJuegosJugador(numJugadorAcomodar));
                        acomodarEnpartidaAjeno(iCartaAcomodar, c.getNumero(), c.getPalo(),
                                numJugador, numJugadorAcomodar, Integer
                                        .parseInt(vista.preguntarInput(
                                                ifVista.PREGUNTA_NUMERO_JUEGO))-1);
                    } else {
                        vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
                    }
                    break;
                case ifVista.ELECCION_VER_JUEGOS_BAJADOS:
                    vista.mostrarJuegos(enviarJuegosJugador(numJugador));
                    break;
                case ifVista.ELECCION_VER_JUEGOS_BAJADOS_MESA:
                    mostrarJuegosEnMesa(numJugador+1);
                    break;
                case ifVista.ELECCION_VER_POZO:
                    vista.mostrarInfo(vista.getPozoString(getPozo()));
                    break;
            }
            if (getJugadorPartida(numJugador).getMano().isEmpty()) {
                partida.setTurnoJugador(numJugador, false);
                corte = true;
                partida.setNumJugadorCorte(numJugador);
                partida.notificarObservadores(NOTIFICACION_CORTE_RONDA);
                partida.finRonda();
                partida.notificarObservadores(NOTIFICACION_PUNTOS);
                partida.incrementarNumJugadorQueEmpiezaRonda();
                if(getRonda()>=partida.getTotalRondas()) finPartida();
            }
        }
        if (bajoJuegos) partida.incPuedeBajar(numJugador);
        if (!corte) {
            Object[] notif = new Object[2];
            notif[0] = NOTIFICACION_FIN_TURNO;
            notif[1] = partida.getNumTurno();
            partida.incTurno();
            partida.notificarObservadores(notif);
        } else {
            partida.notificarObservadores(NOTIFICACION_FIN_RONDA);
        }
    }

    public void setTurno(int numJugador, boolean valor) throws RemoteException {
        partida.setTurnoJugador(numJugador, valor);
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
        ArrayList<ArrayList<Carta>> partidas = j.getJuegos();
        ArrayList<ArrayList<String>> partidasString = new ArrayList<>();
        for (ArrayList<Carta> partida : partidas) {
            ArrayList<ifCarta> cs = new ArrayList<>(partida);
            partidasString.add(ifVista.cartasToStringArray(cs));
        }
        return partidasString;
    }

    public void acomodarEnJuegoPropio(int iCarta, int numJugador, int numpartida)
            throws RemoteException {
        if(getJugadorPartida(numJugador)
                .acomodarCartaJuegoPropio(iCarta, numpartida, getRonda())) {
            vista.mostrarInfo("Se acomodó la carta en el partida.");
            vista.mostrarInfo(vista
                    .getCartasString(enviarJuegosJugador(numJugador).get(numpartida)));
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private boolean hayJuegosEnMesa(int numJugador) throws RemoteException {
        int i = 0;
        boolean hay;
        int cantJugadoresPartida = getCantJugActuales();
        do {
            numJugador++;
            if (numJugador > cantJugadoresPartida-1) {
                numJugador = 0;
            }
            hay = partida.getPuedeBajar(numJugador)>0;
            i++;
        } while (!hay && i < cantJugadoresPartida-1);
        return hay;
    }

    public void mostrarJuegosEnMesa(int numJugador) throws RemoteException {
        int cantJugadoresPartida = getCantJugActuales();
        for (int j = 0; j < cantJugadoresPartida-1; j++) {
            if (numJugador>cantJugadoresPartida-1) {
                numJugador = 0;
            }
            vista.mostrarInfo("juegos de " + numJugador + ": ");
            vista.mostrarJuegos(enviarJuegosJugador(numJugador));
            numJugador++;
        }
    }

    public void acomodarEnpartidaAjeno(int iCarta, int numCarta, Palo paloCarta, int numJugador, int numJugadorAcomodar, int numJuego) throws RemoteException {
        boolean acomodo = getJugadorPartida(numJugadorAcomodar).comprobarAcomodarCarta(numCarta,paloCarta,numJuego,getRonda());
        if (acomodo) {
            Carta c = getJugadorPartida(numJugador).getMano().remove(iCarta);
            getJugadorPartida(numJugador).getJuegos().get(numJuego
            ).add(c);
            vista.mostrarInfo("Se acomodó la carta en el partida.");
            vista.mostrarInfo("juegos de " + partida.getNombreJugador(numJugadorAcomodar) + ": ");
            vista.mostrarJuegos(enviarJuegosJugador(numJugadorAcomodar));
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private boolean bajarseYComprobarCortar(int numJugador) throws RemoteException {
        boolean puedeCortar = false;
        boolean bajoJuegos = false;
        ArrayList<String> cartas = enviarManoJugador(numJugador);
        while (!puedeCortar && vista.preguntarSiQuiereSeguirBajandoJuegos(cartas)) {
            if (bajarse(numJugador, vista.preguntarQueBajarParaJuego(cartas))) {
                bajoJuegos = true;
                Object[] o = new Object[2];
                o[0] = NOTIFICACION_BAJO_JUEGO;
                o[1] = numJugador;
                partida.notificarObservadores(o);
                cartas = enviarManoJugador(numJugador);
                puedeCortar = ifPartida.comprobarPosibleCorte(getRonda(),
                        getJugadorPartida(numJugador).getTriosBajados(),
                        getJugadorPartida(numJugador).getEscalerasBajadas());
            }
        }
        if (puedeCortar) {
            if(getJugadorPartida(numJugador).getMano().size() <= 1) {
                if (getJugadorPartida(numJugador).getMano().size() == 1)
                    partida.tirarAlPozo(numJugador,0);
                partida.setCorteRonda();
                partida.setPuedeBajar(numJugador, 0);
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
                ifPartida.comprobarJuego(getJugadorPartida(numJugador)
                        .seleccionarCartasABajar(cartasABajar),getRonda());
        if(tipoJuego != ifPartida.JUEGO_INVALIDO) {
            partida.bajarJuego(numJugador, cartasABajar, tipoJuego);
            vista.mostrarJuegos(enviarJuegosJugador(numJugador));
            bajo = true;
        } else {
            vista.mostrarInfo(ifVista.MOSTRAR_JUEGO_INVALIDO);
        }
        return bajo;
    }

    public void tirarAlPozoTurno(int numJugador, ArrayList<String> mano)
            throws RemoteException {
        int cartaATirar = vista.preguntarQueBajarParaPozo(mano);
        partida.tirarAlPozo(numJugador, cartaATirar);
    }

    private void finPartida() throws RemoteException {
        partida.determinarGanador(); //al finalizar las rondas
        partida.serializarGanador();
        partida.notificarObservadores(NOTIFICACION_GANADOR);
        partida.notificarObservadores(NOTIFICACION_FIN_PARTIDA);
        //lo siguiente es para poder seguir jugando otras partidas
        partida.removerObservadores();
        partidasJugadas++;
    }

    public void desarrolloRobo() throws RemoteException {
        int numJugador = partida.getNumTurno();
        int eleccion = Integer.parseInt(
                vista.preguntarInputRobar(enviarManoJugador(numJugador), partida.getNombreJugador(numJugador)));
        if (eleccion == ifVista.ELECCION_ROBAR_DEL_POZO) {
            partida.robarDelPozo(numJugador);
        } else if(eleccion == ifVista.ELECCION_ROBAR_DEL_MAZO) {
            partida.robarDelMazo(numJugador);
        }
    }

    public void roboCastigo() throws RemoteException {
        int numJugadorRoboCastigo = partida.getNumJugadorRoboCastigo();
        if (!getJugadorPartida(numJugadorRoboCastigo).isRoboConCastigo()) {
            boolean roboConCastigo = false;
            ifJugador j = getJugadorPartida(numJugadorRoboCastigo);
            vista.mostrarInfo(j.getNombre() + " puede robar con castigo.");
            if (getJugadorPartida(numJugadorRoboCastigo).getPuedeBajar()==0) {
                String eleccion = vista.preguntarInputRobarCastigo
                        (enviarManoJugador(numJugadorRoboCastigo),
                                partida.getNombreJugador(numJugadorRoboCastigo));
                if (Integer.parseInt(eleccion) == ifVista.ELECCION_ROBAR_CON_CASTIGO
                || vista.isRespAfirmativa(eleccion)) {
                    partida.robarConCastigo(numJugadorRoboCastigo);
                    partida.notificarObservadores(NOTIFICACION_HUBO_ROBO_CASTIGO);
                    roboConCastigo = true;
                }
            }
            if (!roboConCastigo) {
                numJugadorRoboCastigo++;
                if (numJugadorRoboCastigo > getCantJugActuales()-1)
                    numJugadorRoboCastigo = 0;
                partida.setNumJugadorRoboCastigo(numJugadorRoboCastigo);
            }
        }
    }

    public void crearPartida(int cantJugadoresDeseada) throws RemoteException {
        if (partidasJugadas>0)
            //porque al terminar la partida se remueven los observadores
            partida.agregarObservador(this);
        int observadorIndex = partida.getObservadorIndex(this);
        partida.crearYAgregarJugador(vista.getNombreVista(), observadorIndex);
        //partida.notificarObservadores(NOTIFICACION_NUEVO_JUGADOR);
        partida.setCantJugadoresDeseada(cantJugadoresDeseada);
        partida.setEnCurso();
        //partida.notificarObservadores(NOTIFICACION_NUEVA_PARTIDA);
        partida.setNumTurno(observadorIndex);
        partida.setNumJugadorQueEmpezoPartida(observadorIndex);
        partida.notificarObservador(observadorIndex, NOTIFICACION_NUEVA_PARTIDA_PROPIO);
    }

    public Object[] getRanking() throws RemoteException {
        return partida.getRanking().readObjects();
    }

    public Eventos jugarPartidaRecienIniciada() throws RemoteException {
        int i = 0;
        Eventos inicio = PARTIDA_AUN_NO_CREADA;
        boolean encontrado = false;
        if (Partida.getInstancia() != null) {
            int cantJugadoresActuales = getCantJugActuales();
            while (i < cantJugadoresActuales && !encontrado) {
                if (getJugadorPartida(i).getNombre().equals(vista.getNombreVista())) {
                    encontrado = true; //significa que el creó la partida, llamó a esta funcion
                    inicio = FALTAN_JUGADORES;
                }
                i++;
            }
            if (!encontrado) {
            //significa que la vista llamó a esta funcion pero no creó la partida
                partida.crearYAgregarJugador(vista.getNombreVista(), partida.getObservadorIndex(this));
            }
            if (getCantJugActuales() == partida.getCantJugadoresDeseada()) {
                inicio = INICIAR_PARTIDA;
            } else {
                inicio = FALTAN_JUGADORES;
            }
        }
        if (inicio==INICIAR_PARTIDA) {
            partida.notificarObservadores(NOTIFICACION_AGREGAR_OBSERVADOR);
            partida.ponerJugadoresEnOrden();
        }
        return inicio;
    }

    public void notificarComienzoPartida() throws RemoteException {
        partida.notificarObservadores(NOTIFICACION_COMIENZO_PARTIDA);
    }

    private int getCantJugActuales() throws RemoteException {
        return partida.getJugadores().size();
    }

    public boolean isPartidaEnCurso() throws RemoteException {
        return partida.isEnCurso();
    }
}
