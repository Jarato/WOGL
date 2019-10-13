package gui2;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.paint.Color;
import pdf.util.Pair;
import simulation.world.Plant;
import simulation.world.PlantGrid;
import simulation.world.PlantGrid.PlantBox;
import simulation.world.World;
import simulation.world.creature.Body;
import simulation.world.creature.Brain.InputMask;
import simulation.world.creature.Cadaver;
import simulation.world.creature.Creature;
import simulation.world.environment.Rock;

public class DrawWorld {
	private static World world;
	private static HashMap<Double, float[][]> radius_circleV_map = new HashMap<Double, float[][]>();
	
	public static void drawCreatures(ArrayList<Creature> creatures) {
		for (Creature c : creatures) {
			Body b = c.getBody();
			double radius = b.getRadius();
			drawFilledCircle(b.getXCoordinate(), b.getYCoordinate(), radius, b.getColor());
			double rotationRadians = Math.toRadians(b.getRotationAngle());
			glPushMatrix();
			glColor3d(0, 0, 0);
			glTranslated(b.getXCoordinate(), b.getYCoordinate(), 0);
			glBegin(GL_LINES);
				glVertex2d(0, 0);
				glVertex2d(Math.cos(rotationRadians)*radius, Math.sin(rotationRadians)*radius);
			glEnd();
			glPopMatrix();
		}
		//if (num_of_rad != radius_circleV_map.size()) {
		//	num_of_rad = radius_circleV_map.size();
		//	System.out.println(num_of_rad);
		//}
		//System.out.println(radius_bodyV_map.size());
	}
	
	private static void drawSpecificCreatureInfo(Creature c) {
		Body b = c.getBody();
		InputMask inMask = c.getBrain().getInputMask();
		Pair<Double,Color>[] eyesInput = inMask.eyesInput;
		glPushMatrix();
		
		glTranslated(b.getXCoordinate(), b.getYCoordinate(), 0);
		for (int e = 0; e < eyesInput.length; e++) {
			double length = eyesInput[e].getX()+b.getRadius();
			double startAngle = b.getRotationAngle()-b.getSightAngle()/2.0+b.getSightAreaWidth()*(e);
			Color seenColor = eyesInput[e].getY();
			glColor3d(seenColor.getRed(), seenColor.getGreen(), seenColor.getBlue());
			glBegin(GL_TRIANGLE_FAN);
			glVertex2d(0, 0);
			for (int i = 0; i < 8; i++) {
				double angle = startAngle+i*b.getSightAreaWidth()/7;
				double radians = Math.toRadians(angle);
				glVertex2d(Math.cos(radians)*length, Math.sin(radians)*length);
			}
			glVertex2d(0, 0);
			glEnd();
		}
		glPopMatrix();
	}
	
	private static void drawCadavers(ArrayList<Cadaver> cadavers) {
		for (Cadaver c : cadavers) {
			drawFilledCircle(c.getXCoordinate(), c.getYCoordinate(), c.getRadius(), c.getColor());
		}
	}
	
	public static int getNumberOfSavedCircleShapes() {
		return radius_circleV_map.size();
	}
	
	public static void resetSavedCircleShapes() {
		radius_circleV_map = new HashMap<Double, float[][]>();
	}
	
	public static void drawFilledConvexPolygon(double x_position, double y_position, float[][] vertices, Color color) {
		glColor3d(color.getRed(), color.getGreen(), color.getBlue());
		glPushMatrix();
		glTranslated(x_position, y_position, 0);
		// draw the oval using line segments
		glBegin(GL_TRIANGLE_FAN);
		for (int i = 0; i < vertices.length; i++) {
			glVertex2f(vertices[i][0], vertices[i][1]);
		}
		glEnd();
		glPopMatrix();
	}
	
	public static void drawFilledConvexPolygon(double x_position, double y_position, double[] xVertices, double[] yVertices, Color color) {
		glColor3d(color.getRed(), color.getGreen(), color.getBlue());
		glPushMatrix();
		glTranslated(x_position, y_position, 0);
		// draw the oval using line segments
		glBegin(GL_TRIANGLE_FAN);
		for (int i = 0; i < xVertices.length; i++) {
			glVertex2d(xVertices[i], yVertices[i]);
		}
		glEnd();
		glPopMatrix();
	}
	
	public static void drawFilledCircle(double x_position, double y_position, double radius, Color color) {
		float[][] bodyVertices;
		if (radius_circleV_map.containsKey(radius)) {
			bodyVertices = radius_circleV_map.get(radius);
		} else {
			bodyVertices = generateCircleVertices((float)radius*2, 20);
			radius_circleV_map.put(radius, bodyVertices);
		}
		drawFilledConvexPolygon(x_position, y_position, bodyVertices, color);
	}
	
	public static void drawPlantGrid(PlantGrid pgrid) {
		PlantBox[][] boxes = pgrid.getGrid();
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[0].length; j++) {
				Plant p = boxes[i][j].getPlant();
				if (p != null) {
					drawFilledCircle(p.getXCoordinate(), p.getYCoordinate(), p.getRadius(), Plant.COLOR);
				}
			}
		}
	}
	
	public static void drawRocks(Rock[] rocks) {
		for (int i = 0; i < rocks.length; i++) {
			Rock r = rocks[i];
			Pair<Double,Double> topleft = r.getTopLeftBound();
			Pair<Double,Double> bottomright = r.getBottomRightBound();
			drawFilledConvexPolygon(0, 0, r.getWorldPoints().getX(), r.getWorldPoints().getY(), Rock.COLOR);
		}
	}
	
	public static void setWorld(World newWorld) {
		world = newWorld;
		radius_circleV_map = new HashMap<Double, float[][]>();
	}
	
	public static void draw() {
		drawBackground();
		drawCadavers(world.getCadavers());
		drawPlantGrid(world.getPlantGrid());
		Creature selectedCreature = ViewControl.getSelectedCreature();
		if (selectedCreature != null) {
			drawSpecificCreatureInfo(selectedCreature);
		}
		drawCreatures(world.getCreatures());
		drawRocks(world.getRockSystem().getRocks());
	}
	
	public static void drawBackground() {
		glColor3d(1, 1, 1);
		glBegin(GL_TRIANGLE_FAN);
		glVertex2d(0, 0);
		glVertex2d(World.SIZE, 0);
		glVertex2d(World.SIZE, World.SIZE);
		glVertex2d(0, World.SIZE);
		glEnd();
	}
	
	public static float[][] generateCircleVertices(float d, int n) {
		return generateOvalVertices(d, d, n);
	}
	
	public static float[][] generateOvalVertices(float w, float h, int n) {
		float theta, angle_increment;
		float PI_2 = (float) Math.PI * 2;
		float x, y;
		if (n <= 0) n = 1;
		angle_increment = PI_2 / n;
		float[][] vertices = new float[n][2];
		int i = 0;
		for (theta = 0.0f; theta < PI_2; theta += angle_increment) {
			vertices[i][0] = (float) (w / 2 * Math.cos(theta));
			vertices[i][1] = (float) (h / 2 * Math.sin(theta));
			i++;
			
		}
		return vertices;
	}

}
