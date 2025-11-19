# tinystruct Maven archetype

This archetype creates a minimal tinystruct application.

## Usage (after installing the archetype locally)

1. Install the archetype into your local Maven repository:

```cmd
mvn clean install
```

2. Generate a new project from the archetype (example):

```cmd
mvn archetype:generate \
	-DarchetypeCatalog=local \
	-DarchetypeGroupId=org.tinystruct \
	-DarchetypeArtifactId=tinystruct-archetype \
	-DarchetypeVersion=1.0.0 \
	-DgroupId=com.mycompany \
	-DartifactId=my-tiny-app \
	-Dpackage=com.mycompany.app \
	-DtinystructVersion=1.7.11
```

3. Build the generated project:

```cmd
cd my-tiny-app
mvn clean package
```

Adjust `tinystructVersion` if needed. The generated project includes a simple `Application` that extends `AbstractApplication` with a sample `hello` action.

## Notes

- **Java version:** this archetype targets Java 17 by default (see `archetype-resources/pom.xml`). Ensure you have JDK 17+ installed.
- **CI:** a sample GitHub Actions workflow is included to build and test the generated projects on push/PR (`.github/workflows/maven.yml`).
- **Maven Wrapper:** consider adding the Maven Wrapper (`mvnw`) to this repository so contributors can build without installing Maven globally.

## Quick tips

- To run tests in a generated project use:

```cmd
mvn test
```

- If you plan to publish the archetype, set `tinystructVersion` to a released version and test generation in a clean directory.
