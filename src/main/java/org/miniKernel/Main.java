package org.miniKernel;

import org.miniKernel.model.Process;
import org.miniKernel.model.Program;
import org.miniKernel.parser.Parser;
import org.miniKernel.scheduler.KernelSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int sch_pol = 1; // 1 = EDF

        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        List<Process> processes = new ArrayList<>();

        System.out.println("=== Mini-Kernel EDF Simulator ===");
        System.out.print("Quantos processos deseja carregar? ");
        int n = Integer.parseInt(scanner.nextLine().trim());

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Processo " + (i + 1) + " ---");

            System.out.print("Nome: ");
            String name = scanner.nextLine().trim();

            System.out.print("Caminho do arquivo .asm: ");
            String path = scanner.nextLine().trim();

            System.out.print("Tempo de chegada (arrivalTime): ");
            int arrivalTime = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Tempo de computacao (Ci): ");
            int computationTime = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Periodo (Pi): ");
            int period = Integer.parseInt(scanner.nextLine().trim());

            Program program = parser.parse(path);
            // absoluteDeadline inicial = arrivalTime + period  (d_i = P_i)
            int deadline = arrivalTime + period;
            processes.add(new Process(name, program, arrivalTime, computationTime, period, deadline));
        }

        System.out.println("\n=== Iniciando simulacao EDF (sch_pol=" + sch_pol + ") ===\n");
        KernelSimulator simulator = new KernelSimulator(processes);
        simulator.run();
        System.out.println("\n=== Simulacao encerrada ===");

        scanner.close();
    }
}
