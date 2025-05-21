import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.INFO);
        for (Handler h : logger.getHandlers()) {
            h.setFormatter(new SimpleFormatter());
        }

        // Aqui você chama o simulador ou carrega os processos
    }
}