package modelo;

import java.util.ArrayList;
import java.util.Iterator;

public class Comprobar {
    static int TRIO = 0;
    static int ESCALERA = 1;
    public static int JUEGO_INVALIDO = 2;

    static int comprobarJuego(ArrayList<Carta> juego, int ronda) {
        int esJuego = JUEGO_INVALIDO;
        switch (ronda) {
            case 1:
            case 4:
                esJuego = comprobarTrio(juego);
                break;
            case 2:
            case 5:
            case 6:
                if (comprobarTrio(juego) == TRIO) {
                    esJuego = TRIO;
                } else {
                    if (comprobarEscalera(juego) == ESCALERA) {
                        esJuego = ESCALERA;
                    }
                }
                break;
            case 3:
            case 7:
                esJuego = comprobarEscalera(juego);
                break;
        }
        return esJuego;
    }

    private static int comprobarTrio(ArrayList<Carta> juego) {
        int formaTrio = 1;
        //igual a false, lo pongo en numero para despues saber si es una escalera o un trio
        int esTrio = JUEGO_INVALIDO;
        int i = 0;
        int numCarta = juego.get(i).getNumero();
        while (numCarta == -1 && i < juego.size()-1) {
            formaTrio++;
            i++;
            numCarta = juego.get(i).getNumero();
        }
        while (i < juego.size()-1) {
            i++;
            int numCartaSig = juego.get(i).getNumero();
            if (numCarta == numCartaSig || numCartaSig == -1) {
                formaTrio++;
            } else {
                formaTrio = 0;
            }
        }
        if (formaTrio >= 3)
            esTrio = TRIO;
        return esTrio;
    }

    private static int comprobarEscalera(ArrayList<Carta> juego) {
        int esEscalera = JUEGO_INVALIDO; //igual a false, lo pongo en numero para despues saber si es una escalera o un trio
        ArrayList<Carta> comodines = extraerComodines(juego);

        if (comprobarMismoPalo(juego)) {
            int contadorEscalera = 1;
            ordenarCartas(juego);
            for (int i = 0; i < juego.size()-1; i++) {
                int numCartaActual = juego.get(i).getNumero();
                int numCartaSiguiente = juego.get(i + 1).getNumero();
                if (numCartaActual == 13 && numCartaSiguiente == 1) {
                    contadorEscalera++;
                }
                else if (numCartaSiguiente == numCartaActual + 1) {
                    contadorEscalera++;
                } else {
                    if (!comodines.isEmpty()) {
                        if (numCartaActual == numCartaSiguiente - 2) {
                            contadorEscalera += 2;
                            comodines.remove(0);
                        }
                    } else {
                        contadorEscalera = 1;
                    }
                }
            }
            if (!comodines.isEmpty())  contadorEscalera += comodines.size();
            if (contadorEscalera >= 4)
                esEscalera = ESCALERA;
        }
        return esEscalera;
    }

    static void ordenarCartas(ArrayList<Carta> cartas) { //metodo de insercion
        boolean intercambio = true, contieneK = false,contieneAs = false;
        while (intercambio) {
            intercambio = false;
            for (int i = 0; i < cartas.size() - 1; i++) {
                Carta cartaActual = cartas.get(i);
                Carta cartaSiguiente = cartas.get(i+1);
                //valido si hay una k y un as, entonces el as debe ser la ultima carta
                if (!contieneK) contieneK = cartaActual.getNumero() == 13 ||
                        cartaSiguiente.getNumero() == 13;
                if (!contieneAs) contieneAs = cartaActual.getNumero() == 1 ||
                        cartaSiguiente.getNumero() == 1;

                if (cartaActual.getNumero() > cartaSiguiente.getNumero()) {
                    intercambio = true;
                    cartas.set(i, cartaSiguiente); //muevo la siguiente un lugar hacia atras
                    cartas.set(i + 1, cartaActual); //o lo mismo, muevo la actual un lugar hacia delante
                }
            }
        }
        if (contieneAs&&contieneK) {
            Carta as = cartas.remove(0);
            cartas.add(as);
        }
    }

    static ArrayList<Carta> extraerComodines(ArrayList<Carta> juego) {
        ArrayList<Carta> comodines = new ArrayList<>();
        Iterator<Carta> iterador = juego.iterator();
        while (iterador.hasNext()) {
            Carta c = iterador.next();
            if (c.getPalo()==Palo.COMODIN) {
                comodines.add(c);
                iterador.remove();
            }
        }
        return comodines;
    }

    static boolean comprobarMismoPalo(ArrayList<Carta> cartas) {
        boolean mismoPalo = false;
        for (int i = 0; i < cartas.size() - 1; i++) {
            Palo palo = cartas.get(i).getPalo();
            mismoPalo = palo == cartas.get(i + 1).getPalo();
        }
        return mismoPalo;
    }

    static boolean comprobarPosibleCorte(int ronda, int trios,
                               int escaleras) {
        boolean puedeCortar = false;
        switch (ronda) {
            case 1:
                puedeCortar = trios == 2;
                break;
            case 2:
                puedeCortar = trios == 1 && escaleras == 1;
                break;
            case 3:
                puedeCortar = escaleras == 2;
                break;
            case 4:
                puedeCortar = trios == 3;
                break;
            case 5:
                puedeCortar = trios == 2 && escaleras == 1;
                break;
            case 6:
                puedeCortar = trios == 1 && escaleras == 2;
                break;
            case 7:
                puedeCortar = escaleras == 3;
                break;
            default:
                break;
        }
        return puedeCortar;
    }
}