import java.util.ArrayList;

/**
 * Represents a single posting, which contains a document ID 
 * along with a list of positions the term appears in the document. 
 */
public class DocId {
	int docId;
	ArrayList<Integer> positionList;
	
	public DocId(int did, int position) {
		docId = did;
		positionList = new ArrayList<Integer>();
		positionList.add(new Integer(position));
	}
	
	public void insertPosition(int position) {
		positionList.add(new Integer(position));
	}
	
	/** 
	 * Returns a string representation of a DocID object 
	 */
	public String toString() {
		// Set up the string with "<"
		String docIdString = "" + docId + ":<";
		// Stringify each position in the positionList 
		for (Integer pos : positionList) {
			docIdString += pos + ",";
		}
		// Replace the final comma with a ">"
		docIdString = docIdString.substring(0, docIdString.length() - 1) + ">";
		return docIdString;
	}
}
