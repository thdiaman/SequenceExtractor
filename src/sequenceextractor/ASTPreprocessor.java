package sequenceextractor;

import java.util.ArrayList;
import java.util.Collections;

import parsehelpers.StatementTypes;
import xmlhelpers.XMLDocument;
import xmlhelpers.XMLNode;
import xmlhelpers.XMLNodeList;

public class ASTPreprocessor {

	public static void preprocessBranches(XMLDocument ast) {
		for (XMLNode method : ast.getElementsByTagName("MethodDeclaration")) {
			addBlockToBranches(method.getChildNodeByName("Block"));
		}
		for (XMLNode method : ast.getElementsByTagName("MethodDeclaration")) {
			for (XMLNode ifstatement : method.getChildNodeByName("Block")
					.getChildNodesRecursivelyByName(StatementTypes.branchStatementTypes)) {
				detectIfElseBranches(ifstatement);
				detectSwitchCaseBranches(ifstatement);
				putConditionsInBlocks(ifstatement);
				// putCatchClausesInBlocks(ifstatement);
			}
		}
//		System.out.println(ast.toXMLString());
//		System.exit(0);
	}

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

	private static void putConditionsInBlocks(XMLNode ifstatement) {
		XMLNodeList branchstatements = ifstatement.getChildNodesRecursivelyByName("Block", "InfixExpression");
		for (int i = 0; i < branchstatements.size() - 1; i++) {
			XMLNode infixstatement = branchstatements.get(i);
			XMLNode blockstatement = branchstatements.get(i + 1);
			if (infixstatement.hasName("InfixExpression") && blockstatement.hasName("Block")) {
				blockstatement
						.addNewChildNodeInTheBeginning(infixstatement.getParentNode().removeChild(infixstatement));
			}
		}
	}

}
