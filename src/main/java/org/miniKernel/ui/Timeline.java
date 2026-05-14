package org.miniKernel.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Timeline {

    private final List<String> ticks = new ArrayList<>();
    private final Set<String> processNames = new LinkedHashSet<>();
    private final Map<String, Integer> executionCount = new HashMap<>();
    private int idleTicks = 0;

    public void record(int time, String processName) {
        while (ticks.size() <= time) {
            ticks.add("IDLE");
        }
        ticks.set(time, processName);

        if ("IDLE".equals(processName)) {
            idleTicks++;
        } else {
            processNames.add(processName);
            executionCount.merge(processName, 1, Integer::sum);
        }
    }

    public List<String> getTicks() {
        return ticks;
    }

    public Set<String> getProcessNames() {
        return processNames;
    }

    public int getIdleTicks() {
        return idleTicks;
    }

    public int getExecutionCount(String name) {
        return executionCount.getOrDefault(name, 0);
    }

    public int getTotalTime() {
        return ticks.size();
    }
}
