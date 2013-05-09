package pacman;

import java.io.Serializable;

public class Player extends Object implements Serializable {
	
	public static final long serialVersionUID = -3868548386090975594L;
	
	public String id;
	public int pacmanx, pacmany, pacmandx, pacmandy;
	public int reqdx, reqdy, viewdx, viewdy;
	public boolean dying, ingame, waiting;
	public int score, pacsleft;

	public Player(String pacmanid){
		
		id = pacmanid;
		pacsleft = 0;
		score = 0;
		ingame = false;
		waiting = false;
		
		reset();
	}

	/*Ver qe pasa cuando se acaban las vidas del pacman*/
	
	public void reset(){
		
		pacmanx = 7 * GameSession.blocksize;
		pacmany = 11 * GameSession.blocksize;
		pacmandx = 0;
		pacmandy = 0;
		reqdx = 0;
		reqdy = 0;
		viewdx = -1;
		viewdy = 0;
		dying = false;
		
		
	}
}
