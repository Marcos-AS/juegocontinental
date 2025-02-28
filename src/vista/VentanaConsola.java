package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VentanaConsola extends ifVista {
    private JTextPane textChat;
    private JTextField textMensaje;

    @Override
    public void iniciar() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,800);
        panelMap = new HashMap<>();
        buttonMap = new HashMap<>();

        JPanel panelChat = new JPanel(new BorderLayout());
        panelChat.setPreferredSize(new Dimension(600, 150)); // Altura fija para el chat
        panelChat.setBorder(BorderFactory.createTitledBorder("Chat"));

        textChat = new JTextPane();
        textChat.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textChat);

        JPanel panelInput = new JPanel(new BorderLayout());
        textMensaje = new JTextField();

        JButton botonEnviar = new JButton("Enviar");
        buttonMap.put("enviar", botonEnviar);

        textMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    botonEnviar.doClick(); // Simula un clic en el botón "Enviar"
                }
            }
        });


        panelInput.add(textMensaje, BorderLayout.CENTER);
        panelInput.add(botonEnviar, BorderLayout.EAST);

        panelChat.add(scrollPane, BorderLayout.CENTER);
        panelChat.add(panelInput, BorderLayout.SOUTH);

        panelMap.put("Mesa", panelChat);

        opcionesIniciales();
        frame.add(panelChat);
        frame.setVisible(true);

        switchInicial();
    }

    public void mostrarPuntosRonda(Map<String, Integer> puntos) {
        StringBuilder puntuacion = new StringBuilder("Puntuación:\n");
        for (Map.Entry<String, Integer> entry : puntos.entrySet()) {
            puntuacion.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        mostrarMensajeChat(puntuacion.toString());
    }

    private void opcionesIniciales() {
        // Limpiamos el contenido previo del JTextPane
        textChat.setText("");

        // Construimos el mensaje a mostrar
        StringBuilder mostrar = new StringBuilder();
        mostrar.append("Bienvenido al juego El Continental\n\n");
        mostrar.append("Elije una opción:\n");
        mostrar.append("1 - Jugar\n");
        mostrar.append("2 - Ver ranking mejores jugadores\n");
        mostrar.append("3 - Ver reglas de juego\n");
        mostrar.append("-1 - Salir del juego\n\n");

        // Verificamos si hay una partida en curso
        if (!ctrl.isPartidaEnCurso()) {
            mostrar.append("Aún no hay una partida creada. Seleccione la opción 'Crear partida'.\n");
        } else {
            mostrar.append("YA HAY UNA PARTIDA INICIADA\n");
        }

        // Mostramos el mensaje en el JTextPane
        textChat.setText(mostrar.toString());

        // Si hay un botón de jugar, lo agregamos al panel de entrada (opcional)
        JButton botonJugar = buttonMap.get("botonJugar");
        JPanel panelChat = panelMap.get("Mesa");
        if (botonJugar != null) {
            JPanel panelInput = (JPanel) panelChat.getComponent(1); // Asumiendo que panelInput es el segundo componente
            panelInput.add(botonJugar, BorderLayout.WEST); // Lo agregamos al panel de entrada
            panelInput.revalidate();
            panelInput.repaint();
        }
    }

    @Override
    public void nuevaPartida() {
        opcionesIniciales();
    }

    @Override
    public void finPartida() {
        opcionesIniciales(); //cambio a partidaIniciada antes para mostrar de vuelta
        switchInicial();
    }

    @Override
    public int[] seleccionarJuego(ArrayList<ArrayList<ArrayList<String>>> juegosMesa) {
        int[] resp = new int[2];
        resp[0] = Integer.parseInt(preguntarInput("Ingresa el número de jugador en cuyos" +
                " juegos bajados quieres acomodar: "))-1;
        resp[1] = Integer.parseInt(preguntarInput("En qué número de juego quieres acomodar tu carta?")) - 1;
        return resp;
    }

    public String preguntarInput(String mensaje) {
        String resp;
        do {
            mostrarMensajeChat(mensaje);  // Muestra el mensaje en el chat
            resp = obtenerEntradaChat();  // Espera la entrada del usuario
        } while (!entradaValida(resp));
        return resp;
    }

    public void mostrarMensajeChat(String mensaje) {
        textChat.setText(textChat.getText() + "\n\n" + mensaje);
        textChat.setCaretPosition(textChat.getDocument().getLength());
    }

    public String obtenerEntradaChat() {
        final String[] entrada = {null};  // Variable para almacenar la entrada
        final CountDownLatch latch = new CountDownLatch(1);  // Mecanismo para esperar

        JButton botonEnviar = buttonMap.get("enviar");
        botonEnviar.addActionListener(e -> {
            entrada[0] = textMensaje.getText();  // Guardamos lo que escribió el usuario
            textMensaje.setText("");  // Limpiamos el campo de texto
            latch.countDown();  // Liberamos el latch, lo que permite continuar con el flujo
        });

        // Esperamos a que el usuario haga clic en el botón
        try {
            latch.await();  // Este método no bloquea la UI y espera hasta que countDown() sea llamado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return entrada[0];  // Devolvemos lo que escribió el usuario
    }



    private int preguntarInputInicial(String input) {
        textMensaje.requestFocusInWindow();
        return Integer.parseInt(preguntarInput(input + "\nIngrese el número de opción que desee: "));
    }

    private void switchInicial() {
        final int ELECCION_JUGAR = 1;
        final int ELECCION_RANKING = 2;
        final int ELECCION_REGLAS = 3;
        final int ELECCION_SALIR = -1;
        int eleccion;
        String input = "";
        boolean iniciada = false;
        do {
            eleccion = preguntarInputInicial(input);
            input = "";
            switch (eleccion) {
                case ELECCION_JUGAR: {
                    if (nombreVista == null) {
                        setNombreVista();
                    }
                    ctrl.crearPartida();
                    iniciada = true;
                    break;
                }
                case ELECCION_RANKING: {
                    input = getRankingString(ctrl.getRanking());
                    break;
                }
                case ELECCION_REGLAS: {
                    mostrarInfo(REGLAS);
                    break;
                }
                case ELECCION_SALIR: {
                    frame.dispose();
                    System.exit(0);
                    break;
                }
            }
        } while (!iniciada);
    }

    public int preguntarCantJugadoresPartida() {
        int cantJugadores = 0;
        while (cantJugadores < 2) {
            cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
                    " deseas para la nueva partida?"));
        }
        return cantJugadores;
        //return 2;//prueba
    }

    //        nombreVista = UUID.randomUUID().toString()
    //                .replace("-", "").substring(0, 10);

    private boolean isRespAfirmativa(String eleccion) {
        String e = eleccion.toLowerCase();
        return e.equals("si") || eleccion.equals("s");
    }

    @Override
    public void cambioTurno() {
        if (ctrl.isPartidaEnCurso()) {
            String nombre = ctrl.getTurnoDe();
            if (nombre.equals(nombreVista)) {
                mostrarMensajeChat("Es tu turno.");

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        String opcion = preguntarInput("1 - Robar del mazo\n2 - Robar del pozo\nElige una opción: ");

                        // Llamamos a desarrolloRobo con la opción seleccionada
                        ctrl.desarrolloRobo(opcion);

                        // Volvemos a preguntar hasta que termine el turno
                        while (ctrl.isTurnoActual()) {
                            ctrl.switchMenuBajar(menuBajar());
                        }

                        ctrl.finTurno();

                        if (ctrl.isPartidaEnCurso()) {
                            SwingUtilities.invokeLater(ctrl::cambioTurno);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        executor.shutdown();
                    }
                });

            } else {
                mostrarMensajeChat("Espera tu turno.");
            }
        }
    }


    private void activarEntradas(boolean habilitar) {
        // Aquí puedes deshabilitar todos los elementos de entrada (botones, campos de texto, etc.)
        textMensaje.setEditable(habilitar);
        buttonMap.get("enviar").setEnabled(habilitar);
        // Puedes agregar más controles que desees deshabilitar
    }

    @Override
    public void actualizarManoJugador(ArrayList<String> cartas) {
        SwingUtilities.invokeLater(() -> {
            manoSize = cartas.size();
            StringBuilder cartasStr = new StringBuilder("Mano:");
            int i = 1;
            for (String carta : cartas) {
                cartasStr.append(i).append(" - ").append(carta).append("\n");
                i++;
            }

            // Muestra las cartas en el chat
            mostrarMensajeChat(cartasStr.toString());
        });
    }

    @Override
    public void actualizarPozo(String cartaATirar) {
        SwingUtilities.invokeLater(() -> {
            String mensaje = cartaATirar.isEmpty() ? "El pozo está vacío." : "Pozo: " + cartaATirar;

            // Mostrar la información en el chat
            mostrarMensajeChat(mensaje);
        });
    }


    private String getRankingString(Object[] ranking) {
        StringBuilder s = new StringBuilder("Ranking de mejores jugadores: \n");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("\n");
            i++;
        }
        return s.toString();
    }

    @Override
    public boolean preguntarInputRobarCastigo() {
        String PREGUNTA_ROBAR_CASTIGO = "Quieres robar con castigo? (robar del pozo y robar del mazo)\n(Si/No)";
        mostrarMensajeChat(PREGUNTA_ROBAR_CASTIGO);

        // Creamos un CountDownLatch que bloquea hasta que se obtenga la entrada.
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] entrada = new String[1]; // Array para almacenar la entrada del usuario

        // Configuramos un ActionListener temporal para el campo de texto
        // (Se recomienda quitarlo después de obtener la entrada para no acumular listeners)
        ActionListener listener = e -> {
            entrada[0] = textMensaje.getText();
            textMensaje.setText(""); // Limpiar el campo después de leer
            latch.countDown();       // Libera el latch
        };
        textMensaje.addActionListener(listener);

        try {
            // Espera hasta que se haga clic en Enviar o se presione Enter (según esté configurado)
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        // Removemos el listener para evitar duplicados en llamadas futuras
        textMensaje.removeActionListener(listener);

        return isRespAfirmativa(entrada[0]);
    }

    private String getCartasString(ArrayList<String> cartas) {
        int i = 1;
        StringBuilder s = new StringBuilder();
        for (String carta : cartas) {
            s.append(i).append(" - ").append(carta).append(" || ");
            i++;
        }
        return s.toString();
    }

    public String menuBajar() {
        String MENU_BAJAR = """
        Elije una opción:
        1 - Bajar uno o más juegos
        2 - Tirar al pozo
        3 - Ordenar cartas
        4 - Acomodar una carta
        """;
        return preguntarInput(MENU_BAJAR);
    }

    @Override
    public int[] preguntarParaOrdenarCartas() {
        int[] elecciones = new int[2];
        int cartaSeleccion = -1;
        int cantCartas = manoSize;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInput("Elije el número de carta que quieres mover: "))-1;
        }
        elecciones[0] = cartaSeleccion;

        cartaSeleccion = -1;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInput("Elije el número de destino al que quieres" +
                            " mover la carta: "))-1;
        }
        elecciones[1] = cartaSeleccion;
        return elecciones;
    }

    @Override
    public int preguntarCartaParaAcomodar() {
        return Integer.parseInt(
                preguntarInput("Indica el número de carta que quieres acomodar" +
                        " en un juego"));
    }

    @Override
    public void mostrarJuegos(String nombreJugador, ArrayList<ArrayList<String>> juegos) {
        StringBuilder mostrar = new StringBuilder();
        int numJuego = 1;
        mostrar.append("Juegos de ").append(nombreJugador).append(": \n");
        if (juegos.isEmpty()) {
            mostrar.append("No tiene juegos bajados.");
        }
        else {
            for (ArrayList<String> juego : juegos) {
                mostrar.append("Juego N° ").append(numJuego).append(": \n")
                    .append(getCartasString(juego)).append("\n");
                numJuego++;
            }
        }
        mostrar.append("\n");
        mostrarMensajeChat(String.valueOf(mostrar));
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos() {
        String resp = preguntarInput("Deseas bajar un juego? (Si/No)");
        return isRespAfirmativa(resp);
    }

    @Override
    public int[] preguntarQueBajarParaJuego() {
        int[] cartasABajar = new int[preguntarCantParaBajar()];
        int iCarta;
        for (int i = 0; i < cartasABajar.length; i++) {
            do {
                iCarta = Integer.parseInt(preguntarInput("Carta " + (i + 1) +
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
                    preguntarInput("Cuantas cartas quieres bajar para el juego? (3 o 4)"));
        }
        return numCartas;
    }

    @Override
    public int preguntarQueBajarParaPozo() {
        int eleccion = Integer.parseInt(
                preguntarInput("Indica el índice de carta para tirar al pozo: "))-1;
        while (eleccion < 0 || eleccion >= manoSize) {
            eleccion = Integer.parseInt(preguntarInput("Ese índice es inválido." +
                    " Vuelve a ingresar un índice de carta"))-1;
        }
        return eleccion;
    }

    @Override
    public void esperarRoboCastigo() {
        mostrarMensajeChat("Espera mientras los demás jugadores pueden robar con castigo.");
    }

    @Override
    public void comienzoRonda(int ronda) {
        SwingUtilities.invokeLater(() -> {
            String mensaje = "Comienza la ronda " + ronda + "!\n" + mostrarCombinacionRequerida(ronda);
            mostrarMensajeChat(mensaje);
        });
    }

    @Override
    String mostrarCombinacionRequerida(int ronda) {
        String s = "Para esta ronda ";
        s += switch (ronda) {
            case 1 -> "(1/7) deben bajarse 2 tríos";
            case 2 -> "(2/7) deben bajarse 1 trío y 1 escalera";
            case 3 -> "(3/7) deben bajarse 2 escaleras";
            case 4 -> "(4/7) deben bajarse 3 tríos";
            case 5 -> "(5/7) deben bajarse 2 tríos y 1 escalera";
            case 6 -> "(6/7) deben bajarse 1 tríos y 2 escaleras";
            case 7 -> "(7/7) deben bajarse 3 escaleras";
            default -> "";
        };
        s += "\nTrío = 3 cartas (mínimo) con el mismo número\nEscalera = 4 cartas (mínimo) con número consecutivo y mismo palo";
        return s;
    }

    @Override
    void setNombreVista() {
        nombreVista = preguntarInput("Indica tu nombre: ");
    }

    @Override
    public void mostrarInfo(String s) {
        mostrarMensajeChat(s);
    }

    @Override
    public void setNumeroJugadorTitulo() {
        frame.setTitle("Mesa - Jugador N°" + (ctrl.getNumJugador(nombreVista)+1) + ": " + nombreVista);
    }
}