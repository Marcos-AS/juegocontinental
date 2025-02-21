package test;

import controlador.Controlador;
import modelo.Partida;
import rmimvc.src.RMIMVCException;
import rmimvc.src.cliente.Cliente;
import java.rmi.RemoteException;
import java.util.Random;
import vista.VentanaConsola;
import vista.ifVista;

public class Test {
    static ifVista vista = new VentanaConsola();

    public static void main(String[] args) {
        Controlador ctrl1 = new Controlador();
        Controlador ctrl2 = new Controlador();
        Cliente cliente1 = new Cliente("127.0.0.1", new Random().nextInt((9999-9000)+1)+9900, "127.0.0.1", 8888);
        Cliente cliente2 = new Cliente("127.0.0.1", new Random().nextInt((9999-9000)+1)+9900, "127.0.0.1", 8888);
        try {
            cliente1.iniciar(ctrl1);
            cliente2.iniciar(ctrl2);
        } catch (RMIMVCException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        Partida partida;
        {
            try {
                partida = Partida.getInstancia();
                int obsIndex1 = partida.getObservadorIndex(ctrl1);
                int obsIndex2 = partida.getObservadorIndex(ctrl2);
                partida.crearJugador("Marcos",obsIndex1);
                partida.crearJugador("Anita",obsIndex2);
                partida.crearPartida(obsIndex1,2);
                boolean inicio = partida.jugarPartida();
                if (inicio) {
                    partida.empezarRonda();
                    String pozoOMazo = String.valueOf(new Random().nextInt(2)+1);
                    partida.desarrolloRobo(pozoOMazo);
                    String eleccionMenuBajar = String.valueOf(new Random().nextInt(5)+1);
                    switch (eleccionMenuBajar) {
                        case ifVista.BAJARSE -> {
                            if(vista.preguntarSiQuiereSeguirBajandoJuegos()) {
                                int[] indicesCartas = vista.preguntarQueBajarParaJuego();
                                while (partida.hayRepetidos(indicesCartas)) {
                                    vista.mostrarInfo("Debe ingresar los índices de nuevo");
                                    indicesCartas = vista.preguntarQueBajarParaJuego();
                                }
                                partida.bajarse(indicesCartas);
                            }
                        }
                        case ifVista.TIRAR -> {
                            int cartaATirar = vista.preguntarQueBajarParaPozo();
                            partida.tirarAlPozo(cartaATirar);
                        }
                        case ifVista.ORDENAR -> {
                            int[] cartasOrdenacion = vista.preguntarParaOrdenarCartas();
                            partida.moverCartaEnMano(cartasOrdenacion[0], cartasOrdenacion[1]);
                        }
                        case ifVista.ACOMODAR -> {
                            int cartaAcomodar = vista.preguntarCartaParaAcomodar();
                            int iJuego = 1;
                            int numJugador = 1;
                            partida.acomodar(cartaAcomodar, iJuego, numJugador);
                        }
                    };
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

}