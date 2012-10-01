/**
 * 
 */
package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



/**
 * @author koenigst
 *
 */
public class Station implements Linkable<TrackComponent>{

    protected TrackComponent platformStretch;
    protected TrackComponent stopStretch;
    protected static Double MAX_STATION_VELOCITY=null;
    protected static Double STOP_STRETCH_LENGTH=null;
    final static protected double MAX_STOP_VELOCITY=1e-3;
    protected static Double WAIT_TIME=null;
    private double gradient;

    final private static String SOURCE_FILE="data/track/station.ini";
    private static void loadIni() {
        if(MAX_STATION_VELOCITY==null) {
            try {
                BufferedReader sourceReader=new BufferedReader(new FileReader(SOURCE_FILE));
                String currentLine=sourceReader.readLine();
                while(currentLine!=null) {
                    String[] splitLine=currentLine.split("=");
                    if(splitLine[0].equalsIgnoreCase("maxVelocity")) {
                        MAX_STATION_VELOCITY=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("stopTrackLength")) {
                        STOP_STRETCH_LENGTH=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("waitTime")) {
                        WAIT_TIME=Double.parseDouble(splitLine[1]);
                    }
                    currentLine=sourceReader.readLine();
                }
            } catch(IOException ex) {
                System.out.println("Unable to load \"station.ini\"");
            }
        }
    }

    private Station nextStation;

    public Station(double chainage,double myGradient) {
        loadIni();
        this.gradient=myGradient;
        this.platformStretch=new TrackSimple(chainage, MAX_STATION_VELOCITY,this.gradient);
    }

    public TrackComponent getLinkTo() {
        return this.platformStretch.getLinkTo();
    }

    public void setNextLink(TrackComponent next) {
        TrackComponent nextTrack=next.getLinkTo();
        this.stopStretch=new TrackStopper(this, nextTrack.getChainage()
                -Math.signum(nextTrack.getChainage()-this.platformStretch.getChainage())
                *STOP_STRETCH_LENGTH, MAX_STATION_VELOCITY,this.gradient);
        this.platformStretch.setNextLink(this.stopStretch.getLinkTo());
        this.stopStretch.setNextLink(nextTrack);
    }

    public void setNextStation(Station next) {
        this.nextStation=next;
    }

    public Station getNextStation() {
        return this.nextStation;
    }

    public boolean arrived(Train requester) {
        return requester.getCurrentTrack()==this.platformStretch && requester.getVelocity()<=MAX_STOP_VELOCITY;
    }

    public double getWaitTime() {
        return WAIT_TIME;
    }

    protected class TrackStopper extends TrackSimple {
        private Station master;

        public TrackStopper(Station myMaster,double myChainage, double myMaxVelocity, double myGradient) {
            super(myChainage, myMaxVelocity,myGradient);
            this.master=myMaster;
        }

        @Override
        public double getMaxVelocity(Train requester) {
            if(this.master==requester.getTargetStation()) {
                return 0;
            } else {
                return super.getMaxVelocity(requester);
            }
        }
    }
}
