package pacman;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


public class PacManServer{

	private static Iface1 stub;
	private static Migrator migrator;

	static boolean verbose = false;
	static int minplayers = Integer.MIN_VALUE;
	static Vector<String> serversips = new Vector<String>();
	static String serverip = "127.0.0.1";
	
	
	public static void main(String[] args){
		
		/* Parseamos los argumentos */

		
		parseargs(args);
		
/*		
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		

		Enumeration<NetworkInterface> nets;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets)){
	    		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	                System.out.printf("InetAddress: %s\n", inetAddress);
	            }
	        	
	        }
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
*/		
		
		
		
		System.out.println(minplayers);
		System.out.println(verbose);
		System.out.println(serversips);
		System.out.println(serverip);

		//		System.exit(0);
			
		// Establecimiento del stub en el rmiserver
		try{
			// Crear el stub (objeto distribuido)
			stub = new IfaceImpl1(minplayers, verbose);
			migrator = new Migrator(stub);

			// Hacer bind de la instancia en el servidor rmi
			Naming.rebind("rmi://" + serverip + ":1099/Iface1", stub);
		} catch (RemoteException e){
			System.out.println("Hubo una excepción creando la instancia del objeto distribuido");
		} catch (MalformedURLException e){
			System.out.println("URL mal formada al tratar de publicar el objeto");
		}
	}
	
	public static void usage(){
		System.out.println("Modo de empleo: java pacman.PacManServer [OPCION]...");
		System.out.println("Las opciones son las que siguen:");
		System.out.println("\t -n \t Establece el minimo de jugadores que debe tener juego antes de comenzar.");
		System.out.println("\t -v \t Activa el modo de depuracion.");
		System.out.println("\t -ips \t Establece la lista con las direcciones ips de los servidores que alojaran el juego.");
		System.exit(0);
	}
	
	public static void parseargs(String[] args){
	
		Vector<String> v_minplayers = new Vector<String>();
		Vector<String> v_serversips = new Vector<String>();
		Vector<String> v_serverip = new Vector<String>();
		Vector<String> v_verbose = new Vector<String>();
		
		Vector<String> aux = null;
		
		int i = 0;
		int length = args.length;

		try{
		
			while (i < length) {
				
				switch (args[i]) {
				case "-n":
					aux = v_minplayers;
					break;
					
				case "-v":
					aux = v_verbose;
					aux.add("true");
					break;
					
				case "-ips":
					aux = v_serversips;
					break;
					
				case "-server":
					aux = v_serverip;
					break;
					
				default:
					if(aux != null)
						aux.add(args[i]);
					break;
				}
				i++;
			}
			
			if(!v_minplayers.isEmpty()){
				minplayers = new Integer(v_minplayers.firstElement()).intValue();
			}else{
				usage();
			}
	
			if(!v_verbose.isEmpty()){
				verbose = new Boolean(v_verbose.firstElement()).booleanValue();
			}
	
			if(!v_serversips.isEmpty()){
				serversips = v_serversips;
			}else{
				usage();
			}
		
			if(!v_serverip.isEmpty()){
				serverip = v_serverip.firstElement();
			}else{
				usage();
			}

		}catch(Exception e){
			usage();
		}
		
	}
	
	
}