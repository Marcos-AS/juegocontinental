package vista;

import controlador.Controlador;
import modelo.ifCarta;
import modelo.ifJugador;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ifVista {
    int ELECCION_BAJARSE = 1;
    int ELECCION_TIRAR_AL_POZO = 2;
    int ELECCION_ORDENAR_CARTAS = 3;
    int ELECCION_ACOMODAR_JUEGO_PROPIO = 4;
    int ELECCION_ACOMODAR_JUEGO_AJENO = 6;
    int ELECCION_ROBAR_DEL_MAZO = 1;
    int ELECCION_ROBAR_DEL_POZO = 2;
    int ELECCION_ROBAR_CON_CASTIGO = 2;
    int ELECCION_CREAR_PARTIDA = 1;
    int ELECCION_JUGAR_PARTIDA = 2;
    int ELECCION_RANKING = 3;
    int ELECCION_REGLAS = 4;
    int ELECCION_SALIR = -1;
    int FALTAN_JUGADORES = 18;
    int INICIAR_PARTIDA = 19;
    int PARTIDA_AUN_NO_CREADA = 20;
    String NO_PUEDE_ACOMODAR = "No puede acomodar porque no tienes o no hay juegos bajados o porque la carta que deseas acomodar no hace juego con el juego elegido.";
    String ADVERTENCIA_BAJARSE = "Recuerda que sólo puedes bajar tus juegos dos veces durante la ronda,\n una en cualquier turno y otra si se procede a cortar.";
    String YA_NO_PUEDE_BAJAR = "No puedes volver a bajar juegos en esta ronda (tampoco robar con castigo).";
    String MOSTRAR_JUEGO_INVALIDO = "No puedes bajar porque la combinacion elegida no forma un juego valido para la ronda\n";
    String PREGUNTA_NUMERO_JUEGO = "En qué número de juego quieres acomodar tu carta?";
    String MENU_ROBAR = "Querés robar del mazo o robar del pozo?\n1 - Robar del mazo\n2 - Robar del pozo\nElige una opción: ";
    String PREGUNTA_ROBAR_CASTIGO = "Quieres robar con castigo? (robar del pozo y robar del mazo)\n1 - No\n2 - Si";
    String MENU_INICIAR = """
        Bienvenido al juego Continental
        Elije una opción:
        1 - Crear partida
        2 - Jugar partida recién creada
        3 - Ver ranking mejores jugadores
        4 - Ver reglas de juego
        -1 - Salir del juego
        """;
    String MENU_INICIAR_INICIADA = """
        <html><head><title>El Continental.</title></head>
        <h1>Bienvenido al juego Continental</h1>
        Elije una opción:<br>
        1 - Crear partida<br>
        2 - Jugar partida recién creada<br>
        3 - Ver ranking mejores jugadores<br>
        4 - Ver reglas de juego<br>
        -1 - Salir del juego<br>
        YA HAY UNA PARTIDA INICIADA
        </html>""";

    String REGLAS =
    """
        OBJETIVO
        ------------------------------------------------------------------------------------------------------------------------------------------------------------
        El objetivo del juego es formar las combinaciones requeridas en cada una de las 7 rondas, procurando acumular la menor cantidad posible de puntos.
        Al final de todas las rondas, el jugador con menos puntos es el ganador.
        
        RONDAS
        ------------------------------------------------------------------------------------------------------------------------------------------------------------
        Se juegan 7 rondas, cada una con requisitos específicos:
        - Ronda 1: Dos tríos
        - Ronda 2: Un trío y una escalera
        - Ronda 3: Dos escaleras
        - Ronda 4: Tres tríos
        - Ronda 5: Dos tríos y una escalera
        - Ronda 6: Un trío y dos escaleras
        - Ronda 7: Tres escaleras
        
        REPARTO DE CARTAS:
        En la 1ra ronda se reparten 6 cartas y en cada ronda se reparte una carta más llegando a 13 cartas en la 7ma ronda.
        Cuando se termina de repartir se deja el mazo en el medio y se da vuelta la primera.
        
        DEFINICIONES
        ------------------------------------------------------------------------------------------------------------------------------------------------------------
        Trío: 3 cartas con el mismo número, sin importar el palo.
        Escalera: 4 o más cartas consecutivas del mismo palo. Puede comenzar con cualquier carta, y el as puede ser la carta intermedia entre la K y el 2.
        Comodín: Se puede tener un trío de comodines, pero no se pueden colocar dos comodines JUNTOS en una escalera.
        
        ROBO
        ------------------------------------------------------------------------------------------------------------------------------------------------------------
        En cada turno, el jugador debe robar una carta y descartar otra. Si no roba del pozo, los siguientes jugadores pueden optar por robar del pozo, pero si lo hacen,
         también deben robar otra del mazo (robo con "castigo"). Este proceso sigue en orden hacia la derecha. En caso de que ningún jugador desee robar del pozo,
         cada jugador debe robar una carta en su turno, ya sea del mazo o del pozo.
        
        BAJAR JUEGOS Y CORTAR
        ------------------------------------------------------------------------------------------------------------------------------------------------------------
        Para cortar, el jugador debe tener completa la combinación requerida para la ronda.
        Se puede cortar con la carta que sobra, o elegir no cortar si no hay cartas sobrantes. En estos casos, el jugador gana la ronda.
        Además, el jugador puede bajar sus juegos una vez durante la ronda, con las siguientes restricciones:
        - No puede robar con "castigo".
        - No puede bajar de nuevo.
        - Las cartas sobrantes se pueden colocar en los juegos bajados por otros jugadores.
        
        FIN DE LA RONDA
        ------------------------------------------------------------------------------------------------------------------------------------------------------------ \r
        Al finalizar cada ronda, se suman los puntos de las cartas que los jugadores tienen en la mano. El ganador de la ronda no suma puntos. \r
        Las cartas tienen valores específicos: los números valen su denominación, las figuras valen 10, el as vale 20 y el comodín 50.\r
     
        FIN DEL JUEGO
        ------------------------------------------------------------------------------------------------------------------------------------------------------------ \r
        Después de todas las rondas, el jugador con menos puntos es declarado ganador.""";

    void setControlador(Controlador ctrl);
    static String mostrarCombinacionRequerida(int ronda) {
        String s = "Para esta ronda deben bajarse: ";
        s += switch (ronda) {
            case 1 -> "2 tríos";
            case 2 -> "1 trío y 1 escalera";
            case 3 -> "2 escaleras";
            case 4 -> "3 tríos";
            case 5 -> "2 tríos y 1 escalera";
            case 6 -> "1 tríos y 2 escaleras";
            case 7 -> "3 escaleras";
            default -> "";
        };
        return s;
    }

    static String cartaToString(ifCarta c) {
        String carta;
        if (c.getPalo().toString().equalsIgnoreCase("COMODIN")) {
            carta = "COMODIN";
        } else {
            String numString = transformarNumCarta(c.getNumero());
            carta = numString + " de " + c.getPalo().toString();
        }
        return carta;
    }

    static String transformarNumCarta(int numCarta) {
        String num = ((Integer) numCarta).toString();
        if (numCarta <= 1 || numCarta >= 11) {
            num = switch (num) {
                case "1" -> "A";
                case "11" -> "J";
                case "12" -> "Q";
                case "13" -> "K";
                case "-1" -> "COMODIN";
                default -> num;
            };
        }
        return num;
    }

    static ArrayList<String> cartasToStringArray(ArrayList<ifCarta> mano) {
        ArrayList<String> manoString = new ArrayList<>();
        for (ifCarta c : mano)
            manoString.add(cartaToString(c));
        return manoString;
    }

    static int menuInicial(boolean enCurso, String nombreVista) throws RemoteException {
        int eleccion = 0;
        do {
            try {
                eleccion = Integer.parseInt(preguntarInputInicial(enCurso, nombreVista));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } while (eleccion < 1 || eleccion > 4);
        return eleccion;
    }

    static String preguntarInputInicial(boolean enCurso, String nombreVista) throws RemoteException {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 18));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 16));
        String mostrar;
        if (enCurso) {
            mostrar = MENU_INICIAR + "\nYA HAY UNA PARTIDA INICIADA";
        } else {
            mostrar = MENU_INICIAR;
        }
        return JOptionPane.showInputDialog(null, mostrar,"Menú inicial - " + nombreVista,JOptionPane.QUESTION_MESSAGE);
    }

    static String asociarRuta(String carta) {
        return "src/vista/cartas/" + carta + ".png";
    }

    static String getPozoString(ifCarta c) {
        String s;
        if (c == null) {
            s = "Pozo vacío";
        } else {
            s = ifVista.cartaToString(c);
        }
        return s;
    }

    static boolean isRespAfirmativa(String eleccion) {
        String e = eleccion.toLowerCase();
        return e.equals("si") || eleccion.equals("s");
    }

    void mostrarAcomodoCarta(String nombre);
    void comienzoRonda(int ronda) throws RemoteException;
    void mostrarInfo(String s);
    void mostrarCartas(ArrayList<String> cartas);
    void mostrarComienzaPartida(ArrayList<String> jugadores);
    int getNumJugadorAcomodar();
    String getNombreVista();
    String getCartasString(ArrayList<String> cartas);
    int menuBajar(String combo);
    int[] preguntarParaOrdenarCartas();
    int preguntarCartaParaAcomodar();
    void mostrarJuegos(String nombreJugador, ArrayList<ArrayList<String>> juegos);
    String preguntarInput(String s);
    String preguntarInputMenu(String s);
    boolean preguntarSiQuiereSeguirBajandoJuegos();
    int[] preguntarQueBajarParaJuego();
    int preguntarQueBajarParaPozo();
    void mostrarPuntosRonda(int[] puntos) throws RemoteException;
    void iniciar() throws RemoteException;
    String preguntarInputRobarCastigo() throws RemoteException;
    String preguntarInputRobar();
    void opcionesIniciales() throws RemoteException;
    void cambioTurno();
    void actualizarManoJugador(ArrayList<String> cartas);
    void actualizarPozo(String cartaATirar);
    void actualizarJuegos();
    void actualizarRestricciones(boolean restriccion);
}
