package parsehelpers;

import java.util.ArrayList;

/**
 * Class that represents a snippet as a sequence of blocks.
 * 
 * @author themis
 */
public class Snippet {

	/**
	 * The blocks that are contained in this statement.
	 */
	public ArrayList<Block> blocks;

	/**
	 * Initializes this snippet.
	 */
	public Snippet() {
		blocks = new ArrayList<Block>();
	}

	/**
	 * Adds a new block to this snippet.
	 */
	public void addBlock() {
		blocks.add(new Block());
	}

	/**
	 * Adds a statement to the last added block.
	 * 
	 * @param statement the statement to be added.
	 */
	public void addStatement(Statement statement) {
		blocks.get(blocks.size() - 1).add(statement);
	}

	/**
	 * Returns a string representation of this snippet.
	 * 
	 * @return a string representation of this snippet.
	 */
	@Override
	public String toString() {
		String ret = "";
		for (Block block : blocks) {
			for (Statement statement : block) {
				if (statement.size() > 1)
					ret += statement.toString() + "\n";
			}
		}
		return ret;
	}

}
