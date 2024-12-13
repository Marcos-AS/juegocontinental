package vista;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PartidaFrame extends JFrame {
    private JPanel panelPrincipal;
    private JPanel panelCartas;

    public PartidaFrame() {
        // Configurar la ventana
        setTitle("Partida en Curso");
        setContentPane(this.panelPrincipal);
        setSize(800, 600); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Permitir cerrar solo esta ventana
        setLayout(new BorderLayout());

        // Ejemplo: Agregar un panel para mostrar las cartas
        JPanel panelCartas = new JPanel();
        panelCartas.add(new JLabel("Aquí se mostrarán las cartas"));
        add(panelCartas, BorderLayout.CENTER);

        // Haz la ventana visible
        setVisible(true);
    }

    public void mostrarCartas(ArrayList<String> cartas) {
        for (int i = 0; i < cartas.size(); i++) { // Suponiendo 5 cartas
            JLabel carta = new JLabel(new ImageIcon("../cartas/" + cartas.get(i) + ".png"));
            carta.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Borde para destacar selección
//            carta.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    System.out.println("Carta seleccionada: " + i);
//                    // Agregar lógica para manejar la selección
//                }
//            });
//            panelCartas.add(carta);
        }

        add(panelCartas, BorderLayout.CENTER);
    }
}
