package main;

import controlador.Controlador;
import rmimvc.src.RMIMVCException;
import rmimvc.src.cliente.Cliente;
import vista.GUI;
import vista.ifVista;
import java.rmi.RemoteException;
import java.util.Random;

public class AppClienteGUI {
        public static void main(String[] args) {
            //creacion de la vista y el controlador
            ifVista vista = new GUI();
            Controlador ctrl = new Controlador(vista);
            vista.setControlador(ctrl);

            //Cliente cliente = new Cliente(ip, Integer.parseInt(port), ipServer, Integer.parseInt(portServer));
            //Cliente cliente = new Cliente("127.0.0.1", new Random().nextInt((9999-9000)+1)+9900, "192.168.0.247", 8888);
            Cliente cliente = new Cliente("192.168.0.5", 9999, "192.168.0.247", 8888);
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
