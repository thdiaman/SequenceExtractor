package sequenceextractor;

/**
 * A pair denoting the level and the order of an extracted statement.
 * 
 * @author themis
 */
public class LevelOrderPair implements Comparable<LevelOrderPair> {

	/**
	 * The level of this pair.
	 */
	public final int level;

	/**
	 * The order of this pair.
	 */
	public final int order;

	/**
	 * Initializes this pair with a level and an order.
	 * 
	 * @param level the level of the pair.
	 * @param order the order of the pair.
	 */
	public LevelOrderPair(int level, int order) {
		this.level = level;
		this.order = order;
	}

	/**
	 * Returns a hash code value for this pair given its level and order, to be used in hashmaps.
	 * 
	 * @return a hash code value for this pair.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 10000 * level;
		result = prime * result + order;
		return result;
	}

	/**
	 * Indicates whether the given pair is equal to this one, i.e. if their levels and orders are equal.
	 * 
	 * @param otherLevelOrderPair the given object to be compared to this one.
	 * @return {@code true} this pair is equal to the given one, or {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object otherLevelOrderPair) {
		if (otherLevelOrderPair == null || getClass() != otherLevelOrderPair.getClass())
			return false;
		else {
			if (level != ((LevelOrderPair) otherLevelOrderPair).level
					|| order != ((LevelOrderPair) otherLevelOrderPair).order)
				return false;
		}
		return true;
	}

	/**
	 * Returns a string representation of this pair.
	 * 
	 * @return a string representation of this pair.
	 */
	@Override
	public String toString() {
		return "(" + level + ", " + order + ")";
	}

	/**
	 * Compares this object with the specified object, comparing first the order and the level of the objects. This
	 * function is used for sorting.
	 * 
	 * @param otherLevelOrderPair the given object to be compared to this one.
	 * @return 1 if this pair is larger than the given one, -1 if it is smaller, or 0 if the two objects are equal.
	 */
	@Override
	public int compareTo(LevelOrderPair otherLevelOrderPair) {
		if (order > otherLevelOrderPair.order)
			return 1;
		else if (order < otherLevelOrderPair.order)
			return -1;
		else
			return level > otherLevelOrderPair.level ? 1 : level < otherLevelOrderPair.level ? -1 : 0;
	}

}
