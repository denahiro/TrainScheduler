/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.PrintWriter;
import org.freehep.graphics2d.VectorGraphics;

/**
 *
 * @author Denahiro
 */
public abstract class Graph {
    protected String xLabel="x";
    protected String yLabel="y";
    protected String title="Graph";

    public abstract void registerState(double time, Train toRegister);

    protected abstract void plotToAxes(GraphAxes axes);

    public abstract void saveToWriter(PrintWriter output);

    public void draw(VectorGraphics g,Dimension dim,Insets insets) {
        GraphAxes myAxes=new GraphAxes();

        this.plotToAxes(myAxes);

        myAxes.title(this.title);
        myAxes.label(this.xLabel, this.yLabel);

        myAxes.draw(g, dim, insets);
    }

    public abstract String getSummedInformation();
}
