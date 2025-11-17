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

setExec('mvnw')
setExec('bin/dispatcher')
