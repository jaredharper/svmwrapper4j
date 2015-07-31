package com.github.jaredharper.svmwrapper4j.pojo;

/**
 * Interface definition for a Data element.  Mainly, a data element
 * will have a data array and a class label, so there must always be
 * some way to access (and modify) these components.  Additionally,
 * there will be a flag to indicate missing values as libsvm operates
 * on sparse arrays.
 * 
 * @author jharper
 *
 */
public interface IDataElement
{

	/**
	 * Flag to note that an array value should be skipped when creating a model
	 */
	public static final Double DO_NOT_PROCESS = Double.NaN;
	
	/**
	 * Flag used to determine if the class label has been initialized
	 * 
	 * @return true if the class label has been set, false if not
	 */
	public boolean isLabeled();
	
	/**
	 * Get the Double[] representing the actual data values
	 * 
	 * @return
	 */
	public Double[] getData();
	
	/**
	 * Set the Double[] representing the actual data values
	 * 
	 * <br><br>missing data entries should be 
	 * represented in the array as <b>DataElement.DO_NOT_PROCESS</b><br>
	 * 
	 * @param data
	 */
	public void setData(Double[] data);
	
	/**
	 * Gets the class label for this data element.  
	 * 
	 * @return
	 */
	public double getClassLabel();
	
	/**
	 * Set the class label to the provided value and mark it as set
	 * @param v
	 */
	public void setClassLabel(double v);

	/**
	 * Return a deep copy of this object
	 * 
	 * @return
	 */
	public IDataElement clone();
	
}
