package org.miniKernel.model;

public class Instruction {
    private String opcode;
    private String operand;
    private boolean immediate;

    public Instruction(String opcode, String operand, boolean immediate) {
        this.opcode = opcode;
        this.operand = operand;
        this.immediate = immediate;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opcode='" + opcode + '\'' +
                ", operand='" + operand + '\'' +
                ", immediate=" + immediate +
                '}';
    }
}
