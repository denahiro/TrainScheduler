package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * @author koenigst
 *
 */
public class TrackSimple implements TrackComponent{

    private TrackComponent nextTrack;

    private double maxVelocity;

    private double length;

    private HashMap<Train,Double> trainEndpositions=new HashMap<Train, Double>();

    public TrackSimple(double myLength,double myVelocity)
    {
        this.length=myLength;
        this.maxVelocity=myVelocity;
    }

    public TrackComponent getNextTrack(Train requester)
    {
        return this.nextTrack;
    }

    public void setNextTrack(TrackComponent next)
    {
        this.nextTrack=next;
    }
	
    public double getMaxVelocity(Train requester)
    {
        return this.maxVelocity;
    }

    public double getLength(Train requester)
    {
        return this.length;
    }

    public double getTrainEndPosition(Train requester)
    {
        return this.trainEndpositions.get(requester);
    }

    public List<Train> getOtherTrains(Train requester)
    {
        ArrayList<Train> outputArray=new ArrayList<Train>();
        for(Map.Entry<Train,Double> entry: this.trainEndpositions.entrySet())
        {
            if(entry.getKey()!=requester)
            {
                outputArray.add(entry.getKey());
            }
        }
        return outputArray;
    }

    public void draw(Graphics g)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setMyPosition(Train requester, double position)
    {
        if(position<this.length)
        {
            this.trainEndpositions.put(requester, position);
            return true;
        }
        else
        {
            this.trainEndpositions.remove(requester);
            return false;
        }
    }
}
