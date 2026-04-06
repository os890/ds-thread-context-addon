# CDI Thread-Scoped Add-on (@ThreadScoped)

`@ThreadScoped` is a custom CDI normal scope backed by `ThreadLocal` storage.
Beans annotated with `@ThreadScoped` are created once per thread entry-point and automatically
destroyed when the outermost entry-point method exits via the built-in interceptor.

## Supported Use-Cases

### Automatic context cleanup

```java
@ThreadScoped
public class MyThreadBean {
    // one instance per thread entry-point
}
```

Any `@ThreadScoped` bean becomes an entry-point automatically: when the outermost
`@ThreadScoped` method returns (or throws), all beans in the current thread context are destroyed.

### Manual control

For scenarios where multiple listeners are called outside a single entry-point callstack,
inject `ManualThreadContextManager`:

```java
@Inject
private ManualThreadContextManager manualThreadContextManager;

// simulate entry-point start
manualThreadContextManager.enter();

// ... call @ThreadScoped beans ...

// end and clean up
manualThreadContextManager.leave(); // or stop() to force-close
```

## Requirements

- Java 25+
- Maven 3.6.3+
- CDI 4.1 (Jakarta EE 11)
- Apache DeltaSpike 2.x

## Building

```bash
mvn clean verify
```

## Quality Plugins

The build enforces the following quality gates:

- **Compiler**: `-Xlint:all`, fail on warnings
- **Enforcer**: Java 25+, Maven 3.6.3+, dependency convergence, no javax.* dependencies
- **Checkstyle**: no star imports, brace enforcement, whitespace rules
- **Apache RAT**: Apache 2.0 license header verification
- **JaCoCo**: code coverage reporting
- **Surefire**: JUnit Jupiter test execution

## Testing

Tests use the [Dynamic CDI Test Bean Addon](https://github.com/os890/dynamic-cdi-test-bean-addon)
with `@EnableTestBeans` for CDI SE container management and Apache OpenWebBeans as the CDI implementation.

## Compatibility

This add-on was tested with Apache OpenWebBeans as well as with TomEE.
This add-on isn't compatible with Weld, because proxies are handled differently with Weld.

## License

[Apache License, Version 2.0](LICENSE)
