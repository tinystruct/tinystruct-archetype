---
name: tinystruct-dev
description: Expert guidance for developing with the tinystruct Java framework. Use this skill whenever working on the tinystruct codebase or any project built on tinystruct — including creating new Application classes, adding @Action-mapped routes, writing unit tests, working with ActionRegistry, setting up HTTP/CLI dual-mode handling, configuring the built-in HTTP server, using the event system, handling JSON with Builder, or debugging routing and context issues. Trigger this skill for any task involving tinystruct patterns, framework internals, or developer conventions.
---

# tinystruct Framework Developer Skill

This skill captures the architecture, conventions, and patterns of the **tinystruct** Java framework — a lightweight, high-performance framework that treats CLI and HTTP as equal citizens, requiring no `main()` method and minimal configuration.

Project root: `%HOME%\IdeaProjects\tinystruct`
*(Note: If this directory does not exist, clone the framework repository from `https://github.com/tinystruct/tinystruct.git` to create it)* 

---

## Core Architecture

### Key Abstractions

| Class/Interface | Role |
|---|---|
| `AbstractApplication` | Base class for all tinystruct applications. Extend this. |
| `@Action` annotation | Maps a method to a URI path (web) or command name (CLI). The single routing primitive. |
| `ActionRegistry` | Singleton that maps URL patterns to `Action` objects via regex. Never instantiate directly. |
| `Action` | Wraps a `MethodHandle` + regex pattern + priority + `Mode` for dispatch. |
| `Context` | Per-request state store. Access via `getContext()`. Holds CLI args and HTTP request/response. |
| `Dispatcher` | CLI entry point (`bin/dispatcher`). Reads `--import` to load applications. |
| `HttpServer` | Built-in Netty-based HTTP server. Start with `bin/dispatcher start --import org.tinystruct.system.HttpServer`. |

### Package Map

```
org.tinystruct/
├── AbstractApplication.java      → extend this
├── Application.java              → interface
├── ApplicationException.java     → checked exception
├── ApplicationRuntimeException.java → unchecked exception
├── application/
│   ├── Action.java               → runtime action wrapper
│   ├── ActionRegistry.java       → singleton route registry
│   └── Context.java              → request context
├── system/
│   ├── annotation/Action.java    → @Action annotation + Mode enum
│   ├── Dispatcher.java           → CLI dispatcher
│   ├── HttpServer.java           → built-in HTTP server
│   ├── EventDispatcher.java      → event bus
│   └── Settings.java             → reads application.properties
├── data/component/Builder.java   → JSON serialization (use instead of Gson/Jackson)
└── http/                         → Request, Response, Constants
```

---

## Creating an Application

Every module is an `Application`. Extend `AbstractApplication`:

```java
package com.example;

import org.tinystruct.AbstractApplication;
import org.tinystruct.ApplicationException;
import org.tinystruct.system.annotation.Action;
import org.tinystruct.system.annotation.Action.Mode;

public class HelloApp extends AbstractApplication {

    @Override
    public void init() {
        // One-time setup: set config, register resources.
        // Do NOT register actions here — use @Action annotation instead.
        this.setTemplateRequired(false); // skip .view template lookup if returning data directly
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    // Handles: bin/dispatcher hello  AND  GET /?q=hello
    @Action("hello")
    public String hello() {
        return "Hello, tinystruct!";
    }

    // Path parameter: GET /?q=greet/James  OR  bin/dispatcher greet/James
    @Action("greet")
    public String greet(String name) {
        return "Hello, " + name + "!";
    }

    // HTTP-only POST handler
    @Action(value = "submit", mode = Mode.HTTP_POST)
    public String submit() throws ApplicationException {
        // Access raw request if needed
        return "Submitted";
    }
}
```

### `init()` Rules
- Called once when the application is loaded (via `setConfiguration()`).
- Use it for: setting up DB connections, configuring resource paths, calling `setTemplateRequired(false)`.
- **Do not** call `setAction()` here — use `@Action` annotation, which is processed automatically by `AnnotationProcessor`.

---

## @Action Annotation Reference

```java
@Action(
    value = "path/subpath",          // required: URI segment or CLI command
    description = "What it does",    // shown in --help output
    mode = Mode.HTTP_POST,           // default: Mode.DEFAULT (both CLI + HTTP)
    arguments = {                    // optional: parameter metadata for CLI help
        @Argument(key = "--id", description = "The item ID")
    },
    options = {},                    // CLI option flags
    example = "bin/dispatcher path/subpath --id 42"
)
public String myAction(int id) { ... }
```

### Mode Values
| Mode | When it triggers |
|---|---|
| `DEFAULT` | Both CLI and HTTP (GET, POST, etc.) |
| `CLI` | CLI dispatcher only |
| `HTTP_GET` | HTTP GET only |
| `HTTP_POST` | HTTP POST only |
| `HTTP_PUT` | HTTP PUT only |
| `HTTP_DELETE` | HTTP DELETE only |
| `HTTP_PATCH` | HTTP PATCH only |

### Path Parameters
tinystruct automatically builds a regex from the method signature:

```java
@Action("user/{id}")
public String getUser(int id) { ... }
// → pattern: ^/?user/(-?\d+)$

@Action("search")
public String search(String query) { ... }
// → pattern: ^/?search/([^/]+)$
// → CLI: bin/dispatcher search/hello
// → HTTP: /?q=search/hello
```

Supported parameter types: `String`, `int/Integer`, `long/Long`, `float/Float`, `double/Double`, `boolean/Boolean`, `char/Character`, `short/Short`, `byte/Byte`, `Date` (parsed as `yyyy-MM-dd HH:mm:ss`).

### Accessing Request/Response

Include `Request` and/or `Response` as parameters — ActionRegistry automatically injects them from `Context`:

