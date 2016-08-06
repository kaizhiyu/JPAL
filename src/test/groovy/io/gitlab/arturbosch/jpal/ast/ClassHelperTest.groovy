package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author artur
 */
class ClassHelperTest extends Specification {

	def "class is empty and has no methods"() {
		expect:
		ClassHelper.hasNoMethods(aClass)
		ClassHelper.isEmptyBody(aClass)
		where:
		aClass = Helper.firstClass(Helper.compile(Helper.EMPTY_DUMMY))
	}

	def "method is within class scope"() {
		expect:
		ClassHelper.inClassScope(aMethod, aClass.name)
		where:
		unit = Helper.compile(Helper.CYCLE_DUMMY)
		aClass = Helper.firstClass(unit)
		aMethod = Helper.nth(unit, 0)
	}

	def "default vs full signature"() {
		given: "cycle class with two inner classes"
		def outerClass = Helper.firstClass(Helper.compile(Helper.CYCLE_DUMMY))
		def innerClass = ASTHelper.getNodesByType(outerClass, ClassOrInterfaceDeclaration.class)[0]
		when: "calling signature methods"
		def signature = ClassHelper.createSignature(innerClass)
		def fullSignature = ClassHelper.createFullSignature(innerClass)
		then: "full signature embraces the inner class behaviour"
		signature == "InnerCycleOne"
		fullSignature == "CycleDummy\$InnerCycleOne"
	}
}
