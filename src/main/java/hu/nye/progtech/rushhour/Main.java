
package hu.nye.progtech.rushhour;

import hu.nye.progtech.rushhour.ui.ConsoleUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.info("Rush Hour alkalmazas elindult.");

        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (RuntimeException e) {
            LOGGER.error("Hiba tortent a jatek futasa kozben.", e);
            throw e;
        } finally {
            LOGGER.info("Rush Hour alkalmazas leallt.");
        }
    }
}
