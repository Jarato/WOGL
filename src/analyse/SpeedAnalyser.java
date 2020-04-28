package analyse;

import java.util.Arrays;
import java.util.HashMap;

import pdf.util.Pair;

public class SpeedAnalyser
{
	HashMap<String, Pair<Integer, Double>>	speedData;

	public SpeedAnalyser()
	{
		speedData = new HashMap<String, Pair<Integer, Double>>();
	}

	public void addData(String text, double time)
	{
		if (speedData.containsKey(text))
		{
			Pair<Integer, Double> pair = speedData.get(text);
			pair.set(pair.getX() + 1, pair.getY() + time);
		}
		else
		{
			speedData.put(text, new Pair<Integer, Double>(1, time));
		}
	}

	public String toString()
	{
		// System.out.println("test");
		String str = "Speed - Analyser - Results:\n";
		String[] texts = new String[0];
		texts = speedData.keySet().toArray(texts);
		Arrays.sort(texts);
		// System.out.println(texts.length);
		for (int i = 0; i < texts.length; i++)
		{
			Pair<Integer, Double> pair = speedData.get(texts[i]);
			str += texts[i] + ":\t" + pair.getY() / pair.getX() + "\n";
		}
		return str;
	}
}
