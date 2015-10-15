import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;


public class NaiveBayesClassifier {
	private static final String testDataDirectory="C:\\Users\\amrishkh\\Desktop\\Test";
	private static final String trainingDataDirectory="C:\\Users\\amrishkh\\Desktop\\Train";
	private HashMap<String,List<File>> fileLists=new HashMap<>(); 
	private ArrayList<String> classNames;
	private StopWordAnalyzer analyzer;
	private PorterStemmer stemmer;
	private ArrayList<MemoryFile> memFiles=new ArrayList<>();
	public static void main(String args[]) throws Exception
	{
	NaiveBayesClassifier classifier=new NaiveBayesClassifier();
	classifier.classify();
	}

	public void classify() throws Exception
	{
		analyzer=new StopWordAnalyzer();
		stemmer=new PorterStemmer();
		ArrayList<OccuranceProbabilties> op=new ArrayList<OccuranceProbabilties>();
		File[] listOfTestFiles=new File(testDataDirectory).listFiles();
		File[] listOfTrainingFiles=new File(trainingDataDirectory).listFiles();
		classNames=new ArrayList<>();
		for(File f: listOfTrainingFiles)
		{
			classNames.add(f.getName());
			OccuranceProbabilties oc=new OccuranceProbabilties();
			oc.setClassName(f.getName());
			oc.setOccuranceMap(new HashMap<String,Double>());
			op.add(oc);
			File classTraining[]=new File(trainingDataDirectory+"/"+f.getName()).listFiles();
			fileLists.put(f.getName(), (List<File>) Arrays.asList(classTraining));
		}
		
		
		loadIntoMemory();
		
		
		for(File f:listOfTestFiles)
		{
			TreeMap<String,BigDecimal> probabilities=new TreeMap<>();
			System.out.println("---------------TEST FILE-----------");
			for(String currentClass:classNames)
			{
				TestRecord testRecord=readOneTestFile(f);
				BigDecimal prob=new BigDecimal("1.0");
				//System.out.println("Test record size:"+testRecord.getWords().size());
				for(String word:testRecord.getWords())
				{
					double termProbInClass=0.0;
					if(getFromExistingProbability(word,op,currentClass)!=0.0)
					{
						termProbInClass=getFromExistingProbability(word,op,currentClass);
						//System.out.println("Optimized");
					}
					else
					{
						termProbInClass=calculateProbabilityInMemory(word,op,currentClass);
						for(OccuranceProbabilties oc:op)
						{
							if(oc.getClassName().equalsIgnoreCase(currentClass))
							{
								oc.getOccuranceMap().put(word, termProbInClass);
								break;
							}
						}
					}
				prob=prob.multiply(new BigDecimal(""+termProbInClass));
				//System.out.println("Prob:"+prob+"  TermProb in class"+termProbInClass);
				}
				probabilities.put(currentClass, prob);
			}
			//System.out.println(probabilities);
			Entry<String,BigDecimal> maxEntry = null;

			for(Entry<String,BigDecimal> entry : probabilities.entrySet()) {
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue())>0) {
			        maxEntry = entry;
			    }
			}
		System.out.println(f.getName()+"--->"+maxEntry.getKey());
		probabilities.clear();
		}	
	}
	
	private void loadIntoMemory() throws IOException
	{
		for(String s:classNames){
		List<File> classTraining=fileLists.get(s);
		for(File f:classTraining)
		{
			ArrayList<String> words=new ArrayList<>();
			
			MemoryFile memfile=new MemoryFile();
			memfile.setFileName(f.getName());
			memfile.setClassname(s);
			String fileAsString=FileUtils.readFileToString(f).replaceAll("\\P{L}", " ").toLowerCase().replaceAll("\n"," ");
			fileAsString=fileAsString.replaceAll("\\s+", " ");
			String[] lines=fileAsString.split("\\s+");
			for(String eachWord:lines){
				eachWord=analyzer.removeStopWords(eachWord);
				String processedWord=stemmer.stem(eachWord);
				if(processedWord.length()>1)
				{
					words.add(processedWord);
				}
			}
			memfile.setContent(words);
			memFiles.add(memfile);
		}
		}
	}
	
	private double calculateProbabilityInMemory(String word,ArrayList<OccuranceProbabilties> op, String currentClass)
	{
		double prob=0.0;
		int count=0;
		int occurances=0;
		for(MemoryFile memFile:memFiles)
		{
			if(memFile.getClassname().equals(currentClass))
			{
				occurances+=Collections.frequency(memFile.getContent(), word)*60;
				count+=memFile.getContent().size();
			}
		}
		prob=(double)((double)occurances+50.0)/(double)((double)count+100.0);
		return prob;
	}
	
	private double calculateProbability(String word,
			ArrayList<OccuranceProbabilties> op, String currentClass) throws Exception {
		// TODO Auto-generated method stub
		double probability=0.0;
		List<File> classTraining=fileLists.get(currentClass);
		ArrayList<String> words=new ArrayList<>();
		double count=0.0;
		for(File f:classTraining)
		{
			String fileAsString=FileUtils.readFileToString(f).replaceAll("\\P{L}", " ").toLowerCase().replaceAll("\n"," ");
			fileAsString=fileAsString.replaceAll("\\s+", " ");
			String[] lines=fileAsString.split("\\s+");
			for(String eachWord:lines){
				
					eachWord=analyzer.removeStopWords(eachWord);
					String processedWord=stemmer.stem(eachWord);
					if(processedWord.length()>1)
					{
						words.add(processedWord);
						if(processedWord.equalsIgnoreCase(word))
						{
							count+=20;
						}
					}
			}
		}
		probability=(double)((double)count+50.0)/(double)((double)words.size()+100.0);
		return probability;
	}

	public double getFromExistingProbability(String word, ArrayList<OccuranceProbabilties>probabilties, String className)
	{
		double value=0.0;
		for(OccuranceProbabilties op:probabilties)
		{
			if(op.getClassName().equals(className))
			{
				Set<String> myKeys=op.getOccuranceMap().keySet();
				for(String s:myKeys)
				{
					if(op.getOccuranceMap().get(s) != null&&s.equals(word))
					{
						value=op.getOccuranceMap().get(s);
					}
				}
			}
		}
		
		return value;
	}

	public TestRecord readOneTestFile(File f)
	{
		TestRecord record=new TestRecord();
		String currentLine;
		ArrayList<String> words=new ArrayList<>();
		try{
		BufferedReader br=new BufferedReader(new FileReader(f));
		while((currentLine=br.readLine())!=null)
		{
			currentLine=currentLine.toLowerCase(); //convert everyword to lower case
			currentLine=currentLine.replaceAll("\\P{L}", " "); //only alphabets
			currentLine=currentLine.replaceAll("\n"," ");
			currentLine=currentLine.replaceAll("\\s+", " ").trim();
			String lineWords[]=currentLine.split("\\s+");
			for(String eachWord:lineWords)
			{
				eachWord=analyzer.removeStopWords(eachWord);
				String processedWord=stemmer.stem(eachWord);
				if(processedWord.length()>1)
				{
				words.add(processedWord);
				}
				
			}
		}
		record.setRecordId(Integer.parseInt(f.getName().replaceAll("\\D+","")));
		record.setWords(words);
		br.close();
		}
		catch(Exception e)
		{
			//some error in processing
		}
		return record;
	}
}