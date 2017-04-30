package outputhelpers;

import parsehelpers.Block;
import parsehelpers.Snippet;
import parsehelpers.Statement;
import sequenceextractor.LevelOrderPair;

/**
 * Class that prints the snippets as sequences.
 * 
 * @author themis
 */
public class SequencePrinter implements SnippetPrinter {

	/**
	 * Receives a snippet and returns a sequence representation for the snippet.
	 * An example of a sequence would be [[A, B, C], [D, E]], where [A, B, C] and [D, E] are sequences of two functions.
	 * 
	 * @param snippet the snippet to be parsed.
	 * @return a tree representation for the snippet.
	 */
	@Override
	public String snippetToString(Snippet snippet) {
		String seq = "[";
		for (LevelOrderPair orderAndLevel : snippet.blocks.keySet()) {
			for (Block block : snippet.blocks.get(orderAndLevel)) {
				if (block.size() > 0) {
					for (Statement statement : block) {
						if (statement.toString().equals("START_METHOD"))
							seq += (seq.charAt(seq.length() - 1) == '[' ? "" : ", ") + "[";
						else if (statement.toString().equals("END_METHOD"))
							seq += "]";
						else if (!(statement.toString().equals("START_METHOD")
								|| statement.toString().equals("END_METHOD")
								|| statement.toString().equals("START_CONDITION")
								|| statement.toString().equals("START_CASE") || statement.toString().equals("START_TRY")
								|| statement.toString().equals("START_LOOP")
								|| statement.toString().equals("ELSE_CONDITION")
								|| statement.toString().equals("ELSEIF_CONDITION")
								|| statement.toString().equals("ELSEIF_CASE")
								|| statement.toString().equals("ELSEIF_TRY") || statement.toString().equals("ELSE_CASE")
								|| statement.toString().equals("ELSE_TRY") || statement.toString().equals("ELSE_LOOP")
								|| statement.toString().equals("END_CONDITION")
								|| statement.toString().equals("END_CASE") || statement.toString().equals("END_TRY")
								|| statement.toString().equals("END_LOOP")))
							seq += (seq.charAt(seq.length() - 1) == '[' ? "" : ", ") + statement.toString();
					}
				}
			}
		}
		return seq + "]";
	}

}
