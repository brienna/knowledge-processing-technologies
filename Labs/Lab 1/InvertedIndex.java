import java.util.*;

/** 
 * Represents an inverted index object.
 * @author Brienna
 */
public class InvertedIndex {
	String[] myDocs;
	ArrayList<String> termList;
	ArrayList<ArrayList<Integer>> docLists;
	
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
	 * Sorts query by each term's postings size, from small to large.
	 * @param query 
	 * @return sorted query
	 */
	public String[] optimizeQuery(String[] query) {
		int[] docFreqs = new int[query.length];
		for (int i = 0; i < query.length; i++) {
			int index = termList.indexOf(query[i]);
			if (index != -1) {
				docFreqs[i] = docLists.get(index).size();
			}
		}
		Arrays.sort(query, Comparator.comparing(s -> docFreqs[Arrays.asList(query).indexOf(s)]));
		return query;
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
	 * Finds matching documents for given query (of any size),
	 * taking into consideration whether the keywords are connected by AND or OR,
	 * and optimizing the query so that shorter postings are processed first
	 * @param query Keywords to search for
	 * @param isAnd If true, the keywords are connected by AND. If false, OR
	 * @return a list of documents that meet the query conditions
	 */
	public ArrayList<Integer> search(String[] query, boolean isAnd) {
		String[] optimizedQuery = optimizeQuery(query);
		int termId = 0; 
		
		// Get document list for first keyword
		ArrayList<Integer> result = null;
		while (result == null && termId < optimizedQuery.length) {
			result = search(optimizedQuery[termId]);
			System.out.println((termId+1) + ". " + optimizedQuery[termId]);
			termId++;
		}
		
		// Get document list for next keyword(s)
		while(termId < optimizedQuery.length) {
			ArrayList<Integer> result1 = search(optimizedQuery[termId]);
			System.out.println((termId + 1) + ". " + optimizedQuery[termId]);
			termId++;
			if (result1 == null) {
				continue;
			}
			
			// If the keywords are connected by AND operator
			if (isAnd) {
				// Merge current list with intermediate list
				result = merge(result, result1);
			} else {
				// Union current list with intermediate list
				result = union(result, result1);
			}
		}
		return result;
	}
	
	/** 
	 * Performs union on two document lists, 
	 * keeping unique documents from either list.
	 * @param list1 
	 * @param list2
	 * @return resulting list from the union
	 */
	private ArrayList<Integer> union(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		ArrayList<Integer> unionList = new ArrayList<Integer>();
		// Initialize pointers
		int id1 = 0, id2 = 0; 
		
		// Union until one list runs out
		while (id1 < list1.size() && id2 < list2.size()) {
			if (list1.get(id1).intValue() == list2.get(id2).intValue()) {
				unionList.add(list1.get(id1));
				id1++;
				id2++;
			} else if (list1.get(id1) < list2.get(id2)) {
				unionList.add(list1.get(id1));
				id1++;
			} else {
				unionList.add(list2.get(id2));
				id2++;
			}
		}
		
		// Print remaining documents of the larger list
		while (id1 < list1.size()) {
			unionList.add(list1.get(id1));
			id1++;
		}
		while (id2 < list2.size()) {
			unionList.add(list2.get(id2));
			id2++;
		}
		
		return unionList;
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
}
