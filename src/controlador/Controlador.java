package controlador;

import excepciones.FaltanJugadoresException;
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
            switch (eleccion) {
                case ifVista.BAJARSE -> {
                    if(vista.preguntarSiQuiereSeguirBajandoJuegos()) {
                        int[] indicesCartas;
                        do {
                            indicesCartas = vista.preguntarQueBajarParaJuego();
                        } while (partida.hayRepetidos(indicesCartas));
                        if (!partida.bajarse(indicesCartas)) {
                            vista.mostrarInfo(ifVista.MOSTRAR_JUEGO_INVALIDO);
                        }
                    }
                }
                case ifVista.TIRAR -> partida.tirarAlPozo(vista.preguntarQueBajarParaPozo());
                case ifVista.ORDENAR -> {
                    int[] cartasOrdenacion = vista.preguntarParaOrdenarCartas();
                    partida.moverCartaEnMano(cartasOrdenacion[0], cartasOrdenacion[1]);
                }
                case ifVista.ACOMODAR -> {
                    int cartaAcomodar = vista.preguntarCartaParaAcomodar();
                    int[] seleccion = vista.seleccionarJuego(juegosMesaToString());
                    int iJuego = seleccion[0];
                    int numJugador = seleccion[1];
                    boolean acomodar = partida.acomodar(cartaAcomodar, iJuego, numJugador);
                    if (acomodar) {
                        vista.mostrarAcomodoCarta();
                    } else {
                        vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
                    }
                }
            }
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
            partida.notificarObservadores(NOTIFICACION_CAMBIO_TURNO);
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

    public ArrayList<ArrayList<ArrayList<String>>> juegosMesaToString() {
        try {
            ArrayList<ArrayList<ArrayList<Carta>>> juegosMesa = partida.enviarJuegosEnMesa();
            ArrayList<ArrayList<ArrayList<String>>> juegosString = new ArrayList<>();
            for (ArrayList<ArrayList<Carta>> juegoMesa : juegosMesa) {
                ArrayList<ArrayList<String>> juegoString = new ArrayList<>();
                for (ArrayList<Carta> juego : juegoMesa) {
                    ArrayList<ifCarta> cs = new ArrayList<>(juego);
                    juegoString.add(ifVista.cartasToStringArray(cs));
                }
                juegosString.add(juegoString);
            }
            return juegosString;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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

    public void crearPartida() {
        try {
            boolean cantValida = false;
            int observadorIndex = partida.getObservadorIndex(this);
            partida.crearJugador(vista.getNombreVista(), observadorIndex);//valida si ya existe
            while (!cantValida) {
                try {
                    if (partida.getCantJugadoresDeseada()==0) { //si no se estableció aún
                        int cantJugadoresDeseada = vista.preguntarCantJugadoresPartida();
                        try {
                            partida.setCantJugadoresDeseada(cantJugadoresDeseada);
                            cantValida = true; //si no lanza excepción el metodo anterior
                            partida.inicializarPartida(observadorIndex);
                        } catch (IllegalArgumentException e) {
                            vista.mostrarInfo(e.getMessage());
                        }
                    }
                    try {
                        cantValida = true;
                        partida.jugarPartida();
                    } catch (FaltanJugadoresException e) {
                        vista.mostrarInfo(e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    vista.mostrarInfo("Ingresa un número");
                } catch (IllegalArgumentException e) {
                    vista.mostrarInfo(e.getMessage());
                }
            }
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