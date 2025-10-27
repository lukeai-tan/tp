package internity;

import java.util.logging.Logger;

import internity.logic.InternityManager;
import internity.utils.InternityLogger;

/**
 * The main entry point for the <b>Internity</b> application.
 * <p>
 * This class is responsible for initializing and starting the core application
 * through the {@link InternityManager}. It defines the default data storage
 * location and initializes the system when the program starts.
 * </p>
 */
public class Internity {
    /**
     * The default file path used to store internship data.
     */
    private static final String DEFAULT_STORAGE_PATH = "./data/internships.txt";

    /**
     * Initializes the Internity system by creating an {@link InternityManager}
     * instance and starting it.
     * <p>
     * This method sets up all necessary components and begins program execution.
     * </p>
     */
    public static void initInternity() {
        Logger logger = InternityLogger.getLogger();
        logger.info("Internity starting...");

        InternityManager manager = new InternityManager(DEFAULT_STORAGE_PATH);
        manager.start();
    }

    /**
     * The main entry point for the <b>Internity</b> application.
     * <p>
     * This method serves as the program launcher and delegates initialization
     * to {@link #initInternity()}.
     * </p>
     */
    public static void main(String[] args) {
        initInternity();
    }
}
