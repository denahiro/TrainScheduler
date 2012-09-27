/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author Denahiro
 */
public interface Graph {
    public abstract void registerState(double time, Train toRegister);

    public abstract void saveToWriter(PrintWriter output);
}
