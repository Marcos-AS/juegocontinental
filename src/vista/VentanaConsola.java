package vista;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VentanaConsola extends ifVista {

    @Override
    public void iniciar() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,800);
        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);
        cardPanel = new JPanel(cardLayout);
        panelMap = new HashMap<>();
        buttonMap = new HashMap<>();

        JPanel panelMenu = new JPanel();
        JPanel panelPozo = new JPanel();
        JPanel panelMano = new JPanel();
        JPanel panelInfoRonda = new JPanel();
        JPanel panelPuntuacion = new JPanel();
        JPanel panelJuegos = new JPanel();
        panelJuegos.setLayout(new BoxLayout(panelJuegos, BoxLayout.Y_AXIS));
        JPanel panelRestricciones = new JPanel();
        JPanel panelTurno = new JPanel();

        JPanel panelMesa = new JPanel();
        panelMesa.setLayout(new BoxLayout(panelMesa, BoxLayout.Y_AXIS));
        panelMesa.add(panelMano);
        panelMesa.add(panelTurno);
        panelMesa.add(panelPuntuacion);
        panelMesa.add(panelInfoRonda);
        panelMesa.add(panelPozo);
        panelMesa.add(panelRestricciones);
        panelMesa.add(panelJuegos);

        cardPanel.add(panelMesa, "Mesa");
        cardPanel.add(panelMenu, "Menu");

        panelMap.put("Menu", panelMenu);
        panelMap.put("Mesa", panelMesa);
        panelMap.put("Pozo", panelPozo);
        panelMap.put("Mano", panelMano);
        panelMap.put("infoRonda", panelInfoRonda);
        panelMap.put("Puntuacion", panelPuntuacion);
        panelMap.put("Juegos", panelJuegos);
        panelMap.put("Restricciones", panelRestricciones);
        panelMap.put("Turno", panelTurno);

        opcionesIniciales();
        frame.add(cardPanel);
        cardLayout.show(cardPanel, "Menu");
        frame.setVisible(true);

        switchInicial();
    }

    private void opcionesIniciales() {
        JPanel panelMenu = panelMap.get("Menu");
        panelMenu.removeAll();
        panelMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        String mostrar = """
        <html><head><title>El Continental.</title></head>
        <h1>Bienvenido al juego El Continental</h1>
        Elije una opción:<br>
        1 - Crear partida<br>
        2 - Jugar partida recién creada<br>
        3 - Ver ranking mejores jugadores<br>
        4 - Ver reglas de juego<br>
        -1 - Salir del juego<br>
        """;
        if (!ctrl.isPartidaEnCurso()) {
            mostrar += "Aún no hay una partida creada. Seleccione la opción 'Crear partida'";
        } else {
            mostrar += "\nYA HAY UNA PARTIDA INICIADA";
        }
        JLabel labelMenu = new JLabel(mostrar);
        panelMenu.add(labelMenu);
        JButton botonJugar = buttonMap.get("botonJugar");
        if (botonJugar!=null) panelMenu.add(botonJugar);
        panelMenu.revalidate();
        panelMenu.repaint();
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

    private int preguntarInputInicial(String input) {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 18));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 16));
        String mostrar = input + "\nIngrese el número de opción que desee: ";
        String titulo = "Menú inicial";
        if (nombreVista != null) {
            titulo += " - " + nombreVista;
        }
        return Integer.parseInt(JOptionPane.showInputDialog(null, mostrar,titulo,JOptionPane.QUESTION_MESSAGE));
    }

    private void switchInicial() {
        final int ELECCION_CREAR_PARTIDA = 1;
        final int ELECCION_JUGAR_PARTIDA = 2;
        final int ELECCION_RANKING = 3;
        final int ELECCION_REGLAS = 4;
        final int ELECCION_SALIR = -1;
        int eleccion;
        String input = "";
        int inicioPartida = 0;
        boolean iniciada = false;
        do {
            eleccion = preguntarInputInicial(input);
            input = "";
            switch (eleccion) {
                case ELECCION_CREAR_PARTIDA: {
                    if (!ctrl.isPartidaEnCurso()) {
                        if (nombreVista == null) {
                            setNombreVista();
                        }
                        ctrl.crearPartida(preguntarCantJugadoresPartida());
                    } else {
                        opcionesIniciales();
                    }
                    break;
                }
                case ELECCION_JUGAR_PARTIDA: {
                    if (ctrl.isPartidaEnCurso()) {
                        if (nombreVista == null) {
                            setNombreVista();//prueba
                        }
                        try {
                            inicioPartida = ctrl.jugarPartidaRecienIniciada().ordinal();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        if (inicioPartida == FALTAN_JUGADORES) {
                            input = "Esperando que ingresen más jugadores...";
                        }
                        iniciada = true;
                    } else {
                        input = "Primero tienes que crear una partida";
                    }
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
        if (inicioPartida == FALTAN_JUGADORES) {
            mostrarInfo(input);
        } else if (inicioPartida == INICIAR_PARTIDA) {
            ctrl.empezarRonda();
            ctrl.cambioTurno();
        }
    }

    private int preguntarCantJugadoresPartida() {
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

    @Override
    public int menuBajar() {
        String MENU_BAJAR = """
        Elije una opción:
        1 - Bajar uno o más juegos
        2 - Tirar al pozo
        3 - Ordenar cartas
        4 - Acomodar en un juego bajado propio
        5 - Acomodar en un juego bajado ajeno""";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int eleccion = 0;
        while (eleccion < ELECCION_BAJARSE || eleccion > ELECCION_ACOMODAR_JUEGO_AJENO) {
            eleccion = Integer.parseInt(preguntarInput(MENU_BAJAR));
        }
        return eleccion;
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
        panelJuegos.add(labelJuegos);
        panelJuegos.revalidate();
        panelJuegos.repaint();
    }

    @Override
    public void actualizarJuegos() {
        JPanel panelJuegos = panelMap.get("Juegos");
        panelJuegos.removeAll();
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