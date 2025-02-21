package controlador;

import modelo.ifPartida;
import modelo.ifCarta;
import modelo.Eventos;
import modelo.Mano;
import modelo.Carta;
import static modelo.Eventos.*;
import vista.ifVista;
import rmimvc.src.cliente.IControladorRemoto;
import rmimvc.src.observer.IObservableRemoto;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;

public class Controlador implements IControladorRemoto {
    private ifVista vista;
    private ifPartida partida;

    public Controlador() {}

    public void setVista(ifVista vista) {
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
                case NOTIFICACION_CAMBIO_TURNO: {
                    vista.cambioTurno();
                    break;
                }
                case NOTIFICACION_ACTUALIZAR_POZO: {
                    ifCarta pozo = partida.getPozo();
                    String actualizar = "";
                    if (pozo != null) {
                        actualizar = ifVista.cartaToString(pozo);
                    }
                    vista.actualizarPozo(actualizar);
                    break;
                }
                case NOTIFICACION_ACTUALIZAR_JUEGOS: {
                    vista.actualizarRestricciones(false);
                    vista.actualizarJuegos();
                    break;
                }
                case NOTIFICACION_ROBO_CASTIGO: {
                    roboCastigo();
                    break;
                }
                case NOTIFICACION_HUBO_ROBO_CASTIGO: {
                    String nom = partida.getNombreJugador(partida.getNumJugadorRoboCastigo());
                    vista.mostrarInfo(nom + " ha robado con castigo.");
                    break;
                }
                case NOTIFICACION_NO_PUEDE_ROBO_CASTIGO: {
                    vista.actualizarRestricciones(true);
                    break;
                }
                case NOTIFICACION_COMIENZO_RONDA:
                    vista.comienzoRonda(partida.getNumRonda());
                    break;
                case NOTIFICACION_NUEVA_PARTIDA: {
                    vista.nuevaPartida();
                    break;
                }
                case NOTIFICACION_AGREGAR_OBSERVADOR:
                    int observadorIndex = partida.getObservadorIndex(this);
                    int numJugador = partida.getNumeroJugador(vista.getNombreVista());
                    if (numJugador != observadorIndex) {
                        partida.setNumeroJugador(numJugador, observadorIndex);
                    }
                    break;
                case NOTIFICACION_CORTE_RONDA: {
                    String nombreJ = partida.getNombreJugador(partida.getNumTurno());
                    if (!nombreJ.equals(vista.getNombreVista())) {
                        vista.mostrarInfo(nombreJ + " ha cortado.");
                    } else {
                        vista.mostrarInfo("Has cortado. Felicitaciones!");
                    }
                    vista.mostrarInfo("Finalizó la ronda " + (partida.getNumRonda()-1));
                    break;
                }
                case NOTIFICACION_PUNTOS: {
                    vista.mostrarPuntosRonda(partida.getPuntosJugadores());
                    break;
                }
                case NOTIFICACION_GANADOR: {
                    vista.mostrarInfo(partida.getGanador() + " es el ganador!");
                    break;
                }
                case NOTIFICACION_BAJO_JUEGO: {
                    mostrarJuegosEnMesa();
                    break;
                }
                case NOTIFICACION_NUMERO_JUGADOR: {
                    vista.setNumeroJugadorTitulo();
                    break;
                }
                case NOTIFICACION_SALIR: {
                    vista.setSalir();
                    break;
                }
                case NOTIFICACION_SALIR_MENU: {
                    vista.salirAlMenu();
                    break;
                }
                case NOTIFICACION_ELEGIR_JUGADOR: {
                    ArrayList<String> nombresDisponibles = partida.getNombreJugadores();
                    vista.elegirJugador(nombresDisponibles);
                    break;
                }
                case NOTIFICACION_FIN_PARTIDA: {
                    vista.finPartida();
                    break;
                }
                case NOTIFICACION_ESPERA: {
                    vista.esperarRoboCastigo();
                    break;
                }
                case NOTIFICACION_TERMINA_ESPERA: {
                    vista.terminaEsperaRoboCastigo();
                    break;
                }
            }
        }
        else if (o instanceof Mano mano) {
            vista.actualizarManoJugador(enviarManoJugador(mano.get()));
        }
    }

    public String getTurnoDe() {
        try {
            int numTurno = partida.getNumTurno();
            partida.setTurnoJugador(numTurno,true);
            return partida.getNombreJugador(numTurno);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchMenuBajar(String eleccion) {
        try {
            partida.switchMenuBajar(eleccion);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void finTurno() {
        try {
            if (vista.isActiva()) {
                partida.finTurno();
            } else {
                partida.setEjecutarFinTurno(true);
                partida.guardar();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTurnoActual() {
        try {
            return partida.isTurnoActual(partida.getNumTurno());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void cambioTurno() {
        try {
            if (vista.isActiva()) {
                partida.notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
            } else {
                vista.salirAlMenu();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void desarrolloRobo(String eleccion) {
        try {
            partida.desarrolloRobo(eleccion);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void ordenarCartas(int numJugador) throws RemoteException {
        int[] cartasOrdenacion = vista.preguntarParaOrdenarCartas();
        partida.moverCartaEnMano(numJugador, cartasOrdenacion[0], cartasOrdenacion[1]);
    }

    private void acomodarPropio(int numJugador) throws RemoteException {
        ArrayList<ArrayList<String>> juegos = enviarJuegosJugador(numJugador);
        if (!juegos.isEmpty()) {
            int cartaAcomodar = vista.preguntarCartaParaAcomodar();
            int numJuego = 0;
            if (juegos.size()>1) {
                numJuego = Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO)) - 1;
            }
            if(partida.comprobarAcomodarCartaPropio(numJugador, cartaAcomodar, numJuego)) {
                partida.acomodarPropio(numJugador,cartaAcomodar,numJuego);
                vista.mostrarAcomodoCarta();
                partida.notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                partida.actualizarMano(numJugador);
            } else {
                vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
            }
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private void acomodarAjeno(int numJugador) throws RemoteException {
        int numJugadorAcomodar;
        if (getCantJugActuales()>2) {
            numJugadorAcomodar = vista.getNumJugadorAcomodar();
        } else {
            if (numJugador==0) {
                numJugadorAcomodar = 1;
            } else numJugadorAcomodar = 0;
        }
        ArrayList<ArrayList<String>> juegos = enviarJuegosJugador(numJugadorAcomodar);
        if (!juegos.isEmpty()) {
            int iCartaAcomodar = vista.preguntarCartaParaAcomodar();
            int numJuego = 0;
            if (juegos.size()>1) {
                numJuego = Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO)) - 1;
            }
            if (partida.comprobarAcomodarAjeno(numJugador,numJugadorAcomodar,
                    iCartaAcomodar,numJuego)) {
                partida.acomodarAjeno(numJugador,numJugadorAcomodar,iCartaAcomodar,numJuego);
                vista.mostrarAcomodoCarta();
                partida.notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                partida.actualizarMano(numJugador);
            } else {
                vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
            }
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private void mostrarJuegosEnMesa() throws RemoteException {
        vista.actualizarJuegos();
        //cada vista tiene que mostrar los juegos de cada jugador
        for (int j = 0; j < getCantJugActuales(); j++) {
            vista.mostrarJuegos(partida.getNombreJugador(j),
                    enviarJuegosJugador(j));
        }
    }

    public int getNumJugador(String nombreJugador) {
        try {
            return partida.getNumJugador(nombreJugador);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> enviarManoJugador(ArrayList<Carta> mano)
            throws RemoteException {
        ArrayList<ifCarta> cs = new ArrayList<>(mano);
        return ifVista.cartasToStringArray(cs);
    }

    public ArrayList<ArrayList<String>> enviarJuegosJugador(int numJugador)
            throws RemoteException {
        ArrayList<ArrayList<Carta>> juegos = partida.getJuegos(numJugador);
        ArrayList<ArrayList<String>> juegosString = new ArrayList<>();
        for (ArrayList<Carta> juego : juegos) {
            ArrayList<ifCarta> cs = new ArrayList<>(juego); //cast a ifCarta
            juegosString.add(ifVista.cartasToStringArray(cs));
        }
        return juegosString;
    }

    private void tirarAlPozoTurno()
            throws RemoteException {
        int cartaATirar = vista.preguntarQueBajarParaPozo();
        partida.tirarAlPozo(cartaATirar);
    }

    private void roboCastigo() throws RemoteException {
        if (!partida.getJugadoresQuePuedenRobarConCastigo().isEmpty()) {
            if (vista.preguntarInputRobarCastigo()) {
                partida.robarConCastigo();
                partida.setJugadoresQuePuedenRobarConCastigo(); //se resetea así no continua el robo castigo
            } else {
                partida.incNumJugadorRoboCastigo();
            }
        }
    }

    public void crearPartida(int cantJugadoresDeseada) {
        int observadorIndex;
        try {
            observadorIndex = partida.getObservadorIndex(this);
            partida.crearJugador(vista.getNombreVista(), observadorIndex);
            partida.crearPartida(observadorIndex, cantJugadoresDeseada);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] getRanking() {
        try {
            return partida.getRanking().readObjects();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Eventos jugarPartidaRecienIniciada() throws RemoteException {
        int i = 0;
        Eventos inicio;
        boolean encontrado = false;
        int cantJugadoresActuales = getCantJugActuales();
        while (i < cantJugadoresActuales && !encontrado) {
            String nombreJugador = partida.getNombreJugador(i);
            String nombreVista = vista.getNombreVista();
            if (nombreJugador.equals(nombreVista)) {
                encontrado = true; //significa que el creó la partida, llamó a esta funcion
            }
            i++;
        }
        if (!encontrado) {
        //significa que la vista llamó a esta funcion pero no creó la partida
            partida.crearJugador(vista.getNombreVista(), partida.getObservadorIndex(this));
        }
        int cantActual = getCantJugActuales();
        int cantDeseada = partida.getCantJugadoresDeseada();
        if (cantActual == cantDeseada) {
            inicio = INICIAR_PARTIDA;
        } else {
            inicio = FALTAN_JUGADORES;
        }

        if (inicio==INICIAR_PARTIDA) {
            partida.notificarObservadores(NOTIFICACION_AGREGAR_OBSERVADOR);
            partida.ponerJugadoresEnOrden();
        }
        return inicio;
    }

    public void empezarRonda() {
        try {
            partida.empezarRonda();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private int getCantJugActuales() throws RemoteException {
        return partida.getCantJugadores();
    }

    public boolean isPartidaEnCurso() {
        try {
            return partida.isEnCurso();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarPartida() {
        try {
            partida.guardar();
            int numJugadorQueLlamo = getNumJugador(vista.getNombreVista());
            if (numJugadorQueLlamo!=partida.getNumTurno()) {
                partida.notificarSalir(); //sale al menu para todas las vistas menos la del turno actual
                partida.notificarObservador(partida.getNumTurno(), NOTIFICACION_SALIR);
            } else {
                partida.notificarObservadores(NOTIFICACION_SALIR_MENU);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cargarPartida() {
        try {
            if(partida.cargarPartida()) {
                partida.notificarObservadores(NOTIFICACION_ELEGIR_JUGADOR);
            } else {
                return false;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean agregarNombreElegido(String nombre) {
        try {
            //ya están los jugadores creados, debo cambiar los nums de los jugadores
            //para que matcheen con el nombre que eligieron y el num de observador
            //para este momento ya se cargó una partida
            boolean agregado = partida.agregarNombreElegido(nombre);
            if (agregado) {
                vista.setActiva(true); //activa se pone en false al guardar la partida, y puede quedar la vista abierta por eso importa cambiarla
                vista.inicializarMenu();
                int obsIndex = partida.getObservadorIndex(this);
                int numJugador = partida.getNumJugador(vista.getNombreVista());
                partida.setNumeroJugador(numJugador,obsIndex); //se cambia el atr. numJugador del jugador
                partida.comprobarEmpezarPartida();
            }
            return agregado;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNumRonda() {
        try {
            return partida.getNumRonda();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}