package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.resolve.QualifiedType
import spock.lang.Specification

/**
 * @author artur
 */
class CompilationStorageTest extends Specification {

	private QualifiedType cycleType = new QualifiedType("io.gitlab.arturbosch.jpal.dummies.CycleDummy",
			QualifiedType.TypeToken.REFERENCE)

	private QualifiedType innerCycleType = new QualifiedType(cycleType.name + ".InnerCycleOne",
			QualifiedType.TypeToken.REFERENCE)

	def "domain tests"() {
		given:
		def storage = JPAL.new(Helper.BASE_PATH)

		when: "retrieving all compilation info"
		def info = storage.allCompilationInfo

		then: "its size must be greater than 1 as more than 2 dummies are known)"
		info.size() > 1

		when: "retrieving a specific type (cycle)"
		def cycleInfo = storage.getCompilationInfo(cycleType).get()
		def cycleInfoFromPath = storage.getCompilationInfo(Helper.CYCLE_DUMMY).get()

		then: "it should have 2 inner classes"
		cycleInfo.innerClasses.size() == 2
		cycleInfoFromPath.innerClasses.size() == 2

		when: "retrieving info for a inner class"
		def infoFromInnerClass = storage.getCompilationInfo(innerCycleType).get()

		then: "it should return info of outer class"
		infoFromInnerClass.qualifiedType == cycleType

		when: "getting all qualified types which are already stored"
		def types = storage.getAllQualifiedTypes()

		then: "it must be greater or equals the amount of classes in dummies package (now 3)"
		types.size() >= 3
	}

	def "compilation storage with processor test"() {
		given: "compilation storage with processor"
		def storage = JPAL.new(Helper.BASE_PATH, new CompilationInfoProcessor<String>() {
			@Override
			String process(CompilationInfo info) {
				return "nice"
			}
		})

		when: "retrieving all compilation info"
		def instances = storage.allCompilationInfo

		then: "every instance should have the string 'nice'"
		instances.each {
			assert it.getProcessedObject(String.class) == "nice"
		}
	}

}
