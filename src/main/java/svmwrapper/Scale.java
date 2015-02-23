package svmwrapper;

import java.util.List;

/**
 * This class contains logic to handle scaling operations.
 * 
 * Code is a simplified implementation of libSVM's provided svm_scale.java
 * 
 * @author jharper
 *
 */
public class Scale
{

	/**
	 * This method takes a collection of DataElement objects and
	 * scales their data to fit (-1,1)
	 * 
	 * @param data List containing the arrays to be modified
	 */
	public static void scale(List<DataElement> dv)
	{

		
		
		// Assume all input vectors are of the same size
		int size = dv.get(0).getData().length;
		double[] max = new double[size];
		double[] min = new double[size];
		
		// Neither min nor max use default initialization value
		for (int i = 0; i < max.length; i++)
		{
			max[i] = -Double.MAX_VALUE;		
			min[i] = Double.MAX_VALUE;
		}

		// First iteration, find global min/max
		for (DataElement element : dv)
		{
			Double[] d = element.getData();
			for (int i = 0; i < d.length; i++)
			{
				max[i] = Math.max(max[i], d[i]);
				min[i] = Math.min(min[i], d[i]);
			}
		}

		// scale values
		for (DataElement element : dv)
		{
			Double[] d = element.getData();
			for (int index = 0; index < d.length; index++)
			{
			
				/* 
				 * skip single-valued attribute
				 * 
				 * for now just treat any scaled value outside 
				 * the -1,1 range as a skip value.  need logic
				 * in the train routine to check for this. 
				 */
				if (max[index] == min[index]) 
					d[index] = DataElement.DO_NOT_PROCESS;
				
				/* min/max forced to [-1,1] */
				else if (d[index] == min[index])
					d[index] = -1.0;
				else if (d[index] == max[index])
					d[index] = 1.0;
				
				/* actual scaling calculation for common case */
				else
					d[index] = -1.0 + 2.0 * (d[index] - min[index]) / (max[index] - min[index]);
			}
		}
	}
	
}
