/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.io.PrintWriter;

/**
 *
 * @author Denahiro
 */
public interface Graph {
    public abstract void registerState(double time, Train toRegister);

    public abstract void saveToWriter(PrintWriter output);

    public abstract void draw(Graphics2D g,Dimension dim,Insets insets);
}
