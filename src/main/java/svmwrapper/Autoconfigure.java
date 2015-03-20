package svmwrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import libsvm.svm;
import libsvm.svm_parameter;

/**
 * This class contains logic to configure
 * the svm_parameter object in a Train object
 * in a semi-intelligent fashion for quick
 * prototyping.<br><br>
 * 
 * The purpose of this class is to offer speedy
 * convenience.  For in depth use you should
 * be configuring the svm_parameter object
 * yourself.
 * 
 * @author jharper
 *
 */
public class Autoconfigure
{

	/**
	 * Heuristics to generate nu values that are somewhat tailored
	 * to the input data.
	 * 
	 * @param t - The Train object being configured
	 */
	public static void autoconfigureNuSvc(Train t) throws Exception
	{
		try
		{
			
			List<DataElement> data = t.getData();
			String error_msg;
			double accuracy;
			
			svm_parameter param = new svm_parameter();
			
			param.svm_type = svm_parameter.NU_SVC;
			param.kernel_type = svm_parameter.SIGMOID;
	
			param.nu = 0.95;
			param.gamma = 1d / (double) data.size();
			param.coef0 = 0;
			
			param.p = 0.755;
	
			param.cache_size = 100;		
			param.shrinking = 1;
			param.probability = 0;		
			param.nr_weight = 0;
			param.weight_label = new int[0];
			param.weight = new double[0];
						
			double bestAccuracy = 0;
			HashMap<Double, Double> results = new HashMap<>();
			
			double[] nuVals = {0.1, 0.25, 0.33, 0.40, 0.5, 0.66, 0.75, 0.8, 0.95};
			for (double n : nuVals)
			{
				param.nu = n;
				t.read_problem();
				
				error_msg = svm.svm_check_parameter(t.getProblem(), param);
				if (error_msg != null && error_msg.equals("specified nu is infeasible"))
				{
					continue;
				}
				if (error_msg != null)
				{
					Logger.getAnonymousLogger().log(Level.SEVERE,"Error with parameter object");
					throw new Exception("Error with parameter object");
				}				
				
				t.setNrFold(data.size());
				t.do_cross_validation();
				
				results.put(param.nu, t.getAccuracy());
			}
	
			for (Double key : results.keySet())
			{
				double localAccuracy = results.get(key);
				if (localAccuracy > bestAccuracy)
				{
					bestAccuracy = localAccuracy;
					param.nu = key;
				}
			}	
			
			t.setAccuracy(bestAccuracy);
		}
		catch (Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,"Error in autoconfigure" + System.lineSeparator() + 
					ex.getMessage());
			throw ex;
		}
	}
	
	public static void autoconfigureEpSvr(Train t)
	{
		
	}
}
