package be.lyam;

public class Location {
	
	public int x;
	public int y;
	public int z;
	
	public Location() {
		this(0,0,0);
	}
	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return ("(x="+this.x+";y="+this.y+";z="+this.z+")");
	}
	
	public boolean equals(Object var) {
		if(!(var instanceof Location)) {
			return false;
		}
		Location l = (Location) var;
		return (l.x == this.x && l.y == this.y && l.z == this.z); 
	}

	public boolean equals2D(int x, int y) {
		return (x == this.x && y == this.y);
	}
	
}
