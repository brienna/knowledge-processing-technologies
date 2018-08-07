import java.util.*;

/**
 * Pre-process a sample, hard-coded document collection 
 * to construct a vector representation of the documents,
 * then use k-means clustering algorithm to cluster the documents,
 * using a tf-idf weighting mechanism. 
 * 
 * Clustering.java uses only a tf weighting mechanism.
 * 
 * @author Brienna Herold
 */
public class ClusteringWithTfIdf {
	// Declare attributes
	int k; // num of clusters
	ArrayList<String[]> tokenizedDocs;
	HashMap<Integer, double[]> vectorSpace;
	ArrayList<String> vocabulary; // termList
	ArrayList<ArrayList<Doc>> docLists;
	
	/**
	 * Constructor for attribute initialization
	 * @param numC number of clusters
	 */
	public ClusteringWithTfIdf(int numC)
	{
		k = numC;
	}
	
	/**
	 * Load the documents to build the vector representations
	 * @param docs
	 */
	public void preprocess(String[] docs){
		tokenizedDocs = new ArrayList<String[]>();
		vocabulary = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Doc>>();
		ArrayList<Doc> docList; // reusable variable
		
		// For each document,
		for (int i = 0; i < docs.length; i++) {
			// Tokenize document, removing punctuation
			String[] tokens = docs[i].split("[ _\".,?!/:;$%&*+()\\-\\^]+");
			tokenizedDocs.add(i, tokens); // MINE
		
			// For each token,
			for (String token : tokens) {
				// If new, 
				if (!vocabulary.contains(token)) {
					// Add to vocabulary
					vocabulary.add(token);
					// Set corresponding postings
					docList = new ArrayList<Doc>();
					Doc doc = new Doc(i, 1); // Initial weight is raw word frequency, starting with 1
					docList.add(doc);
					docLists.add(docList);
				} else {
				// If not new,
					// Retrieve postings
					int index = vocabulary.indexOf(token);
					docList = docLists.get(index);
					boolean match = false;
					
					// Search postings for matching document id
					for (Doc d : docList) {
						if (d.id == i) {
							d.tw++; // Increase word frequency 
							match = true;
							break;
						}
					}
					
					// If postings did not contain matching document id, 
					// add a new posting for the document 
					if (!match) {
						Doc d = new Doc(i, 1);
						docList.add(d);
					}
				}
			}
		} // End parsing documents
		
		// Calculate tf*idf of each document and set as weights
		int N = vocabulary.size();
		// For each term in the vocabulary
		for (int i = 0; i < vocabulary.size(); i++) {
			// Get its corresponding postings & document frequency 
			docList = docLists.get(i);
			int df = docList.size();
			Doc d;
			// For each document in the postings
			for (int j = 0; j < df; j++) {
				// Get its Doc object, calculate tf-idf
				d = docList.get(j);
				double tfidf = (1 + Math.log(d.tw)) * Math.log(N / (df * 1.0));
				d.tw = tfidf;
				docList.set(j, d);
			}
		}
		
		// Construct vector space with normalized document vectors as points
		vectorSpace = new HashMap<Integer, double[]>(); 
		double[] weights;
		// For each posting,
		for (int i = 0; i < docLists.size(); i++) {
			docList = docLists.get(i);
		    // For each document in the posting,
		    for (int j = 0; j < docList.size(); j++) {
		        Doc d = docList.get(j);
		        
		        // If document doesn't have a vector in vectorSpace yet,
		        if (!vectorSpace.containsKey(d.id)) {
		            // Create vector, adding weight to the same position term is in the vocabulary
		            weights = new double[vocabulary.size()];
		            weights[i] = d.tw;
		            vectorSpace.put(d.id, weights);
		        } else {
		            // Get document vector and update it
		        	weights = vectorSpace.get(d.id);
		        	weights[i] = d.tw;
		        	vectorSpace.put(d.id, weights);
		        }
		    }
		}
	}
	
	/**
	 * Calculate cosine similarity between two vectors
	 * @param vector1 
	 * @param vector2
	 * @return cosine similarity
	 */
	private double getSimilarity(double[] vector1, double[] vector2) {
		double dotProduct = 0.0;
		double norm1 = 0.0;
		double norm2 = 0.0;
		double cosineSimilarity = 0.0;
		
		for (int i = 0; i < vector1.length; i++) { // vectors are of same length
			dotProduct += vector1[i] * vector2[i]; // a * b
			norm1 += Math.pow(vector1[i], 2); // a^2
			norm2 += Math.pow(vector2[i], 2); // b^2
		}
		
		norm1 = Math.sqrt(norm1); // sqrt(a^2)
		norm2 = Math.sqrt(norm2); // sqrt(b^2)
		
		if (norm1 != 0.0 | norm2 != 0.0) {
			cosineSimilarity = dotProduct / (norm1 * norm2);
		} else {
			// keep cosineSimilarity at 0.0
		}
		
		return cosineSimilarity;
	}
	
