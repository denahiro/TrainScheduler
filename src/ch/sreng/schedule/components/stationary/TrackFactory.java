/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.stationary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Denahiro
 */
public class TrackFactory {

    final static private String dataFolder="data/track/";

    private static BufferedReader getAlignmentReader(String filename) throws FileNotFoundException {
        return new BufferedReader(new FileReader(dataFolder+filename));
    }

    private static List<List<String>> readStrings(BufferedReader myReader) throws IOException {
        List<List<String>> outList=new ArrayList<List<String>>();

        //Read and dump first header
        myReader.readLine();
        //Read and dump second header
        myReader.readLine();
        String currentLine=myReader.readLine();
        while(currentLine!=null) {
            List<String> splitLine=Arrays.asList(currentLine.split(";"));
            if(splitLine.size()>0) {
                outList.add(splitLine);
            }
            currentLine=myReader.readLine();
        }

        return outList;
    }

    private static Segment parseHorizontalSegment(ListIterator<String> lineIt) {
        Segment out=new Segment();
        
        try {
            out.beginChainage=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}
        try {
            out.endChainage=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}
        lineIt.next();
        out.stationName=lineIt.next();
        if(out.stationName.isEmpty()) out.stationName=null;
        if(lineIt.hasNext()) lineIt.next();
        if(lineIt.hasNext()) lineIt.next();
        try {
            if(lineIt.hasNext()) out.maxVelocity=Double.parseDouble(lineIt.next())/3.6;
        } catch(NumberFormatException ex) {}
        if(lineIt.hasNext()) lineIt.next();

        return out;
    }
    
    private static Segment parseVerticalSegment(ListIterator<String> lineIt) {
        Segment out=new Segment();

        try {
            out.beginChainage=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}
        try {
            out.endChainage=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}
        if(lineIt.hasNext()) lineIt.next();
        if(lineIt.hasNext()) lineIt.next();
        try {
            if(lineIt.hasNext()) out.gradient=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}

        return out;
    }

    private static List<Segment> interleaveSegments(List<Segment> horizontal,List<Segment> vertical) {
        List<Segment> out=new ArrayList<Segment>();

        ListIterator<Segment> horIt=horizontal.listIterator();
        ListIterator<Segment> vertIt=vertical.listIterator();
        Segment prevHor=null;
        Segment prevVert=null;
        Segment currentHor=horIt.next();
        Segment currentVert=vertIt.next();
        Segment nextHor=horIt.next();
        Segment nextVert=vertIt.next();

        while(currentHor!=null && currentVert!=null) {
            Segment newSegment=new Segment();
            double tmpBegin=Double.NEGATIVE_INFINITY;
            if(currentHor.beginChainage!=null) {
                if(tmpBegin<currentHor.beginChainage) {
                    tmpBegin=currentHor.beginChainage;
                }
            } else if (tmpBegin<prevHor.endChainage) {
                tmpBegin=prevHor.endChainage;
            }
            if(tmpBegin<currentVert.beginChainage) {
                tmpBegin=currentVert.beginChainage;
            }
            newSegment.beginChainage=tmpBegin;

            double tmpEnd=Double.POSITIVE_INFINITY;
            if(currentHor.endChainage!=null) {
                if(tmpEnd>currentHor.endChainage) {
                    tmpEnd=currentHor.endChainage;
                }
            } else if (tmpEnd>nextHor.beginChainage) {
                tmpEnd=nextHor.beginChainage;
            }
            if(tmpEnd>currentVert.endChainage) {
                tmpEnd=currentVert.endChainage;
            }
            newSegment.endChainage=tmpEnd;

            //Get the maximum velocity
            if(currentHor.maxVelocity!=null) {
                newSegment.maxVelocity=currentHor.maxVelocity;
            } else {
                double tmpVelocity=0;
                if(prevHor!=null && prevHor.maxVelocity!=null && tmpVelocity<prevHor.maxVelocity) {
                    tmpVelocity=prevHor.maxVelocity;
                }
                if(nextHor!=null && nextHor.maxVelocity!=null && tmpVelocity<nextHor.maxVelocity) {
                    tmpVelocity=nextHor.maxVelocity;
                }
                newSegment.maxVelocity=tmpVelocity;
            }

            //Get the gradient
            if(currentVert.gradient!=null) {
                newSegment.gradient=currentVert.gradient;
            } else {
                newSegment.gradient=(prevVert.gradient+nextVert.gradient)/2;
            }

            //Get the station name
            newSegment.stationName=currentHor.stationName;

            //Add to the output
            out.add(newSegment);

            //Advance
            if((currentHor.endChainage==null && nextHor.beginChainage.equals(newSegment.endChainage))
                    || (currentHor.endChainage!=null && currentHor.endChainage.equals(newSegment.endChainage))) {
                prevHor=currentHor;
                currentHor=nextHor;
                if(horIt.hasNext()) {
                    nextHor=horIt.next();
                } else {
                    nextHor=null;
                }
            }
            if((currentVert.endChainage==null && nextVert.beginChainage.equals(newSegment.endChainage))
                    || (currentVert.endChainage!=null && currentVert.endChainage.equals(newSegment.endChainage))) {
                prevVert=currentVert;
                currentVert=nextVert;
                if(vertIt.hasNext()) {
                    nextVert=vertIt.next();
                } else {
                    nextVert=null;
                }
            }
        }
        return out;
    }

