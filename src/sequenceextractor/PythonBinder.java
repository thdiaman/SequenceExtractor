package sequenceextractor;

import java.nio.charset.Charset;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

/**
 * Class used to bind this library to a python file.
 * 
 * @author themis
 */
public class PythonBinder {

	/**
	 * Function used to bind this library to a python file. It works by opening a {@link Scanner} on the standard input,
	 * reading the required task in the form of a message and writing the result as a message in the standard output.
	 * The base 64 format is used to send the messages.
	 * 
	 * @param args receives the booleans denoting if function call types, literals and branches should be kept, if
	 *            the output should be a tree or sequence, and if the output should be flattened.
	 */
	public static void main(String[] args) {
		boolean keepFunctionCallTypes = args.length > 0 ? Boolean.parseBoolean(args[0]) : false;
		boolean keepLiterals = args.length > 1 ? Boolean.parseBoolean(args[1]) : false;
		boolean keepBranches = args.length > 2 ? Boolean.parseBoolean(args[2]) : true;
		boolean outputTree = args.length > 3 ? Boolean.parseBoolean(args[3]) : false;
		boolean flattenOutput = args.length > 4 ? Boolean.parseBoolean(args[4]) : true;
		boolean addUniqueIDs = args.length > 5 ? Boolean.parseBoolean(args[5]) : false;
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			// Receive message and decode it
			String b64message = scanner.nextLine();
			String message = new String(DatatypeConverter.parseBase64Binary(b64message));

			// Operate on message and return response
			String messageresult = "";
			if (message.equals("START_OF_TRANSMISSION")) {
				messageresult = message;
				String b64messageresult = DatatypeConverter
						.printBase64Binary(messageresult.getBytes(Charset.forName("US-ASCII")));
				System.out.println(b64messageresult);
				System.out.flush();
				continue;
			} else if (message.equals("END_OF_TRANSMISSION")) {
				messageresult = message;
				String b64messageresult = DatatypeConverter
						.printBase64Binary(messageresult.getBytes(Charset.forName("US-ASCII")));
				System.out.println(b64messageresult);
				System.out.flush();
				break;
			} else {
				messageresult = SequenceExtractor.extractSequence(message, keepFunctionCallTypes, keepLiterals,
						keepBranches, outputTree, flattenOutput, addUniqueIDs).toString();
				String b64messageresult = DatatypeConverter
						.printBase64Binary(messageresult.getBytes(Charset.forName("US-ASCII")));
				System.out.println(b64messageresult);
				System.out.flush();
			}
		}
		scanner.close();
	}

}
