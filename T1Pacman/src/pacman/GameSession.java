package pacman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import javax.swing.Timer;

public class GameSession implements ActionListener, Serializable {

	private static final long serialVersionUID = 2615372360707852353L;

	final short leveldata[] = { 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18,
			18, 18, 22, 21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16,
			20, 21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 21, 0,
			0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20, 17, 18, 18, 18,
			16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20, 17, 16, 16, 16, 16, 16,
			20, 0, 17, 16, 16, 16, 16, 24, 20, 25, 16, 16, 16, 24, 24, 28, 0,
			25, 24, 24, 16, 20, 0, 21, 1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17,
			20, 0, 21, 1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
			1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21, 1, 17, 16,
			16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16,
			16, 16, 18, 16, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 20, 0, 21, 1, 25, 24, 24, 24, 24, 24, 24, 24,
			24, 16, 16, 16, 18, 20, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24,
			24, 28 };

	final static int blocksize = 24;
	final static int nrofblocks = 15;
	final static int scrsize = nrofblocks * blocksize;

	short[] screendata;
	Timer timer;

	public int nrofghosts = 6;
	final int maxghosts = 12;
	final int validspeeds[] = { 1, 2, 3, 4, 6, 8 };
	final int maxspeed = 6;
	public int[] dx, dy;
	public int[] ghostx, ghosty, ghostdx, ghostdy, ghostspeed;
	private int currentspeed;
	final int pacmanspeed = 6;

	public HashMap<String, Player> players = new HashMap<String, Player>();

	private boolean verbose = true;
	private int minplayers = 0;
	
	public GameSession(int _minplayers, boolean _verbose) {

		verbose = _verbose;
		minplayers = _minplayers;
		
		screendata = new short[nrofblocks * nrofblocks];

		dx = new int[4];
		dy = new int[4];
		ghostx = new int[maxghosts];
		ghostdx = new int[maxghosts];
		ghosty = new int[maxghosts];
		ghostdy = new int[maxghosts];
		ghostspeed = new int[maxghosts];
		nrofghosts = 6;
		currentspeed = 3;

		/* Se inicia el timer */
		timer = new Timer(40, this);
		
		/* Se inicializa el screendata */
		init();

		debug("Jugadores conectados: " + players.size() + "\n");

	}
	
	public void init(){
		short i;
		for (i = 0; i < nrofblocks * nrofblocks; i++)
			screendata[i] = leveldata[i];
	}

	public String createplayer() {

		String id = UUID.randomUUID().toString();
		Player p = new Player(id);
		players.put(id, p);

		debug("Se crea el jugador con id: " + p.id + "\n");

		return id;

	}

	public void PlayGame() {

		debug(".");
		
		checkdeaths();
		moveghosts();
		movepacmans();
		checkmaze();

	}

	public void checkdeaths() {
		for (Player p : players.values()) {
			if (p.dying && p.ingame) {
				p.pacsleft--;
				debug("El jugador " + p.id + " pierde una vida, le quedan " + p.pacsleft + " vidas.\n");
				
				if (p.pacsleft == 0) {
					p.ingame = false;
					debug("El jugador " + p.id + " se retira del juego, quedan " + ingameplayers() + " jugando.\n");
				}
				LevelContinue();
			}
		}
	}

	public void movepacmans() {
		for (Player p : players.values()) {
			if(p.ingame)
				movepacman(p);
		}
	}

	public void GameInit(Player p) {
		
		debug("El jugador " + p.id + " ingresa al juego.\n");

		p.pacsleft = 3;
		p.score = 0;
		
		if(timer.isRunning()){
			p.ingame = true;
			p.waiting = false;
		}else{
			p.ingame = false;
			p.waiting = true;
		}		
		
		if(waitingplayers() >= minplayers){
			
			if(!timer.isRunning()){
				timer.start();
				LevelInit();
			}
			for (Player q : players.values()) {
				if(q.waiting){
					q.ingame = true;
					q.waiting = false;
				}
			}
			
		}
	}

	public void debug(String msg){
		if(verbose)
			System.out.print(msg);
	}
	
	public void LevelInit() {
		int i;
		for (i = 0; i < nrofblocks * nrofblocks; i++)
			screendata[i] = leveldata[i];

		LevelContinue();
	}

	public void LevelContinue() {
		short i;
		int dx = 1;
		int random;

		for (i = 0; i < nrofghosts; i++) {
			ghosty[i] = 4 * blocksize;
			ghostx[i] = 4 * blocksize;
			ghostdy[i] = 0;
			ghostdx[i] = dx;
			dx = -dx;
			random = (int) (Math.random() * (currentspeed + 1));
			if (random > currentspeed)
				random = currentspeed;
			ghostspeed[i] = validspeeds[random];
		}

		/* Recorremos todos los jugadores y seteamos los valores por defecto */

		for (Player p : players.values()) {
			p.reset();
		}

	}

	public void gameend(Player p) {
		p.ingame = false;
		p.waiting = false;
		
		if(ingameplayers() == 0 && timer.isRunning()){
			timer.stop();
		}

		debug("El jugador " + p.id + " se retira del juego, quedan " + ingameplayers() + " jugando.\n");
	}

