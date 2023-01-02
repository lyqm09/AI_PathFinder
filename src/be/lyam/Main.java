package be.lyam;

import be.lyam.ui.FXController;

public class Main {
	
	private final static double VERSION = 1.1;
	
	public static void main(String[] args) {
		System.out.println("AI Portal " + VERSION + " charged with Java " + System.getProperty("java.version"));
		
		FXController.main(args);
	/*	
		Game game = new Game(new AIPlayer(.5,.5,true));
		System.out.println("game start");
		while(!game.isOver()) {
			
			game.getPlayer().update(game);
			
		}
		System.out.println("game over");
	*/
	}



}
