package xmlhelpers;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class that represents an XML document. This class is used as a wrapper of {@link Document} and contains useful
 * functions for abstract parsing.
 * 
 * @author themis
 */
public class XMLDocument {

	/**
	 * The inner document object.
	 */
	private Document document;

	/**
	 * Initializes this class given the content of an XML document as a string.
	 * 
	 * @param content the content of an XML document.
	 */
	public XMLDocument(String content) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(content));
			document = builder.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns all elements of the document with the given tag name.
	 * 
	 * @param tagName the given tag name.
	 * @return a node list of the elements with the given tag name.
	 */
	public XMLNodeList getElementsByTagName(String tagName) {
		return new XMLNodeList(document.getElementsByTagName(tagName));
	}

	/**
	 * Removes the xml nodes from the XML string given as input.
	 * 
	 * @param xml the XML string of which the nodes are removed.
	 * @param nodesToBeRemoved the XML nodes to be removed.
	 * @return the XML string without the removed nodes.
	 */
	public static String removeXMLNodes(String xml, String... nodesToBeRemoved) {
		class IntPair {
			final int x;
			final int y;

			IntPair(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}
		for (String nodeToBeRemoved : nodesToBeRemoved) {
			ArrayList<IntPair> matches = new ArrayList<IntPair>();
			final Pattern pattern = Pattern.compile("<" + nodeToBeRemoved + ">(.+?)</" + nodeToBeRemoved + ">");
			final Matcher matcher = pattern.matcher(xml);
			while (matcher.find()) {
				matches.add(new IntPair(matcher.start(), matcher.end()));
			}
			for (int j = matches.size() - 1; j >= 0; j--) {
				IntPair match = matches.get(j);
				xml = xml.substring(0, match.x) + xml.substring(match.y);
			}
		}
		return xml;
	}

	/**
	 * Returns the XML representation of this document.
	 * 
	 * @return a string with the XML representation of this document.
	 */
	public String toXMLString() {
		DOMSource source = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

}
