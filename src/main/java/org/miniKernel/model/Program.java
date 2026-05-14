package org.miniKernel.model;
import java.util.*;

public class Program {
    List<Instruction> instructions;
    Map<String, Integer> data;
    Map<String, Integer> labels;

    public Program(List<Instruction> instructions, Map<String, Integer> data, Map<String, Integer> labels) {
        this.instructions = instructions;
        this.data = data;
        this.labels = labels;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public Map<String, Integer> getData() {
        return data;
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
    }

    public Map<String, Integer> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Integer> labels) {
        this.labels = labels;
    }
}
