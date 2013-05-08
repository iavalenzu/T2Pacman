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
	
	
	public GameSession getgamesession() throws RemoteException;

	public void setgamesession(GameSession gs) throws RemoteException;
	
	public String createplayer() throws RemoteException;

	public void setreqplayer(String playerid, int dx, int dy) throws RemoteException; 
	
	public void gameinit(String playerid) throws RemoteException;
	
	public void gameend(String playerid) throws RemoteException;

	
}
