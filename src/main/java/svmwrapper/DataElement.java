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
public class DataElement implements IDataElement
{
	protected Double[] data;
	protected double classLabel;
	protected boolean isLabeled = false;	
	
	/**
	 * Flag used to determine if the class label has been initialized
	 * 
	 * @return true if the class label has been set, false if not
	 */
	@Override
	public boolean isLabeled()
	{
		return isLabeled;
	}
	
	/**
	 * Get number of data values
	 * 
	 * @return
	 */
	@Override
	public Double[] getData()
	{
		return data;
	}
	
	/**
	 * Set the Double[] representing the actual data values
	 * 
	 * <br><br>missing data entries should be 
	 * represented in the array as <b>DataElement.DO_NOT_PROCESS</b><br>
	 * 
	 * @param data
	 */
	@Override
	public void setData(Double[] data)
	{
		this.data = data;
	}
	
	/**
	 * Gets the class label for this data element.  
	 * 
	 * @return
	 */
	@Override
	public double getClassLabel()
	{
		return classLabel;
	}
	
	/**
	 * Set the class label to the provided value and mark it as set
	 * @param v
	 */
	@Override
	public void setClassLabel(double v)
	{
		this.classLabel = v;
		this.isLabeled = true;
	}
	
	/**
	 * Return a deep copy of this
	 */
	@Override
	public IDataElement clone()
	{
		DataElement e = new DataElement();
		
		// Deep copy data
		Double[] newData = new Double[data.length];
		for (int i = 0; i < data.length; i++)
			newData[i] = data[i];
		e.setData(newData);
		
		// Set label if appropriate
		if (isLabeled)
			e.setClassLabel(classLabel);
		
		return e;
	}

}
