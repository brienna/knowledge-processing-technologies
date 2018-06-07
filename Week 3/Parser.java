import java.util.*;
import java.io.*;

/** 
 * Focuses on the text processing before building inverted index,
 * parsing/tokenizing documents in a given folder and removing stopwords.
 * 
 * main() also shows how to stem an example word using Porter's algorithm in Stemmer.java,
 * which completes this file as a precursor to lab 1, where I will need to integrate
 * tokenizing, stopword removal, stemming, and inverted index construction. 
 */
public class Parser {
	String[] myDocs; // documents/files to be tokenized
	ArrayList<String> termList;
	String[] stopList = {"a", "is", "in", "so", "of", "at", "the", "to", 
			"an", "and", "it", "as", "be", "are"};
	
	public Parser(String foldername) {
		File folder = new File(foldername);
		File[] listOfFiles = folder.listFiles();
		myDocs = new String[listOfFiles.length];
		
		// Fill myDocs with filenames
		for (int i = 0; i < listOfFiles.length; i++) {
			String filename = listOfFiles[i].getName();
			System.out.println("File " + filename);
			myDocs[i] = filename;
		}
		
		// Sort stoplist to enable binary search with compareTo
		Arrays.sort(stopList);
		
		// Tokenize each file
		for (String doc : myDocs) {
			System.out.println("Parsing " + doc);
			String[] tokens = parse(foldername + "/" + doc);
			// For each token
			for (String token : tokens) {
				// Show if not stopword
				if (testIfStopword(token) == -1) {
					System.out.println(token);
				} 
			}
		}
	}
	
	public String[] parse(String filename) {
		String[] tokens = null;
		
		// Open file
		// Reminder: When you use IO, you need try/catch
		try {
			FileReader fReader = new FileReader(filename);
			BufferedReader bReader = new BufferedReader(fReader);
			String charSeq = new String();
			String line = null;
			while ((line = bReader.readLine()) != null) {
				// Concatenate all lines in the file into one character sequence
				charSeq += line.toLowerCase(); // case-folding
			}
			// Tokenize character sequence on predefined delimiters
			String delimiters = "[ .,?!:;$%&*+()\\-\\^]+"; // regex
			tokens = charSeq.split(delimiters);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return tokens;
	}
	
	/**
	 * Searches through stoplist to see if key is a stopword.
	 * 
	 * @param key A potential stopword
	 * @return index of match, or -1 if no match found
	 */
	public int testIfStopword(String key) {
		// Establish index range of stoplist segment, 
		// first starting with entire stoplist
		int low = 0; // first index 
		int high = stopList.length - 1; // last index 
		
		// Perform binary search
		while (low <= high) {
			// Get the middle stopword in the current segment of the stoplist
			int mid = low + (high - low)/2;
			// See if there is a match, using compareTo because, 
			// given we have sorted the stoplist alphabetically,
			// compareTo returns a negative integer, zero, or a positive integer 
			// if key is less than, equal to, or greater than stopword
			int result = key.compareTo(stopList[mid]);
			// If key is less than stopword, shorten right end of stoplist segment
			if (result < 0 ) {
				high = mid - 1; 
			} else if (result > 0) {
				// If key is greater than stopword, shorten left end of stoplist segment
				low = mid + 1;
			} else {
				// If key is equal to zero, key matches stopword
				// Return index of stopword
				return mid;
			}
		}
		
		// Return -1 if no match is found
		return -1;
	}
		
	
	public static void main(String[] args) {
		// In Eclipse run after adding "data as argument, Run > Run Configurations > Arguments
		Parser p = new Parser(args[0]);
		
		// See if stopword tester works properly
		System.out.println("in stoplist, 'is' has an index of " + p.testIfStopword("is"));
		
		// See how to stem "replacement" 
		Stemmer st = new Stemmer(); // Porter's algorithm
		st.add("replacement".toCharArray(), "replacement".length());
		st.stem();
		System.out.println("stemmed: " + st.toString());
	}
	
}
