package excepciones;

public class JugadorDesconectadoException extends Exception{
    public JugadorDesconectadoException() {
        super("Un jugador se desconectó");
    }
}
