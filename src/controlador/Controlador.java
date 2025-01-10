package controlador;

import modelo.*;
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
                case NOTIFICACION_CAMBIO_TURNO: {
                    vista.cambioTurno();
                    break;
                }
                case NOTIFICACION_ACTUALIZAR_POZO: {
                    Carta pozo = partida.getPozo();
                    String actualizar = "";
                    if (pozo != null) {
                        actualizar = ifVista.cartaToString(pozo);
                    }
                    vista.actualizarPozo(actualizar);
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
                case NOTIFICACION_COMIENZO_RONDA:
                    vista.comienzoRonda(partida.getNumRonda());
                    break;
//                case NOTIFICACION_NUEVO_JUGADOR:
//                    String nombreJugador = partida.getJugadores().get(partida.getJugadores().size()-1).getNombre();
//                    vista.mostrarInfo(nombreJugador + " ha ingresado.");
//                    break;
                case NOTIFICACION_NUEVA_PARTIDA_PROPIO: {
                    vista.mostrarInfo("Se ha iniciado una partida.");
                    break;
                }
//                case NOTIFICACION_COMIENZO_PARTIDA:
//                    ArrayList<String> jugadores = new ArrayList<>();
//                    for (Jugador j : partida.getJugadores()) {
//                        jugadores.add(j.getNombre());
//                    }
//                    vista.mostrarComienzaPartida(jugadores);
//                    break;
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
                    partidasJugadas++;
                    String ganador = partida.getGanador().getNombre();
                    vista.mostrarInfo(ganador + " es el ganador!");
                    break;
                }
