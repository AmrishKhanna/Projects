package machinelearning;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;


public class NaiveBayes {

	// Data structure that stores word and its probabilities.....Learning stuff is done here 
	private static HashMap<String, HashMap<String, Double>> Classes = new HashMap<String, HashMap<String, Double>>();
	private static HashMap<String, Double> probabilities;
	private static StopWords sw = new StopWords();
	private static PorterStemmer ps = new PorterStemmer();
	private static ArrayList<InMemoryTest> fileInfo = new ArrayList<InMemoryTest>();
	private static Scanner reader;

	// Removing unwanted characters and spaces
	public static ArrayList<String> Cleaning(String content) {
		content = content.replaceAll("[!@#$%^|&*()~`,-?./\\;\\n+:'\"]", " ");
		content = content.replaceAll("^A-Za-z", "").replaceAll("_+", "");
		content = content.replaceAll("\\s+", " ");
		String[] words = content.split(" ");
		ArrayList<String> wordlist = new ArrayList<String>(Arrays.asList(words));
		return wordlist;
	}
	
	// Returns the index of the class with maximum probability
		public static int maxProbability(ArrayList<BigDecimal> prob)
		{
			int Index = 0;
			for (int i = 1; i < prob.size(); i++){
				BigDecimal nextProbability = prob.get(i);
				if ((nextProbability.compareTo(prob.get(Index))>0)){
					Index = i;
				}
			} 

			return Index;
		}

	//***************************************************************************************************************
	// Disk file read method
	//***************************************************************************************************************
		
