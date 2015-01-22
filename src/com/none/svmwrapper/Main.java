package com.none.svmwrapper;

import java.util.List;

public class Main
{

	public void scale(List<Double[]> data)
	{

		int size = data.size();
		double[] max = new double[size];
		double[] min = new double[size];

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
				d[index] = output(index, d[index], min, max);
			}
		}
	}

	private Double output(int index, double value, double[] feature_min, double[] feature_max)
	{
		/* skip single-valued attribute */
		if (feature_max[index] == feature_min[index]) 
			value = 0.0;
		
		/* min/max forced to [-1,1] */
		else if (value == feature_min[index])
			value = -1;
		else if (value == feature_max[index])
			value = 1;
		
		/* actual scaling calculation for common case */
		else
			value = (value - feature_min[index]) / (feature_max[index] - feature_min[index]);

		return value;
	}

	public void train()
	{

	}

	public void test()
	{

	}

	public void apply()
	{

	}

}
