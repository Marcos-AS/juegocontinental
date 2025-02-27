package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import java.util.UUID; //prueba
public class GUI extends ifVista {
    private ArrayList<String> mano;

    @Override
    public void iniciar() {
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(asociarRuta("cartas_inicio")).getImage());

        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);
        cardPanel = new JPanel(cardLayout);
        panelMap = new HashMap<>();
        buttonMap = new HashMap<>();

        JPanel panelMenu = new JPanel();
        JPanel panelMesa = new JPanel();
        JPanel panelJuegos = new JPanel();
        JPanel panelMano = new JPanel(new FlowLayout());
        JPanel panelInfoRonda = new JPanel();
        JPanel panelRestricciones = new JPanel();
        JPanel panelPuntos = new JPanel();
        JPanel panelTurno = new JPanel();

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
        panelMesa.add(panelTurno, BorderLayout.PAGE_END);
        panelMesa.setBackground(new Color(81, 206, 81));

        cardPanel.add(panelMenu, "Menu");
        cardPanel.add(panelMesa, "Mesa");

        panelMap.put("pozoYMazo", panelPozoYMazo);
        panelMap.put("Menu", panelMenu);
        panelMap.put("Mesa", panelMesa);
        panelMap.put("Juegos", panelJuegos);
        panelMap.put("Mano", panelMano);
        panelMap.put("infoRonda", panelInfoRonda);
        panelMap.put("Restricciones", panelRestricciones);
        panelMap.put("Puntuacion", panelPuntos);
        panelMap.put("Turno", panelTurno);

        inicializarMenu();
        frame.add(cardPanel);
        cardLayout.show(cardPanel, "Menu");
        frame.setVisible(true);
    }

    @Override
    public void inicializarMenu() {
        JPanel panelMenu = panelMap.get("Menu");
        panelMenu.removeAll();
        panelMenu.repaint();
        panelMenu.revalidate();

        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelMenu.setBackground(new Color(85, 158, 196));

        JLabel label = new JLabel("¡Bienvenido al juego!");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonJugar = new JButton("Jugar");
        JButton botonCargar = new JButton("Cargar partida");

        botonJugar.addActionListener(e -> {
            if (nombreVista == null) {
                setNombreVista();
            }
            ctrl.crearPartida();
        });


        botonCargar.addActionListener(e -> {
            if (!ctrl.cargarPartida()) {
                mostrarInfo("No hay una partida para cargar.");
            }
        });

        buttonMap.put("botonJugar", botonJugar);
        buttonMap.put("botonCargar", botonCargar);

        panelMenu.add(label);
        panelMenu.add(Box.createVerticalStrut(30));
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

    public int preguntarCantJugadoresPartida() {
        return Integer.parseInt(preguntarInput("Cuántos jugadores" +
                " deseas para la nueva partida?"));
        //return 2;//prueba
    }

    private void crearBotonesMenuBajar(JPanel panelMesa) {
        JButton bajarJuegoBoton = new JButton("Bajar Juego");
        JButton tirarAlPozoBoton = new JButton("Tirar al pozo");
        JButton acomodarBoton = new JButton("Acomodar carta en un juego");
        JButton ordenarBoton = new JButton("Ordenar mano");
        JButton guardarYSalir = new JButton("Guardar y salir");

        bajarJuegoBoton.setEnabled(false);
        tirarAlPozoBoton.setEnabled(false);
        acomodarBoton.setEnabled(false);
        ordenarBoton.setEnabled(false);

        bajarJuegoBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(BAJARSE);
            if (!ctrl.isTurnoActual()) { //si cortó despues de bajar juegos, entonces el turno se hizo false
                activarBotonesBajar(false);
                ctrl.finTurno();
                ctrl.cambioTurno();
            }
        });

        tirarAlPozoBoton.addActionListener(e -> {
            ctrl.switchMenuBajar(TIRAR);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(100); // Espera un momento para que la GUI se actualice
                    return null;
                }

                @Override
                protected void done() {
                    //Actualiza la GUI después de la espera
                    activarBotonesBajar(false);
                    ctrl.finTurno();
                    ctrl.cambioTurno();
                }
            }.execute();
        });

        acomodarBoton.addActionListener(e -> ctrl.switchMenuBajar(ACOMODAR));
        ordenarBoton.addActionListener(e -> ctrl.switchMenuBajar(ORDENAR));
        guardarYSalir.addActionListener(e -> ctrl.guardarPartida());

        JPanel panelBotones = new JPanel();
        panelBotones.add(bajarJuegoBoton);
        panelBotones.add(tirarAlPozoBoton);
        panelBotones.add(acomodarBoton);
        panelBotones.add(ordenarBoton);
        panelBotones.add(guardarYSalir);

        buttonMap.put("bajarJuego", bajarJuegoBoton);
        buttonMap.put("tirarAlPozo", tirarAlPozoBoton);
        buttonMap.put("acomodar", acomodarBoton);
        buttonMap.put("ordenar", ordenarBoton);
        buttonMap.put("guardar", guardarYSalir);

        panelMap.put("botones", panelBotones);
        panelMesa.add(panelBotones, BorderLayout.SOUTH);
    }

    private String asociarRuta(String carta) {
        return "src/vista/cartas/" + carta + ".png";
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
        private final String origen;

        public CartaListener(String origen) {
            this.origen = origen; // "pozo" o "mazo"
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton boton = (JButton) e.getSource();
            if (!boton.isEnabled()) {
                return;
            }
            boton.setEnabled(false);
            robarCarta(origen);
        }
    }

    private void robarCarta(String origen) {
        ctrl.desarrolloRobo(origen);
        buttonMap.get("guardar").setEnabled(false);
        activarBotonesBajar(true);
    }

    @Override
    public void cambioTurno() {
        if (ctrl.isPartidaEnCurso()) {
            String nombre = ctrl.getTurnoDe();
            buttonMap.get("guardar").setEnabled(true);
            if (nombre.equals(nombreVista)) {
                buttonMap.get("cartaPozo").setEnabled(true);
                buttonMap.get("cartaMazo").setEnabled(true);
                buttonMap.get("ordenar").setEnabled(true);
                JPanel panelTurno = panelMap.get("Turno");
                panelTurno.removeAll();
                JLabel label = new JLabel("Es tu turno");
                label.setFont(new Font("Arial", Font.BOLD, 16));
                panelTurno.add(label);
                panelTurno.revalidate();
                panelTurno.repaint();
            } else {
                buttonMap.get("cartaPozo").setEnabled(false);
                buttonMap.get("cartaMazo").setEnabled(false);
                JPanel panelTurno = panelMap.get("Turno");
                panelTurno.removeAll();
                panelTurno.add(new JLabel("Espera tu turno."));
                panelTurno.revalidate();
                panelTurno.repaint();
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
                JLabel imgCarta = getImage(carta);
                panelMano.add(imgCarta);
                //buttonCarta.setBorder(BorderFactory.createTitledBorder(String.valueOf(i+1)));
            }
            cardLayout.show(cardPanel, "Mesa");
        });
    }

    private JLabel getImage(String carta) {
        ImageIcon imagen = new ImageIcon(asociarRuta(carta));
        return new JLabel(new ImageIcon(imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH)));
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
        ImageIcon imagen = new ImageIcon(asociarRuta(rutaImagen));
        cartaPozo.setIcon(new ImageIcon(imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH)));
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

    private JButton getImageButton(String carta) {
        ImageIcon imagen = new ImageIcon(asociarRuta(carta));

        MediaTracker tracker = new MediaTracker(new JLabel());
        tracker.addImage(imagen.getImage(), 0);

        try {
            tracker.waitForAll(); // Esperar hasta que la imagen se cargue
        } catch (InterruptedException e) {
            System.err.println("Error al cargar la imagen para " + carta);
        }

        // Crear el ImageIcon con la imagen redimensionada
        Image imagenRedimensionada =
                imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
        ImageIcon iconRedimensionado = new ImageIcon(imagenRedimensionada);
        return new JButton(iconRedimensionado);
    }

    private void activarBotonesBajar(boolean activar) {
        buttonMap.get("bajarJuego").setEnabled(activar);
        buttonMap.get("tirarAlPozo").setEnabled(activar);
        buttonMap.get("acomodar").setEnabled(activar);
        buttonMap.get("ordenar").setEnabled(activar);
    }

    private int seleccionarUnaCarta(String tituloDialogo) {
        // Crear un diálogo para la selección de una carta
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(cardPanel),
                tituloDialogo, true);
        dialogo.setLayout(new BorderLayout());
        dialogo.setSize(400, 600);
        dialogo.setLocationRelativeTo(frame);

        // Panel para mostrar las cartas
        JPanel panelCartas = new JPanel(new FlowLayout());
        dialogo.add(panelCartas, BorderLayout.CENTER);


        // Variable para rastrear la carta seleccionada
        final int[] cartaSeleccionada = {-1};

        // Crear botones para cada carta en la mano
        for (int i = 0; i < manoSize; i++) {
            JButton botonCarta = getImageButton(mano.get(i));
            botonCarta.setToolTipText("Carta " + (i + 1));
            int index = i;

            botonCarta.addActionListener(e -> {
                cartaSeleccionada[0] = index;
                dialogo.dispose();
            });

            panelCartas.add(botonCarta);
        }

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
        panelJuegosJugador.setBorder(BorderFactory.createTitledBorder("Juegos de " + nombreJugador + " jugador N° " + (ctrl.getNumJugador(nombreJugador) + 1)));
        // Iterar sobre los juegos y crear subpaneles para cada uno
        for (int i = 0; i < juegos.size(); i++) {
            ArrayList<String> juego = juegos.get(i);
            JPanel panelJuego = new JPanel();
            panelJuego.setLayout(new BoxLayout(panelJuego, BoxLayout.X_AXIS)); // Espaciado entre cartas
            panelJuego.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // Borde para cada juego
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
    public int[] preguntarQueBajarParaJuego() {
        // Crear un diálogo para la selección de cartas
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(cardPanel),
                "Seleccionar cartas", true);
        dialogo.setLayout(new BorderLayout());
        dialogo.setSize(400, 600);
        dialogo.setLocationRelativeTo(frame);

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
                if (!ctrl.agregarNombreElegido(nombre)) {
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
        cardLayout.show(cardPanel, "Menu");
        buttonMap.get("botonJugar").setEnabled(false);
        buttonMap.get("botonIniciar").setEnabled(true);
        buttonMap.get("botonCargar").setEnabled(true);
    }

    @Override
    public void esperarRoboCastigo() {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = panelMap.get("pozoYMazo");
            JLabel label = new JLabel("Espere mientras los demás jugadores pueden robar con castigo");
            panel.add(label);
            panel.revalidate();
            panel.repaint();
            activarBotonesBajar(false);
        });
    }

    @Override
    public void terminaEsperaRoboCastigo() {
        SwingUtilities.invokeLater(() -> {
            activarBotonesBajar(true);
            JPanel panel = panelMap.get("pozoYMazo");
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JLabel) {
                    panel.remove(comp);
                    panel.revalidate();
                    panel.repaint();
                    break;
                }
            }
        });
    }

    private int preguntarCantParaBajar() {
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(cardPanel), "Seleccionar cantidad de cartas", true);
        dialogo.setLayout(new FlowLayout());
        dialogo.setSize(300, 150);
        dialogo.setLocationRelativeTo(frame);

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
        if (manoSize < 4) botonCuatro.setEnabled(false);
        int ronda = ctrl.getNumRonda();
        if (ronda == 3 || ronda == 7) botonTres.setEnabled(false);
        dialogo.add(panelBotones, BorderLayout.CENTER);

        dialogo.setVisible(true);

        return cantidadSeleccionada[0];
    }

