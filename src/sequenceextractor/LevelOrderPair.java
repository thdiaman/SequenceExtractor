package sequenceextractor;

public class LevelOrderPair implements Comparable<LevelOrderPair> {
	public final int level;
	public final int order;

	public LevelOrderPair(int level, int order) {
		this.level = level;
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 10000 * level;
		result = prime * result + order;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass())
			return false;
		else {
			LevelOrderPair o = ((LevelOrderPair) obj);
			if (level != o.level || order != o.order)
				return false;
		}
		return true;
	}

	public String toString() {
		return "(" + level + ", " + order + ")";
	}

	@Override
	public int compareTo(LevelOrderPair o) {
		if (order > o.order)
			return 1;
		else if (order < o.order)
			return -1;
		else
			return level > o.level ? 1 : level < o.level ? -1 : 0;
	}

}
