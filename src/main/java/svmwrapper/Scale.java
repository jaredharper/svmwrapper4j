package svmwrapper;

import java.util.List;

/**
 * This class contains logic to handle scaling operations.<br><br>
 * 
 * Code is a simplified implementation of libSVM's provided svm_scale.java<br><br>
 * 
 * @author jharper
 *
 */
public class Scale
{

	/**
	 * This method takes a collection of IDataElement objects and
	 * scales their data to fit the given range
	 * 
	 * @param dv List containing the {@link IDataElement} objects to be modified
	 */
	public static void scale(List<? extends IDataElement> dv, int scaleMin, int scaleMax)
	{
		
		// Find the largest input and use its length
		// to size the min/max arrays
		int size = 0;
		for (int i = 0; i < dv.size(); i++)
		{
			int temp = dv.get(i).getData().length;
			if (temp > size)
				size = temp;
		}
		
		double[] max = new double[size];
		double[] min = new double[size];
		
		// Neither min nor max use default initialization value
		for (int i = 0; i < max.length; i++)
		{
			max[i] = -Double.MAX_VALUE;		
			min[i] = Double.MAX_VALUE;
		}

		// First iteration, find global min/max
		for (IDataElement element : dv)
		{
			Double[] d = element.getData();
			for (int i = 0; i < d.length; i++)
			{
				if (d[i] == IDataElement.DO_NOT_PROCESS)
					continue;
				max[i] = Math.max(max[i], d[i]);
				min[i] = Math.min(min[i], d[i]);
			}
		}

		// scale values
		for (IDataElement element : dv)
		{
			Double[] d = element.getData();
			for (int index = 0; index < d.length; index++)
			{
			
				/* 
				 * skip single-valued attribute
				 */
				if (max[index] == min[index]) 
					d[index] = IDataElement.DO_NOT_PROCESS;
				
				/* min/max provided as input param */
				else if (d[index] == min[index])
					d[index] = (double) scaleMin;
				else if (d[index] == max[index])
					d[index] = (double) scaleMax;
				
				/* actual scaling calculation for common case */
				else
				{
					double v = scaleMin + (scaleMax - scaleMin) * (d[index] - min[index]) / (max[index] - min[index]);
					if (v != 0)
						d[index] = v;
					else
						d[index] = IDataElement.DO_NOT_PROCESS;
				}
					
			}
		}
	}
	
}
