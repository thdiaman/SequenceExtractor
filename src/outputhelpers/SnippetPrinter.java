package outputhelpers;

import parsehelpers.Snippet;

/**
 * Interface that defines how the snippets should be represented to string.
 * 
 * @author themis
 */
public interface SnippetPrinter {

	/**
	 * Receives a snippet and returns a string representation.
	 * 
	 * @param snippet the snippet to be parsed.
	 * @param addUniqueIDs boolean denoting whether statements should have IDs ({@code true}) or not ({@code false}).
	 * @return a string representation for the snippet.
	 */
	public String snippetToString(Snippet snippet, boolean addUniqueIDs);

}
