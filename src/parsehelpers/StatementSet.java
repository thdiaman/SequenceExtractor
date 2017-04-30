package parsehelpers;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Class that represents a set of statements.
 * 
 * @author themis
 */
@SuppressWarnings("serial")
public class StatementSet extends HashSet<String> {

	/**
	 * Initializes this set providing its items.
	 * 
	 * @param items the items of the set.
	 */
	public StatementSet(String... items) {
		super();
		addAll(Arrays.asList(items));
	}

	/**
	 * Initializes this set providing other sets. This function implements the union of the given sets.
	 * 
	 * @param sets the sets that contain all items to be added in this set.
	 */
	public StatementSet(StatementSet... sets) {
		super();
		for (StatementSet set : sets)
			addAll(set);
	}

}
