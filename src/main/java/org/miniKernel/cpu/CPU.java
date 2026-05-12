package org.miniKernel.cpu;

import org.miniKernel.model.ExecutionResult;
import org.miniKernel.model.Process;

public class CPU {
    private final ExecutionEngine engine = new ExecutionEngine();

    public ExecutionResult step(Process process, int currentTick) {
        return engine.execute(process, currentTick);
    }
}
