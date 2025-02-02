package controlador;

import modelo.ifPartida;
import modelo.ifJugador;
import modelo.ifCarta;
import modelo.Carta;
import modelo.Eventos;
import modelo.NotificacionActualizarMano;
import modelo.Comprobar;
import rmimvc.src.cliente.IControladorRemoto;
import rmimvc.src.observer.IObservableRemoto;
import vista.ifVista;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;
import static modelo.Eventos.*;

public class Controlador implements IControladorRemoto {
    ifVista vista;
    ifPartida partida;

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
                    String nom = getJugadorPartida(partida.getNumJugadorRoboCastigo()).getNombre();
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
                    int numJugador = partida.getJugador(vista.getNombreVista()).getNumeroJugador();
                    if (numJugador != observadorIndex) {
                        partida.setNumeroJugador(numJugador, observadorIndex);
                    }
                    break;
                case NOTIFICACION_CORTE_RONDA: {
                    String nombreJ = getJugadorPartida(partida.getNumJugadorCorte()).getNombre();
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
                    String ganador = partida.getGanador().getNombre();
                    vista.mostrarInfo(ganador + " es el ganador!");
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
            }
        }
        else if (o instanceof NotificacionActualizarMano notif) {
            vista.actualizarManoJugador(enviarManoJugador(notif.cartas()));
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

    public ifJugador getJugadorPartida(int numJugadorPartida) {
        try {
            return partida.getJugadores().get(numJugadorPartida);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchMenuBajar(int eleccion) {
        try {
            int numJugador = partida.getNumTurno();
            if (partida.isTurnoActual(numJugador)) {
                switch (eleccion) {
                    case ifVista.ELECCION_BAJARSE:
                        bajarJuegos(numJugador);
                        break;
                    case ifVista.ELECCION_TIRAR_AL_POZO:
                        tirarAlPozoTurno();
                        break;
                    case ifVista.ELECCION_ORDENAR_CARTAS:
                        ordenarCartas(numJugador);
                        break;
                    case ifVista.ELECCION_ACOMODAR_JUEGO_PROPIO:
                        acomodarPropio(numJugador);
                        break;
                    case ifVista.ELECCION_ACOMODAR_JUEGO_AJENO:
                        acomodarAjeno(numJugador);
                        break;
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void finTurno() {
        try {
            partida.finTurno();
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
            if (Objects.equals(eleccion, ifVista.ELECCION_ROBAR_DEL_MAZO)) {
                partida.robarDelMazo();
            } else if (Objects.equals(eleccion, ifVista.ELECCION_ROBAR_DEL_POZO)) {
                partida.robarDelPozo();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void bajarJuegos(int numJugador) throws RemoteException {
        int cantVecesQueBajo = partida.getPuedeBajar(numJugador);
        if (cantVecesQueBajo == 0 || cantVecesQueBajo == 1) {
            bajarseYComprobarCortar(numJugador);
        } else {
            vista.mostrarInfo(ifVista.YA_NO_PUEDE_BAJAR);
        }
    }

    public void ordenarCartas(int numJugador) throws RemoteException {
        int[] cartasOrdenacion = vista.preguntarParaOrdenarCartas();
        partida.moverCartaEnMano(numJugador, cartasOrdenacion[0], cartasOrdenacion[1]);
    }

    public void acomodarPropio(int numJugador) throws RemoteException {
        ArrayList<ArrayList<String>> juegos = enviarJuegosJugador(numJugador);
        if (!juegos.isEmpty()) {
            int cartaAcomodar = vista.preguntarCartaParaAcomodar();
            int numJuego = 0;
            if (juegos.size()>1) {
                numJuego = Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO)) - 1;
            }
            if(partida.comprobarAcomodarCartaPropio(numJugador, cartaAcomodar, numJuego)) {
                partida.acomodarEnJuegoPropio(numJugador,cartaAcomodar,numJuego);
                vista.mostrarAcomodoCarta(partida.getNombreJugador(numJugador));
                partida.notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                partida.actualizarMano(numJugador);
            } else {
                vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
            }
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    public void acomodarAjeno(int numJugador) throws RemoteException {
        if (hayJuegosEnMesa(numJugador)) {
            int iCartaAcomodar = vista.preguntarCartaParaAcomodar();
            int numJugadorAcomodar = vista.getNumJugadorAcomodar();
            int numJuego = Integer.parseInt(vista.preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO))-1;
            if (partida.comprobarAcomodarCartaAjeno(numJugador,numJugadorAcomodar,iCartaAcomodar,numJuego)) {
                partida.acomodarEnJuegoAjeno(numJugador,numJugadorAcomodar,iCartaAcomodar,numJuego);
                vista.mostrarAcomodoCarta(partida.getNombreJugador(numJugadorAcomodar));
                partida.notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                partida.actualizarMano(numJugador);
            } else {
                vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
            }
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    public void mostrarJuegosEnMesa() throws RemoteException {
        vista.actualizarJuegos();
        for (int j = 0; j < getCantJugActuales(); j++) {
            vista.mostrarJuegos(partida.getNombreJugador(j),
                    enviarJuegosJugador(j));
        }
    }

    public int getNumJugador(String nombreJugador) {
        try {
            return partida.getNumJugador(nombreJugador)+1;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> enviarManoJugador(ArrayList<Carta> mano)
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

    private void bajarseYComprobarCortar(int numJugador) throws RemoteException {
        Eventos puedeCortar = NO_PUEDE_CORTAR;
        while (puedeCortar==NO_PUEDE_CORTAR && vista.preguntarSiQuiereSeguirBajandoJuegos()) {
            int[] indicesCartas = vista.preguntarQueBajarParaJuego();
            while (hayRepetidos(indicesCartas)) {
                vista.mostrarInfo("Debe ingresar los índices de nuevo");
                indicesCartas = vista.preguntarQueBajarParaJuego();
            }
            if (partida.comprobarBajarse(numJugador, indicesCartas)
            != Comprobar.JUEGO_INVALIDO) {
                partida.incPuedeBajar(numJugador);
                partida.notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                partida.actualizarMano(numJugador);
                puedeCortar = partida.comprobarPosibleCorte(numJugador);
            } else {
                vista.mostrarInfo(ifVista.MOSTRAR_JUEGO_INVALIDO);
            }
        }
        if (puedeCortar == PUEDE_CORTAR) {
            boolean corte = partida.cortar(numJugador);
            if(!corte)
                vista.mostrarInfo("Para cortar debe quedarte en la mano 1 o 0 cartas");
        } else if (puedeCortar == SOBRAN_CARTAS) {
            vista.mostrarInfo("Ya no puede bajar. Debe acomodar las cartas que le sobraron.");
        } else {
            int[] faltante = partida.comprobarQueFaltaParaCortar(numJugador);
            vista.mostrarInfo("Para cortar faltan " + faltante[0] + " trios y " + faltante[1] + " escaleras");
        }
    }

    private static boolean hayRepetidos(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] == array[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    public void tirarAlPozoTurno()
            throws RemoteException {
        int numJugador = partida.getNumTurno();
        int cartaATirar = vista.preguntarQueBajarParaPozo();
        partida.tirarAlPozo(numJugador, cartaATirar);
    }

    public void roboCastigo() throws RemoteException {
        if (!partida.getJugadoresQuePuedenRobarConCastigo().isEmpty()) {
            if (vista.preguntarInputRobarCastigo()) {
                partida.robarConCastigo();
                partida.setJugadoresQuePuedenRobarConCastigo(); //se resetea así no continua el robo castigo
            }
        }
    }

    public void crearPartida(int cantJugadoresDeseada) {
        int observadorIndex;
        try {
            observadorIndex = partida.getObservadorIndex(this);
            partida.crearYAgregarJugador(vista.getNombreVista(), observadorIndex);
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
            String nombreJugador = getJugadorPartida(i).getNombre();
            String nombreVista = vista.getNombreVista();
            if (nombreJugador.equals(nombreVista)) {
                encontrado = true; //significa que el creó la partida, llamó a esta funcion
            }
            i++;
        }
        if (!encontrado) {
        //significa que la vista llamó a esta funcion pero no creó la partida
            partida.crearYAgregarJugador(vista.getNombreVista(), partida.getObservadorIndex(this));
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
        return partida.getJugadores().size();
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
            partida.guardarPartida();
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

    public void salirAlMenu() {
        try {
            partida.notificarObservadores(NOTIFICACION_SALIR);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean agregarNombreElegido(String nombre) {
        try {
            //ya están los jugadores creados, debo cambiar los nums de los jugadores
            //para que matcheen con el nombre que eligieron y el num de observador
            int obsIndex = partida.getObservadorIndex(this);
            partida.setNumeroJugador(partida.getNumJugador(vista.getNombreVista()),obsIndex);
            return partida.agregarNombreElegido(nombre);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
