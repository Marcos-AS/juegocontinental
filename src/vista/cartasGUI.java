package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class cartasGUI extends JFrame{

    private JButton cartaPozo;
    private JButton cartaMazo;
    private JPanel panelMano;
    private JPanel panelJuegos;
    private JPanel panelPozoConBorde;
    private CountDownLatch latch;
    private String resultadoRobar;
    private JButton bajarJuegoBoton;
    private JButton tirarAlPozoBoton;
    private JButton acomodarPropioBoton;
    private JButton acomodarAjenoBoton;
    private JButton finalizarSeleccionBoton;
    protected boolean botonBajarPresionado;
    protected boolean botonTirarPresionado;
    protected boolean acomodarPropioPresionado;
    protected boolean acomodarAjenoPresionado;
    protected boolean finalizarSeleccionPresionado;
    private ArrayList<JButton> botonesCartas;  // Lista de botones de cartas
    private ArrayList<Integer> cartasSeleccionadas;

    public cartasGUI() {
        setTitle("Mesa");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);
        botonesCartas = new ArrayList<>();
        cartasSeleccionadas = new ArrayList<>();

        bajarJuegoBoton = new JButton("Bajar Juego");
        bajarJuegoBoton.addActionListener(e -> {
            botonBajarPresionado = true;
            latch.countDown();
        });

        tirarAlPozoBoton = new JButton("Tirar al pozo");
        tirarAlPozoBoton.addActionListener(e -> {
            botonTirarPresionado = true;
            latch.countDown();
        });

        acomodarPropioBoton = new JButton("Acomodar en un juego propio");
        acomodarPropioBoton.addActionListener(e -> {
            acomodarPropioPresionado = true;
            latch.countDown();
        });

        acomodarAjenoBoton = new JButton("Acomodar en un juego ajeno");
        acomodarAjenoBoton.addActionListener(e -> {
            acomodarAjenoPresionado = true;
            latch.countDown();
        });

        finalizarSeleccionBoton = new JButton("Finalizar Selección");
        finalizarSeleccionBoton.addActionListener(e -> finalizarSeleccion());

        JPanel panelBotones = new JPanel();
        bajarJuegoBoton.setEnabled(false);
        tirarAlPozoBoton.setEnabled(false);
        acomodarPropioBoton.setEnabled(false);
        acomodarAjenoBoton.setEnabled(false);
        finalizarSeleccionBoton.setEnabled(false);  // Al principio no está habilitado
        panelBotones.add(bajarJuegoBoton);
        panelBotones.add(tirarAlPozoBoton);
        panelBotones.add(acomodarPropioBoton);
        panelBotones.add(acomodarAjenoBoton);
        panelBotones.add(finalizarSeleccionBoton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void activarBotonesBajar() {
        bajarJuegoBoton.setEnabled(true);
        tirarAlPozoBoton.setEnabled(true);
        acomodarPropioBoton.setEnabled(true);
        acomodarAjenoBoton.setEnabled(true);
    }

    public void activarBotonFinalizarSeleccion() {
        finalizarSeleccionBoton.setEnabled(true);
    }

    private void finalizarSeleccion() {
        latch.countDown();
        finalizarSeleccionPresionado = true;
        finalizarSeleccionBoton.setEnabled(false);  // Deshabilitar después de la selección
    }

    public int esperarAccion() {
        // Crear el latch para esperar un solo evento
        latch = new CountDownLatch(1);  // Solo un clic es necesario para continuar

        try {
            latch.await(); // Bloquea el hilo principal hasta que el usuario haga clic (lo decrementa)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return obtenerValorBoton();
    }

    public int obtenerValorBoton() {
        int resp = 0;
        if (botonBajarPresionado) {
            resp = ifVista.ELECCION_BAJARSE;
            botonBajarPresionado = false;
        } else if (botonTirarPresionado) {
            resp = ifVista.ELECCION_TIRAR_AL_POZO;
            botonTirarPresionado = false;
        } else if (acomodarPropioPresionado) {
            resp = ifVista.ELECCION_ACOMODAR_JUEGO_PROPIO;
            acomodarPropioPresionado = false;
        } else if (acomodarAjenoPresionado) {
            resp = ifVista.ELECCION_ACOMODAR_JUEGO_AJENO;
            acomodarAjenoPresionado = false;
        }
        return resp;
    }

    public JButton getImageButtonCarta(String carta, int indice) {
        ImageIcon imagen = new ImageIcon(ifVista.asociarRuta(carta));
        Image imagenRedimensionada =
                imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);

        // Crear el ImageIcon con la imagen redimensionada
        ImageIcon iconRedimensionado = new ImageIcon(imagenRedimensionada);
        JButton boton = new JButton(iconRedimensionado);
        boton.setToolTipText("Selecciona esta carta");
        boton.addActionListener(e -> {
            System.out.println("Se hizo clic en la carta con índice: " + indice);
            seleccionarCarta(indice);
        }); //evento
        return boton;
    }

    public JButton getImageButton(String carta) {
        ImageIcon imagen = new ImageIcon(ifVista.asociarRuta(carta));
        Image imagenRedimensionada =
                imagen.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);

        // Crear el ImageIcon con la imagen redimensionada
        ImageIcon iconRedimensionado = new ImageIcon(imagenRedimensionada);
        return new JButton(iconRedimensionado);
    }

    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {
        if (panelJuegos != null) {
            remove(panelJuegos);
        }

        panelJuegos = new JPanel();
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

        add(panelJuegos, BorderLayout.EAST); // Agregar el panel a la posición deseada en el layout

        revalidate();
        repaint();
    }

    public void eliminarCartaDeMano(int index, ArrayList<String> cartas) {
        String cartaATirar = cartas.remove(index);

        // Limpiar el panel de la mano
        panelMano.removeAll();

        // Agregar los botones de las cartas restantes
        for (int i = 0; i < cartas.size(); i++) {
            JButton botonCarta = getImageButtonCarta(cartas.get(i), i);
            panelMano.add(botonCarta);
        }
        revalidate();
        repaint();

        actualizarCartaPozo(cartaATirar);
        panelPozoConBorde.revalidate();
        panelPozoConBorde.repaint();
    }

    public void actualizarCartaPozo(String carta) {
        cartaPozo = getImageButton(carta);
        panelPozoConBorde.removeAll();
        panelPozoConBorde.add(cartaPozo);
    }


    public int[] obtenerCartasSeleccionadas() {
        latch = new CountDownLatch(1);
        // Esperar la acción del usuario
        try {
            latch.await(); // Bloquea el hilo principal hasta que el usuario haga clic
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int[] seleccion = new int[cartasSeleccionadas.size()];
        for (int i = 0; i < cartasSeleccionadas.size(); i++) {
            seleccion[i] = cartasSeleccionadas.get(i);
        }
        System.out.println("cartas seleccionadas " + cartasSeleccionadas);
        return seleccion;
    }

    // Limpiar la selección de cartas
    public void limpiarSeleccion() {
        for (int i = 0; i < cartasSeleccionadas.size(); i++) {
            botonesCartas.get(cartasSeleccionadas.get(i)).setBorder(null);  // Quitar el borde
        }
        cartasSeleccionadas.clear();  // Limpiar la lista de seleccionadas
    }

    public String addCartasToPanel(String pozo, ArrayList<String> cartas) {
        latch = new CountDownLatch(1);
        resultadoRobar = "";

        // Panel principal para el mazo y el pozo
        JPanel panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        panelCartas.setBackground(new Color(34, 139, 34)); // Fondo verde estilo mesa

        // Carta del Pozo (última carta visible)
        cartaPozo = getImageButton(pozo);
                new JLabel(new ImageIcon(ifVista.asociarRuta(pozo))); // Cambia por la ruta correcta
        cartaPozo.setToolTipText("Robar carta del pozo");

        panelPozoConBorde = new JPanel();
        panelPozoConBorde.setLayout(new FlowLayout());
        panelPozoConBorde.setBorder(BorderFactory.createLineBorder(Color.RED, 5)); // Borde rojo de 5 píxeles
        panelPozoConBorde.add(cartaPozo);

        panelCartas.add(panelPozoConBorde);

        // Carta del Mazo (dada vuelta)
        cartaMazo = getImageButton("carta-dada-vuelta");
        cartaMazo.setToolTipText("Robar carta del mazo");
        panelCartas.add(cartaMazo);

        // Agregar listeners a las cartas
        cartaPozo.addMouseListener(new CartaListener("pozo"));
        cartaMazo.addMouseListener(new CartaListener("mazo"));

        // Panel para la mano del jugador
        panelMano = new JPanel(new FlowLayout());
        panelMano.setBorder(BorderFactory.createTitledBorder("Tu mano"));
        panelMano.setBackground(Color.LIGHT_GRAY);
        for (int i = 0; i < cartas.size(); i++) {
            JButton botonCarta = getImageButtonCarta(cartas.get(i), i);
            botonesCartas.add(botonCarta);
            panelMano.add(botonCarta);
        }


        // Añadir todo a la ventana principal
        add(panelCartas, BorderLayout.CENTER);
        add(panelMano, BorderLayout.SOUTH);

        revalidate();
        repaint();

        // Esperar la acción del usuario
        try {
            latch.await(); // Bloquea el hilo principal hasta que el usuario haga clic
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultadoRobar;
    }

    // Listener para manejar clics en las cartas
    private class CartaListener extends MouseAdapter {
        private String origen;

        public CartaListener(String origen) {
            this.origen = origen; // "pozo" o "mazo"
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            robarCarta(origen);
        }
    }

    // Método para manejar el robo de cartas
    private void robarCarta(String origen) {
        if ("pozo".equals(origen)) {
            cartaPozo.setVisible(false);
            panelPozoConBorde.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
            resultadoRobar = "2"; // Si robó del pozo
        } else if ("mazo".equals(origen)) {
            resultadoRobar = "1"; // Si robó del mazo
        }
        latch.countDown(); // Libera el bloqueo
    }

    private void seleccionarCarta(int index) {
        System.out.println("Carta seleccionada con índice: " + index);
        System.out.println("cartas seleccionadas " + cartasSeleccionadas);
        cartasSeleccionadas.add(index);  // Agregar la carta seleccionada
        System.out.println("cartas seleccionadas " + cartasSeleccionadas);
        JButton boton = botonesCartas.get(index);
        boton.setBorder(BorderFactory.createLineBorder(Color.RED, 3));  // Resaltar la carta seleccionada
        revalidate();
        repaint();
    }

    public void actualizarManoJugador(ArrayList<String> cartas) {
        panelMano.removeAll();  // Limpiar la mano actual

        // Agregar las nuevas cartas
        for (String carta : cartas) {
            System.out.println("cargando desde " + carta);
            panelMano.add(getImageButton(carta));
        }

        // Revalidate y repaint para actualizar la interfaz
        panelMano.revalidate();
        panelMano.repaint();
    }

    // Muestra un mensaje en un diálogo
    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(cartasGUI::new);
    }
}
