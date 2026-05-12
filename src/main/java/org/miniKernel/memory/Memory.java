package org.miniKernel.memory;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private final Map<String, Integer> data;

    public Memory(Map<String, Integer> initial) {
        this.data = new HashMap<>(initial);
    }

    public int load(String var) {
        if (!data.containsKey(var)) {
            throw new RuntimeException("Variável não definida: " + var);
        }
        return data.get(var);
    }

    public void store(String var, int value) {
        data.put(var, value);
    }
}
