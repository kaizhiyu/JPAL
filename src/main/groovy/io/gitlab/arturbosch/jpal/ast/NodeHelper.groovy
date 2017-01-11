package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import io.gitlab.arturbosch.jpal.internal.Validate

import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * Provides static methods to search for specific nodes.
 *
 * @author artur
 */
final class NodeHelper {

	static Predicate<Node> unitPredicate = { it instanceof CompilationUnit }
	static Predicate<Node> classPredicate = { it instanceof ClassOrInterfaceDeclaration }
	static Predicate<Node> methodPredicate = { it instanceof MethodDeclaration }

	private NodeHelper() {}

	/**
	 * Returns a list of private method declarations found within the given node.
	 * @param n node, most often ClassOrInterfaceDeclaration or CompilationUnit
	 * @return list of private method declarations
	 */
	static List<MethodDeclaration> findPrivateMethods(Node n) {
		return findMethods(Validate.notNull(n)).stream()
				.filter { it.modifiers.contains(Modifier.PRIVATE) }
				.collect(Collectors.toList())
	}

	/**
	 * Returns a list of method declarations found within the given node.
	 * @param n node, most often ClassOrInterfaceDeclaration or CompilationUnit
	 * @return list of method declarations
	 */
	static List<MethodDeclaration> findMethods(Node n) {
		return Validate.notNull(n).getNodesByType(MethodDeclaration.class)
	}

	/**
	 * Returns a list of private field declarations found within the given node.
	 * @param n node, most often ClassOrInterfaceDeclaration or CompilationUnit
	 * @return list of private field declarations
	 */
	static List<FieldDeclaration> findPrivateFields(Node n) {
		return findFields(Validate.notNull(n)).stream()
				.filter { it.modifiers.contains(Modifier.PRIVATE) }
				.collect(Collectors.toList())
	}

	/**
	 * Returns a list of field declarations found within the given node.
	 * @param n node, most often ClassOrInterfaceDeclaration or CompilationUnit
	 * @return list of field declarations
	 */
	static List<FieldDeclaration> findFields(Node n) {
		return Validate.notNull(n).getNodesByType(FieldDeclaration.class)
	}

	/**
	 * Returns a set of names of inner classes which are found within the given node.
	 * @param n node to search for inner classes
	 * @return set of strings
	 */
	static Set<String> findNamesOfInnerClasses(Node n) {
		return Validate.notNull(n).getNodesByType(ClassOrInterfaceDeclaration.class).stream()
				.filter { it.parentNode instanceof Optional<ClassOrInterfaceDeclaration> }
				.map { it.nameAsString }
				.collect(Collectors.toSet())
	}

	/**
	 * Searches for the class given node is declared in.
	 * @param node given node
	 * @return maybe a class declaration
	 */
	static Optional<ClassOrInterfaceDeclaration> findDeclaringClass(Node node) {
		return findDeclaring(node, classPredicate)
	}

	/**
	 * Searches for the compilation uni given node is declared in.
	 * @param node given node
	 * @return maybe a compilation unit
	 */
	static Optional<CompilationUnit> findDeclaringCompilationUnit(Node node) {
		return findDeclaring(node, unitPredicate)
	}

	/**
	 * Searches for the method given node is declared in.
	 * @param node given node
	 * @return maybe a method declaration
	 */
	static Optional<MethodDeclaration> findDeclaringMethod(Node node) {
		return findDeclaring(node, methodPredicate)
	}

	private static Optional findDeclaring(Node node, Predicate<Node> predicate) {
		Optional<Node> parent = Validate.notNull(node).getParentNode()
		while (parent.isPresent()) {
			if (predicate.test(parent.get())) return parent
			parent = parent.get().getParentNode()
		}
		return Optional.empty()
	}

}
