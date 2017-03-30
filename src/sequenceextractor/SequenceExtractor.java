package sequenceextractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import astextractor.ASTExtractor;
import parsehelpers.Block;
import parsehelpers.LookUpTable;
import parsehelpers.Snippet;
import parsehelpers.Statement;
import xmlhelpers.XMLDocument;
import xmlhelpers.XMLNode;
import xmlhelpers.XMLNodeList;

/**
 * The sequence extractor class that receives code snippets and translates it to sequences.
 * 
 * @author themis
 */
public class SequenceExtractor {

	/**
	 * Returns the type of a variable given its XML node.
	 * 
	 * @param node the XML node of the variable.
	 * @return the type of the variable of which the XML node is given.
	 */
	private static String getType(XMLNode node) {
		String type = null;
		if (node.getChildNodeByName("SimpleType") != null) {
			XMLNode stype = node.getChildNodeByName("SimpleType");
			if (stype.getChildNodeByName("QualifiedName") != null) {
				XMLNode sstype = stype.getChildNodeByName("QualifiedName");
				ArrayList<XMLNode> snnodes = sstype.getChildNodesByName("SimpleName");
				type = snnodes.get(snnodes.size() - 1).getTextContent();
			} else
				type = stype.getChildNodeByName("SimpleName").getTextContent();
		} else if (node.getChildNodeByName("PrimitiveType") != null)
			type = node.getChildNodeByName("PrimitiveType").getTextContent();
		else if (node.getChildNodeByName("ArrayType") != null) {
			type = "" + getType(node.getChildNodeByName("ArrayType"));
		} else if (node.getChildNodeByName("ParameterizedType") != null) {
			type = "" + getType(node.getChildNodeByName("ParameterizedType"));
		}
		return type;
	}

	/**
	 * Iterates statement-level code recursively and constructs a statement.
	 * 
	 * @param node the node given as root.
	 * @param output the output statement.
	 * @param lookUpTable the look up table for variables.
	 */
	private static void iterateLowLevelCode(XMLNode node, Statement output, LookUpTable lookUpTable) {
		if (node.isTextNode()) {
			output.add(lookUpTable.getTypeOfVariable(node.getTextContent()));
		} else {
			if (node.hasName("VariableDeclarationFragment") || node.hasName("Assignment")) {
				output.add("AM");
				for (XMLNode childnode : node.getChildNodes()) {
					iterateLowLevelCode(childnode, output, lookUpTable);
				}
			} else if (node.hasName("ClassInstanceCreation")) {
				if (output.size() > 1 && output.get(1) != null && !output.get(1).equals("___")) {
					String ctype = output.get(1);
					output.clear();
					output.add("CI");
					output.add(ctype);
				} else {
					output.clear();
					output.add("CI");
					output.add(getType(node));
				}
			} else if (node.hasName("MethodInvocation")) {
				if (output.size() > 1 && output.get(1) != null && !output.get(1).equals("___")) {
					String ctype = output.get(1);
					output.clear();
					output.add("FC");
					output.add(ctype);
				} else {
					output.clear();
					output.add("FC");
					output.add("void");
				}
				// Append also the type of the calling object in this case
				String methodType = lookUpTable
						.getTypeOfVariable(node.getChildNodeByName("SimpleName").getTextContent());
				if (methodType != null && methodType != "___" && output.size() > 1) {
					if (!(methodType.equals(output.get(1)) || methodType.equals("byte") || methodType.equals("short")
							|| methodType.equals("int") || methodType.equals("long") || methodType.equals("float")
							|| methodType.equals("double") || methodType.equals("char") || methodType.equals("String")
							|| methodType.equals("boolean")))
						output.add(methodType);
				}
			} else if (node.hasName("SuperMethodInvocation")) {
				output.add("FC");
				output.add(lookUpTable.getTypeOfVariable(node.getChildNodeByName("SimpleName").getTextContent()));
			} else if (node.hasName("SimpleType") || node.hasName("ArrayType") || node.hasName("PrimitiveType")) {
				output.add(getType(node.getParentNode()));
			} else if (node.hasName("ReturnStatement")) {
				for (XMLNode childnode : node.getChildNodes()) {
					iterateLowLevelCode(childnode, output, lookUpTable);
				}
			} else if (node.hasName("CastExpression")) {
				for (XMLNode childnode : node.getChildNodes().subList(1)) {
					iterateLowLevelCode(childnode, output, lookUpTable);
				}
			} else if (node.hasName("QualifiedName")) {
				for (XMLNode childnode : node.getChildNodes().subList(1)) {
					iterateLowLevelCode(childnode, output, lookUpTable);
				}
			} else {
				for (XMLNode childnode : node.getChildNodes()) {
					iterateLowLevelCode(childnode, output, lookUpTable);
				}
			}
		}
	}

