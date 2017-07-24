package io.gitlab.arturbosch.jpal.resolution.nested

import com.github.javaparser.ast.body.AnnotationDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
class InnerClassesNameCollector extends VoidVisitorAdapter {

	private String mainName
	private Set<String> names = new HashSet<>()

	InnerClassesNameCollector(String mainName) {
		this.mainName = mainName
	}

	Set<String> start(TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
			visit(typeDeclaration as ClassOrInterfaceDeclaration, null)
		} else if (typeDeclaration instanceof AnnotationDeclaration) {
			visit(typeDeclaration as AnnotationDeclaration, null)
		} else if (typeDeclaration instanceof EnumDeclaration) {
			visit(typeDeclaration as EnumDeclaration, null)
		}
		return names
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		addIfInnerClass(n)
		super.visit(n, arg)
	}

	private void addIfInnerClass(TypeDeclaration n) {
		def className = n.nameAsString
		if (className != mainName) {
			names.add(className)
		}
	}

	@Override
	void visit(EnumDeclaration n, Object arg) {
		addIfInnerClass(n)
		super.visit(n, arg)
	}

	@Override
	void visit(AnnotationDeclaration n, Object arg) {
		addIfInnerClass(n)
		super.visit(n, arg)
	}
}