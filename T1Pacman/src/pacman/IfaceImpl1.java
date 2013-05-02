package pacman;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.Timer;

public class IfaceImpl1 extends UnicastRemoteObject implements Iface1 {

	private static final long serialVersionUID = -5328206226473118382L;
	
	GameSession gamesession;
	
	public IfaceImpl1(int minplayers, boolean verbose) throws RemoteException {
		super();
		gamesession = new GameSession(minplayers, verbose);
	}

	@Override
	public void gameinit(String playerid) throws RemoteException {

		Player p = gamesession.players.get(playerid); 
		if(p==null) return;
		
		gamesession.GameInit(p);
		
	}

	@Override
	public GameSession getgamesession() throws RemoteException {
		return gamesession;
	}

	@Override
	public void setgamesession(GameSession _gs) throws RemoteException {
		gamesession = _gs;
	}

	@Override
	public String createplayer() throws RemoteException {
		return gamesession.createplayer();
	}

	public void setreqplayer(String playerid, int dx, int dy){
		
		Player p = gamesession.players.get(playerid); 
		if(p==null) return;
		
		gamesession.setreqplayer(p, dx, dy);
		
	}

	@Override
	public void gameend(String playerid) throws RemoteException {

		Player p = gamesession.players.get(playerid); 
		if(p==null) return;

		gamesession.gameend(p);		
	}
	
}