	// Naive Bayesian Classification that classifies test file based on training dataset files
	public static String naiveBayesLearning(ArrayList<String> terms, String traindir) {

		//Opening Training set directory
		File directory = new File(traindir);
		
		File[] listOfSubDirectory = directory.listFiles();
		int totalNoOfWords, noOfOccur;
		ArrayList<BigDecimal> totalProb = new ArrayList<BigDecimal>();

		for (int i = 0; i < listOfSubDirectory.length; i++) {
			totalProb.add(new BigDecimal("1.0"));

			File subdirectory = new File(listOfSubDirectory[i].getAbsolutePath());
			

			//For first time add directory name as key to Classes HashMap
			if (!Classes.containsKey(subdirectory.getName())) 
			{
				Classes.put(subdirectory.getName(), null);
			}
			File[] listOfFiles = subdirectory.listFiles();

			//For each word in the test file
			for (String term : terms) {
				totalNoOfWords = 0;
				noOfOccur = 0;
				double probability = 1.0;

				//For each file in training set subdirectory
				for (int j = 0; j < listOfFiles.length; j++) {
					File file = listOfFiles[j];

					//First time creating probabilities hashmap or getting hashmap
					if (Classes.get(subdirectory.getName()) == null) 
					{
						probabilities = new HashMap<String, Double>();
					} 
					else 
					{
						probabilities = Classes.get(subdirectory.getName());
					}
					if(probabilities.containsKey(term))
					{
						probability = probabilities.get(term);
					}
					else
					{
						if (file.isFile()) {
							ArrayList<String> trainingWords = new ArrayList<String>();
							try {
								String content = FileUtils.readFileToString(file);
								ArrayList<String> words = Cleaning(content);

								// for words in training file remove stop word and stem it
								for (String word : words) {
									if (sw.removeStopwords(word)) {
										trainingWords.add(ps.stem(word.toLowerCase()));
									}
								}
								
								// For stemmed training word compare with the test word and find probability
								for (String word : trainingWords) {
									if (term.equals(word)) {
										noOfOccur+=30;
									}
								}
								totalNoOfWords = totalNoOfWords + trainingWords.size();
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
						}
						// Normalizing probability to make it no equal to zero
						probability = (((double)noOfOccur)+50) / (((double)totalNoOfWords)+100);
					}
				}
				totalProb.set(i, (totalProb.get(i).multiply(new BigDecimal(""+probability))));
				probabilities.put(term, probability);				
				Classes.put(subdirectory.getName(), probabilities);
			}
		}
		return listOfSubDirectory[maxProbability(totalProb)].getName();
	}
	
	//************************************************************************************************************
	//In Memory Computation Method
	//************************************************************************************************************
	
	// Reading training data to memory
	public static void InitialTrainingSetRead(String traindir)
	{
		//Opening Training set directory
		File directory = new File(traindir);
		File[] listOfSubDirectory = directory.listFiles();
		for(int k=0;k<listOfSubDirectory.length;k++)
		{					
			String subdirectoryPath = listOfSubDirectory[k].getAbsolutePath();
			File subdirectory = new File(subdirectoryPath);
			File[] listOfFiles = subdirectory.listFiles();
			InMemoryTest memtest = new InMemoryTest();
			memtest.setCategoryname(listOfSubDirectory[k].getName());
			ArrayList<String> templist = new ArrayList<String>();
			for(File files : listOfFiles)
			{
				try {
					String content = FileUtils.readFileToString(files);
					ArrayList<String> words = Cleaning(content);

					// for words in training file remove stop word and stem it
					for (String word : words) {
						if (sw.removeStopwords(word)) {
							templist.add(ps.stem(word.toLowerCase()));
						}
					}	
				} 
				catch (IOException e) {
					e.printStackTrace();
				}							
			}	
			memtest.setContent(templist);
			fileInfo.add(memtest);
		}
	}
	
	// Naive Bayesian Classification for in memory computation
	public static String InMemoryComputationTest(ArrayList<String> terms)
	{	
		int totalNoOfWords, noOfOccur;
		ArrayList<BigDecimal> totalProb = new ArrayList<BigDecimal>();
		
		//Learning Process
		for(int i=0;i<fileInfo.size();i++)
		{			
			totalProb.add(new BigDecimal("1.0"));

			//For first time add directory name as key to Classes HashMap
			if (!Classes.containsKey(fileInfo.get(i).Categoryname)) 
			{
				Classes.put(fileInfo.get(i).Categoryname, null);
			}

			for (String term : terms)
			{
				totalNoOfWords = 0;
				noOfOccur = 0;
				double probability = 1.0;

				//First time creating probabilities hashmap or getting appropriate hashmap
				if (Classes.get(fileInfo.get(i).Categoryname) == null) 
				{
					probabilities = new HashMap<String, Double>();
				} 
				else 
				{
					probabilities = Classes.get(fileInfo.get(i).Categoryname);
				}
				if(probabilities.containsKey(term))
				{
					probability = probabilities.get(term);
				}
				else
				{
					ArrayList<String> trainingWords = new ArrayList<String>();
					trainingWords = fileInfo.get(i).getContent();

					// For stemmed training word compare with the test word and find probability
					for (String word : trainingWords) {
						if (term.equals(word)) {
							noOfOccur+=30;
						}
					}
					totalNoOfWords = trainingWords.size();
					
					// Normalizing probability to make it no equal to zero
					probability = (((double)noOfOccur)+50) / (((double)totalNoOfWords)+100);

				}
				totalProb.set(i, (totalProb.get(i).multiply(new BigDecimal(""+probability))));
				probabilities.put(term, probability);
				Classes.put(fileInfo.get(i).Categoryname, probabilities);
			}
		}		
		return fileInfo.get(maxProbability(totalProb)).getCategoryname();
	}

	// Processing test file one by one
	public static void ReadTestFile(int choice, String traindir, String testdir)
	{		
		File directory = new File(testdir);

		File[] listOfFiles = directory.listFiles();
		String Classitbelongs;		

		System.out.println("Reading test files and classifying them.......");
		System.out.println();
		System.out.println("<------------------------------Classified Test Files---------------------------------->");
		for (File file : listOfFiles) {
			ArrayList<String> testWords = new ArrayList<String>();
			if (file.isFile()) {
				try {
					String content = FileUtils.readFileToString(file);
					ArrayList<String> words = Cleaning(content);

					for (String word : words) {
						if (sw.removeStopwords(word)) {
							testWords.add(ps.stem(word.toLowerCase()));
						}
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}

				switch(choice)
				{
				case 1:// File Read operation
					Classitbelongs = naiveBayesLearning(testWords, traindir);
					System.out.println("File Name : " + file.getName()+ "\t\t" + "Class : "+Classitbelongs);
					break;
				case 2:// In Memory Computation Test
					Classitbelongs = InMemoryComputationTest(testWords); 
					System.out.println("File Name : " + file.getName()+ "\t\t" + "Class : "+Classitbelongs);
					break;						
				}	
			}
		}		
	}
	
	// Main Function that gets the user choice
	public static void main(String[] args) 
	{		
		int choice;
		String testdir, traindir;
		String input;
		reader = new Scanner(System.in);
		System.out.println();
		
		// Getting input training path
		System.out.println("Enter the training dataset path:");
		input = reader.nextLine();
		traindir = input.replaceAll("\\\\", "/");
		System.out.println();
		
		// Getting input test path
		System.out.println("Enter the test dataset path");
		input = reader.nextLine();
		testdir = input.replaceAll("\\\\", "/");
		System.out.println();
		
		System.out.println("Enter Your choice");
		System.out.println("1. File Read Method");
		System.out.println("2. In Memory Computation Method");
		choice = reader.nextInt();
		System.out.println();
		
		if (choice == 2)
		{
			System.out.println("Loading training dataset in to memory...");
			InitialTrainingSetRead(traindir);
		}
		ReadTestFile(choice, traindir, testdir);			
		System.gc();
	}
}
