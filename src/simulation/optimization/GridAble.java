package simulation.optimization;

import pdf.util.Pair;

public interface GridAble {
	public Pair<Integer,Integer> getGridPosition();
	public void setGridPosition(Pair<Integer,Integer> pos);
}


