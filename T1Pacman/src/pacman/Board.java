package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8839500830199434028L;
	Dimension d;
	Font smallfont = new Font("Helvetica", Font.BOLD, 14);

	FontMetrics fmsmall, fmlarge;
	Image ii;
	Color dotcolor = new Color(192, 192, 0);
	Color mazecolor;

	final int pacanimdelay = 2;
	final int pacmananimcount = 4;

	int pacanimcount = pacanimdelay;
	int pacanimdir = 1;
	int pacmananimpos = 0;

	Image ghost;
	Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
	Image pacman3up, pacman3down, pacman3left, pacman3right;
	Image pacman4up, pacman4down, pacman4left, pacman4right;

	int reqdx, reqdy;

	Timer timer;

	private Iface skeleton;
	private GameSession currentgamesession;
	private Player currentplayer;
	private String playerid;

	private boolean verbose;
	
	public Board(String serverip, boolean _verbose) {

		verbose = _verbose;
		
		try {
			
			Logger.debug("Server hostname: " + serverip, "Server", verbose);
			
			skeleton = (Iface) Naming.lookup("rmi://" + serverip + ":1099/Iface1");

			/* Dado que el juego puede migrar de un servidor a otro, es necesario saber que servidor esta corriendo actualmente el juego,
			 * es por esta razon que se debe preguntar al servidor al cual me conecté, si esta corriendo el juego o si uno de sus compañeros
			 * lo esta haciendo, para luego conectarme a él. 
			 */
			
			String activeserver = skeleton.getActiveGameServer();
			if(activeserver != null){
				skeleton = (Iface) Naming.lookup("rmi://" + activeserver + ":1099/Iface1");
				Logger.debug("El juego esta actualmente en el servidor '" + activeserver + "'", "Server", verbose);
			}else{
				Logger.debug("El juego no esta disponible en los servidores", "Server", verbose);
				System.exit(0);
			}
			
			playerid = skeleton.createPlayer();

		} catch (NotBoundException e) {
			Logger.error("El servicio no esta publicado en el servidor.");
			System.exit(128);
		} catch (MalformedURLException e) {
			Logger.error("URL invalida.");
			System.exit(128);
		} catch (RemoteException e) {
			e.printStackTrace();
			Logger.error("Excepcion remota tratando de conectarse al servidor.");
			System.exit(128);
		}

		GetImages();

		addKeyListener(new TAdapter());

		mazecolor = new Color(5, 100, 5);
		setFocusable(true);

		d = new Dimension(400, 400);

		setBackground(Color.black);
		setDoubleBuffered(true);

		timer = new Timer(40, this);
		timer.start();
	}

	public void addNotify() {
		super.addNotify();
	}

	public void DoAnim() {
		pacanimcount--;
		if (pacanimcount <= 0) {
			pacanimcount = pacanimdelay;
			pacmananimpos = pacmananimpos + pacanimdir;
			if (pacmananimpos == (pacmananimcount - 1) || pacmananimpos == 0)
				pacanimdir = -pacanimdir;
		}
	}

	public void ShowIntroScreen(Graphics2D g2d) {

		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, GameSession.scrsize / 2 - 30,
				GameSession.scrsize - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, GameSession.scrsize / 2 - 30,
				GameSession.scrsize - 100, 50);

		String s = "Press s to start.";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = this.getFontMetrics(small);

		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(s, (GameSession.scrsize - metr.stringWidth(s)) / 2,
				GameSession.scrsize / 2);
	}

	public void ShowWaitingScreen(Graphics2D g2d) {

		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, GameSession.scrsize / 2 - 30,
				GameSession.scrsize - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, GameSession.scrsize / 2 - 30,
				GameSession.scrsize - 100, 50);

		String s = "Esperando jugadores.";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = this.getFontMetrics(small);

		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(s, (GameSession.scrsize - metr.stringWidth(s)) / 2,
				GameSession.scrsize / 2);
	}	
	
	public void DrawScore(Graphics2D g) {
		int i;
		String s;

		g.setFont(smallfont);
		g.setColor(new Color(96, 128, 255));
		s = "Score: " + currentplayer.score;
		g.drawString(s, GameSession.scrsize / 2 + 96, GameSession.scrsize + 16);
		for (i = 0; i < currentplayer.pacsleft; i++) {
			g.drawImage(pacman3left, i * 28 + 8, GameSession.scrsize + 1, this);
		}
	}

	public void DrawGhosts(Graphics2D g2d) throws RemoteException {
		short i;
		for (i = 0; i < currentgamesession.getNrofghosts(); i++) {
			drawGhost(g2d, currentgamesession.getGhostX(i) + 1,
					currentgamesession.getGhostY(i) + 1);
		}
	}

	public void drawGhost(Graphics2D g2d, int x, int y) {
		g2d.drawImage(ghost, x, y, this);
	}

	public void DrawPacMans(Graphics2D g2d) throws RemoteException {
		for (Player p : currentgamesession.getPlayers().values()) {
			if(p.ingame){
				DrawPacMan(g2d, p);
			}
		}
	}

	public void DrawPacMan(Graphics2D g2d, Player p) {
		if (p.viewdx == -1)
			DrawPacManLeft(g2d, p);
		else if (p.viewdx == 1)
			DrawPacManRight(g2d, p);
		else if (p.viewdy == -1)
			DrawPacManUp(g2d, p);
		else
			DrawPacManDown(g2d, p);
	}

	public void DrawPacManUp(Graphics2D g2d, Player p) {
		switch (pacmananimpos) {
		case 1:
			g2d.drawImage(pacman2up, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3up, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4up, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		}
	}

	public void DrawPacManDown(Graphics2D g2d, Player p) {
		switch (pacmananimpos) {
		case 1:
			g2d.drawImage(pacman2down, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3down, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4down, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		}
	}

	public void DrawPacManLeft(Graphics2D g2d, Player p) {
		switch (pacmananimpos) {
		case 1:
			g2d.drawImage(pacman2left, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3left, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4left, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		}
	}

	public void DrawPacManRight(Graphics2D g2d, Player p) {
		switch (pacmananimpos) {
		case 1:
			g2d.drawImage(pacman2right, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3right, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4right, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, p.pacmanx + 1, p.pacmany + 1, this);
			break;
		}
	}

	public void DrawMaze(Graphics2D g2d) {

		int scrsize = GameSession.nrofblocks * GameSession.blocksize;

		short i = 0;
		int x, y;

		for (y = 0; y < scrsize; y += GameSession.blocksize) {
			for (x = 0; x < scrsize; x += GameSession.blocksize) {

				g2d.setColor(mazecolor);
				g2d.setStroke(new BasicStroke(2));

				if ((currentgamesession.getScreendata(i) & 1) != 0) // draws left
				{
					g2d.drawLine(x, y, x, y + GameSession.blocksize - 1);
				}
				if ((currentgamesession.getScreendata(i) & 2) != 0) // draws top
				{
					g2d.drawLine(x, y, x + GameSession.blocksize - 1, y);
				}
				if ((currentgamesession.getScreendata(i) & 4) != 0) // draws right
				{
					g2d.drawLine(x + GameSession.blocksize - 1, y, x
							+ GameSession.blocksize - 1, y
							+ GameSession.blocksize - 1);
				}
				if ((currentgamesession.getScreendata(i) & 8) != 0) // draws bottom
				{
					g2d.drawLine(x, y + GameSession.blocksize - 1, x
							+ GameSession.blocksize - 1, y
							+ GameSession.blocksize - 1);
				}
				if ((currentgamesession.getScreendata(i) & 16) != 0) // draws point
				{
					g2d.setColor(dotcolor);
					g2d.fillRect(x + 11, y + 11, 2, 2);
				}
				i++;
			}
		}
	}

	public void GameInit() throws RemoteException {
		skeleton.gameInit(playerid);
	}

	public void GameEnd() throws RemoteException{
		skeleton.gameEnd(playerid);
	}
	
	public void GetImages() {

		ghost = new ImageIcon(Board.class.getResource("./ghost.gif"))
				.getImage();
		pacman1 = new ImageIcon(Board.class.getResource("./pacman.gif"))
				.getImage();
		pacman2up = new ImageIcon(Board.class.getResource("./up1.gif"))
				.getImage();
		pacman3up = new ImageIcon(Board.class.getResource("./up2.gif"))
				.getImage();
		pacman4up = new ImageIcon(Board.class.getResource("./up3.gif"))
				.getImage();
		pacman2down = new ImageIcon(Board.class.getResource("./down1.gif"))
				.getImage();
		pacman3down = new ImageIcon(Board.class.getResource("./down2.gif"))
				.getImage();
		pacman4down = new ImageIcon(Board.class.getResource("./down3.gif"))
				.getImage();
		pacman2left = new ImageIcon(Board.class.getResource("./left1.gif"))
				.getImage();
		pacman3left = new ImageIcon(Board.class.getResource("./left2.gif"))
				.getImage();
		pacman4left = new ImageIcon(Board.class.getResource("./left3.gif"))
				.getImage();
		pacman2right = new ImageIcon(Board.class.getResource("./right1.gif"))
				.getImage();
		pacman3right = new ImageIcon(Board.class.getResource("./right2.gif"))
				.getImage();
		pacman4right = new ImageIcon(Board.class.getResource("./right3.gif"))
				.getImage();

	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);

		try {

			/* Preguntamos al servidor si se definió un hostname al cual migrar, si es asi
			 * nos conectamos a el y modificamos la referencia de skeleton, para que la consultas se 
			 * ejecuten al nuevo servidor
			 */

			String hostname = skeleton.getMigrationHostname();
			
			if(hostname != null){
				skeleton = (Iface) Naming.lookup("rmi://" + hostname + ":1099/Iface1");
				Logger.debug("El juego migró al servidor '" + hostname + "'", "Server", verbose);
			}

			
			/* Se obtiene la sesion del juego */
			currentgamesession = skeleton.getGameSession();
			currentplayer = currentgamesession.getPlayer(playerid);

			DrawMaze(g2d);
			DrawScore(g2d);
			DoAnim();

			if (currentplayer.ingame){
				DrawPacMans(g2d);
				DrawGhosts(g2d);
			}else if(currentplayer.waiting){
				ShowWaitingScreen(g2d);
			}else
				ShowIntroScreen(g2d);

		} catch (Exception e2) {
			e2.printStackTrace();
		}

		g.drawImage(ii, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			try {

				int key = e.getKeyCode();

				if (currentplayer.ingame) {
					if (key == KeyEvent.VK_LEFT) {
						reqdx = -1;
						reqdy = 0;
						skeleton.setReqPlayer(playerid, reqdx, reqdy);

					} else if (key == KeyEvent.VK_RIGHT) {
						reqdx = 1;
						reqdy = 0;
						skeleton.setReqPlayer(playerid, reqdx, reqdy);

					} else if (key == KeyEvent.VK_UP) {
						reqdx = 0;
						reqdy = -1;
						skeleton.setReqPlayer(playerid, reqdx, reqdy);

					} else if (key == KeyEvent.VK_DOWN) {
						reqdx = 0;
						reqdy = 1;
						skeleton.setReqPlayer(playerid, reqdx, reqdy);

					} else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
						GameEnd();
					} else if (key == KeyEvent.VK_PAUSE) {
						if (timer.isRunning())
							timer.stop();
						else
							timer.start();
					}
				} else {
					if (key == 's' || key == 'S') {
						GameInit();
					}
				}

			} catch (RemoteException e1) {
				e1.printStackTrace();
			}

		}

		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();

			try {

				if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP
						|| key == Event.DOWN) {
					reqdx = 0;
					reqdy = 0;
					skeleton.setReqPlayer(playerid, reqdx, reqdy);
				}

			} catch (RemoteException e1) {
				e1.printStackTrace();
			}

		}
	}

	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
