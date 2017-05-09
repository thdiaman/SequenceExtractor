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

	/**
	 * The current level of the tree node of this snippet.
	 */
	private int currentLevel;

	/**
	 * The current order of the tree node of this snippet.
	 */
	private int currentOrder;

	/**
	 * Initializes this snippet.
	 */
	public Snippet() {
		blocks = new TreeMap<LevelOrderPair, ArrayList<Block>>();
		currentLevel = -1;
		currentOrder = 0;
	}

	/**
	 * Adds a new block to this snippet in the given level.
	 * 
	 * @param level the level to which the new block is added.
	 */
	public void addBlock(int level) {
		LevelOrderPair levelAndOrder = new LevelOrderPair(currentLevel, currentOrder);
		if (!blocks.containsKey(levelAndOrder))
			blocks.put(levelAndOrder, new ArrayList<Block>());
		blocks.get(levelAndOrder).add(new Block());
	}

	/**
	 * Moves a level in for this snippet.
	 */
	public void levelInner() {
		currentLevel++;
		currentOrder++;
		if (blocks.size() > 0)
			currentOrder = blocks.lastKey().order + 1;
	}

	/**
	 * Moves a level out for this snippet.
	 */
	public void levelOuter() {
		currentLevel--;
		if (blocks.size() > 0)
			currentOrder = blocks.lastKey().order + 1;
	}

	/**
	 * Starts a new block.
	 * 
	 * @param type the type of the block (METHOD, LOOP, CONDITION, CASE, TRY).
	 */
	public void startBlock(String type) {
		addStatement(new Statement("START", type));
	}

	/**
	 * Adds a path to the last branch block.
	 * 
	 * @param type the type of the block (CONDITION, CASE, TRY).
	 */
	public void elseifBlock(String type) {
		addStatement(new Statement("ELSEIF", type));
	}

	/**
	 * Adds a final path to the last branch block (e.g. else statement for if branches).
	 * 
	 * @param type the type of the block (LOOP, CONDITION, CASE, TRY).
	 */
	public void elseBlock(String type) {
		addStatement(new Statement("ELSE", type));
	}

	/**
	 * Ends the last block.
	 * 
	 * @param type the type of the block (METHOD, LOOP, CONDITION, CASE, TRY).
	 */
	public void endBlock(String type) {
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
			addBlock(currentLevel);
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
