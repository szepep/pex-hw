package com.szepe.peter.pex;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StopWatch {

    Map<String, Long> taskToTime = new LinkedHashMap<>();

    public <T, R> R start(String task, Supplier<R> function) {
        long startTime = System.nanoTime();
        R r = function.get();
        long time = System.nanoTime() - startTime;

        long newTime = taskToTime.getOrDefault(task, 0L) + time;
        taskToTime.put(task, newTime);

        return r;
    }

    public String prettyPrint() {
        long sum = taskToTime.values().stream().mapToLong(i -> i).sum();
        StringBuilder builder = new StringBuilder("Stats:\n");
        for (Map.Entry<String, Long> e : taskToTime.entrySet()) {
            builder.append(e.getValue())
                    .append("\t")
                    .append(100 * e.getValue() / sum).append("%")
                    .append("\t")
                    .append(e.getKey())
                    .append("\n");
        }
        return builder.toString();
    }
}
