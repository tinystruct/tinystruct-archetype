package ${package};

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    @Test
    public void testSayHello() {
        Application app = new Application();
        assertEquals("Hello from generated tinystruct app!", app.sayHello());
    }
}
