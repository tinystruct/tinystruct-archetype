package ${package};

import org.tinystruct.AbstractApplication;
import org.tinystruct.system.annotation.Action;

@Action(value = "", description = "Sample tinystruct application", mode = Action.Mode.CLI)
public class Application extends AbstractApplication {
    @Override
    public void init() {
        this.setTemplateRequired(false);
    }

    @Action(value = "hello", description = "Say hello")
    public String sayHello() {
        return "Hello from generated tinystruct app!";
    }

    @Action(value = "hello", description = "Say hello")
    public String sayHello(String words) {
        return words;
    }

    @Action(value = "hello", mode = Mode.HTTP_GET)
    public String helloGet() {
        return "GET";
    }

    @Action(value = "hello", mode = Mode.HTTP_POST)
    public String helloPost() {
        return "POST";
    }

    @Action(value = "--help", description = "Print help information")
    @Override
    public String help() {
        return super.help();
    }

    @Override
    public String version() {
        return "";
    }
}
