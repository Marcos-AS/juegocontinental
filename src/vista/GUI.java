package vista;

import controlador.Controlador;
import modelo.Eventos;
import modelo.ifCarta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GUI extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;

    public void iniciar() throws RemoteException {
        nombreVista = preguntarInput("Indica tu nombre:");
        opcionesIniciales();
    }

    @Override
    public void opcionesIniciales() throws RemoteException {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("¡Bienvenido al juego!");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);

        JButton botonIniciar = new JButton("Iniciar Partida");
        panel.add(botonIniciar, FlowLayout.LEFT);

        JButton botonJugar = new JButton("Jugar");
        botonJugar.setEnabled(false);
        panel.add(botonJugar, FlowLayout.RIGHT);

        botonIniciar.addActionListener(e -> {
            try {
                botonIniciar.setEnabled(false);
                if (!ctrl.isPartidaEnCurso()) {
                    int cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
                            " deseas para la nueva partida?"));
                    ctrl.crearPartida(cantJugadores);
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
                if (ctrl.isPartidaEnCurso()) {
                    if (nombreVista == null) nombreVista = preguntarInput("Indica tu nombre: ");
                    Eventos inicioPartida = ctrl.jugarPartidaRecienIniciada();
                    if (inicioPartida == Eventos.PARTIDA_AUN_NO_CREADA) {
                        mostrarInfo("La partida aun no ha sido creada." +
                                " Seleccione la opción 'Crear partida' ");
                    } else if (inicioPartida == Eventos.FALTAN_JUGADORES) {
                        mostrarInfo("Esperando que ingresen más jugadores...");
                    } else if (inicioPartida == Eventos.INICIAR_PARTIDA) {
                        ctrl.notificarComienzoPartida();
                        ctrl.partida();
                    }
                } else {
                    mostrarInfo("Primero tienes que crear una partida");
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(new BarraMenu().agregarMenuBarra(ctrl.getRanking()),
                FlowLayout.LEADING);
        this.setContentPane(panel);
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void mostrarCartas(ArrayList<String> cartas) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        for (String carta : cartas) {
            JLabel label = crearLabelConImagen(carta);
            panel.add(label);
        }

        JOptionPane.showMessageDialog(this, panel, "Tus cartas", JOptionPane.PLAIN_MESSAGE);
    }

    private JLabel crearLabelConImagen(String nombreCarta) {
        ImageIcon icono = cargarImagenCarta(nombreCarta);
        JLabel label = new JLabel(icono);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private ImageIcon cargarImagenCarta(String nombreCarta) {
        String ruta = "recursos/cartas/" + nombreCarta + ".png"; // Ruta de las imágenes
        return new ImageIcon(ruta);
    }

    public String preguntarInputRobar(ArrayList<String> cartas) throws RemoteException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelCartas(cartas), BorderLayout.CENTER);

        JLabel mensaje = new JLabel("Selecciona una opción para robar:");
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(mensaje, BorderLayout.NORTH);

        String[] opciones = {"Robar del mazo", "Robar del pozo"};
        int seleccion = JOptionPane.showOptionDialog(this, panel, "Robar carta",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

        return seleccion == 0 ? "Mazo" : "Pozo";
    }

    private JPanel crearPanelCartas(ArrayList<String> cartas) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        for (String carta : cartas) {
            JLabel label = crearLabelConImagen(carta);
            panel.add(label);
        }

        return panel;
    }

    public int preguntarCartaParaAcomodar(ArrayList<String> cartas) {
        JPanel panel = crearPanelCartas(cartas);

        JLabel mensaje = new JLabel("Selecciona la carta que quieres acomodar:");
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        int seleccion = JOptionPane.showOptionDialog(this, new Object[]{mensaje, panel}, "Acomodar carta",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, cartas.toArray(), null);

        return seleccion;
    }

    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        int numJuego = 1;
        for (ArrayList<String> juego : juegos) {
            JLabel titulo = new JLabel("Juego " + numJuego + ":");
            titulo.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(titulo);

            JPanel panelCartas = crearPanelCartas(juego);
            panel.add(panelCartas);
            numJuego++;
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Juegos bajados", JOptionPane.PLAIN_MESSAGE);
    }

    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    public String preguntarInput(String mensaje) {
        String resp;
        do {
            resp = JOptionPane.showInputDialog(this, mensaje, "Entrada", JOptionPane.QUESTION_MESSAGE);
        } while (!validarEntrada(resp));
        return resp;
    }

    private boolean validarEntrada(String resp) {
        if (resp == null || resp.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La entrada no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public int[] preguntarQueBajarParaJuego(ArrayList<String> cartas) {
        int numCartas = preguntarCantParaBajar(cartas);
        int[] indicesSeleccionados = new int[numCartas];

        for (int i = 0; i < numCartas; i++) {
            JPanel panel = crearPanelCartas(cartas);
            JLabel mensaje = new JLabel("Selecciona la carta " + (i + 1) + " que quieres bajar:");
            mensaje.setHorizontalAlignment(SwingConstants.CENTER);

            int seleccion = JOptionPane.showOptionDialog(this, new Object[]{mensaje, panel}, "Seleccionar carta",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, cartas.toArray(), null);

            indicesSeleccionados[i] = seleccion;
        }

        return indicesSeleccionados;
    }

    private int preguntarCantParaBajar(ArrayList<String> cartas) {
        String[] opciones = {"3", "4"};
        int seleccion = JOptionPane.showOptionDialog(this, "¿Cuántas cartas quieres bajar para el juego?",
                "Seleccionar cantidad", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        return seleccion + 3; // Devuelve 3 o 4 dependiendo de la selección
    }

    public int preguntarQueBajarParaPozo(ArrayList<String> cartas) {
        JPanel panel = crearPanelCartas(cartas);

        JLabel mensaje = new JLabel("Selecciona la carta que quieres tirar al pozo:");
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        int seleccion = JOptionPane.showOptionDialog(this, new Object[]{mensaje, panel}, "Tirar al pozo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, cartas.toArray(), null);

        return seleccion;
    }

    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {
        StringBuilder puntuacion = new StringBuilder("Puntuación de la ronda:\n");
        for (int i = 0; i < puntos.length; i++) {
            puntuacion.append(ctrl.getJugadorPartida(i).getNombre()).append(": ").append(puntos[i]).append("\n");
        }

        JOptionPane.showMessageDialog(this, puntuacion.toString(), "Puntuación", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarRanking(Object[] ranking) {
        StringBuilder s = new StringBuilder("Ranking de mejores jugadores:\n");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("\n");
            i++;
        }
        JOptionPane.showMessageDialog(this, s.toString(), "Ranking", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarComienzaPartida(ArrayList<String> jugadores) {
        StringBuilder mensaje = new StringBuilder("Comienza la partida con los siguientes jugadores:\n");
        for (String jugador : jugadores) {
            mensaje.append("- ").append(jugador).append("\n");
        }
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, mensaje.toString(),
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
    public int menuBajar(ArrayList<String> cartasStr) {
        String[] opciones = {"Bajar al juego", "Descartar"};
        int seleccion = JOptionPane.showOptionDialog(this, "Selecciona una opción:", "Menú Bajar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        return seleccion;
    }

    @Override
    public int[] preguntarParaOrdenarCartas(ArrayList<String> cartas) {
        int[] indices = new int[cartas.size()];
        for (int i = 0; i < cartas.size(); i++) {
            indices[i] = i;
        }
        return indices;
    }

    @Override
    public String preguntarInputRobarCastigo(ArrayList<String> cartas) throws RemoteException {
        return preguntarInput("Selecciona una carta para robar como castigo:");
    }

    @Override
    public boolean isRespAfirmativa(String eleccion) {
        return eleccion.equalsIgnoreCase("sí") || eleccion.equalsIgnoreCase("yes");
    }

    @Override
    public void mostrarAcomodoCarta(String nombre) {
        JOptionPane.showMessageDialog(this, nombre + " está acomodando una carta.", "Acomodar Carta", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void comienzoTurno(String nomJ, int numJ) throws RemoteException {
        JOptionPane.showMessageDialog(this, "Es el turno de " + nomJ + " (Jugador " + numJ + ")", "Comienzo de Turno", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarInfo(String s) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, s,
                "Jugador: " + nombreVista, JOptionPane.INFORMATION_MESSAGE));
    }


    @Override
    public String preguntarInputMenu(String s, String cartas) {
        return preguntarInput(s + "\nCartas disponibles: " + cartas);
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos(ArrayList<String> cartas) {
        String respuesta = preguntarInput("¿Quieres seguir bajando juegos? (sí/no)");
        return isRespAfirmativa(respuesta);
    }

    @Override
    public String getPozoString(ifCarta c) {
        return c.toString();
    }
}
