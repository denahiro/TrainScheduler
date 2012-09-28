/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.freehep.graphics2d.VectorGraphics;

/**
 *
 * @author Denahiro
 */
public abstract class Graph {
    HashMap<Train, List<DataPoint>> data=new HashMap<Train, List<DataPoint>>();

    protected abstract DataPoint generateDataPoint(double time, Train toRegister);
    protected String xName="x";
    protected String yName="y";
    protected String xLabel="x";
    protected String yLabel="y";
    protected String title="Graph";

    public void registerState(double time, Train toRegister) {
        if(!this.data.containsKey(toRegister)) {
            this.data.put(toRegister, new ArrayList<DataPoint>());
        }

        this.data.get(toRegister).add(this.generateDataPoint(time, toRegister));
    }

    public void saveToWriter(PrintWriter output) {
        output.println("train,"+this.xName+","+this.yName);
        Collection<List<DataPoint>> entries=this.data.values();
        Iterator<List<DataPoint>> firstIt=entries.iterator();
        int currentTrain=1;
        while(firstIt.hasNext()) {
            List<DataPoint> currentList=firstIt.next();
            ListIterator<DataPoint> secondIt=currentList.listIterator();
            while(secondIt.hasNext()) {
                DataPoint currentPoint=secondIt.next();
                output.println(Integer.toString(currentTrain)+","+currentPoint.toString());
            }
            ++currentTrain;
        }
    }

    public void draw(VectorGraphics g,Dimension dim,Insets insets) {
        GraphAxes myAxes=new GraphAxes();
        for(Train currentTrain: this.data.keySet()) {
            List<DataPoint> c=this.data.get(currentTrain);
            List<Double> times=new ArrayList<Double>();
            List<Double> positions=new ArrayList<Double>();
            for(DataPoint p: c) {
                times.add(p.x);
                positions.add(p.y);
            }
            myAxes.plot(times, positions, currentTrain.getColor());
        }

        myAxes.title(this.title);
        myAxes.label(this.xLabel, this.yLabel);

        myAxes.draw(g, dim, insets);
    }

    protected class DataPoint {
        public double x;
        public double y;

        public DataPoint(double myX,double myY) {
            this.x=myX;
            this.y=myY;
        }

        @Override
        public String toString() {
            return Double.toString(this.x)+","+Double.toString(this.y);
        }
    }
}
