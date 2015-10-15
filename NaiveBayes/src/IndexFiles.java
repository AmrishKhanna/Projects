import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexFiles {
public static final String FILES_TO_INDEX_DIRECTORY = "C:/Users/raghusab/Desktop/MachineLearning/Training Dataset/";
public static final String INDEX_DIRECTORY = "C:/Users/raghusab/Desktop/MachineLearning/index";
public static final boolean CREATE_INDEX = true; // true: drop existing index and create new one
// false: add new documents to existing index

public static void Index(){
final File docDir = new File(FILES_TO_INDEX_DIRECTORY);
if (!docDir.exists() || !docDir.canRead()) {
System.out.println("Document directory '"+ docDir.getAbsolutePath()+" does not exist");
System.exit(1);
}
 
Date start = new Date();
try {
System.out.println("Indexing to directory " + INDEX_DIRECTORY + "...");
 
Directory dir = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_DIRECTORY, "alt.atheism"));
Analyzer analyzer = new StandardAnalyzer();
IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
 
if (CREATE_INDEX) {
// Create a new index in the directory, removing any
// previously indexed documents:
iwc.setOpenMode(OpenMode.CREATE);
} else {
// Add new documents to an existing index:
iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
}
 
IndexWriter writer = new IndexWriter(dir, iwc);
indexDocs(writer, docDir);
 
writer.close();
 
Date end = new Date();
System.out.println(end.getTime() - start.getTime() + " total milliseconds");
 
} catch (IOException e) {
System.out.println(" caught a " + e.getClass()
+ "\n with message: " + e.getMessage());
}
}
 
static void indexDocs(IndexWriter writer, File file) throws IOException {
if (file.canRead()) {
if (file.isDirectory()) {
String[] files = file.list();
// an IO error could occur
if (files != null) {
for (int i = 0; i < files.length; i++) {
indexDocs(writer, new File(file, files[i]));
}
}
} else {
FileInputStream fis;
try {
fis = new FileInputStream(file);
} catch (FileNotFoundException fnfe) {
// at least on windows, some temporary files raise this
// exception with an ";access denied" message
// checking if the file can be read doesn't help
return;
}
 
try {
// make a new, empty document
Document doc = new Document();
 
Field pathField = new StringField("path", file.getPath(),
Field.Store.YES);
doc.add(pathField);
doc.add(new TextField("filename",file.getName(), Field.Store.YES));
 
doc.add(new LongField("modified", file.lastModified(),
Field.Store.NO));
 
doc.add(new TextField("contents", new BufferedReader(
new InputStreamReader(fis, "UTF-8"))));
 
if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
// New index, so we just add the document (no old
// document can be there):
System.out.println("adding " + file);
writer.addDocument(doc);
} else {
// Existing index (an old copy of this document may have
// been indexed) so
// we use updateDocument instead to replace the old one
// matching the exact
// path, if present:
System.out.println("updating " + file);
writer.updateDocument(new Term("path", file.getPath()),
doc);
}
} catch (Exception e) {
e.printStackTrace();
} finally {
fis.close();
}
}
}
}


	public static void queryUI(String pathIndex) throws IOException, ParseException {
		// Preparing the index
		Path indexPath = Paths.get(pathIndex);
		Directory fsdir = FSDirectory.open(indexPath);
		Analyzer analyzer = new StandardAnalyzer();
		IndexReader reader = DirectoryReader.open(fsdir);
		IndexSearcher searcher = new IndexSearcher(reader);
		// uncomment next line to switch to BM25 ranking.
		// searcher.setSimilarity(new BM25Similarity());
	 
		QueryParser parser = new QueryParser("text", analyzer);
	 
		// User UI
		boolean end = false;
		Scanner scanner = new Scanner(System.in);
		while (!end) {
			System.out.println("Query? (EXIT ends program): ");
			String queryString = scanner.nextLine();
			if (queryString.equals("EXIT")) {
				end = true;
			} else {
				Query q = parser.parse(queryString);
				TopDocs top20 = searcher.search(q, 20);
				ScoreDoc[] results = top20.scoreDocs;
				// Iterate over and display results
				System.out.println("Results (Top " + results.length + ")");
				for (int i = 0; i < results.length; i++) {
					float score = results[i].score;
					Document d = searcher.doc(results[i].doc);
					System.out.println(i + " (" + score + ") " + d.get("title"));
				}
			}
		}
		scanner.close();
		reader.close();
	}
	
/**
* @param args
*/
public static void main(String[] args) {
Index();
}
 
}
