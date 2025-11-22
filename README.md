# Kickstart your tinystruct project using the tinystruct-archetype

*A simple way to bootstrap applications with tinystruct 1.7.11*

If you're building an application with the **tinystruct** framework, the easiest way to begin is by using the **tinystruct-archetype**, now available on **Maven Central**.
It gives you a clean, fully prepared project structure with zero manual setup.

## 🌟 Why Use tinystruct-archetype?

### **1. Instant Project Generation**

One command creates a complete tinystruct application with the correct `pom.xml`, directory layout, and starter code.

### **2. Clean, Consistent Structure**

Your project is generated with tinystruct’s recommended conventions:

* `AbstractApplication` as the base
* `@Action` for defining routes
* Ready to run via CLI or as an HTTP service
* No boilerplate or extra configuration needed

### **3. Built for tinystruct 1.7.11**
Select the version you want (e.g., `1.7.11`), and it's applied automatically.

### **4. No Repo Clone Needed**
Because the archetype is published on Maven Central, you can use it immediately.

## 🚀 Generate a tinystruct project

Simply run:

```cmd
mvn archetype:generate -DarchetypeGroupId="org.tinystruct" -DarchetypeArtifactId="tinystruct-archetype" -DarchetypeVersion="1.0.1" -DgroupId="com.mycompany" -DartifactId="my-tiny-app" -Dpackage="com.mycompany.app" -DtinystructVersion="1.7.11" -DinteractiveMode="false"
```
Adjust `tinystructVersion` if needed. The generated project includes a simple `Application` that extends `AbstractApplication` with a sample `hello` action.
This creates your new project instantly.


## ▶️ Run your tinystruct application

Since tinystruct now uses **HttpServer** as the default HTTP server, the run command becomes:

```bash
cd my-tiny-app
bin/dispatcher start \
  --import org.tinystruct.system.HttpServer \
  --import com.mycompany.app.Application
```

Your tinystruct service is now up and running.

## Notes

- **Java version:** this archetype targets Java 17 by default (see `archetype-resources/pom.xml`). Ensure you have JDK 17+ installed.
- **CI:** a sample GitHub Actions workflow is included to build and test the generated projects on push/PR (`.github/workflows/maven.yml`).
- **Maven Wrapper:** consider adding the Maven Wrapper (`mvnw`) to this repository so contributors can build without installing Maven globally.