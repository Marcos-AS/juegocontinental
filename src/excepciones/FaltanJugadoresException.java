package excepciones;

public class FaltanJugadoresException extends Exception {
    public FaltanJugadoresException() {
        super("Faltan jugadores para iniciar la partida");
    }
}
