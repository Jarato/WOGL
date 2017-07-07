package main.simulation.world;

import javafx.scene.paint.Color;
import pdf.ai.nnetwork.IOINeuralNet;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class Brain extends IOINeuralNet {
	//CONSTS
	public static final int NUMBER_OF_SIGHT_AREAS = 7;
	public static final double SIGHT_RANGE = 200;
	public static final double SIGHT_MAXANGLE = 150;
	public static final double SIGHT_AREA_WIDTH = SIGHT_MAXANGLE/NUMBER_OF_SIGHT_AREAS;
	public static final int NUMBER_OF_INPUTS = 3*4*NUMBER_OF_SIGHT_AREAS+3;
	public static final int NUMBER_OF_INTERCELLS = 25;
	public static final int NUMBER_OF_OUTPUTS = 9;
	
	public class InputMask {
    	/**
    	 * X: distance<br>
    	 * Y: color
    	 */
    	public final Pair<Double, Color>[] eyesInputPlant = new Pair[NUMBER_OF_SIGHT_AREAS];
    	public final Pair<Double, Color>[] eyesInputCreature = new Pair[NUMBER_OF_SIGHT_AREAS];
    	public final Pair<Double, Color>[] eyesInputWall = new Pair[NUMBER_OF_SIGHT_AREAS];
        public boolean gotHurt = false;
        public double stomachPercent = 1.0;
        public double lifePercent = 1.0;
        
        private InputMask() {
        	for (int i = 0; i < eyesInputPlant.length; i++) {
        		eyesInputPlant[i] = new Pair<Double, Color>(0.0,Color.WHITE);
        	}
        	for (int i = 0; i < eyesInputCreature.length; i++) {
        		eyesInputCreature[i] = new Pair<Double, Color>(0.0,Color.WHITE);
        	}
        	for (int i = 0; i < eyesInputPlant.length; i++) {
        		eyesInputWall[i] = new Pair<Double, Color>(0.0,Color.WHITE);
        	}
        }
        
        public void resetEyesInput() {
        	for (int i = 0; i < eyesInputPlant.length; i++) {
        		eyesInputPlant[i].set(SIGHT_RANGE,World.NOTHING_COLOR);
        	}
        	for (int i = 0; i < eyesInputCreature.length; i++) {
        		eyesInputCreature[i].set(SIGHT_RANGE,World.NOTHING_COLOR);
        	}
        	for (int i = 0; i < eyesInputPlant.length; i++) {
        		eyesInputWall[i].set(SIGHT_RANGE,World.NOTHING_COLOR);
        	}
        }
    }

	private final InputMask inputMask = new InputMask();

	public InputMask getInputMask() {
		return inputMask;
	}

	public Brain(int numberOfInputCells, int numberOfInterCells, int numberOfOutputCells) {
		super(numberOfInputCells, numberOfInterCells, numberOfOutputCells);
	}

	public void applyInputMask() {
		int pos = 0;
		for (int i = 0; i < inputMask.eyesInputPlant.length; i++) {
			//distance
			double temp = (SIGHT_RANGE-inputMask.eyesInputPlant[i].getX())/SIGHT_RANGE;
			setInputValue(pos, temp*temp);
			pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputPlant[i].getY().getRed());
	        pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputPlant[i].getY().getGreen());
	        pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputPlant[i].getY().getBlue());
	        pos++;
		}
		for (int i = 0; i < inputMask.eyesInputCreature.length; i++) {
			//distance
			double temp = (SIGHT_RANGE-inputMask.eyesInputCreature[i].getX())/SIGHT_RANGE;
			setInputValue(pos, temp*temp);
			pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputCreature[i].getY().getRed());
	        pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputCreature[i].getY().getGreen());
	        pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputCreature[i].getY().getBlue());
	        pos++;
		}
		for (int i = 0; i < inputMask.eyesInputPlant.length; i++) {
			//distance
			pos= i*4;
			double temp = (SIGHT_RANGE-inputMask.eyesInputWall[i].getX())/SIGHT_RANGE;
			setInputValue(pos, temp*temp);
			pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputWall[i].getY().getRed());
	        pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputWall[i].getY().getGreen());
	        pos++;
	        setInputValue(pos, 1.0-inputMask.eyesInputWall[i].getY().getBlue());
	        pos++;
		}
	    setInputValue(pos, 1.0-inputMask.stomachPercent);
	    setInputValue(pos+1, 1.0-inputMask.lifePercent);
	    setInputValue(pos+2, (inputMask.gotHurt?1.0:0.0));
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