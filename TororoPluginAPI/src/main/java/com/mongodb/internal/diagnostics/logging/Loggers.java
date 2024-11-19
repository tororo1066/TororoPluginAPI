package com.mongodb.internal.diagnostics.logging;

@SuppressWarnings("unused")
public class Loggers {
    public static Logger getLogger(String suffix) {
        return new NoOpLogger(suffix);
    }
}