	/**
	 * Cluster the documents using k-means
	 */
	public void cluster(){
		if (k < 2) {
			// Can't cluster, return everything in 1 cluster
		} else {
			// Get distances between all documents (to test)
			System.out.println("COSINE DISTANCES BETWEEN ALL DOCUMENTS: ");
			for (int i = 0; i < vectorSpace.size(); i++) {
				for (int j = 0; j < vectorSpace.size(); j++) {
					System.out.println("between " + i + " and " + j + " = " +
							String.format("%1.5f", getSimilarity(vectorSpace.get(i), vectorSpace.get(j))));
				}
			}
			
			// Set first and ninth documents as initial centroids
			// (for this lab, it is required to do so, otherwise it would be randomly set)
			double[][] centroids = new double[k][];
			centroids[0] = vectorSpace.get(5);
			centroids[1] = vectorSpace.get(2);
			
			HashMap<Integer, double[]>[] clusters;
			int iterations = 0;
			double[][] oldCentroids = null;
			System.out.println("\nRUNNING K-MEANS...");
			while (!Arrays.deepEquals(oldCentroids, centroids)) {
				iterations++;
				System.out.println("\nIteration: " + iterations);
				
				// Save old centroids for convergence test. 
				oldCentroids = centroids;
				
				// Assign clusters to each document based on centroids
				clusters = getClusters(centroids);
				printClusters(clusters);
				
				// Update centroids
				centroids = getCentroids(clusters);
			}
				
			System.out.println("\nK-means converged after " + iterations + " iterations");
		}
 	}
	
	/**
	 * Prints string representation of given clusters.
	 * @param clusters
	 */
	public void printClusters(HashMap<Integer, double[]>[] clusters) {
		System.out.println("CLUSTERS FOUND IN THIS ITERATION: ");
		String clusterString;
		for (int i = 0; i < clusters.length; i++) {
			clusterString = "Cluster: " + i + "\n";
			HashMap<Integer, double[]> cluster = clusters[i];
			for (Integer id : cluster.keySet()) {
				clusterString += id + " ";
			}
			System.out.println(clusterString);
		}
	}
	
	/**
	 * Assigns clusters to each vectorized document based on given centroids.
	 * @param centroids
	 * @return clusters 
	 */
	public HashMap<Integer, double[]>[] getClusters(double[][] centroids) {
		HashMap<Integer, double[]>[] clusters = new HashMap[k];
		for (int i = 0; i < k; i++) {
			clusters[i] = new HashMap<Integer, double[]>();
		}

		// For each document, 
		for (int n = 0; n < vectorSpace.size(); n++) {
			double[] currDocVector = vectorSpace.get(n);
			int currDocId = n;
			
			// Calculate cosine similarities between document & centroids
			double[] scores = new double[k];
			for (int i = 0; i < k; i++) {
				System.out.println("Comparing document " + n + " with cluster centroid " + i);
				scores[i] = getSimilarity(centroids[i], currDocVector);
				System.out.println("Similarity = " + scores[i]);
			}
			
			// Place document in the cluster with the closest centroid 
			int clusterId = 0;
			double max = scores[clusterId];
			for (int i = 1; i < scores.length; i++) {
				if (scores[i] > max) {
					max = scores[i];
					clusterId = i;
				}
			}
			clusters[clusterId].put(currDocId, currDocVector);
		}
		
		return clusters;
	}
	
	/**
	 * Updates centroids (each of dimension vocabulary.size()) based on given clusters.
	 * @param clusters 
	 * @return centroids
	 */
	public double[][] getCentroids(HashMap<Integer, double[]>[] clusters) {
		System.out.println("UPDATED CENTROID VECTORS: ");
		double[][] centroids = new double[k][];
		
		// For each cluster,
		for (int i = 0; i < clusters.length; i++) {
			HashMap<Integer, double[]> cluster = clusters[i];
			// Calculate geometric mean of document vectors in the cluster
			double[] mean = new double[vocabulary.size()];
			for (Integer id : cluster.keySet()) {
				double[] currDocVector = cluster.get(id);
				for (int x = 0; x < currDocVector.length; x++) {
					mean[x] += currDocVector[x];
				}
				for (int x = 0; x < mean.length; x++) {
					mean[x] = mean[x] / cluster.size();
				}
			}
			centroids[i] = mean;
			System.out.println(Arrays.toString(mean));
		}
		
		return centroids;
	}
	
	/**
	 * String representation of inverted index.
	 */
	public String toString() {
		System.out.println("INVERTED INDEX:");
		String matrixString = new String();
		ArrayList<Doc> docList;
		for (int i = 0; i < vocabulary.size(); i++) {
			matrixString += String.format("%-15s", vocabulary.get(i));
			docList = docLists.get(i);
			for (int j = 0; j < docList.size(); j++) {
				matrixString += docList.get(j) + "\t";
			}
			matrixString += "\n";
		}
		return matrixString;
	}
	
	/**
	 * String representation of the documents in the vector space.
	 */
	public void printVectorSpace() {
		System.out.println("VECTOR SPACE:");
		String matrixString = new String();
		for (int docId : vectorSpace.keySet()) {
			matrixString += docId + ": <";
			double[] weights = vectorSpace.get(docId);
			for (int i = 0; i < weights.length; i++) {
				matrixString += weights[i] + ", ";
			}
			matrixString += ">\n";
		}
		System.out.println(matrixString);
	}
	
	/**
	 * Main entry point of program.
	 * @param args
	 */
	public static void main(String[] args){
		String[] docs = {"hot chocolate cocoa beans",
				 "cocoa ghana africa",
				 "beans harvest ghana",
				 "cocoa butter",
				 "butter truffles",
				 "sweet chocolate can",
				 "brazil sweet sugar can",
				 "suger can brazil",
				 "sweet cake icing",
				 "cake black forest"
				};
		
		ClusteringWithTfIdf c = new ClusteringWithTfIdf(2);
		c.preprocess(docs);
		System.out.println(c);
		c.printVectorSpace();
		c.cluster();
		
		/*
		 * Expected result:
		 * Cluster: 0
			0	1	2	3	4	
		   Cluster: 1
			5	6	7	8	9	
		 */
	}
}