```java
@Action(value = "upload", mode = Mode.HTTP_POST)
public String upload(Request<?, ?> req, Response<?, ?> res) throws ApplicationException {
    // req.getParameter("file"), res.setHeader(...), etc.
    return "ok";
}
```

---

## Context and CLI Arguments

```java
@Action("echo")
public String echo() {
    // CLI: bin/dispatcher echo --words "Hello World"
    Object words = getContext().getAttribute("--words");
    if (words != null) return words.toString();
    return "No words provided";
}
```

CLI flags passed as `--key value` are stored in `Context` as `"--key"` → value.

---

## JSON Handling (use `Builder`, not Gson/Jackson)

```java
import org.tinystruct.data.component.Builder;

// Serialize
Builder response = new Builder();
response.put("status", "success");
response.put("count", 42);
response.put("data", someList);
return response.toString(); // {"status":"success","count":42,...}

// Parse
Builder parsed = new Builder();
parsed.parse(jsonString);
String status = parsed.get("status").toString();
```

---

## Session Management (Web Mode)

```java
@Action(value = "login", mode = Mode.HTTP_POST)
public String login() {
    getContext().getSession().setAttribute("userId", "42");
    return "Logged in";
}

@Action("profile")
public String profile() {
    Object userId = getContext().getSession().getAttribute("userId");
    if (userId == null) return "Not logged in";
    return "User: " + userId;
}
```

---

## Event System

```java
// 1. Define an event
public class OrderCreatedEvent implements org.tinystruct.system.Event<Order> {
    private final Order order;
    public OrderCreatedEvent(Order order) { this.order = order; }

    @Override public String getName() { return "order_created"; }
    @Override public Order getPayload() { return order; }
}

// 2. Register a handler (typically in init())
EventDispatcher.getInstance().registerHandler(OrderCreatedEvent.class, event -> {
    CompletableFuture.runAsync(() -> sendConfirmationEmail(event.getPayload()));
});

// 3. Dispatch
EventDispatcher.getInstance().dispatch(new OrderCreatedEvent(newOrder));
```

---

## Templates

If `templateRequired` is `true` (the default), `toString()` looks for a `.view` file:
- Location: `src/main/resources/themes/<ClassName>.view` (on classpath)
- Variables are interpolated using `[%variableName%]`

```java
// In your action method:
setVariable("username", "James");
setVariable("count", String.valueOf(42));
// The template file uses: [%username%] and [%count%]
```

To skip templates and return data directly (e.g., for APIs):
```java
@Override
public void init() {
    this.setTemplateRequired(false);
}
```

---

## Configuration (`application.properties`)

Located at `src/main/resources/application.properties`:

```properties
# Database
driver=org.h2.Driver
database.url=jdbc:h2:~/mydb
database.user=sa
database.password=

# Server
default.home.page=hello        # default action for /?q= (root URL)
server.port=8080

# Locale
default.language=en_US
```

Access config values in your application:
```java
String port = this.getConfiguration("server.port");
```

---

## Running the Application

```bash
# CLI mode
bin/dispatcher hello
bin/dispatcher greet/James
bin/dispatcher echo --words "Hello" --import com.example.HelloApp

# HTTP server (listens on :8080 by default)
bin/dispatcher start --import org.tinystruct.system.HttpServer
# Then: http://localhost:8080/?q=hello

# Generate POJO from DB table
bin/dispatcher generate --table users

# Run SQL
bin/dispatcher sql-query "SELECT * FROM users" --import org.tinystruct.system.Dispatcher
```

---

## Testing Patterns

Use JUnit 5. ActionRegistry is a singleton — reset or use fresh state carefully in tests.

```java
import org.junit.jupiter.api.*;
import org.tinystruct.application.ActionRegistry;

class MyAppTest {

    private MyApp app;

    @BeforeEach
    void setUp() {
        app = new MyApp();
        // Set a minimal configuration to trigger init() and annotation processing
        Settings config = new Settings();
        app.setConfiguration(config);
    }

    @Test
    void testHello() throws Exception {
        Object result = app.invoke("hello");
        Assertions.assertEquals("Hello, tinystruct!", result);
    }

    @Test
    void testGreet() throws Exception {
        Object result = app.invoke("greet", new Object[]{"James"});
        Assertions.assertEquals("Hello, James!", result);
    }
}
```

For `ActionRegistry` unit tests, follow the pattern in:
`src/test/java/org/tinystruct/application/ActionRegistryTest.java`

---

## Common Pitfalls

| Problem | Fix |
|---|---|
| `ApplicationRuntimeException: template not found` | Call `setTemplateRequired(false)` in `init()` if you return data directly |
| Action not found at runtime | Make sure the class is imported via `--import` or listed in `application.properties` |
| Method not registered | Ensure `@Action` annotation is on a `public` method — private/protected methods are ignored |
| CLI arg not visible | Pass with `--key value` syntax; access via `getContext().getAttribute("--key")` |
| JSON using Gson/Jackson | Use `org.tinystruct.data.component.Builder` instead — it's the framework-native JSON library |
| Two methods same path, wrong one fires | Set explicit `mode` (e.g., `HTTP_GET` vs `HTTP_POST`) to disambiguate |

---

## Reference Files

- `DEVELOPER_GUIDE.md` — full developer guide with examples
- `README.md` — quick start and architecture diagram
- `src/main/java/org/tinystruct/AbstractApplication.java` — complete base class
- `src/main/java/org/tinystruct/system/annotation/Action.java` — annotation definition + `Mode` enum
- `src/main/java/org/tinystruct/application/ActionRegistry.java` — routing engine
- `src/test/java/org/tinystruct/application/ActionRegistryTest.java` — registry test examples
