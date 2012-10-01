/**
 * 
 */
package ch.sreng.schedule;

import ch.sreng.schedule.simulation.SimulationDispatcher;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author koenigst
 *
 */
public class Scheduler {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            SimulationDispatcher.run("data/dispatch.xml");
        } catch (IOException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
