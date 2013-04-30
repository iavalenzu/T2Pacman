package pacman;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.JFrame;

import pacman.Board;

public class PacMan extends JFrame {

	private static final long serialVersionUID = -6266532536717450429L;

	private Board board;

	public PacMan() {
		try {

			board = new Board();

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

	public static void main(String[] args) {
		new PacMan();
	}
}
