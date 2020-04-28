package analyse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AnalyseStrg
{
	private static SpeedAnalyser		speedAnalyser		= new SpeedAnalyser();
	private static String fileName = "DATA";

	public static void initFileName(String newFileName) {
		AnalyseStrg.fileName = newFileName;
	}

	public static SpeedAnalyser getSpeedAnalyser()
	{
		return speedAnalyser;
	}

	public static void writeFile()
	{
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_'at'_HH_mm_ss");
		//Date current = new Date();

		File dataFile = new File("DATA.txt");// formatter.format(current) + ".txt");
		try
		{
			dataFile.createNewFile();
		}
		catch (IOException e)
		{
			System.out.println("Hoppla, die Datei konnte nicht erzeugt werden!");
			e.printStackTrace();
		}
		try
		{

			PrintStream output = new PrintStream(dataFile);
			output.println(speedAnalyser);
			output.flush();
			output.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Hoppla, die Datei konnte nicht gefunden werden!");
			e.printStackTrace();
		}
	}
}
