import java.util.*;
import java.io.*;

/** 
 * Represents a parser object which parses the text 
 * in a given folder into stemmed tokens, minus stopwords.
 * @author Brienna
 */
public class Parser {
	private String[] myDocs; // documents to be parsed
	private String[] parsedDocs; // parsed documents 
	private ArrayList<String> termList;
	private ArrayList<String> stopList;
	private File[] listOfFiles;
	
	/**
	 * Constructs a parser object for given folder.
	 * @param foldername - the folder containing the documents to be parsed
	 */
	public Parser(String foldername) {
		// Initialize all properties
		File folder = new File(foldername);
		listOfFiles = folder.listFiles();
		myDocs = new String[listOfFiles.length]; 
		parsedDocs = new String[listOfFiles.length];
		stopList = new ArrayList<String>();
		
		// Fill stopList with words from stoplist.txt
		try {
			FileReader fReader = new FileReader("stopwords.txt");
			BufferedReader bReader = new BufferedReader(fReader);
			String stopwords = new String();
			String line = null;
			while ((line = bReader.readLine()) != null) {
				// Add stopword to stopList
				String stopword = line.toLowerCase();
				stopList.add(stopword);
			}
			
			// Sort stoplist to enable binary search with compareTo
			Collections.sort(stopList);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		// Fill myDocs with filenames
		for (int i = 0; i < listOfFiles.length; i++) {
			String filename = listOfFiles[i].getName();
			System.out.println("File " + filename);
			myDocs[i] = filename;
		}
		System.out.println();
			
		// Tokenize each document, adding the tokens to the parsed document
		String parsedDoc = "";
		Stemmer st = new Stemmer();
		for (int i = 0; i < myDocs.length; i++) {
			System.out.println("Parsing " + myDocs[i]);
			String[] tokens = parse(foldername + "/" + myDocs[i]);
			// For each token, 
			for (String token : tokens) {
				// If stopword, skip
				if (testIfStopword(token) != -1) {
					continue;
				} else {
					// Otherwise, stem it with Porter's algorithm 
					// and add it to the parsed doc
					st.add(token.toCharArray(), token.length());
					st.stem();
					//System.out.println(token + st.toString());
					parsedDoc += st + " ";
				}
			}
			// Save parsed doc to collection of parsed docs
			parsedDocs[i] = parsedDoc;
			System.out.println(parsedDoc + "\n");
			// Reset parsed doc string to parse next document
			parsedDoc = ""; 
		}
	}
	
	/**
	 * Parses the document into tokens.
	 * @param filename - the document to parse
	 * @return the parsed document
	 */
	private String[] parse(String filename) {
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
	private int testIfStopword(String key) {
		// Establish index range of stoplist segment, 
		// first starting with entire stoplist
		int low = 0; // first index 
		int high = stopList.size() - 1; // last index 
		
		// Perform binary search
		while (low <= high) {
			// Get the middle stopword in the current segment of the stoplist
			int mid = low + (high - low)/2;
			// See if there is a match, using compareTo because, 
			// given we have sorted the stoplist alphabetically,
			// compareTo returns a negative integer, zero, or a positive integer 
			// if key is less than, equal to, or greater than stopword
			int result = key.compareTo(stopList.get(mid));
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
	
	/**
	 * Returns all parsed documents.
	 */
	public String[] getParsedDocs() {
		return parsedDocs;
	}
	
	/**
	 * Returns name of file at specified index in folder.
	 */
	public String getFileName(int index) {
		return listOfFiles[index].getName();
	}
}
