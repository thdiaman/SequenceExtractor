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
	 * @return a string representation for the snippet.
	 */
	public String snippetToString(Snippet snippet);

}
