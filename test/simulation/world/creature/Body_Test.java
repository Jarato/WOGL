package simulation.world.creature;

import static org.junit.Assert.*;

import org.junit.Test;

//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import pdf.ai.dna.DNA;

public class Body_Test {
	
	@Test
	public void testRotate() {
		Body body = new Body(0.0,0,0);
		body.rotate(20);
		assertEquals(20 ,body.getRotationAngle(), 0.0001);
		body.rotate(-30);
		assertEquals(350, body.getRotationAngle(), 0.0001);
	}

	@Test
	public void testAccelerateRotationDirect() {
		Body body = new Body(0.0,0,0);
		body.accelerateRotationDirect(2);
		assertEquals(2, body.getRotationVelocity(), 0.0001);
		body.accelerateRotationDirect(-3);
		assertEquals(-1, body.getRotationVelocity(), 0.0001);
	}

	@Test
	public void testAccelerateRotationPercent() {
		Body body = new Body(0.0,0,0);
		body.accelerateRotationDirect(2);
		body.accelerateRotationPercent(2);
		assertEquals(4, body.getRotationVelocity(), 0.0001);
		body.accelerateRotationPercent(-0.25);
		assertEquals(-1, body.getRotationVelocity(), 0.0001);
	}

	@Test
	public void testAcceleratePercent() {
		Body body = new Body(0,0,0);
		body.accelerateDirect(2, 3);
		body.acceleratePercent(2);
		assertEquals(4, body.getVelocity().getX(), 0.0001);
		assertEquals(6, body.getVelocity().getY(), 0.0001);
		body.acceleratePercent(-0.25);
		assertEquals(-1, body.getVelocity().getX(), 0.0001);
		assertEquals(-1.5, body.getVelocity().getY(), 0.0001);
	}

	@Test
	public void testAccelerateDirect() {
		Body body = new Body(0,0,0);
		body.accelerateDirect(3, 2);
		assertEquals(3, body.getVelocity().getX(), 0.0001);
		assertEquals(2, body.getVelocity().getY(), 0.0001);
		body.accelerateDirect(-2, -3);
		assertEquals(1, body.getVelocity().getX(), 0.0001);
		assertEquals(-1, body.getVelocity().getY(), 0.0001);
	}

	@Test
	public void testAccelerateAngle() {
		Body body = new Body(0,0,0);
		body.accelerateAngle(0, 1);
		assertEquals(1, body.getVelocity().getX(), 0.0001);
		assertEquals(0, body.getVelocity().getY(), 0.0001);
		body = new Body(0,0,0);
		body.accelerateAngle(90, 1);
		assertEquals(0, body.getVelocity().getX(), 0.0001);
		assertEquals(1, body.getVelocity().getY(), 0.0001);
		body = new Body(0,0,0);
		body.accelerateAngle(180, 1);
		assertEquals(-1, body.getVelocity().getX(), 0.0001);
		assertEquals(0, body.getVelocity().getY(), 0.0001);
		body = new Body(0,0,0);
		body.accelerateAngle(270, 1);
		assertEquals(0, body.getVelocity().getX(), 0.0001);
		assertEquals(-1, body.getVelocity().getY(), 0.0001);
		body = new Body(0,0,0);
		body.accelerateAngle(45, Math.sqrt(2));
		assertEquals(1, body.getVelocity().getX(), 0.0001);
		assertEquals(1, body.getVelocity().getY(), 0.0001);
	}

	@Test
	public void testChangeStomachContent() {
		Body body = new Body(0,0,0);
		body.changeStomachContent(5);
		body.checkStomachBounds();
		assertEquals(5, body.getStomach().getX(), 0.0001);
		body.changeStomachContent(-10);
		body.checkStomachBounds();
		assertEquals(0, body.getStomach().getX(), 0.0001);
		body.changeStomachContent(body.getStomach().getY()+15);
		body.checkStomachBounds();
		assertEquals(body.getStomach().getY(), body.getStomach().getX(), 0.0001);
	}
	
	@Test
	public void testCalculateAngleRotation() {
		Body bodyTest = new Body(0,0,0);
		bodyTest.setRotationAngle(90);
		Body bodyOther = new Body(0, 0, 10);
		assertEquals(0, bodyTest.calculateAngleToRotation(bodyOther), 0.0001);
		bodyTest.setRotationAngle(0);
		assertEquals(90, bodyTest.calculateAngleToRotation(bodyOther), 0.0001);
		bodyOther.setCoordinates(-10, 0);
		assertEquals(180, bodyTest.calculateAngleToRotation(bodyOther), 0.0001);
	}

	@Test
	public void testChangeLife() {
		Body body = new Body(0,0,0);
		body.changeLife(5);
		body.checkLifeBounds();
		assertEquals(5, body.getLife().getX(), 0.0001);
		body.changeLife(-10);
		body.checkLifeBounds();
		assertEquals(0, body.getLife().getX(), 0.0001);
		body.changeLife(body.getLife().getY()+15);
		body.checkLifeBounds();
		assertEquals(body.getLife().getY(), body.getLife().getX(), 0.0001);
	}

	@Test
	public void testGetNumberOfNeededGenes() {
		Body body = new Body(0,0,0);
		assertEquals(Body.NUMBER_OF_GENES, body.getNumberOfNeededGenes());
	}

	@Test
	public void testCompoundDNA() {
		Body body = new Body(0,0,0);
		DNA dna = new DNA(new double[] {10,30,20, 15, 3, 11, 50}, 0, 255);
		body.compoundDNA(dna);
		assertEquals(0.039215687, body.getColor().getRed(), 0.0001);
		assertEquals(0.039215687*3, body.getColor().getGreen(), 0.0001);
		assertEquals(0.039215687*2, body.getColor().getBlue(), 0.0001);
	}

}
