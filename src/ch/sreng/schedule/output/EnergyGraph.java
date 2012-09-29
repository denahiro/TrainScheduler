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
public class EnergyGraph extends Graph {

    private double timeFactor;
    private double energyFactor;

    private String xName;
    private String yName;

    private HashMap<Train, List<DataPoint>> data=new HashMap<Train, List<DataPoint>>();

    public enum Units{
        LARGE, SI
    }

    public EnergyGraph() {
        this(Units.SI);
    }

    public EnergyGraph(Units myUnits) {
        super();
        this.title="Energy Consumption Graph";
        switch(myUnits)
        {
            case LARGE:
                this.timeFactor=60;
                this.energyFactor=1000000;
                this.xLabel="Time [min]";
                this.yLabel="Consumed Power [MW]";
                this.xName="timeMin";
                this.yName="powerMW";
                break;
            default :
                this.timeFactor=1;
                this.energyFactor=1;
                this.xLabel="Time [s]";
                this.yLabel="Consumed Power [W]";
                this.xName="timeS";
                this.yName="powerW";
                break;
        }
    }

    public void registerState(double time, Train toRegister) {
        if(!this.data.containsKey(toRegister)) {
            this.data.put(toRegister, new ArrayList<DataPoint>());
        }

        this.data.get(toRegister).add(new DataPoint(time/this.timeFactor
                ,toRegister.getPowerConsumption()/this.energyFactor));
    }

    public void saveToWriter(PrintWriter output) {
        output.println(this.xName+","+this.yName);

        List<DataPoint> firstList=this.data.values().iterator().next();
        List<Double> times=new ArrayList<Double>();
        for(DataPoint p: firstList) {
            times.add(p.time);
        }

        List<Double> energies=this.addEnergyConsumption();

        ListIterator<Double> timeIt=times.listIterator();
        ListIterator<Double> energyIt=energies.listIterator();
        while(timeIt.hasNext() && energyIt.hasNext()) {
            output.println(Double.toString(timeIt.next())+","+Double.toString(energyIt.next()));
        }
    }

    protected void plotToAxes(GraphAxes axes) {
        List<DataPoint> firstList=this.data.values().iterator().next();
        List<Double> times=new ArrayList<Double>();
        for(DataPoint p: firstList) {
            times.add(p.time);
        }

        axes.plot(times, this.addEnergyConsumption(), Color.BLACK);
    }

    private List<Double> addEnergyConsumption() {
        Iterator<List<DataPoint>> it=this.data.values().iterator();
        List<DataPoint> dataList=it.next();
        List<Double> returnList=new ArrayList<Double>();
        for(DataPoint p: dataList) {
            returnList.add(p.energyConsumption);
        }

        while(it.hasNext()) {
            dataList=it.next();
            ListIterator<DataPoint> dataIt=dataList.listIterator();
            ListIterator<Double> returnIt=returnList.listIterator();
            while(dataIt.hasNext() && returnIt.hasNext()) {
                Double returnElement=returnIt.next();
                returnElement=new Double(returnElement+dataIt.next().energyConsumption);
                returnIt.set(returnElement);
            }
        }

        return returnList;
    }

    protected class DataPoint {
        public double time;
        public double energyConsumption;

        public DataPoint(double myTime,double myEnergy) {
            this.time=myTime;
            this.energyConsumption=myEnergy;
        }

        @Override
        public String toString() {
            return Double.toString(this.time)+","+Double.toString(this.energyConsumption);
        }
    }
}
