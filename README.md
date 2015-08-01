# svmwrapper4j

svmwrapper4j provides a simple library to mimic the conventions and idioms used by the libSVM command line tools.  If you are familiar with using libSVM interactively, then svmwrapper4j allows you to quickly add the same logic to your java applications.

Example usage (from CompleteTest.java):

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


In the above example, all DataElements in predictList are now classified.  The predicted label can be accessed by the dataElement's getLabel() method.

A DataElement object can be instantiated simply:

			// Creating a DataElement object
			DataElement e = new DataElement();
			
			Double[] myData = 
			e.setData(myData);
