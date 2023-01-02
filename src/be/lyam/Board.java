package be.lyam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
	
	public static final int SIZE = 10;  
	
	private Map<Location, Piece> board = new HashMap<>();
	private List<Portals> portals = new ArrayList<>();
	private Location flag;
	private Location spawn;
	private final AIPlayer player;
	private int players;
	
	Board(Location flag, Location spawn, AIPlayer player, int players) {
		this.flag = flag;
		this.spawn = spawn;
		this.player = player;
		this.players = players;
		
		this.reset();
	}
	Board(AIPlayer player, int players) {
		this(new Location(1,1,1), new Location(9,9,9), player, players);
	}
	
	public void reset() {
		
		// area
		for(int z = 0; z < SIZE; z++) {
			for(int y = 0; y < SIZE; y++) {
				for(int x = 0; x < SIZE; x++) {
					this.board.put(new Location(x,y,z), Piece.EMPTY);
				}
			}
		}
		
		// flag
		if((!isValideLocation(this.flag)) || this.flag == null) {
			this.flag = new Location(5,5,9);
		}
		this.set(this.flag, Piece.FLAG);
		
		// spawn
		if((!isValideLocation(this.spawn)) || this.spawn == null) {
			this.spawn = new Location(9,9,9);
		}
		this.set(this.spawn, Piece.PLAYER);
		
		// player
		for(int i = 0; i < this.players; i++) {
			this.player.setLocation(i,this.spawn);
		}
		
		
		//portals
		Location first = new Location(3,3,3);
		Location second = new Location(8,8,8);
		this.addPortals(first, second);
	}
	
	public boolean set(Location location, Piece piece) {
		if(!isValideLocation(location))
			return false;
		if(!this.containsKey(location))
			return false;
		if(piece == null)
			return false;
		
		this.board.replace(this.findSquare(location), this.get(location), piece);
		return true;
	}
	
	public Piece get(Location location) {
		if(!isValideLocation(location))
			return Piece.INVALID;
		
		return this.containsKey(location) ? this.board.get(this.findSquare(location)): Piece.INVALID;
	}
	
	public boolean containsKey(Object var) {
		if(!(var instanceof Location))
			return false;
		
		Location location = (Location) var;
		return this.board.containsKey(this.findSquare(location));
	}
	
	public Location findSquare(Location location) {
		for(Location l : this.board.keySet()) {
			if(l.equals(location)) {
				return l;
			}
		}
		
		return location;
	}
	
	public Location getFlag() {
		return this.flag;
	}
	
	public Location getSpawn() {
		return this.spawn;
	}
	
	public void addPortals(Location first, Location second) {
		Portals p = new Portals(first, second);
		this.portals.add(p);
		this.set(first, Piece.PORTAL);
		this.set(second, Piece.PORTAL);
	}
	
	public boolean hasPortals(Location location) {
		for(Portals p : this.portals) {
			if(p.first.equals(location)||p.second.equals(location))
				return true;
		}
		return false;
	}
	public boolean hasPortals2D(int x, int y) {
		for(Portals p : this.portals) {
			if(p.first.equals2D(x,y)||p.second.equals2D(x,y))
				return true;
		}
		return false;
	}
	public int getPortalZLocation(int x, int y) {
		for(Portals p : this.portals) {
			if(p.first.equals2D(x,y)||p.second.equals2D(x,y)) {
				if(p.first.equals2D(x,y)) {
					return p.first.z;
				} else if(p.second.equals2D(x,y)) {
					return p.second.z;
				}
			}
		}
		return 0;
	}
	
	public void setPlayers(int i) {
		this.players = i;
	}
	
	public Portals getPortal(Location location) {
		for(Portals p : this.portals) {
			if(p.first.equals(location)||p.second.equals(location))
				return p;
		}
		return null;
	}
	
	public Location getPiece(Piece piece) {
		for(Location l : this.board.keySet()) {
			if(this.get(l) == Piece.PLAYER)
				return l;
		}
		return null;
	}
	
	public static boolean isValideLocation(Location location) {
		return (location != null && location.x < SIZE && location.x >= 0
				&& location.y < SIZE && location.y >= 0
				&& location.z < SIZE && location.z >= 0);
	}

}
