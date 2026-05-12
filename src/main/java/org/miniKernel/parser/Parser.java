package org.miniKernel.parser;

import org.miniKernel.model.Instruction;
import org.miniKernel.model.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Parser {

    private static final Set<String> VALID_OPCODES = Set.of(
            "LOAD", "STORE",
            "ADD", "SUB", "MULT", "DIV",
            "BRANY", "BRPOS", "BRZERO", "BRNEG",
            "SYSCALL"
    );

    public Program parse(String filePath) {
        List<Instruction> instructions = new ArrayList<>();
        Map<String, Integer> data = new HashMap<>();
        Map<String, Integer> labels = new HashMap<>();

        boolean inCode = false;
        boolean inData = false;

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            for (String rawLine : lines) {
                String line = rawLine.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase(".code")) {
                    inCode = true;
                    inData = false;
                    continue;
                }

                if (line.equalsIgnoreCase(".endcode")) {
                    inCode = false;
                    continue;
                }

                if (line.equalsIgnoreCase(".data")) {
                    inData = true;
                    inCode = false;
                    continue;
                }

                if (line.equalsIgnoreCase(".enddata")) {
                    inData = false;
                    continue;
                }

                if (inCode) {
                    parseCodeLine(line, instructions, labels);
                } else if (inData) {
                    parseDataLine(line, data);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + filePath, e);
        }

        return new Program(instructions, data, labels);
    }

    private void parseCodeLine(String line,
                               List<Instruction> instructions,
                               Map<String, Integer> labels) {

        if (line.contains(":")) {
            String[] labelParts = line.split(":", 2);

            String label = labelParts[0].trim();
            labels.put(label, instructions.size());

            line = labelParts[1].trim();

            if (line.isEmpty()) {
                return;
            }
        }

        String[] parts = line.split("\\s+");

        String opcode = parts[0].toUpperCase();

        if (!VALID_OPCODES.contains(opcode)) {
            throw new RuntimeException("Instrução inválida: " + opcode);
        }

        String operand = null;
        boolean immediate = false;

        if (parts.length > 1) {
            operand = parts[1];

            if (operand.startsWith("#")) {
                immediate = true;
                operand = operand.substring(1);
            }
        }

        Instruction instruction = new Instruction(opcode, operand, immediate);
        instructions.add(instruction);
    }

    private void parseDataLine(String line, Map<String, Integer> data) {
        String[] parts = line.split("\\s+");

        if (parts.length != 2) {
            throw new RuntimeException("Linha inválida na seção .data: " + line);
        }

        String variableName = parts[0];
        int value = Integer.parseInt(parts[1]);

        data.put(variableName, value);
    }
}