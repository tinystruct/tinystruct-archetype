// archetype-post-generate.groovy
// This script runs after the archetype generates a project. It sets executable bits on
// common scripts that require it (Unix systems): `mvnw` and `bin/dispatcher`.

def baseDir = new File(request.outputDirectory)

def setExec = { path ->
    def f = new File(baseDir, path)
    if (f.exists()) {
        try {
            f.setExecutable(true, false)
            println "Set executable: ${f.absolutePath}"
        } catch (Exception e) {
            println "Warning: could not set executable on ${f.absolutePath}: ${e.message}"
        }
    }
}

setExec('bin/dispatcher')

import java.nio.file.Path
import java.nio.file.Paths

// Get the generated project directory
Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
File projectDir = projectPath.toFile()

println ""
println "=" * 60
println "Compiling generated project..."
println "=" * 60

try {
    // Execute Maven compile
    def command = System.getProperty('os.name').toLowerCase().contains('windows')
            ? ['cmd', '/c', 'mvn', 'clean', 'compile']
            : ['mvn', 'clean', 'compile']

    def process = command.execute(null, projectDir)
    process.consumeProcessOutput(System.out, System.err)
    process.waitFor()

    if (process.exitValue() == 0) {
        println ""
        println "✓ Project compiled successfully!"
        println ""
    } else {
        println ""
        println "⚠ Compilation completed with warnings/errors"
        println "  You may need to run 'mvn clean compile' manually"
        println ""
    }
} catch (Exception e) {
    println ""
    println "⚠ Could not auto-compile: ${e.message}"
    println "  Please run 'mvn clean compile' manually"
    println ""
}

println "Project location: ${projectPath}"
println "=" * 60
