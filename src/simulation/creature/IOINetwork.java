package simulation.creature;

import java.util.Arrays;
import java.util.stream.IntStream;

import pdf.ai.dna.DNA;
import pdf.ai.dna.Evolutionizable;
import pdf.ai.nnetwork.NNetWork;

public class IOINetwork implements NNetWork,Evolutionizable{
	private DNA dna;
	private int numberOfNeededGenes;
	private double[] IN_vector;
	private double[][] IM_matrix;
	private double[] MID_vector;
	private double[][] MM_matrix;
	private double[] OUT_vector;
	private double[][] MO_matrix;
	
	public IOINetwork(int numIN, int numMID, int numOUT) {
		IN_vector = new double[numIN+1];
		IM_matrix = new double[numMID][numIN+1];
		MID_vector = new double[numMID];
		MM_matrix = new double[numMID][numMID];
		OUT_vector = new double[numOUT];
		MO_matrix = new double[numOUT][numMID+1];
		numberOfNeededGenes = (numIN+1)*numMID + numMID*numMID + (numMID+1)*numOUT;
		dna = new DNA(numberOfNeededGenes);
		dna.setRandom();
		compoundDNA();
	}
	
	public IOINetwork(DNA newDna, int numIN, int numMID, int numOUT) {
		IN_vector = new double[numIN+1];
		IM_matrix = new double[numMID][numIN+1];
		MID_vector = new double[numMID];
		MM_matrix = new double[numMID][numMID];
		OUT_vector = new double[numOUT];
		MO_matrix = new double[numOUT][numMID+1];
		numberOfNeededGenes = (numIN+1)*numMID + numMID*numMID + (numMID+1)*numOUT;
		dna = newDna;
		compoundDNA();
	}
	
	public static void main(String[] args) {
		//System.out.println("test");
		IOINetwork network = new IOINetwork(5, 40, 1);
		double[] input = new double[] {1,0,-1,0.5,-0.5};
		network.setInputValues(input);
		network.calculateNet();
		double[] output = network.getOutputValues();
		System.out.println(output[0]);
		for (int i = 0; i < 1000; i ++) {
			network.setInputValues(input);
			network.calculateNet();
			output = network.getOutputValues();
			System.out.println(output[0]);
		}
	}
	
	private double[] activationFunction(double[] vector) {
		double[] res = new double[vector.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = singleActivation(vector[i]);
		}
		return res;
	}
	
	private double singleActivation(double value) {
		return value/(1+Math.abs(value));
	}
	
	@Override
	public void compoundDNA() {
		double[] values = dna.getGeneValues();
		int pos = 0;
		for (int i = 0; i < IM_matrix.length; i++) {
			for (int j = 0; j < IM_matrix[i].length; j++) {
				IM_matrix[i][j] = values[pos];
				pos++;
			}
		}
		for (int i = 0; i < MM_matrix.length; i++) {
			for (int j = 0; j < MM_matrix[i].length; j++) {
				MM_matrix[i][j] = values[pos];
				pos++;
			}
		}
		for (int i = 0; i < MO_matrix.length; i++) {
			for (int j = 0; j < MO_matrix[i].length; j++) {
				MO_matrix[i][j] = values[pos];
				pos++;
			}
		}
		//System.out.println("pos "+pos);
	}
	
	public double[] multiply(double[][] matrix, double[] vector) {
	    int rows = matrix.length;
	    int columns = matrix[0].length;

	    double[] result = new double[rows];

	    for (int row = 0; row < rows; row++) {
	        double sum = 0;
	        for (int column = 0; column < columns; column++) {
	            sum += matrix[row][column]
	                    * vector[column];
	        }
	        result[row] = sum;
	    }
	    return result;
	}
	
	public double[] multiply_stream(double[][] matrix, double[] vector) {
	    return Arrays.stream(matrix)
	                 .mapToDouble(row -> 
	                    IntStream.range(0, row.length)
	                             .mapToDouble(col -> row[col] * vector[col])
	                             .sum()
	                 ).toArray();
	}

	@Override
	public void compoundDNA(DNA newDNA) {
		dna = newDNA;
		compoundDNA();
	}

	@Override
	public DNA getDNA() {
		return dna;
	}

	@Override
	public int getNumberOfNeededGenes() {
		return numberOfNeededGenes;
	}

	@Override
	public void calculateNet() {
		int lastpos = IN_vector.length;
		// add 1 for bias
		double[] in_bias = Arrays.copyOf(IN_vector, lastpos+1);
		in_bias[lastpos] = 1;
		double[] mid = multiply(IM_matrix, in_bias);
		for (int i = 0; i < mid.length; i++) {
			mid[i] += MID_vector[i];
		}
		mid = activationFunction(mid);
		MID_vector = multiply(MM_matrix, mid);
		lastpos = mid.length;
		double[] mid_bias = Arrays.copyOf(mid, lastpos+1);
		mid_bias[lastpos] = 1;
		OUT_vector = activationFunction(multiply(MO_matrix, mid_bias));
	}

	@Override
	public double getOutputValue(int indexOfOutput) {
		return OUT_vector[indexOfOutput];
	}

	@Override
	public double[] getOutputValues() {
		return OUT_vector;
	}

	@Override
	public void setInputValue(int indexOfInput, double valueOfInput) {
		IN_vector[indexOfInput] = valueOfInput;
	}

	@Override
	public void setInputValues(double... newInputs) {
		IN_vector = newInputs;
	}

}
