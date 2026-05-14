package org.miniKernel.scheduler;

import org.miniKernel.cpu.CPU;
import org.miniKernel.model.ExecutionResult;
import org.miniKernel.model.Process;
import org.miniKernel.model.ProcessState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class KernelSimulator {

    private static final int MAX_TIME = 500;

    private final CPU cpu = new CPU();
    private final EDFScheduler scheduler = new EDFScheduler();

    private final List<Process> pending;
    private final List<Process> readyQueue;
    private final List<Process> blockedList;

    private final Set<String> reportedMisses = new HashSet<>();

    private int currentTime = 0;

    public KernelSimulator(List<Process> processes) {
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
                System.out.printf("Time %3d | IDLE%n", currentTime);
                currentTime++;
                continue;
            }

            readyQueue.remove(current);
            current.setState(ProcessState.RUNNING);

            ExecutionResult result = cpu.step(current, currentTime);
            current.incrementExecutedTime();

            logExecution(current, result);

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
            System.out.println("\n[Simulacao encerrada: tempo maximo (" + MAX_TIME + ") atingido]");
        }
    }

    private void admitArrivals() {
        Iterator<Process> it = pending.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.getArrivalTime() <= currentTime) {
                p.setState(ProcessState.READY);
                readyQueue.add(p);
                it.remove();
                System.out.printf("Time %3d | ARRIVED  | %-10s | Deadline=%d%n",
                        currentTime, p.getName(), p.getDeadline());
            }
        }
    }

    private void unblockProcesses() {
        blockedList.removeIf(p -> {
            if (p.getBlockedUntil() <= currentTime) {
                p.setState(ProcessState.READY);
                readyQueue.add(p);
                System.out.printf("Time %3d | UNBLOCKED | %-10s%n", currentTime, p.getName());
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
                System.out.printf("Time %3d | *** DEADLINE MISS *** | %s%n", currentTime, p.getName());
                reportedMisses.add(missKey);
            }
        }
    }

    private void logExecution(Process p, ExecutionResult result) {
        String status = switch (result) {
            case CONTINUE  -> "RUNNING ";
            case BLOCKED   -> "BLOCKED ";
            case FINISHED  -> "FINISHED";
        };
        System.out.printf("Time %3d | %s | %-10s | PC=%d | ACC=%d | Deadline=%d%n",
                currentTime, status, p.getName(), p.getPc(), p.getAcc(), p.getDeadline());
    }
}
