import java.util.*;

/**
 * Indexes terms in unstructured text,
 * appearing as a table where each column
 * denotes a document, and each row denotes 
 * a term. If a term appears in a document, 
 * that cell gets filled with 1, otherwise 0.
 */
public class IncidenceMatrix {
	String[] myDocs;
	ArrayList<String> termList;
	ArrayList<int[]> docLists;
	
	/**
	 * Construct an incidence matrix
	 * @param docs List of input strings (or docs)
	 */
	public IncidenceMatrix(String[] docs) {
		myDocs = docs;
		termList = new ArrayList<String>();
		docLists = new ArrayList<int[]>();
		
		// For each document
		for (int i = 0; i < myDocs.length; i++) {
			// Tokenize document by spaces
			String[] tokens = myDocs[i].split(" ");
			for (String token : tokens) {
				// If token is new
				if (!termList.contains(token)) {
					// Store it (think of it as adding a row)
					termList.add(token);
					// Create document list & mark the corresponding entry for this term/document 
					int[] docList = new int[myDocs.length];
					docList[i] = 1;
					docLists.add(docList);
				} else {
					// If token has already been stored
					// Find its index
					int index = termList.indexOf(token);
					// Use this index to remove its document list (so it can be updated)
					int[] docList = docLists.remove(index);
					// Mark the corresponding entry for this term/document
					docList[i] = 1;
					// Return updated document list to collection of document lists
					docLists.add(index, docList);
				}
			}
		}
	}
	
	public String toString() {
		String matrixString = new String();
		for (int i = 0; i < termList.size(); i++) {
			matrixString += String.format("%-15s", termList.get(i));
			int[] docList = docLists.get(i);
			for (int j = 0; j < docList.length; j++) {
				matrixString += docList[j] + "\t";
			}
			matrixString += "\n";
		}
		return matrixString;
	}
	
	public static void main(String[] args) {
		String[] docs = {"new home sales top forecasts",
						 "home sales rise in july",
						 "increase in home sales in july",
						 "july new home sales rise"};
		IncidenceMatrix matrix = new IncidenceMatrix(docs);
		System.out.println(matrix);
	}
}
