/**
 * 
 */
package ch.sreng.schedule.components.mobile;

import ch.sreng.schedule.Scheduler;
import ch.sreng.schedule.components.stationary.Station;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.Time;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * @author koenigst
 *
 */
public class Train {

    private TrackComponent currentTrack;
//  private double currentPosition;
    private Station targetStation;
    private Double departureTime;
    private Double lastDepartureTime;
    private double currentVelocity;
    private static Double MAX_VELOCITY=null;
    private static Double LENGTH=null;
    private DriveStrategy driveStrategy;
    private SafetyStrategy safetyStrategy;
    private Power power;
    private Color trainColor;

    final private static String SOURCE_FILE="data/mobile/train.ini";
    private static void loadIni() {
        if(MAX_VELOCITY==null) {
            try {
                BufferedReader sourceReader=new BufferedReader(new InputStreamReader(Scheduler.class.getResourceAsStream(SOURCE_FILE)));
                String currentLine=sourceReader.readLine();
                while(currentLine!=null) {
                    String[] splitLine=currentLine.split("=");
                    if(splitLine[0].equalsIgnoreCase("maxVelocity")) {
                        MAX_VELOCITY=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("length")) {
                        LENGTH=Double.parseDouble(splitLine[1]);
                    }
                    currentLine=sourceReader.readLine();
                }
            } catch(IOException ex) {
                System.out.println("Unable to load \"train.ini\"");
            }
        }
    }

    public Train(DriveStrategy myDriveStrategy, SafetyStrategy mySafetyStrategy,
            Power myPower,Color myColor)
    {
        loadIni();
        this.driveStrategy=myDriveStrategy;
        this.safetyStrategy=mySafetyStrategy;
        this.power=myPower;
//        this.MAX_VELOCITY=myMaxVelocity;
//        this.LENGTH=myLength;
        this.trainColor=myColor;
    }

    public void setInitialConditions(TrackComponent initialTrack,Station initialTargetStation
            ,double initialPosition,double initialVelocity)
    {
        this.currentVelocity=initialVelocity;
        this.currentTrack=initialTrack;
        this.setPosition(initialPosition);
        this.targetStation=initialTargetStation;
    }

    public Station getTargetStation()
    {
        return this.targetStation;
    }

    public double getLength()
    {
        return LENGTH;
    }

    public double getVelocity()
    {
        return this.currentVelocity;
    }

    public double getMaxAcceleration()
    {
        return this.power.getMaxAcceleration();
    }

    public double getMaxDeceleration()
    {
        return this.power.getMaxDeceleration();
    }

    public double getMaxVelocity()
    {
        return MAX_VELOCITY;
    }

    public TrackComponent getCurrentTrack()
    {
        return this.currentTrack;
    }

    public Color getColor() {
        return this.trainColor;
    }

    public double getPowerConsumption() {
        return this.power.getPowerConsumption();
    }

    public void move(Time timer)
    {
        if(this.targetStation.arrived(this)) {
            if(this.departureTime==null) {
                if(this.lastDepartureTime==null) {
                    this.departureTime=new Double(this.targetStation.getWaitTime()+timer.getTime());
                } else {
                    double travelTime=timer.getTime()-this.lastDepartureTime;
                    this.departureTime=new Double(this.targetStation.getWaitTime()
                            +this.safetyStrategy.travelTimeSafetyWaitTime(travelTime)
                            +timer.getTime());
                }
                this.currentVelocity=0;
            } else if(this.departureTime<timer.getTime()) {
                this.lastDepartureTime=new Double(timer.getTime());
                this.departureTime=null;
                this.targetStation=this.targetStation.getNextStation();
            }

            this.power.calculatePowerConsumption(Arrays.asList(0.0d,timer.getDeltaTime()), Arrays.asList(0.0d,0.0d));
        } else {
            List<Double> timeList=new ArrayList<Double>();
            List<Double> velocityList=new ArrayList<Double>();
            timeList.add(0.0d);
            velocityList.add(this.currentVelocity);

            List<DriveStrategy.AccelerationAtTime> accelProfile=this.driveStrategy.getAccelerationProfile(this, timer);
            double timePassed=0;
            ListIterator<DriveStrategy.AccelerationAtTime> it=accelProfile.listIterator();
            DriveStrategy.AccelerationAtTime currentAcceleration=it.next();
            while(timePassed<timer.getDeltaTime())
            {
                double accelerationTime;
                DriveStrategy.AccelerationAtTime nextAcceleration=null;
                if(it.hasNext()) {
                    nextAcceleration=it.next();
                    if(nextAcceleration.time<timer.getDeltaTime()) {
                        accelerationTime=nextAcceleration.time-timePassed;
                    } else {
                        accelerationTime=timer.getDeltaTime()-timePassed;
                    }
                } else {
                    accelerationTime=timer.getDeltaTime()-timePassed;
                }

                if(accelerationTime<0) {
                    throw new RuntimeException("Negative acceleration time.");
                }

                this.setPosition(this.currentTrack.getTrainEndPosition(this)+this.currentVelocity*accelerationTime
                        +currentAcceleration.acceleration*accelerationTime*accelerationTime/2);
                this.currentVelocity+=currentAcceleration.acceleration*accelerationTime;
                timePassed+=accelerationTime;
                currentAcceleration=nextAcceleration;

                timeList.add(timePassed);
                velocityList.add(this.currentVelocity);
            }

            this.power.calculatePowerConsumption(timeList, velocityList);
        }
    }

    public boolean equals(Train x)
    {
        return this==x;
    }

//    public double getRelativePosition(TrackComponent origo)
//    {
//        double returnLength=0;
//        TrackComponent currentTrack=origo;
//        while(currentTrack!=this.currentTrack)
//        {
//            returnLength+=currentTrack.getLength(this);
//            currentTrack=currentTrack.getNextTrack(this);
//        }
//        return returnLength+this.currentTrack.getTrainEndPosition(this);
//    }

    public double getAbsolutePosition() {
        return this.currentTrack.getAbsoluteTrainEndPosition(this);
    }

    public SafetyStrategy getSafetyStrategy()
    {
        return this.safetyStrategy;
    }

    private void setPosition(double newPosition)
    {
        while(!this.currentTrack.setMyPosition(this, newPosition))
        {
            newPosition-=this.currentTrack.getLength(this);
            this.currentTrack=this.currentTrack.getNextTrack(this);
        }
    }
}
