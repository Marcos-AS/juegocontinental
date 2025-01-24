package vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID; //prueba
import java.util.List;

public class GUI implements ifVista {
    private Controlador ctrl;
    private String nombreVista;
    private final JFrame frame = new JFrame("El Continental");
    private static final Color fondoPanelMesa = new Color(81, 206, 81);
    private int manoSize;
    private ArrayList<String> mano;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Map<String, JPanel> panelMap;
    private Map<String, JButton> buttonMap;
    private boolean guardarYSalir = false;
    private static final int ANCHO_CARTA = 80;
    private static final int ALTO_CARTA = 120;
    private static final int ANCHO_FRAME = 900;
    private static final int ALTO_FRAME = 800;

    @Override
    public void iniciar() {
        frame.setSize(900,800);
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
        JPanel panelPuntos = new JPanel();

        panelMano.setBorder(BorderFactory.createTitledBorder("Tu mano"));
        panelMano.setBackground(Color.LIGHT_GRAY);

        //CREA PANEL MESA
        panelMesa.removeAll();
        panelMesa.revalidate();
        panelMesa.repaint();

        crearBotonesMenuBajar(panelMesa);
        JPanel panelPozoYMazo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelPozoYMazo.add(addPozo());
        panelPozoYMazo.add(addMazo());
        panelMesa.add(panelPozoYMazo, BorderLayout.CENTER);
        panelMesa.add(panelMano, BorderLayout.NORTH);
        panelMesa.add(panelJuegos, BorderLayout.SOUTH);
        panelMesa.add(panelInfoRonda, BorderLayout.PAGE_START);
        panelMesa.add(panelPuntos, BorderLayout.EAST);
        panelMesa.add(panelRestricciones, BorderLayout.PAGE_END);
        panelMesa.setBackground(fondoPanelMesa);

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
        panelMap.put("Puntos", panelPuntos);

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

        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        panelMenu.setBackground(new Color(85, 158, 196));

        JLabel label = new JLabel("¡Bienvenido al juego!");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonIniciar = new JButton("Iniciar Partida");
        JButton botonJugar = new JButton("Jugar");
        JButton botonCargar = new JButton("Cargar partida");

        botonJugar.setEnabled(false);

        botonIniciar.addActionListener(e -> {
            botonIniciar.setEnabled(false);
            int cantJugadores = 0;
            if (!ctrl.isPartidaEnCurso()) {
//                    while (cantJugadores < 2) {
//                        cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
//                                " deseas para la nueva partida?"));
//                    }
//                    ctrl.crearPartida(cantJugadores);
                if (nombreVista == null) {
                    setNombreVista();
                }
                ctrl.crearPartida(2); //prueba
            } else {
                mostrarInfo("Ya hay una partida en curso");
            }
            botonJugar.setEnabled(true);
        });

        botonJugar.addActionListener(e -> {
            try {
                botonCargar.setEnabled(false);
                if (nombreVista == null)
                    setNombreVista();
                int inicioPartida = ctrl.jugarPartidaRecienIniciada().ordinal();
                if (inicioPartida == FALTAN_JUGADORES) {
                    mostrarInfo("Esperando que ingresen más jugadores...");
                } else if (inicioPartida == INICIAR_PARTIDA) { //1 solo cli ejecuta esto
                    ctrl.empezarRonda();
                    ctrl.cambioTurno();
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        botonCargar.addActionListener(e -> {
            if (!ctrl.cargarPartida()) {
                mostrarInfo("No hay una partida para cargar.");
            }
        });

        panelMenu.add(label);
        panelMenu.add(Box.createVerticalStrut(30));
        panelMenu.add(botonIniciar);
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(botonJugar);
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(botonCargar);
        panelMenu.add(Box.createVerticalStrut(30));
        frame.setJMenuBar(new BarraMenu().agregarMenuBarra(ctrl.getRanking()));

        for (Component comp : panelMenu.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }
    }

    private void crearBotonesMenuBajar(JPanel panelMesa) {
        JButton bajarJuegoBoton = new JButton("Bajar Juego");
        JButton tirarAlPozoBoton = new JButton("Tirar al pozo");
        JButton acomodarPropioBoton = new JButton("Acomodar en un juego propio");
        JButton acomodarAjenoBoton = new JButton("Acomodar en un juego ajeno");
        JButton ordenarBoton = new JButton("Ordenar mano");
        JButton guardarYSalir = new JButton("Guardar y salir");

        bajarJuegoBoton.setEnabled(false);
        tirarAlPozoBoton.setEnabled(false);
        acomodarPropioBoton.setEnabled(false);
        acomodarAjenoBoton.setEnabled(false);
        ordenarBoton.setEnabled(false);
        guardarYSalir.setEnabled(false);

        bajarJuegoBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(ifVista.ELECCION_BAJARSE);
            if (!ctrl.isTurnoActual()) {
                activarBotonesBajar(false);
                ctrl.finTurno();
                ctrl.cambioTurno();
            }
        });

        tirarAlPozoBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(ifVista.ELECCION_TIRAR_AL_POZO);
            activarBotonesBajar(false);
            ctrl.finTurno();
            ctrl.cambioTurno();
        });

        acomodarPropioBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(ifVista.ELECCION_ACOMODAR_JUEGO_PROPIO);
        });

        acomodarAjenoBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(ifVista.ELECCION_ACOMODAR_JUEGO_AJENO);
        });

        ordenarBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(ifVista.ELECCION_ORDENAR_CARTAS);
        });

        guardarYSalir.addActionListener(e -> {
            ctrl.guardarPartida();
            setGuardarYSalir(true);
            ctrl.salirAlMenu();
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(bajarJuegoBoton);
        panelBotones.add(tirarAlPozoBoton);
        panelBotones.add(acomodarPropioBoton);
        panelBotones.add(acomodarAjenoBoton);
        panelBotones.add(ordenarBoton);
        panelBotones.add(guardarYSalir);

        buttonMap.put("bajarJuego", bajarJuegoBoton);
        buttonMap.put("tirarAlPozo", tirarAlPozoBoton);
        buttonMap.put("acomodarPropio", acomodarPropioBoton);
        buttonMap.put("acomodarAjeno", acomodarAjenoBoton);
        buttonMap.put("ordenar", ordenarBoton);
        buttonMap.put("guardar", guardarYSalir);

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
        buttonMap.put("cartaMazo", cartaMazo);
        cartaMazo.addMouseListener(new CartaListener("mazo"));
        return cartaMazo;
    }

    private class CartaListener extends MouseAdapter {
        private String origen;

        public CartaListener(String origen) {
            this.origen = origen; // "pozo" o "mazo"
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton boton = (JButton) e.getSource();
            buttonMap.get("guardar").setEnabled(false);
            robarCarta(origen, boton);
        }
    }

    private void robarCarta(String origen, JButton botonOrigen) {
        String eleccion = "";
        botonOrigen.setEnabled(false);
        if ("pozo".equals(origen)) {
            eleccion = ELECCION_ROBAR_DEL_POZO;
            buttonMap.get("cartaMazo").setEnabled(false);
        } else if ("mazo".equals(origen)) {
            eleccion = ELECCION_ROBAR_DEL_MAZO;
            buttonMap.get("cartaPozo").setEnabled(false);
        }
        ctrl.desarrolloRobo(eleccion);
        activarBotonesBajar(true);
    }

    //IMPLEMENTACIÓN DE IFVISTA ---------------------------------------------------

    @Override
    public void cambioTurno() {
        if (!guardarYSalir) {
            String nombre = ctrl.getTurnoDe();
            if (nombre.equals(nombreVista)) {
                buttonMap.get("cartaPozo").setEnabled(true);
                buttonMap.get("cartaMazo").setEnabled(true);
                buttonMap.get("ordenar").setEnabled(true);
            } else {
                buttonMap.get("cartaPozo").setEnabled(false);
                buttonMap.get("cartaMazo").setEnabled(false);
            }
        }
    }

    @Override
    public void actualizarManoJugador(ArrayList<String> cartas) {
        SwingUtilities.invokeLater(() -> {
            JPanel panelMano = panelMap.get("Mano");
            manoSize = cartas.size();
            panelMano.removeAll();
            panelMano.revalidate();
            panelMano.repaint();
            mano = new ArrayList<>();
            for (int i = 0; i < manoSize; i++) {
                String carta = cartas.get(i);
                mano.add(carta);
                JButton buttonCarta = getImageButton(carta);
                buttonCarta.setBorder(BorderFactory.createTitledBorder(String.valueOf(i+1)));
                panelMano.add(buttonCarta);
            }
            cardLayout.show(cardPanel, "Mesa");
        });
    }

    @Override
    public void actualizarPozo(String cartaATirar) {
        JButton cartaPozo = buttonMap.get("cartaPozo");
        if (cartaATirar.isEmpty()) {
            setImage(cartaPozo, "pozo-vacio");
            cartaPozo.setToolTipText("El pozo está vacío");
        } else {
            setImage(cartaPozo, cartaATirar);
            cartaPozo.setToolTipText("Robar carta del pozo");
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
        JButton guardarYSalir = buttonMap.get("guardar");
        guardarYSalir.setEnabled(true);
    }

    @Override
    public void actualizarJuegos() {
        JPanel panelJuegos = panelMap.get("Juegos");
        panelJuegos.removeAll();
        panelJuegos.setLayout(new BoxLayout(panelJuegos, BoxLayout.Y_AXIS)); // Layout vertical (una carta debajo de otra)
        panelJuegos.setBackground(Color.LIGHT_GRAY);
        panelJuegos.revalidate();
        panelJuegos.repaint();
    }

    private void setGuardarYSalir(boolean guardarYSalir) {
        this.guardarYSalir = guardarYSalir;
    }

    private JButton getImageButton(String carta) {
        ImageIcon imagen = new ImageIcon(ifVista.asociarRuta(carta));

        MediaTracker tracker = new MediaTracker(new JLabel());
        tracker.addImage(imagen.getImage(), 0);

        try {
            tracker.waitForAll(); // Esperar hasta que la imagen se cargue
        } catch (InterruptedException e) {
            System.err.println("Error al cargar la imagen para " + carta);
        }

        // Crear el ImageIcon con la imagen redimensionada
        Image imagenRedimensionada =
                imagen.getImage().getScaledInstance(ANCHO_CARTA,ALTO_CARTA, Image.SCALE_SMOOTH);
        ImageIcon iconRedimensionado = new ImageIcon(imagenRedimensionada);
        return new JButton(iconRedimensionado);
    }

    @Override
    public String preguntarInputRobar() {
        return "1";
    }

    @Override
    public int menuBajar() {
        activarBotonesBajar(true);
        return 0;
    }

    private void activarBotonesBajar(boolean activar) {
        buttonMap.get("bajarJuego").setEnabled(activar);
        buttonMap.get("tirarAlPozo").setEnabled(activar);
        buttonMap.get("acomodarPropio").setEnabled(activar);
        buttonMap.get("acomodarAjeno").setEnabled(activar);
        buttonMap.get("ordenar").setEnabled(activar);
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

    private int seleccionarUnaCarta(String tituloDialogo) {
        // Crear un diálogo para la selección de una carta
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(cardPanel), tituloDialogo, true);
        dialogo.setLayout(new BorderLayout());
        dialogo.setSize(400, 600);
        dialogo.setLocationRelativeTo(null);

        // Panel para mostrar las cartas
        JPanel panelCartas = new JPanel(new FlowLayout());
        dialogo.add(panelCartas, BorderLayout.CENTER);

        // Botón para confirmar selección
        JButton botonConfirmar = new JButton("Confirmar selección");
        botonConfirmar.setEnabled(false);
        dialogo.add(botonConfirmar, BorderLayout.SOUTH);

        // Variable para rastrear la carta seleccionada
        final int[] cartaSeleccionada = { -1 };

        // Crear botones para cada carta en la mano
        for (int i = 0; i < manoSize; i++) {
            JButton botonCarta = getImageButton(mano.get(i));
            botonCarta.setToolTipText("Carta " + (i + 1));
            int index = i;

            botonCarta.addActionListener(e -> {
                // Desmarcar la selección previa si existe
                if (cartaSeleccionada[0] != -1) {
                    Component prevButton = panelCartas.getComponent(cartaSeleccionada[0]);
                    if (prevButton instanceof JButton) {
                        ((JButton) prevButton).setBorder(BorderFactory.createEmptyBorder());
                    }
                }

                // Seleccionar la nueva carta
                cartaSeleccionada[0] = index;
                botonCarta.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                botonConfirmar.setEnabled(true);
            });

            panelCartas.add(botonCarta);
        }

        // Acción al confirmar
        botonConfirmar.addActionListener(e -> dialogo.dispose());

        dialogo.setVisible(true);

        // Retorna la carta seleccionada (si no selecciona nada, retornará -1)
        return cartaSeleccionada[0];
    }

    @Override
    public int preguntarCartaParaAcomodar() {
        return seleccionarUnaCarta("Seleccionar carta para acomodar");
    }

    @Override
    public int preguntarQueBajarParaPozo() {
        return seleccionarUnaCarta("Seleccionar carta para el pozo");
    }

    @Override
    public void mostrarJuegos(String nombreJugador, ArrayList<ArrayList<String>> juegos) {
        JPanel panelJuegos = panelMap.get("Juegos");
        JPanel panelJuegosJugador = new JPanel();
        panelJuegosJugador.setBorder(BorderFactory.createTitledBorder("Juegos de " + nombreJugador + " jugador N° " + ctrl.getNumJugador(nombreJugador)));
        // Iterar sobre los juegos y crear subpaneles para cada uno
        for (int i = 0; i < juegos.size(); i++) {
            ArrayList<String> juego = juegos.get(i);
            JPanel panelJuego = new JPanel();
            panelJuego.setLayout(new BoxLayout(panelJuego, BoxLayout.X_AXIS)); // Espaciado entre cartas
            panelJuego.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // Borde para cada juego
            panelJuego.setBorder(BorderFactory.createTitledBorder("Juego N° " + (i + 1)));
            panelJuego.setBackground(new Color(200, 200, 255)); // Fondo azul claro para diferenciar

            for (String carta : juego) {
                panelJuego.add(getImageButton(carta));
            }

            panelJuegosJugador.add(panelJuego);
        }
        panelJuegos.add(panelJuegosJugador);
        panelJuegos.revalidate();
        panelJuegos.repaint();
    }

    @Override
    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    @Override
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

    @Override
    public int[] preguntarQueBajarParaJuego() {
        // Crear un diálogo para la selección de cartas
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(cardPanel), "Seleccionar cartas", true);
        dialogo.setLayout(new BorderLayout());
        dialogo.setSize(400, 600);
        dialogo.setLocationRelativeTo(null);

        // Panel para mostrar las cartas
        JPanel panelCartas = new JPanel(new FlowLayout());
        dialogo.add(panelCartas, BorderLayout.CENTER);

        // Botón para confirmar selección
        JButton botonConfirmar = new JButton("Confirmar selección");
        botonConfirmar.setEnabled(false);
        dialogo.add(botonConfirmar, BorderLayout.SOUTH);

        // Array para rastrear selección de cartas
        boolean[] seleccionadas = new boolean[manoSize];
        List<Integer> seleccionIndices = new ArrayList<>();
        int cantABajar = preguntarCantParaBajar();
        // Crear botones para cada carta en la mano
        for (int i = 0; i < manoSize; i++) {
            JButton botonCarta = getImageButton(mano.get(i));
            botonCarta.setToolTipText("Carta " + (i + 1));
            int index = i;
            botonCarta.addActionListener(e -> {
                if (seleccionadas[index]) {
                    seleccionadas[index] = false;
                    seleccionIndices.remove((Integer) index);
                    botonCarta.setBorder(BorderFactory.createEmptyBorder());
                } else {
                    seleccionadas[index] = true;
                    seleccionIndices.add(index);
                    botonCarta.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                }
                botonConfirmar.setEnabled(seleccionIndices.size() == cantABajar);
            });
            panelCartas.add(botonCarta);
        }

        // Acción al confirmar
        botonConfirmar.addActionListener(e -> dialogo.dispose());

        dialogo.setVisible(true);

        // Convertir la lista seleccionada a un array
        return seleccionIndices.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public void elegirJugador(ArrayList<String> nombreJugadores) {
        JPanel panelMenu = panelMap.get("Menu");
        panelMenu.removeAll();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));

        JLabel labelTitulo = new JLabel("Selecciona el jugador que eras en la partida anterior:");
        labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMenu.add(labelTitulo);
        panelMenu.add(Box.createVerticalStrut(10));

        for (String nombre : nombreJugadores) {
            JButton botonJugador = new JButton(nombre);
            botonJugador.addActionListener(e -> {
                nombreVista = nombre;
                if (ctrl.agregarNombreElegido(nombre)) {
                    inicializarMenu();
                }
                else {
                    mostrarInfo("Jugador ya elegido.");
                }
            });
            panelMenu.add(botonJugador);
            panelMenu.add(Box.createVerticalStrut(10));
        }

        panelMenu.revalidate();
        panelMenu.repaint();
    }

    @Override
    public void nuevaPartida() {
        mostrarInfo("Se ha creado una partida.");
    }

    @Override
    public void finPartida() {

    }

    private int preguntarCantParaBajar() {
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(cardPanel), "Seleccionar cantidad de cartas", true);
        dialogo.setLayout(new FlowLayout());
        dialogo.setSize(300, 150);
        dialogo.setLocationRelativeTo(null);

        JPanel panelBotones = new JPanel();
        dialogo.add(new JLabel("¿Cuántas cartas quieres bajar para el juego?"), BorderLayout.NORTH);

        final int[] cantidadSeleccionada = {0};

        JButton botonTres = new JButton("3 cartas");
        JButton botonCuatro = new JButton("4 cartas");

        botonTres.addActionListener(e -> {
            cantidadSeleccionada[0] = 3;
            dialogo.dispose(); // Cerrar el cuadro de diálogo
        });

        botonCuatro.addActionListener(e -> {
            cantidadSeleccionada[0] = 4;
            dialogo.dispose(); // Cerrar el cuadro de diálogo
        });

        panelBotones.add(botonTres);
        panelBotones.add(botonCuatro);
        if (manoSize<4) botonCuatro.setEnabled(false);
        dialogo.add(panelBotones, BorderLayout.CENTER);

        dialogo.setVisible(true);

        return cantidadSeleccionada[0];
    }

    @Override
    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {
        JPanel panelPuntuacion = panelMap.get("Puntos");
        StringBuilder puntuacion = new StringBuilder("<html>Puntuación<br>");
        for (int i = 0; i < puntos.length; i++) {
            puntuacion.append(ctrl.getJugadorPartida(i).getNombre()).append(": ").append(puntos[i]).append("<br>");
        }

        JLabel labelPuntos = new JLabel(String.valueOf(puntuacion));
        panelPuntuacion.removeAll();
        panelPuntuacion.add(labelPuntos);
        panelPuntuacion.revalidate();
        panelPuntuacion.repaint();
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
    public int getNumJugadorAcomodar() {
        return Integer.parseInt(preguntarInput("Ingresa el número de jugador en cuyos" +
                " juegos bajados quieres acomodar: "))-1;
    }

    @Override
    public String getNombreVista() {
        return nombreVista;
    }

    private void setNombreVista() {
//        this.nombreVista = UUID.randomUUID().toString()
//                .replace("-", "").substring(0, 10);//prueba
        nombreVista = preguntarInput("Indique su nombre: ");
    }

    @Override
    public String getCartasString(ArrayList<String> cartas) {
        return String.join(", ", cartas);
    }

    @Override
    public int[] preguntarParaOrdenarCartas() {
        int[] elecciones = new int[2];
        elecciones[0] = seleccionarUnaCarta("Elije la carta que quieres mover");
        elecciones[1] = seleccionarUnaCarta("Elije la carta al lado de la cual quieres mover esa carta");
        return elecciones;
    }

    @Override
    public boolean preguntarInputRobarCastigo() {
        int opcion = JOptionPane.showOptionDialog(
                null,
                "Quieres robar con castigo? (robar del pozo y robar del mazo)", // Mensaje
                "Robo castigo - " + nombreVista, // Título del cuadro
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // Ícono personalizado (null usa el predeterminado)
                new Object[] { "Sí", "No" }, // Etiquetas de los botones
                "Sí" // Botón predeterminado
        );

        return opcion == JOptionPane.YES_OPTION;
    }

    @Override
    public void mostrarAcomodoCarta(String nombre) {
        mostrarInfo("Se acomodó la carta en el juego.");
    }

    @Override
    public void mostrarInfo(String s) {
        String titulo;
        if (nombreVista!=null) {
            titulo = "Jugador: " + nombreVista;
        } else {
            titulo = "";
        }
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, s, titulo, JOptionPane.INFORMATION_MESSAGE));
    }


    @Override
    public String preguntarInputMenu(String s) {
        return preguntarInput(s);
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos() {
        int opcion = JOptionPane.showOptionDialog(
                null,
                "¿Deseas bajar un juego?", // Mensaje
                "Bajar juego", // Título del cuadro
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // Ícono personalizado (null usa el predeterminado)
                new Object[] { "Sí", "No" }, // Etiquetas de los botones
                "Sí" // Botón predeterminado
        );

        return opcion == JOptionPane.YES_OPTION;
    }

    @Override
    public void setNumeroJugadorTitulo() {
        frame.setTitle("El Continental - Jugador N°" + ctrl.getNumJugador(nombreVista) + ": " + nombreVista);
    }

    @Override
    public void salirAlMenu() {
        cardLayout.show(cardPanel, "Menu");
    }
}