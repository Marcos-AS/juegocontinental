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
import java.util.Arrays;

public class GUI extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;
    private JLabel cartaPozo;
    private JLabel cartaMazo;
    private cartasGUI panelMesa;

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
                        panelMesa = new cartasGUI();
                    } else if (inicioPartida == Eventos.INICIAR_PARTIDA) {
                        ctrl.notificarComienzoPartida();
                        panelMesa = new cartasGUI();
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

    private ImageIcon cargarImagenCarta(String nombreCarta) {
        String ruta = "recursos/cartas/" + nombreCarta + ".png"; // Ruta de las imágenes
        return new ImageIcon(ruta);
    }



    public String preguntarInputRobar(ArrayList<String> cartas) throws RemoteException {
        return panelMesa.addCartasToPanel(ifVista.getPozoString(ctrl.getPozo()), cartas);
    }


    public int preguntarCartaParaAcomodar(ArrayList<String> cartas) {
        //JPanel panel = crearPanelCartas(cartas);

        JLabel mensaje = new JLabel("Selecciona la carta que quieres acomodar:");
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        //int seleccion = JOptionPane.showOptionDialog(this,
//                new Object[]{mensaje, panel}, "Acomodar carta",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, cartas.toArray(), null);

  //      return seleccion;
        return 0;
    }

    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {
        System.out.println("bajado");
        panelMesa.mostrarJuegos(juegos);
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

//    public int[] preguntarQueBajarParaJuego(ArrayList<String> cartas) {
//        panelMesa.limpiarSeleccion();
//        panelMesa.activarBotonFinalizarSeleccion();
//        int[] iCartas = panelMesa.obtenerCartasSeleccionadas();
//        System.out.println(Arrays.toString(iCartas));
//        return iCartas;
//    }

    public int[] preguntarQueBajarParaJuego(ArrayList<String> cartas) {
        int[] cartasABajar = new int[preguntarCantParaBajar(cartas)];
        int iCarta;
        for (int i = 0; i < cartasABajar.length; i++) {
            do {
                iCarta = Integer.parseInt(preguntarInputMenu("Carta " + (i + 1) +
                                ":\nIndica el índice de la carta que quieres bajar: ",
                        getCartasString(cartas)));
            } while (iCarta < 0 || iCarta >= cartas.size());
            cartasABajar[i] = iCarta;
        }
        return cartasABajar;
    }

    private int preguntarCantParaBajar(ArrayList<String> cartas) {
        int numCartas = 0;
        while (numCartas > 4 || numCartas < 3) {
            numCartas = Integer.parseInt(
                    preguntarInputMenu("Cuantas cartas quieres bajar para el juego? (3 o 4)",
                            getCartasString(cartas)));
        }
        return numCartas;
    }

    public int preguntarQueBajarParaPozo(ArrayList<String> cartas) {
        String cartasStr = getCartasString(cartas); //modificar
        int eleccion = Integer.parseInt(
                preguntarInputMenu("Indica el índice de carta para tirar al pozo: ",
                        ""));
        while (eleccion < 0 || eleccion >= cartas.size()) {
            eleccion = Integer.parseInt(preguntarInputMenu("Ese índice es inválido." +
                    " Vuelve a ingresar un índice de carta", cartasStr));
        }
        panelMesa.eliminarCartaDeMano(eleccion, cartas);
        return eleccion;
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
        panelMesa.actualizarManoJugador(cartasStr);
        panelMesa.activarBotonesBajar();

        // Esperar hasta que el botón sea presionado
        return panelMesa.esperarAccion();
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
    public void mostrarAcomodoCarta(String nombre) {
        JOptionPane.showMessageDialog(this, nombre + " está acomodando una carta.", "Acomodar Carta", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void comienzoTurno(String nomJ, int numJ) throws RemoteException {
        JOptionPane.showMessageDialog(this, "Es el turno de " + nomJ + " (Jugador " + numJ + ")", "Comienzo de Turno", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarInfo(String s) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(GUI.this, s,
                        "Jugador: " + nombreVista, JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    @Override
    public void mostrarCartas(ArrayList<String> cartas) {

    }


    @Override
    public String preguntarInputMenu(String s, String cartas) {
        return preguntarInput(s);
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos(ArrayList<String> cartas) {
        String resp = preguntarInputMenu("Deseas bajar un juego? (Si/No)"
                , "");
        return ifVista.isRespAfirmativa(resp);
    }

}
