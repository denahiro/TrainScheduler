/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resourceTest;

import ch.sreng.schedule.simulation.SimulationDispatcher;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Denahiro
 */
public class TestDispatcher {

    public static void main(String[] args) {
        try {
            SimulationDispatcher.run("data/dispatch.xml");
        } catch (IOException ex) {
            Logger.getLogger(TestDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
