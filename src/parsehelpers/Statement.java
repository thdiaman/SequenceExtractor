package parsehelpers;

import java.util.ArrayList;

/**
 * A statement that represents a single java command.
 * 
 * @author themis
 */
@SuppressWarnings("serial")
public class Statement extends ArrayList<String> {

	/**
	 * Initializes an empty statement.
	 */
	public Statement() {
		super();
	}

	/**
	 * Initializes a special type of statement including the type of flow (e.g. START, END, etc.) and the given block
	 * type (e.g. METHOD). Example statements include "START, METHOD", "END, METHOD", etc.
	 * 
	 * @param flowType the type of the flow.
	 * @param blockType the type of the block.
	 */
	public Statement(String flowType, String blockType) {
		super();
		add(flowType);
		add(blockType);
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
