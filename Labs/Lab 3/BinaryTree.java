import java.util.*;

/**
 * Binary search tree structure that stores the term dictionary.
 */
public class BinaryTree {
	
	/**
	 * Insert a node to a subtree. Assumes that our key values are distinct,
	 * because we only compare if it is greater or less than the node
	 * @param node root node of subtree
	 * @param nodeToBeAdded the node to be inserted into the subtree
	 */
	public void add(Node node, Node nodeToBeAdded) {
		// If the term in the node we want to insert is lower in ABC than root node,
		// we deal with the left subtree of the root node
		if (nodeToBeAdded.term.compareTo(node.term) < 0) {
			// If there exists a left subtree, go into it
			if (node.left != null) {
				add(node.left, nodeToBeAdded); // recursion to continue traversing tree
			} else {
				// Otherwise add node
				System.out.println("Inserted " + nodeToBeAdded.term + " to left of node " + node.term);
				node.left = nodeToBeAdded;
			}
		} else if (nodeToBeAdded.term.compareTo(node.term) > 0) {
			// If there exists a right subtree, go into it
			if (node.right != null) {
				add(node.right, nodeToBeAdded); // recursion to continue traversing tree
			} else {
				// Otherwise add node
				System.out.println("Inserted " + nodeToBeAdded.term + " to right of node " + node.term);
				node.right = nodeToBeAdded;
			}
		}
	}
	
	/**
	 * Searches a term in a subtree.
	 * @param n root node of a subtree
	 * @param term a query term
	 * @return tree nodes with term that match the query term or null if no match
	 */
	public Node search(Node n, String term) {
		// If current node is null, it means that we exhausted
		// the tree without finding any match for the key
		if (n == null) {
			return null;
		}
		
		// If the current node matches the key, return it
		if (n.term.equals(term)) {
			return n;
		} else if(n.term.compareTo(term) > 0) {
			// If current node is greater than key in ABC order, search left subtree,
			// skipping right tree cuz it contains all greater terms in ABC order
			return search(n.left, term);
		} else {
			// Otherwise search right tree
			return search(n.right, term);
		}
	}
	
	
	
}
