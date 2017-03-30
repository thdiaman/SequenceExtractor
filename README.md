SequenceExtractor: Statement Sequence Extractor for Java Source Code Snippets
=============================================================================
SequenceExtractor is a statement sequence extractor for Java source code snippets.
The tool allows exporting the sequence of statements of snippets in a list format.
It can be used as a library either from Java or from Python using the Python binding.

Using as a library
------------------
Import the library in your code. Then, you can parse snippets as follows:
<pre><code>ArrayList<String> sequence = SequenceExtractor.extractSequence(""
						+ "JFrame frame = new JFrame(\"myframe\");\n"
						+ "JPanel panel = new JPanel()\n;"
						+ "Container pane = frame.getContentPane();\n"
						+ "GridLayout layout = new GridLayout(2,2);\n"
						+ "panel.setLayout(layout);\n"
						+ "panel.add(upperLeft);\n"
						+ "panel.add(upperRight);\n"
						+ "panel.add(lowerLeft);\n"
						+ "panel.add(lowerRight);\n"
						+ "pane.add(panel);\n"
						);</code></pre>  
The result is a list with the sequence of calls for the snippet. For the above example the result is:
<pre><code>[CI_JFrame, CI_JPanel, FC_Container, CI_GridLayout, FC_void, FC_void, FC_void, FC_void, FC_void, FC_void]</code></pre>  

There are three types of commands:
- object instantiations (<code>CI</code>)
- assignments (<code>AM</code>)
- function calls (<code>FC</code>)  

There are also certain options when extracting the snippets provided asparameters of the <code>extractSequence</code> function. These are:
- keepFunctionCallTypes: denotes whether to output also the objects performing the function calls (instead of only the return types), default is false.
- keepLiterals denotes if commands with literals (primitive types) should be extracted, or discarded, default is false.

Using in Python
---------------
SequenceExtractor also has python bindings. Using the python wrapper is simple. At first, the library
has to be imported and the SequenceExtractor object has to be initialized given the path to the jar
of the library and the options to keep function call types (<code>keep_function_call_types</code>)
and keep literals (<code>keep_literals</code>):
<pre><code>sequence_extractor = SequenceExtractor("path/to/SequenceExtractor-0.1.jar", True, False)</code></pre>
After that, you can parse snippets as follows:
<pre><code>sequence = sequence_extractor.parse_snippet(
			"JFrame frame = new JFrame(\"myframe\");\n" +
			"JPanel panel = new JPanel();\n" +
			"Container pane = frame.getContentPane();\n" +
			"GridLayout layout = new GridLayout(2,2);\n" +
			"panel.setLayout(layout);\n" +
			"panel.add(upperLeft);\n" +
			"panel.add(upperRight);\n" +
			"panel.add(lowerLeft);\n" +
			"panel.add(lowerRight);\n" +
			"pane.add(panel)\n;"
	)</code></pre>
  
Note that after using the library, you have to close the SequenceExtractor object using function <code>close</code>, i.e.:<pre><code>sequence_extractor.close()</code></pre>


