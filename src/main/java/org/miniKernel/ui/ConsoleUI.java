package org.miniKernel.ui;

import org.miniKernel.model.ExecutionResult;
import org.miniKernel.model.Process;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Camada de visualização do simulador.
 *
 * Responsabilidades (Pessoa 4):
 *  - Banner / mensagens de status do sistema
 *  - Logs de eventos (chegada, desbloqueio, execução, syscall, deadline miss)
 *  - Tabela temporal (Gantt em ASCII) com todo o histórico
 *  - Resumo final (estatísticas por processo + globais)
 *
 * Todas as mensagens passam por aqui. O simulador NÃO imprime nada diretamente
 * em System.out — sempre delega para ConsoleUI. Isso facilita futuramente trocar
 * para outra forma de visualização (arquivo, GUI, JSON, etc.).
 */
public class ConsoleUI {

    private static final String LINE = "============================================================";

    /* ===================== Banner / Status ===================== */

    public void printBanner() {
        System.out.println();
        System.out.println(LINE);
        System.out.println("           Mini-Kernel EDF  -  Simulador de Processos");
        System.out.println("                 Sistemas Operacionais - TP1");
        System.out.println(LINE);
        System.out.println();
    }

    public void printSimulationStart(int processCount, int schedulingPolicy) {
        System.out.println();
        System.out.println(LINE);
        System.out.printf("  Iniciando simulação | sch_pol=%d (EDF) | %d processo(s)%n",
                schedulingPolicy, processCount);
        System.out.println(LINE);
        System.out.println();
    }

    public void printSimulationEnd() {
        System.out.println();
        System.out.println(LINE);
        System.out.println("                  Simulação encerrada");
        System.out.println(LINE);
    }

    public void printMaxTimeReached(int maxTime) {
        System.out.println();
        System.out.printf("[!] Tempo máximo da simulação (%d) atingido.%n", maxTime);
    }

    /* ===================== Resumo dos processos carregados ===================== */

    public void printLoadedProcesses(List<Process> processes) {
        System.out.println();
        System.out.println("--- Processos carregados ---");
        System.out.printf("%-10s | %-8s | %-8s | %-8s | %-10s%n",
                "Nome", "Arrival", "Ci", "Pi", "Deadline");
        System.out.println("-----------------------------------------------------------");
        for (Process p : processes) {
            System.out.printf("%-10s | %-8d | %-8d | %-8d | %-10d%n",
                    p.getName(),
                    p.getArrivalTime(),
                    p.getComputationTime(),
                    p.getPeriod(),
                    p.getDeadline());
        }
        System.out.println();
    }

    /* ===================== Logs por tick ===================== */

    public void logArrival(int time, Process p) {
        System.out.printf("Time %3d | ARRIVED   | %-10s | Ci=%d Pi=%d Deadline=%d%n",
                time, p.getName(), p.getComputationTime(), p.getPeriod(), p.getDeadline());
    }

    public void logUnblock(int time, Process p) {
        System.out.printf("Time %3d | UNBLOCKED | %-10s%n", time, p.getName());
    }

    public void logIdle(int time) {
        System.out.printf("Time %3d | IDLE%n", time);
    }

    /**
     * Imprime o log da execução de UMA instrução por um processo, no tick atual.
     */
    public void logExecution(int time, Process p, ExecutionResult result) {
        String status = switch (result) {
            case CONTINUE -> "RUNNING ";
            case BLOCKED  -> "BLOCKED ";
            case FINISHED -> "FINISHED";
        };
        System.out.printf("Time %3d | %s | %-10s | PC=%d | ACC=%d | Deadline=%d%n",
                time, status, p.getName(), p.getPc(), p.getAcc(), p.getDeadline());
    }

    public void logSyscallPrint(Process p, int value) {
        System.out.printf("           >> SYSCALL 1 (%s) imprime: %d%n", p.getName(), value);
    }

    public void logSyscallRead(Process p, int value) {
        System.out.printf("           >> SYSCALL 2 (%s) leitura: %d%n", p.getName(), value);
    }

    public void logDeadlineMiss(int time, Process p) {
        System.out.printf("Time %3d | *** DEADLINE MISS *** | %-10s | Deadline=%d%n",
                time, p.getName(), p.getDeadline());
    }

    /* ===================== Tabela temporal (Gantt) ===================== */

    /**
     * Imprime uma tabela tipo Gantt mostrando, tick a tick, qual processo executou.
     * Limita-se a 80 colunas por linha para caber bem no terminal.
     */
    public void printTimeline(Timeline timeline) {
        int total = timeline.getTotalTime();
        if (total == 0) {
            System.out.println("\n[Tabela temporal vazia]");
            return;
        }

        System.out.println();
        System.out.println("--- Tabela Temporal (Gantt) ---");
        System.out.println("Cada célula representa 1 unidade de tempo.");
        System.out.println();

        List<String> ticks = timeline.getTicks();
        final int CELL_WIDTH = 4;
        final int LINE_WIDTH = 80;
        int cellsPerLine = Math.max(1, LINE_WIDTH / (CELL_WIDTH + 1));

        for (int start = 0; start < total; start += cellsPerLine) {
            int end = Math.min(start + cellsPerLine, total);

            StringBuilder header = new StringBuilder("t  ");
            StringBuilder body = new StringBuilder("   ");
            for (int t = start; t < end; t++) {
                header.append(String.format("|%-" + CELL_WIDTH + "d", t));
                String label = ticks.get(t);
                if (label.length() > CELL_WIDTH) {
                    label = label.substring(0, CELL_WIDTH);
                }
                body.append(String.format("|%-" + CELL_WIDTH + "s", label));
            }
            header.append("|");
            body.append("|");

            System.out.println(header);
            System.out.println(body);
            System.out.println();
        }
    }

    /* ===================== Resumo final ===================== */

    /**
     * Imprime o resumo da simulação:
     *   - Total de ticks
     *   - Ocupação de CPU por processo
     *   - Ociosidade
     *   - Deadlines perdidos por processo
     */
    public void printSummary(Timeline timeline, Map<String, Integer> missedDeadlines) {
        System.out.println();
        System.out.println("--- Resumo da Simulação ---");
        System.out.printf("Tempo total simulado: %d unidade(s)%n", timeline.getTotalTime());
        System.out.printf("CPU ociosa: %d tick(s)%n", timeline.getIdleTicks());

        Set<String> names = timeline.getProcessNames();
        if (!names.isEmpty()) {
            System.out.println();
            System.out.printf("%-10s | %-12s | %-12s%n",
                    "Processo", "Ticks CPU", "Deadlines perdidos");
            System.out.println("------------------------------------------------");
            for (String name : names) {
                int execTicks = timeline.getExecutionCount(name);
                int misses = missedDeadlines.getOrDefault(name, 0);
                System.out.printf("%-10s | %-12d | %-12d%n", name, execTicks, misses);
            }
        }

        int totalMisses = missedDeadlines.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println();
        if (totalMisses == 0) {
            System.out.println("Resultado: nenhum deadline foi perdido.");
        } else {
            System.out.printf("Resultado: %d deadline(s) perdido(s) no total.%n", totalMisses);
        }
    }

    /* ===================== Erros / mensagens auxiliares ===================== */

    public void printError(String message) {
        System.out.println("[ERRO] " + message);
    }

    public void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }
}
