from sequenceextractor import SequenceExtractor

if __name__ == '__main__':
	'''Used as a test for the python bindings'''
	sequence_extractor = SequenceExtractor("../target/SequenceExtractor-0.2.jar")
	sequence = sequence_extractor.parse_snippet(
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
	)
	print(sequence)
	sequence_extractor.close()
