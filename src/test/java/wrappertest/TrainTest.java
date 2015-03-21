package wrappertest;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import svmwrapper.DataElement;
import svmwrapper.Train;

/**
 * This test case reads in scaled data and trains a model, writing
 * the expected (k-fold) accuracy/error values to the output device
 * 
 * @author jharper
 *
 */
public class TrainTest
{

	@Test
	public void test()
	{
		try
		{

			// Read input data
			ArrayList<DataElement> elements = loadInputData();
			
			// Put labeled data in the training list
			ArrayList<DataElement> trainList = new ArrayList<DataElement>();			
			for (DataElement e : elements)
			{
				if (e.isLabeled() == true)
					trainList.add(e);
			}
			
			// Attempt train and cross validation
			Train t = new Train();
			t.setData(trainList);
			t.autoconfigureEpSVR();
			t.train();

			// Note predicted accuracy
			Logger.getAnonymousLogger().log(Level.INFO,"Accuracy: " + t.getAccuracy()
					+ System.lineSeparator() + "Error: " + t.getError());

		}
		catch (Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,ex.getMessage());
			fail();
		}
	}
	
	/**
	 * This is the file logic that reads in the test input file (in libsvm format)<br><br>
	 * 
	 * (placed here so as to simplify the test method above)<br><br>
	 * 
	 * @return ArrayList of {@link DataElement} representing the data
	 */
	public ArrayList<DataElement> loadInputData()
	{

		ArrayList<DataElement> elements = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new FileReader(new File("src/test/data/a7xhl.scaled"))))
		{

			// Read input data
			for (String line = r.readLine(); line != null; line = r.readLine())
			{
				
				DataElement de = new DataElement();				
				String[] components = line.split(" ");
				
				// Get the first token in the line, which is a class label
				try
				{
					double label = Double.parseDouble(components[0]);
					de.setClassLabel(label);	
				}
				catch (NumberFormatException nfx)
				{
					// Unlabeled data is expected
				}

				// Get the index:value pairs
				ArrayList<Double> data = new ArrayList<>();
				String[] t = Arrays.copyOfRange(components, 1, components.length);
				int lastIndex = 0;
				for (int i = 0; i < t.length; i++)
				{
					String pair = t[i];
					int index = -1;
					Double d = 0.0;
					try
					{
						index = Integer.parseInt(pair.split(":")[0]);
						d = Double.parseDouble(pair.split(":")[1]);
					}
					catch (Exception e)
					{
						Logger.getAnonymousLogger().log(Level.INFO,pair.toString());
						fail();
					}
					if (index > data.size())
					{
						while (lastIndex++ < index)
						{
							data.add(DataElement.DO_NOT_PROCESS);
						}
					}
					data.add(index,d);
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
