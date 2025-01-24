package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaReglas extends JFrame{

    public VentanaReglas() {
        setTitle("Reglas");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // crear un JTextArea con el contenido de la variable 'reglas'
        JTextArea areaTexto = new JTextArea(ifVista.REGLAS);
        areaTexto.setEditable(false);   // desactivar la edición del texto en el JTextArea

        areaTexto.setMargin(new Insets(20, 20, 20, 20));  

        // configurar fuente 
        Font fuente = new Font("Candara", Font.PLAIN, 22);
        areaTexto.setFont(fuente);
       
        // configurar color de texto usando HSB 
        float[] hsb = Color.RGBtoHSB(242, 232, 207, null);
        Color color = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        areaTexto.setForeground(color);

        areaTexto.setBackground(new Color(56, 102, 65));  // configurar color de fondo

        JScrollPane desplazarPanel = new JScrollPane(areaTexto);    // permite desplazarse por el contenido de "areaTexto"

        getContentPane().add(desplazarPanel, BorderLayout.CENTER);
    }
}
