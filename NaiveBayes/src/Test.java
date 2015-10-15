import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class Test{
    
	public static final String FILES_TO_INDEX_DIRECTORY = "C:/Users/raghusab/Desktop/MachineLearning/Training Dataset/alt.atheism/";
	public static Directory INDEX_DIRECTORY=null;

	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";

	public static void main(String[] args) throws Exception {
		System.out.print(9.655967679731993E-52>2.4857044001971094E-52);
	/*	createIndex();
		searchIndex("mushrooms");
		searchIndex("steak");
		searchIndex("steak AND cheese");
		searchIndex("steak and cheese");
		searchIndex("bacon OR cheese");

	}

	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
		INDEX_DIRECTORY= FSDirectory.open(new File("C:/Users/raghusab/Desktop/MachineLearning/index"));
		boolean recreateIndexIfExists = true;
		IndexWriterConfig config=new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(INDEX_DIRECTORY, config);
		File dir = new File(FILES_TO_INDEX_DIRECTORY);
		File[] files = dir.listFiles();
		for (File file : files) {
			Document document = new Document();

			String path = file.getCanonicalPath();
			document.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.NOT_ANALYZED));

			Reader reader = new FileReader(file);
			document.add(new Field(FIELD_CONTENTS, reader));

			indexWriter.addDocument(document);
		}
		//indexWriter.optimize();
		indexWriter.close();
	}

	public static void searchIndex(String searchString) throws IOException, ParseException {
		System.out.println("Searching for '" + searchString + "'");
		Directory directory = FSDirectory.open(new File("C:/Users/raghusab/Desktop/MachineLearning/index"));
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser(FIELD_CONTENTS, analyzer);
		Query query = queryParser.parse(searchString);
		TopDocs hits = indexSearcher.search(query,0);
		int count=hits.totalHits;
		System.out.println("Number of hits: " + count);

		Iterator<Hit> it = hits.iterator();
		while (it.hasNext()) {
			Hit hit = it.next();
			Document document = hit.getDocument();
			String path = document.get(FIELD_PATH);
			System.out.println("Hit: " + path);
		}
*/
	}


}