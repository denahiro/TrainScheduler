/**
 * 
 */
package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;



/**
 * @author koenigst
 *
 */
public class Station implements Linkable<TrackComponent>{

    protected TrackComponent platformStretch;
    protected TrackComponent stopStretch;
    final protected double MAX_STATION_VELOCITY=5;
    final protected double MAX_STOP_VELOCITY=1e-3;
    final protected double STOP_STRETCH_LENGTH=5;
    final protected double waitTime;

    private Station nextStation;

    public Station(double chainage, double myWaitTime) {
        this.platformStretch=new TrackSimple(chainage, this.MAX_STATION_VELOCITY);
        this.waitTime=myWaitTime;
    }

    public TrackComponent getLinkTo() {
        return this.platformStretch.getLinkTo();
    }

    public void setNextLink(TrackComponent next) {
        TrackComponent nextTrack=next.getLinkTo();
        this.stopStretch=new TrackStopper(this, nextTrack.getChainage()
                -Math.signum(nextTrack.getChainage()-this.platformStretch.getChainage())
                *this.STOP_STRETCH_LENGTH, this.MAX_STATION_VELOCITY);
//        System.out.println(this.stopStretch.getChainage());
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
        return requester.getCurrentTrack()==this.platformStretch && requester.getVelocity()<=this.MAX_STOP_VELOCITY;
    }

    public double getWaitTime() {
        return this.waitTime;
    }

    protected class TrackStopper extends TrackSimple {
        private Station master;

        public TrackStopper(Station myMaster,double myChainage, double myMaxVelocity) {
            super(myChainage, myMaxVelocity);
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