	/**
	 * Iterates statement-level code recursively and constructs a statement.
	 * 
	 * @param node the node given as root.
	 * @param lookUpTable the look up table for variables.
	 * @return the output statement.
	 */
	private static Statement iterateLowLevelCode(XMLNode node, LookUpTable lookUpTable) {
		Statement output = new Statement();
		iterateLowLevelCode(node, output, lookUpTable);
		return output;
	}

	/**
	 * Processes a method and populates the snippet.
	 * 
	 * @param method the method node to be processed.
	 * @param lookUpTable the look up table for variables.
	 * @param snippet the snippet where statements are added.
	 */
	private static void processMethod(XMLNode method, LookUpTable lookUpTable, Snippet snippet) {
		lookUpTable.enterMethod();

		ArrayList<XMLNode> parameters = method.getChildNodesByName("SingleVariableDeclaration");
		for (XMLNode parameter : parameters) {
			String parameterName = parameter.getChildNodeByName("SimpleName").getTextContent();
			String parameterType = getType(parameter);
			lookUpTable.addMethodVariable(parameterName, parameterType);
		}

		XMLNode block = method.getChildNodeByName("Block");
		Set<String> statementTypes = new HashSet<String>(
				Arrays.asList("VariableDeclarationStatement", "ExpressionStatement", "ReturnStatement"));
		if (block != null) {
			ArrayList<XMLNode> statements = block.getChildNodesRecursivelyByName(statementTypes);
			if (statements.size() > 0)
				snippet.addBlock();
			for (XMLNode statement : statements) {
				if (statement.hasName("VariableDeclarationStatement")) {
					String variableType = getType(statement);
					XMLNode vardecl = statement.getChildNodeByName("VariableDeclarationFragment");
					ArrayList<XMLNode> variableNodes = vardecl.getChildNodesByName("SimpleName");
					for (XMLNode variableNode : variableNodes) {
						String variableName = variableNode.getTextContent();
						lookUpTable.addMethodVariable(variableName, variableType);
					}
					if (vardecl.hasMoreThanOneChildren())
						snippet.addStatement(iterateLowLevelCode(vardecl, lookUpTable));
				} else {
					snippet.addStatement(iterateLowLevelCode(statement, lookUpTable));
				}
			}
		}
	}

	/**
	 * Processes a field variable and populates the snippet.
	 * 
	 * @param variable the variable node to be processed.
	 * @param lookUpTable the look up table for variables.
	 * @param snippet the snippet where statements are added.
	 */
	private static void processField(XMLNode variable, LookUpTable lookUpTable, Snippet snippet) {
		String variableType = getType(variable);
		ArrayList<XMLNode> variableDeclarationNodes = variable.getChildNodesByName("VariableDeclarationFragment");
		for (XMLNode vardecl : variableDeclarationNodes) {
			ArrayList<XMLNode> variableNodes = vardecl.getChildNodesByName("SimpleName");
			for (XMLNode variableNode : variableNodes) {
				String variableName = variableNode.getTextContent();
				lookUpTable.addClassVariable(variableName, variableType);
			}
			if (vardecl.hasMoreThanOneChildren()) {
				snippet.addBlock();
				snippet.addStatement(iterateLowLevelCode(vardecl, lookUpTable));
			}
		}
	}

	/**
	 * Receives as input an AST in XML format and returns a sequence of statements
	 * 
	 * @param xml an AST in XML format.
	 * @param keepFunctionCallTypes {@code true} if call types should be kept, or {@code false} otherwise.
	 * @param keepLiterals {@code true} if literals (primitives) should be kept, or {@code false} otherwise.
	 * @return a snippet as a sequence of statements
	 */
	private static Snippet createSequence(String xml, boolean keepFunctionCallTypes, boolean keepLiterals) {
		// Preprocess the XML of the AST
		xml = "<file>" + xml + "</file>";
		xml = xml.replaceAll("\n|\r| ", "");
		xml = XMLDocument.removeXMLNodes(xml, "Javadoc", "Modifier", "Dimension", "ImportDeclaration",
				"PackageDeclaration");
		if (!keepLiterals)
			xml = XMLDocument.removeXMLNodes(xml, "BooleanLiteral", "StringLiteral", "NumberLiteral",
					"CharacterLiteral");
		XMLDocument ast = new XMLDocument(xml);

		// Initialize the snippet and the look up table
		Snippet snippet = new Snippet();
		LookUpTable lookUpTable = new LookUpTable();
		XMLNodeList nodeList;

		// Process a class
		nodeList = ast.getElementsByTagName("TypeDeclaration");
		for (XMLNode node : nodeList) {
			String superClassName = null;
			if (node.getChildNodeByName("SimpleType") != null)
				superClassName = node.getChildNodeByName("SimpleType").getTextContent();
			lookUpTable.enterClass(superClassName);
			ArrayList<XMLNode> variables = node.getChildNodesByName("FieldDeclaration");
			for (XMLNode variable : variables) {
				processField(variable, lookUpTable, snippet);
			}
			ArrayList<XMLNode> methods = node.getChildNodesByName("MethodDeclaration");
			for (XMLNode method : methods) {
				String methodName = method.getChildNodeByName("SimpleName").getTextContent();
				String methodType = getType(method);
				lookUpTable.addClassVariable(methodName, methodType);
			}
		}

		// Process a field
		lookUpTable = new LookUpTable();
		lookUpTable.enterClass();
		nodeList = ast.getElementsByTagName("FieldDeclaration");
		for (XMLNode variable : nodeList) {
			processField(variable, lookUpTable, snippet);
		}

		// Process a method
		nodeList = ast.getElementsByTagName("MethodDeclaration");
		for (XMLNode method : nodeList) {
			String methodName = method.getChildNodeByName("SimpleName").getTextContent();
			String methodType = getType(method);
			lookUpTable.addClassVariable(methodName, methodType);
		}
		for (XMLNode method : nodeList) {
			processMethod(method, lookUpTable, snippet);
		}

		// Remove empty or error statements
		for (Iterator<Block> iterator = snippet.blocks.iterator(); iterator.hasNext();) {
			Block block = iterator.next();
			for (Iterator<Statement> siterator = block.iterator(); siterator.hasNext();) {
				Statement statement = siterator.next();
				while (statement.size() > (keepFunctionCallTypes ? 3 : 2)) {
					statement.remove(statement.size() - 1);
				}
				if (statement.isEmpty() || statement.contains(null) || statement.contains("null")
						|| statement.contains("___")) {
					siterator.remove();
				}
			}
		}

		return snippet;
	}

