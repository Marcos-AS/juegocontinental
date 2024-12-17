package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaReglas extends JFrame{
    public String reglas = "OBJETIVO\r\n" + 
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" +
            "El objetivo del juego es formar las combinaciones requeridas en cada una de las 7 rondas, procurando acumular la menor cantidad posible de puntos. \r\n" + 
            "Al final de todas las rondas, el jugador con menos puntos es el ganador.\r\n" + 
            "\r\n" +

            "RONDAS\r\n" + 
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" +
            "Se juegan 7 rondas, cada una con requisitos específicos:\r\n" + 
            "- Ronda 1: Dos tríos\r\n" + 
            "- Ronda 2: Un trío y una escalera\r\n" + 
            "- Ronda 3: Dos escaleras\r\n" + 
            "- Ronda 4: Tres tríos\r\n" + 
            "- Ronda 5: Dos tríos y una escalera\r\n" + 
            "- Ronda 6: Un trío y dos escaleras\r\n" + 
            "- Ronda 7: Tres escaleras\r\n" + "\r\n" +

            "DEFINICIONES\r\n" + 
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" +
            "Trío: 3 cartas con el mismo número, sin importar el palo.\r\n" + 
            "Escalera: 4 o más cartas consecutivas del mismo palo. Puede comenzar con cualquier carta, y el as puede ser la carta intermedia entre la K y el 2. \r\n" + 
            "Comodín: Se puede tener un trío de comodines, pero no se pueden colocar dos comodines JUNTOS en una escalera.\r\n" + "\r\n" +
            
            "ROBO\r\n" + 
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" +
            "En cada turno, el jugador debe robar una carta y descartar otra. Si no roba del pozo, los siguientes jugadores pueden optar por robar del pozo, pero si lo hacen,\r\n " + 
            "también deben robar otra del mazo (robo con \"castigo\"). Este proceso sigue en orden hacia la derecha. En caso de que ningún jugador desee robar del pozo,\r\n" + 
            " cada jugador debe robar una carta en su turno, ya sea del mazo o del pozo.\r\n" + 
            "\r\n" + 

            "BAJAR JUEGOS Y CORTAR\r\n" + 
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" +
            "Para cortar, el jugador debe tener completa la combinación requerida para la ronda.\r\n" + 
            "Se puede cortar con la carta que sobra, o elegir no cortar si no hay cartas sobrantes. En estos casos, el jugador gana la ronda. \r\n" + 
            "Además, el jugador puede bajar sus juegos una vez durante la ronda, con las siguientes restricciones:\r\n" + 
            "- No puede robar con \"castigo\".\r\n" + 
            "- No puede bajar de nuevo.\r\n" + 
            "- Las cartas sobrantes se pueden colocar en los juegos bajados por otros jugadores.\r\n" + 
            "\r\n" + 

            "FIN DE LA RONDA\r\n" + 
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" +
            "Al finalizar cada ronda, se suman los puntos de las cartas que los jugadores tienen en la mano. El ganador de la ronda no suma puntos. \r\n" + 
            "Las cartas tienen valores específicos: los números valen su denominación, las figuras valen 10, el as vale 20 y el comodín 50.\r\n" + 
            "\r\n" + 

            "FIN DEL JUEGO\r\n" +
            "------------------------------------------------------------------------------------------------------------------------------------------------------------ \r\n" + 
            "Después de todas las rondas, el jugador con menos puntos es declarado ganador.";

    public VentanaReglas() {
        setTitle("Reglas");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // crear un JTextArea con el contenido de la variable 'reglas'
        JTextArea areaTexto = new JTextArea(reglas);
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
