package vista;

import controlador.Controlador;
import modelo.Eventos;
import modelo.ifCarta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VentanaConsola extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;
    private final JFrame frame = new JFrame("Mano");
    private JPanel panelMano;
    private JPanel panelPozo;
    private JPanel panelInfoRonda;
    private int manoSize;


    public void iniciar() throws RemoteException {
        nombreVista = preguntarInput("Indica tu nombre:");
        setFrame();
        opcionesIniciales();
    }

    private void setFrame() {
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(400,800);
        panelPozo = new JPanel();
        panelMano = new JPanel();
        panelInfoRonda = new JPanel();
        frame.add(panelInfoRonda);
        frame.add(panelPozo);
        frame.add(panelMano);
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
                        Eventos inicioPartida = ctrl.jugarPartidaRecienIniciada();
                        if (inicioPartida == Eventos.PARTIDA_AUN_NO_CREADA) {
                            mostrarInfo("La partida aun no ha sido creada." +
                                    " Seleccione la opción 'Crear partida' ");
                        } else if (inicioPartida == Eventos.FALTAN_JUGADORES) {
                            mostrarInfo("Esperando que ingresen más jugadores...");
                        } else if (inicioPartida == Eventos.INICIAR_PARTIDA) {
                            ctrl.notificarComienzoPartida();
                            partidaIniciada = true;
                            ctrl.partida();
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
                //                case 5: {
                //                    Object[] partidas = srl.readObjects();
                //                    for (Object o : partidas) {
                //                        System.out.println(o.toString());
                //                    }
                //                }
            }
        } while (eleccion != -1 && !partidaIniciada);
    }

    @Override
    public void cambioTurno() {

    }

    @Override
    public void finTurno() {

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

    private String mostrarInputDialog(String mostrar, String titulo, int ancho, int largo) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(mostrar);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField campoTexto = new JTextField();
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 16));
//        campoTexto.setMaximumSize(new Dimension(200,30));
        panel.setPreferredSize(new Dimension(ancho, largo));
        panel.add(label, BorderLayout.CENTER);
        panel.add(campoTexto, BorderLayout.SOUTH);
        int resultado = JOptionPane.showConfirmDialog(
                null,
                panel,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

//        panel.addFocusListener(a);
//                (new WindowAdapter() {
//            public void windowGainedFocus(WindowEvent e) {
//                campoTexto.requestFocusInWindow();
//            }
//        });

        if (resultado==JOptionPane.OK_OPTION)
            return campoTexto.getText();
        return null;
    }

    public String preguntarInputMenu(String s) {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, s,nombreVista,JOptionPane.QUESTION_MESSAGE);
            //resp = mostrarInputDialog(mostrar, nombreVista, 600, 600);
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
        int i = 0;
        StringBuilder s = new StringBuilder();
        for (String carta : cartas) {
            s.append(i).append(" - ").append(carta).append("\n");
            i++;
        }
        return s.toString();
    }

    public int menuBajar(String combo) {
        int eleccion = 0;
        String s = combo + "\n";
        while (eleccion < 1 || eleccion > 8) {
            s += """
                Elije una opción:
                1 - Bajar uno o más juegos
                2 - Tirar al pozo
                3 - Ordenar cartas
                4 - Acomodar en un juego bajado propio
                5 - Ver juegos bajados propios
                6 - Acomodar en un juego bajado ajeno
                7 - Ver juegos bajados de todos los jugadores
                8 - Ver pozo""";
            eleccion = Integer.parseInt(preguntarInput(s));
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
        mostrarInfo("Juegos de " + nombre + ": ");
    }

    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {
        int numJuego = 1;
        if (juegos.isEmpty()) {
            mostrarInfo("No tiene juegos bajados.");
        } else {
            for (ArrayList<String> juego : juegos) {
                mostrarInfo("Juego N° " + numJuego + ":\n" + getCartasString(juego));
                numJuego++;
            }
        }
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
        StringBuilder s = new StringBuilder("Puntuación: \n");
        for (int i = 0; i < puntos.length; i++) {
            s.append(ctrl.getJugadorPartida(i).getNombre())
                            .append(": ").append(puntos[i]).append("\n");
        }
        mostrarInfo(s.toString());
    }

 }
