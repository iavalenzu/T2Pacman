package pacman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Timer;

public class Server implements ActionListener {

	private Timer migrator;
	
	private Vector<String> servers = new Vector<String>();
	private String hostname, otherserver;
	private String migrationhostname;
	
	private int minplayers;
	private boolean verbose;

	/*Guarda la session del juego*/
	private GameSession gamesession = null;
	
	public Server(String _hostname, String _otherserver, int _minplayers, boolean _verbose){

		hostname = _hostname;
		otherserver = _otherserver;
		minplayers = _minplayers;
		verbose = _verbose;
		
		migrationhostname = null;
		
		
		if(otherserver == null)
			gamesession = new GameSession(minplayers, verbose);
		else
			gamesession = null;
		
		migrator = new Timer(20000, this);
		migrator.start();
		registerHostname();
		
	}
	
	public String getMigrationHostname(){
		return migrationhostname;
	}
	
	
	public GameSession getGameSession(){
		return gamesession;
	}
	
	public void setGameSession(GameSession gs){
		
		Logger.debug("Recibo el juego y lo continúo.", "Server",  verbose);
		
		gamesession = gs;
		gamesession.restart();
		migrator.restart();
		migrationhostname = null;
		
		
	}
	
	public Player getPlayer(String playerid){
		return gamesession.getPlayer(playerid); 
	}
	
	public void migrate(){
		
		if(gamesession == null) return;
		
		/*Solo migra si el juego esta andando*/
		if(!gamesession.isRunning()) return;
		
		if(servers.isEmpty()) return;
		
		String chosenserver = getMinOverLoadServer();
		
		try {
	    	Iface1 skeleton = (Iface1) Naming.lookup("rmi://" + chosenserver + ":1099/Iface1");
	    	migrator.stop();
	    	gamesession.stop();
	    	skeleton.setGameSession(gamesession);
	    	migrationhostname = chosenserver;
	    	
	    	Logger.debug("Migro el juego al servidor '" + chosenserver + "'.", "Server", verbose);
	    	
		} catch (Exception e) {
			Logger.error("No pude migrar el juego al servidor '" + chosenserver + "'");
			Logger.error(e.getMessage());
		}
		
		
	}

	public String getMinOverLoadServer(){
		
		if(servers.isEmpty()) return null;
		
		Iterator<String> itr = servers.iterator();
		
		double min = Double.MAX_VALUE;
		String chosenserver = null;
		
		/*Recorremos la lista de servidores obtenida e informacion la existencia de hostname*/
	    while(itr.hasNext()){
	    	
	    	String serverip = itr.next();
	    	try {
		    	Iface1 skeleton = (Iface1) Naming.lookup("rmi://" + serverip + ":1099/Iface1");
		    	double overload = skeleton.getOverLoad(); 
		    	if(overload < min){
		    		min = overload;
		    		chosenserver = serverip;
		    	}
			} catch (Exception e) {
				Logger.error("No puedo obtener la carga del servidor '" + serverip + "'");
				Logger.error(e.getMessage());
			}
	    }		
	    
	    return chosenserver;
	}
	
	public void registerHostname(){

		Vector<String> otherservers = null;
		
		if(otherserver != null && !otherserver.equals(hostname)){
		
			try {
				Iface1 skeleton = (Iface1) Naming.lookup("rmi://" + otherserver + ":1099/Iface1");

				/*Se obtiene la lista de los servidores conectados con 'otherserver'*/
				otherservers = skeleton.getServers();
				Logger.debug("Obteniendo la lista de servidores de '" + otherserver + "'", "Server", verbose);

				/*Se agrega el hostname actual a la lista de servidores de 'otherserver'*/
				if(skeleton.addServerIp(hostname))
					Logger.debug("Agregé '" + hostname + "' al servidor '" + otherserver + "'", "Server", verbose);
				else
					Logger.debug("Fallé al agregar '" + hostname + "' al servidor '" + otherserver + "'", "Server", verbose);
				
				/*Se agrega 'otherserver' a la lista de servidores*/
				servers.add(otherserver);
				
			} catch (Exception e) {
				Logger.error("No puedo obtener la lista de servidores de '" + otherserver + "'");
				Logger.error(e.getMessage());
			}	
		
		}
		
		if(otherservers != null && !otherservers.isEmpty()){
			
			Iterator<String> itr = otherservers.iterator();
			
			/*Recorremos la lista de servidores obtenida e informacion la existencia de hostname*/
		    while(itr.hasNext()){
		    	
		    	String serverip = itr.next();

		    	if(!serverip.equals(hostname)){
		    	
					try {
						
				    	Iface1 skeleton = (Iface1) Naming.lookup("rmi://" + serverip + ":1099/Iface1");
	
				    	if(skeleton.addServerIp(hostname))
				    		Logger.debug("Informando la existencia del nuevo servidor '" + hostname + "' al servidor '" + serverip + "'", "Server", verbose);
				    	else
				    		Logger.debug("Falle al informar la existiencia del nuevo servidor '" + hostname + "' al servidor '" + serverip + "'", "Server", verbose);
				    	
				    	servers.add(serverip);
				    	
					} catch (Exception e) {
						Logger.error("No puedo informa la existencia del nuevo servidor a '" + serverip + "'");
						Logger.error(e.getMessage());
					}
					
		    	}
		    }
			
		}
		
		Logger.debug("Servers: " + servers.toString(), "Server", verbose);

	}	
	
	public String getActiveGameServer(){
		
		if(gamesession != null){
			return hostname;
		}
		
		if(servers != null && !servers.isEmpty()){
			
			Iterator<String> itr = servers.iterator();
			
			/*Recorremos la lista de servidores obtenida buscando quien esta manejando el juego en este momento*/
		    while(itr.hasNext()){
		    	
		    	String serverip = itr.next();

		    	if(!serverip.equals(hostname)){
		    	
					try {
						
				    	Iface1 skeleton = (Iface1) Naming.lookup("rmi://" + serverip + ":1099/Iface1");
				    	
				    	if(skeleton.getGameSession() != null){
				    		Logger.debug("El juego esta corriendo actualmente en el servidor '" + hostname + "'", "Server", verbose);
				    		return serverip;
				    	}
				    	
					} catch (Exception e) {
						Logger.error("No puedo saber si el servidor '" + serverip + "' esta corriendo el juego.");
						Logger.error(e.getMessage());
					}
					
		    	}
		    }
		}		
		
	    return null;
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		migrate();
	}

	public Vector<String> getServers() {
		return servers;
	}

	public boolean addServerIp(String server) {

		boolean out = false;
		
		if(!servers.contains(server))
			out = servers.add(server);

		Logger.debug("Servers: " + servers.toString(), "Server", verbose);
		
		return out;		
	}

}
