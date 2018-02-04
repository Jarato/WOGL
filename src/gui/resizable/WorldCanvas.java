package gui.resizable;

import java.util.ArrayList;

import gui.SelectedCreatureInfo;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import pdf.ai.dna.DNA;
import pdf.simulation.CollisionCircle;
import pdf.simulation.Point2D;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.world.Plant;
import simulation.world.PlantGrid.PlantBox;
import simulation.world.World;
import simulation.world.creature.Body;
import simulation.world.creature.Brain;
import simulation.world.creature.Brain.InputMask;
import simulation.world.creature.Cadaver;
import simulation.world.creature.Creature;
import simulation.world.environment.Rock;

public class WorldCanvas extends ResizableCanvas {
	//CONSTS
	public static final double MAX_ZOOM = 50;
	public static final double ZOOM_SPEED = 1/200.0;
	public static final double CREATURE_MOUTH_ANGLE = 25;
	
	//ATTRIBUTES
	private World world;
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
	private SelectedCreatureInfo sci;
	
	public void setWorld(World newWorld) {
		world = newWorld;
	}
	
	public void initZoomDragValues() {
		double min = Math.min(getWidth(), getHeight());
		zoomD = min/World.SIZE;
		xWorldBound = Math.max((getWidth()-World.SIZE*zoomD)/2.0,0.0);
		yWorldBound = Math.max((getHeight()-World.SIZE*zoomD)/2.0,0.0);
	}
	
	public void setSelectedCreatureInfo(SelectedCreatureInfo setSci) {
		sci = setSci;
	}
	
