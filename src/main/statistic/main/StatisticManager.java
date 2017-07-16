/**
 * 
 */
package main.statistic.main;

import java.util.ArrayList;

/**
 * @author Pagad
 *
 */
public class StatisticManager {
	
	static ArrayList<Statistic> allStatistics = new ArrayList<Statistic>();

	
	public static void createStatistic(String name) {
		Statistic statistic = new Statistic(name);
		allStatistics.add(statistic);
	}
	
}
