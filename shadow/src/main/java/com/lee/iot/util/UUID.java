package com.lee.iot.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

public class UUID {

    private static final RandomBasedGenerator V4 = Generators.randomBasedGenerator();
    private static final TimeBasedEpochRandomGenerator V7 = Generators.timeBasedEpochRandomGenerator();


    public static java.util.UUID randomTimeBaseUUID() {
        return V7.generate();
    }

    public static java.util.UUID randomUUID() {
        return V4.generate();
    }

}
