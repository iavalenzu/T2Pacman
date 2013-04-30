package pacman;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface Iface1 extends Remote{
	
	/*
	* Esta es la interfaz (skeleton) del objeto distribuido
	* Permite definir los métodos que se le pueden invocar a dichos
	* objetos en el servidor.
	*/ 
	
	public GameSession getgamesession() throws RemoteException;

	public void setgamesession(GameSession gs) throws RemoteException;
	
	public String createplayer() throws RemoteException;

	public void setreqplayer(String playerid, int dx, int dy) throws RemoteException; 
	
	public void gameinit(String playerid) throws RemoteException;
	
	public void gameend(String playerid) throws RemoteException;
	
}
