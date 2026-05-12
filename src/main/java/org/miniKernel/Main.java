package org.miniKernel;

import org.miniKernel.model.Instruction;
import org.miniKernel.model.Program;
import org.miniKernel.parser.Parser;

public class Main {

    public static void main(String[] args) {

        Parser parser = new Parser();

        Program program = parser.parse("C:\\Users\\JulliaRoss\\Documents\\sistemasOperacionais\\MiniKernel\\exemples\\loop.asm");

        System.out.println("===== INSTRUCTIONS =====");

        for (Instruction instruction : program.getInstructions()) {

            System.out.println(
                    instruction.getOpcode()
                            + " | "
                            + instruction.getOperand()
                            + " | immediate="
                            + instruction.isImmediate()
            );
        }

        System.out.println("\n===== LABELS =====");

        System.out.println(program.getLabels());

        System.out.println("\n===== DATA =====");

        System.out.println(program.getData());
    }
}