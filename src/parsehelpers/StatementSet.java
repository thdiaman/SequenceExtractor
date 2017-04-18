package parsehelpers;

import java.util.Arrays;
import java.util.HashSet;

@SuppressWarnings("serial")
public class StatementSet extends HashSet<String> {

	public StatementSet(String... items) {
		super();
		addAll(Arrays.asList(items));
	}

	public StatementSet(StatementSet... sets) {
		super();
		for (StatementSet set : sets)
			addAll(set);
	}

}
