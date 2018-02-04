package gui;

import java.util.ArrayList;

import gui.resizable.RessourceCanvas;
import javafx.scene.canvas.Canvas;
import pdf.ai.dna.DNA;
import pdf.util.Pair;
import simulation.world.creature.Creature;

public class SelectedCreatureInfo {
	private Creature creature;
	//private Pair<Integer,Double> minDifId = new Pair<Integer,Double>(-1,0.0);
	//private Pair<Integer,Double> maxDifId = new Pair<Integer,Double>(-1,0.0);
	private boolean showPlantView;
	private boolean showCreatureView;
	private boolean showWallView;
	private boolean showCollision;
	private boolean followSelected;
	private boolean selected;
	private Canvas bodyCanvas;
	private RessourceCanvas ressourceCanvas;
	//private WorldCanvas wCanvas;
	private WorldWindowCtrl wControl;
		
		//public SelectedCreatureInfo(WorldCanvas wc, WorldWindowCtrl wctrl) {
		//	wCanvas = wc;
		//	wControl = wctrl;
		//}
	
	public Creature getCreature() {
		return creature;
	}
	
	public Canvas getBodyCanvas() {
		return bodyCanvas;
	}

	public void setBodyCanvas(Canvas bodyCanvas) {
		this.bodyCanvas = bodyCanvas;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isShowPlantView() {
		return showPlantView;
	}

	public void setShowPlantView(boolean showPlantView) {
		this.showPlantView = showPlantView;
	}

	public boolean isShowCreatureView() {
		return showCreatureView;
	}

	public void setShowCreatureView(boolean showCreatureView) {
		this.showCreatureView = showCreatureView;
	}

	public boolean isShowWallView() {
		return showWallView;
	}

	public void setShowWallView(boolean showWallView) {
		this.showWallView = showWallView;
	}

	public boolean isShowCollision() {
		return showCollision;
	}

	public void setShowCollision(boolean showCollision) {
		this.showCollision = showCollision;
	}

	public boolean isFollowSelected() {
		return followSelected;
	}

	public void setFollowSelected(boolean followSelected) {
		this.followSelected = followSelected;
	}
	
	public boolean initInfo(Creature c) { // result : creature changed
		if (c == null || creature == null || creature.getId() != c.getId()) {
			creature = c;
			showPlantView = false;
			showCreatureView = false;
			showWallView = false;
			showCollision = false;
			followSelected = false;
			return true;
		}
		return false;
	}

	public WorldWindowCtrl getwControl() {
		return wControl;
	}

	public void setwControl(WorldWindowCtrl wControl) {
		this.wControl = wControl;
	}

	public RessourceCanvas getRessourceCanvas() {
		return ressourceCanvas;
	}

	public void setRessourceCanvas(RessourceCanvas ressourceCanvas) {
		this.ressourceCanvas = ressourceCanvas;
	}
	
	/*private void checkGeneticSimilarity() {
		Creature selC = null;
		ArrayList<Creature> creatures = world.getCreatures();
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).getId() == selectedId) selC = creatures.get(i);
		}
		if (selC != null) {
			double minDif = 1;
			double maxDif = 0;
			int minId = -1;
			int maxId = -1;
			DNA selDNA = selC.getBody().getDNA();
			for (int i = 0; i < creatures.size(); i++) {
				if (creatures.get(i).getId() != selectedId) {
					double diff = selDNA.percentageDifference(creatures.get(i).getBody().getDNA());
					if (diff > maxDif) {
						maxDif = diff;
						maxId = creatures.get(i).getId();
					}
					if (diff < minDif) {
						minDif = diff;
						minId = creatures.get(i).getId();
					}
				}
			}
			minDifId.set(minId, minDif);
			maxDifId.set(maxId, maxDif);
		}
	}*/
}
