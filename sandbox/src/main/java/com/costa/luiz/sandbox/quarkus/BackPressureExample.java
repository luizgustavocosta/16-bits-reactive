package com.costa.luiz.sandbox.quarkus;

import java.util.concurrent.TimeUnit;

public class BackPressureExample {

    public static Number canOnlyConsumeOneItemPerSecond(Number number) {
        try {
            // Starting doing a heavy work during 1 second
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage());
        }
        return number;
    }
}
