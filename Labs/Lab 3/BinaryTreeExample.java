import java.util.Arrays;

/**  
 * Tree structure for building word index.
 */
class Node {
	Node left;
	Node right;
	int value;
	
	public Node(int value) {
		this.value = value;
	}
}

public class BinaryTreeExample {
	Node root;
	
	public BinaryTreeExample() {
		
	}
	
	/**
	 * Builds a balanced tree, avoiding a situation like a long linked list.
	 * @param keys An array of values 
	 */
	public BinaryTreeExample(int[] keys) {
		Arrays.sort(keys);
		int start = 0; // keys pointer
		int end = keys.length - 1; 
		int mid = (start+end) / 2;
		
		// Set middle-valued key as root
		Node r = new Node(keys[mid]);
		root = r; 
		// Add all the values from the first key to the middle-1 key to the left tree of root
		add(r, keys, start, mid-1);
		// Add all the values from the middle+1 key to the last key to the right tree of root
		add(r, keys, mid+1, end);
	}
	
	public void add(Node n, int[] values, int start, int end) {
		// Continue adding until pointer reaches end of keys 
		if (start <= end) {
			// Identify mid-point of current sub-array
			int mid = (start + end) / 2;
			// Build left subtree
			if (values[mid] < n.value)  {
				n.left = new Node(values[mid]);
				// Add all the values from first sub-array key to middle-1 key to left tree of new node
				add(n.left, values, start, mid-1); 
				// Add all the values from the middle+1 key of sub-array to right tree of new node
				add(n.left, values, mid+1, end); 
			} else {
				// Build right subtree
				n.right = new Node(values[mid]);
				add(n.right, values, start, mid-1);
				add(n.right, values, mid+1, end);
			}
		}
	}
	
	
	
	/**
	 * Find the right place to put the value into the tree
	 * based on comparison at the given node. 
	 * Assumes that our key values are distinct,
	 * because we only compare if it is greater than or less than the nodes. 
	 * @param node
	 * @param value
	 */
	public void insert(Node node, int value) {
		// If the value we want to insert is smaller than the current node's value
		// we insert the value into the left tree, 
		// otherwise we insert the value into the right tree
		if (value < node.value) {
			// If there exists a left tree, we go into it
			if (node.left != null) {
				insert(node.left, value); // recursion to continue traversing tree
			} else {
				// Otherwise create node
				System.out.println("Inserted " + value + " to left of node " + node.value);
				node.left = new Node(value);
			}
		} else if (value > node.value) {
			// If there exists a right tree, we go into it
			if (node.right != null) {
				insert(node.right, value); // recursion to continue traversing tree
			} else {
				// Otherwise create node
				System.out.println("Inserted " + value + " to right of node " + node.value);
				node.right = new Node(value);
			}
		}
	}
	
	public void run() {
		Node rootNode = new Node(25);
		System.out.println("Building tree with rootvalue " + rootNode.value);
		System.out.println("===============================");
		insert(rootNode, 11);
		insert(rootNode, 15);
		insert(rootNode, 16);
		insert(rootNode, 23);
		insert(rootNode, 79);
		printInOrder(rootNode);
	}
	
	/**
	 * Prints out the subtree based on given node,
	 * with each node value in ascending order.
	 * @param node
	 */
	public void printInOrder2(Node node) {
		// Looks at left subtree first because it contains smaller values
		if (node.left != null) {
			System.out.println(node.left.value);
			printInOrder(node.left);
		} 
		
		// Look at right subtree after left subtree is done
		if (node.right != null) {
			System.out.println(node.right.value);
			printInOrder(node.right);
		} 
	}
	
	public void printInOrder(Node node) {
		if (node != null) {
			printInOrder(node.left);
			System.out.println("Traversed " + node.value);
			printInOrder(node.right);
		}
	}
		
	public Node search(Node n, int key) {
		// If current node is null, 
		// it means we exhausted the tree without finding any match for the key
		if (n == null) {
			return null;
		}
		// If the current node matches the key, we return it
		if (n.value == key) {
			return n;
		} else if (n.value > key) {
			// If current node is greater than key, search left tree,
			// skipping right tree cuz it contains all greater values
			return search(n.left, key);
		} else {
			// Otherwise search right tree
			return search(n.right, key);
		}
	}
	
	public static void main(String[] args) {
		BinaryTreeExample bte = new BinaryTreeExample();
		bte.run();
		
		int[] values = {11, 15, 16, 25, 23, 78};
		BinaryTreeExample bt = new BinaryTreeExample(values);
		bt.printInOrder(bt.root);
		
		Node n = bt.search(bt.root, 11);
		if (n != null) {
			System.out.println(n.value);
		} else {
			System.out.println("no match!");
		}
	}
}
