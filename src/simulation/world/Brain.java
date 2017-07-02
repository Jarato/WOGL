package simulation.world;

import javafx.scene.paint.Color;
import pdf.ai.nnetwork.IOINeuralNet;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.Consts;

public class Brain extends IOINeuralNet {
	class InputMask {
    	/**
    	 * X: distance<br>
    	 * Y: color
    	 */
    	final Pair<Double, Color>[] eyesInputs = new Pair[Consts.CREATURE.NUMBER_OF_SIGHT_AREAS];
    	/**
         * 0 = front<br>
         * 1 = right<br>
         * 2 = back<br>
         * 3 = left
         */
    	final boolean[] sidesTouched = new boolean[] {false,false,false,false};
        boolean gotHurt = false;
        double stomachPercent = 1.0;
        double lifePercent = 1.0;
    }

	private final InputMask inputMask = new InputMask();

	public InputMask getInputMask() {
		return inputMask;
	}

	public Brain(int numberOfInputCells, int numberOfInterCells, int numberOfOutputCells) {
		super(numberOfInputCells, numberOfInterCells, numberOfOutputCells);
	}

	public void applyInputMask() {
		int pos;
		for (int i = 0; i < inputMask.eyesInputs.length; i++) {
			//distance
			pos= i*4;
			setInputValue(0+pos, Math.pow(Consts.CREATURE.SIGHT_RANGE-inputMask.eyesInputs[i].getX()/Consts.CREATURE.SIGHT_RANGE, 2.0));
	        setInputValue(1+pos, 1.0-inputMask.eyesInputs[i].getY().getRed());
	        setInputValue(2+pos, 1.0-inputMask.eyesInputs[i].getY().getGreen());
	        setInputValue(3+pos, 1.0-inputMask.eyesInputs[i].getY().getBlue());
		}
	    pos = getInputMask().eyesInputs.length*4;
	    for (int i = 0; i < getInputMask().sidesTouched.length; i++) {
	        setInputValue(pos+i,(inputMask.sidesTouched[i]?1.0:0.0));
	    }
	    pos = pos+getInputMask().sidesTouched.length;
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
