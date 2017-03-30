package xmlhelpers;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class that represents a list of XML nodes. This class is used as a wrapper of {@link NodeList} and contains useful
 * functions for abstract parsing.
 * 
 * @author themis
 */
@SuppressWarnings("serial")
public class XMLNodeList extends ArrayList<XMLNode> {

	/**
	 * Initializes this class given a node list object.
	 * 
	 * @param list a node list object.
	 */
	public XMLNodeList(NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			add(new XMLNode(node));
		}
	}

	/**
	 * Initializes this class as an empty list of nodes.
	 */
	public XMLNodeList() {

	}

	/**
	 * Adds a new node to this list.
	 * 
	 * @param node the node to be added.
	 */
	public void add(Node node) {
		add(new XMLNode(node));
	}

	/**
	 * Returns a sublist of this list fro the given index to the end of the list.
	 * 
	 * @param fromIndex the start index of the sublist.
	 * @return a list containing the nodes from the given index to the end of the list.
	 */
	public List<XMLNode> subList(int fromIndex) {
		return super.subList(fromIndex, size());
	}
}
