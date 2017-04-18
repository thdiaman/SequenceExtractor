package xmlhelpers;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

/**
 * Class that represents an XML node. This class is used as a wrapper of {@link Node} and contains useful functions for
 * abstract parsing.
 * 
 * @author themis
 */
public class XMLNode {

	/**
	 * The inner node object.
	 */
	private Node node;

	/**
	 * Initializes this object given an XML node.
	 * 
	 * @param node the inner node object.
	 */
	public XMLNode(Node node) {
		this.node = node;
	}

	/**
	 * Initializes this object given a name.
	 * 
	 * @param name the name of the node object.
	 */
	public XMLNode(String name) {
		@SuppressWarnings("restriction")
		org.w3c.dom.Document xmlDoc = new com.sun.org.apache.xerces.internal.dom.DocumentImpl();
		org.w3c.dom.Element root = xmlDoc.createElement(name);
		node = root;
	}

	/**
	 * Returns a list of the child nodes of this node by name.
	 * 
	 * @param name the name of the child nodes that are returned.
	 * @return a list of nodes with the given name.
	 */
	public XMLNodeList getChildNodesByName(String name) {
		XMLNodeList nodes = new XMLNodeList();
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node cNode = node.getChildNodes().item(j);
			if (cNode.getNodeName().equals(name))
				nodes.add(cNode);
		}
		return nodes;
	}

	/**
	 * Returns the first child node of this node by name.
	 * 
	 * @param name the name of the returned child node.
	 * @return the first child node with the given name.
	 */
	public XMLNode getChildNodeByName(String name) {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node cNode = node.getChildNodes().item(j);
			if (cNode.getNodeName().equals(name))
				return new XMLNode(cNode);
		}
		return null;
	}

	/**
	 * Returns the content of this node as a string.
	 * 
	 * @return the text content of this node.
	 */
	public String getTextContent() {
		return node.getTextContent();
	}

	/**
	 * Checks if this node has more than one children.
	 * 
	 * @return {@code true} if this node has more than one children, or {@code false} otherwise.
	 */
	public boolean hasMoreThanOneChildren() {
		return node.getChildNodes().getLength() > 1;
	}

	/**
	 * Finds all child nodes of the given node that have the given names resursively.
	 * 
	 * @param node the node of which the child nodes are searched.
	 * @param names the names for which the child nodes are found.
	 * @param nodes the child nodes that are found.
	 */
	private void getChildNodesRecursivelyByName(Node node, Collection<String> names, XMLNodeList nodes) {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node cNode = node.getChildNodes().item(j);
			if (names.contains(cNode.getNodeName()))
				nodes.add(cNode);
			else
				getChildNodesRecursivelyByName(cNode, names, nodes);
		}
	}

	/**
	 * Finds all deep child nodes of the given node that have the given names resursively.
	 * 
	 * @param node the node of which the child nodes are searched.
	 * @param names the names for which the child nodes are found.
	 * @param nodes the child nodes that are found.
	 */
	private void getDeepChildNodesRecursivelyByName(Node node, Collection<String> names, XMLNodeList nodes) {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node cNode = node.getChildNodes().item(j);
			if (names.contains(cNode.getNodeName())) {
				nodes.add(cNode);
				getDeepChildNodesRecursivelyByName(cNode, names, nodes);
			} else
				getDeepChildNodesRecursivelyByName(cNode, names, nodes);
		}
	}

	/**
	 * Returns all child nodes that have the given names resursively.
	 * 
	 * @param names the names for which the child nodes are found.
	 * @return a list of nodes with the given names.
	 */
	public XMLNodeList getChildNodesRecursivelyByName(Set<String> names) {
		XMLNodeList nodes = new XMLNodeList();
		getChildNodesRecursivelyByName(node, names, nodes);
		return nodes;
	}

	public XMLNodeList getChildNodesRecursivelyByName(String... names) {
		XMLNodeList nodes = new XMLNodeList();
		getChildNodesRecursivelyByName(node, Arrays.asList(names), nodes);
		return nodes;
	}

	/**
	 * Returns all deep child nodes that have the given names resursively.
	 * 
	 * @param names the names for which the child nodes are found.
	 * @return a list of nodes with the given names.
	 */
	public XMLNodeList getDeepChildNodesRecursivelyByName(Set<String> names) {
		XMLNodeList nodes = new XMLNodeList();
		getDeepChildNodesRecursivelyByName(node, names, nodes);
		return nodes;
	}

	public XMLNodeList getDeepChildNodesRecursivelyByName(String... names) {
		XMLNodeList nodes = new XMLNodeList();
		getDeepChildNodesRecursivelyByName(node, Arrays.asList(names), nodes);
		return nodes;
	}

	/**
	 * Checks if this node has the given name.
	 * 
	 * @param name the name of this node to be checked.
	 * @return {@code true} if this node has the given name, or {@code false} otherwise.
	 */
	public boolean hasName(String name) {
		return node.getNodeName().equals(name);
	}

	/**
	 * Checks if the name of this node is contained in the given set.
	 * 
	 * @param names a set of names to be checked whether it contains the name of this node.
	 * @return {@code true} if the name of this node is contained in the given set, or {@code false} otherwise.
	 */
	public boolean hasName(Set<String> names) {
		return names.contains(node.getNodeName());
	}

	public boolean hasName(String... names) {
		return Arrays.asList(names).contains(node.getNodeName());
	}

	/**
	 * Returns the child nodes of this node.
	 * 
	 * @return the child nodes of this node.
	 */
	public XMLNodeList getChildNodes() {
		return new XMLNodeList(node.getChildNodes());
	}

	/**
	 * Checks if this node is a text node.
	 * 
	 * @return {@code true} if this node is a text node, or {@code false} otherwise.
	 */
	public boolean isTextNode() {
		return node.getNodeType() == Node.TEXT_NODE;
	}

	/**
	 * Returns the parent node of this node.
	 * 
	 * @return the parent node of this node.
	 */
	public XMLNode getParentNode() {
		return new XMLNode(node.getParentNode());
	}

	public boolean textContentStartsWith(String text) {
		return node.getTextContent() != null ? node.getTextContent().startsWith(text) : false;
	}

	public void insertNewParentNode(XMLNode newParentNode) {
		Node oldParentNode = node.getParentNode();
		newParentNode.node.appendChild(newParentNode.node.getOwnerDocument().importNode(node, true));
		oldParentNode.replaceChild(node.getOwnerDocument().importNode(newParentNode.node, true), node);
	}

	public void addNewChildNode(XMLNode newChildNode) {
		node.appendChild(node.getOwnerDocument().importNode(newChildNode.node, true));
	}

	public void addNewChildNodes(XMLNodeList newChildNodes) {
		for (XMLNode newChildNode : newChildNodes) {
			node.appendChild(node.getOwnerDocument().importNode(newChildNode.node, true));
		}
	}

	public void removeParentNode() {
		XMLNode oldParentNode = getParentNode();
		XMLNode newParentNode = oldParentNode.getParentNode();
		newParentNode.node.appendChild(node);
		if (!oldParentNode.node.hasChildNodes())
			oldParentNode.getParentNode().node.removeChild(oldParentNode.node);
	}

	public void replaceChild(XMLNode newChild, XMLNode oldChild) {
		node.replaceChild(node.getOwnerDocument().importNode(newChild.node, true), oldChild.node);
	}

	public XMLNode removeChild(XMLNode child) {
		return new XMLNode(node.removeChild(child.node));
	}

	public void addNewChildNodeInTheBeginning(XMLNode newChildNode) {
		node.appendChild(node.getOwnerDocument().importNode(newChildNode.node, true));
		if (node.hasChildNodes())
			node.insertBefore(node.getOwnerDocument().importNode(newChildNode.node, true), node.getFirstChild());
		else
			node.appendChild(node.getOwnerDocument().importNode(newChildNode.node, true));
	}

	/**
	 * Returns the XML representation of this node.
	 * 
	 * @return a string with the XML representation of this node.
	 */
	public String toXMLString() {
		DOMSource source = new DOMSource();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			source.setNode(node);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	/**
	 * Returns a string representation of this node as a string representation of the inner node.
	 * 
	 * @return a string representation of this node.
	 */
	@Override
	public String toString() {
		return "Node(name: " + node.getNodeName() + ", content: " + node.getTextContent() + ")";
	}
}
