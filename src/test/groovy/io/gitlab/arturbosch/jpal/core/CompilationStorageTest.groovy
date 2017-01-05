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
		CompilationStorage.create(Helper.BASE_PATH)
		assert CompilationStorage.isInitialized()

		when: "retrieving all compilation info"
		def info = CompilationStorage.allCompilationInfo

		then: "its size must be greater than 1 as more than 2 dummies are known)"
		info.size() > 1

		when: "retrieving a specific type (cycle)"
		def cycleInfo = CompilationStorage.getCompilationInfo(cycleType).get()
		def cycleInfoFromPath = CompilationStorage.getCompilationInfo(Helper.CYCLE_DUMMY).get()

		then: "it should have 2 inner classes"
		cycleInfo.innerClasses.size() == 2
		cycleInfoFromPath.innerClasses.size() == 2

		when: "retrieving info for a inner class"
		def infoFromInnerClass = CompilationStorage.getCompilationInfo(innerCycleType).get()

		then: "it should return info of outer class"
		infoFromInnerClass.qualifiedType == cycleType

		when: "getting all qualified types which are already stored"
		def types = CompilationStorage.getAllQualifiedTypes()

		then: "it must be greater or equals the amount of classes in dummies package (now 3)"
		types.size() >= 3

		when: "adding a new path to the compilation storage"
		def pathToAdd = Helper.BASE_PATH.resolve("test/TestReference.java")
		def updatedCU = CompilationStorage.updateCompilationInfoWithSamePaths([pathToAdd])[0]

		then: "a new compilation info is added"
		updatedCU.qualifiedType.shortName == "TestReference"

		when: "a file is relocated"
		def pathToRelocate = Helper.BASE_PATH.resolve("test/InnerClassesDummy.java")
		def relocatedCU = CompilationStorage.updateRelocatedCompilationInfo(pathToAdd, pathToRelocate).get()
		def removedCU = CompilationStorage.getCompilationInfo(pathToAdd)

		then: "old path is absent and new present"
		relocatedCU.qualifiedType.shortName == "InnerClassesDummy"
		!removedCU.isPresent()
	}

	def "compilation storage with processor test"() {
		given: "compilation storage with processor"
		CompilationStorage.createWithProcessor(Helper.BASE_PATH, new CompilationInfoProcessor<String>(){
			@Override
			String process(CompilationInfo info) {
				return "nice"
			}
		})
		assert CompilationStorage.isInitialized()

		when: "retrieving all compilation info"
		def instances = CompilationStorage.allCompilationInfo

		then: "every instance should have the string 'nice'"
		instances.each {
			assert it.getProcessedObject(String.class) == "nice"
		}
	}

}
