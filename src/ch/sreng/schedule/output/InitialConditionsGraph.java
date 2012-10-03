/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import ch.sreng.schedule.components.mobile.SimplePower;
import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.Station;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.procedure.DriveStrategyBangBang;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.Master;
import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Denahiro
 */
public class InitialConditionsGraph extends Graph {

    private Double headway=null;
    private Double lastTimeDeploy=null;

    private double lastVelocity;
    private double lastTime;

    private List<DataPoint> initialConditions=new ArrayList<DataPoint>();

    public InitialConditionsGraph(double myHeadways) {
        this.headway=myHeadways;
    }

    public void registerState(double time, Train toRegister) {
        if(this.lastTimeDeploy==null || this.lastTimeDeploy+this.headway<=time) {
            this.initialConditions.add(new DataPoint(time,toRegister.getCurrentTrack()
                ,toRegister.getTargetStation(),toRegister.getPositionOnCurrentTrack()
                ,toRegister.getVelocity()));
            this.lastTimeDeploy=time;
        }
        this.lastVelocity=toRegister.getVelocity();
        this.lastTime=time;
    }

    public void initialiseTrains(Master myMaster) {
        Color tmpColor=Color.RED;
        Double tmpVelocity=this.lastVelocity;
        for(DataPoint p:this.initialConditions) {
            if(this.lastTime-p.time>0.5*this.headway) {
                Train tmpTrain=new Train(new DriveStrategyBangBang(), new SafetyStrategy()
                    , new SimplePower(), tmpColor);
                if(tmpVelocity==null) {
                    tmpVelocity=p.velocity;
                }
                tmpTrain.setInitialConditions(p.currentTrack, p.nextStation, p.position, tmpVelocity);
                myMaster.registerTrain(tmpTrain);
                tmpColor=Color.BLUE;
                tmpVelocity=null;
            }
        }
    }

    public double getLastVelocity(){
        return this.lastVelocity;
    }

    public void saveToWriter(PrintWriter output) {
        throw new UnsupportedOperationException("Not supported.");
    }

    protected void plotToAxes(GraphAxes axes) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getSummedInformation() {
        throw new UnsupportedOperationException("Not supported.");
    }

    protected class DataPoint {
        public double time;
        public TrackComponent currentTrack;
        public double position;
        public Station nextStation;
        public double velocity;

        public DataPoint(double myTime,TrackComponent myTrack,Station myStation
                ,double myPosition,double myVelocity) {
            this.time=myTime;
            this.position=myPosition;
            this.currentTrack=myTrack;
            this.nextStation=myStation;
            this.velocity=myVelocity;
        }
    }
}
