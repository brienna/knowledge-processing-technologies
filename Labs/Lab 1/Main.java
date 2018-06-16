import java.io.File;
import java.util.ArrayList;

/**
 * Processes a number of text documents to generate indexed terms,
 * builds an inverted index for the terms and documents, and
 * formulates and processes queries using the constructed inverted index.
 * @author Brienna
 */
public class Main {
	private InvertedIndex index;
	private Parser p;
	
	/** 
	 * Constructs the main object.
	 * @param foldername - the folder to parse, index, and search
	 */
	public Main(String foldername) {
		// Parse documents in given foldername in arguments
		p = new Parser(foldername);
		
		// Construct inverted index with the parsed documents
		String[] parsedDocs = p.getParsedDocs();
		index = new InvertedIndex(parsedDocs);
		System.out.println(index);
		
		test();
	}

	/**
	 * Runs test queries.
	 */
	public void test() {
		// Query one keyword 
		String query = "strained"; 
		String[] parsedQuery = stemWords(query);
		System.out.println("Searching for '" + query + "' which is in the documents");
		processResult(index.search(parsedQuery, true));
		
		String query2 = "moon";
		String[] parsedQuery2 = stemWords(query2);
		System.out.println("\nSearching for '" + query2 + "' which is not in the documents");
		processResult(index.search(parsedQuery2, true));
		
		// Query two keywords
		String query3 = "fact number";
		String[] parsedQuery3 = stemWords(query3);
		System.out.println("\nSearching for '" + query3 + "', both of which are in the documents");
		processResult(index.search(parsedQuery3, true));
		
		String query4 = "crown queen";
		String[] parsedQuery4 = stemWords(query4);
		System.out.println("\nSearching for '" + query4 + "', the first of which is in the documents");
		processResult(index.search(parsedQuery4, true));
		
		// Query two keywords that are connected using OR operator
		String query5 = "fact number";
		String[] parsedQuery5 = stemWords(query5);
		System.out.println("\nSearching for '" + query5 + "' with OR operator, both of which are in the documents");
		processResult(index.search(parsedQuery5, false));
		
		String query6 = "check facts";
		String[] parsedQuery6 = stemWords(query6);
		System.out.println("\nSearching for '" + query6 + "' with OR operator, both of which are in the documents");
		processResult(index.search(parsedQuery6, false));
		
		// Query three or more keywords
		String query7 = "time story star quick";
		String[] parsedQuery7 = stemWords(query7);
		System.out.println("\nSearching for '" + query7 + "', all of which are in the documents");
		processResult(index.search(parsedQuery7, true));
		
		String query8 = "watch a movie";
		String[] parsedQuery8 = stemWords(query8);
		System.out.println("\nSearching for '" + query8 + "', all of which are in the documents");
		processResult(index.search(parsedQuery8, true));
	}
	
	/**
	 * Processes and prints the result of each query.
	 */
	public void processResult(ArrayList<Integer> result) {
		System.out.println("Results: ");
		if (result != null) {
			for (Integer i : result) {
				System.out.println(i + ": " + p.getFileName(i));
			}
		} else {
			System.out.println("No match");
		}
	}
	
	/**
	 * Stems the given string (usually query).
	 * @return stemmed string as an array
	 */
	public String[] stemWords(String str) {
		Stemmer st = new Stemmer();
		String[] words = str.split(" ");
		String[] stemmed = new String[words.length];
		for (int i = 0; i < words.length; i++) {
			st.add(words[i].toCharArray(), words[i].length());
			st.stem();
			stemmed[i] = st.toString();
		}
		return stemmed;
	}
	
	/**
	 * Starts the whole program.
	 * @param args - [0] holds the foldername string
	 */
	public static void main(String[] args) {
		new Main(args[0]);
	}

}
