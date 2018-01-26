package simulation.world.environment;

import javafx.scene.paint.Color;
import pdf.util.Pair;

public class Rock {
	public static final double COLLISION_HARDNESS = 1.0;
	public static final Color COLOR = Color.GRAY;
	private Pair<Double,Double>[] edges;
	
	public Rock(int numberOfEdges) {
		edges = new Pair[numberOfEdges];
	}
	
	public Pair<Double,Double>[] getEdges(){
		return edges;
	}
}
