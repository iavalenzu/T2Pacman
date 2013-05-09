package pacman;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.net.MalformedURLException;


public class PacManServer{

	private static Iface1 stub;

	static boolean verbose = false;
	static int minplayers = 0;
	static String hostname = "127.0.0.1";
	static String otherserver = null;
	
	
	public static void main(String[] args){
		
		/* Parseamos los argumentos */
		parseargs(args);
			
		// Establecimiento del stub en el rmiserver
		try{
			Logger.debug("El servidor esta corriendo en " + hostname + ".", "Server",  verbose);

			// Crear el stub (objeto distribuido)
			stub = new IfaceImpl1(minplayers, hostname, otherserver, verbose);

			// Hacer bind de la instancia en el servidor rmi
			Naming.rebind("rmi://" + hostname + ":1099/Iface1", stub);
			
		} catch (RemoteException e){
			e.printStackTrace();
			Logger.error("Hubo una excepción creando la instancia del objeto distribuido.");
		} catch (MalformedURLException e){
			Logger.error("URL mal formada al tratar de publicar el objeto.");
		}
	}
	
	public static void usage(){
		System.out.println("Modo de empleo: java pacman.PacManServer [OPCION]...");
		System.out.println("Las opciones son las que siguen:");
		System.out.println("\t -n \t Establece el minimo de jugadores que debe tener juego antes de comenzar.");
		System.out.println("\t -v \t Activa el modo de depuracion.");
		System.out.println("\t -hostname \t Define la ip donde correrá el servidor del juego.");
		System.out.println("\t -otherserver \t Establece la ip de algun servidor de un grupo de servidores que esten corriendo el juego, con el fin de agregarlo al grupo y permitir la migracion.");
		System.exit(0);
	}
	
	public static void parseargs(String[] args){
	
		int i = 0;
		int length = args.length;

		try{
		
			while (i < length) {
				
				switch (args[i]) {
				case "-h":
					usage();
					break;
					
				case "-n":
					minplayers = new Integer(args[++i]).intValue();
					break;
					
				case "-v":
					verbose = true;
					break;
					
				case "-otherserver":
					otherserver = args[++i];
					break;

				case "-hostname":
					hostname = args[++i];
					
					/*Fijamos la propiedad*/
					System.setProperty("java.rmi.server.hostname", hostname);
					break;
					
				default:
					usage();
					break;
				}
				i++;
			}
			

		}catch(Exception e){
			usage();
		}
		
	}
	
	
}