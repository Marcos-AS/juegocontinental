package main;

import controlador.Controlador;
import rmimvc.src.RMIMVCException;
//import rmimvc.src.Util;
import rmimvc.src.cliente.Cliente;
import vista.GUI;
import vista.VentanaConsola;
import vista.ifVista;

//import javax.swing.*;
import java.rmi.RemoteException;
//import java.util.ArrayList;
import java.util.Random;

public class AppClienteGUI {
        public static void main(String[] args) {

//        ArrayList<String> ips = Util.getIpDisponibles();
//        String ip = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione la IP en la que escucha peticiones el cliente",
//                "IP del cliente",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                ips.toArray(),
//                null
//        );
//        String port = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione el puerto en el que escucha peticiones el cliente",
//                "Puerto del cliente",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                null,
//                9999
//        );
//        String ipServer = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione la IP en la que se ejecuta el servidor",
//                "IP del servidor",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                ips.toArray(),
//                null
//        );
//        String portServer = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione el puerto en el que escucha peticiones el servidor",
//                "Puerto del servidor",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                null,
//                8888
//        );

            //creacion de la vista y el controlador
            ifVista vista = new GUI();
            Controlador ctrl = new Controlador(vista);
            vista.setControlador(ctrl);

            //Cliente cliente = new Cliente(ip, Integer.parseInt(port), ipServer, Integer.parseInt(portServer));
            Cliente cliente = new Cliente("127.0.0.1", new Random().nextInt((9999-9900)+1)+9900, "192.168.0.247", 8888);
            //Cliente cliente = new Cliente("192.168.0.40", 9999, "192.168.0.247", 8888);
            try {
                //se agrega el ctrl como observador y se setea el modelo como atributo del ctrl
                cliente.iniciar(ctrl);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (RMIMVCException e) {
                e.printStackTrace();
            }
        }
}
