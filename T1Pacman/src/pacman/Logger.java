package pacman;

import java.util.Date;

public class Logger {
	
	static public void debug(String msg, String tag, boolean verbose){
		Date now = new Date();
		print("\n" + "[" + now.toString() + "] " + tag + ": " + msg + "\t", verbose);
	}	
	static public void debug(String msg, String tag){
		debug(msg, tag, true);
	}
	static public void error(String msg){
		debug(msg, "Error", true);
	}
	
	static public void print(String msg, boolean verbose){
		if(verbose)
			System.out.print(msg);
	}
	
	static public void println(String msg, boolean verbose){
		if(verbose)
			System.out.println(msg);
	}
	
	

}
