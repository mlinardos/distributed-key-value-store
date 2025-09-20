package io.github.mlinardos.kvstore.generator;

import java.lang.reflect.Type;

public class KeyTypePair {
    private String key;
    private Type type;

    public KeyTypePair(String key, Type type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