//                case NOTIFICACION_NUEVA_PARTIDA: {
//                    String nombre = getJugadorPartida(partida.getNumJugadorQueEmpezoPartida()).getNombre();
//                    vista.mostrarInfo(nombre + " ha iniciado una partida nueva");
//                    break;
//                }
                case NOTIFICACION_BAJO_JUEGO: {
                    String nombre = partida.getNombreJugador(partida.getNumTurno());
                    if (!vista.getNombreVista().equals(nombre)) {
                        //vista.mostrarInfo(nombre + " bajó un juego.");
                        //MOSTRAR DE OTRA FORMA!!
                    }
                    break;
                }
            }
        }
        else if (o instanceof Object[] obj) {
            if (obj[0] == NOTIFICACION_ACTUALIZAR_MANO) {
                vista.actualizarManoJugador(enviarManoJugador((ArrayList<Carta>) obj[1]));
            }
        }
    }

    public void partida() throws RemoteException {
        partida.inicioRonda();
    }

    public String getTurnoDe() {
        try {
            return partida.getNombreJugador(partida.getNumTurno());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getPuntos() {
        try {
            return partida.getPuntosJugadores();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getJugadores() {
        ArrayList<String> nombresJugadores = new ArrayList<>();
        try {
            for (Jugador j : partida.getJugadores()) {
                nombresJugadores.add(j.getNombre());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return nombresJugadores;
    }

    public int getRonda() throws RemoteException {
        return partida.getNumRonda();
    }

    public ifCarta getPozo() throws RemoteException {
        return partida.getPozo();
    }

    public ifJugador getJugadorPartida(int numJugadorPartida) throws RemoteException {
        return partida.getJugadores().get(numJugadorPartida);
    }

    public boolean switchMenuBajar(int eleccion) throws RemoteException {
        int numJugador = partida.getNumTurno();
        boolean bajoJuegos = false;
        if (partida.isTurnoActual(numJugador)) {
            switch (eleccion) {
                case ifVista.ELECCION_BAJARSE:
                    bajoJuegos = bajarJuegos(numJugador);
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
                case ifVista.ELECCION_VER_JUEGOS_BAJADOS:
                    vista.mostrarJuegos(enviarJuegosJugador(numJugador));
                    break;
                case ifVista.ELECCION_VER_JUEGOS_BAJADOS_MESA:
                    mostrarJuegosEnMesa(numJugador + 1);
                    break;
                case ifVista.ELECCION_VER_POZO:
                    vista.mostrarInfo(ifVista.getPozoString(getPozo()));
                    break;
            }
        }
        return bajoJuegos;
    }

    public void desarrolloTurno() throws RemoteException {
        int numJugador = partida.getNumTurno();
        partida.actualizarMano(numJugador);
        while(partida.isTurnoActual(numJugador)) {
            int eleccion = vista.menuBajar(ifVista.mostrarCombinacionRequerida(getRonda()));
            switchMenuBajar(eleccion);
            partida.actualizarMano(numJugador);
        }
        partida.finTurno();
    }

    public void desarrolloRobo() {
        String eleccion = vista.preguntarInputRobar();
        if (Objects.equals(eleccion, "1")) {
            robarDelMazo();
        } else if (Objects.equals(eleccion, "2")) {
            robarDelPozo();
        }
    }

    public void robarDelPozo() {
        try {
            partida.robarDelPozo();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void robarDelMazo() {
        try {
            partida.robarDelMazo();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean bajarJuegos(int numJugador) throws RemoteException {
        int cantVecesQueBajo = partida.getPuedeBajar(numJugador);
        if (cantVecesQueBajo == 0) {
            vista.mostrarInfo(ifVista.ADVERTENCIA_BAJARSE);
        } else if (cantVecesQueBajo == 1) {
            vista.mostrarInfo("Debes tener los partidas requeridos para la" +
                    " ronda y cortar si deseas bajar ahora.");
        } else {
            vista.mostrarInfo(ifVista.YA_NO_PUEDE_BAJAR);
        }
        return bajarseYComprobarCortar(numJugador);
    }

    public void ordenarCartas(int numJugador) throws RemoteException {
        int[] cartasOrdenacion = vista.preguntarParaOrdenarCartas();
        partida.moverCartaEnMano(numJugador, cartasOrdenacion[0], cartasOrdenacion[1]);
    }

    public void acomodarPropio(int numJugador) throws RemoteException {
        ArrayList<ArrayList<String>> juegos = enviarJuegosJugador(numJugador);
        if (!juegos.isEmpty()) {
            int cartaAcomodar = vista.preguntarCartaParaAcomodar();
            vista.mostrarJuegos(juegos);
            acomodarEnJuegoPropio(cartaAcomodar,numJugador,
                    Integer.parseInt(vista
                            .preguntarInput(ifVista.PREGUNTA_NUMERO_JUEGO))-1);
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    public void acomodarAjeno(int numJugador) throws RemoteException {
        if (hayJuegosEnMesa(numJugador)) {
            mostrarJuegosEnMesa(numJugador+1);
            int iCartaAcomodar =
                    vista.preguntarCartaParaAcomodar();
            int numJugadorAcomodar = vista.getNumJugadorAcomodar();
            vista.mostrarJuegos(enviarJuegosJugador(numJugadorAcomodar));
            acomodarEnJuegoAjeno(iCartaAcomodar,
                    numJugador, numJugadorAcomodar, Integer
                            .parseInt(vista.preguntarInput(
                                    ifVista.PREGUNTA_NUMERO_JUEGO))-1);
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    public ArrayList<String> enviarManoJugador(ArrayList<Carta> mano) throws RemoteException {
        ArrayList<ifCarta> cs = new ArrayList<>(mano);
        return ifVista.cartasToStringArray(cs);
    }

    public ArrayList<ArrayList<String>> enviarJuegosJugador(int numJugador) throws RemoteException {
        ArrayList<ArrayList<Carta>> juegos = partida.getJuegos(numJugador);
        ArrayList<ArrayList<String>> juegosString = new ArrayList<>();
        for (ArrayList<Carta> juego : juegos) {
            ArrayList<ifCarta> cs = new ArrayList<>(juego); //cast a ifCarta
            juegosString.add(ifVista.cartasToStringArray(cs));
        }
        return juegosString;
    }

    public void acomodarEnJuegoPropio(int iCarta, int numJugador, int numJuego)
            throws RemoteException {
        if(partida.comprobarAcomodarCarta(numJugador, iCarta, numJuego, getRonda())) {
            vista.mostrarAcomodoCarta(partida.getNombreJugador(numJugador));
            vista.mostrarJuegos(enviarJuegosJugador(numJugador));
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

    public void acomodarEnJuegoAjeno(int iCarta,
             int numJugador, int numJugadorAcomodar, int numJuego) throws RemoteException {
        if (partida.comprobarAcomodarCarta(numJugadorAcomodar,iCarta,numJuego,getRonda())) {
            partida.acomodarEnJuegoAjeno(numJugador,iCarta,numJuego);
            vista.mostrarAcomodoCarta(partida.getNombreJugador(numJugadorAcomodar));
            vista.mostrarJuegos(enviarJuegosJugador(numJugadorAcomodar));
        } else {
            vista.mostrarInfo(ifVista.NO_PUEDE_ACOMODAR);
        }
    }

    private boolean bajarseYComprobarCortar(int numJugador) throws RemoteException {
        boolean puedeCortar = false;
        boolean bajoJuegos = false;
        while (!puedeCortar && vista.preguntarSiQuiereSeguirBajandoJuegos()) {
            if (bajarse(numJugador, vista.preguntarQueBajarParaJuego())) {
                bajoJuegos = true;
                partida.notificarObservadores(NOTIFICACION_BAJO_JUEGO);
                puedeCortar = Comprobar.comprobarPosibleCorte(getRonda(),
                        partida.getTriosBajados(numJugador),
                        partida.getEscalerasBajadas(numJugador));
            }
        }
        if (puedeCortar) {
            if(!partida.cortar(numJugador))
                vista.mostrarInfo("Para cortar debe quedarte en la mano 1 o 0 cartas");
        } else {
            int[] faltante = partida.comprobarQueFaltaParaCortar(numJugador);
            vista.mostrarInfo("Para cortar faltan " + faltante[0] + " trios y " + faltante[1] + " escaleras");
        }
        return bajoJuegos;
    }

    public boolean bajarse(int numJugador, int [] cartasABajar) throws RemoteException {
        boolean bajo = false;
        int tipoJuego = partida.comprobarBajarse(numJugador, cartasABajar);
        if(tipoJuego != Comprobar.JUEGO_INVALIDO) {
            vista.mostrarJuegos(enviarJuegosJugador(numJugador));
            bajo = true;
        } else {
            vista.mostrarInfo(ifVista.MOSTRAR_JUEGO_INVALIDO);
        }
        return bajo;
    }

    public void tirarAlPozoTurno()
            throws RemoteException {
        int numJugador = partida.getNumTurno();
        int cartaATirar = vista.preguntarQueBajarParaPozo();
        partida.tirarAlPozo(numJugador, cartaATirar);
    }

    public void roboCastigo() throws RemoteException {
        //int numJugadorRobo = partida.getNumJugadorRoboCastigo();
        //String nombre = partida.getNombreJugador(numJugadorRobo);
        //vista.mostrarInfo(nombre + " puede robar con castigo.");
        String eleccion = vista.preguntarInputRobarCastigo();
        if (Integer.parseInt(eleccion) == ifVista.ELECCION_ROBAR_CON_CASTIGO
                || ifVista.isRespAfirmativa(eleccion)) {
            partida.robarConCastigo();
        } else {
            partida.removeJugadorRoboCastigo();
        }
    }

    public void crearPartida(int cantJugadoresDeseada) throws RemoteException {
        if (partidasJugadas>0)
            //porque al terminar la partida se remueven los observadores
            partida.agregarObservador(this);
        int observadorIndex = partida.getObservadorIndex(this);
        partida.crearPartida(vista.getNombreVista(), observadorIndex, cantJugadoresDeseada);
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
