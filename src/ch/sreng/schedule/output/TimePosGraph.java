/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;
import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Denahiro
 */
public class TimePosGraph extends Graph {

    private double timeFactor;
    private double lengthFactor;

    private String xName;
    private String yName;

    private HashMap<Train, List<DataPoint>> data=new HashMap<Train, List<DataPoint>>();

    @Override
    public String getSummedInformation() {
        return "Number of Trains: "+Integer.toString(this.data.size());
    }

    public enum Units{
        LARGE, SI
    }

    public TimePosGraph() {
        this(Units.SI);
    }

    public TimePosGraph(Units myUnits) {
        super();
        this.title="";
        switch(myUnits)
        {
            case LARGE:
                this.timeFactor=60;
                this.lengthFactor=1000;
                this.xLabel="Time [min]";
                this.yLabel="Position [km]";
                this.xName="timeMin";
                this.yName="positionKM";
                break;
            default :
                this.timeFactor=1;
                this.lengthFactor=1;
                this.xLabel="Time [s]";
                this.yLabel="Position [m]";
                this.xName="timeS";
                this.yName="positionM";
                break;
        }
    }

    public void registerState(double time, Train toRegister) {
        if(!this.data.containsKey(toRegister)) {
            this.data.put(toRegister, new ArrayList<DataPoint>());
        }

        this.data.get(toRegister).add(new DataPoint(time/this.timeFactor
                ,toRegister.getAbsolutePosition()/this.lengthFactor));
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

    protected void plotToAxes(GraphAxes axes) {
        axes.setGrid(60/this.timeFactor, 0, Color.BLACK);
        for(Train currentTrain: this.data.keySet()) {
            List<DataPoint> c=this.data.get(currentTrain);
            List<Double> times=new ArrayList<Double>();
            List<Double> positions=new ArrayList<Double>();
            for(DataPoint p: c) {
                times.add(p.time);
                positions.add(p.position);
            }
            axes.plot(times, positions, currentTrain.getColor());
        }
    }

    protected class DataPoint {
        public double time;
        public double position;

        public DataPoint(double myTime,double myPosition) {
            this.time=myTime;
            this.position=myPosition;
        }

        @Override
        public String toString() {
            return Double.toString(this.time)+","+Double.toString(this.position);
        }
    }
}
