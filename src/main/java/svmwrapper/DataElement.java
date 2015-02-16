package svmwrapper;

/**
 * POJO wrapper for a data element used by libSVM
 * 
 * This consists mostly of an array of floats and a class value
 * 
 * @author jharper
 *
 */
public class DataElement
{
	private Double[] data;
	private double classLabel;
	private boolean isLabeled;	
	private int databaseId;
	
	public boolean isLabeled()
	{
		return isLabeled;
	}
	
	public void setLabeled(boolean isLabeled)
	{
		this.isLabeled = isLabeled;
	}

	public Double[] getData()
	{
		return data;
	}
	
	public void setData(Double[] data)
	{
		this.data = data;
	}
	
	public double getClassLabel()
	{
		return classLabel;
	}
	
	public void setClassLabel(double v)
	{
		this.classLabel = v;
	}

	public int getDatabaseId()
	{
		return databaseId;
	}

	public void setDatabaseId(int databaseId)
	{
		this.databaseId = databaseId;
	}

}
