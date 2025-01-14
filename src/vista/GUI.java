package vista;

import controlador.Controlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUI implements ifVista {
    private Controlador ctrl;
    private String nombreVista;
    private final JFrame frame = new JFrame("El Continental");
    private static final Color fondo = new Color(34, 139, 34);
    private int manoSize;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Map<String, JPanel> panelMap;
    private Map<String, JButton> buttonMap;


    public void iniciar() {
        //nombreVista = preguntarInput("Indica tu nombre:");
        nombreVista = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 10); //prueba
        opcionesIniciales();
    }

    @Override
    public void opcionesIniciales() {
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(ifVista.asociarRuta("cartas_inicio")).getImage());

        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);
        cardPanel = new JPanel(cardLayout);
        panelMap = new HashMap<>();
        buttonMap = new HashMap<>();

        JPanel panelMenu = new JPanel();
        JPanel panelEsperar = new JPanel();
        JPanel panelMesa = new JPanel();
        JPanel panelJuegos = new JPanel();
        JPanel panelMano = new JPanel(new FlowLayout());
        JPanel panelInfoRonda = new JPanel();
        JPanel panelRestricciones = new JPanel();

        panelMano.setBorder(BorderFactory.createTitledBorder("Tu mano"));
        panelMano.setBackground(Color.LIGHT_GRAY);

        panelMesa.removeAll();
        panelMesa.revalidate();
        panelMesa.repaint();

        crearBotonesMenuBajar(panelMesa);
        JPanel panelIntermedio = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelIntermedio.add(addPozo());
        panelIntermedio.add(addMazo());
        panelMesa.add(panelIntermedio, BorderLayout.CENTER);
        panelMesa.add(panelMano, BorderLayout.NORTH);
        panelMesa.add(panelRestricciones, BorderLayout.PAGE_END);
        cardLayout.show(cardPanel,"Mesa");

        cardPanel.add(panelMenu, "Menu");
        cardPanel.add(panelEsperar, "Esperar");
        cardPanel.add(panelMesa, "Mesa");

        panelMap.put("Menu", panelMenu);
        panelMap.put("Esperar", panelEsperar);
        panelMap.put("Mesa", panelMesa);
        panelMap.put("Juegos", panelJuegos);
        panelMap.put("Mano", panelMano);
        panelMap.put("infoRonda", panelInfoRonda);
        panelMap.put("Restricciones", panelRestricciones);

        inicializarMenu();
        frame.add(cardPanel);
        cardLayout.show(cardPanel, "Menu");
        frame.setVisible(true);
    }

    private void inicializarMenu() {
        JPanel panelMenu = panelMap.get("Menu");
        panelMenu.removeAll();
        panelMenu.repaint();
        panelMenu.revalidate();

        panelMenu.setLayout(new FlowLayout());

        JLabel label = new JLabel("¡Bienvenido al juego!");
        label.setFont(new Font("Arial", Font.BOLD, 20));

        JButton botonIniciar = new JButton("Iniciar Partida");

        JButton botonJugar = new JButton("Jugar");
        botonJugar.setEnabled(false);

        botonIniciar.addActionListener(e -> {
            try {
                botonIniciar.setEnabled(false);
                int cantJugadores = 0;
                if (!ctrl.isPartidaEnCurso()) {
//                    while (cantJugadores < 2) {
//                        cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
//                                " deseas para la nueva partida?"));
//                    }
//                    ctrl.crearPartida(cantJugadores);
                    ctrl.crearPartida(2); //prueba
                } else {
                    mostrarInfo("Ya hay una partida en curso");
                }
                botonJugar.setEnabled(true);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        botonJugar.addActionListener(e -> {
            try {
                int inicioPartida = ctrl.jugarPartidaRecienIniciada().ordinal();
                if (inicioPartida == FALTAN_JUGADORES) {
                    mostrarInfo("Esperando que ingresen más jugadores...");
                } else if (inicioPartida == INICIAR_PARTIDA) {
                    ctrl.empezarRonda();
                    ctrl.cambioTurno();
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        try {
            panelMenu.add(label);
            panelMenu.add(botonIniciar, FlowLayout.LEFT);
            panelMenu.add(botonJugar, FlowLayout.RIGHT);
            panelMenu.add(new BarraMenu().agregarMenuBarra(ctrl.getRanking()),
                    FlowLayout.LEADING);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //  PARTIDA funciones agregadas--------------------------------------------------------

    public void cambioTurno() {

        String nombre = ctrl.getTurnoDe();
        if (nombre.equals(nombreVista)) {
            jugar();
        }
    }

    private void jugar() {
    }

    private void crearBotonesMenuBajar(JPanel panelMesa) {
        JButton bajarJuegoBoton = new JButton("Bajar Juego");
        JButton tirarAlPozoBoton = new JButton("Tirar al pozo");
        JButton acomodarPropioBoton = new JButton("Acomodar en un juego propio");
        JButton acomodarAjenoBoton = new JButton("Acomodar en un juego ajeno");

        bajarJuegoBoton.setEnabled(false);
        tirarAlPozoBoton.setEnabled(false);
        acomodarPropioBoton.setEnabled(false);
        acomodarAjenoBoton.setEnabled(false);

        bajarJuegoBoton.addActionListener(e -> {
            try {
                ctrl.switchMenuBajar(ifVista.ELECCION_BAJARSE);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        tirarAlPozoBoton.addActionListener(e -> {
            try {
                ctrl.switchMenuBajar(ifVista.ELECCION_TIRAR_AL_POZO);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        acomodarPropioBoton.addActionListener(e -> {
            try {
                ctrl.switchMenuBajar(ifVista.ELECCION_ACOMODAR_JUEGO_PROPIO);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        acomodarAjenoBoton.addActionListener(e -> {
            try {
                ctrl.switchMenuBajar(ifVista.ELECCION_ACOMODAR_JUEGO_AJENO);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(bajarJuegoBoton);
        panelBotones.add(tirarAlPozoBoton);
        panelBotones.add(acomodarPropioBoton);
        panelBotones.add(acomodarAjenoBoton);

        buttonMap.put("bajarJuego", bajarJuegoBoton);
        buttonMap.put("tirarAlPozo", tirarAlPozoBoton);
        buttonMap.put("acomodarPropio", acomodarPropioBoton);
        buttonMap.put("acomodarAjeno", acomodarAjenoBoton);

        panelMesa.add(panelBotones, BorderLayout.SOUTH);
    }

    private JButton addPozo() {
        JButton cartaPozo = getImageButton("pozo-vacio");
        cartaPozo.setToolTipText("Robar carta del pozo");
        buttonMap.put("cartaPozo", cartaPozo);
        cartaPozo.addMouseListener(new CartaListener("pozo"));
        return cartaPozo;
    }

    private JButton addMazo() {
        JButton cartaMazo = getImageButton("carta-dada-vuelta");
        cartaMazo.setToolTipText("Robar carta del mazo");
        cartaMazo.addMouseListener(new CartaListener("mazo"));
        return cartaMazo;
    }

    public void actualizarManoJugador(ArrayList<String> cartas) {
        JPanel panelMano = panelMap.get("Mano");
        manoSize = cartas.size();
        panelMano.removeAll();
        panelMano.revalidate();
        panelMano.repaint();
        for (String carta : cartas) {
            //System.out.println("cargando desde " + carta);
            panelMano.add(getImageButton(carta));
        }
        cardLayout.show(cardPanel, "Mesa");
    }

    public void actualizarPozo(String cartaATirar) {
        JButton cartaPozo = buttonMap.get("cartaPozo");
        if (cartaATirar.isEmpty()) {
            setImage(cartaPozo, "pozo-vacio");
            cartaPozo.setToolTipText("El pozo está vacío");
        } else {
            setImage(cartaPozo, cartaATirar);
        }
        cartaPozo.revalidate();
        cartaPozo.repaint();
    }

    private void setImage(JButton cartaPozo, String rutaImagen) {
        ImageIcon imagen = new ImageIcon(ifVista.asociarRuta(rutaImagen));
        cartaPozo.setIcon(new ImageIcon(imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH)));
    }

    @Override
    public void comienzoRonda(int ronda) {
        JLabel label = new JLabel(ifVista.mostrarCombinacionRequerida(ronda));
        JPanel panelInfoRonda = panelMap.get("infoRonda");
        panelInfoRonda.removeAll();
        panelInfoRonda.add(label);
        panelInfoRonda.revalidate();
        panelInfoRonda.repaint();
    }

    @Override
    public void actualizarJuegos() {

    }

    private class CartaListener extends MouseAdapter {
        private String origen;

        public CartaListener(String origen) {
            this.origen = origen; // "pozo" o "mazo"
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton boton = (JButton) e.getSource();
            robarCarta(origen, boton);
        }
    }

    private void robarCarta(String origen, JButton botonOrigen) {
        String eleccion = "";
        botonOrigen.setEnabled(false);
        if ("pozo".equals(origen)) {
            eleccion = ELECCION_ROBAR_DEL_POZO;
        } else if ("mazo".equals(origen)) {
            eleccion = ELECCION_ROBAR_DEL_MAZO;
        }
        ctrl.desarrolloRobo(eleccion);
        activarBotonesBajar();
    }

    public JButton getImageButton(String carta) {
        ImageIcon imagen = new ImageIcon(ifVista.asociarRuta(carta));
        Image imagenRedimensionada =
                imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);

        // Crear el ImageIcon con la imagen redimensionada
        ImageIcon iconRedimensionado = new ImageIcon(imagenRedimensionada);
        return new JButton(iconRedimensionado);
    }

    private void esperar(String nombre) {

    }

    private void generarTabla(JPanel panel){
        panel.setLayout(new BorderLayout());
        panel.setBackground(fondo);
        String[] nombreColumnas = {"Jugador","Puntos"};
        DefaultTableModel modelo = new DefaultTableModel(nombreColumnas,0);
        JTable tabla = new JTable(modelo);
        int[] puntos = ctrl.getPuntos();
        ArrayList<String> nombres = ctrl.getJugadores();
        for (int i = 0; i < puntos.length; i++){
            Object[] fila = {nombres.get(i),puntos[i]};
            modelo.addRow(fila);
        }
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(fondo);
        scroll.setBorder(null);
        panel.add(scroll,BorderLayout.CENTER);
    }

    //IMPLEMENTACIÓN DE IFVISTA ---------------------------------------------------

    @Override
    public String preguntarInputRobar() {
        return "1";
    }

    @Override
    public int menuBajar() {
        activarBotonesBajar();
        return 0;
    }

    private void activarBotonesBajar() {
        buttonMap.get("bajarJuego").setEnabled(true);
        buttonMap.get("tirarAlPozo").setEnabled(true);
        buttonMap.get("acomodarPropio").setEnabled(true);
        buttonMap.get("acomodarAjeno").setEnabled(true);
    }

    public int preguntarQueBajarParaPozo() {
        int eleccion = Integer.parseInt(
                preguntarInputMenu("Indica el índice de carta para tirar al pozo: "));
        while (eleccion < 0 || eleccion >= manoSize) {
            eleccion = Integer.parseInt(preguntarInputMenu("Ese índice es inválido." +
                    " Vuelve a ingresar un índice de carta"));
        }
        return eleccion;
    }

    @Override
    public void actualizarRestricciones(boolean restriccion) {
        JPanel panelRestricciones = panelMap.get("Restricciones");
        panelRestricciones.removeAll();
        if (restriccion) {
            Label label = new Label("Ya no puede robar con castigo y no puede volver a bajar en esta mano");
            panelRestricciones.add(label);
        }
        panelRestricciones.revalidate();
        panelRestricciones.repaint();
    }

    public int preguntarCartaParaAcomodar() {
        return Integer.parseInt(
                preguntarInputMenu("Indica el número de carta que quieres acomodar" +
                        " en un juego"));
    }

    public void mostrarJuegos(String nombreJugador, ArrayList<ArrayList<String>> juegos) {
        JPanel panelJuegos = panelMap.get("Juegos");
        panelJuegos.removeAll();
        panelJuegos.revalidate();
        panelJuegos.repaint();
        panelJuegos.setLayout(new BoxLayout(panelJuegos, BoxLayout.Y_AXIS)); // Layout vertical (una carta debajo de otra)
        panelJuegos.setBorder(BorderFactory.createTitledBorder("Juegos en la mesa"));
        panelJuegos.setBackground(Color.LIGHT_GRAY);

        // Iterar sobre los juegos y crear subpaneles para cada uno
        for (ArrayList<String> juego : juegos) {
            JPanel panelJuego = new JPanel();
            panelJuego.setLayout(new BoxLayout(panelJuego, BoxLayout.Y_AXIS)); // Espaciado entre cartas
            panelJuego.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // Borde para cada juego
            panelJuego.setBackground(new Color(200, 200, 255)); // Fondo azul claro para diferenciar

            // Crear botones o etiquetas para cada carta en el juego
            for (String carta : juego) {
                System.out.println("cargando imagen desde " + ifVista.asociarRuta(carta));
                JButton cartaLabel = getImageButton(carta); // Aquí defines el tamaño de la carta
                panelJuego.add(cartaLabel);
            }

            // Agregar el subpanel del juego al panel principal de juegos
            panelJuegos.add(panelJuego);
        }

        cardLayout.show(cardPanel, "Juegos");
    }

    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    public String preguntarInput(String mensaje) {
        String resp;
        do {
            resp = JOptionPane.showInputDialog(frame, mensaje, "Entrada", JOptionPane.QUESTION_MESSAGE);
        } while (!validarEntrada(resp));
        return resp;
    }

    private boolean validarEntrada(String resp) {
        if (resp == null || resp.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "La entrada no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public int[] preguntarQueBajarParaJuego() {
        int[] cartasABajar = new int[preguntarCantParaBajar()];
        int iCarta;
        for (int i = 0; i < cartasABajar.length; i++) {
            do {
                iCarta = Integer.parseInt(preguntarInputMenu("Carta " + (i + 1) +
                                ":\nIndica el índice de la carta que quieres bajar: "));
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

    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {
        StringBuilder puntuacion = new StringBuilder("Puntuación de la ronda:\n");
        for (int i = 0; i < puntos.length; i++) {
            puntuacion.append(ctrl.getJugadorPartida(i).getNombre()).append(": ").append(puntos[i]).append("\n");
        }

        JOptionPane.showMessageDialog(frame, puntuacion.toString(), "Puntuación", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarRanking(Object[] ranking) {
        StringBuilder s = new StringBuilder("Ranking de mejores jugadores:\n");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("\n");
            i++;
        }
        JOptionPane.showMessageDialog(frame, s.toString(), "Ranking", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarComienzaPartida(ArrayList<String> jugadores) {
        StringBuilder mensaje = new StringBuilder("Comienza la partida con los siguientes jugadores:\n");
        for (String jugador : jugadores) {
            mensaje.append("- ").append(jugador).append("\n");
        }
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(frame, mensaje.toString(),
                    "Inicio de Partida", JOptionPane.INFORMATION_MESSAGE));
    }

    @Override
    public int getNumJugadorAcomodar() {
        String respuesta = preguntarInput("¿Qué número de jugador eres para acomodar?");
        return Integer.parseInt(respuesta);
    }

    @Override
    public String getNombreVista() {
        return nombreVista;
    }

    @Override
    public String getCartasString(ArrayList<String> cartas) {
        return String.join(", ", cartas);
    }



    @Override
    public int[] preguntarParaOrdenarCartas() {
        int[] elecciones = new int[2];
        int cartaSeleccion = -1;
        int cantCartas = manoSize;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInputMenu("Elije el número de carta que quieres mover: "));
        }
        elecciones[0] = cartaSeleccion;

        cartaSeleccion = -1;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInputMenu("Elije el número de destino al que quieres" +
                            " mover la carta: "));
        }
        elecciones[1] = cartaSeleccion;
        return elecciones;
    }

    @Override
    public String preguntarInputRobarCastigo() {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, ifVista.PREGUNTA_ROBAR_CASTIGO,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!validarEntrada(resp));
        return resp;    }

    @Override
    public void mostrarAcomodoCarta(String nombre) {
        JOptionPane.showMessageDialog(frame, nombre + " está acomodando una carta.", "Acomodar Carta", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarInfo(String s) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, s, "Jugador: " + nombreVista, JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @Override
    public void mostrarCartas(ArrayList<String> cartas) {

    }


    @Override
    public String preguntarInputMenu(String s) {
        return preguntarInput(s);
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos() {
        String resp = preguntarInputMenu("Deseas bajar un juego? (Si/No)");
        return ifVista.isRespAfirmativa(resp);
    }
}
