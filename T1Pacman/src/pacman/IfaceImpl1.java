package pacman;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class IfaceImpl1 extends UnicastRemoteObject implements Iface1 {

	private static final long serialVersionUID = -5328206226473118382L;

	private Server server = null;

	public IfaceImpl1(int minplayers, String hostname, String otherserver,
			boolean verbose) throws RemoteException {
		super();
		server = new Server(hostname, otherserver, minplayers, verbose);
	}

	public Vector<String> getServers() throws RemoteException {
		return server.getServers();
	}

	@Override
	public boolean addServerIp(String servername) throws RemoteException {
		return server.addServerIp(servername);
	}

	//TODO Modificar esta funcion. y obtener la carga del servidor
	public double getOverLoad() {
		return Math.random();
	}

	@Override
	public void gameInit(String playerid) throws RemoteException {

		GameSession gamesession = server.getGameSession();
		if (gamesession == null)
			return;
		Player p = server.getPlayer(playerid);
		if (p == null)
			return;
		gamesession.GameInit(p);

	}

	@Override
	public GameSession getGameSession() throws RemoteException {
		return server.getGameSession();
	}

	@Override
	public void setGameSession(GameSession _gs) throws RemoteException {
		server.setGameSession(_gs);
	}

	public String getActiveGameServer() throws RemoteException {
		return server.getActiveGameServer();
	}

	@Override
	public String getMigrationHostname() throws RemoteException {
		return server.getMigrationHostname();
	}

	@Override
	public String createPlayer() throws RemoteException {

		GameSession gamesession = server.getGameSession();
		if (gamesession == null)
			return null;
		return gamesession.createplayer();
	}

	public void setReqPlayer(String playerid, int dx, int dy) {

		GameSession gamesession = server.getGameSession();
		if (gamesession == null)
			return;
		Player p = server.getPlayer(playerid);
		if (p == null)
			return;
		gamesession.setreqplayer(p, dx, dy);

	}

	@Override
	public void gameEnd(String playerid) throws RemoteException {

		GameSession gamesession = server.getGameSession();
		if (gamesession == null)
			return;
		Player p = server.getPlayer(playerid);
		if (p == null)
			return;
		gamesession.gameend(p);
	}

}
