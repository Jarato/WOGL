package statistic.base;

import java.util.ArrayList;

import simulation.world.World;
import statistic.base.Datapack.DATATYPE;

/**
 * @author Pagad
 *
 */
public class Statistic {
	
	private String name=null;
	private ArrayList<Datapack> DataList= new ArrayList<Datapack>();

	protected Statistic(String name) {
		this.setName(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * addDataByMathode
	 * 
	 * @param methodeName
	 * getMethode of Variable you want to observe
	 * @param o 
	 * 
	 * @param data
	 * data type of the Variable:
	 * 			DATATYPE.INTEGER
	 * 			DATATYPE.LONG
	 * 			DATATYPE.FLOAT
	 * 			DATATYPE.DOUBLE
	 */
	
	public void addDataByMethode(Object o, String methodeName, DATATYPE data) {
		DataList.add(new Datapack(methodeName,o, data));
	}
	
	
	/**
	 * 
	 * @param world 
	 * @param VariableName
	 * 	      Name of Variable you want to observe
	 * 
	 * @param data 
	 * 		  DatenTyp 
	 * 	 		DATATYPE.INTEGER
	 * 			DATATYPE.LONG
	 * 			DATATYPE.FLOAT
	 * 			DATATYPE.DOUBLE
	 */
	public void addData(Object o, String VariableName,DATATYPE data) {
		addDataByMethode(o,VariableName,data);		
	}
	
	public String toTable() {
		
		return StatisticManager.StatisticToTable(this);
	}

	/**
	 * @return the dataList
	 */
	public ArrayList<Datapack> getDataList() {
		return DataList;
	}

	/**
	 * 
	 */
	public void observe() {
		for(Datapack p: DataList) {
			p.observe();
		}
		
	}

	

}
