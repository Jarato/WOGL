package gui.lwjgl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import java.io.File;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import analyse.AnalyseStrg;
import gui.lwjgl.ViewControl;
import simulation.World;
import simulation.environment.rocks.Rock;

public class lwjgl_gui {
	public static boolean paused;
	public static boolean fastforward;
	private static World world;

	/*
	 * interesting world-seeds
	 * 
	 */
	
	
	public static void main(String[] args) {
		simulation(1);
		System.exit(0);
	}
	
	public static void simulation() {
		initDisplay();
		AnalyseStrg.getSpeedAnalyser().clearSpeedData();
		paused = false;
		fastforward = false;
		glClearColor((float)Rock.COLOR.getRed(), (float)Rock.COLOR.getGreen(), (float)Rock.COLOR.getBlue(), 1);
		world = new World(); // PUT IN SEEDS HERE
		world.initialize();
		DrawWorld.setWorld(world);
		ViewControl.init(world);
		int step = 0;
		while (!Display.isCloseRequested()) {
			if (Display.wasResized()) {
				ViewControl.resizeGL();
			}
			Display.update();
			glClear(GL_COLOR_BUFFER_BIT);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnable(GL_BLEND);
			processKeyboard();
			processMouse();
			// DRAW
			if (!paused) {
				long timestamp = System.currentTimeMillis();
				world.step();
				long second = System.currentTimeMillis()-timestamp;
				AnalyseStrg.getSpeedAnalyser().addData("Step", second);
				if (fastforward) {
					for (int i = 0; i < 99; i++) {
						world.step();
					}
					fastforward = false;
				}
			}
			long timestamp = System.currentTimeMillis();
			DrawWorld.draw();
			long second = System.currentTimeMillis()-timestamp;
			AnalyseStrg.getSpeedAnalyser().addData("Draw", second);
			if (step % 1000 == 0) {
				//System.out.println(DrawWorld.getNumberOfSavedCircleShapes());
				DrawWorld.resetSavedCircleShapes();
				AnalyseStrg.writeFile();
				//System.out.println("Analysed!");
			}
			step++;
			Display.sync(120);
		}
		Display.destroy();	
	}
	
	public static void simulation(int number) {
		initDisplay();
		AnalyseStrg.getSpeedAnalyser().clearSpeedData();
		paused = false;
		fastforward = false;
		glClearColor((float)Rock.COLOR.getRed(), (float)Rock.COLOR.getGreen(), (float)Rock.COLOR.getBlue(), 1);
		world = new World(42l); // PUT IN SEEDS HERE
		world.initialize();
		DrawWorld.setWorld(world);
		ViewControl.init(world);
		int step = 0;
		while (!Display.isCloseRequested() && step < 5001) {
			if (Display.wasResized()) {
				ViewControl.resizeGL();
			}
			Display.update();
			glClear(GL_COLOR_BUFFER_BIT);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnable(GL_BLEND);
			processKeyboard();
			processMouse();
			// DRAW
			if (!paused) {
				long timestamp = System.currentTimeMillis();
				world.step();
				long second = System.currentTimeMillis()-timestamp;
				AnalyseStrg.getSpeedAnalyser().addData("Step", second);
				if (fastforward) {
					for (int i = 0; i < 99; i++) {
						world.step();
					}
					fastforward = false;
				}
			}
			long timestamp = System.currentTimeMillis();
			DrawWorld.draw();
			long second = System.currentTimeMillis()-timestamp;
			AnalyseStrg.getSpeedAnalyser().addData("Draw", second);
			if (step % 1000 == 0) {
				//System.out.println(DrawWorld.getNumberOfSavedCircleShapes());
				DrawWorld.resetSavedCircleShapes();
				AnalyseStrg.writeFile("RP3N_5k_"+number);
				//System.out.println("Analysed!");
			}
			step++;
			Display.sync(120);
		}
		Display.destroy();
	}

	public static void processKeyboard() {
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			fastforward = true;
		}
		while (Keyboard.next()) {
			// If key up was pressed move up
			if (Keyboard.getEventKey() == Keyboard.KEY_P) {
				if (Keyboard.getEventKeyState()) {
					paused = !paused;
				}
			}
		}
	}

	public static void processMouse() {
		// drag the square with left mouse button
		while (Mouse.next()) {
			if (Mouse.getEventButton() == 0) {
				if (Mouse.getEventButtonState()) {
					// System.out.println("Mousebutton 0 first press");
					ViewControl.printPosition();
				}
			}
			if (Mouse.getEventButton() == 1) {
				if (Mouse.getEventButtonState()) {
					// System.out.println("Mousebutton 0 first press");
					ViewControl.initDragView();
				} else {
					ViewControl.finaliseDragView();
				}
			}// grow and shrink with wheel
			if (Mouse.hasWheel()) {
				int wheelDelta = Mouse.getEventDWheel();
				if (wheelDelta != 0) {
					//System.out.println(Mouse.getX()+"\t"+Mouse.getY());
					if (wheelDelta > 0) ViewControl.zoom(0.85f);
					else if (wheelDelta < 0) ViewControl.zoom(1.15f);
				}
			}
		}
		if (Mouse.isButtonDown(1)) {
			// squareX = Mouse.getX() ;
			// squareY = Mouse.getY() ;
			ViewControl.dragView();
			// System.out.println("MouseButton 0 holding");
		}
	
		
	}

	private static void initDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(900, 900));
			Display.setTitle("WOGL - Alpha");
			Display.setResizable(true);
			Display.create();
			Keyboard.create();
			Keyboard.enableRepeatEvents(false);
		} catch (LWJGLException e) {
			System.err.println("Display wasn't initialized correctly.");
			System.exit(1);
		}
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity(); // Resets any previous projection matrices
		glOrtho(0, 4000, 4000, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

}
