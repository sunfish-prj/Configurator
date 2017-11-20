package configurator.exceptions;

public class ConfiguratorStartupException extends RuntimeException {
    public ConfiguratorStartupException() {
        super();
    }
    public ConfiguratorStartupException(String s) {
        super(s);
    }
    public ConfiguratorStartupException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public ConfiguratorStartupException(Throwable throwable) {
        super(throwable);
    }
}