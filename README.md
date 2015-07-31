# svmwrapper4j

svmwrapper4j is intended to provide a simple library to mimic the conventions and idioms used by the libSVM command line tools.

Scale.java, Train.java and Predict.java are based on svm_scale, svm_train and svm_predict (respectively).

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

			// Check labels
			for (IDataElement e : predictList)
			{
				Logger.getAnonymousLogger().log(Level.INFO, "Added label " + e.getClassLabel());
				
				// After calling predict() all entries in the List should have a class label
				if (e.isLabeled() == false) 
					fail();
			}
