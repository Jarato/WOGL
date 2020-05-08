package simulation.creature;

import javafx.scene.paint.Color;
//import pdf.ai.nnetwork.cell.activationfunction.ReLU;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.World;

public class Brain extends IOINetwork {
	//CONSTS
	public static final int NUMBER_OF_SIGHT_AREAS = 9;
	public static final double SIGHT_RANGE = 400;
	public static final int NUMBER_OF_COLLISION_DETECTION_AREAS = 4;
	public static final double COLLISION_DETECTION_AREA_ANGLE = 360.0/NUMBER_OF_COLLISION_DETECTION_AREAS;
	public static final int NUMBER_OF_INPUTS = 4*NUMBER_OF_SIGHT_AREAS+NUMBER_OF_COLLISION_DETECTION_AREAS+4;
	//    3 (view-kind: creature, plant, rock) * 4 (information: r,g,b + distance) *NUMBER_OF_SIGHT_AREAS + NUMBER_OF_COLLISION_DETECTION_AREAS + 4 (stomach, life, hurt, velocity)
	public static final int NUMBER_OF_INTERCELLS = 30;
	public static final int NUMBER_OF_OUTPUTS = 9;
	
	public class InputMask {
    	/**
    	 * X: distance<br>
    	 * Y: color
    	 */
    	public final Pair<Double, Color>[] eyesInput = new Pair[NUMBER_OF_SIGHT_AREAS];
    	public double[] collision = new double[NUMBER_OF_COLLISION_DETECTION_AREAS];
        public boolean gotHurt = false;
        public double stomachPercent = 1.0;
        public double lifePercent = 1.0;
        public double movementSpeed = 0;
        
        private InputMask() {
        	for (int i = 0; i < eyesInput.length; i++) {
        		eyesInput[i] = new Pair<Double, Color>(0.0,Color.WHITE);
        	}
        }
        
        public void resetEyesInput() {
        	for (int i = 0; i < eyesInput.length; i++) {
        		eyesInput[i].set(SIGHT_RANGE,World.NOTHING_COLOR);
        	}
        }
        
        public void reset_Col_GotHurt() {
        	gotHurt = false;
        	collision = new double[NUMBER_OF_COLLISION_DETECTION_AREAS];
        }
    }

	private final InputMask inputMask = new InputMask();
	
	public InputMask getInputMask() {
		return inputMask;
	}

	public Brain(int numberOfInputCells, int numberOfInterCells, int numberOfOutputCells) {
		super(numberOfInputCells, numberOfInterCells, numberOfOutputCells);
	}
	
	public double transformEyeInputRangeIntoActivation(double input) {
		return -1.0/SIGHT_RANGE * input + 1;
		//return Math.log(SIGHT_RANGE/(input+1))*0.166834544833271+0.000416913215098139;
	}
	
	private int setEyeInput(int pos, Pair<Double, Color> eyeInput) {
		//double temp = (SIGHT_RANGE-eyeInput.getX())/SIGHT_RANGE;
		setInputValue(pos, transformEyeInputRangeIntoActivation(eyeInput.getX()));
		pos++;
		Color seenC = eyeInput.getY();
        setInputValue(pos, 1.0-seenC.getRed());
        pos++;
        setInputValue(pos, 1.0-seenC.getGreen());
        pos++;
        setInputValue(pos, 1.0-seenC.getBlue());
        pos++;
		return pos;
	}

	public void applyInputMask() {
		int pos = 0;
		for (int i = 0; i < inputMask.eyesInput.length; i++) {
			// PLANTS
			pos = setEyeInput(pos,inputMask.eyesInput[i]);
		}
		for (int i = 0; i < inputMask.collision.length; i++) {
			// COLLISION
			setInputValue(pos, inputMask.collision[i]);
			pos++;
		}
		// OTHER
	    setInputValue(pos, 1.0-inputMask.stomachPercent); //STOMACH
	    setInputValue(pos+1, 1.0-inputMask.lifePercent);  //LIFE
	    setInputValue(pos+2, (inputMask.gotHurt?1.0:0.0)); //GOT HURT
	    setInputValue(pos+3, 1);		//CONSTANT
	}


	public int[] interpretOutput(){
		/*
		 * 0 means no action, every number is a specific action.
		 */
		int[] interpreted = new int[6];
		double[] output = this.getOutputValues();
		Pair<Integer,Double> erg;
		erg = UtilMethods.getMaximum(output[0],output[1]);
		if (erg.getY() > 0.0) interpreted[0] = erg.getX()+1;
		erg = UtilMethods.getMaximum(output[2],output[3]);
		if (erg.getY() > 0.0) interpreted[1] = erg.getX()+1;
		erg = UtilMethods.getMaximum(output[4],output[5]);
		if (erg.getY() > 0.0) interpreted[2] = erg.getX()+1;
		if (output[6] > 0) interpreted[3] = 1;
		if (output[7] > 0) interpreted[4] = 1;
		if (output[8] > 0) interpreted[5] = 1;
		return interpreted;
	}
}