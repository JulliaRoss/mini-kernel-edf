package org.miniKernel.cpu;

import org.miniKernel.model.*;
import java.util.Random;

public class ExecutionEngine {
    private final Random random = new Random();

    public ExecutionResult execute(Process process, int currentTick) {
        Instruction instr = process.getProgram().getInstructions().get(process.getPc());
        String opcode = instr.getOpcode();
        String operand = instr.getOperand();
        boolean immediate = instr.isImmediate();

        switch (opcode) {
            case "LOAD" -> {
                process.setAcc(resolveValue(process, operand, immediate));
                process.setPc(process.getPc() + 1);
            }
            case "STORE" -> {
                process.getMemory().store(operand, process.getAcc());
                process.setPc(process.getPc() + 1);
            }
            case "ADD" -> {
                process.setAcc(process.getAcc() + resolveValue(process, operand, immediate));
                process.setPc(process.getPc() + 1);
            }
            case "SUB" -> {
                process.setAcc(process.getAcc() - resolveValue(process, operand, immediate));
                process.setPc(process.getPc() + 1);
            }
            case "MULT" -> {
                process.setAcc(process.getAcc() * resolveValue(process, operand, immediate));
                process.setPc(process.getPc() + 1);
            }
            case "DIV" -> {
                process.setAcc(process.getAcc() / resolveValue(process, operand, immediate));
                process.setPc(process.getPc() + 1);
            }
            case "BRPOS" -> {
                if (process.getAcc() > 0) {
                    process.setPc(process.getProgram().getLabels().get(operand));
                } else {
                    process.setPc(process.getPc() + 1);
                }
            }
            case "BRZERO" -> {
                if (process.getAcc() == 0) {
                    process.setPc(process.getProgram().getLabels().get(operand));
                } else {
                    process.setPc(process.getPc() + 1);
                }
            }
            case "BRNEG" -> {
                if (process.getAcc() < 0) {
                    process.setPc(process.getProgram().getLabels().get(operand));
                } else {
                    process.setPc(process.getPc() + 1);
                }
            }
            case "BRANY" -> {
                process.setPc(process.getProgram().getLabels().get(operand));
            }
            case "SYSCALL" -> {
                int code = Integer.parseInt(operand);
                if (code == 0) {
                    process.setState(ProcessState.FINISHED);
                    return ExecutionResult.FINISHED;
                }
                if (code == 1) {
                    System.out.println("[" + process.getName() + "] ACC = " + process.getAcc());
                }
                int blockTime = 1 + random.nextInt(3);
                process.setBlockedUntil(currentTick + blockTime);
                process.setState(ProcessState.BLOCKED);
                process.setPc(process.getPc() + 1);
                return ExecutionResult.BLOCKED;
            }
            default -> throw new RuntimeException("Opcode desconhecido: " + opcode);
        }

        return ExecutionResult.CONTINUE;
    }

    private int resolveValue(Process process, String operand, boolean immediate) {
        if (immediate) {
            return Integer.parseInt(operand);
        }
        return process.getMemory().load(operand);
    }
}
