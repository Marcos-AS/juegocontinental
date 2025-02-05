package vista;

import javax.swing.*;

public class BarraMenu extends JFrame{

    protected JMenuBar agregarMenuBarra(Object[] ranking) {
        JMenuBar menuBarra = new JMenuBar();

        // ITEM "MENU" -------------------------------------------------------------------------------
        JMenu menuPrincipal = new JMenu("Menu");

        // agregar los subitems al item "menu"
        menuPrincipal.add(crearItemReglas());
        menuPrincipal.addSeparator();       // separador entre items
        menuPrincipal.add(crearItemSalir());

        // ITEM "RANKING" ----------------------------------------------------------------------------
        JMenu menuRanking = new JMenu("Ranking");

        // SUBITEM "MOSTRAR RANKING"
        menuRanking.add(crearItemRanking(ranking));

        // agregar los menus a la barra de menu
        menuBarra.add(menuPrincipal);
        menuBarra.add(menuRanking);

        return menuBarra;
    }

    private JMenuItem crearItemReglas() {
        JMenuItem itemReglas = new JMenuItem("Reglas");
        itemReglas.addActionListener(e -> {
            VentanaReglas ventanaReglas = new VentanaReglas();  // abrir una nueva ventana con las reglas
            ventanaReglas.setVisible(true);
        });
        return itemReglas;
    }

    private JMenuItem crearItemSalir() {
        JMenuItem itemSalir = new JMenuItem("Salir del juego");
        itemSalir.addActionListener (e -> System.exit(0));
        return itemSalir;
    }

    private JMenuItem crearItemRanking(Object[] ranking) {
        JMenuItem itemVerRanking = new JMenuItem("Mostrar el ranking");

        itemVerRanking.addActionListener(e -> {
            JFrame rankingFrame = new JFrame("Ranking puntos partidas");
            rankingFrame.setSize(600,600);
            JLabel label = new JLabel(mostrarRanking(ranking));
            JScrollPane scrollPane = new JScrollPane(label);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            rankingFrame.add(scrollPane);
            rankingFrame.setVisible(true);
        });
        return itemVerRanking;
    }

    private String mostrarRanking(Object[] ranking) {
        StringBuilder s = new StringBuilder("<html>Ranking de mejores jugadores: <br>");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("<br>");
            i++;
        }
        s.append("</html>");
        return s.toString();
    }
}