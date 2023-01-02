package be.lyam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AIPlayer {
//	Q(s,a) = (1-lr).Q(s,a)+lr.(R+ls.max(a').Q(s',a'))
	
	public Location[] positions;
	
	private Map<LocationActions, Double> squares;
	private final static int ACTIONS = 6;
	
	private final double learningStep; 
	private final double learningRate;
	
	private double exploration;
	private double explorationD = .00005;
	private double explorationLimit = .01;
	
	private Random random = new Random();
	
	private Game game;
	
	public AIPlayer() {
		this(.5,.5, true, 10);
	}
	public AIPlayer(double learningStep, double learningRate, boolean exploration, int maxPlayers) {
		this.learningRate = learningRate;
		this.learningStep = learningStep;
		
		this.positions = new Location[maxPlayers];
		
		this.squares = new HashMap<>();
		
		for(int z = 0; z < Board.SIZE; z++) {
			for(int y = 0; y < Board.SIZE; y++) {
				for(int x = 0; x < Board.SIZE; x++) {
					for(int a = 0; a < ACTIONS; a++) {
						this.squares.put(new LocationActions(x,y,z,a), (double)0);
					}
				}	
			}
		}
		
		this.exploration = exploration ? 1 : 0;
		
	}
	
	public boolean update(int player, Game game) {
		this.game = game;
		int action;
		boolean exploration;
		
		if(this.positions[player] == null)
			this.setLocation(player, this.game.getBoard().getSpawn());
		
		if(exploration = this.random.nextDouble() < this.exploration) {
			action = this.random.nextInt(ACTIONS);
		} else {
			action = this.getMaxIndex(this.getActions(this.positions[player]));
		}
		
		if(this.exploration > this.explorationLimit) {
			this.exploration-=this.explorationD;
		}
	
		return game.move(player, action, exploration);		
	}
	
	public void backPropagation(int player, int action, double reward, Location newLocation, boolean notModified) {
		double qValue = this.getValue(this.positions[player], action);
		double target = notModified ? reward : reward+this.learningStep*this.getMaxValue(this.getActions(newLocation));
		
		double error = target-qValue;
		double learning = this.learningRate*error;
		
		this.game.addLoss(error);
		this.game.addLearning(learning);
		
		//	this.setValue(this.positions[player], action, (1-this.learningRate)*qValue+learning);
		this.setValue(this.positions[player], action, (qValue+learning));
		this.setLocation(player, newLocation);
	}
	
	private boolean setValue(Location location, int action, double value) {
		if(!Board.isValideLocation(location))
			return false;
		LocationActions l = this.findSquare(new LocationActions(location, action));
		if(!this.containsKey(l))
			return false;
		
		
		this.squares.replace(l, this.get(l), value);
		return true;
	}
	
	private double getValue(Location location, int action) {
		if(!Board.isValideLocation(location))
			return 0;
		
		LocationActions l = this.findSquare(new LocationActions(location, action));
		return this.containsKey(l) ? this.squares.get(l) : 0;
	}
	
	private List<Double> getActions(Location location) {
		List<Double> actions = new ArrayList<>();
		for(int i = 0; i < ACTIONS; i++) {
			actions.add(this.get(new LocationActions(location, i)));
		}
		return actions;
	}
	
	private int getMaxIndex(List<Double> actions) {
		List<Integer> m = new ArrayList<>();
		m.add(0);
		for(int i = 1; i < actions.size(); i++) {
		//	System.out.println(((double)actions.get(i))+" - "+((double)actions.get(m.get(0))));
			if (((double)actions.get(i)) == ((double)actions.get(m.get(0)))) {
			//	System.out.println((double)actions.get(m.get(0))+" add "+(double)actions.get(i));
				m.add(i);
			} else if (((double)actions.get(i)) > ((double)actions.get(m.get(0)))) {
			//	System.out.println((double)actions.get(m.get(0))+" took "+(double)actions.get(i));
				m.clear();
				m.add(i);
			}
		}
		int i = this.random.nextInt(m.size());
		return m.get(i);
	}
	
	private double getMaxValue(List<Double> actions) {
		double m = Double.NEGATIVE_INFINITY;
		if(actions.size() != 6) {
			System.err.println("il y a une erreur a getMaxValue car il n'y a pas assez de valeurs "+actions.size());
			game.toggleOver();
		}
		for(int i = 0; i < actions.size(); i++) {
			m = Math.max(m, actions.get(i));
		}
		return m;
	}
	
	private double get(LocationActions var1) {
		if(!Board.isValideLocation(var1.location))
			return 0;
		
		return this.containsKey(var1) ? this.squares.get(this.findSquare(var1)) : 0;
	}
	
	private boolean containsKey(Object obj) {
		if(!(obj instanceof LocationActions))
			return false;
		LocationActions l = (LocationActions) obj;
		return this.squares.containsKey(this.findSquare(l));
	}
	
	private LocationActions findSquare(LocationActions locationActions) {
		for(LocationActions l : this.squares.keySet()) {
			if(l.equals(locationActions)) {
				return l;
			}
		}
		
		return locationActions;
	}
	
	public Location getLocation(int player) {
		return this.positions[player];
	}
	public List<Location> getLocations() {
		List<Location> l = new ArrayList<>();
		for(int p = 0; p < positions.length; p++) {
			l.add(positions[p]);
		}
		return l;
	}
	public void setLocation(int player, Location location) {
		this.positions[player] = location;
	}
	
	public double getLearningRate() {
		return this.learningRate;
	}
	public double getLearningStep() {
		return this.learningStep;
	}
	
	public double getExploration() {
		return this.exploration;
	}
	
	private class LocationActions {
		
		public Location location;
		public int action;
		
		public LocationActions(int x, int y, int z, int action) {
			this(new Location(x,y,z),action);
		}
		public LocationActions(Location location, int action) {
			this.location = location;
			this.action = action;
		}
		
		public String toString() {
			return ("(x="+location.x+";y="+location.y+";z="+location.z+";a="+action+")");
		}
		
		public boolean equals(Object var) {
			if(!(var instanceof LocationActions)) {
				return false;
			}
			LocationActions l = (LocationActions) var;
			return (l.location.x == this.location.x && l.location.y == this.location.y && l.location.z == this.location.z && l.action == this.action); 
		}
		
	}

	
}
