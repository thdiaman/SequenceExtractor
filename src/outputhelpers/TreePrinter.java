package outputhelpers;

import parsehelpers.Block;
import parsehelpers.Snippet;
import parsehelpers.Statement;
import sequenceextractor.LevelOrderPair;

public class TreePrinter implements SnippetPrinter {

	@Override
	public String snippetToString(Snippet snippet) {
		String treeseq = "[";
		for (LevelOrderPair orderAndLevel : snippet.blocks.keySet()) {
			for (Block block : snippet.blocks.get(orderAndLevel)) {
				if (block.size() > 0) {
					for (Statement statement : block) {
						if (statement.toString().equals("START_METHOD"))
							treeseq += (treeseq.charAt(treeseq.length() - 1) == '[' ? "" : ", ") + "[";
						else if (statement.toString().equals("END_METHOD"))
							treeseq += "]";
						else if (statement.toString().equals("START_CONDITION")
								|| statement.toString().equals("START_CASE") || statement.toString().equals("START_TRY")
								|| statement.toString().equals("START_LOOP"))
							treeseq += (treeseq.charAt(treeseq.length() - 1) == '[' ? "" : ", ") + "[[";
						else if (statement.toString().equals("ELSE_CONDITION")
								|| statement.toString().equals("ELSEIF_CONDITION")
								|| statement.toString().equals("ELSE_CASE")
								|| statement.toString().equals("ELSEIF_CASE")
								|| statement.toString().equals("ELSEIF_TRY")
								|| statement.toString().equals("ELSE_LOOP"))
							treeseq += "], [";
						else if (statement.toString().equals("ELSE_TRY"))
							treeseq += "]]";
						else if (statement.toString().equals("END_CONDITION") || statement.toString().equals("END_CASE")
								|| statement.toString().equals("END_LOOP"))
							treeseq += "]]";
						else if (statement.toString().equals("END_TRY"))
							treeseq += "";
						else
							treeseq += (treeseq.charAt(treeseq.length() - 1) == '[' ? "" : ", ") + statement.toString();
					}
				}
			}
		}
		return treeseq + "]";
	}

}
