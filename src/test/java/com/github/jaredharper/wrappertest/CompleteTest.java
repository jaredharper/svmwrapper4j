package com.github.jaredharper.wrappertest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.github.jaredharper.svmwrapper4j.libsvm.Predict;
import com.github.jaredharper.svmwrapper4j.libsvm.Scale;
import com.github.jaredharper.svmwrapper4j.libsvm.Train;
import com.github.jaredharper.svmwrapper4j.pojo.DataElement;
import com.github.jaredharper.svmwrapper4j.pojo.IDataElement;

/**
 * As implied, this is an end-to-end test demonstrating a complete workflow
 * (data gathering, scaling, training and ultimately prediction).
 * 
 * @author jharper
 *
 */
public class CompleteTest
{

	@Test
	public void test()
	{
		try
		{

			// Raw data goes into DataElement objects
			ArrayList<DataElement> elements = loadInputData();

			// Perform scale operation
			Scale.scale(elements, -1, 1);

			// Split the list into train and test sets
			ArrayList<IDataElement> trainList = new ArrayList<IDataElement>();
			ArrayList<IDataElement> predictList = new ArrayList<IDataElement>();
			for (DataElement e : elements)
			{
				if (e.isLabeled() == true)
					trainList.add(e);
				else if (e.isLabeled() == false) 
					predictList.add(e);
			}

			// Configure and train the model
			Train t = new Train();
			t.setData(trainList);
			t.setQuiet(true);
			t.autoconfigureNuSVC();
			t.train();

			// Check the estimated accuracy
			Logger.getAnonymousLogger().log(Level.INFO, "Accuracy: " + t.getAccuracy());

			// Classify unlabeled samples
			Predict.predict(t.getModel(), 0, predictList);

			// Check labels
			for (IDataElement e : predictList)
			{
				Logger.getAnonymousLogger().log(Level.INFO, "Added label " + e.getClassLabel());
				
				// After calling predict() all entries in the List should have a class label
				if (e.isLabeled() == false) 
					fail();
			}

		}
		catch (Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
			fail();
		}
	}
	
	/**
	 * This is the file logic that reads in the test input file (in libsvm format)<br><br>
	 * 
	 * (placed here so as to simplify the test method above)<br><br>
	 * 
	 * @return ArrayList of {@link DataElement} objects representing the data
	 */
	private ArrayList<DataElement> loadInputData()
	{

		ArrayList<DataElement> elements = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new FileReader(new File("src/test/data/sample.txt"))))
		{

			// Read input data			
			for (String line = r.readLine(); line != null; line = r.readLine())
			{
				
				DataElement de = new DataElement();
				
				String[] components = line.split(" ");
				
				// Get the first token in the line, which is a class label
				try
				{
					int label = Integer.parseInt(components[0]);
					de.setClassLabel(label);	
				}
				catch (NumberFormatException nfx)
				{
					// Unlabeled data is expected
				}

				
				// Get the index:value pairs
				ArrayList<Double> data = new ArrayList<>();
				String[] t = Arrays.copyOfRange(components, 1, components.length);
				for (int i = 0; i < t.length; i++)
				{
					String pair = t[i];
					Double d = 0.0;
					try
					{
						d = Double.parseDouble(pair.split(":")[1]);
					}
					catch (Exception e)
					{
						Logger.getAnonymousLogger().log(Level.INFO,pair.toString());
						fail();
					}
					data.add(d);
				}
				Double[] d = new Double[data.size()];
				data.toArray(d);
				de.setData(d);
				
				elements.add(de);
			}			
		}
		catch (Exception e)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,"Error reading inputs " + e.getMessage());
			fail();
		}
		return elements;
	}
}
