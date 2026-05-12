package org.miniKernel.scheduler;

import org.miniKernel.model.Process;

import java.util.Comparator;
import java.util.List;

public class EDFScheduler implements Scheduler {

    @Override
    public Process chooseNext(List<Process> readyProcesses) {
        return readyProcesses.stream()
                .min(Comparator.comparingInt(Process::getDeadline))
                .orElse(null);
    }
}