    private static List<Segment> parseAllSegments(String filename) throws IOException {
        List<List<String>> data=readStrings(getAlignmentReader(filename));

        List<Segment> horizontalSegments=new ArrayList<Segment>();
        List<Segment> verticalSegments=new ArrayList<Segment>();
        for(List<String> line: data) {
            ListIterator<String> lineIt=line.listIterator();
            horizontalSegments.add(parseHorizontalSegment(lineIt));
            if(lineIt.hasNext()) verticalSegments.add(parseVerticalSegment(lineIt));
        }

        List<Segment> combined=interleaveSegments(horizontalSegments,verticalSegments);

        return combined;
    }

    private static TrackContainer convertToTracks(List<Segment> segments) {
        List<Station> myStations=new ArrayList<Station>();
        List<Linkable<TrackComponent>> myLinkables=new ArrayList<Linkable<TrackComponent>>();

        ListIterator<Segment> it=segments.listIterator();

        while(it.hasNext()) {
            Segment currentSegment=it.next();
            if(currentSegment.stationName==null) {
                myLinkables.add(new TrackSimple(currentSegment.beginChainage
                        ,currentSegment.maxVelocity, currentSegment.gradient));
            } else {
                Station tmpStation=new Station(currentSegment.beginChainage, currentSegment.gradient);
                myStations.add(tmpStation);
                myLinkables.add(tmpStation);
            }
        }

        while(it.hasPrevious()) {
            Segment currentSegment=it.previous();
            if(currentSegment.stationName==null) {
                myLinkables.add(new TrackSimple(currentSegment.endChainage
                        ,currentSegment.maxVelocity, currentSegment.gradient));
            } else {
                Station tmpStation=new Station(currentSegment.endChainage, currentSegment.gradient);
                myStations.add(tmpStation);
                myLinkables.add(tmpStation);
            }
        }

        ListIterator<Linkable<TrackComponent>> linker=myLinkables.listIterator();
        Linkable<TrackComponent> lastLinkable=linker.next();
        while(linker.hasNext()) {
            Linkable<TrackComponent> currentLinkable=linker.next();
            lastLinkable.setNextLink(currentLinkable.getLinkTo());
            lastLinkable=currentLinkable;
        }
        lastLinkable.setNextLink(myLinkables.get(0).getLinkTo());

        ListIterator<Station> linkerStations=myStations.listIterator();
        Station lastStation=linkerStations.next();
        while(linkerStations.hasNext()) {
            Station currentStation=linkerStations.next();
            lastStation.setNextStation(currentStation);
            lastStation=currentStation;
        }
        lastStation.setNextStation(myStations.get(0));

        return new TrackContainer(myLinkables.get(0).getLinkTo(), myStations.get(0));
    }

    public static TrackContainer loadFile(String filename) throws IOException {
        List<Segment> segments = parseAllSegments(filename);
        TrackContainer out=convertToTracks(segments);
        return out;
    }

    public static class TrackContainer {
        private TrackComponent firstTrack;
        private Station firstStation;

        public TrackContainer(TrackComponent myFirstTrack, Station myFirstStation) {
            this.firstStation=myFirstStation;
            this.firstTrack=myFirstTrack;
        }

        public TrackComponent getFirstTrack(){
            return this.firstTrack;
        }

        public Station getFirstStation(){
            return this.firstStation;
        }
    }

    private static class Segment {
        public Double beginChainage;
        public Double endChainage;
        public Double maxVelocity;
        public Double gradient;
        public String stationName;

        @Override
        public String toString() {
            return "beginChainage: "+(this.beginChainage==null?"null":Double.toString(this.beginChainage))
                    +";endChainage: "+(this.endChainage==null?"null":Double.toString(this.endChainage))
                    +";maxVelocity: "+(this.maxVelocity==null?"null":Double.toString(this.maxVelocity))
                    +";gradient: "+(this.gradient==null?"null":Double.toString(this.gradient))
                    +";stationName: "+this.stationName;
        }
    }
}
