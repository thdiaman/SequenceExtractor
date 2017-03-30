package sequenceextractor;

import java.util.ArrayList;
import sequenceextractor.SequenceExtractor;

/**
 * Class used as a test of the {@link SequenceExtractor}.
 * 
 * @author themis
 */
public class SequenceExtractorTest {

	/**
	 * Gives a snippet to the sequence extractor and prints the extracted sequence.
	 * 
	 * @param args unused parameter.
	 */
	public static void main(String[] args) {
		// @formatter:off
		ArrayList<String> sequence = SequenceExtractor.extractSequence(""
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
						);
		// @formatter:on
		System.out.println(sequence);
	}
}
