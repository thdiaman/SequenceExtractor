package parsehelpers;

import java.util.ArrayList;
import java.util.TreeMap;

import sequenceextractor.LevelOrderPair;

/**
 * Class that represents a snippet as a sequence of blocks.
 * 
 * @author themis
 */
public class Snippet {

	/**
	 * The blocks that are contained in this snippet.
	 */
	public TreeMap<LevelOrderPair, ArrayList<Block>> blocks;
	int currentLevel;
	int currentOrder;

	/**
	 * Initializes this snippet.
	 */
	public Snippet() {
		blocks = new TreeMap<LevelOrderPair, ArrayList<Block>>();
		currentLevel = -1;
		currentOrder = 0;
	}

	/**
	 * Adds a new block to this snippet.
	 */
	public void addBlock(int level) {
		LevelOrderPair levelAndOrder = new LevelOrderPair(currentLevel, currentOrder);
		if (!blocks.containsKey(levelAndOrder))
			blocks.put(levelAndOrder, new ArrayList<Block>());
		blocks.get(levelAndOrder).add(new Block());
	}

	public void addBlock() {
		addBlock(currentLevel);
	}

	public void levelInner() {
		currentLevel++;
		currentOrder++;
		if (blocks.size() > 0)
			currentOrder = blocks.lastKey().order + 1;
	}

	public void levelOuter() {
		currentLevel--;
		if (blocks.size() > 0)
			currentOrder = blocks.lastKey().order + 1;
	}

	public void startMethodBlock() {
		addStatement(new Statement("START", "METHOD"));
	}

	public void endMethodBlock() {
		addStatement(new Statement("END", "METHOD"));
	}

	public void startBranchBlock(String type) {
		addStatement(new Statement("START", type));
	}

	public void elseifBranchBlock(String type) {
		addStatement(new Statement("ELSEIF", type));
	}

	public void elseBranchBlock(String type) {
		addStatement(new Statement("ELSE", type));
	}

	public void endBranchBlock(String type) {
		addStatement(new Statement("END", type));
	}

	/**
	 * Adds a statement to the last added block.
	 * 
	 * @param statement the statement to be added.
	 */
	public void addStatement(Statement statement) {
		LevelOrderPair blockKey = new LevelOrderPair(currentLevel, currentOrder);
		if (!blocks.containsKey(blockKey))
			addBlock();
		blocks.get(new LevelOrderPair(currentLevel, currentOrder))
				.get(blocks.get(new LevelOrderPair(currentLevel, currentOrder)).size() - 1).add(statement);
	}

	/**
	 * Returns a string representation of this snippet.
	 * 
	 * @return a string representation of this snippet.
	 */
	@Override
	public String toString() {
		String ret = "";
		for (LevelOrderPair orderAndLevel : blocks.keySet()) {
			for (Block block : blocks.get(orderAndLevel)) {
				if (block.size() > 0) {
					for (Statement statement : block) {
						if (statement.size() > 1)
							ret += new String(new char[3 * orderAndLevel.level + 1]).replace("\0", " ")
									+ statement.toString() + "\n";
					}
				}
			}
		}
		return ret;
	}

}
