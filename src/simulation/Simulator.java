package simulation;

import core.*;
import core.Process;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controla o tempo global da simulação e aciona o escalonador a cada passo.
 */
public class Simulator {
    public static int globalTime = 0;

    private static final Logger logger = Logger.getLogger(Simulator.class.getName());

    private final Scheduler scheduler;
    private final int tempoMaximo;

    public Simulator(Scheduler scheduler, int tempoMaximo) {
        this.scheduler = scheduler;
        this.tempoMaximo = tempoMaximo;
    }

    /**
     * Executa a simulação passo a passo.
     */
    public void run() {
        logger.info("Iniciando simulação...");

        while (!scheduler.isSimulationComplete() && globalTime < tempoMaximo) {
            logger.info("==== Tempo: " + globalTime + " ====");
            scheduler.tick(globalTime);
            globalTime++;

            try {
                Thread.sleep(500); // Simula tempo real (ajustável)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logger.info("Simulação encerrada.");
        mostrarRelatorioFinal();
    }

    private void mostrarRelatorioFinal() {
        List<Process> processos = scheduler.getFinishedProcesses();
        double usoCpu = processos.stream().mapToInt(p -> p.exec1 + p.exec2).sum() / (double) globalTime;
        double tempoEsperaMedio = processos.stream().mapToInt(p -> p.waitTime).average().orElse(0);
        double turnaroundMedio = processos.stream().mapToInt(p -> p.finishTime - p.arrivalTime).average().orElse(0);

        System.out.println("\n===== Relatório Final =====");
        System.out.println("Tempo total: " + globalTime);
        System.out.println("Uso da CPU (médio): " + String.format("%.2f", usoCpu * 100) + "%");
        System.out.println("Tempo de espera médio: " + tempoEsperaMedio);
        System.out.println("Turnaround médio: " + turnaroundMedio);
        System.out.println("Trocas de contexto: " + scheduler.getContextSwitches());
    }
}