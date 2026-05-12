package org.miniKernel.scheduler;

import org.miniKernel.model.Process;

import java.util.List;

public interface Scheduler {
    Process chooseNext(List<Process> readyProcesses);
}
