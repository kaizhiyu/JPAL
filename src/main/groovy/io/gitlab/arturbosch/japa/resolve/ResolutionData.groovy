package io.gitlab.arturbosch.japa.resolve

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.PackageDeclaration
import io.gitlab.arturbosch.japa.util.Validate

/**
 * Holds information of a compilation unit. Used by a resolver to access qualified types.
 *
 * @author artur
 */
class ResolutionData {

	final String packageName
	final Map<String, String> imports

	static ResolutionData of(CompilationUnit unit) {
		Validate.notNull(unit)
		return new ResolutionData(unit.package, unit.imports)
	}

	ResolutionData(PackageDeclaration packageDeclaration, List<ImportDeclaration> imports) {
		this.packageName = Optional.ofNullable(packageDeclaration)
				.map { it.packageName }.orElse("")
		this.imports = Validate.notNull(imports).collectEntries {
			[Arrays.asList(it.toStringWithoutComments().split("\\.")).last(), it.name.toStringWithoutComments()]
		}
	}

}
