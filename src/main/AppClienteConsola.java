package main;

import controlador.Controlador;
import rmimvc.src.RMIMVCException;
import rmimvc.src.cliente.Cliente;
import vista.VentanaConsola;
import vista.ifVista;

import java.rmi.RemoteException;
import java.util.Random;


public class AppClienteConsola {

    public static void main(String[] args) {

//        ArrayList<String> ips = Util.getIpDisponibles();
//        String ip = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione la IP en la que escuchará peticiones el cliente",
//                "IP del cliente",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                ips.toArray(),
//                null
//        );
//        String port = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione el puerto en el que escuchará peticiones el cliente",
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
        ifVista vista = new VentanaConsola();
        Controlador ctrl = new Controlador(vista);
        vista.setControlador(ctrl);

        //Cliente cliente = new Cliente(ip, Integer.parseInt(port), ipServer, Integer.parseInt(portServer));
        Cliente cliente = new Cliente("127.0.0.1", new Random().nextInt((9999-9900)+1)+9900, "127.0.0.1", 8888);
        try {
            //se agrega el ctrl como observador y se setea el modelo como atributo del ctrl
            cliente.iniciar(ctrl);
            vista.iniciar();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RMIMVCException e) {
            e.printStackTrace();
        }
    }
}