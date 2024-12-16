package main;

import modelo.Partida;
import rmimvc.src.RMIMVCException;
//import rmimvc.src.Util;
import rmimvc.src.servidor.Servidor;

//import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
//import java.util.ArrayList;

public class AppServidor {

    public static void main(String[] args) throws RemoteException, UnknownHostException {
//        ArrayList<String> ips = Util.getIpDisponibles();
//        String ip = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione la IP en la que escuchará peticiones el servidor",
//                "IP del servidor",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                ips.toArray(),
//                null
//        );
//        String port = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione el puerto en el que escuchará peticiones el servidor",
//                "Puerto del servidor",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                null,
//                8888
//        );
        Partida modelo = Partida.getInstancia(); //crea instancia del juego, sera unica
        //Servidor servidor = new Servidor(ip, Integer.parseInt(port));
        //Servidor servidor = new Servidor("127.0.0.1", 8888);
        Servidor servidor = new Servidor("192.168.0.247", 8888);
        try {
            servidor.iniciar(modelo);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RMIMVCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
