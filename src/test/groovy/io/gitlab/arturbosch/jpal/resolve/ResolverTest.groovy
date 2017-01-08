package io.gitlab.arturbosch.jpal.resolve

import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.UnknownType
import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import spock.lang.Specification

/**
 * @author artur
 */
class ResolverTest extends Specification {

	CompilationStorage storage
	Resolver resolver

	def setup() {
		storage = JPAL.new(Helper.BASE_PATH)
		resolver = new Resolver(storage)
	}

	def "get qualified type from imports"() {
		given: "resolution data for cycle dummy with asterisk imports and initialized compilation storage"
		def data = ResolutionData.of(Helper.compile(Helper.CYCLE_DUMMY))
		when: "retrieving qualified type for two types"
		def helper = resolver.getQualifiedType(data, new ClassOrInterfaceType("Helper"))
		def testReference = resolver.getQualifiedType(data, new ClassOrInterfaceType("TestReference"))
		def innerClasses = resolver.getQualifiedType(data,
				new ClassOrInterfaceType("InnerClassesDummy.InnerClass.InnerInnerClass"))
		then: "Helper type is retrieved from qualified import and TestReference from asterisk"
		helper.name == "io.gitlab.arturbosch.jpal.Helper"
		testReference.name == "io.gitlab.arturbosch.jpal.dummies.test.TestReference"
		innerClasses.name == "io.gitlab.arturbosch.jpal.dummies.test.InnerClassesDummy.InnerClass.InnerInnerClass"
	}

	def "domain tests"() {
		expect: "the right qualified types"
		resolver.getQualifiedType(data, importType).isReference()
		resolver.getQualifiedType(data, cycleType).isReference()
		resolver.getQualifiedType(data, innerCycleType).isReference()
		resolver.getQualifiedType(data, javaType).isFromJdk()
		resolver.getQualifiedType(data, primitiveType).isPrimitive()
		resolver.getQualifiedType(data, boxedType).isPrimitive()
		resolver.getQualifiedType(data, unknownType).typeToken == QualifiedType.TypeToken.UNKNOWN

		where: "resolution data from the cycle class and different kinds of class types"
		unit = Helper.compile(Helper.CYCLE_DUMMY)
		data = ResolutionData.of(unit)
		importType = new ClassOrInterfaceType("Helper")
		cycleType = new ClassOrInterfaceType("CycleDummy")
		innerCycleType = new ClassOrInterfaceType("CycleDummy.InnerCycleOne")
		javaType = new ClassOrInterfaceType("ArrayList")
		primitiveType = new PrimitiveType(PrimitiveType.Primitive.BOOLEAN)
		boxedType = primitiveType.toBoxedType()
		unknownType = new UnknownType()
	}

}
