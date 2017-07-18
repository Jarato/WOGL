/**
 * 
 */
package main.statistic.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * @author Pagad
 *
 */
public  class Datapack {
	
	public static enum DATATYPE {
	    STRING,LONG,INTEGER,DOUBLE,FLOAT
	}
	
	private ArrayList<?> datalist;
	private String name;
	private Object invokeOn;
	private DATATYPE d;

	public Datapack(String name, Object o, DATATYPE d) {
		this.name=name;
		this.invokeOn=o;
		this.d=d;
		
		switch(d) {
		case DOUBLE:
			datalist=new ArrayList<Double>();
			break;
		case FLOAT:
			datalist=new ArrayList<Float>();
			break;
		case INTEGER:
			datalist=new ArrayList<Integer>();
			break;
		case LONG:
			datalist=new ArrayList<Long>();
			break;
		case STRING:
			datalist=new ArrayList<String>();
			break;
		default:
			break;
		
		}
		//dataList= new ArrayList
	}

	/**
	 * @param i
	 * @return
	 */
	public String getList(int i) {
		return datalist.get(i)+"";
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 */
	public void observe() {
		
		try {
			Method m = invokeOn.getClass().getMethod(name);
			Object get = m.invoke(invokeOn);
			switch(d) {
			case DOUBLE:
				Double getD= (Double) get;
				((ArrayList<Double>) datalist).add(getD);
				break;
			case FLOAT:
				Float getF= (Float) get;
				((ArrayList<Float>) datalist).add(getF);
				break;
			case INTEGER:
				int getI= (Integer) get;
				((ArrayList<Integer>) datalist).add(getI);
				break;
			case LONG:
				Long getL= (Long) get;
				((ArrayList<Long>) datalist).add(getL);
				break;
			case STRING:
				String getS= (String) get;
				((ArrayList<String>) datalist).add(getS);
				break;
			default:
				break;
			
			}

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public int getLenght() {
		return datalist.size();
	}
	
}
