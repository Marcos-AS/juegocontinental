package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class cartasGUI extends JFrame{

    private JLabel cartaPozo;
    private JLabel cartaMazo;
    private JPanel panelMano;

    public cartasGUI() {
        setTitle("Mesa");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);
    }

    public void addCartasToPanel(String pozo, ArrayList<String> cartas) {
        // Panel principal para el mazo y el pozo
        JPanel panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        panelCartas.setBackground(new Color(34, 139, 34)); // Fondo verde estilo mesa

        // Carta del Pozo (última carta visible)
        cartaPozo = new JLabel(new ImageIcon(ifVista.asociarRuta(pozo))); // Cambia por la ruta correcta
        cartaPozo.setToolTipText("Robar carta del pozo");
        panelCartas.add(cartaPozo);

        // Carta del Mazo (dada vuelta)
        cartaMazo = new JLabel(new ImageIcon("vista/cartas/carta-dada-vuelta.png"));
        cartaMazo.setToolTipText("Robar carta del mazo");
        panelCartas.add(cartaMazo);

        // Agregar listeners a las cartas
        cartaPozo.addMouseListener(new CartaListener("pozo"));
        cartaMazo.addMouseListener(new CartaListener("mazo"));

        // Panel para la mano del jugador
        panelMano = new JPanel(new FlowLayout());
        panelMano.setBorder(BorderFactory.createTitledBorder("Tu mano"));
        panelMano.setBackground(Color.LIGHT_GRAY);
        for (String carta : cartas) {
            panelMano.add(getImageButton(carta));
        }

        // Añadir todo a la ventana principal
        add(panelCartas, BorderLayout.CENTER);
        add(panelMano, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    public JButton getImageButton(String carta) {
        ImageIcon imagen = new ImageIcon(ifVista.asociarRuta(carta));
        return new JButton(imagen);
    }

//    public void mostrarCartas(ArrayList<String> cartas) {
//        JPanel panel = new JPanel();
//        panel.setLayout(new FlowLayout());
//
//        for (String carta : cartas) {
//            JLabel label = crearLabelConImagen(carta);
//            panel.add(label);
//        }
//
//        JOptionPane.showMessageDialog(this, panel, "Tus cartas", JOptionPane.PLAIN_MESSAGE);
//    }

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
        // Simula agregar una carta a la mano
        JLabel nuevaCarta = new JLabel(new ImageIcon("carta-nueva.png")); // Ruta de la nueva carta
        panelMano.add(nuevaCarta);
        panelMano.revalidate();
        panelMano.repaint();

        if (origen.equals("pozo")) {
            mostrarInfo("Has robado una carta del pozo.");
        } else {
            mostrarInfo("Has robado una carta del mazo.");
        }

        // Actualiza la carta visible del pozo o mazo si es necesario
        // Esto dependerá de tu lógica de juego
    }

    // Muestra un mensaje en un diálogo
    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(cartasGUI::new);
    }
}
