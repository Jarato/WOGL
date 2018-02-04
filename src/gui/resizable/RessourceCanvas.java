package gui.resizable;

import gui.SelectedCreatureInfo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simulation.world.creature.Body;
import simulation.world.creature.Creature;

public class RessourceCanvas extends ResizableCanvas {

	private SelectedCreatureInfo sci;
	
	public void setSelectedCreatureInfo(SelectedCreatureInfo selCreInf) {
		sci = selCreInf;
	}
	
	@Override
	public void draw() {
		if (sci.getCreature() != null) {
			GraphicsContext gc = getGraphicsContext2D();
			Creature c = sci.getCreature();
			Body b = c.getBody();
			
			double lifePortion = b.getLife().getX()/b.getLife().getY();
			double stomachPortion = b.getStomach().getX()/b.getStomach().getY();
			
			double lifeRectStartY = getHeight()*(1-lifePortion);
			double stomachRectStartY = getHeight()*(1-stomachPortion);
			gc.setFill(Color.WHITE);
			gc.fillRect(0,0,getWidth(), getHeight());
			gc.setFill(Color.GREEN);
			gc.fillRect(0,lifeRectStartY, getWidth()/2,getHeight());
			gc.setFill(Color.CORNFLOWERBLUE);
			gc.fillRect(getWidth()/2, stomachRectStartY, getWidth(), getHeight());
		}
	}	

}
