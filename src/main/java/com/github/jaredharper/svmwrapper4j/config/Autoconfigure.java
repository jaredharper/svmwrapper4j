package com.github.jaredharper.svmwrapper4j.config;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.jaredharper.svmwrapper4j.libsvm.Train;

import libsvm.svm;
import libsvm.svm_parameter;

/**
 * This class contains logic to configure
 * the svm_parameter object in a Train object
 * in a semi-intelligent fashion for quick
 * prototyping.<br><br>
 * 
 * The purpose of this class is to offer speed and
 * convenience.  For maximum accuracy you should
 * be configuring the svm_parameter object
 * manually based on your data.
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
			
			int dataSize = t.getDataSize();
			String error_msg;
			
			svm_parameter param = new svm_parameter();
			
			param.svm_type = svm_parameter.NU_SVC;
			param.kernel_type = svm_parameter.RBF;
	
			param.nu = 0.5;
			param.gamma = 1d / 2048d;
			param.coef0 = 0;
			
			param.p = 0.1;
			param.eps = 0.001;
	
			param.cache_size = 100;		
			param.shrinking = 1;
			param.probability = 0;		
			param.nr_weight = 0;
			param.weight_label = new int[0];
			param.weight = new double[0];
			
			t.setParam(param);
			t.setNrFold(dataSize);	
			
			double bestAccuracy = 0;
			HashMap<Double, Double> results = new HashMap<>();
			
			for (double n = 0.05; n <= 1.; n += 0.05)
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
	
}