//        this.nombreVista = UUID.randomUUID().toString()
//                .replace("-", "").substring(0, 10);//prueba

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
                SwingUtilities.getWindowAncestor(cardPanel),
                "Quieres robar con castigo? (robar del pozo y robar del mazo)",
                "Robo castigo - " + nombreVista,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Sí", "No"}, // Etiquetas de los botones
                "Sí" // Botón predeterminado
        );

        return opcion == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos() {
        int opcion = JOptionPane.showOptionDialog(
                frame, //parent
                "¿Deseas bajar un juego?", // Mensaje
                "Bajar juego", // Título del cuadro
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // Ícono personalizado (null usa el predeterminado)
                new Object[]{"Sí", "No"}, // Etiquetas de los botones
                "Sí" // Botón predeterminado
        );

        return opcion == JOptionPane.YES_OPTION;
    }

    @Override
    public void salirAlMenu() {
        cardLayout.show(cardPanel, "Menu");
        JPanel menu = panelMap.get("Menu");
        buttonMap.get("botonCargar").setEnabled(true);
        JLabel label = new JLabel("Un jugador se desconectó. Se guardó la partida.");
        menu.add(label);
        menu.revalidate();
        menu.repaint();
    }

    public int[] seleccionarJuego(ArrayList<ArrayList<ArrayList<String>>> juegosMesa) {
        // Crear diálogo
        JDialog dialogo = new JDialog((JFrame) null, "Seleccionar un juego", true);
        dialogo.setLayout(new BorderLayout());
        dialogo.setSize(600, 400);
        dialogo.setLocationRelativeTo(null);

        // Panel de jugadores y juegos
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        dialogo.add(scrollPane, BorderLayout.CENTER);

        // Botón para confirmar selección
        JButton botonConfirmar = new JButton("Confirmar selección");
        botonConfirmar.setEnabled(false);
        dialogo.add(botonConfirmar, BorderLayout.SOUTH);

        // Variables de selección
        final int[] jugadorSeleccionado = {-1};
        final int[] juegoSeleccionado = {-1};

        // Recorre los jugadores y sus juegos
        for (int i = 0; i < juegosMesa.size(); i++) {
            ArrayList<ArrayList<String>> juegosJugador = juegosMesa.get(i);

            JPanel panelJugador = new JPanel();
            panelJugador.setLayout(new BoxLayout(panelJugador, BoxLayout.Y_AXIS));
            panelJugador.setBorder(BorderFactory.createTitledBorder("Jugador " + (i + 1)));

            for (int j = 0; j < juegosJugador.size(); j++) {
                JPanel panelJuego = new JPanel();
                panelJuego.setLayout(new BorderLayout());

                // Panel de cartas del juego
                JPanel panelCartas = new JPanel(new FlowLayout());
                for (String carta : juegosJugador.get(j)) {
                    panelCartas.add(getImage(carta));
                }
                panelJuego.add(panelCartas, BorderLayout.CENTER);

                // Botón para seleccionar el juego
                JButton botonJuego = new JButton("Juego " + (j + 1));
                int finalI = i;
                int finalJ = j;

                botonJuego.addActionListener(e -> {
                    jugadorSeleccionado[0] = finalI;
                    juegoSeleccionado[0] = finalJ;

                    // Resaltar el botón seleccionado
                    for (Component comp : panelJugador.getComponents()) {
                        if (comp instanceof JPanel) {
                            for (Component btn : ((JPanel) comp).getComponents()) {
                                if (btn instanceof JButton) {
                                    ((JButton) btn).setBorder(BorderFactory.createEmptyBorder());
                                }
                            }
                        }
                    }
                    botonJuego.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

                    botonConfirmar.setEnabled(true);
                });

                panelJuego.add(botonJuego, BorderLayout.SOUTH);
                panelJugador.add(panelJuego);
            }

            panelPrincipal.add(panelJugador);
        }

        // Acción al confirmar
        botonConfirmar.addActionListener(e -> dialogo.dispose());

        dialogo.setVisible(true);

        // Retorna el número de jugador y el índice del juego seleccionado (-1 si no selecciona)
        return new int[]{juegoSeleccionado[0],jugadorSeleccionado[0]};
    }
}