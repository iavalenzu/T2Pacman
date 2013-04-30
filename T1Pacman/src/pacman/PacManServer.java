package pacman;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.net.MalformedURLException;


public class PacManServer{

	private static Iface1 stub;

	public static void main(String[] args){
		
		boolean verbose = false;
		int minplayers = Integer.MIN_VALUE;
		
		
		/* Parseamos los argumentos */
		
		int length = args.length;
		int i = 0;
		
		try{
		
			while (i < length) {
				
				switch (args[i]) {
				case "-n":
					minplayers = Integer.parseInt(args[++i]);
					break;
				case "-v":
					verbose = true;
					break;
	
				default:
					break;
				}
				i++;
			}
			
		}catch(Exception e){
			System.out.println("Modo de empleo: java pacman.PacManServer [OPCION]...");
			System.out.println("Las opciones son las que siguen:");
			System.out.println("\t -n \t Establece el maximo de jugadores que pueden participar de un juego.");
			System.out.println("\t -v \t Activa el modo de depuracion.");
			System.exit(0);
		}
		
		// Establecimiento del stub en el rmiserver
		try{
			// Crear el stub (objeto distribuido)
			stub = new IfaceImpl1(minplayers, verbose);

			// Hacer bind de la instancia en el servidor rmi
			Naming.rebind("rmi://localhost:1099/Iface1", stub);
		} catch (RemoteException e){
			System.out.println("Hubo una excepción creando la instancia del objeto distribuido");
		} catch (MalformedURLException e){
			System.out.println("URL mal formada al tratar de publicar el objeto");
		}
	}
}