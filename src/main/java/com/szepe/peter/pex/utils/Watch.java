package com.szepe.peter.pex.utils;

import com.szepe.peter.pex.api.Try;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class Watch {

    private final AtomicLong sum = new AtomicLong();
    private final AtomicInteger counter = new AtomicInteger();


    public <T, R, E extends Exception> Try<T, R, E> start(Try.CheckedFunction<T, R, E> function, T t) {
        counter.incrementAndGet();
        long startTime = System.nanoTime();
        Try<T, R, E> result = Try.tryIt(function).apply(t);
        long elapsedTime = System.nanoTime() - startTime;
        sum.addAndGet(elapsedTime);
        return result;
    }

    public String prettyPrint() {
        return "Caled " + counter.get() + " times\n" +
                "Spent " + sum.get() / 1_000_000_000 + " s\n";
    }
}
