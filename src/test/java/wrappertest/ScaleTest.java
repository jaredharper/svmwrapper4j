package wrappertest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import svmwrapper.DataElement;
import svmwrapper.Scale;
import svmwrapper.Train;


public class ScaleTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{

		
		try (BufferedReader r = new BufferedReader(new FileReader(new File("src/test/data/sample.txt"))))
		{

			// Read input data
			ArrayList<DataElement> elements = new ArrayList<>();
			for (String line = r.readLine(); line != null; line = r.readLine())
			{
				DataElement de = new DataElement();
				
				String[] components = line.split(" ");
				
				// Get the first token in the line, which is a class label
				int label = Integer.parseInt(components[0]);
				de.setClassLabel(label);
				
				// Get the index:value pairs
				// This does NOT support sparse Arrays
				// Mostly because I don't use sparse arrays
				// XXX TODO FIXME support sparse arrays
				ArrayList<Double> data = new ArrayList<>();
				String[] t = Arrays.copyOfRange(components, 1, components.length);
				//for (String pair : Arrays.copyOfRange(components, 1, components.length))
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
			
			// Perform scale operation
			Scale.scale(elements);	
			
			// Read scaled data
			for (DataElement e : elements)
			{
				Double[] d = e.getData();
				for (Double value : d)
				{
					if (value < -1.0 || value > 1.0)
						fail();
				}
			}
			
			// Attempt train and cross validation
			Train t = new Train();
			t.setData(elements);
			t.train();
			
			Logger.getAnonymousLogger().log(Level.INFO,"Accuracy: " + t.getAccuracy() + System.lineSeparator() +
					"Error:" + t.getError());

		}
		catch (Exception ex)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE,ex.getMessage());
			fail();
		}
	}

}
