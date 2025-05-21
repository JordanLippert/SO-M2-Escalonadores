package core;

import simulation.Simulator;

import java.util.logging.Logger;

/**
 * Representa um núcleo de CPU que pode executar um processo por vez.
 */
public class CPUCore {
    private static final Logger logger = Logger.getLogger(CPUCore.class.getName());

    private final int id;
    private Process currentProcess;
    private boolean idle = true;

    public CPUCore(int id) {
        this.id = id;
    }

    /**
     * Simula a execução de um processo durante um passo de tempo.
     */
    public void tick(int globalTime, int quantum) {
        if (currentProcess == null) {
            idle = true;
            return;
        }

        currentProcess.quantumRemaining--;
        if (!currentProcess.inIO) {
            if (currentProcess.remainingExec1 > 0) {
                currentProcess.remainingExec1--;
            } else if (currentProcess.hasIO && currentProcess.remainingExec2 > 0) {
                currentProcess.remainingExec2--;
            }
        }

        logger.info(String.format("Core %d executando %s (quantum restante: %d)", id, currentProcess.id, currentProcess.quantumRemaining));
        //Verificações de fim de execução, bloqueio ou fim do quantum serão feitas no escalonador
    }

    public void assignProcess(Process p, int quantum) {
        this.currentProcess = p;
        this.currentProcess.quantumRemaining = quantum;
        this.currentProcess.state = ProcessState.RUNNING;
        idle = false;
        if (p.startTime == -1) {
            p.startTime = Simulator.globalTime;
        }
    }

    public Process releaseProcess() {
        Process p = currentProcess;
        currentProcess = null;
        idle = true;
        return p;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public boolean isIdle() {
        return idle;
    }

    public int getId() {
        return id;
    }
}
