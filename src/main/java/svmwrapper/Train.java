package svmwrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * This class contains logic to create, train and cross-validate
 * the SVM/SVR.<br><br>
 * 
 * Code is a simplified version of libsvm's svm_train.java<br><br>
 * 
 * To use: <br> instantiate <br>call setData() with your data<br> call an autoconfigure method<br> call train()<br> then get the populated
 * model with getModel()
 * 
 * @author jharper
 *
 */
public class Train
{

	private svm_parameter param;
	private svm_problem prob;
	private svm_model model;
	private String error_msg;
	private int nr_fold;
	private double accuracy;
	private double error;
	private HashMap<Double, Double> results;
	private List<IDataElement> data;

	/**
	 * Accessor for the svm_model that will be populated
	 * based on provided data.  This is needed by Predict.
	 */
	public svm_model getModel()
	{
		return model;
	}
	
	/**
	 * Get model accuracy as computed by do_cross_validation()
	 * 
	 * @return double representing accuracy (usual values are 0.0-1.0)
	 */
	public double getAccuracy()
	{
		return accuracy;
	}
	
	/**
	 * Mutator for the current object's accuracy value
	 * 
	 * @param accuracy
	 */
	public void setAccuracy(double accuracy)
	{
		this.accuracy = accuracy;
	}
	
	/**
	 * Get model error as computed by do_cross_validation().
	 * 
	 * @return double representing error if SVR was used.
	 */
	public double getError()
	{
		return error;
	}
	
	/**
	 * Mutator for the input data needed to create the model
	 * 
	 * @param d - a List of {@link IDataElement}
	 */
	public void setData(List<IDataElement> d)
	{
		data = d;
	}
	
	/**
	 * Accessor for the number of data used to create the model
	 * 
	 * @return size of IDataElement collection
	 */
	public int getDataSize()
	{
		return data.size();
	}
	
	/**
	 * Mutator for the svm_parameter object that handles
	 * configuration details for training the model.
	 * 
	 * Use this if you understand the technical details
	 * behind svm type / kernel details and want to
	 * configure an svm_parameter object manually.
	 * 
	 * @param param
	 */
	public void setParam(svm_parameter param)
	{
		this.param = param;
	}
	
	/**
	 * Accessor for the svm_parameter object that handles
	 * configuration details for training the model.
	 * 
	 * @return
	 */
	public svm_parameter getParam()
	{
		return param;
	}
	
	/**
	 * Accessor the number of folds for cross validation
	 * 
	 * @return number of folds
	 */
	public int getNrFold()
	{
		return nr_fold;
	}
	
	/**
	 * Mutator for the number of folds used by cross validation
	 * 
	 * @param nrFold number of folds
	 */
	public void setNrFold(int nrFold)
	{
		this.nr_fold = nrFold;
	}
	
	/**
	 * Accessor for the svm_probem used internally
	 * by calls to the libsvm lib
	 * 
	 * @return svm_problem object used internally
	 */
	public svm_problem getProblem()
	{
		return prob;
	}
	
	/**
	 * This method will set the svm type to Nu SVC
	 * and attempt to find a nu value that fits the data
	 * 
	 * @throws Exception - On unrecoverable error 
	 * 
	 * @return Map of nu values to the projected accuracy as computed by k-fold
	 * 
	 */
	public HashMap<Double,Double> autoconfigureNuSVC() throws Exception
	{
		Autoconfigure.autoconfigureNuSvc(this);
		return results;
	}
	
	/**
	 * This method will set the type to Epsilon SVR
	 * and attempt to find a p value that fits
	 * the data
	 * 
	 */
	public void autoconfigureEpSVR()
	{
		Autoconfigure.autoconfigureEpSvr(this);		
	}	
	
	/**
	 * This method creates an svm_model based
	 * on the provided data and configuration details<br><br>
	 * 
	 * You should either call one of the automagic configuration
	 * methods or manually configure your svm_model object before
	 * calling this method
	 * 
	 * @throws Exception on unrecoverable error
	 * 
	 */
	public void train() throws Exception
	{
		read_problem();
		
		error_msg = svm.svm_check_parameter(prob, param);
		if (error_msg != null)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,"ERROR: " + error_msg);
			throw new Exception("Unrecoverable error while setting parameters");
		}
		
		model = svm.svm_train(prob, param);
	}
	
	/**
	 * Perform k-fold<br><br>
	 * 
	 * Note that the k value is part of the svm_parameter object and should
	 * be set before calling this method
	 * 
	 * @throws Exception on unrecoverable error
	 * 
	 */
	public void do_cross_validation() throws Exception
	{
		
		if (nr_fold == 0)
			throw new Exception("Cannot perform k-fold with a k value of 0");
		
		read_problem();
		
		error_msg = svm.svm_check_parameter(prob, param);
		if (error_msg != null)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,"ERROR: " + error_msg);
			throw new Exception("Unrecoverable error while setting parameters");
		}
		
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob, param, nr_fold, target);
		if (param.svm_type == svm_parameter.EPSILON_SVR || param.svm_type == svm_parameter.NU_SVR)
		{
			for (i = 0; i < prob.l; i++)
			{
				double y = prob.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			error = total_error / prob.l;
			accuracy = ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
					/ ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy));
		}
		else
		{
			for (i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i]) ++total_correct;
			accuracy = 100.0 * total_correct / prob.l;
		}
	}

	/**
	 * Convert the IDataElement List to svm_node and svm_problem objects
	 * 
	 * @throws IOException
	 */
	public void read_problem()
	{
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;

		for (IDataElement d : data)
		{						
			
			vy.addElement((double) d.getClassLabel());
			
			Double[] thisData = d.getData();
			
			// Need to know how many entries are actually
			// being used (total - DO NOT PROCESS flagged entries)
			int numValidEntries = 0;
			for (int k = 0; k < thisData.length; k++)
			{
				if (thisData[k] != IDataElement.DO_NOT_PROCESS)
					numValidEntries++;				
			}
			
			int m = numValidEntries;
			svm_node[] x = new svm_node[m];			
			for (int j = 0, k = 0; j < m; j++, k++)
			{
				
				// Get next index:value pair from thisData that
				// isn't flagged as DO NOT PROCESS and add to x				
				while (thisData[k] == IDataElement.DO_NOT_PROCESS)
					k++;
								
				x[j] = new svm_node();
				x[j].index = k;
				x[j].value = thisData[k];			
			}
			
			if (m > 0) max_index = Math.max(max_index, x[m - 1].index);
			vx.addElement(x);
		}

		prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for (int i = 0; i < prob.l; i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; i++)
			prob.y[i] = vy.elementAt(i);

		if (param.gamma == 0 && max_index > 0) param.gamma = 1.0 / max_index;

	}
}
