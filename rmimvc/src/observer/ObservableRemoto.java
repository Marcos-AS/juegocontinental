package rmimvc.src.observer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Esta clase, entre otras, permiten aplicar el patrón Observer
 * entre el modelo que reside en el servidor y el controlador que posee cada cliente.
 * El modelo remoto debe extender de esta clase para poder notificar a los controladores de los cambios
 * mediante el método notificarObservadores().
 * Cada controlador debe suscribirse con el método agregarObservador() para recibir las actualizaciones del modelo remoto.
 *
 */
public abstract class ObservableRemoto implements Remote, IObservableRemoto {
	private ArrayList<IObservadorRemoto> observadores;// = new ArrayList<>();
	
	public ObservableRemoto() {
		observadores = new ArrayList<>();
	}

	@Override
	public void agregarObservador(IObservadorRemoto o) throws RemoteException {
		observadores.add(o);
	}

	@Override
	public void removerObservador(IObservadorRemoto o) throws RemoteException {
		observadores.remove(o);
	}

	protected ArrayList<IObservadorRemoto> getObservadores() throws RemoteException {
		return observadores;
	}

	@Override
	public void notificarObservadores(Object obj) throws RemoteException {
		for (IObservadorRemoto o: observadores)
			o.actualizar(this, obj);
	}

	public void notificarObservadores(ArrayList<Integer> jugadoresQuePuedenRobarConCastigo, Object obj) throws RemoteException {
		if (!jugadoresQuePuedenRobarConCastigo.isEmpty()) {
			for (int i = 0; i < jugadoresQuePuedenRobarConCastigo.size(); i++) {
				int numJ = jugadoresQuePuedenRobarConCastigo.get(i);
				if (numJ >= 0) {
					observadores.get(numJ).actualizar(this, obj);
				}
			}
		}
	}

	public void notificarObservador(int numJugador, Object o) throws RemoteException {
		observadores.get(numJugador).actualizar(this, o);
	}

}
