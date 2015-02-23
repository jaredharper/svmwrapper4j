package svmwrapper;

/**
 * POJO wrapper for a data element used by libSVM<br><br>
 * 
 * This consists mostly of an array of real numbers and a class value<br><br>
 * 
 * This represents a single line from a regular libsvm input file
 * 
 * @author jharper
 *
 */
public class DataElement
{
	private Double[] data;
	private double classLabel;
	private boolean isLabeled = false;	
	private int databaseId;
	
	/**
	 * Flag to note that an array value should be skipped when creating a model
	 */
	public static final Double DO_NOT_PROCESS = null;
	
	/**
	 * Flag used to determine if the class label has been initialized
	 * 
	 * @return true if the class label has been set, false if not
	 */
	public boolean isLabeled()
	{
		return isLabeled;
	}
	
	/**
	 * Get the Double[] representing the actual data values
	 * 
	 * @return
	 */
	public Double[] getData()
	{
		return data;
	}
	
	/**
	 * Set the Double[] representing the actual data values
	 * 
	 * @param data
	 */
	public void setData(Double[] data)
	{
		this.data = data;
	}
	
	/**
	 * Gets the class label for this data element.  
	 * 
	 * @return
	 */
	public double getClassLabel()
	{
		return classLabel;
	}
	
	/**
	 * Set the class label to the provided value and mark it as set
	 * @param v
	 */
	public void setClassLabel(double v)
	{
		this.classLabel = v;
		this.isLabeled = true;
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
