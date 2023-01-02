package be.lyam;

import be.lyam.ui.Statistiques;
import javafx.application.Platform;

public class Game {

	private boolean consolePrinter; 
	
	private int iterator;
	private int explor;
	private int shots;
	private int minShots;
	
	private int succedMinShots;
	private final static int SUCCES = 50;
	private boolean isFinish;
	
	private Statistiques stats;
	private Board board;
	private AIPlayer ai;
	private int players;
	
	private double loss;
	private double learning;
	
	
	public Game(AIPlayer player, int players) {
		this.consolePrinter = true;
		this.ai = player;
		this.players = players;
		this.stats = new Statistiques(this);
		this.reset();
	}
	
	public void reset() {
		
		if(this.board == null) {
			this.board = new Board(this.ai, this.players);
		} else {
			this.board.reset();
		}
		
		this.iterator = 1;
		this.explor = 0;
		this.shots = 0;
		this.minShots = 9*players;//((Math.abs(this.board.getSpawn().x-this.board.getFlag().x))+(Math.abs(this.board.getSpawn().y-this.board.getFlag().y))+(Math.abs(this.board.getSpawn().z-this.board.getFlag().z))*players);
		
		this.succedMinShots = 0;
		this.isFinish = false;
	}
	
	private Location moves(int player, int i) {
		Location loc = ai.getLocation(player);
		switch(i) {
		case 0 :
			loc = new Location((loc.x+1), loc.y, loc.z);
			break;
		case 1 :
			loc = new Location((loc.x-1), loc.y, loc.z);
			break;
		case 2 :
			loc = new Location(loc.x, (loc.y+1), loc.z);
			break;
		case 3 :
			loc = new Location(loc.x, (loc.y-1), loc.z);
			break;
		case 4 :
			loc = new Location(loc.x, loc.y, (loc.z+1));
			break;
		case 5 :
			loc = new Location(loc.x, loc.y, (loc.z-1));
			break;
		default :
			break;	
		}
		
		return loc;	
	}
	
	public boolean move(int player, int action, boolean exploration) {
		Location nextLocation = this.moves(player, action);
		double reward;
		boolean restart = false;
		boolean notModified = false;
		
		if(!Board.isValideLocation(nextLocation)) {
			reward = -100;
			nextLocation = ai.getLocation(player);
			notModified = true;
		} else {
			if(this.board.hasPortals(nextLocation))
				nextLocation = this.board.getPortal(nextLocation).goTo(nextLocation);
			
			reward = -1;
			notModified = restart = nextLocation.equals(board.getFlag());
			if(notModified) {
				reward = 100;
				if(consolePrinter) {
					System.out.println("--------");
					System.out.println("it took the flag");
					System.out.println(this.shots+" shots");
					System.out.println(this.iterator+" games");
				}
				int s = this.shots;
				int e = this.explor;
				int i = this.iterator;
				Platform.runLater(() -> {
					this.stats.update(s, e, i);
				});
					
			}
		}
		
		this.ai.backPropagation(player, action, reward, nextLocation, notModified);
		
		if(this.board.hasPortals(ai.getLocation(player))) {
			this.board.set(ai.getLocation(player), Piece.PORTAL);
		} else {
			this.board.set(ai.getLocation(player), Piece.EMPTY);
		}
		this.board.set(nextLocation, Piece.PLAYER);
		
		
		this.shots +=1;
		if(exploration) {
			this.explor +=1;
		}
		
		if(restart) {
			this.succedMinShots = (this.shots <= this.minShots) ? this.succedMinShots+1 : 0;
			this.isFinish = this.succedMinShots >= SUCCES; //hier
			this.board.reset();
			this.iterator +=1;
			this.explor = 0;
			this.shots = 0;
			return true;
		}
		return false;
	}
	
	public void setConsolePrinter(boolean b) {
		this.consolePrinter = b;
	}
	
	public int getIterator() {
		return this.iterator;
	}
	
	public int getExplor() {
		return this.explor;
	}
	
	public int getShots() {
		return this.shots;
	}
	public int getMinShots() {
		return this.minShots;
	}
	
	public int getErrors() {
		return Math.max(0, this.shots - this.minShots);
	}
	
	public int getSuccesMinShots() {
		return this.succedMinShots;
	}
	
	public void addLoss(double i) {
		this.loss +=i;
	}
	public double getLoss() {
		return this.loss;
	}
	
	public void addLearning(double i) {
		this.learning +=i;
	}
	public double getLearning() {
		return this.learning;
	}
	
	public void addPlayers(int i) {
		this.players = ((this.players+i)<=10 ? this.players+i: this.players);
		this.board.setPlayers(this.players);
		for(int j = this.players; j < (this.players-i); j++) {
			ai.setLocation(j, this.board.getSpawn());
		}
		System.out.println("players : "+this.players);
		//set minshots
		this.minShots = 9*players;
	}
	public void removePlayers(int i) {
		this.players = ((this.players-i)>0 ? this.players-i: this.players);
		this.board.setPlayers(this.players);
		for(int j = this.players; j < (this.players+i); j++) {
			ai.setLocation(j, null);
		}
		System.out.println("player : "+this.players);
		//set minshots
		this.minShots = 9*players;
	}
	
	public boolean isOver() {
		return this.isFinish;
	}
	public void toggleOver() {
		if(this.isFinish)
			this.isFinish = false;
		if(!this.isFinish)
			this.isFinish = true;
	}
	public void setOver(boolean b) {
		this.isFinish = b;
	}
	
	public AIPlayer getPlayer() {
		return this.ai;
	}

	public Board getBoard() {
		return this.board;
	}

	public int getNPlayers() {
		return this.players;
	}
	
	public Statistiques getStats() {
		return this.stats;
	}
	
}
