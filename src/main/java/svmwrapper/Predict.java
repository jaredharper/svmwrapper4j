package svmwrapper;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

/**
 * This class contains logic to use the svm_model generated by Train
 * to predict the class label for unclassified data.<br><br>
 * 
 * Code is a simplified version of libsvm's svm_predict.java<br><br>
 * 
 * @author jharper
 *
 */
public class Predict
{

	static void info(String s) 
	{
		Logger.getAnonymousLogger().log(Level.INFO,s);
	}

	private static void predict(svm_model model, int predict_probability, List<DataElement> data) throws IOException
	{
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		for (DataElement d : data)
		{			
			Double[] thisData = d.getData();
			
			// Need to know how many entries are actually
			// being used (total - DO NOT PROCESS flagged entries)
			int numValidEntries = 0;
			for (int k = 0; k < thisData.length; k++)
			{
				if (thisData[k] != Scale.DO_NOT_PROCESS)
					numValidEntries++;				
			}
			
			// populate node array
			int m = numValidEntries;
			svm_node[] x = new svm_node[m];			
			for (int j = 0, k = 0; j < m; j++, k++)
			{
				
				// Get next index:value pair from thisData that
				// isn't flagged as DO NOT PROCESS and add to x				
				while (thisData[k] == Scale.DO_NOT_PROCESS)
					k++;
								
				x[j] = new svm_node();
				x[j].index = k;
				x[j].value = thisData[k];			
			}
			
			double target = d.getClassLabel();
			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
			}
			else
			{
				v = svm.svm_predict(model,x);
			}

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
		
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			Predict.info("Mean squared error = "+error/total+" (regression)\n");
			Predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
			Predict.info("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (classification)\n");
	}


}