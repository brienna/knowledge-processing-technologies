import java.util.ArrayList;

/**
 * A single node in a binary tree.
 */
public class Node {
	Node left;
	Node right;
	String term;
	ArrayList<Integer> docLists;
	
	/**
	 * Creates a node using a term and a document list.
	 * @param term the term/value in the node
	 * @param docList the ids of the documents that contain the term
	 */
	public Node(String term, ArrayList<Integer> docList) {
		this.term = term;
		this.docLists = docList;
	}
}
