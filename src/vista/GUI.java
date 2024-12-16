package vista;

import controlador.Controlador;
import modelo.Eventos;
import modelo.ifCarta;

import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GUI implements ifVista{
    private Controlador ctrl;
    private String nombreVista;
    private PartidaFrame pFrame;
    private VentanaConsola consola = new VentanaConsola();
    private JTextField tFieldNombreJugador;
    private JPanel panelPrincipal;
    private JLabel titulo;
    private JButton iniciarPartidaButton;
    private JButton reglasButton;
    private JButton rankingButton;

    public GUI() {
        JFrame frame = new JFrame("El Continental");
        frame.setSize(1000,700);
        frame.setContentPane(this.panelPrincipal); // Usa el nombre de tu panel principal
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Ajusta el tamaño al contenido
        //agregarMenuBarra();
        frame.setVisible(true); // Muestra la ventana
        iniciarPartidaButton.addActionListener(e -> {
            try {
                iniciar();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        tFieldNombreJugador.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                nombreVista = tFieldNombreJugador.getText();
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {

            }
        });
    }

    @Override
    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void mostrarAcomodoCarta(String nombre) {

    }

    @Override
    public void comienzoTurno(String nomJ, int numJ) throws RemoteException {

    }

    @Override
    public void mostrarInfo(String s) {

    }

    @Override
    public void mostrarCartas(ArrayList<String> cartas) {

    }

    @Override
    public void mostrarComienzaPartida(ArrayList<String> jugadores) {

    }

    @Override
    public int getNumJugadorAcomodar() {
        return 0;
    }

    @Override
    public String getNombreVista() {
        return "";
    }

    @Override
    public String getCartasString(ArrayList<String> cartas) {
        return "";
    }

    @Override
    public int menuBajar(ArrayList<String> cartasStr) {
        return 0;
    }

    @Override
    public int[] preguntarParaOrdenarCartas(ArrayList<String> cartas) {
        return new int[0];
    }

    @Override
    public int preguntarCartaParaAcomodar(ArrayList<String> cartas) {
        return 0;
    }

    @Override
    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {

    }

    @Override
    public String preguntarInput(String s) {
        return "";
    }

    @Override
    public String preguntarInputMenu(String s, String cartas) {
        return "";
    }

    @Override
    public boolean preguntarSiQuiereSeguirBajandoJuegos(ArrayList<String> cartas) {
        return false;
    }

    @Override
    public int[] preguntarQueBajarParaJuego(ArrayList<String> cartas) {
        return new int[0];
    }

    @Override
    public int preguntarQueBajarParaPozo(ArrayList<String> cartas) {
        return 0;
    }

    @Override
    public String getPozoString(ifCarta c) {
        return "";
    }

    @Override
    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {

    }

    public void iniciar() throws RemoteException {
        if (!ctrl.isPartidaEnCurso()) {
            int cantJugadores = Integer.parseInt(consola.preguntarInput("Cuántos jugadores" +
                    " deseas para la nueva partida?"));
            ctrl.crearPartida(cantJugadores);
            //partidaCreada = true;
        }
        Eventos inicioPartida = ctrl.jugarPartidaRecienIniciada();
        if (inicioPartida == Eventos.FALTAN_JUGADORES) {
            mostrarInfo("Esperando que ingresen más jugadores...");
        } else if (inicioPartida == Eventos.INICIAR_PARTIDA) {
            pFrame = new PartidaFrame();
            ctrl.notificarComienzoPartida();
            ctrl.partida();
        }
    }

    @Override
    public String preguntarInputRobar(ArrayList<String> cartas) throws RemoteException {
        pFrame.mostrarCartas(cartas);
        return "";
    }

    @Override
    public String preguntarInputRobarCastigo(ArrayList<String> cartas) throws RemoteException {
        return "";
    }

    @Override
    public void opcionesIniciales() throws RemoteException {

    }

    @Override
    public boolean isRespAfirmativa(String eleccion) {
        return false;
    }
}
