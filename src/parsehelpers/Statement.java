package parsehelpers;

import java.util.ArrayList;

/**
 * A statement that represents a single java command.
 * 
 * @author themis
 */
@SuppressWarnings("serial")
public class Statement extends ArrayList<String> {

	public Statement() {
		super();
	}

	public Statement(String flowType, String specialType) {
		super();
		add(flowType);
		add(specialType);
	}

	/**
	 * Returns a string representation of this statement.
	 * 
	 * @return a string representation of this statement.
	 */
	@Override
	public String toString() {
		if (size() > 2)
			return get(0) + "_" + get(1) + "(" + get(2) + ")";
		else
			return size() > 1 ? get(0) + "_" + get(1) : "";
	}
}
