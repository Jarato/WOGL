package main.simulation.world.environment;

import pdf.simulation.CollisionCircle;

public class RockSystem {
	private Rock[] rocks;
	
	public RockSystem(int numberOfRocks) {
		rocks = new Rock[numberOfRocks];
	}
	
	public Rock[] getRocks() {
		return rocks;
	}
	
	public void checkCollision(CollisionCircle object) {
		
	}
}
