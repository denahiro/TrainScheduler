/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

/**
 *
 * @author Denahiro
 */
public class TrainInfoGraph implements Graph {

    HashMap<Train, List<DataPoint>> data=new HashMap<Train, List<DataPoint>>();

    public void registerState(double time, Train toRegister) {
        if(!this.data.containsKey(toRegister)) {
            this.data.put(toRegister, new ArrayList<DataPoint>());
        }

        DataPoint newPoint=new DataPoint(time,toRegister);

        this.data.get(toRegister).add(newPoint);
    }

    public void saveToWriter(PrintWriter output) {
        output.println("train,time,position,velocity,maxVelocity");
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

    protected class DataPoint {
        private double time;
        private double position;
        private double velocity;
        private double maxVelocity;

        public DataPoint(double myTime,Train requester) {
            this.time=myTime;
            this.position=requester.getAbsolutePosition();
            this.velocity=requester.getVelocity();
            this.maxVelocity=requester.getCurrentTrack().getMaxVelocity(requester);
        }

        @Override
        public String toString() {
            return Double.toString(this.time)+","+Double.toString(this.position)+","
                    +Double.toString(this.velocity)+","+Double.toString(this.maxVelocity);
        }
    }
}
