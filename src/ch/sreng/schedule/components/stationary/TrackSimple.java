package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.Scheduler;
import ch.sreng.schedule.components.mobile.Train;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * @author koenigst
 *
 */
public class TrackSimple implements TrackComponent{

    private TrackComponent nextTrack;
    private double maxVelocity;
    private double gradient;
    private double length;
    private double chainage;
    
    private HashMap<Train,Double> trainEndpositions=new HashMap<Train, Double>();

    private static TreeMap<Double,Double> gradientMaxVelocity=null;
    final private static String SOURCE_FILE="data/track/gradientLimit.csv";
    private void limitVelocity() {
        if(gradientMaxVelocity==null) {
            try {
                    gradientMaxVelocity=new TreeMap<Double, Double>();
                    BufferedReader sourceReader=new BufferedReader(new FileReader(SOURCE_FILE));
                    String currentLine=sourceReader.readLine();
                    while(currentLine!=null) {
                        String[] splitLine=currentLine.split(";");
                        gradientMaxVelocity.put(Double.parseDouble(splitLine[0]),Double.parseDouble(splitLine[1]));
                        currentLine=sourceReader.readLine();
                    }
            } catch(IOException ex) {
                System.out.println("Unable to load \"trackSimple.csv\"");
            }
        }
        for(Double key: gradientMaxVelocity.keySet()) {
            if(key<this.gradient && gradientMaxVelocity.get(key)<this.maxVelocity) {
                this.maxVelocity=gradientMaxVelocity.get(key);
            }
        }
    }

    public TrackSimple(double myChainage,double myVelocity,double myGradient)
    {
        this.gradient=myGradient;
        this.chainage=myChainage;
        this.maxVelocity=myVelocity;
        this.limitVelocity();
    }

    public TrackComponent getNextTrack(Train requester)
    {
        return this.nextTrack;
    }

    public TrackComponent getLinkTo() {
        return this;
    }

    public void setNextLink(TrackComponent next) {
        this.nextTrack=next;
        this.length=Math.abs(this.chainage-this.nextTrack.getChainage());
    }
	
    public double getMaxVelocity(Train requester)
    {
        return this.maxVelocity;
    }

    public double getLength(Train requester)
    {
        return this.length;
    }

    public double getChainage() {
        return this.chainage;
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

    public boolean setTrainPosition(Train requester, double position)
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

    public void removeTrain(Train requester){
        this.trainEndpositions.remove(requester);
    }

    public double getAbsoluteTrainEndPosition(Train requester) {
        if(this.nextTrack.getChainage()>this.chainage) {
            return this.chainage+this.getTrainEndPosition(requester);
        } else {
            return this.chainage-this.getTrainEndPosition(requester);
        }
    }
}
