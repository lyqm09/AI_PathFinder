package be.lyam;

public class Portals {
	
	public Location first;
	public Location second;
	
	public Portals(Location portal1, Location portal2) {
		this.first = portal1;
		this.second = portal2;
	}
	
	public Location goTo(Location var1) {
		if(var1.equals(this.first))
			return this.second;
		if(var1.equals(this.second))
			return this.first;
		return var1;
	}

}
