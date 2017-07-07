package main.gui;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import main.simulation.world.Body;
import main.simulation.world.Brain;
import main.simulation.world.Creature;
import main.simulation.world.Plant;
import main.simulation.world.PlantGrid;
import main.simulation.world.World;
import main.simulation.world.Brain.InputMask;
import main.simulation.world.PlantGrid.PlantBox;
import pdf.simulation.CollisionCircle;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class WorldCanvas extends ResizableCanvas {
	//CONSTS
	public static final double Z = 2;
	
	//ATTRIBUTES
	private World world;
	
	public void setWorld(World newWorld) {
		world = newWorld;
	}
	
	@Override
	public void draw() {
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.strokeRect(0, 0, World.SIZE*Z, World.SIZE*Z);
		if (world != null) {
			
		
		PlantBox[][] pGrid = world.getPlantGrid().getGrid();
		for (int i = 0; i < pGrid.length; i++) {
			for (int j = 0; j < pGrid[0].length; j++) {
				Plant p = pGrid[i][j].getPlant();
				if (p != null) {
					fillCircle(gc,p,Plant.COLOR);
				}
			}
		}
		ArrayList<Creature> creatures = world.getCreatures();
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			Body b = c.getBody();
			if (c.getId()==0) {	
				//Vision-Test
				InputMask mask = c.getBrain().getInputMask();
				for (int e = 0; e < mask.eyesInputs.length; e++) {
				//	if (mask.eyesInputs[e].getX() != null && b != null) {
					double length = mask.eyesInputs[e].getX()+b.getRadius();
					gc.setFill(mask.eyesInputs[e].getY());
					double angle = b.getRotationAngle()-Brain.SIGHT_MAXANGLE/2.0+Brain.SIGHT_AREA_WIDTH*(e+1);
					gc.fillArc((b.getXCoordinate()-length)*Z, (b.getYCoordinate()-length)*Z, length*2*Z, length*2*Z, -angle, Brain.SIGHT_AREA_WIDTH, ArcType.ROUND);
					gc.strokeArc((b.getXCoordinate()-length)*Z, (b.getYCoordinate()-length)*Z, length*2*Z, length*2*Z, -angle, Brain.SIGHT_AREA_WIDTH, ArcType.OPEN);
				//	}
					//mask.eyesInputs[e].getX()
				}
				gc.strokeText("stomach: ("+UtilMethods.roundTo(b.getStomach().getX(),2)+"/"+b.getStomach().getY()+")", World.SIZE*Z+3, 15);
				gc.strokeText("life: ("+UtilMethods.roundTo(b.getLife().getX(),2)+"/"+b.getLife().getY()+")", World.SIZE*Z+3, 30);
				//gc.strokeText(b.+b.getRotationAngle(), World.SIZE*Z+3, 15);
				//gc.strokeText(String.valueOf(b.getRotationAngle()), b.getXCoordinate()*Z, b.getYCoordinate()*Z);
				
				
				/*			//PlantGrid-Test
				double range = b.getRadius()+Plant.RADIUS;
				Pair<Integer,Integer> upLeft = world.getPlantGridPosition(b.getXCoordinate()-range,b.getYCoordinate()-range);
				Pair<Integer,Integer> downRight = world.getPlantGridPosition(b.getXCoordinate()+range,b.getYCoordinate()+range);
				double newRadius = b.getRadius()+Plant.RADIUS;
				gc.strokeOval((b.getXCoordinate()-newRadius)*Z, (b.getYCoordinate()-newRadius)*Z, newRadius*2*Z, newRadius*2*Z);
				for (int w = upLeft.getX(); w <= downRight.getX(); w++) {
					for (int h = upLeft.getY(); h <= downRight.getY(); h++) {
						gc.strokeRect(w*PlantGrid.PLANTBOX_SIZE*Z, h*PlantGrid.PLANTBOX_SIZE*Z, PlantGrid.PLANTBOX_SIZE*Z, PlantGrid.PLANTBOX_SIZE*Z);
					}
				}*/
			}
			
			
			
			fillCircle(gc,b,b.getColor());
			double rotationRadians = Math.toRadians(b.getRotationAngle());
			double length = b.getRadius();
			if (c.attacks()) length += Body.SPIKE_LENGTH;
			gc.strokeLine(b.getXCoordinate()*Z, b.getYCoordinate()*Z, (b.getXCoordinate()+Math.cos(rotationRadians)*length)*Z, (b.getYCoordinate()+Math.sin(rotationRadians)*length)*Z);
			if (c.getSplitTimer() != Creature.SPLIT_BASETIME) {
				gc.strokeText(String.valueOf(c.getSplitTimer()), b.getXCoordinate()*Z+3*Z, b.getYCoordinate()*Z+7*Z);
			}
			
		}
		}
	}
	
	private void fillCircle(GraphicsContext gc, CollisionCircle circle, Color color) {
		gc.setFill(color);
		gc.fillOval((circle.getXCoordinate()-circle.getRadius())*Z, (circle.getYCoordinate()-circle.getRadius())*Z, circle.getRadius()*2*Z, circle.getRadius()*2*Z);
		gc.strokeOval((circle.getXCoordinate()-circle.getRadius())*Z, (circle.getYCoordinate()-circle.getRadius())*Z, circle.getRadius()*2*Z, circle.getRadius()*2*Z);
	}

}