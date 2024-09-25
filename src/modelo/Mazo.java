package modelo;

import rmimvc.src.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class Mazo extends ObservableRemoto {

    public static ArrayList<Carta> iniciarMazo(int numBarajas)  throws RemoteException {
        ArrayList<Carta> mazo = new ArrayList<>();
        int i = 0;
        while(i < numBarajas) {
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.PICAS));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.DIAMANTES));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.TREBOL));
            for(int j = 1; j < 14; j++)
                mazo.add(new Carta(j, Palo.CORAZONES));
            for(int j = 0; j < 2; j++)
                mazo.add(new Carta(-1, Palo.COMODIN));
            i++;
        }
        return mazo;
    }

    public static ArrayList<Carta> mezclarCartas(ArrayList<Carta> mazo)  throws RemoteException{
        ArrayList<Carta> mazoMezclado = new ArrayList<>();
        Random random = new Random();
        while(!mazo.isEmpty()) {
            Carta c = mazo.remove(random.nextInt(mazo.size()));
            mazoMezclado.add(c);
        }
        return mazoMezclado;
    }

    public static Carta sacarPrimeraDelMazo(ArrayList<Carta> mazo) {
        return mazo.remove(mazo.size()-1);
    }
}
