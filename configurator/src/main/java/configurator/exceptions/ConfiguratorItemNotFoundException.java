package configurator.exceptions;

public class ConfiguratorItemNotFoundException extends Exception {
    public ConfiguratorItemNotFoundException() {
        super();
    }
    public ConfiguratorItemNotFoundException(String s) {
        super(s);
    }
    public ConfiguratorItemNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public ConfiguratorItemNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
