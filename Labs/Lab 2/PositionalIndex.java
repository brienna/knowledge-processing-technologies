import java.util.ArrayList;

/**
 * Represents a positional index, a form of inverted index that
 * makes it easy to answer phrase queries where we are interested
 * in documents where the terms in the phrase appear together.
 *
 * Caveats: Does not consider direction of phrase query.
 * Ex. "new home sales" and "sales home new" will return the same
 * results. This is essentially what a positional index does however.
 * It only looks at the relative positions of the terms,
 * not the direction of the query. This is more useful.
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
	 * appear within k words of each other, e.g.
	 * "in" and "july" within 1 word of each other would
	 * retrieve the document id for "home sales rise in july".
	 * @param q1 One of the two terms
	 * @param q2 One of the two terms
	 * @param k distance of the two terms
	 * @return merged list of docIds
	 */
	public ArrayList<DocId> intersect(ArrayList<DocId> list1, ArrayList<DocId> list2, int k) {
		ArrayList<DocId> mergedList = new ArrayList<DocId>();

		// Initialize document list pointers
		int id1 = 0, id2 = 0;
		// Traverse both lists until one list runs out
		while (id1 < list1.size() && id2 < list2.size()) {
			// If both terms appear in the same document
			if (list1.get(id1).docId == list2.get(id2).docId) {
				// Create placeholder list for positions
				ArrayList<Integer> placeholder = new ArrayList<Integer>();

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
						if (Math.abs(pp1.get(pid1) - pp2.get(pid2)) <= k) {
							match = true;
							placeholder.add(pp2.get(pid2)); // Add position
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
						// Add positions also
						DocId doid = list1.get(id1);
						for (Integer pos : placeholder) {
							doid.insertPosition(pos);
						}
						mergedList.add(doid);
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

	public ArrayList<DocId> intersect2(String q1, String q2, int k) {
		ArrayList<DocId> answer = new ArrayList<DocId>();
		return answer;
	}

	/**
	 * Takes phrase query with multiple terms and returns a list
	 * of DocID objects which each represent a single posting
	 * that the terms appear in.
	 * @param query String consisting of multiple terms
	 * @return merged list of DocIds that each term appears in
	 */
	public ArrayList<DocId> phraseQuery(String query) {
		// Get documents that all query terms appear in
		String[] terms = query.split(" ");
		ArrayList<DocId> intermediate = docLists.get(termList.indexOf(terms[0]));
		for (int i = 1; i < terms.length; i++) {
			ArrayList<DocId> nextList = docLists.get(termList.indexOf(terms[i]));
			intermediate = intersect(intermediate, nextList, i);
		}

		return intermediate;
	}

	public static void main(String[] args) {
		// Initialize example docs
		String[] docs = {"new home sales top forecasts",
						 "home sales rise in july",
						 "increase in home sales in july",
						 "july new home sales rise",
						 "july new home sales top forecasts"
		};

		// Build positional index using example docs
		PositionalIndex pi = new PositionalIndex(docs);
		System.out.println(pi);

		// Get documents that two given terms appear adjacent to each other
		System.out.println("\nQuerying 'top forecasts'");
		ArrayList<DocId> result2 = pi.phraseQuery("top forecasts");
		for (DocId resultDoc : result2) {
			System.out.println("Found document ID: " + resultDoc.docId);
			System.out.println(docs[resultDoc.docId]);
		}

		// Get documents that three given terms appear adjacent to each other
		System.out.println("\nQuerying 'new home sales'");
		ArrayList<DocId> result = pi.phraseQuery("sales home new");
		for (DocId resultDoc : result) {
			System.out.println("Found document ID: " + resultDoc.docId);
			System.out.println(docs[resultDoc.docId]);

		}

		// Get documents that four given terms appear adjacent to each other
		System.out.println("\nQuerying 'july new home sales'");
		ArrayList<DocId> result3 = pi.phraseQuery("july new home sales");
		for (DocId resultDoc : result3) {
			System.out.println("Found document ID: " + resultDoc.docId);
			System.out.println(docs[resultDoc.docId]);
		}

		// Get documents that five given terms appear adjacent to each other
		System.out.println("\nQuerying 'july new home sales top'");
		ArrayList<DocId> result4 = pi.phraseQuery("july new home sales top");
		for (DocId resultDoc : result4) {
			System.out.println("Found document ID: " + resultDoc.docId);
			System.out.println(docs[resultDoc.docId]);
		}
	}
}
