package parsehelpers;

import java.util.HashMap;

/**
 * Class that implements a look up table for variables in all levels.
 * 
 * @author themis
 */
public class LookUpTable {

	/**
	 * Keeps the super class of the class for which the look up table is created.
	 */
	String superClass;

	/**
	 * Holds all class level variables.
	 */
	HashMap<String, String> classLevelTable;

	/**
	 * Holds all method level variables.
	 */
	HashMap<String, String> methodLevelTable;

	/**
	 * Initializes this table.
	 */
	public LookUpTable() {

	}

	/**
	 * Method that is called when entering a new class.
	 */
	public void enterClass() {
		classLevelTable = new HashMap<String, String>();
		superClass = "";
	}

	/**
	 * Method that is called when entering a new class.
	 * 
	 * @param superClass the super class of the class that is entered.
	 */
	public void enterClass(String superClass) {
		if (superClass != null) {
			classLevelTable = new HashMap<String, String>();
			this.superClass = superClass;
		} else
			enterClass();
	}

	/**
	 * Method that is called when entering a new method.
	 */
	public void enterMethod() {
		methodLevelTable = new HashMap<String, String>();
	}

	/**
	 * Adds a new class variable in this look up table.
	 * 
	 * @param variableName the name of the variable to be added.
	 * @param variableType the type of the variable that is added.
	 */
	public void addClassVariable(String variableName, String variableType) {
		classLevelTable.put(variableName, variableType);
	}

	/**
	 * Adds a new method variable in this look up table.
	 * 
	 * @param variableName the name of the variable to be added.
	 * @param variableType the type of the variable that is added.
	 */
	public void addMethodVariable(String variableName, String variableType) {
		methodLevelTable.put(variableName, variableType);
	}

	/**
	 * Returns the type of a variable given its name. The method table is looked up first and then if the variable is
	 * not declared there, the variable is searched in class scope.
	 * 
	 * @param variableName the name of the variable.
	 * @return the type of the variable given or {@code "___"} if the variable does not exist.
	 */
	public String getTypeOfVariable(String variableName) {
		if (methodLevelTable != null && methodLevelTable.containsKey(variableName))
			return methodLevelTable.get(variableName);
		else if (classLevelTable.containsKey(variableName))
			return classLevelTable.get(variableName);
		else
			return "___";
	}

	/**
	 * Returns the super class of the class for which the look up table is created.
	 * 
	 * @return the super class of the class for which the look up table is created.
	 */
	public String getSuperClass() {
		return superClass;
	}

	/**
	 * Returns a string representation of this look up table.
	 * 
	 * @return a string representation of this look up table.
	 */
	@Override
	public String toString() {
		return "superClass: " + superClass + "\nclassLevelTable: " + classLevelTable + "\nmethodLevelTable: "
				+ methodLevelTable;
	}
}