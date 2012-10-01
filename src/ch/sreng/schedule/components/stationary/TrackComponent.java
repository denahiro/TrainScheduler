/**
 * 
 */
package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;
import java.util.List;

/**
 * @author koenigst
 *
 */
public interface TrackComponent extends Linkable<TrackComponent>{
	
    public abstract double getMaxVelocity(Train requester);

    public abstract TrackComponent getNextTrack(Train requester);

    public abstract void draw(java.awt.Graphics g);

    public abstract double getLength(Train requester);

    public abstract double getChainage();

    public abstract double getTrainEndPosition(Train requester);
    
    public abstract double getAbsoluteTrainEndPosition(Train requester);

    public abstract List<Train> getOtherTrains(Train requester);

    public abstract boolean setTrainPosition(Train requester, double position);

    public abstract void removeTrain(Train requester);
}
