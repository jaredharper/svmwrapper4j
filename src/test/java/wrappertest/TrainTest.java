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
import org.junit.runners.JUnit4;

import svmwrapper.DataElement;
import svmwrapper.Predict;
import svmwrapper.Scale;
import svmwrapper.Train;


public class TrainTest
{


	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	/**
	 * End to end test of svmwrapper
	 */
	@Test
	public void test()
	{

		
		try (BufferedReader r = new BufferedReader(new FileReader(new File("src/test/data/a7xhl.scaled"))))
		{

			// Read input data
			ArrayList<DataElement> elements = new ArrayList<>();
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
				// This does NOT support sparse Arrays
				// Mostly because I don't use sparse arrays
				// XXX TODO FIXME support sparse arrays
				ArrayList<Double> data = new ArrayList<>();
				String[] t = Arrays.copyOfRange(components, 1, components.length);
				//for (String pair : Arrays.copyOfRange(components, 1, components.length))
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
							data.add(null);
						}
					}
					data.add(index,d);
				}
				Double[] d = new Double[data.size()];
				data.toArray(d);
				de.setData(d);
				
				elements.add(de);
			}
			
			// Split the list into train and test sets
			ArrayList<DataElement> trainList = new ArrayList<DataElement>();			
			ArrayList<DataElement> predictList = new ArrayList<DataElement>();			
			for (DataElement e : elements)
			{
				if (e.isLabeled() == true)
					trainList.add(e);
				else if (e.isLabeled() == false)
					predictList.add(e);
			}
			
			// Attempt train and cross validation
			Train t = new Train();
			t.setData(trainList);
			t.autoconfigureEpSVR();
			t.train();

			Logger.getAnonymousLogger().log(Level.INFO,"Accuracy: " + t.getAccuracy()
					+ System.lineSeparator() + "Error: " + t.getError());

		}
		catch (Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,ex.getMessage());
			fail();
		}
	}
}
