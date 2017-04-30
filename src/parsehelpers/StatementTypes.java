package parsehelpers;

/**
 * Class keeping hold of the different sets of statement types in ASTs.
 * 
 * @author themis
 */
public class StatementTypes {

	/**
	 * The block statement type.
	 */
	public final static StatementSet blockStatementType = new StatementSet("Block");

	/**
	 * The catch statement type.
	 */
	public final static StatementSet catchClauseStatementType = new StatementSet("CatchClause");

	/**
	 * The simple statements, including expressions, variable declarations, etc.
	 */
	public final static StatementSet simpleStatementTypes = new StatementSet("VariableDeclarationStatement",
			"ExpressionStatement", "ReturnStatement", "ThrowStatement");

	/**
	 * The condition statement types, including if, switch and try statements.
	 */
	public final static StatementSet conditionStatementTypes = new StatementSet("IfStatement", "SwitchStatement",
			"TryStatement");

	/**
	 * The loop statement types, including for, while and do/while statements.
	 */
	public final static StatementSet loopStatementTypes = new StatementSet("ForStatement", "WhileStatement",
			"DoStatement");

	/**
	 * The branch statement types, including conditions and loops.
	 */
	public final static StatementSet branchStatementTypes = new StatementSet(conditionStatementTypes,
			loopStatementTypes);

	/**
	 * All statement types apart from blocks and catch clauses.
	 */
	public final static StatementSet allStatementTypes = new StatementSet(simpleStatementTypes, branchStatementTypes);

	/**
	 * All statement types including blocks and catch clauses.
	 */
	public final static StatementSet allAndBlockStatementTypes = new StatementSet(allStatementTypes, blockStatementType,
			catchClauseStatementType);
}
