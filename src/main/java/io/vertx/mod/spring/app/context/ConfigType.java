package io.vertx.mod.spring.app.context;

/**
 * User: Mark Spritzler
 * Date: 7/9/13
 * Time: 12:29 PM
 */
public enum ConfigType {
    XML("xml"),
    JAVA_CONFIG("class");

    String value;

    ConfigType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
