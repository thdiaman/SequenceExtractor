package sequenceextractor;

import java.util.ArrayList;
import java.util.Collections;

import parsehelpers.StatementTypes;
import xmlhelpers.XMLDocument;
import xmlhelpers.XMLNode;
import xmlhelpers.XMLNodeList;

/**
 * The AST preprocessor class that receives an AST and preprocesses it.
 * 
 * @author themis
 */
public class ASTPreprocessor {

	/**
	 * Preprocesses the branches of an AST by first adding a block to all branches (e.g. if statements with no braces),
	 * and then setting all branch paths in the same level (e.g. if, else if and else statements)
	 * 
	 * @param ast the AST to be preprocessed in XML format.
	 */
	public static void preprocessBranches(XMLDocument ast) {
		for (XMLNode method : ast.getElementsByTagName("MethodDeclaration")) {
			if (method.getChildNodeByName("Block") != null)
				addBlockToBranches(method.getChildNodeByName("Block"));
		}
		for (XMLNode method : ast.getElementsByTagName("MethodDeclaration")) {
			if (method.getChildNodeByName("Block") != null) {
				for (XMLNode ifstatement : method.getChildNodeByName("Block")
						.getDeepChildNodesRecursivelyByName(StatementTypes.branchStatementTypes)) {
					detectIfElseBranches(ifstatement);
					detectSwitchCaseBranches(ifstatement);
					putConditionsInBlocks(ifstatement);
				}
			}
		}
	}

	/**
	 * Adds a block to all branch statement types (e.g. if statements with no braces) of the given method block.
	 * 
	 * @param block the block of a method.
	 */
	public static void addBlockToBranches(XMLNode block) {
		ArrayList<XMLNode> statements = block.getChildNodesRecursivelyByName(StatementTypes.branchStatementTypes);
		for (XMLNode statement : statements) {
			// Add block to all branch statement types
			ArrayList<XMLNode> branchstatements = statement.getChildNodesRecursivelyByName(StatementTypes.allAndBlockStatementTypes);
			for (XMLNode branchstatement : branchstatements) {
				if (!branchstatement.hasName("Block"))
					branchstatement.insertNewParentNode(new XMLNode("Block"));
			}
			branchstatements = statement.getChildNodesByName("Block");
			for (XMLNode branchstatement : branchstatements) {
				addBlockToBranches(branchstatement);
			}
		}
	}

	/**
	 * Detects if/else if/else statements and puts them all under the same branch block. This function is recursive to
	 * support nested if statements.
	 * 
	 * @param ifstatement an if statement of a method block.
	 */
	public static void detectIfElseBranches(XMLNode ifstatement) {
		ArrayList<XMLNode> statements = ifstatement.getDeepChildNodesRecursivelyByName("IfStatement");
		Collections.reverse(statements);
		for (XMLNode statement : statements) {
			if (statement.getParentNode().hasName("Block")
					&& statement.getParentNode().getParentNode().hasName("IfStatement")) {
				XMLNodeList branchstatements = statement.getChildNodesRecursivelyByName("Block", "InfixExpression");
				for (XMLNode branchstatement : branchstatements) {
					branchstatement.removeParentNode();
					branchstatement.removeParentNode();
				}
			}
		}
	}

	/**
	 * Detects switch/case/default statements and puts them all under the same branch block. This function is recursive
	 * to support nested switch statements.
	 * 
	 * @param switchstatement a switch statement of a method block.
	 */
	private static void detectSwitchCaseBranches(XMLNode switchstatement) {
		if (switchstatement.hasName("SwitchStatement")) {
			for (XMLNode nestedswitchstatement : switchstatement
					.getDeepChildNodesRecursivelyByName("SwitchStatement")) {
				detectSwitchCaseBranches(nestedswitchstatement);
			}

			ArrayList<XMLNode> statements = switchstatement.getChildNodesRecursivelyByName("SwitchCase", "Block",
					"BreakStatement");
			ArrayList<ArrayList<XMLNode>> cases = new ArrayList<ArrayList<XMLNode>>();
			// Find which are the blocks for each case
			for (XMLNode statement : statements) {
				if (statement.hasName("SwitchCase")) {
					cases.add(new ArrayList<XMLNode>());
					cases.get(cases.size() - 1).add(statement);
				} else if (statement.hasName("Block", "BreakStatement")) {
					for (ArrayList<XMLNode> acase : cases) {
						if (!acase.get(acase.size() - 1).hasName("BreakStatement"))
							acase.add(statement);
					}
				}
			}

			// Merge blocks
			XMLNode newSwitchstatement = new XMLNode("SwitchStatement");
			for (ArrayList<XMLNode> acase : cases) {
				XMLNode currentSwitch = null;
				for (XMLNode statement : acase) {
					if (statement.hasName("SwitchCase")) {
						currentSwitch = new XMLNode("Block");
						currentSwitch.addNewChildNode(statement);
					} else if (statement.hasName("Block")) {
						currentSwitch.addNewChildNodes(statement.getChildNodes());
					}
				}
				newSwitchstatement.addNewChildNode(currentSwitch);
			}
			switchstatement.getParentNode().replaceChild(newSwitchstatement, switchstatement);
		}
	}

	/**
	 * Puts all infix expressions of conditions in blocks so that they are in the same level as the corresponding branch
	 * of the condition.
	 * 
	 * @param ifstatement an if statement of a method block.
	 */
	private static void putConditionsInBlocks(XMLNode ifstatement) {
		XMLNodeList branchstatements = ifstatement.getChildNodesRecursivelyByName("Block", "InfixExpression");
		for (int i = 0; i < branchstatements.size() - 1; i++) {
			XMLNode infixstatement = branchstatements.get(i);
			XMLNode blockstatement = branchstatements.get(i + 1);
			if (infixstatement.hasName("InfixExpression") && blockstatement.hasName("Block")) {
				blockstatement.addNewChildNodeInTheBeginning(infixstatement.getParentNode().removeChild(infixstatement));
			}
		}
	}

}
