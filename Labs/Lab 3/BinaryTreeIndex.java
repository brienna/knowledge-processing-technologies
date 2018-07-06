import java.util.ArrayList;

public class BinaryTreeIndex {
	String[] myDocs;
	BinaryTree termList;
	Node root;
	
	/**
	 * Construct binary search tree to store term dictionary.
	 * @param docs List of input strings
	 */
	public BinaryTreeIndex(String[] docs) {
		myDocs = docs;
		termList = new BinaryTree();
		ArrayList<Integer> docList;
		
		// Tokenize each document
		for (int i = 0; i < myDocs.length; i++) {
			String[] tokens = myDocs[i].split(" ");
			String token;
			
			// Check each token
			for (int j = 0; j < tokens.length; j++) {
				token = tokens[j];
				
				// Create root node with first token in first document
				if (i == 0 && j == 0) {
					docList = new ArrayList<Integer>();
					docList.add(i);
					root = new Node(token, docList);
					continue; 
				}
				
				// If token has not been added to tree yet
				if (termList.search(root, token) == null) {
					// Create and add a node for it 
					docList = new ArrayList<Integer>();
					docList.add(i);
					Node tokenNode = new Node(token, docList);
					termList.add(root, tokenNode);
				} else {
					// If token has been added to tree, get its node
					Node tokenNode = termList.search(root, token);
					docList = tokenNode.docLists;
					// If current document is new for token
					if (!docList.contains(i)) {
						// Store it
						docList.add(i);
					} 
						
				}
			}
		}
	}
	
	/**
	 * conjunctive query search
	 * @param query the set of query terms
	 * @return doclists that contain all the query terms
	 */
	public ArrayList<Integer> search(String[] query)
	{
		ArrayList<Integer> result = search(query[0]);
		int termId = 1;
		while(termId<query.length)
		{
			ArrayList<Integer> result1 = search(query[termId]);
			result = merge(result,result1);
			termId++;
		}		
		return result;
	}
	
	/**
	 * Single keyword search
	 * @param query the query string
	 * @return doclists that contain the term
	 */
	public ArrayList<Integer> search(String query)
	{
			Node node = termList.search(root, query);
			if(node==null)
				return null;
			return node.docLists;
	}
	
	private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2)
	{
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		int id1 = 0, id2=0;
		while(id1<l1.size()&&id2<l2.size()){
			if(l1.get(id1).intValue()==l2.get(id2).intValue()){
				mergedList.add(l1.get(id1));
				id1++;
				id2++;
			}
			else if(l1.get(id1)<l2.get(id2))
				id1++;
			else
				id2++;
		}
		return mergedList;
	}
	
	
	/**
	 * Processes and prints the result of each query.
	 */
	public void processResult(ArrayList<Integer> result) {
		if (result != null) {
			for (Integer i : result) {
				System.out.println(i);
			}
		} else {
			System.out.println("No match");
		}
	}
	
	
	/**
	 * Starts the whole program.
	 */
	public static void main(String[] args) {
		// Construct binary search tree with sample docs
		String[] docs = {"new home sales top forecasts",
						 "home sales rise in july",
						 "increase in home sales in july",
						 "july new home sales rise"};
		BinaryTreeIndex bti = new BinaryTreeIndex(docs);
		
		// Test single term queries
		System.out.println("\nSearching for 'home': ");
		bti.processResult(bti.search("home"));
		
		System.out.println("\nSearching for 'rise': ");
		bti.processResult(bti.search("rise"));
		
		System.out.println("\nSearching for 'new': ");
		bti.processResult(bti.search("new"));
		
		// Test conjunctive queries
		System.out.println("\nSearching for 'new home': ");
		bti.processResult(bti.search("new home".split(" ")));
		
		System.out.println("\nSearching for 'july sales': ");
		bti.processResult(bti.search("july sales".split(" ")));
		
		System.out.println("\nSearching for 'home sales rise': ");
		bti.processResult(bti.search("home sales rise".split(" ")));
	}
}
