package ${package};

import org.tinystruct.AbstractApplication;
import org.tinystruct.ApplicationException;
import org.tinystruct.system.annotation.Action;
import org.tinystruct.system.annotation.Action.Mode;

public class Application extends AbstractApplication {

    @Override
    public void init() {
        // Initialization logic (e.g., setting up resources)
        // Note: Do NOT register actions here — use the @Action annotation instead.
        this.setTemplateRequired(false); // Skip .view template lookup if returning data directly
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    // Handles: bin/dispatcher hello  AND  GET /?q=hello
    @Action(value = "hello", description = "Say hello to tinystruct")
    public String sayHello() {
        return "Hello, tinystruct!";
    }

    // Path parameter: GET /?q=greet/James  OR  bin/dispatcher greet/James
    @Action(value = "greet", description = "Greet a user by name")
    public String greet(String name) {
        return "Hello, " + name + "!";
    }

    // HTTP-only POST handler
    @Action(value = "submit", mode = Mode.HTTP_POST, description = "Handle form submission")
    public String submit() throws ApplicationException {
        // Logic for handling submission
        return "Submitted successfully";
    }

    // Built-in help command
    @Action(value = "--help", description = "Print help information", mode = Mode.CLI)
    @Override
    public String help() {
        return super.help();
    }
}
