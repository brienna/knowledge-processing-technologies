
/**
 * Document class for the vector representation of a document
 */
class Doc{
	int id; 
	double tw;
	
	public Doc(int id, double tw) {
		this.id = id;
		this.tw = tw;
	}
	
	public String toString() {
		String docRepr = id + ": " + tw;
		return docRepr;
	}
}