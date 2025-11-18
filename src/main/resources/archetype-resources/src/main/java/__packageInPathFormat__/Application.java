package ${package};

import org.tinystruct.AbstractApplication;
import org.tinystruct.system.annotation.Action;

@Action(value = "", description = "Sample tinystruct application", mode = Action.Mode.CLI)
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

    @Override
    public void init() {

    }

    @Override
    public String version() {
        return "";
    }
}
