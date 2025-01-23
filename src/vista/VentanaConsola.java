package vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VentanaConsola extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;
    private JFrame frame = new JFrame("El Continental");
    private Map<String, JPanel> panelMap;
    private Map<String, JButton> buttonMap;
    private int manoSize;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private int partidaIniciada = 0;


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

        JPanel panelMesa = new JPanel();
        panelMesa.setLayout(new BoxLayout(panelMesa, BoxLayout.Y_AXIS));
        panelMesa.add(panelPuntuacion);
        panelMesa.add(panelInfoRonda);
        panelMesa.add(panelPozo);
        panelMesa.add(panelMano);
        panelMesa.add(panelJuegos);
        panelMesa.add(panelRestricciones);

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

        if (ctrl.isPartidaEnCurso()) partidaIniciada++;
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
        String mostrar = MENU_INICIAR;
        if (partidaIniciada == 0) {
            mostrar += "Aún no hay una partida creada. Seleccione la opción 'Crear partida'";
        } else if (partidaIniciada == 1) {
            mostrar += "\nYA HAY UNA PARTIDA INICIADA";
        } else if (partidaIniciada > 1) {
            mostrar += "\nYA HAY UNA PARTIDA INICIADA Y NO PUEDE CREARSE UNA NUEVA";
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
        partidaIniciada++;
        opcionesIniciales();
    }

    @Override
    public void finPartida() {
        partidaIniciada = 0;
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
        for (Component comp : cardPanel.getComponents()) {
            System.out.println("Panel encontrado: " + comp.getName());
        }
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
//                            int cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
//                                    " deseas para la nueva partida?"));
//                            ctrl.crearPartida(cantJugadores);
                        ctrl.crearPartida(2); //prueba
                    } else {
                        partidaIniciada++;
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
                    mostrarInfo(ifVista.REGLAS);
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

        private void setNombreVista() {
        nombreVista = preguntarInput("Indica tu nombre: ");
//        nombreVista = UUID.randomUUID().toString()
//                .replace("-", "").substring(0, 10);
        frame.setTitle("Mesa - " + nombreVista);
    }

    @Override
    public void cambioTurno() {
        if (ctrl.isPartidaEnCurso()) {
            String nombre = ctrl.getTurnoDe();
            if (nombre.equals(nombreVista)) {
                ctrl.desarrolloRobo(preguntarInputRobar());
                while (ctrl.isTurnoActual()) {
                    ctrl.switchMenuBajar(menuBajar());
                }
                ctrl.finTurno();
                if (ctrl.isPartidaEnCurso())
                    ctrl.cambioTurno();
            }
        }
    }

    @Override
    public void actualizarManoJugador(ArrayList<String> cartas) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("actualizando mano");
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

    public void comienzoRonda(int ronda) {
        JPanel panelInfoRonda = panelMap.get("infoRonda");
        JLabel label = new JLabel(ifVista.mostrarCombinacionRequerida(ronda));
        panelInfoRonda.removeAll();
        panelInfoRonda.add(label);
        panelInfoRonda.revalidate();
        panelInfoRonda.repaint();
    }

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

    @Override
    public void setNumeroJugadorTitulo() {
        frame.setTitle("Mesa - Jugador N°" + ctrl.getNumJugador(nombreVista) + ": " + nombreVista);
    }

    @Override
    public void salirAlMenu() {

    }

    @Override
    public void partidaCargada() {

    }

    @Override
    public void elegirJugador(ArrayList<String> nombreJugadores) {

    }

    public void mostrarInfo(String s) {
        String titulo = "Aviso";
        if (nombreVista!= null) {
            titulo += " - Jugador: " + nombreVista;
        }
        String finalTitulo = titulo;
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, s,
                finalTitulo, JOptionPane.INFORMATION_MESSAGE)
        );
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

    public String getRankingString(Object[] ranking) {
        StringBuilder s = new StringBuilder("Ranking de mejores jugadores: \n");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("\n");
            i++;
        }
        return s.toString();
    }

    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    public int getNumJugadorAcomodar() {
        return Integer.parseInt(preguntarInput("Ingresa el número de jugador en cuyos" +
                        " juegos bajados quieres acomodar: "));
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

    public boolean preguntarInputRobarCastigo() {
        String resp;
        do
            resp = JOptionPane.showInputDialog(null, ifVista.PREGUNTA_ROBAR_CASTIGO,nombreVista,JOptionPane.QUESTION_MESSAGE);
        while (!validarEntrada(resp));
        return ifVista.isRespAfirmativa(resp);
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
        JPanel panelJuegos = panelMap.get("Juegos");
        panelJuegos.add(labelJuegos);
        panelJuegos.revalidate();
        panelJuegos.repaint();
    }

    public void actualizarJuegos() {
        JPanel panelJuegos = panelMap.get("Juegos");
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
        JPanel panelPuntuacion = panelMap.get("Puntuacion");
        panelPuntuacion.removeAll();
        panelPuntuacion.add(labelPuntos);
        panelPuntuacion.revalidate();
        panelPuntuacion.repaint();
    }

 }