	/**
	 * Returns a substring of a string given a delimiter using python-like list comprehensions.</br>
	 * Python corresponding command: {@code delimiter.join(string.split(delimiter)[from, to])}</br>
	 * Example call {@code getSubstring("aa.bb.ccc.dd.eee", ".", 1, -1)} gives {@code "bb.ccc.dd"}.
	 * 
	 * @param string the string to be split.
	 * @param delimiter the delimiter at which the string is split.
	 * @param from the starting point of the list.
	 * @param to the ending point of the list which can be negative.
	 * @return the string joined after processing.
	 */
	private static String getSubstring(String string, CharSequence delimiter, int from, int to) {
		String[] splitString = string.split(delimiter.toString());
		if (to < 0)
			to += splitString.length;
		splitString = Arrays.copyOfRange(splitString, from, to);
		return String.join(delimiter, splitString);
	}

	/**
	 * Returns the Abstract Syntax Tree of a snippet. This function can also handle non-complete snippets (i.e. those
	 * not surrounded by a method or class).
	 * 
	 * @param snippet the snippet given as a string.
	 * @param keepNodeInfo denotes if any added nodes should be kept in the AST.
	 * @return the AST of a snippet.
	 */
	protected static String getASTofSnippet(String snippet, boolean keepNodeInfo) {
		String ast = ASTExtractor.parseString(snippet);
		if (!ast.trim().startsWith("<CompilationUnit>")) {
			// Put code inside class declaration
			ast = ASTExtractor.parseString("class SampleClass{\n" + snippet + "\n}\n");
			if (!keepNodeInfo)
				ast = getSubstring(ast, "\n", 3, -2);
			if (!getSubstring(ast, "\n", 3, -2).trim().startsWith("<MethodDeclaration>")) {
				// Put code inside method declaration
				ast = ASTExtractor.parseString("class SampleClass{\nvoid SampleMethod(){\n" + snippet + "\n}\n\n}\n");
				if (!keepNodeInfo)
					ast = getSubstring(ast, "\n", 6, -3);
			}
		}
		return ast;
	}

	/**
	 * Returns the Abstract Syntax Tree of a snippet.
	 * 
	 * @param snippet the snippet given as a string.
	 * @return the AST of a snippet.
	 */
	protected static String getASTofSnippet(String snippet) {
		return getASTofSnippet(snippet, true);
	}

	/**
	 * Extracts the sequence for a snippet.
	 * 
	 * @param snippet the snippet of which the sequence is extracted.
	 * @return the snippet as a list of statements.
	 */
	public static ArrayList<String> extractSequence(String snippet) {
		return extractSequence(snippet, false, false);
	}

	/**
	 * Extracts the sequence for a snippet.
	 * 
	 * @param snippet the snippet of which the sequence is extracted.
	 * @param keepFunctionCallTypes {@code true} if call types should be kept, or {@code false} otherwise.
	 * @param keepLiterals {@code true} if literals (primitives) should be kept, or {@code false} otherwise.
	 * @return the snippet as a list of statements.
	 */
	public static ArrayList<String> extractSequence(String snippet, boolean keepFunctionCallTypes, boolean keepLiterals) {
		String ast = getASTofSnippet(snippet);
		Snippet seq = createSequence(ast, keepFunctionCallTypes, keepLiterals);
		ArrayList<String> sequence = new ArrayList<String>();
		for (Block block : seq.blocks) {
			for (Statement statement : block) {
				sequence.add(statement.toString());
			}
		}
		return sequence;
	}

}
