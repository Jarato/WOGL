package test.statstic.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import main.simulation.world.World;
import main.statistic.main.Datapack.DATATYPE;
import main.statistic.main.Statistic;
import main.statistic.main.StatisticManager;

public class CreateTests {

	
	
	@Test
	public void createStatsticsTest() {
		Statistic s1 = StatisticManager.createStatistic("firstStatistic");
		World world = new World();

		
		s1.addData(world,"getWorldSeed", DATATYPE.LONG);
		
		s1.observe();
		s1.observe();
		s1.observe();
		s1.observe();
		s1.observe();
		
		System.out.println(s1.toTable());
		
	}
	
	
	/*	try {
	Method m= world.getClass().getMethod("getWorldSeed");
} catch (NoSuchMethodException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (SecurityException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
try {
	System.out.println("invoke: "+(m.invoke(world)));
} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
System.out.println("normal: "+ world.getWorldSeed());
*/
	
	
}
