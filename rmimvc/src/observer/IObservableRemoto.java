package rmimvc.src.observer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Esta es la interface con los métodos públicos de la clase ObservableRemoto. Extiende la interface Remote
 * ya que el observable será accedido remotamente, por eso todos los métodos pueden generar una RemoteException.
 * 
 */
public interface IObservableRemoto extends Remote {

	/**
	 * Agrega un observador para que pueda recibir las actualizaciones de cambios de estado.
	 * 
	 * @param o observador a agregar.
	 * @throws RemoteException como es un método remoto puede fallar la comunicación.
	 * @see Remote
	 * @see RemoteException
	 */
	void agregarObservador(IObservadorRemoto o) throws RemoteException;

	/**
	 * Saca un observador para que deje de recibir actualizaciones en los cambios de estado.
	 * 
	 * @param o observador a remover.
	 * @throws RemoteException como es un método remoto puede fallar la comunicación.
	 * @see Remote
	 * @see RemoteException
	 */
	void removerObservador(IObservadorRemoto o) throws RemoteException;

	/**
	 * Notifica a cada uno de los observadores pasándoles el parámetro obj.
	 * 
	 * @param obj objeto a pasar con la notificación de cambio de estado.
	 * @throws RemoteException como es un método remoto puede fallar la comunicación.
	 * @see Remote
	 * @see RemoteException
	 */
	void notificarObservadores(Object obj) throws RemoteException;

	/**
	 * Notifica a cada uno de los observadores de un cambio de estado.
	 * 
	 * @throws RemoteException como es un método remoto puede fallar la comunicación.
	 * @see Remote
	 * @see RemoteException
	 */
	void notificarObservadores(ArrayList<Integer> jugadoresQuePuedenRobarConCastigo, Object obj) throws RemoteException;
	void notificarObservador(int numJugador, Object o) throws RemoteException;

}