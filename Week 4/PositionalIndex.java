import java.util.ArrayList;

/**
 * Represents a positional index, a form of inverted index that
 * makes it easy to answer phrase queries where we are interested
 * in documents where the terms in the phrase appear together.
 */
public class PositionalIndex {
	String[] myDocs;
	ArrayList<String> termList;
	ArrayList<ArrayList<DocId>> docLists;

	public PositionalIndex(String[] docs) {
		myDocs = docs;
		termList = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<DocId>>();
		ArrayList<DocId> docList;

		// Tokenize each document
		for (int i = 0; i < myDocs.length; i++) {
			String[] tokens = myDocs[i].split(" ");
			String token;
			// Check each token
			for (int j = 0; j < tokens.length; j++) {
				token = tokens[j];
				// If token has not been added to termList yet,
				if (!termList.contains(token)) {
					// Add it & create a posting to add to docLists
					termList.add(token);
					docList = new ArrayList<DocId>();
					DocId doid = new DocId(i, j); // i = docId, j = position of term in doc
					docList.add(doid);
					docLists.add(docList);
				} else {
					int index = termList.indexOf(token);
					docList = docLists.get(index);
					int k = 0;
					boolean match = false;
					// Check if document is already in postings
					for (DocId doid : docList) {
						// If yes, update posting to include new position
						if (doid.docId == i) {
							doid.insertPosition(j);
							docList.set(k, doid);
							match = true;
							break;
						}
						k++;
					}

					// If no, create posting to add to docList
					if (!match) {
						DocId doid = new DocId(i, j);
						docList.add(doid);
					}
				}
			}
		}
	}

	/**
	 * Returns a string representation of the positional index.
	 */
	public String toString() {
		String matrixString = new String();
		ArrayList<DocId> docList;

		// For each term
		for (int i = 0; i < termList.size(); i++) {
			// Show term
			matrixString += String.format("%-15s", termList.get(i));
			// Show term's postings
			docList = docLists.get(i);
			for (int j = 0; j < docList.size(); j++) {
				matrixString += docList.get(j) + "\t";
			}

			matrixString += "\n";
		}

		return matrixString;
	}

	/**
	 * Returns docIds of documents that two given terms 
	 * appear adjacent to each other, e.g. "in july" 
	 * for the document "home sales rise in july".
	 * @param q1 One of the two terms
	 * @param q2 One of the two terms
	 * @return merged list of docIds
	 */
	public ArrayList<Integer> intersect(String q1, String q2) {
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		
		// Get each term's document list
		ArrayList<DocId> list1 = docLists.get(termList.indexOf(q1));
		ArrayList<DocId> list2 = docLists.get(termList.indexOf(q2));
	
		// Initialize document list pointers
		int id1 = 0, id2 = 0;
		
		// Traverse both lists until one list runs out
		while (id1 < list1.size() && id2 < list2.size()) {
			// If both terms appear in the same document
			if (list1.get(id1).docId == list2.get(id2).docId) {
				// Get each term's position list
				ArrayList<Integer> pp1 = list1.get(id1).positionList;
				ArrayList<Integer> pp2 = list2.get(id2).positionList;
				
				// Initialize position list pointers
				int pid1 = 0, pid2 = 0;
				// Traverse first position list 
				while (pid1 < pp1.size()) {
					boolean match = false;
					// For each position in the first position list, 
					// traverse the second position list as well
					while (pid2 < pp2.size()) {
						// If the two terms appear together, we add this document
						// Else if the second position moves past the first, quit the second position list 
						// Otherwise we move to the next second position
						if (Math.abs(pp1.get(pid1) - pp2.get(pid2)) <= 1) {
							match = true;
							mergedList.add(list1.get(id1).docId);
							// Having found this match, we quit the second position list
							break;
						} else if (pp2.get(pid2) > pp1.get(pid1)) {
							break;
						} else {
							pid2++;
						}
					}
					
					// If a match is found, we also quit the first position list
					// Otherwise we move to the next position 
					if (match) {
						break;
					} else {
						pid1++;
					}
				}
				id1++;
				id2++;
			}
			else if (list1.get(id1).docId < list2.get(id2).docId) {
				id1++;
			} else {
				id2++;
			}
		}
		
		return mergedList;
	}
	
	public static void main(String[] args) {
		// Initialize example docs
		String[] docs = {"new home sales top forecasts",
						 "home sales rise in july",
						 "increase in home sales in july",
						 "july new home sales rise"
		};
		
		// Build positional index using example docs
		PositionalIndex pi = new PositionalIndex(docs);
		System.out.println(pi);
		
		// Get documents that two given terms appear adjacent to each other
		ArrayList<Integer> result = pi.intersect("in", "home");
		if (result != null) {
			for (Integer i : result) {
				System.out.println(docs[i.intValue()]);
			} 
		} else {
			System.out.println("No match!");
		}
	}
}