	public Creature getCreatureOnPosition(double x, double y) {
		ArrayList<Creature> creatures = world.getCreatures();	
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).getBody().distanceTo(getWorldPositionOf(x, y))<creatures.get(i).getBody().getRadius()) {
				return creatures.get(i);
			}
		}
		return null;
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
		double drawSize = World.SIZE;
		double xtra = Math.max(getWidth()*0.5, getWidth()-drawSize*zoomD);
		double ytra = Math.max(getHeight()*0.5, getHeight()-drawSize*zoomD);
		if (xWorldBound > xtra) xWorldBound = xtra;
		if (xWorldBound < getWidth()-drawSize*zoomD-xtra) xWorldBound = getWidth()-drawSize*zoomD-xtra;
		if (yWorldBound > ytra) yWorldBound = ytra;
		if (yWorldBound < getHeight()-drawSize*zoomD-ytra) yWorldBound = getHeight()-drawSize*zoomD-ytra;
	}
	
	
	
	@Override
	public void draw() {
		if (getWidth() != oldWidth || getHeight() != oldHeight) {
			oldHeight = getHeight();
			oldWidth = getWidth();
		}
		if (sci == null) System.out.println("SCI IS NULL");
		if (sci.isFollowSelected()) {
			Creature c = sci.getCreature();
			Body b = c.getBody();	
			xWorldBound = -b.getXCoordinate()*zoomD+getWidth()/2.0;
			yWorldBound = -b.getYCoordinate()*zoomD+getHeight()/2.0;
		}
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setFill(Rock.COLOR);
		gc.fillRect(0, 0, getWidth(), getHeight());
		gc.setFill(Color.WHITE);
		gc.fillRect(xWorldBound,yWorldBound, World.SIZE*zoomD, World.SIZE*zoomD);
		gc.strokeRect(xWorldBound,yWorldBound, World.SIZE*zoomD, World.SIZE*zoomD);
		if (world != null) {
			// ROCKS
			Rock[] rocks = world.getRockSystem().getRocks();
			gc.setStroke(Color.BLACK);
			gc.setFill(Rock.COLOR);
			for (int i = 0; i < rocks.length; i++) {
				double[] xPo = rocks[i].getWorldPoints().getX();
				double[] yPo = rocks[i].getWorldPoints().getY();
				double[] xsPo = new double[xPo.length];
				double[] ysPo = new double[yPo.length];
				for (int k = 0; k < xPo.length; k++) {
					xsPo[k] = xWorldBound + xPo[k]*zoomD;
					ysPo[k] = yWorldBound + yPo[k]*zoomD;
				}
				gc.fillPolygon(xsPo, ysPo, xsPo.length);
				gc.strokePolygon(xsPo, ysPo, xsPo.length);
				// rectangle round rocks
				double x = xWorldBound + rocks[i].getTopLeftBound().getX()*zoomD;
				double y = yWorldBound + rocks[i].getTopLeftBound().getY()*zoomD;
				double w = (rocks[i].getBottomRightBound().getX()-rocks[i].getTopLeftBound().getX())*zoomD;
				double h = (rocks[i].getBottomRightBound().getY()-rocks[i].getTopLeftBound().getY())*zoomD;
				//gc.strokeRect(x, y, w, h);
			}
			// PLANTS
			gc.setStroke(Color.BLACK);
			PlantBox[][] pGrid = world.getPlantGrid().getGrid();
			for (int i = 0; i < pGrid.length; i++) {
				for (int j = 0; j < pGrid[0].length; j++) {
					Plant p = pGrid[i][j].getPlant();
					if (p != null) {
						fillCircle(gc,p,Plant.COLOR);
					}
				}
			}
			
			// CADAVERS
			ArrayList<Cadaver> cadavers = world.getCadavers();
			for (int i = 0; i < cadavers.size(); i++) {
				Cadaver cad = cadavers.get(i);
				fillCircle(gc, cad, cad.getColor(), Color.GRAY);
			}
			// CREATURES
			ArrayList<Creature> creatures = world.getCreatures();
			for (int i = 0; i < creatures.size(); i++) {
				Creature c = creatures.get(i);
				drawCreatureBody(gc, c);
			}
			//Creature selC = sci.getCreature();
			if ( sci.getCreature() != null) {
				if (sci.getCreature().isAlive()) { 
					drawSelectedCreature(gc, sci);
					sci.getRessourceCanvas().draw();
				} else {
					sci.initInfo(null);
					sci.getwControl().setSelectedPaneVisibility(false);
				}
			}
		}
	}
	
	private void drawCreatureBody(GraphicsContext gc, Creature c) {
		Body b = c.getBody();
		//BODY
		gc.setLineWidth(2);
		int line_colorVal = (int)Math.round(255.0 - 255.0/c.getLineColorDivider());
		Color age_color = Color.rgb(line_colorVal, line_colorVal, line_colorVal);
		fillCircle(gc,b,b.getColor(), age_color);
		gc.setLineWidth(1);
		// MOUTH
		if (c.eats()) {
			gc.setFill(age_color);
			double length = b.getRadius();
			double angle = b.getRotationAngle()-CREATURE_MOUTH_ANGLE/2.0;
			gc.fillArc(xWorldBound+(b.getXCoordinate()-length)*zoomD, yWorldBound+(b.getYCoordinate()-length)*zoomD, b.getRadius()*2*zoomD, length*2*zoomD, -angle, -CREATURE_MOUTH_ANGLE, ArcType.ROUND);
		}
		gc.setStroke(age_color);
		double rotationRadians = Math.toRadians(b.getRotationAngle());
		double length = b.getRadius();
		// SPIKE+DIRECTION
		if (c.attacks()) length *= Body.SPIKE_LENGTH_PERCENT;
		gc.strokeLine(xWorldBound+b.getXCoordinate()*zoomD, yWorldBound+b.getYCoordinate()*zoomD, xWorldBound+(b.getXCoordinate()+Math.cos(rotationRadians)*length)*zoomD, yWorldBound+(b.getYCoordinate()+Math.sin(rotationRadians)*length)*zoomD);	
		gc.setStroke(Color.BLACK);
	}
	
	private void drawSelectedCreature(GraphicsContext gc, SelectedCreatureInfo sci) {
		sci.getwControl().updateSelectedPaneInfo();
		Creature c = sci.getCreature();
		Body b = c.getBody();
		double[] bodyPoint = new double[] {b.getXCoordinate(), b.getYCoordinate()};
		
		InputMask mask = c.getBrain().getInputMask();
		if (sci.isShowPlantView()) {
			for (int e = 0; e < mask.eyesInputPlant.length; e++) {
				double length = mask.eyesInputPlant[e].getX()+b.getRadius();
				double angle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e+1);
				drawViewArc(gc,b,length,angle, b.getSightAreaWidth(), mask.eyesInputPlant[e].getY());
			}
		} else if (sci.isShowCreatureView()) {
			for (int e = 0; e < mask.eyesInputCreature.length; e++) {
				double length = mask.eyesInputCreature[e].getX()+b.getRadius();
				double angle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e+1);
				drawViewArc(gc,b,length,angle, b.getSightAreaWidth(), mask.eyesInputCreature[e].getY());
			}
		} else if (sci.isShowWallView()) {
			for (int e = 0; e < mask.eyesInputWall.length; e++) {
				double length = mask.eyesInputWall[e].getX()+b.getRadius();
				
				double angle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e+0.5);
				double angle_radians = Math.toRadians(angle);
				double[] endPoint = new double[] {Math.cos(angle_radians), Math.sin(angle_radians)};
				endPoint = UtilMethods.vectorAddition(bodyPoint, UtilMethods.vectorSkalar(endPoint, length));
				//gc.setStroke(Rock.COLOR);
				gc.setLineWidth(2);
				gc.strokeLine(xWorldBound + b.getXCoordinate()*zoomD, yWorldBound + b.getYCoordinate()*zoomD, xWorldBound + endPoint[0]*zoomD, yWorldBound + endPoint[1]*zoomD);
				//drawViewArc(gc,b,length,angle, b.getSightAreaWidth(), mask.eyesInputWall[e].getY());
			}
		} else if (sci.isShowCollision()) {
			for (int e = 0; e < mask.collision.length; e++) {
				double length = b.getRadius()*2;
				double angle = b.getRotationAngle()-Brain.COLLISION_DETECTION_AREA_ANGLE/2.0+Brain.COLLISION_DETECTION_AREA_ANGLE*(e+1);
				drawViewArc(gc,b,length,angle, Brain.COLLISION_DETECTION_AREA_ANGLE, Color.hsb(0, 0, 1-mask.collision[e]));
			}
		}
		drawCreatureBody(gc, c);
		gc.setLineWidth(2);
		gc.setStroke(Color.RED);
		gc.strokeOval(xWorldBound + (bodyPoint[0]-b.getRadius()*1.1)*zoomD, yWorldBound + (bodyPoint[1]-b.getRadius()*1.1)*zoomD, b.getRadius()*2.2*zoomD, b.getRadius()*2.2*zoomD);
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		
		//Selected Panel
		Canvas can = sci.getBodyCanvas();
		double bodyCW = can.getWidth()/2.0;
		double bodyCH = can.getHeight()/2.0;
		GraphicsContext gcBody = can.getGraphicsContext2D();
		//BODY
		double zoom = can.getWidth()/(Body.MAX_RADIUS+1);
		gcBody.setFill(Color.WHITE);
		gcBody.fillRect(0, 0, can.getWidth(), can.getHeight());
		gcBody.setLineWidth(2);
		int line_colorVal = (int)Math.round(255.0 - 255.0/c.getLineColorDivider());
		Color age_color = Color.rgb(line_colorVal, line_colorVal, line_colorVal);
		gcBody.setFill(b.getColor());
		gcBody.setStroke(age_color);
		double xStart = bodyCW-b.getRadius()*zoom/2.0;
		double yStart = bodyCH-b.getRadius()*zoom/2.0;
		gcBody.fillOval(xStart, yStart, b.getRadius()*zoom, b.getRadius()*zoom);
		gcBody.strokeOval(xStart, yStart, b.getRadius()*zoom, b.getRadius()*zoom);
		gcBody.setLineWidth(1);
		// MOUTH
		if (c.eats()) {
			gcBody.setFill(age_color);
			double length = b.getRadius();
			double angle = b.getRotationAngle()-CREATURE_MOUTH_ANGLE/2.0;
			gcBody.fillArc(xStart, yStart, b.getRadius()*zoom, length*zoom, -angle, -CREATURE_MOUTH_ANGLE, ArcType.ROUND);
		}
		gcBody.setStroke(age_color);
		double rotationRadians = Math.toRadians(b.getRotationAngle());
		double length = b.getRadius();
		// SPIKE+DIRECTION
		if (c.attacks()) length *= Body.SPIKE_LENGTH_PERCENT;
		gcBody.strokeLine(bodyCW, bodyCH, bodyCW+(Math.cos(rotationRadians)*length)*zoom/2.0, bodyCH+(Math.sin(rotationRadians)*length)*zoom/2.0);
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
		}
		Pair<double[], Double> res = world.getRockSystem().getClosestPointTo(b);
		gc.strokeLine(xWorldBound + b.getXCoordinate()*zoomD, yWorldBound + b.getYCoordinate()*zoomD, xWorldBound + res.getX()[0]*zoomD, yWorldBound + res.getX()[1]*zoomD);*/
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