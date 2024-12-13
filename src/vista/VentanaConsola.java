package vista;

import controlador.Controlador;
import modelo.Eventos;
import modelo.ifCarta;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VentanaConsola extends JFrame implements ifVista {
    private Controlador ctrl;
    private String nombreVista;

    public void iniciar() throws RemoteException {
        int eleccion;
        int cantJugadores; //minimo
        boolean partidaIniciada = false;
        boolean partidaCreada = false;
        do {
            eleccion = menuInicial();
            switch (eleccion) {
                case ifVista.ELECCION_CREAR_PARTIDA: {
                    if (!ctrl.isPartidaEnCurso()) {
                        nombreVista = preguntarInput("Indica tu nombre:");
                        cantJugadores = Integer.parseInt(preguntarInput("Cuántos jugadores" +
                                " deseas para la nueva partida?"));
                        ctrl.crearPartida(cantJugadores);
                        partidaCreada = true;
                    } else {
                        mostrarInfo("Ya hay una partida en curso");
                    }
                    break;
                }
                case ifVista.ELECCION_JUGAR_PARTIDA: {
                    if (partidaCreada || ctrl.isPartidaEnCurso()) {
                        if (nombreVista == null) nombreVista = preguntarInput("Indica tu nombre: ");
                        Eventos inicioPartida = ctrl.jugarPartidaRecienIniciada();
                        if (inicioPartida == Eventos.PARTIDA_AUN_NO_CREADA) {
                            mostrarInfo("La partida aun no ha sido creada." +
                                    " Seleccione la opción 'Crear partida' ");
                        } else if (inicioPartida == Eventos.FALTAN_JUGADORES) {
                            partidaIniciada = true;
                            mostrarInfo("Esperando que ingresen más jugadores...");
                        } else if (inicioPartida == Eventos.INICIAR_PARTIDA) {
                            partidaIniciada = true; //esto inicia el funcionamiento del juego
                            ctrl.notificarComienzoPartida();
                            ctrl.partida();
                        }
                    } else {
                        mostrarInfo("Primero tienes que crear una partida");
                    }
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
                //                case 5: {
                //                    Object[] partidas = srl.readObjects();
                //                    for (Object o : partidas) {
                //                        System.out.println(o.toString());
                //                    }
                //                }
            }
        } while (eleccion != -1 && !partidaIniciada);
    }

    private int menuInicial() throws RemoteException {
        int eleccion = 0;
        do {
            try {
                eleccion = Integer.parseInt(preguntarInputInicial(ifVista.MENU_INICIAR));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } while (eleccion < 1 || eleccion > 4);
        return eleccion;
    }

    public void setControlador(Controlador ctrl) {
        this.ctrl = ctrl;
    }

    public int getNumJugadorAcomodar() {
        return Integer.parseInt(preguntarInput("Ingresa el número de jugador en cuyos" +
                        " juegos bajados quieres acomodar: "));
    }

    public void comienzoTurno(String nomJ, int numJ) throws RemoteException {
        mostrarInfo(ifVista.mostrarCombinacionRequerida(ctrl.getRonda()));
        if (!nomJ.equals(nombreVista)) {
            mostrarInfo("--------------------------\nEs el turno de "
                    + nomJ);
        } else {
            mostrarInfo("************\nEs tu turno.");
            ctrl.setTurno(numJ, true);
        }
    }

    public void mostrarInfo(String s) {
        JOptionPane.showMessageDialog(null, s,
                "Jugador: " + nombreVista, JOptionPane.INFORMATION_MESSAGE);
    }

    public String preguntarInputMenu(String s, String cartas) {
        String mostrar = cartas + "\n " + s;
        return JOptionPane.showInputDialog(null, mostrar, nombreVista, JOptionPane.QUESTION_MESSAGE);
    }

    public String preguntarInput(String s) {
        return JOptionPane.showInputDialog(null, s, nombreVista, JOptionPane.QUESTION_MESSAGE);
    }

    public String preguntarInputRobar(ArrayList<String> cartas, String nomJ)
            throws RemoteException {
        String mostrar = getCartasString(cartas) + "\n Pozo: " + getPozoString(ctrl.getPozo()) + "\n " + ifVista.MENU_ROBAR;
        return JOptionPane.showInputDialog(null, mostrar, nomJ, JOptionPane.QUESTION_MESSAGE);
    }

    public String preguntarInputRobarCastigo(ArrayList<String> cartas, String nomJ)
            throws RemoteException {
        String mostrar = getCartasString(cartas) + "\n Pozo: " + getPozoString(ctrl.getPozo()) + "\n " + ifVista.PREGUNTA_ROBAR_CASTIGO;
        return JOptionPane.showInputDialog(null, mostrar, nomJ, JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public boolean isRespAfirmativa(String eleccion) {
        String e = eleccion.toLowerCase();
        return e.equals("si") || eleccion.equals("s");
    }

    public String preguntarInputInicial(String s) throws RemoteException {
        String enCurso = "";
        if (ctrl.isPartidaEnCurso()) {
            enCurso = "YA HAY UNA PARTIDA INICIADA";
        }
        String mostrar = s + "\n\n " + enCurso;
        return JOptionPane.showInputDialog(null, mostrar,
                "Menú inicial", JOptionPane.QUESTION_MESSAGE);
    }

    public String getPozoString(ifCarta c) {
        String s;
        if (c == null) {
            s = "Pozo vacío";
        } else {
            s = ifVista.cartaToString(c);
        }
        return s;
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


    public String getNombreVista() {
        return nombreVista;
    }

    public String getCartasString(ArrayList<String> cartas) {
        int i = 0;
        StringBuilder s = new StringBuilder();
        for (String carta : cartas) {
            s.append(i).append(" - ").append(carta).append("\n");
            i++;
        }
        return s.toString();
    }

    public int menuBajar(String cartasStr) {
        int eleccion = 0;
        String s = cartasStr + "\n";
        while (eleccion < 1 || eleccion > 8) {
            s += """
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

    public int[] preguntarParaOrdenarCartas(ArrayList<String> cartas) {
        int[] elecciones = new int[2];
        int cartaSeleccion = -1;
        int cantCartas = cartas.size();
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInputMenu("Elije el número de carta que quieres mover: ",
                            getCartasString(cartas)));
        }
        elecciones[0] = cartaSeleccion;

        cartaSeleccion = -1;
        while (cartaSeleccion < 0 || cartaSeleccion > cantCartas - 1) {
            cartaSeleccion = Integer.parseInt(
                    preguntarInputMenu("Elije el número de destino al que quieres" +
                            " mover la carta: ", getCartasString(cartas)));
        }
        elecciones[1] = cartaSeleccion;
        return elecciones;
    }

    public int preguntarCartaParaAcomodar(ArrayList<String> cartas) {
        return Integer.parseInt(
                preguntarInputMenu("Indica el número de carta que quieres acomodar" +
                        " en un juego", getCartasString(cartas)));
    }

    public void mostrarJuegos(ArrayList<ArrayList<String>> juegos) {
        int numJuego = 1;
        if (juegos.isEmpty()) {
            mostrarInfo("No tiene juegos bajados.");
        } else {
            for (ArrayList<String> juego : juegos) {
                mostrarInfo("Juego N° " + numJuego + ":\n" + getCartasString(juego));
                numJuego++;
            }
        }
    }

    public boolean preguntarSiQuiereSeguirBajandoJuegos(ArrayList<String> cartas) {
        String resp = preguntarInputMenu("Deseas bajar un juego? (Si/No)"
                , getCartasString(cartas));
        return isRespAfirmativa(resp);
    }

    public int[] preguntarQueBajarParaJuego(ArrayList<String> cartas) {
        int[] cartasABajar = new int[preguntarCantParaBajar(cartas)];
        int iCarta;
        for (int i = 0; i < cartasABajar.length; i++) {
            do {
                iCarta = Integer.parseInt(preguntarInputMenu("Carta " + (i + 1) +
                        ":\nIndica el índice de la carta que quieres bajar: ",
                        getCartasString(cartas)));
            } while (iCarta < 0 || iCarta >= cartas.size());
            cartasABajar[i] = iCarta;
        }
        return cartasABajar;
    }

    private int preguntarCantParaBajar(ArrayList<String> cartas) {
        int numCartas = 0;
        while (numCartas > 4 || numCartas < 3) {
            numCartas = Integer.parseInt(
                    preguntarInputMenu("Cuantas cartas quieres bajar para el juego? (3 o 4)",
                            getCartasString(cartas)));
        }
        return numCartas;
    }

    public int preguntarQueBajarParaPozo(ArrayList<String> cartas) {
        String cartasStr = getCartasString(cartas); //modificar
        int eleccion = Integer.parseInt(
                preguntarInputMenu("Indica el índice de carta para tirar al pozo: ",
                        cartasStr));
        while (eleccion < 0 || eleccion >= cartas.size()) {
            eleccion = Integer.parseInt(preguntarInputMenu("Ese índice es inválido." +
                    " Vuelve a ingresar un índice de carta", cartasStr));
        }
        return eleccion;
    }

    public void mostrarPuntosRonda(int[] puntos) throws RemoteException {
        StringBuilder s = new StringBuilder("Puntuación: \n");
        for (int i = 0; i < puntos.length; i++) {
            s.append(ctrl.getJugadorPartida(i).getNombre())
                            .append(": ").append(puntos[i]).append("\n");
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
