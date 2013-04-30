package pacman;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class IfaceImpl1 extends UnicastRemoteObject implements Iface1 {

	private static final long serialVersionUID = -5328206226473118382L;
	
	GameSession gs;
	
	public IfaceImpl1(int minplayers, boolean verbose) throws RemoteException {
		super();
		gs = new GameSession(minplayers, verbose);
	}

	@Override
	public void gameinit(String playerid) throws RemoteException {

		Player p = gs.players.get(playerid); 
		if(p==null) return;
		
		gs.GameInit(p);
		
	}

	@Override
	public GameSession getgamesession() throws RemoteException {
		return gs;
	}

	@Override
	public void setgamesession(GameSession _gs) throws RemoteException {
		gs = _gs;
	}

	@Override
	public String createplayer() throws RemoteException {
		return gs.createplayer();
	}

	public void setreqplayer(String playerid, int dx, int dy){
		
		Player p = gs.players.get(playerid); 
		if(p==null) return;
		
		gs.setreqplayer(p, dx, dy);
		
	}

	@Override
	public void gameend(String playerid) throws RemoteException {
		// TODO Auto-generated method stub
		Player p = gs.players.get(playerid); 
		if(p==null) return;

		gs.gameend(p);		
	}
	
}
