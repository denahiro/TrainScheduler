/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.Train;

/**
 *
 * @author Denahiro
 */
public class TimePosGraph extends Graph {

    private double timeFactor;
    private double lengthFactor;

    public enum Units{
        LARGE, SI
    }

    public TimePosGraph() {
        this(Units.SI);
    }

    public TimePosGraph(Units myUnits) {
        super();
        this.title="Graphical Timetable";
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

    public DataPoint generateDataPoint(double myTime,Train requester) {
        return new DataPoint(myTime/this.timeFactor, requester.getAbsolutePosition()/this.lengthFactor);
    }
}
