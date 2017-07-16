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
	
	//ATTRIBUTES
	private World world;
	private int selectedId = -1;
	/*
	 * 0 = Plants
	 * 1 = Creatures
	 * 2 = Walls
	 */
	private int whichEyes = 0;
	
	private double xs;
	private double ys;
	private double oldWidth;
	private double oldHeight;
	private double f;
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
		return new Pair<Double,Double>((x-xs)/f, (y-ys)/f);
	}
	
	public void setDragStartPos(double xPos, double yPos) {
		dragStartMPos = new Pair<Double,Double>(xPos,yPos);
		dragStartWPos = new Pair<Double,Double>(xs,ys);
	}
	
	public void zoom(double xPos, double yPos, double amount) {
		Pair<Double,Double> wPos = getWorldPositionOf(xPos, yPos);
		if (posIsInWorld(wPos)) {	
			double newf = f*(1+amount*ZOOM_SPEED);
			if (newf > MAX_ZOOM) newf = MAX_ZOOM;
			double mult = newf/f-1;
			xs = xs-mult*wPos.getX()*f;
			ys = ys-mult*wPos.getY()*f;
			
			f = newf;
			double min = Math.min(getWidth(), getHeight());
			if (f < min/World.SIZE) {
				f = min/World.SIZE;
				xs = Math.max((getWidth()-World.SIZE*f)/2.0,0.0) ;
				ys = Math.max((getHeight()-World.SIZE*f)/2.0,0.0) ;
			}
			checkWorldOutputPosition();
		}	
	}
	
	private boolean posIsInWorld(Pair<Double,Double> pos) {
		return (pos.getX()>=0 && pos.getX()<=World.SIZE && pos.getY()>=0 && pos.getY()<=World.SIZE);
	}
	
	public void drag(double xPos, double yPos) {
		xs = dragStartWPos.getX()+(xPos-dragStartMPos.getX());
		ys = dragStartWPos.getY()+(yPos-dragStartMPos.getY());
		checkWorldOutputPosition();
	}
	
	private void checkWorldOutputPosition() {
		double xtra = Math.max(0, getWidth()-World.SIZE*f);
		double ytra = Math.max(0, getHeight()-World.SIZE*f);
		if (xs > xtra) xs = xtra;
		if (xs < getWidth()-World.SIZE*f-xtra) xs = getWidth()-World.SIZE*f-xtra;
		if (ys > ytra) ys = ytra;
		if (ys < getHeight()-World.SIZE*f-ytra) ys = getHeight()-World.SIZE*f-ytra;
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
			f = min/World.SIZE;
			xs = Math.max((getWidth()-World.SIZE*f)/2.0,0.0) ;
			ys = Math.max((getHeight()-World.SIZE*f)/2.0,0.0) ;
		}
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.strokeRect(xs,ys, World.SIZE*f, World.SIZE*f);
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
			fillCircle(gc,b,b.getColor(), (c.eats()?Color.RED:Color.BLACK));
			gc.setStroke(Color.BLACK);
			double rotationRadians = Math.toRadians(b.getRotationAngle());
			double length = b.getRadius();
			if (c.attacks()) length += Body.SPIKE_LENGTH;
			gc.strokeLine(xs+b.getXCoordinate()*f, ys+b.getYCoordinate()*f, xs+(b.getXCoordinate()+Math.cos(rotationRadians)*length)*f, ys+(b.getYCoordinate()+Math.sin(rotationRadians)*length)*f);
			
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
		
		double ypos = ys+15;
		double xpos = xs+World.SIZE*f+3;
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
		gc.fillArc(xs+(b.getXCoordinate()-length)*f, ys+(b.getYCoordinate()-length)*f, length*2*f, length*2*f, -angle, width, ArcType.ROUND);
		gc.strokeArc(xs+(b.getXCoordinate()-length)*f, ys+(b.getYCoordinate()-length)*f, length*2*f, length*2*f, -angle, width, ArcType.ROUND);
	}
	
	private void fillCircle(GraphicsContext gc, CollisionCircle circle, Color color) {
		gc.setFill(color);
		gc.fillOval(xs+(circle.getXCoordinate()-circle.getRadius())*f, ys+(circle.getYCoordinate()-circle.getRadius())*f, circle.getRadius()*2*f, circle.getRadius()*2*f);
		gc.strokeOval(xs+(circle.getXCoordinate()-circle.getRadius())*f, ys+(circle.getYCoordinate()-circle.getRadius())*f, circle.getRadius()*2*f, circle.getRadius()*2*f);
	}
	
	private void fillCircle(GraphicsContext gc, CollisionCircle circle, Color fillColor, Color strokeColor) {
		gc.setFill(fillColor);
		gc.setStroke(strokeColor);
		gc.fillOval(xs+(circle.getXCoordinate()-circle.getRadius())*f, ys+(circle.getYCoordinate()-circle.getRadius())*f, circle.getRadius()*2*f, circle.getRadius()*2*f);
		gc.strokeOval(xs+(circle.getXCoordinate()-circle.getRadius())*f, ys+(circle.getYCoordinate()-circle.getRadius())*f, circle.getRadius()*2*f, circle.getRadius()*2*f);
	}

}