package com.plugin.component;

import java.util.HashMap;
import java.util.Map;

public class CostCache {

    public static Map<String, Long> sStartTime = new HashMap<>();
    public static Map<String, Long> sEndTime = new HashMap<>();

    public static void start(String methodName, long startTime) {
        sStartTime.put(methodName, startTime);
    }

    public static void end(String methodName, long endTime) {
        sEndTime.put(methodName, endTime);
    }

    public static String cost(String methodName) {
        long start = sStartTime.get(methodName);
        long end = sEndTime.get(methodName);
        return "method: " + methodName + " cost " + (end - start) + " ns";
    }
}