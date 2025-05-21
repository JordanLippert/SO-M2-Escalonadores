package core;

import java.util.*;
import java.util.logging.Logger;

/**
 * Responsável por aplicar a política Round Robin e gerenciar os núcleos e filas de processos.
 */
public class Scheduler {
    private static final Logger logger = Logger.getLogger(Scheduler.class.getName());

    private final Queue<Process> readyQueue = new LinkedList<>();
    private final List<CPUCore> cores;
    private final List<Process> blockedList = new ArrayList<>();
    private final List<Process> finishedList = new ArrayList<>();

    private final int quantum;
    private int contextSwitches = 0;

    public Scheduler(int numCores, int quantum) {
        this.quantum = quantum;
        this.cores = new ArrayList<>();
        for (int i = 0; i < numCores; i++) {
            cores.add(new CPUCore(i));
        }
    }

    /**
     * Adiciona um novo processo à fila de prontos.
     */
    public void addProcess(Process p) {
        logger.info("Processo " + p.id + " chegou ao sistema.");
        readyQueue.offer(p);
    }

    /**
     * Atualiza o estado da simulação a cada passo do tempo.
     */
    public void tick(int globalTime) {
        //1º Atualiza bloqueios (desbloqueia processos se necessário)
        updateIO();

        //2º Executa processos nos núcleos
        for (CPUCore core : cores) {
            Process p = core.getCurrentProcess();
            if (p != null) {
                core.tick(globalTime, quantum);

                if (p.isFinished()) {
                    logger.info(p.id + " finalizou.");
                    p.state = ProcessState.FINISHED;
                    p.finishTime = globalTime;
                    finishedList.add(core.releaseProcess());
                    continue;
                }

                if (p.shouldStartIO()) {
                    logger.info(p.id + " bloqueou para I/O.");
                    p.inIO = true;
                    p.state = ProcessState.BLOCKED;
                    blockedList.add(core.releaseProcess());
                    continue;
                }

                if (p.quantumRemaining <= 0) {
                    logger.info(p.id + " atingiu o limite de quantum.");
                    p.state = ProcessState.READY;
                    readyQueue.offer(core.releaseProcess());
                    contextSwitches++;
                }
            }
        }

        //3º Escalona novos processos nos núcleos disponíveis
        for (CPUCore core : cores) {
            if (core.isIdle() && !readyQueue.isEmpty()) {
                Process next = readyQueue.poll();
                core.assignProcess(next, quantum);
                logger.info("Escalonado " + next.id + " no núcleo " + core.getId());
            }
        }

        //4º Incrementa tempo de espera para os processos na fila
        for (Process p : readyQueue) {
            p.waitTime++;
        }
    }

    /**
     * Atualiza os processos em I/O, desbloqueando-os após o tempo.
     */
    private void updateIO() {
        Iterator<Process> it = blockedList.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            p.ioTime--;
            if (p.ioTime <= 0) {
                p.state = ProcessState.READY;
                p.inIO = false;
                logger.info(p.id + " retornou da I/O para a fila de prontos.");
                readyQueue.offer(p);
                it.remove();
            }
        }
    }

    public boolean isSimulationComplete() {
        return readyQueue.isEmpty() &&
                blockedList.isEmpty() &&
                cores.stream().allMatch(CPUCore::isIdle);
    }

    public List<Process> getFinishedProcesses() {
        return finishedList;
    }

    public int getContextSwitches() {
        return contextSwitches;
    }

    public List<CPUCore> getCores() {
        return cores;
    }

    public Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    public List<Process> getBlockedList() {
        return blockedList;
    }
}
