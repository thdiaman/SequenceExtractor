package sequenceextractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import astextractor.ASTExtractor;
import outputhelpers.FlattenedSequencePrinter;
import outputhelpers.FlattenedTreePrinter;
import outputhelpers.SequencePrinter;
import outputhelpers.SnippetPrinter;
import outputhelpers.TreePrinter;
import parsehelpers.Block;
import parsehelpers.LookUpTable;
import parsehelpers.Snippet;
import parsehelpers.Statement;
import parsehelpers.StatementTypes;
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
				XMLNodeList methodTypeAndName = node.getChildNodesByName("SimpleName");
				if (methodTypeAndName.size() > 0) {
					String methodType = lookUpTable.getTypeOfVariable(methodTypeAndName.get(0).getTextContent());
					if (methodType != null && methodType != "___" && output.size() > 1) {
						if (!(methodType.equals(output.get(1)) || methodType.equals("byte")
								|| methodType.equals("short") || methodType.equals("int") || methodType.equals("long")
								|| methodType.equals("float") || methodType.equals("double")
								|| methodType.equals("char") || methodType.equals("String")
								|| methodType.equals("boolean")))
							output.add(methodType);
					}
				}
				if (methodTypeAndName.size() > 1) {
					String methodName = methodTypeAndName.get(1).getTextContent();
					if (output.size() > 2) {
						output.set(2, output.get(2) + "." + methodName);
					}
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

	private static void processBlock(XMLNode block, LookUpTable lookUpTable, Snippet snippet, boolean keepBranches) {
		if (block != null) {
			ArrayList<XMLNode> statements = block.getChildNodesRecursivelyByName(StatementTypes.allStatementTypes);
			for (XMLNode statement : statements) {
				if (statement.hasName("VariableDeclarationStatement")) {
					String variableType = getType(statement);
					XMLNode vardecl = statement.getChildNodeByName("VariableDeclarationFragment");
					ArrayList<XMLNode> variableNodes = vardecl.getChildNodesByName("SimpleName");
					for (XMLNode variableNode : variableNodes) {
						String variableName = variableNode.getTextContent();
						lookUpTable.addMethodVariable(variableName, variableType);
					}
					if (vardecl.hasMoreThanOneChildren()) {
						for (XMLNode classinstantiation : statement.getChildNodesRecursivelyByName("ClassInstanceCreation", "MethodInvocation")) {
							XMLNodeList innerinstantiations = classinstantiation.getDeepChildNodesRecursivelyByName("ClassInstanceCreation", "MethodInvocation");
							Collections.reverse(innerinstantiations);
							for (XMLNode innerclassinstantiation : innerinstantiations) {
								snippet.addStatement(iterateLowLevelCode(innerclassinstantiation, lookUpTable));
							}
						}
						snippet.addStatement(iterateLowLevelCode(vardecl, lookUpTable));
					}
				} else if (statement.hasName(StatementTypes.branchStatementTypes)) {
					ArrayList<XMLNode> branchstatements = statement.getChildNodesByName("Block");
					if (statement.getChildNodes().size() > 0 && !statement.getChildNodes().get(0).hasName("Block")) {
						snippet.addStatement(iterateLowLevelCode(statement.getChildNodes().get(0), lookUpTable));
					}

					// Check the type of the branch
					if (statement.hasName(StatementTypes.loopStatementTypes) && branchstatements.size() > 0) {
						snippet.levelInner();
						snippet.startBlock("LOOP");
						processBlock(branchstatements.get(0), lookUpTable, snippet, keepBranches);
						if (keepBranches)
							snippet.elseBlock("LOOP");
						snippet.endBlock("LOOP");
						snippet.levelOuter();
					} else if (statement.hasName("IfStatement") && branchstatements.size() > 0) {
						snippet.levelInner();
						snippet.startBlock("CONDITION");
						processBlock(branchstatements.get(0), lookUpTable, snippet, keepBranches);
						if (keepBranches) {
							boolean hasElse = false;
							for (int i = 1; i < branchstatements.size(); i++) {
								if (branchstatements.get(i).getChildNodeByName("InfixExpression") != null)
									snippet.elseifBlock("CONDITION");
								else {
									hasElse = true;
									snippet.elseBlock("CONDITION");
								}
								processBlock(branchstatements.get(i), lookUpTable, snippet, keepBranches);
							}
							if (!hasElse)
								snippet.elseBlock("CONDITION");
						}
						snippet.endBlock("CONDITION");
						snippet.levelOuter();
					} else if (statement.hasName("SwitchStatement") && branchstatements.size() > 0) {
						snippet.levelInner();
						snippet.startBlock("CASE");
						processBlock(branchstatements.get(0), lookUpTable, snippet, keepBranches);
						if (keepBranches) {
							boolean hasDefault = false;
							for (int i = 1; i < branchstatements.size(); i++) {
								if (!branchstatements.get(i).getChildNodeByName("SwitchCase")
										.textContentStartsWith("default")) {
									snippet.elseifBlock("CASE");
								} else {
									hasDefault = true;
									snippet.elseBlock("CASE");
								}
								processBlock(branchstatements.get(i), lookUpTable, snippet, keepBranches);
							}
							if (!hasDefault)
								snippet.elseBlock("CASE");
						}
						snippet.endBlock("CASE");
						snippet.levelOuter();
					} else if (statement.hasName("TryStatement") && branchstatements.size() > 0) {
						snippet.levelInner();
						snippet.startBlock("TRY");
						processBlock(branchstatements.get(0), lookUpTable, snippet, keepBranches);
						if (keepBranches) {
							boolean hasCatch = false;
							boolean hasFinally = false;
							for (int i = 1; i < branchstatements.size(); i++) {
								if (branchstatements.get(i).getChildNodeByName("CatchClause") != null) {
									hasCatch = true;
									snippet.elseifBlock("TRY");
									processBlock(branchstatements.get(i), lookUpTable, snippet, keepBranches);
								}
							}
							if (!hasCatch)
								snippet.elseifBlock("TRY");
							for (int i = 1; i < branchstatements.size(); i++) {
								if (branchstatements.get(i).getChildNodeByName("CatchClause") == null) {
									hasFinally = true;
									snippet.elseBlock("TRY");
									processBlock(branchstatements.get(i), lookUpTable, snippet, keepBranches);
								}
							}
							if (!hasFinally)
								snippet.elseBlock("TRY");
						}
						snippet.endBlock("TRY");
						snippet.levelOuter();
					}
				} else {
					for (XMLNode classinstantiation : statement.getChildNodesRecursivelyByName("ClassInstanceCreation", "MethodInvocation")) {
						XMLNodeList innerinstantiations = classinstantiation.getDeepChildNodesRecursivelyByName("ClassInstanceCreation", "MethodInvocation");
						Collections.reverse(innerinstantiations);
						for (XMLNode innerclassinstantiation : innerinstantiations) {
							snippet.addStatement(iterateLowLevelCode(innerclassinstantiation, lookUpTable));
						}
					}
					snippet.addStatement(iterateLowLevelCode(statement, lookUpTable));
				}
			}
		}
	}

	/**
	 * Processes a method and populates the snippet.
	 * 
	 * @param method the method node to be processed.
	 * @param lookUpTable the look up table for variables.
	 * @param snippet the snippet where statements are added.
	 * @param keepBranches {@code true} if all branches should be kept, or {@code false} for the first branch.
	 */
	private static void processMethod(XMLNode method, LookUpTable lookUpTable, Snippet snippet, boolean keepBranches) {
		lookUpTable.enterMethod();

		ArrayList<XMLNode> parameters = method.getChildNodesByName("SingleVariableDeclaration");
		for (XMLNode parameter : parameters) {
			String parameterName = parameter.getChildNodeByName("SimpleName").getTextContent();
			String parameterType = getType(parameter);
			lookUpTable.addMethodVariable(parameterName, parameterType);
		}

		XMLNode block = method.getChildNodeByName("Block");
		snippet.levelInner();
		snippet.startBlock("METHOD");
		processBlock(block, lookUpTable, snippet, keepBranches);
		snippet.endBlock("METHOD");
		snippet.levelOuter();
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
				snippet.levelInner();
				// snippet.addBlock();
				snippet.addStatement(iterateLowLevelCode(vardecl, lookUpTable));
				snippet.levelOuter();
			}
		}
	}

	/**
	 * Receives as input an AST in XML format and returns a sequence of statements
	 * 
	 * @param xml an AST in XML format.
	 * @param keepFunctionCallTypes {@code true} if call types should be kept, or {@code false} otherwise.
	 * @param keepLiterals {@code true} if literals (primitives) should be kept, or {@code false} otherwise.
	 * @param keepBranches {@code true} if all branches should be kept, or {@code false} for the first branch.
	 * @return a snippet as a sequence of statements
	 */
	private static Snippet createSequence(String xml, boolean keepFunctionCallTypes, boolean keepLiterals,
			boolean keepBranches) {
		// Preprocess the XML of the AST
		xml = "<file>" + xml + "</file>";
		xml = xml.replaceAll("\n|\r| ", "");
		xml = XMLDocument.removeXMLNodes(xml, "Javadoc", "Modifier", "Dimension", "ImportDeclaration",
				"PackageDeclaration");
		if (!keepLiterals)
			xml = XMLDocument.removeXMLNodes(xml, "BooleanLiteral", "StringLiteral", "NumberLiteral",
					"CharacterLiteral");
		XMLDocument ast = new XMLDocument(xml);
		ASTPreprocessor.preprocessBranches(ast);

		// Initialize the snippet and the look up table
		Snippet snippet = new Snippet();
		LookUpTable lookUpTable = new LookUpTable();
		XMLNodeList nodeList;
		// snippet.levelInner();

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
			processMethod(method, lookUpTable, snippet, keepBranches);
		}

		// Remove empty or error statements
		for (Iterator<ArrayList<Block>> iterator = snippet.blocks.values().iterator(); iterator.hasNext();) {
			ArrayList<Block> blocks = iterator.next();
			for (Iterator<Block> niterator = blocks.iterator(); niterator.hasNext();) {
				Block block = niterator.next();
				for (Iterator<Statement> siterator = block.iterator(); siterator.hasNext();) {
					Statement statement = siterator.next();
					if (statement.contains("___")) {
						statement.remove("___");
					}
					while (statement.size() > (keepFunctionCallTypes ? 3 : 2)) {
						statement.remove(statement.size() - 1);
					}
					if (statement.isEmpty() || statement.contains(null) || statement.contains("null")
							|| statement.contains("___")) {
						siterator.remove();
					}
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
	public static String extractSequence(String snippet) {
		return extractSequence(snippet, false, false, true, false, true, false);
	}

	/**
	 * Extracts the sequence for a snippet.
	 * 
	 * @param snippet the snippet of which the sequence is extracted.
	 * @param keepFunctionCallTypes {@code true} if call types should be kept, or {@code false} otherwise.
	 * @param keepLiterals {@code true} if literals (primitives) should be kept, or {@code false} otherwise.
	 * @param keepBranches {@code true} if all branches should be kept, or {@code false} for the first branch.
	 * @param outputTree {@code true} if the output should be a tree, or {@code false} for output as a sequence.
	 * @param flattenOutput {@code true} if the output should be flattened, or {@code false} otherwise.
	 * @param addUniqueIDs {@code true} if the statements should have IDs, or {@code false} otherwise.
	 * @return the snippet as a list of statements.
	 */
	public static String extractSequence(String snippet, boolean keepFunctionCallTypes, boolean keepLiterals,
			boolean keepBranches, boolean outputTree, boolean flattenOutput, boolean addUniqueIDs) {
		String ast = getASTofSnippet(snippet);
		Snippet seq = createSequence(ast, keepFunctionCallTypes, keepLiterals, keepBranches);
		SnippetPrinter printer;
		if (outputTree) {
			if (!flattenOutput)
				printer = new TreePrinter();
			else
				printer = new FlattenedTreePrinter();
		} else {
			if (!flattenOutput)
				printer = new SequencePrinter();
			else
				printer = new FlattenedSequencePrinter();
		}
		return printer.snippetToString(seq, addUniqueIDs);
	}

}
