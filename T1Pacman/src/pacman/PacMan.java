package pacman;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JFrame;

import pacman.Board;

public class PacMan extends JFrame {

	private static final long serialVersionUID = -6266532536717450429L;

	private Board board;
	private static String serverip = null;
	private static boolean verbose = false;

	public PacMan(String serverip, boolean verbose) {
		try {

			board = new Board(serverip, verbose);

			add(board);
			setTitle("Pacman");
			setSize(380, 420);
			setLocationRelativeTo(null);
			setVisible(true);

			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					try {
						board.GameEnd();
						System.exit(0);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	public static void parseargs(String[] args){
		
		Vector<String> v_serverip = new Vector<String>();
		Vector<String> v_verbose = new Vector<String>();
		
		Vector<String> aux = null;
		
		int i = 0;
		int length = args.length;

		try{
		
			while (i < length) {
				
				switch (args[i]) {
				case "-server":
					aux = v_serverip;
					break;
					
				case "-v":
					aux = v_verbose;
					aux.add("true");
					break;
					
					
				default:
					if(aux != null)
						aux.add(args[i]);
					break;
				}
				i++;
			}
			
			if(!v_serverip.isEmpty()){
				serverip = v_serverip.firstElement();
			}else{
				usage();
			}
	
			if(!v_verbose.isEmpty()){
				verbose = new Boolean(v_verbose.firstElement()).booleanValue();
			}
	
		}catch(Exception e){
			usage();
		}
		
	}	
	
	public static void usage(){
		System.out.println("Modo de empleo: java pacman.PacMan [OPCION]...");
		System.out.println("Las opciones son las que siguen:");
		System.out.println("\t -server \t Establece la ip del servidor que esta correindo el juego.");
		System.out.println("\t -v \t Activa el modo de depuracion.");
		System.exit(0);
	}	

	public static void main(String[] args) {
		
		parseargs(args);
		new PacMan(serverip, verbose);
	}
}
