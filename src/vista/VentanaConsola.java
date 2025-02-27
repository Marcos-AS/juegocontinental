package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        textChat.setText(puntuacion.toString());
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
        JPanel panelMenu = panelMap.get("Menu");
        JButton botonJugar = new JButton("Jugar otra partida");
        botonJugar.addActionListener(e -> {
            botonJugar.setEnabled(false);
            switchInicial();
        });
        buttonMap.put("botonJugar", botonJugar);
        panelMenu.add(botonJugar);
        panelMenu.revalidate();
        panelMenu.repaint();
        cardLayout.show(cardPanel, "Menu");
    }

    @Override
    public int[] seleccionarJuego(ArrayList<ArrayList<ArrayList<String>>> juegosMesa) {
        int[] resp = new int[2];
        resp[0] = Integer.parseInt(preguntarInput("Ingresa el número de jugador en cuyos" +
                " juegos bajados quieres acomodar: "))-1;
        resp[1] = Integer.parseInt(preguntarInput("En qué número de juego quieres acomodar tu carta?")) - 1;
        return resp;
    }

    private int preguntarInputInicial(String input) {
        // Mostrar el mensaje en el JTextPane
        textChat.setText(textChat.getText()+"\n"+input + "\nIngrese el número de opción que desee: ");
        textChat.setCaretPosition(textChat.getDocument().getLength());

        // Cambiar el foco al JTextField para que el usuario pueda escribir
        textMensaje.requestFocusInWindow();

        // Crear un semáforo para esperar la entrada del usuario
        final int[] respuesta = { -1 }; // Valor por defecto en caso de error
        final Object lock = new Object(); // Objeto para sincronización
        JButton botonEnviar = buttonMap.get("enviar");
        // Agregar un ActionListener al botón de enviar
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Leer el valor ingresado por el usuario
                    String texto = textMensaje.getText().trim();
                    respuesta[0] = Integer.parseInt(texto); // Convertir a entero
                    textMensaje.setText(""); // Limpiar el campo de texto
                    synchronized (lock) {
                        lock.notify(); // Notificar que se ha recibido la entrada
                    }
                } catch (NumberFormatException ex) {
                    // Si el usuario ingresa algo que no es un número, mostrar un mensaje de error
                    textChat.setText(textChat.getText()+"\nError: Debe ingresar un número válido.\n" + input + "\nIngrese el número de opción que desee: ");
                    textMensaje.setText(""); // Limpiar el campo de texto
                }
            }
        });

        // Esperar a que el usuario ingrese un valor
        synchronized (lock) {
            try {
                lock.wait(); // Esperar hasta que se reciba la entrada
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Retornar la respuesta del usuario
        return respuesta[0];
    }

    private void switchInicial() {
        final int ELECCION_JUGAR = 1;
        final int ELECCION_RANKING = 2;
        final int ELECCION_REGLAS = 3;
        final int ELECCION_SALIR = -1;
        int eleccion;
        String input = "";
        int inicioPartida = 0;
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
        if (inicioPartida == 0) {
            mostrarInfo(input);
        } else if (inicioPartida == 1) {
            ctrl.empezarRonda();
            ctrl.cambioTurno();
        }
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
                JPanel panelTurno = panelMap.get("Turno");
                panelTurno.removeAll();
                panelTurno.add(new JLabel("Es tu turno."));
                panelTurno.revalidate();
                panelTurno.repaint();

                CountDownLatch latch = new CountDownLatch(1);
                try {
                    SwingUtilities.invokeLater(() -> {
                        ctrl.desarrolloRobo(preguntarInput("1 - Robar del mazo\n2 - Robar del pozo\nElige una opción: "));
                        latch.countDown();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(() -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (ctrl.isTurnoActual()) {
                        ctrl.switchMenuBajar(menuBajar());
                    }
                    ctrl.finTurno();
                    if (ctrl.isPartidaEnCurso()) {
                        ctrl.cambioTurno();
                    }
                });
                executor.shutdown();
            } else {
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
            StringBuilder cartasStr = new StringBuilder();
            int i = 1;
            for (String carta : cartas) {
                cartasStr.append(i).append(" - ").append(carta).append("<br>");
                i++;
            }
            JLabel labelCartas = new JLabel("<html>Mano:<br>" + cartasStr + "</html>");
            panelMano.add(labelCartas);
            cardLayout.show(cardPanel, "Mesa");
        });
    }

    @Override
    public void actualizarPozo(String cartaATirar) {
        JPanel panelPozo = panelMap.get("Pozo");
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
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, PREGUNTA_ROBAR_CASTIGO,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!entradaValida(resp));
        return isRespAfirmativa(resp);
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
        StringBuilder mostrar = new StringBuilder("<html>");
        int numJuego = 1;
        mostrar.append("Juegos de ").append(nombreJugador).append(": <br>");
        if (juegos.isEmpty()) {
            mostrar.append("No tiene juegos bajados.");
        }
        else {
            for (ArrayList<String> juego : juegos) {
                mostrar.append("Juego N° ").append(numJuego).append(": <br>")
                    .append(getCartasString(juego)).append("<br>");
                numJuego++;
            }
        }
        mostrar.append("<br><br>");
        JLabel labelJuegos = new JLabel(String.valueOf(mostrar));
        JPanel panelJuegos = panelMap.get("Juegos");
        panelJuegos.removeAll();
        panelJuegos.add(labelJuegos);
        panelJuegos.revalidate();
        panelJuegos.repaint();
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

    public void esperaRoboCastigo() {
        JOptionPane.showMessageDialog(frame,
        "Atención: Otros jugadores pueden robar con castigo.",
        "Aviso", JOptionPane.WARNING_MESSAGE);
    }

}