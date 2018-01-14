package main.gui;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import main.simulation.world.Body;
import main.simulation.world.Brain;
import main.simulation.world.Creature;
import main.simulation.world.Plant;
import main.simulation.world.World;
import main.simulation.world.Brain.InputMask;
import main.simulation.world.PlantGrid.PlantBox;
import pdf.ai.dna.DNA;
import pdf.simulation.CollisionCircle;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class WorldCanvas extends ResizableCanvas {
	//CONSTS
	public static final double MAX_ZOOM = 50;
	public static final double ZOOM_SPEED = 1/200.0;
	public static final double CREATURE_MOUTH_ANGLE = 20;
	
	//ATTRIBUTES
	private World world;
	private int selectedId = -1;
	/*
	 * 0 = Plants
	 * 1 = Creatures
	 * 2 = Walls
	 */
	private int whichEyes = 0;
	
	private double xWorldBound;
	private double yWorldBound;
	private double oldWidth;
	private double oldHeight;
	private double zoomD;
	private Pair<Double,Double> dragStartMPos = new Pair<Double,Double>(0.0,0.0);
	private Pair<Double,Double> dragStartWPos = new Pair<Double,Double>(0.0,0.0);
	private Pair<Integer,Double> minDifId = new Pair<Integer,Double>(-1,0.0);
	private Pair<Integer,Double> maxDifId = new Pair<Integer,Double>(-1,0.0);
	
	public void setWorld(World newWorld) {
		world = newWorld;
	}
	
	public int getIdOfPosition(double x, double y) {
		ArrayList<Creature> creatures = world.getCreatures();	
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).getBody().distanceTo(getWorldPositionOf(x, y))<creatures.get(i).getBody().getRadius()) {
				return creatures.get(i).getId();
			}
		}
		return -1;
	}
	
	public Pair<Double,Double> getWorldPositionOf(double x, double y){
		return new Pair<Double,Double>((x-xWorldBound)/zoomD, (y-yWorldBound)/zoomD);
	}
	
	public void setDragStartPos(double xPos, double yPos) {
		dragStartMPos = new Pair<Double,Double>(xPos,yPos);
		dragStartWPos = new Pair<Double,Double>(xWorldBound,yWorldBound);
	}
	
	public void zoom(double xPos, double yPos, double amount) {
		Pair<Double,Double> wPos = getWorldPositionOf(xPos, yPos);
		if (posIsInWorld(wPos)) {	
			double newf = zoomD*(1+amount*ZOOM_SPEED);
			if (newf > MAX_ZOOM) newf = MAX_ZOOM;
			double mult = newf/zoomD-1;
			xWorldBound = xWorldBound-mult*wPos.getX()*zoomD;
			yWorldBound = yWorldBound-mult*wPos.getY()*zoomD;
			
			zoomD = newf;
			double min = Math.min(getWidth(), getHeight());
			if (zoomD < min/World.SIZE) {
				zoomD = min/World.SIZE;
				xWorldBound = Math.max((getWidth()-World.SIZE*zoomD)/2.0,0.0) ;
				yWorldBound = Math.max((getHeight()-World.SIZE*zoomD)/2.0,0.0) ;
			}
			checkWorldOutputPosition();
		}	
	}
	
	private boolean posIsInWorld(Pair<Double,Double> pos) {
		return (pos.getX()>=0 && pos.getX()<=World.SIZE && pos.getY()>=0 && pos.getY()<=World.SIZE);
	}
	
	public void drag(double xPos, double yPos) {
		xWorldBound = dragStartWPos.getX()+(xPos-dragStartMPos.getX());
		yWorldBound = dragStartWPos.getY()+(yPos-dragStartMPos.getY());
		checkWorldOutputPosition();
	}
	
	private void checkWorldOutputPosition() {
		double xtra = Math.max(0, getWidth()-World.SIZE*zoomD);
		double ytra = Math.max(0, getHeight()-World.SIZE*zoomD);
		if (xWorldBound > xtra) xWorldBound = xtra;
		if (xWorldBound < getWidth()-World.SIZE*zoomD-xtra) xWorldBound = getWidth()-World.SIZE*zoomD-xtra;
		if (yWorldBound > ytra) yWorldBound = ytra;
		if (yWorldBound < getHeight()-World.SIZE*zoomD-ytra) yWorldBound = getHeight()-World.SIZE*zoomD-ytra;
	}
	
	public void setSelectedId(int newId) {
		if (newId == selectedId) {
			whichEyes = (whichEyes+1)%3;
		} else {
			selectedId = newId;
			checkGeneticSimilarity();
			whichEyes = 0;
		}
	}
	
	private void checkGeneticSimilarity() {
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
			DNA selDNA = selC.getDNA();
			for (int i = 0; i < creatures.size(); i++) {
				if (creatures.get(i).getId() != selectedId) {
					double diff = selDNA.percentageDifference(creatures.get(i).getDNA());
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
	}
	
	@Override
	public void draw() {
		if (getWidth() != oldWidth || getHeight() != oldHeight) {
			oldHeight = getHeight();
			oldWidth = getWidth();
			double min = Math.min(getWidth(), getHeight());
			zoomD = min/World.SIZE;
			xWorldBound = Math.max((getWidth()-World.SIZE*zoomD)/2.0,0.0) ;
			yWorldBound = Math.max((getHeight()-World.SIZE*zoomD)/2.0,0.0) ;
		}
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.strokeRect(xWorldBound,yWorldBound, World.SIZE*zoomD, World.SIZE*zoomD);
		if (world != null) {
			
			checkGeneticSimilarity();
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
			if (c.getId()==selectedId) {	
				drawSelectedCreature(gc, c);
			}
			
			//BODY
			fillCircle(gc,b,b.getColor(), Color.BLACK);
			// MOUTH
			if (c.eats()) {
				gc.setFill(Color.BLACK);
				double length = b.getRadius();
				double angle = b.getRotationAngle()-CREATURE_MOUTH_ANGLE/2.0;
				gc.fillArc(xWorldBound+(b.getXCoordinate()-length)*zoomD, yWorldBound+(b.getYCoordinate()-length)*zoomD, b.getRadius()*2*zoomD, length*2*zoomD, -angle, -CREATURE_MOUTH_ANGLE, ArcType.ROUND);
			}
			gc.setStroke(Color.BLACK);
			double rotationRadians = Math.toRadians(b.getRotationAngle());
			double length = b.getRadius();
			// SPIKE+DIRECTION
			if (c.attacks()) length *= Body.SPIKE_LENGTH_PERCENT;
			gc.strokeLine(xWorldBound+b.getXCoordinate()*zoomD, yWorldBound+b.getYCoordinate()*zoomD, xWorldBound+(b.getXCoordinate()+Math.cos(rotationRadians)*length)*zoomD, yWorldBound+(b.getYCoordinate()+Math.sin(rotationRadians)*length)*zoomD);
			
		}
		}
	}
	
	private void drawSelectedCreature(GraphicsContext gc, Creature c) {
		Body b = c.getBody();
		//Vision-Test
		InputMask mask = c.getBrain().getInputMask();
		switch(whichEyes) {
		case 0: 
			for (int e = 0; e < mask.eyesInputPlant.length; e++) {
				double length = mask.eyesInputPlant[e].getX()+b.getRadius();
				double angle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e+1);
				drawViewArc(gc,b,length,angle, b.getSightAreaWidth(), mask.eyesInputPlant[e].getY());
			}
		break;
		case 1:
			for (int e = 0; e < mask.eyesInputCreature.length; e++) {
				double length = mask.eyesInputCreature[e].getX()+b.getRadius();
				double angle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e+1);
				drawViewArc(gc,b,length,angle, b.getSightAreaWidth(), mask.eyesInputCreature[e].getY());
			}	
		break;
		case 2:
			for (int e = 0; e < mask.eyesInputWall.length; e++) {
				double length = mask.eyesInputWall[e].getX()+b.getRadius();
				double angle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e+1);
				drawViewArc(gc,b,length,angle, b.getSightAreaWidth(), mask.eyesInputWall[e].getY());
			}		
		}
		Creature minDifC = world.getCreatureById(minDifId.getX());
		double xPosMinDif = 0;
		double yPosMinDif = 0;
		if (minDifC != null) {
			Body minDifB = minDifC.getBody();
			xPosMinDif = minDifB.getXCoordinate();
			yPosMinDif = minDifB.getYCoordinate();
		}
		Creature maxDifC = world.getCreatureById(maxDifId.getX());
		double xPosMaxDif = 0;
		double yPosMaxDif = 0;
		if (minDifC != null) {
			Body maxDifB = maxDifC.getBody();
			xPosMaxDif = maxDifB.getXCoordinate();
			yPosMaxDif = maxDifB.getYCoordinate();
		}
		gc.setLineWidth(2);
		gc.setStroke(Color.BLUE);
		gc.strokeLine(xWorldBound+b.getXCoordinate()*zoomD, yWorldBound+b.getYCoordinate()*zoomD, xWorldBound+xPosMinDif*zoomD, yWorldBound+ yPosMinDif*zoomD);
		gc.setStroke(Color.RED);
		gc.strokeLine(xWorldBound+b.getXCoordinate()*zoomD, yWorldBound+b.getYCoordinate()*zoomD, xWorldBound+xPosMaxDif*zoomD, yWorldBound+yPosMaxDif*zoomD);
		double ypos = yWorldBound+15;
		double xpos = xWorldBound+World.SIZE*zoomD+3;
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		gc.strokeText("id: "+String.valueOf(c.getId()), xpos, ypos);
		ypos += 15;
		gc.strokeText("generation: "+c.getGeneration(), xpos, ypos);
		ypos += 15;
		gc.strokeText("stomach: ("+UtilMethods.roundTo(b.getStomach().getX(),2)+"/"+UtilMethods.roundTo(b.getStomach().getY(),2)+")", xpos, ypos);
		ypos += 15;
		gc.strokeText("life: ("+UtilMethods.roundTo(b.getLife().getX(),2)+"/"+UtilMethods.roundTo(b.getLife().getY(),2)+")", xpos, ypos);
		ypos += 15;
		gc.strokeText("age: "+String.valueOf(c.getAge()), xpos, ypos);
		ypos += 15;
		gc.strokeText("speed: "+String.valueOf(UtilMethods.point2DLength(c.getBody().getVelocity())), xpos, ypos);
		ypos += 15;
		gc.strokeText("least different creature: "+minDifId.getX()+" ("+UtilMethods.roundTo(minDifId.getY()*100,2)+"%)", xpos, ypos);
		ypos += 15;
		gc.strokeText("most different creature: "+maxDifId.getX()+" ("+UtilMethods.roundTo(maxDifId.getY()*100,2)+"%)", xpos, ypos);
		ypos += 15;
		gc.strokeText("is attacked: "+(mask.gotHurt?"true":"false"), xpos, ypos);
		if (c.getSplitTimer() != c.getBody().getSplitTimerBase()){
			ypos += 15;
			gc.strokeText("splitTimer: "+String.valueOf(c.getSplitTimer()), xpos, ypos);
		}
		ypos += 15;
		if (c.getParentId() == -1) {
			gc.strokeText("parent: creator", xpos, ypos);
		} else {
			gc.strokeText("parent: "+c.getParentId(), xpos, ypos);
		}
		for (int i = 0; i < c.getChildrenIdList().size(); i++) {
			ypos += 15;
			gc.strokeText("child "+(i+1)+": "+c.getChildrenIdList().get(i), xpos, ypos);
		}
		int[] f = world.getPlantGrid().getNoFieldsW();
		for (int i = 0; i < 9; i++) {
			ypos += 15;
			gc.strokeText(i+": "+f[i], xpos, ypos);
		}
		
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
	
	private void drawViewArc(GraphicsContext gc, Body b, double length, double angle, double width, Color col) {
		gc.setFill(col);
		gc.fillArc(xWorldBound+(b.getXCoordinate()-length)*zoomD, yWorldBound+(b.getYCoordinate()-length)*zoomD, length*2*zoomD, length*2*zoomD, -angle, width, ArcType.ROUND);
		gc.strokeArc(xWorldBound+(b.getXCoordinate()-length)*zoomD, yWorldBound+(b.getYCoordinate()-length)*zoomD, length*2*zoomD, length*2*zoomD, -angle, width, ArcType.ROUND);
	}
	
	private void fillCircle(GraphicsContext gc, CollisionCircle circle, Color color) {
		gc.setFill(color);
		gc.fillOval(xWorldBound+(circle.getXCoordinate()-circle.getRadius())*zoomD, yWorldBound+(circle.getYCoordinate()-circle.getRadius())*zoomD, circle.getRadius()*2*zoomD, circle.getRadius()*2*zoomD);
		gc.strokeOval(xWorldBound+(circle.getXCoordinate()-circle.getRadius())*zoomD, yWorldBound+(circle.getYCoordinate()-circle.getRadius())*zoomD, circle.getRadius()*2*zoomD, circle.getRadius()*2*zoomD);
	}
	
	private void fillCircle(GraphicsContext gc, CollisionCircle circle, Color fillColor, Color strokeColor) {
		gc.setFill(fillColor);
		gc.setStroke(strokeColor);
		gc.fillOval(xWorldBound+(circle.getXCoordinate()-circle.getRadius())*zoomD, yWorldBound+(circle.getYCoordinate()-circle.getRadius())*zoomD, circle.getRadius()*2*zoomD, circle.getRadius()*2*zoomD);
		gc.strokeOval(xWorldBound+(circle.getXCoordinate()-circle.getRadius())*zoomD, yWorldBound+(circle.getYCoordinate()-circle.getRadius())*zoomD, circle.getRadius()*2*zoomD, circle.getRadius()*2*zoomD);
	}

}