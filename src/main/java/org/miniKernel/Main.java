package org.miniKernel;

import org.miniKernel.model.Process;
import org.miniKernel.model.Program;
import org.miniKernel.parser.Parser;
import org.miniKernel.scheduler.KernelSimulator;
import org.miniKernel.ui.ConsoleUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int SCH_POL_EDF = 1;

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        Parser parser = new Parser();
        ui.printBanner();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        List<Process> processes;
        try {
            processes = loadProcesses(reader, parser, ui, args);
        } catch (Exception e) {
            ui.printError("Falha ao carregar processos: " + e.getMessage());
            return;
        }

        if (processes.isEmpty()) {
            ui.printError("Nenhum processo carregado. Encerrando.");
            return;
        }

        ui.printLoadedProcesses(processes);
        ui.printSimulationStart(processes.size(), SCH_POL_EDF);

        KernelSimulator simulator = new KernelSimulator(processes, ui);
        simulator.run();

        ui.printSimulationEnd();
    }

    private static List<Process> loadProcesses(BufferedReader reader,
                                               Parser parser,
                                               ConsoleUI ui,
                                               String[] args) throws IOException {
        if (args != null && args.length >= 1) {
            return loadFromFile(args[0], parser);
        }

        while (true) {
            System.out.println("Escolha o modo de carga dos processos:");
            System.out.println("  [1] Entrada manual pelo teclado");
            System.out.println("  [2] Carregar a partir de um arquivo de configuração");
            System.out.print("Opção: ");
            String opt = reader.readLine();
            if (opt == null) {
                return List.of();
            }
            opt = opt.trim();

            switch (opt) {
                case "1" -> {
                    return loadManually(reader, parser, ui);
                }
                case "2" -> {
                    System.out.print("Caminho do arquivo de configuração: ");
                    String path = reader.readLine();
                    if (path == null || path.isBlank()) {
                        ui.printError("Caminho inválido.");
                        continue;
                    }
                    try {
                        return loadFromFile(path.trim(), parser);
                    } catch (Exception e) {
                        ui.printError(e.getMessage());
                    }
                }
                default -> ui.printError("Opção inválida. Digite 1 ou 2.");
            }
        }
    }

    private static List<Process> loadManually(BufferedReader reader,
                                              Parser parser,
                                              ConsoleUI ui) throws IOException {
        int n = readInt(reader, "Quantos processos deseja carregar? ", ui, 1, 100);
        List<Process> processes = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Processo " + (i + 1) + " ---");

            String name = readNonEmpty(reader, "Nome: ", ui);
            String path = readNonEmpty(reader, "Caminho do arquivo .asm: ", ui);
            int arrival = readInt(reader, "Tempo de chegada (arrivalTime): ", ui, 0, 100000);
            int ci = readInt(reader, "Tempo de computação (Ci): ", ui, 1, 100000);
            int pi = readInt(reader, "Período (Pi): ", ui, 1, 100000);

            Program program = parser.parse(path);
            int deadline = arrival + pi;
            processes.add(new Process(name, program, arrival, ci, pi, deadline));
        }
        return processes;
    }

    private static List<Process> loadFromFile(String configPath, Parser parser) throws IOException {
        List<String> rawLines = Files.readAllLines(Path.of(configPath));
        List<String> lines = new ArrayList<>();
        for (String l : rawLines) {
            String s = l.trim();
            if (s.isEmpty() || s.startsWith("#")) {
                continue;
            }
            lines.add(s);
        }

        if (lines.isEmpty()) {
            throw new RuntimeException("Arquivo de configuração vazio: " + configPath);
        }

        int n = Integer.parseInt(lines.get(0));
        int expected = 1 + (n * 5);
        if (lines.size() < expected) {
            throw new RuntimeException(
                    "Arquivo de configuração incompleto. Esperado " + expected +
                    " linhas úteis, encontrado " + lines.size());
        }

        List<Process> processes = new ArrayList<>(n);
        int idx = 1;
        for (int i = 0; i < n; i++) {
            String name = lines.get(idx++);
            String path = lines.get(idx++);
            int arrival = Integer.parseInt(lines.get(idx++));
            int ci = Integer.parseInt(lines.get(idx++));
            int pi = Integer.parseInt(lines.get(idx++));

            Program program = parser.parse(path);
            int deadline = arrival + pi;
            processes.add(new Process(name, program, arrival, ci, pi, deadline));
        }
        return processes;
    }

    private static int readInt(BufferedReader reader, String prompt,
                               ConsoleUI ui, int min, int max) throws IOException {
        while (true) {
            System.out.print(prompt);
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Entrada interrompida.");
            }
            try {
                int v = Integer.parseInt(line.trim());
                if (v < min || v > max) {
                    ui.printError("Valor fora do intervalo [" + min + ", " + max + "].");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                ui.printError("Digite um número inteiro válido.");
            }
        }
    }

    private static String readNonEmpty(BufferedReader reader, String prompt,
                                       ConsoleUI ui) throws IOException {
        while (true) {
            System.out.print(prompt);
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Entrada interrompida.");
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                ui.printError("Valor não pode ser vazio.");
                continue;
            }
            return trimmed;
        }
    }
}
