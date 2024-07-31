package vista;

import controlador.Controlador;
import modelo.Eventos;
import modelo.ifCarta;
import modelo.ifJugador;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class VentanaConsola extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;

    public void iniciar() throws RemoteException {
        String nombreJugador = preguntarInput("Indica tu nombre:");
        ctrl.agregarNuevoJugador(nombreJugador); //agrega jugador a juego y setea nombreVista
        mostrarInfo("Jugador agregado.");
        nombreVista = nombreJugador;
        int eleccion;
        int cantJugadores; //minimo
        boolean partidaIniciada = false;
        do {
            eleccion = Integer.parseInt(preguntarInput(ifVista.MENU_INICIAR));
            try {
                switch (eleccion) {
                    case ifVista.ELECCION_CREAR_PARTIDA: {
                        cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores deseas para la nueva partida?"));
                        ctrl.crearPartida(cantJugadores);
                        break;
                    }
                    case ifVista.ELECCION_RANKING: {
                        mostrarRanking(ctrl.getRanking());
                        break;
                    }
                    case ifVista.ELECCION_REGLAS: {
                        mostrarInfo(ifVista.REGLAS);
                        break;
                    }
                    case ifVista.ELECCION_JUGAR_PARTIDA: {
                        Eventos inicioPartida = ctrl.jugarPartidaRecienIniciada();
                        if (inicioPartida == Eventos.PARTIDA_AUN_NO_CREADA) {
                            mostrarInfo("La partida aun no ha sido creada. Seleccione la opción 'Crear partida' ");
                        } else if (inicioPartida == Eventos.FALTAN_JUGADORES) {
                            partidaIniciada = true;
                            mostrarInfo("Esperando que ingresen más jugadores...");
                        } else if (inicioPartida == Eventos.INICIAR_PARTIDA) {
                            partidaIniciada = true; //esto inicia el funcionamiento del juego
                            ctrl.notificarComienzoPartida();
                            ctrl.partida();
                        }
                        break;
                    }
                    //                case 5: {
                    //                    Object[] partidas = srl.readObjects();
                    //                    for (Object o : partidas) {
                    //                        System.out.println(o.toString());
                    //                    }
                    //                }
                }
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número entre 1 y 4");
            }
        } while (eleccion != -1 && !partidaIniciada);
    }

    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    public void comienzoTurno(ifJugador jA) throws RemoteException {
        mostrarInfo(ifVista.mostrarCombinacionRequerida(ctrl.getRonda()));
        mostrarPozo(ctrl.getPozo());
        String nombreJugador = jA.getNombre();
        if (!nombreJugador.equals(nombreVista)) {
            mostrarInfo("--------------------------\nEs el turno del jugador: " + nombreJugador);
        } else {
            mostrarInfo("************\nEs tu turno.");
            ctrl.setTurno(jA.getNumeroJugador(), true);
        }
    }

    public void mostrarInfo(String s) {
        JOptionPane.showMessageDialog(null, s);
    }

    public String preguntarInput(String s) {
        return JOptionPane.showInputDialog(null, s);
    }

    public void mostrarPozo(ifCarta c) {
        String s;
        if (c == null) {
            s = "Pozo vacío";
        } else {
            s = ifVista.cartaToString(c);
        }
        mostrarInfo(s);
    }

    public void mostrarComienzaPartida(String[] jugadores) {
        StringBuilder s = new StringBuilder("COMIENZA LA PARTIDA\nJugadores:");
        int i = 1;
        for (String nombreJugador : jugadores) {
            s.append(i).append("- ").append(nombreJugador).append("\n");
            i++;
        }
        mostrarInfo(s.toString());
    }

    @Override
    public String getNombreVista() {
        return nombreVista;
    }

    public void mostrarCartas(ArrayList<String> cartas) {
        int i = 0;
        StringBuilder s = new StringBuilder();
        for (String carta : cartas) {
            s.append(i).append(" - ").append(carta);
            i++;
        }
        mostrarInfo(s.toString());
    }

    public int menuBajar() {
        int eleccion = 0;
        String s;
        while (eleccion < 1 || eleccion > 8) {
            s = """
                Elije una opción:
                1 - Bajar uno o más juegos
                2 - Tirar al pozo
                3 - Ordenar cartas
                4 - Acomodar en un juego bajado propio
                5 - Ver juegos bajados propios
                6 - Acomodar en un juego bajado ajeno
                7 - Ver juegos bajados de todos los jugadores
                8 - Ver pozo""";
            eleccion = Integer.parseInt(preguntarInput(s));
        }
        return eleccion;
    }

    public int[] preguntarParaOrdenarCartas(int cantCartas) {
        int[] elecciones = new int[2];
        int cartaSeleccion = -1;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(preguntarInput("Elije el número de carta que quieres mover: "));
        }
        elecciones[0] = cartaSeleccion;

        cartaSeleccion = -1;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(preguntarInput("Elije el número de destino al que quieres mover la carta: "));
        }
        elecciones[1] = cartaSeleccion;
        System.out.println();
        return elecciones;
    }

    public int preguntarCartaParaAcomodar() {
        return Integer.parseInt(preguntarInput("Indica el número de carta que quieres acomodar en un juego"));
    }

    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {
        int numJuego = 1;
        if (juegos.isEmpty()) {
            mostrarInfo("No tiene juegos bajados.");
        } else {
            for (ArrayList<String> juego : juegos) {
                mostrarInfo("Juego N° " + numJuego + ":");
                mostrarCartas(juego);
                numJuego++;
            }
        }
    }

    public boolean preguntarSiQuiereSeguirBajandoJuegos() {
        String resp = preguntarInput("Deseas bajar un juego? (Si/No)");
        return resp.equalsIgnoreCase("Si") || resp.equalsIgnoreCase("S");
    }

    public int[] preguntarQueBajarParaJuego(int cantCartas) {
        int[] cartasABajar = new int[cantCartas];
        for (int i = 0; i < cartasABajar.length; i++) {
            cartasABajar[i] = Integer.parseInt(preguntarInput("Carta " + (i + 1) + ":\nIndica el índice de la carta que quieres bajar: "));
        }
        return cartasABajar;
    }

    public int preguntarCantParaBajar() {
        int numCartas = 0;
        while (numCartas > 4 || numCartas < 3) {
            numCartas = Integer.parseInt(preguntarInput("Cuantas cartas quieres bajar para el juego? (3 o 4)"));
        }
        return numCartas;
    }

    public int preguntarQueBajarParaPozo(int cantCartas) {
        int eleccion = Integer.parseInt(preguntarInput("Indica el índice de carta para tirar al pozo: "));
        while (eleccion < 0 || eleccion >= cantCartas) {
            eleccion = Integer.parseInt(preguntarInput("Ese índice es inválido. Vuelve a ingresar un índice de carta"));
        }
        return eleccion;
    }

    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {
        StringBuilder s = new StringBuilder("Puntuación: \n");
        for (int i = 0; i < puntos.length; i++) {
            s.append("Jugador ").append(ctrl.getJugadorPartida(i).getNombre()).append(": ").append(puntos[i]);
        }
        mostrarInfo(s.toString());
    }

    private void mostrarRanking(Object[] ranking) {
        StringBuilder s = new StringBuilder("Ranking de mejores jugadores: \n");
        int i = 1;
        for (Object o : ranking) {
            s.append(i).append(" - ").append(o).append("\n");
            i++;
        }
        mostrarInfo(s.toString());
    }

 }
