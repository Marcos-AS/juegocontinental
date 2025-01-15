package vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VentanaConsola extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;
    private JPanel panelMano;
    private JPanel panelPozo;
    private JPanel panelInfoRonda;
    private JPanel panelPuntuacion;
    private JPanel panelJuegos;
    private JPanel panelRestricciones;
    private int manoSize;


    public void iniciar() throws RemoteException {
        nombreVista = preguntarInput("Indica tu nombre:");
        setFrame();
        opcionesIniciales();
    }

    private void setFrame() {
        JFrame frame = new JFrame("Mano " + nombreVista);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(600,800);
        panelPozo = new JPanel();
        panelMano = new JPanel();
        panelInfoRonda = new JPanel();
        panelPuntuacion = new JPanel();
        panelJuegos = new JPanel();
        panelRestricciones = new JPanel();
        frame.add(panelPuntuacion);
        frame.add(panelInfoRonda);
        frame.add(panelPozo);
        frame.add(panelMano);
        frame.add(panelJuegos);
        frame.add(panelRestricciones);
        frame.setVisible(true);
    }

    public void opcionesIniciales() throws RemoteException {
        int eleccion;
        boolean partidaCreada = false;
        boolean partidaIniciada = false;
        do {
            eleccion = ifVista.menuInicial(ctrl.isPartidaEnCurso(), nombreVista);
            switch (eleccion) {
                case ifVista.ELECCION_CREAR_PARTIDA: {
                    if (!ctrl.isPartidaEnCurso()) {
                        int cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
                                " deseas para la nueva partida?"));
                        ctrl.crearPartida(cantJugadores);
                        partidaCreada = true;
                    } else {
                        mostrarInfo("Ya hay una partida en curso");
                    }
                    break;
                }
                case ifVista.ELECCION_JUGAR_PARTIDA: {
                    if (partidaCreada || ctrl.isPartidaEnCurso()) {
                        if (nombreVista == null) nombreVista = preguntarInput("Indica tu nombre: ");
                        int inicioPartida = ctrl.jugarPartidaRecienIniciada().ordinal();
                        if (inicioPartida == PARTIDA_AUN_NO_CREADA) {
                            mostrarInfo("La partida aun no ha sido creada." +
                                    " Seleccione la opción 'Crear partida' ");
                        } else if (inicioPartida == FALTAN_JUGADORES) {
                            mostrarInfo("Esperando que ingresen más jugadores...");
                        } else if (inicioPartida == INICIAR_PARTIDA) {
                            ctrl.empezarRonda();
                            ctrl.cambioTurno();
                        }
                    } else {
                        mostrarInfo("Primero tienes que crear una partida");
                    }
                    break;
                }
                case ifVista.ELECCION_RANKING: {
                    mostrarRanking(ctrl.getRanking());
                    break;
                }
                case ifVista.ELECCION_REGLAS: {
                    mostrarInfo(ifVista.REGLAS);
                    break;
                }
            }
        } while (eleccion != -1 && !partidaIniciada);
    }

    @Override
    public void cambioTurno() {
        String nombre = ctrl.getTurnoDe();
        if (nombre.equals(nombreVista)) {
            ctrl.desarrolloRobo(preguntarInputRobar());
            try {
                ctrl.desarrolloTurno();
                ctrl.cambioTurno();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void actualizarManoJugador(ArrayList<String> cartas) {
        manoSize = cartas.size();
        StringBuilder cartasStr = new StringBuilder();
        int i = 1;
        for (String carta : cartas) {
            cartasStr.append(i).append(" - ").append(carta).append("<br>");
            i++;
        }
        JLabel labelCartas = new JLabel("<html>Mano:<br>" + cartasStr + "</html>");
        panelMano.removeAll();
        panelMano.add(labelCartas);
        panelMano.repaint();
        panelMano.revalidate();
    }

    @Override
    public void actualizarPozo(String cartaATirar) {
        JLabel labelPozo;
        if (cartaATirar.isEmpty()) {
            labelPozo = new JLabel("Pozo vacío");
        } else {
            labelPozo = new JLabel("<html>Pozo:<br>"+cartaATirar+"</html>");
        }
        panelPozo.removeAll();
        panelPozo.add(labelPozo);
        panelPozo.repaint();
        panelPozo.revalidate();
    }

    public void comienzoRonda(int ronda) {
        JLabel label = new JLabel(ifVista.mostrarCombinacionRequerida(ronda));
        panelInfoRonda.removeAll();
        panelInfoRonda.add(label);
        panelInfoRonda.revalidate();
        panelInfoRonda.repaint();
    }

    public void actualizarRestricciones(boolean restriccion) {
        panelRestricciones.removeAll();
        if (restriccion) {
            Label label = new Label("Ya no puede robar con castigo y no puede volver a bajar en esta mano");
            panelRestricciones.add(label);
        }
            panelRestricciones.revalidate();
            panelRestricciones.repaint();
    }

    @Override
    public void setNumeroJugadorTitulo() {

    }

    public void mostrarInfo(String s) {
        JOptionPane.showMessageDialog(null, s,
                "Jugador: " + nombreVista, JOptionPane.INFORMATION_MESSAGE);
    }

    public String preguntarInput(String s) {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, s,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!validarEntrada(resp));
        return resp;
    }

    public boolean validarEntrada(String resp) {
        boolean valida = true;
        // si el usuario cerró el diálogo o presionó "Cancelar"
        if (resp == null) {
            JOptionPane.showMessageDialog(null, "No se puede cancelar esta entrada. Por favor, ingresa un valor.", "Error", JOptionPane.ERROR_MESSAGE);
            valida = false;
        }

        // Validar si la entrada está vacía o solo contiene espacios en blanco
        else if (resp.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La entrada no puede estar vacía. Intenta de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            valida = false;
        }
        return valida;
    }

    public void mostrarRanking(Object[] ranking) {
        StringBuilder s = new StringBuilder("Ranking de mejores jugadores: \n");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("\n");
            i++;
        }
        mostrarInfo(s.toString());
    }

    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    public int getNumJugadorAcomodar() {
        return Integer.parseInt(preguntarInput("Ingresa el número de jugador en cuyos" +
                        " juegos bajados quieres acomodar: "));
    }

    public void mostrarCartas(ArrayList<String> cartas) {
        JOptionPane.showMessageDialog(null, getCartasString(cartas),
                "Jugador: " + nombreVista, JOptionPane.INFORMATION_MESSAGE);

    }

    public String preguntarInputMenu(String s) {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, s,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!validarEntrada(resp));
        return resp;
    }

    public String preguntarInputRobar() {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, ifVista.MENU_ROBAR,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!validarEntrada(resp));
        return resp;
    }

    public String preguntarInputRobarCastigo() {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, ifVista.PREGUNTA_ROBAR_CASTIGO,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!validarEntrada(resp));
        return resp;
    }

    public void mostrarComienzaPartida(ArrayList<String> jugadores) {
        StringBuilder s = new StringBuilder("COMIENZA LA PARTIDA\nJugadores:");
        int i = 1;
        for (String nombreJugador : jugadores) {
            s.append(i).append("- ").append(nombreJugador).append("\n");
            i++;
        }
        mostrarInfo(s.toString());
    }


    public String getNombreVista() {
        return nombreVista;
    }

    public String getCartasString(ArrayList<String> cartas) {
        int i = 1;
        StringBuilder s = new StringBuilder();
        for (String carta : cartas) {
            s.append(i).append(" - ").append(carta).append(" || ");
            i++;
        }
        return s.toString();
    }

    public int menuBajar() {
        int eleccion = 0;
        while (eleccion < ELECCION_BAJARSE || eleccion > ELECCION_ACOMODAR_JUEGO_AJENO) {
            eleccion = Integer.parseInt(preguntarInput(MENU_BAJAR));
            if (eleccion == ELECCION_SALIR) break;
        }
        return eleccion;
    }

    public int[] preguntarParaOrdenarCartas() {
        int[] elecciones = new int[2];
        int cartaSeleccion = -1;
        int cantCartas = manoSize;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInputMenu("Elije el número de carta que quieres mover: "))-1;
        }
        elecciones[0] = cartaSeleccion;

        cartaSeleccion = -1;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInputMenu("Elije el número de destino al que quieres" +
                            " mover la carta: "))-1;
        }
        elecciones[1] = cartaSeleccion;
        return elecciones;
    }

    public int preguntarCartaParaAcomodar() {
        return Integer.parseInt(
                preguntarInputMenu("Indica el número de carta que quieres acomodar" +
                        " en un juego"));
    }

    public void mostrarAcomodoCarta(String nombre) {
        mostrarInfo("Se acomodó la carta en el juego.");
    }

    public void mostrarJuegos(String nombreJugador, ArrayList<ArrayList<String>> juegos) {
        StringBuilder mostrar = new StringBuilder("<html>");
        int numJuego = 1;
        mostrar.append("Juegos de ").append(nombreJugador).append(": <br>");
        if (juegos.isEmpty()) {
            mostrar.append("No tiene juegos bajados.");
        }
        else {
            for (ArrayList<String> juego : juegos) {
                mostrar.append("Juego N° ").append(numJuego).append(": <br>").append(getCartasString(juego)).append("<br>");
                numJuego++;
            }
        }
        mostrar.append("<br><br>");
        JLabel labelJuegos = new JLabel(String.valueOf(mostrar));
        panelJuegos.add(labelJuegos);
        panelJuegos.revalidate();
        panelJuegos.repaint();
    }

    public void actualizarJuegos() {
        panelJuegos.removeAll();
        panelJuegos.revalidate();
        panelJuegos.repaint();
    }

    public boolean preguntarSiQuiereSeguirBajandoJuegos() {
        String resp = preguntarInputMenu("Deseas bajar un juego? (Si/No)");
        return ifVista.isRespAfirmativa(resp);
    }

    public int[] preguntarQueBajarParaJuego() {
        int[] cartasABajar = new int[preguntarCantParaBajar()];
        int iCarta;
        for (int i = 0; i < cartasABajar.length; i++) {
            do {
                iCarta = Integer.parseInt(preguntarInputMenu("Carta " + (i + 1) +
                        ":\nIndica el índice de la carta que quieres bajar: "))-1;
            } while (iCarta < 0 || iCarta >= manoSize);
            cartasABajar[i] = iCarta;
        }
        return cartasABajar;
    }

    private int preguntarCantParaBajar() {
        int numCartas = 0;
        while (numCartas > 4 || numCartas < 3) {
            numCartas = Integer.parseInt(
                    preguntarInputMenu("Cuantas cartas quieres bajar para el juego? (3 o 4)"));
        }
        return numCartas;
    }

    public int preguntarQueBajarParaPozo() {
        int eleccion = Integer.parseInt(
                preguntarInputMenu("Indica el índice de carta para tirar al pozo: "))-1;
        while (eleccion < 0 || eleccion >= manoSize) {
            eleccion = Integer.parseInt(preguntarInputMenu("Ese índice es inválido." +
                    " Vuelve a ingresar un índice de carta"))-1;
        }
        return eleccion;
    }

    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {
        StringBuilder s = new StringBuilder("<html>Puntuación<br>");
        for (int i = 0; i < puntos.length; i++) {
            s.append(ctrl.getJugadorPartida(i).getNombre())
                            .append(": ").append(puntos[i]).append("<br>");
        }
        JLabel labelPuntos = new JLabel(String.valueOf(s));
        panelPuntuacion.removeAll();
        panelPuntuacion.add(labelPuntos);
        panelPuntuacion.revalidate();
        panelPuntuacion.repaint();
    }

 }
