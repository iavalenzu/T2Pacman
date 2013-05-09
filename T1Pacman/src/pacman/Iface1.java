package pacman;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.Vector;

public interface Iface1 extends Remote{
	
	/*
	* Esta es la interfaz (skeleton) del objeto distribuido
	* Permite definir los métodos que se le pueden invocar a dichos
	* objetos en el servidor.
	*/ 
	
	public Vector<String> getServers() throws RemoteException;

	public boolean addServerIp(String server) throws RemoteException;
	
	public double getOverLoad() throws RemoteException;
	
	public String  getMigrationHostname() throws RemoteException;
	
	public String getActiveGameServer() throws RemoteException; 
	
	public GameSession getGameSession() throws RemoteException;

	public void setGameSession(GameSession gs) throws RemoteException;
	
	public String createPlayer() throws RemoteException;

	public void setReqPlayer(String playerid, int dx, int dy) throws RemoteException; 
	
	public void gameInit(String playerid) throws RemoteException;
	
	public void gameEnd(String playerid) throws RemoteException;

	
}
