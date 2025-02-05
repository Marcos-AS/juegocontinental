package modelo;

import java.util.ArrayList;
import java.util.Random;

public class Mazo {
    private static final int BARAJAS_HASTA_4_JUGADORES = 2;
    private static final int BARAJAS_MAS_4_JUGADORES = 3;

    protected static ArrayList<Carta> iniciarMazo(int numBarajas) {
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
                mazo.add(new Carta(Carta.COMODIN, Palo.COMODIN));
            i++;
        }
        return mazo;
    }

    protected static ArrayList<Carta> mezclarCartas(ArrayList<Carta> mazo) {
        ArrayList<Carta> mazoMezclado = new ArrayList<>();
        Random random = new Random();
        while(!mazo.isEmpty()) {
            Carta c = mazo.remove(random.nextInt(mazo.size()));
            mazoMezclado.add(c);
        }
        return mazoMezclado;
    }

    protected static Carta sacarPrimeraDelMazo(ArrayList<Carta> mazo) {
        return mazo.remove(mazo.size()-1);
    }

    protected static int determinarNumBarajas(ArrayList<Jugador> jugadores) {
        int cantBarajas = BARAJAS_HASTA_4_JUGADORES;
        if (jugadores.size() >= 4 && jugadores.size() <= 6) {
            cantBarajas = BARAJAS_MAS_4_JUGADORES;
            //} else if(this.jugadores.size() >= 6 && this.jugadores.size() <= 8) {
            //  cantBarajas = BARAJAS_MAS_6_JUGADORES;
        }
        return cantBarajas;
    }
}
