package wrappertest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import svmwrapper.DataElement;
import svmwrapper.Scale;


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
		
		// Read input data
		
		// Load input data into dv		
		ArrayList<DataElement> dv = new ArrayList<>();
		
		// Perform scale operation
		Scale.scale(dv);	
		
		// Read scaled data
	}

}
