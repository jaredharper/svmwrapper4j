package com.none.svmwrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import libsvm.svm_problem;

/**
 * This class contains logic to create, train and cross-validate
 * the SVM/SVR.<br>
 * 
 * To use: <br> call CTOR <br>call setData() with your data<br> call train()<br> then get the populated
 * model with getModel()
 * 
 * @author jharper
 *
 */
public class Train
{

	private svm_parameter param;	// set by parse_command_line
	private svm_problem prob;		// set by read_problem
	private svm_model model;
	private String input_file_name;	// set by parse_command_line
	private String model_file_name;	// set by parse_command_line
	private String error_msg;
	private int cross_validation;
	private int nr_fold;
	private List<DataElement> data;

	/**
	 * Accessor for the svm_model that will be populated
	 * based on provided data
	 */
	public svm_model getModel()
	{
		return model;
	}
	
	/**
	 * Mutator for the input data needed to create the model
	 * 
	 * @param d - a List of {@link DataElement}
	 */
	public void setData(List<DataElement> d)
	{
		data = d;
	}
	
	/**
	 * CTOR uses the libsvm default values for now
	 * 
	 */
	public Train()
	{
		
		// Initialize param with default values
		param = new svm_parameter();
		
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = 0;	// 1/num_features
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		cross_validation = 0;
		
		// XXX TODO FIXME
		// reimplement parsing for optional svm arguments
		
	}
	
	
	/**
	 * This method creates an svm_model and optionally
	 * does k-fold cross validation
	 * 
	 * @throws IOException
	 */
	public void train() throws IOException
	{
		// parse_command_line(argv);
		read_problem();
		error_msg = svm.svm_check_parameter(prob, param);

		if (error_msg != null)
		{
			System.err.print("ERROR: " + error_msg + "\n");
			System.exit(1);
		}

		if (cross_validation != 0)
		{
			do_cross_validation();
		}
		else
		{
			model = svm.svm_train(prob, param);
			svm.svm_save_model(model_file_name, model);
		}
	}
	
	/**
	 * Optionally perform k-fold
	 * 
	 */
	private void do_cross_validation()
	{
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
			System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
			System.out.print("Cross Validation Squared correlation coefficient = " + ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
					/ ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy)) + "\n");
		}
		else
		{
			for (i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i]) ++total_correct;
			System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / prob.l + "%\n");
		}
	}

	/**
	 * read in a problem (in svmlight format)
	 * 
	 * @throws IOException
	 */
	private void read_problem() throws IOException
	{
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;

		for (DataElement d : data)
		{						
			
			vy.addElement((double) d.getClassLabel());
			
			Double[] thisData = d.getData();
			
			// Need to know how many entries are actually
			// being used (total - DO NOT PROCESS flagged entries)
			int numValidEntries = 0;
			for (int k = 0; k < thisData.length; k++)
			{
				if (thisData[k] != Scale.DO_NOT_PROCESS)
					numValidEntries++;
				
			}
			
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

		if (param.kernel_type == svm_parameter.PRECOMPUTED) for (int i = 0; i < prob.l; i++)
		{
			if (prob.x[i][0].index != 0)
			{
				System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
				System.exit(1);
			}
			if ((int) prob.x[i][0].value <= 0 || (int) prob.x[i][0].value > max_index)
			{
				System.err.print("Wrong input format: sample_serial_number out of range\n");
				System.exit(1);
			}
		}
	}
}
