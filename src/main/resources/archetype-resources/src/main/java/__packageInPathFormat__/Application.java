package ${package};

import org.tinystruct.AbstractApplication;
import org.tinystruct.system.annotation.Action;
import org.tinystruct.system.annotation.Argument;

@Action(value = "", description = "Sample tinystruct application", mode = org.tinystruct.system.annotation.Action.Mode.CLI)
public class Application extends AbstractApplication {

    @Action(value = "hello", description = "Say hello")
    public String sayHello() {
        return "Hello from generated tinystruct app!";
    }

    @Action(value = "--help", description = "Print help information")
    @Override
    public String help() {
        return super.help();
    }
}
