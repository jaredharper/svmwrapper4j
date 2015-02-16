package wrappertest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import svmwrapper.DataElement;
import svmwrapper.Scale;

/**
 * This class tests the Scale program and makes sure the computed
 * values are the same as those generated by the cli svm_scale
 * 
 * @author jthomas
 *
 */
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

	/**
	 * Scale and compare
	 */
	@Ignore
	@Test
	public void test()
	{

		ArrayList<DataElement> computed = new ArrayList<>();
		ArrayList<DataElement> cliScaled = new ArrayList<>();

		readFile(computed, "src/test/data/unscaled_sample.txt");
		readFile(cliScaled, "src/test/data/scaled_sample.txt");
		
		// Perform scale operation		
		Scale.scale(computed);	
		
		// Compare computed scaled values to known values
		if (cliScaled.size() != computed.size())
		{
			fail();
		}
		for (int i = 0; i < cliScaled.size(); i++)
		{
			Double[] cli = cliScaled.get(i).getData();
			Double[] gen = computed.get(i).getData();
			
			for (int j = 0; j < cli.length; j++)
			{
				if (cli[j] == Scale.DO_NOT_PROCESS)
					continue;
				else if (cli[j] != gen[j])
					fail();
			}
		}
	}
	
	/**
	 * Read filename in, convert to DataElement ArrayList, load in elts
	 * 
	 * @param elts
	 * @param filename
	 */
	private void readFile(ArrayList<DataElement> elts, String filename)
	{
		// Get the unscaled sample data and load it into elements
		try (BufferedReader r = new BufferedReader(new FileReader(new File(filename))))
		{

			// Read unscaled input data			
			for (String line = r.readLine(); line != null; line = r.readLine())
			{
				DataElement de = new DataElement();
				
				String[] components = line.split(" ");
				
				// Get the first token in the line, which is a class label
				try
				{
					int label = Integer.parseInt(components[0]);
					de.setClassLabel(label);	
					de.setLabeled(true);
				}
				catch (NumberFormatException nfx)
				{
					de.setLabeled(false);
				}

				
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
				
				elts.add(de);
			}			
		}
		catch (Exception ex)
		{
			
		}
	}

}
