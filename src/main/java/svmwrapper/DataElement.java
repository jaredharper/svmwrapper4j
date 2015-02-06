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
	private int classLabel;
	private boolean isLabeled;	
	
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
	
	public int getClassLabel()
	{
		return classLabel;
	}
	
	public void setClassLabel(int classLabel)
	{
		this.classLabel = classLabel;
	}

}
