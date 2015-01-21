package com.none.svmwrapper;

import java.util.List;

public class Main
{

	public double[] scale(List<Double[]> data)
	{

		int size = data.size();
		double[] max = new double[size];
		double[] min = new double[size];

		// First iteration, find global min/max
		// For each vector
		// was while nextline()
		for (Double[] d : data)
		{
			int next_index = 1;

			// For each element in each vector
			// was while st.hasmoretokens
			for (int i = 0; i < data.size(); i++)
			{

				for (int j = next_index; j < i; j++)
				{
					max[i] = Math.max(max[i], 0);
					min[i] = Math.min(min[i], 0);
				}

				max[i] = Math.max(max[i], d[i]);
				min[i] = Math.min(min[i], d[i]);

				next_index = i + 1;

			}
		}

		// scale values
		//
		for (Double[] d : data)
		{
			int next_index = 1;
			double target;
			double value;

			// ?
			// output_target(target);
			for (int index = 0; index < d.length; index++)
			{
				value = d[index];
				for (int i = next_index; i < index; i++)
					d[i] = output(i, 0, min, max);
				d[index] = output(index, value, min, max);
				next_index = index + 1;
			}

			for (int i = next_index; i <= d.length; i++)
				output(i, 0, min, max);
			System.out.print("\n");
		}
		return null;
	}

	private Double output(int index, double value, double[] feature_min, double[] feature_max)
	{
		/* skip single-valued attribute */
		if (feature_max[index] == feature_min[index]) return null;

		/* actual scaling */
		if (value == feature_min[index])
			value = -1;
		else if (value == feature_max[index])
			value = 1;
		else
			value = (value - feature_min[index]) / (feature_max[index] - feature_min[index]);

		if (value != 0)
		{
			System.out.print(index + ":" + value + " ");
			// new_num_nonzeros++;
		}

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
