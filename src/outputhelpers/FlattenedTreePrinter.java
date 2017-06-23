package outputhelpers;

import org.python.util.PythonInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import parsehelpers.Snippet;

/**
 * Class that prints the snippets as flattened trees.
 * 
 * @author themis
 */
public class FlattenedTreePrinter extends TreePrinter implements SnippetPrinter {

	/**
	 * Receives a snippet and returns a string representation in the form of flattened trees.
	 * Given for example the snippet tree [[A, [B, C], [D, E]]], this function would return
	 * [[A, B, D], [A, B, E], [A, C, D], [A, C, E]].
	 * 
	 * @param snippet the snippet to be parsed.
	 * @param addUniqueIDs boolean denoting whether statements should have IDs ({@code true}) or not ({@code false}).
	 * @return a flattened tree representation for the snippet.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String snippetToString(Snippet snippet, boolean addUniqueIDs) {
		String tree = super.snippetToString(snippet, false);
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("flattened_tree_printer.py");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		String s = null;
		try {
			while ((length = inputStream.read(buffer)) != -1)
				outputStream.write(buffer, 0, length);
			s = outputStream.toString("UTF-8");
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PythonInterpreter pi = new PythonInterpreter();
		if (s != null)
			pi.exec(s);
		pi.exec("result = get_paths('" + tree + "', " + (addUniqueIDs ? "True": "False") + ")");
		ArrayList<ArrayList<String>> result = pi.get("result", ArrayList.class);
		return result.toString();
	}

}
