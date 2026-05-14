package org.miniKernel.scheduler;

import org.miniKernel.cpu.CPU;
import org.miniKernel.model.ExecutionResult;
import org.miniKernel.model.Process;
import org.miniKernel.model.ProcessState;
import org.miniKernel.ui.ConsoleUI;
import org.miniKernel.ui.Timeline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KernelSimulator {

    private static final int MAX_TIME = 500;

    private final CPU cpu = new CPU();
    private final EDFScheduler scheduler = new EDFScheduler();
    private final ConsoleUI ui;

    private final List<Process> pending;
    private final List<Process> readyQueue;
    private final List<Process> blockedList;

    private final Timeline timeline = new Timeline();
    private final Map<String, Integer> missedDeadlines = new HashMap<>();
    private final Set<String> reportedMisses = new HashSet<>();

    private int currentTime = 0;

    public KernelSimulator(List<Process> processes, ConsoleUI ui) {
        this.ui = ui;
        this.pending = new ArrayList<>(processes);
        this.pending.sort(Comparator.comparingInt(Process::getArrivalTime));
        this.readyQueue = new ArrayList<>();
        this.blockedList = new ArrayList<>();
    }

    public void run() {
        while (currentTime < MAX_TIME
                && (!pending.isEmpty() || !readyQueue.isEmpty() || !blockedList.isEmpty())) {

            admitArrivals();
            unblockProcesses();
            checkDeadlineMisses();

            Process current = scheduler.chooseNext(readyQueue);

            if (current == null) {
                ui.logIdle(currentTime);
                timeline.record(currentTime, "IDLE");
                currentTime++;
                continue;
            }

            readyQueue.remove(current);
            current.setState(ProcessState.RUNNING);

            ExecutionResult result = cpu.step(current, currentTime);
            current.incrementExecutedTime();

            ui.logExecution(currentTime, current, result);
            timeline.record(currentTime, current.getName());

            switch (result) {
                case FINISHED -> current.setState(ProcessState.FINISHED);

                case BLOCKED -> blockedList.add(current);

                case CONTINUE -> {
                    if (current.getExecutedTime() >= current.getComputationTime()) {
                        current.resetForNextPeriod();
                        readyQueue.add(current);
                    } else {
                        current.setState(ProcessState.READY);
                        readyQueue.add(current);
                    }
                }
            }

            currentTime++;
        }

        if (currentTime >= MAX_TIME) {
            ui.printMaxTimeReached(MAX_TIME);
        }

        ui.printTimeline(timeline);
        ui.printSummary(timeline, missedDeadlines);
    }

    private void admitArrivals() {
        Iterator<Process> it = pending.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.getArrivalTime() <= currentTime) {
                p.setState(ProcessState.READY);
                readyQueue.add(p);
                it.remove();
                ui.logArrival(currentTime, p);
            }
        }
    }

    private void unblockProcesses() {
        blockedList.removeIf(p -> {
            if (p.getBlockedUntil() <= currentTime) {
                p.setState(ProcessState.READY);
                readyQueue.add(p);
                ui.logUnblock(currentTime, p);
                return true;
            }
            return false;
        });
    }

    private void checkDeadlineMisses() {
        List<Process> allActive = new ArrayList<>(readyQueue);
        allActive.addAll(blockedList);

        for (Process p : allActive) {
            String missKey = p.getName() + "_" + p.getDeadline();
            if (currentTime > p.getDeadline() && !reportedMisses.contains(missKey)) {
                ui.logDeadlineMiss(currentTime, p);
                reportedMisses.add(missKey);
                missedDeadlines.merge(p.getName(), 1, Integer::sum);
            }
        }
    }
}
