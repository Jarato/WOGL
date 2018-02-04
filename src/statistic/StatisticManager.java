/**
 * 
 */
package statistic;

import java.util.ArrayList;

/**
 * @author Pagad
 *
 */
public class StatisticManager {

	static private ArrayList<Statistic> allStatistics = new ArrayList<Statistic>();

	/**
	 * create a new Statistic and store it
	 * 
	 * @param name
	 *            your name for this Statistic
	 * 
	 * @return the new Statistic
	 * 
	 */

	public static Statistic createStatistic(String name) {
		Statistic statistic = new Statistic(name);
		allStatistics.add(statistic);
		return statistic;
	}

	/**
	 * 
	 * @param name
	 * @return the statistic with the @param name
	 * 
	 */
	public static Statistic getStasticByName(String name) {
		Statistic s = null;
		for (Statistic ss : allStatistics) {
			if (ss.getName().equals(name)) {
				s = ss;
			}
		}
		return s;
	}

	/**
	 * @param statistic
	 * @return
	 */
	public static String StatisticToTable(Statistic statistic) {
		String res = statistic.getName();

		ArrayList<Datapack> list = statistic.getDataList();
		for (int j = 0; j < list.get(0).getLenght(); j++) {
			res += "\n" + j + ": ";
			for (Datapack p : list) {
				res += p.getName() + ": " + p.getList(j) + ",  ";
			}
		}

		return res;
	}

}
