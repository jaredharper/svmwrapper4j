package com.none.svmwrapper;

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
	 * Flag to note that an array value should be skipped when creating a model
	 */
	public static final double doNotProcess = Double.MAX_VALUE;
	
	/**
	 * This method takes a collection of data arrays and
	 * scales them to fit (-1,1)
	 * 
	 * @param data List containing the arrays to be modified
	 */
	public static void scale(List<Double[]> data)
	{

		// Assume all input vectors are of the same size
		int size = data.get(0).length;
		double[] max = new double[size];
		double[] min = new double[size];
		
		// Neither min nor max use default initialization value
		for (double d : max)
			d = Double.MIN_VALUE;		
		for (double d : min)
			d = Double.MAX_VALUE;

		// First iteration, find global min/max
		for (Double[] d : data)
		{
			for (int i = 0; i < d.length; i++)
			{
				max[i] = Math.max(max[i], d[i]);
				min[i] = Math.min(min[i], d[i]);
			}
		}

		// scale values
		for (Double[] d : data)
		{
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
					d[index] = Scale.doNotProcess;
				
				/* min/max forced to [-1,1] */
				else if (d[index] == min[index])
					d[index] = -1.0;
				else if (d[index] == max[index])
					d[index] = 1.0;
				
				/* actual scaling calculation for common case */
				else
					d[index] = (d[index] - min[index]) / (max[index] - min[index]);
			}
		}
	}
	
}