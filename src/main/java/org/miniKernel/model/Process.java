package org.miniKernel.model;

import org.miniKernel.memory.Memory;

public class Process {
    private final String name;
    private int pc;
    private int acc;
    private final int arrivalTime;
    private final int computationTime;
    private final int period;
    private final int deadline;
    private ProcessState state;
    private final Program program;
    private final Memory memory;
    private int blockedUntil;

    public Process(String name, Program program,
                   int arrivalTime, int computationTime,
                   int period, int deadline) {
        this.name = name;
        this.program = program;
        this.arrivalTime = arrivalTime;
        this.computationTime = computationTime;
        this.period = period;
        this.deadline = deadline;
        this.pc = 0;
        this.acc = 0;
        this.state = ProcessState.READY;
        this.memory = new Memory(program.getData());
        this.blockedUntil = 0;
    }

    public String getName() { return name; }
    public int getPc() { return pc; }
    public void setPc(int pc) { this.pc = pc; }
    public int getAcc() { return acc; }
    public void setAcc(int acc) { this.acc = acc; }
    public int getArrivalTime() { return arrivalTime; }
    public int getComputationTime() { return computationTime; }
    public int getPeriod() { return period; }
    public int getDeadline() { return deadline; }
    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }
    public Program getProgram() { return program; }
    public Memory getMemory() { return memory; }
    public int getBlockedUntil() { return blockedUntil; }
    public void setBlockedUntil(int blockedUntil) { this.blockedUntil = blockedUntil; }

    @Override
    public String toString() {
        return "Process{name='" + name + "', state=" + state + ", pc=" + pc + ", acc=" + acc + "}";
    }
}