	public void setreqplayer(Player p, int dx, int dy) {
		p.reqdx = dx;
		p.reqdy = dy;
	}

	public int ingameplayers(){
		
		int ingameplayers = 0;
		for (Player p : players.values()) {
			if(p.ingame) ingameplayers++;
		}
		
		return ingameplayers;
		
	}
	
	public int waitingplayers(){
		
		int waitingplayers = 0;
		for (Player p : players.values()) {
			if(p.waiting) waitingplayers++;
		}
		
		return waitingplayers;
		
	}	
	
	public void moveghosts() {

		short i;
		int pos;
		int count;

		for (i = 0; i < nrofghosts; i++) {
			if (ghostx[i] % blocksize == 0 && ghosty[i] % blocksize == 0) {
				pos = ghostx[i] / blocksize + nrofblocks
						* (int) (ghosty[i] / blocksize);

				count = 0;
				if ((screendata[pos] & 1) == 0 && ghostdx[i] != 1) {
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}
				if ((screendata[pos] & 2) == 0 && ghostdy[i] != 1) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}
				if ((screendata[pos] & 4) == 0 && ghostdx[i] != -1) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}
				if ((screendata[pos] & 8) == 0 && ghostdy[i] != -1) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}

				if (count == 0) {
					if ((screendata[pos] & 15) == 15) {
						ghostdx[i] = 0;
						ghostdy[i] = 0;
					} else {
						ghostdx[i] = -ghostdx[i];
						ghostdy[i] = -ghostdy[i];
					}
				} else {
					count = (int) (Math.random() * count);
					if (count > 3)
						count = 3;
					ghostdx[i] = dx[count];
					ghostdy[i] = dy[count];
				}

			}
			ghostx[i] = ghostx[i] + (ghostdx[i] * ghostspeed[i]);
			ghosty[i] = ghosty[i] + (ghostdy[i] * ghostspeed[i]);

			/* Recorremos las lista de jugadores y revisamos si mueren */

			for (Player p : players.values()) {

				if (p.ingame) {

					if (p.pacmanx > (ghostx[i] - 12)
							&& p.pacmanx < (ghostx[i] + 12)
							&& p.pacmany > (ghosty[i] - 12)
							&& p.pacmany < (ghosty[i] + 12)) {
						p.dying = true;

						debug("El jugador " + p.id + " muere.\n");

					}
				}
			}

		}
	}

	public void checkmaze() {

		short i = 0;
		boolean finished = true;

		while (i < nrofblocks * nrofblocks && finished) {
			if ((screendata[i] & 48) != 0)
				finished = false;
			i++;
		}

		if (finished) {

			/* Recorremos los jugadores y actualizamos el puntaje */
			for (Player p : players.values()) {
				p.score += 50;
			}

			if (nrofghosts < maxghosts)
				nrofghosts++;
			if (currentspeed < maxspeed)
				currentspeed++;

			LevelInit();
		}
	}

	public void movepacman(Player p) {

		int pos;
		short ch;

		if (p.reqdx == -p.pacmandx && p.reqdy == -p.pacmandy) {
			p.pacmandx = p.reqdx;
			p.pacmandy = p.reqdy;
			p.viewdx = p.pacmandx;
			p.viewdy = p.pacmandy;
		}
		if (p.pacmanx % blocksize == 0 && p.pacmany % blocksize == 0) {
			pos = p.pacmanx / blocksize + nrofblocks
					* (int) (p.pacmany / blocksize);
			ch = screendata[pos];

			if ((ch & 16) != 0) {
				screendata[pos] = (short) (ch & 15);
				p.score++;
			}

			if (p.reqdx != 0 || p.reqdy != 0) {
				if (!((p.reqdx == -1 && p.reqdy == 0 && (ch & 1) != 0)
						|| (p.reqdx == 1 && p.reqdy == 0 && (ch & 4) != 0)
						|| (p.reqdx == 0 && p.reqdy == -1 && (ch & 2) != 0) || (p.reqdx == 0
						&& p.reqdy == 1 && (ch & 8) != 0))) {
					p.pacmandx = p.reqdx;
					p.pacmandy = p.reqdy;
					p.viewdx = p.pacmandx;
					p.viewdy = p.pacmandy;
				}
			}

			// Check for standstill
			if ((p.pacmandx == -1 && p.pacmandy == 0 && (ch & 1) != 0)
					|| (p.pacmandx == 1 && p.pacmandy == 0 && (ch & 4) != 0)
					|| (p.pacmandx == 0 && p.pacmandy == -1 && (ch & 2) != 0)
					|| (p.pacmandx == 0 && p.pacmandy == 1 && (ch & 8) != 0)) {
				p.pacmandx = 0;
				p.pacmandy = 0;
			}
		}
		p.pacmanx = p.pacmanx + pacmanspeed * p.pacmandx;
		p.pacmany = p.pacmany + pacmanspeed * p.pacmandy;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PlayGame();
	}

}
