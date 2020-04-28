package simulation.world.creature;

import java.util.Random;

import org.junit.Test;

import pdf.ai.dna.DNA;
import simulation.World;
import simulation.creature.Creature;
import simulation.creature.Brain.InputMask;

public class Creature_Test {

	@Test
	public void testWorkBrain() {
		World testWorld = new World();
		Creature testCreature = new Creature(0, 50, 50, new Random());
		testCreature.getBody().setRadius(5);
		DNA viewedDNA = new DNA(testCreature.getNumberOfNeededGenes(),0, 1);
		double[] genes = viewedDNA.getGeneValues();
		for (int i = 0; i < 3; i++) {
			genes[genes.length-1-i] = 0.1+0.1*i;
		}
		Creature viewedCreature = new Creature(1, viewedDNA, 80, 50);
		viewedCreature.getBody().setRadius(5);
		testWorld.getCreatures().add(testCreature);
		testWorld.getCreatures().add(viewedCreature);
		testCreature.workBrain(testWorld);
		InputMask mask = testCreature.getBrain().getInputMask();
		//assertEquals(20, mask.eyesInputCreature[3].getX(), 0.0001);
		//assertEquals(0.3, mask.eyesInputCreature[3].getY().getRed(), 0.0001);
		//assertEquals(0.2, mask.eyesInputCreature[3].getY().getGreen(), 0.0001);
		//assertEquals(0.1, mask.eyesInputCreature[3].getY().getBlue(), 0.0001);
		
		viewedCreature.getBody().setCoordinates(50.001, 259.9);
		testCreature.workBrain(testWorld);
		mask = testCreature.getBrain().getInputMask();
		//assertEquals(199.9, mask.eyesInputCreature[6].getX(), 0.0001);
		//assertEquals(0.3, mask.eyesInputCreature[6].getY().getRed(), 0.0001);
		//assertEquals(0.2, mask.eyesInputCreature[6].getY().getGreen(), 0.0001);
		//assertEquals(0.1, mask.eyesInputCreature[6].getY().getBlue(), 0.0001);
		
		testCreature.getBody().rotate(180);
		viewedCreature.getBody().setCoordinates(49.999, 259.9);
		testCreature.workBrain(testWorld);
		//assertEquals(199.9, mask.eyesInputCreature[0].getX(), 0.0001);
		//assertEquals(0.3, mask.eyesInputCreature[0].getY().getRed(), 0.0001);
		//assertEquals(0.2, mask.eyesInputCreature[0].getY().getGreen(), 0.0001);
		//assertEquals(0.1, mask.eyesInputCreature[0].getY().getBlue(), 0.0001);
		for (int i = 0; i < 6; i++) {
			testCreature.getBody().rotate(-testCreature.getBody().getSightAreaWidth());
			testCreature.workBrain(testWorld);
			//System.out.println("i: "+i);
			//assertEquals(199.9, mask.eyesInputCreature[i+1].getX(), 0.0001);
			//assertEquals(0.3, mask.eyesInputCreature[i+1].getY().getRed(), 0.0001);
			//assertEquals(0.2, mask.eyesInputCreature[i+1].getY().getGreen(), 0.0001);
			//assertEquals(0.1, mask.eyesInputCreature[i+1].getY().getBlue(), 0.0001);
		}
	}

	@Test
	public void testWorkBody() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSplit() {
		//fail("Not yet implemented");
	}

}
