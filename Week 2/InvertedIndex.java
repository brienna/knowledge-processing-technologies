import java.util.*;

/*
 * Represents an inverted index, which tackles the scalability 
 * problem of an incidence matrix by focusing on only the occurrences
 * of a term instead the absences as well. 
 */
public class InvertedIndex {
	String[] myDocs;
	
	ArrayList<String> termList;
	ArrayList<ArrayList<Integer>> docLists;	
	// Note: I would prefer to use a HashMap, but prof uses parallel ArrayLists
	
	/**
	 * Constructs an inverted index for given documents. 
	 * @param docs - the documents to be indexed
	 */
	public InvertedIndex(String[] docs) {
		// Initialize attributes
		myDocs = docs;
		termList = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Integer>>();
		
		// Create document list variable to be initialized later (frequently)
		ArrayList<Integer> docList;
		
		// For each document
		for (int i = 0; i < myDocs.length; i++) {
			// Identify current document by its index (docID)
			Integer docID = new Integer(i);
			// Tokenize document by spaces
			String[] tokens = myDocs[i].split(" ");
			for (String token : tokens) {
				// If token is new
				if (!termList.contains(token)) {
					// Store it
					termList.add(token);
					// Start document list for token
					docList = new ArrayList<Integer>();
					// Add docID to document list 
					docList.add(docID);
					// Add document list to collection of document lists
					docLists.add(docList);
				} else { 
					// If token has been stored 
					// Find its index (cuz we're using 2 parallel ArrayLists)
					int index = termList.indexOf(token);
					// Use docID to retrieve its corresponding document list
					docList = docLists.get(index);
					// If current document is new
					if (!docList.contains(docID)) {
						// Store it
						docList.add(docID);
						// Update collection of document lists
						docLists.set(index, docList);
					}
				}
			}
 		}
	}
	
	/**
	 * Returns inverted index as a printable string.
	 */
	public String toString() {
		String matrixString = new String();
		ArrayList<Integer> docList;
		// For each token
		for (int i = 0; i < termList.size(); i++) {
			// Add token to string
			matrixString += String.format("%-15s", termList.get(i));
			// Add corresponding document list to string
			docList = docLists.get(i);
			// Then add the whole matrix too
			for (int j = 0; j < docList.size(); j++) {
				matrixString += docList.get(j) + "\t";
			}
			matrixString += "\n";
		}
		return matrixString;
	}
	
	/**
	 * Returns the document list for the given term.
	 * @param query Keyword consisting of one term
	 */
	public ArrayList<Integer> search(String query) {
		int index = termList.indexOf(query);
		if (index < 0) {
			return null;
		} else {
			return docLists.get(index);
		}
	}
	
	/**
	 * Finds matching documents for given query (of any size)
	 * @param query Keywords to search for
	 * @return a list of documents that meet the query conditions
	 */
	public ArrayList<Integer> search(String[] query) {
		// Get document list for first keyword
		ArrayList<Integer> result = search(query[0]);
		int termId = 1; // index of the second keyword
		while (termId < query.length) {
			// Get document list for second keyword
			ArrayList<Integer> result1 = search(query[termId]);
			// Merge current list with intermediate list
			result = merge(result, result1);
			termId++;
		}
		return result;
	}
	
	/**
	 * Intersects two document lists, 
	 * keeping unique documents that appear in both lists.
	 * @param list1
	 * @param list2
	 * @return resulting list from the intersection
	 */
	private ArrayList<Integer> merge(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		// Initialize pointers
		int id1 = 0, id2 = 0;
		while (id1 < list1.size() && id2 < list2.size()) {
			if (list1.get(id1).intValue() == list2.get(id2).intValue()) {
				mergedList.add(list1.get(id1));
				id1++;
				id2++;
			} else if (list1.get(id1) < list2.get(id2)) {
				id1++;
			} else {
				id2++;
			}
		}
		return mergedList;
	}
	
	public static void main(String[] args) {
		// Create sample document collection
		String[] docs = {"new home sales top forecasts",
						 "home sales rise in july",
						 "increase in home sales in july",
						 "july new home sales rise"
		};
		
		// Build & print structure
		InvertedIndex inverted = new InvertedIndex(docs);
		System.out.print(inverted);
		
		// Search structure for specific term & print documents that contain it
		String query = "top";
		ArrayList<Integer> result = inverted.search(query);
		if (result != null) {
			for (Integer i : result) {
				System.out.println(docs[i.intValue()]);
			}
		} else {
			System.out.println("No match for " + query);
		}
		
		// Search structure for multiple keywords & print documents that contain them
		String[] query2 = {"july", "in"};
		ArrayList<Integer> result2 = inverted.search(query2);
		if (result2 != null) {
			for (Integer i : result2) {
				System.out.println(docs[i.intValue()]);
			} 
		} else {
			System.out.println("No match for " + query2);
		}
	}

}
