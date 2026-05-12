package org.miniKernel.cpu;

import org.miniKernel.model.ExecutionResult;
import org.miniKernel.model.Instruction;
import org.miniKernel.model.Process;
import org.miniKernel.model.ProcessState;
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
                int divisor = resolveValue(process, operand, immediate);
                if (divisor == 0) {
                    throw new RuntimeException("Divisão por zero no processo: " + process.getName());
                }
                process.setAcc(process.getAcc() / divisor);
                process.setPc(process.getPc() + 1);
            }
            case "BRPOS" -> {
                if (process.getAcc() > 0) {
                    process.setPc(resolveLabel(process, operand));
                } else {
                    process.setPc(process.getPc() + 1);
                }
            }
            case "BRZERO" -> {
                if (process.getAcc() == 0) {
                    process.setPc(resolveLabel(process, operand));
                } else {
                    process.setPc(process.getPc() + 1);
                }
            }
            case "BRNEG" -> {
                if (process.getAcc() < 0) {
                    process.setPc(resolveLabel(process, operand));
                } else {
                    process.setPc(process.getPc() + 1);
                }
            }
            case "BRANY" -> {
                process.setPc(resolveLabel(process, operand));
            }
            case "SYSCALL" -> {
                int code = Integer.parseInt(operand);
                if (code == 0) {
                    process.setState(ProcessState.FINISHED);
                    return ExecutionResult.FINISHED;
                }
                if (code == 1) {
                    System.out.println("[" + process.getName() + "] ACC = " + process.getAcc());
                } else if (code != 2) {
                    throw new RuntimeException("SYSCALL desconhecido: " + code + " no processo: " + process.getName());
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
        if (operand == null) {
            throw new RuntimeException("Instrução sem operando no processo: " + process.getName());
        }
        if (immediate) {
            return Integer.parseInt(operand);
        }
        return process.getMemory().load(operand);
    }

    private int resolveLabel(Process process, String label) {
        Integer target = process.getProgram().getLabels().get(label);
        if (target == null) {
            throw new RuntimeException("Label não encontrada: '" + label + "' no processo: " + process.getName());
        }
        return target;
    }
}
