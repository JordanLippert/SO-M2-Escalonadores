package core;

import java.util.UUID;

/**
 * Esta classe representa um processo que será escalonado pelo sistema.
 * Pode conter dois trechos de execução com um bloqueio para I/O no meio
 */
public class Process {
    public final String id;
    public final int arrivalTime; //o que será isso?
    public final int exec1;
    public final boolean hasIO;
    public int ioTime;
    public final int exec2;

    public int remainingExec1;
    public int remainingExec2;
    public int waitTime = 0;
    public int turnaroundTime = 0;
    public int quantumRemaining;
    public ProcessState state = ProcessState.READY;

    public int startTime = -1;
    public int finishTime = -1;
    public boolean inIO = false;


    public Process(String id, int arrivalTime, int exec1, boolean hasIO, int ioTime, int exec2) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.exec1 = exec1;
        this.exec2 = exec2;
        this.hasIO = hasIO;
        this.ioTime = ioTime;
        this.remainingExec1 = exec1;
        this.remainingExec2 = exec2;
    }

    /**
     * Cria um processo com ID aleatório (caso não fornecido)
     */
    public static Process createAutoID(int arrivalTime, int exec1, boolean hasIO, int ioTime, int exec2) {
        return new Process(UUID.randomUUID().toString().substring(0,5), arrivalTime, exec1, hasIO, ioTime, exec2);
    }

    public boolean isFinished() {
        return remainingExec1 == 0 && (!hasIO || remainingExec2 == 0);
    }

    public boolean shouldStartIO() {
        return hasIO && remainingExec1 == 0 && !inIO;
    }

    @Override
    public String toString() {
        return String.format("Processo[%s}", id);
    }
}
