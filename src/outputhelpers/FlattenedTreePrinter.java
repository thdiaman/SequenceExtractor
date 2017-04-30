package outputhelpers;

import parsehelpers.Snippet;

/**
 * Class that prints the snippets as flattened trees.
 * 
 * @author themis
 */
public class FlattenedTreePrinter implements SnippetPrinter {

	/**
	 * Receives a snippet and returns a string representation in the form of flattened trees.
	 * Given for example the snippet tree [[A, [B, C], [D, E]]], this function would return
	 * [[A, B, D], [A, B, E], [A, C, D], [A, C, E]].
	 * 
	 * @param snippet the snippet to be parsed.
	 * @return a flattened tree representation for the snippet.
	 */
	@Override
	public String snippetToString(Snippet snippet) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
