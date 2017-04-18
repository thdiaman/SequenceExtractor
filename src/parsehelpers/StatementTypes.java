package parsehelpers;

public class StatementTypes {
	public final static StatementSet blockStatementType = new StatementSet("Block");
	public final static StatementSet catchClauseStatementType = new StatementSet("CatchClause");
	public final static StatementSet simpleStatementTypes = new StatementSet("VariableDeclarationStatement",
			"ExpressionStatement", "ReturnStatement", "ThrowStatement");
	public final static StatementSet conditionStatementTypes = new StatementSet("IfStatement", "SwitchStatement",
			"TryStatement");
	public final static StatementSet loopStatementTypes = new StatementSet("ForStatement", "WhileStatement",
			"DoStatement");
	public final static StatementSet branchStatementTypes = new StatementSet(conditionStatementTypes,
			loopStatementTypes);
	public final static StatementSet allStatementTypes = new StatementSet(simpleStatementTypes, branchStatementTypes);
	public final static StatementSet allAndBlockStatementTypes = new StatementSet(allStatementTypes,
			blockStatementType, catchClauseStatementType);
}
