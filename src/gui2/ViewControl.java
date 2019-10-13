package gui2;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Point;
import org.lwjgl.util.glu.GLU;

import pdf.util.Pair;
import simulation.world.World;
import simulation.world.creature.Creature;

import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glScaled;

public class ViewControl {
	private static World world;
	private static Creature selectedCreature;
	private static boolean followSelectedCreature;
	private static float ZOOM_MIN = 0.1f;
	private static float ZOOM_MAX = 5;
	private static float zoom = 1;
	private static int mouseDragFirstPosX, mouseDragFirstPosY;
	private static float cameraX, cameraY;
	private static float tempCameraX, tempCameraY;
	private static int window_width, window_height;
	private static boolean viewDragged = false;

	public static void resizeGL() {
		window_width = Display.getWidth();
		window_height = Display.getHeight();
		//System.out.println(cameraX+"\t"+cameraY);
		glViewport(0, 0, window_width, window_height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-cameraX*zoom, (window_width-cameraX)*zoom, (window_height-cameraY)*zoom, -cameraY*zoom, -1, 1);
		//glPushMatrix();
		//glViewport(0, 0, Display.getWidth(), Display.getHeight());
		//glMatrixMode(GL_MODELVIEW);
		//glLoadIdentity();
		//glPushMatrix();
	}
	
	public static Creature getSelectedCreature() {
		if (selectedCreature != null && !selectedCreature.isAlive()) {
			selectedCreature = null;
		}
		return selectedCreature;
	}
	
	public static void initDragView() {
		Point cursorP = getCursorPosition();
		mouseDragFirstPosX = cursorP.getX();
		mouseDragFirstPosY = cursorP.getY();
		viewDragged=true;
	}
	
	public static void dragView() {
		Point cursorP = getCursorPosition();
		tempCameraX = cameraX+cursorP.getX()-mouseDragFirstPosX;
		tempCameraY = cameraY+cursorP.getY()-mouseDragFirstPosY;
    	//glViewport(tempCameraX, tempCameraY, window_width, window_height);
    	glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-tempCameraX*zoom, (window_width-tempCameraX)*zoom, (window_height-tempCameraY)*zoom, -tempCameraY*zoom, -1, 1);
	}
	
	public static void init(World world) {
		ViewControl.world = world;
	}
	
	private static void restrictViewToWorld() {
		
	}
	
	public static void finaliseDragView() {
		cameraX = tempCameraX;
		cameraY = tempCameraY;
		viewDragged=false;
	}
	
	// ####################### ZOOM ##########################
	
	/*public void zoom(double xPos, double yPos, double amount) {
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
	}*/
	
	/*public Pair<Double,Double> getWorldPositionOf(double x, double y){
		return new Pair<Double,Double>((x-xWorldBound)/zoomD, (y-yWorldBound)/zoomD);
	}*/
	
	public static void printPosition() {
		Point cursorP = getCursorPosition();
		Pair<Float,Float> cursorInWP = getCursorInWorldPosition();
		//System.out.println("Cursor in window position:\t" + cursorP.getX() + "\t" + cursorP.getY());
		//System.out.println("Cursor in world position:\t" + cursorInWP.getX() + "\t" + cursorInWP.getY());
		Creature c = getCreatureOnPosition(cursorInWP.getX(), cursorInWP.getY());
		selectedCreature = c;
		//System.out.println(cameraX+"\t"+cameraY);
	}
	
	private static Creature getCreatureOnPosition(double worldX, double worldY) {
		ArrayList<Creature> creatures = world.getCreatures();	
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).getBody().distanceTo(new Pair<Double,Double>(worldX, worldY))<creatures.get(i).getBody().getRadius()) {
				return creatures.get(i);
			}
		}
		return null;
	}
	
	public static Pair<Float,Float> getCursorInWorldPosition() {
		return new Pair<Float,Float>(((Mouse.getX()-cameraX)*zoom), (window_height-Mouse.getY()-cameraY)*zoom);
	}
	
	private static Point getCursorPosition() {
		return new Point(Mouse.getX(), (window_height-Mouse.getY()));
	}
	
	public static void zoom(float zoomMultiplier) {
		float newZoom = zoom*zoomMultiplier;
	    if (newZoom > ZOOM_MAX) newZoom = ZOOM_MAX;
	    else if (newZoom < ZOOM_MIN) newZoom = ZOOM_MIN;
	    if (zoom == newZoom) return;
	    //Pair<Float,Float> cursorInWorldPos = getCursorInWorldPosition();
	    Point cursorPosition = getCursorPosition();
	    //System.out.println(cursorInWorldPos.getX()+"\t"+cursorInWorldPos.getY());
	    
	    float trueMultiplier = newZoom/zoom;
	    if (viewDragged) {
	    	tempCameraX = (tempCameraX-cursorPosition.getX()*(1-trueMultiplier))/trueMultiplier;
		    tempCameraY = (tempCameraY-cursorPosition.getY()*(1-trueMultiplier))/trueMultiplier;
		    System.out.println("temp");
	    } else {
	    	cameraX = (cameraX-cursorPosition.getX()*(1-trueMultiplier))/trueMultiplier;
		    cameraY = (cameraY-cursorPosition.getY()*(1-trueMultiplier))/trueMultiplier;
	    }
	    //cursorInWorldPos = getCursorInWorldPosition();
	    //System.out.println(cursorInWorldPos.getX()+"\t"+cursorInWorldPos.getY());
	    zoom = newZoom;
	    //System.out.println(zoom);
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    //glOrtho(0,  0+Display.getWidth() * zoom , 0+Display.getHeight() * zoom, 0, 1, -1);
	    glOrtho(-cameraX*zoom, (window_width-cameraX)*zoom, (window_height-cameraY)*zoom, -cameraY*zoom, -1, 1);
	    
	}
}
