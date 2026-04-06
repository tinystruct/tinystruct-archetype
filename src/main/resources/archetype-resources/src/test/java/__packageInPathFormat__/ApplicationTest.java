package ${package};

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinystruct.system.Settings;

public class ApplicationTest {

    private Application app;

    @BeforeEach
    public void setUp() {
        app = new Application();
        // Setting configuration triggers init() and annotation processing
        app.setConfiguration(new Settings());
    }

    @Test
    public void testSayHello() throws Exception {
        Object result = app.invoke("hello");
        Assertions.assertEquals("Hello, tinystruct!", result);
    }

    @Test
    public void testGreet() throws Exception {
        Object result = app.invoke("greet", new Object[]{"James"});
        Assertions.assertEquals("Hello, James!", result);
    }
}
