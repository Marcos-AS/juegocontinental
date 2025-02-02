package vista;

import controlador.Controlador;
import modelo.ifCarta;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ifVista {
    int ELECCION_BAJARSE = 1;
    int ELECCION_TIRAR_AL_POZO = 2;
    int ELECCION_ORDENAR_CARTAS = 3;
    int ELECCION_ACOMODAR_JUEGO_PROPIO = 4;
    int ELECCION_ACOMODAR_JUEGO_AJENO = 5;
    String ELECCION_ROBAR_DEL_MAZO = "1";
    String ELECCION_ROBAR_DEL_POZO = "2";
    int ELECCION_CREAR_PARTIDA = 1;
    int ELECCION_JUGAR_PARTIDA = 2;
    int ELECCION_RANKING = 3;
    int ELECCION_REGLAS = 4;
    int ELECCION_SALIR = -1;
    int FALTAN_JUGADORES = 0;
    int INICIAR_PARTIDA = 1;
    String NO_PUEDE_ACOMODAR = "No puede acomodar porque no tienes o no hay juegos bajados o porque la carta que deseas acomodar no hace juego con el juego elegido.";
    String YA_NO_PUEDE_BAJAR = "No puedes volver a bajar juegos en esta ronda.";
    String MOSTRAR_JUEGO_INVALIDO = "No puedes bajar porque la combinacion elegida no forma un juego valido para la ronda\n";
    String PREGUNTA_NUMERO_JUEGO = "En qué número de juego quieres acomodar tu carta?";
    String PREGUNTA_ROBAR_CASTIGO = "Quieres robar con castigo? (robar del pozo y robar del mazo)\n(Si/No)";
    String MENU_ROBAR = "1 - Robar del mazo\n2 - Robar del pozo\nElige una opción: ";
    String MENU_BAJAR = """
        Elije una opción:
        1 - Bajar uno o más juegos
        2 - Tirar al pozo
        3 - Ordenar cartas
        4 - Acomodar en un juego bajado propio
        5 - Acomodar en un juego bajado ajeno""";
    String MENU_INICIAR = """
        <html><head><title>El Continental.</title></head>
        <h1>Bienvenido al juego El Continental</h1>
        Elije una opción:<br>
        1 - Crear partida<br>
        2 - Jugar partida recién creada<br>
        3 - Ver ranking mejores jugadores<br>
        4 - Ver reglas de juego<br>
        -1 - Salir del juego<br>
        """;

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
        No se puede cortar si al jugador le sobra más de una carta.  Si le sobra más de una carta, debe acomodar en los juegos bajados las que le sobraron y ahí podrá cortar.
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
        String s = "<html>Para esta ronda ";
        s += switch (ronda) {
            case 1 -> "(1/7) deben bajarse 2 tríos";
            case 2 -> "(2/7) deben bajarse 1 trío y 1 escalera";
            case 3 -> "(3/7) deben bajarse 2 escaleras";
            case 4 -> "(4/7) deben bajarse 3 tríos";
            case 5 -> "(5/7) deben bajarse 2 tríos y 1 escalera";
            case 6 -> "(6/7) deben bajarse 1 tríos y 2 escaleras";
            case 7 -> "(7/7) deben bajarse 3 escaleras";
            default -> "";
        };
        s += "<br>Trío = 3 cartas (mínimo) con el mismo número<br>Escalera = 4 cartas (mínimo) con número consecutivo y mismo palo</html>";
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

    static String asociarRuta(String carta) {
        return "src/vista/cartas/" + carta + ".png";
    }

    static boolean isRespAfirmativa(String eleccion) {
        String e = eleccion.toLowerCase();
        return e.equals("si") || eleccion.equals("s");
    }

    void mostrarAcomodoCarta(String nombre);
    void comienzoRonda(int ronda) throws RemoteException;
    void mostrarInfo(String s);
    int getNumJugadorAcomodar();
    String getNombreVista();
    String getCartasString(ArrayList<String> cartas);
    int menuBajar();
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
    boolean preguntarInputRobarCastigo() throws RemoteException;
    String preguntarInputRobar();
    void cambioTurno();
    void actualizarManoJugador(ArrayList<String> cartas);
    void actualizarPozo(String cartaATirar);
    void actualizarJuegos();
    void actualizarRestricciones(boolean restriccion);
    void setNumeroJugadorTitulo();

    void salirAlMenu();

    void elegirJugador(ArrayList<String> nombreJugadores);

    void nuevaPartida();

    void finPartida();
}
