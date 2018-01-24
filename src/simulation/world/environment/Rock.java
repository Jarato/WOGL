package simulation.world.environment;

import pdf.util.Pair;

public class Rock {
	private Pair<Double,Double>[] edges;
	
	public Rock(int numberOfEdges) {
		edges = new Pair[numberOfEdges];
	}
	
	public Pair<Double,Double>[] getEdges(){
		return edges;
	}
}
