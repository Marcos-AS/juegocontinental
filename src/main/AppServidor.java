package main;

import modelo.Partida;
import rmimvc.src.RMIMVCException;
import rmimvc.src.servidor.Servidor;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class AppServidor {

    public static void main(String[] args) throws RemoteException, UnknownHostException {
        Partida modelo = Partida.getInstancia(); //crea instancia del juego, sera unica
        //Servidor servidor = new Servidor(ip, Integer.parseInt(port));
        //Servidor servidor = new Servidor("127.0.0.1", 8888);
        //System.out.println(InetAddress.getLocalHost().getHostAddress());
        Servidor servidor = new Servidor("192.168.0.247", 8888);
        try {
            servidor.iniciar(modelo);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RMIMVCException e) {
            e.printStackTrace();
        }
    }
}
