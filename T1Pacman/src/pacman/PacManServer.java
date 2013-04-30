package pacman;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.net.MalformedURLException;


public class PacManServer{

	private static Iface1 stub;

	public static void main(String[] args){
		
		// Establecimiento del stub en el rmiserver
		try{
			// Crear el stub (objeto distribuido)
			stub = new IfaceImpl1();

			// Hacer bind de la instancia en el servidor rmi
			Naming.rebind("rmi://localhost:1099/Iface1", stub);
		} catch (RemoteException e){
			System.out.println("Hubo una excepción creando la instancia del objeto distribuido");
		} catch (MalformedURLException e){
			System.out.println("URL mal formada al tratar de publicar el objeto");
		}
	}
}