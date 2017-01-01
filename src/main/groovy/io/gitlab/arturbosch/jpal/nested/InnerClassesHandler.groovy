package io.gitlab.arturbosch.jpal.nested

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.type.Type
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.internal.Validate

/**
 * Stores inner class information of a compilation unit.
 *
 * @author artur
 */
@CompileStatic
class InnerClassesHandler {

	private String outerClassName
	private Set<String> innerClassesNames

	InnerClassesHandler(CompilationUnit unit) {
		Validate.notNull(unit)
		def types = unit.getTypes()
		if (types.size() >= 1) {
			def mainClass = types[0]
			innerClassesNames = NodeHelper.findNamesOfInnerClasses(mainClass)
			outerClassName = mainClass.name
		} else {
			throw new NoClassesException("Given compilation unit has no type declarations!")
		}
	}

	/**
	 * Tests if the given class name is a inner cass.
	 * @param className name as string
	 * @return true if inner class
	 */
	boolean isInnerClass(String className) {
		Validate.notNull(className)
		return innerClassesNames.contains(className)
	}

	/**
	 * Appends the outer class to the given inner class
	 * @param type probably a inner class type
	 * @return unqualified name for inner class
	 */
	String getUnqualifiedNameForInnerClass(Type type) {
		Validate.notNull(type)
		return isInnerClass(type.toString(Printer.NO_COMMENTS)) ? "${outerClassName}.$type" : "$type"
	}

}
