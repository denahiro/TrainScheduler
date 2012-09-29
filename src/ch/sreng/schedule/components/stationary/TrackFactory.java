/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.Scheduler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Denahiro
 */
public class TrackFactory {

    final private String dataFolder="data/track/";

    private BufferedReader getAlignmentReader(String filename) {
        return new BufferedReader(new InputStreamReader(Scheduler.class.getResourceAsStream(this.dataFolder+filename)));
    }

    private List<List<String>> readStrings(BufferedReader myReader) throws IOException {
        List<List<String>> outList=new ArrayList<List<String>>();

        //Read and dump first header
        myReader.readLine();
        //Read and dump second header
        myReader.readLine();
        String currentLine=myReader.readLine();
        while(currentLine!=null) {
            outList.add(Arrays.asList(currentLine.split(";")));
            currentLine=myReader.readLine();
        }

        return outList;
    }

    private Segment parseHorizontalSegment(List<String> line) {
        Segment out=new Segment();

        ListIterator<String> lineIt=line.listIterator();

        try {
            out.beginChainage=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}
        try {
            out.endChainage=Double.parseDouble(lineIt.next());
        } catch(NumberFormatException ex) {}
        if(lineIt.hasNext()) lineIt.next();
        if(lineIt.hasNext()) out.stationName=lineIt.next();
        if(lineIt.hasNext()) lineIt.next();
        if(lineIt.hasNext()) lineIt.next();
        try {
            if(lineIt.hasNext()) out.maxVelocity=Double.parseDouble(lineIt.next())/3.6;
        } catch(NumberFormatException ex) {}

        System.out.println(out);
        return out;
    }
    private Segment parseVerticalSegment(List<String> line) {
        if(line.size()<9) {
            return null;
        } else {
            Segment out=new Segment();

            ListIterator<String> lineIt=line.listIterator(8);

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

            System.out.println(out);
            return out;
        }
    }

    private List<Segment> parseAllSegments(String filename) throws IOException {
        List<List<String>> data=this.readStrings(this.getAlignmentReader(filename));

        List<Segment> horizontalSegments=new ArrayList<Segment>();
        List<Segment> verticalSegments=new ArrayList<Segment>();
        for(List<String> line: data) {
            horizontalSegments.add(this.parseHorizontalSegment(line));
            Segment tmpVertSeg=this.parseVerticalSegment(line);
            if(tmpVertSeg!=null) verticalSegments.add(tmpVertSeg);
        }


        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void loadFile(String filename) {
        try {
            List<Segment> segments = this.parseAllSegments(filename);
        } catch (IOException ex) {
            System.out.println("Unable to open file \""+filename+"\".");
        }
    }

    private class Segment {
        public Double beginChainage;
        public Double endChainage;
        public Double maxVelocity;
        public Double gradient;
        public String stationName;

        @Override
        public String toString() {
            return "beginChainage: "+(this.beginChainage==null?"Null":Double.toString(this.beginChainage))
                    +";endChainage: "+(this.endChainage==null?"Null":Double.toString(this.endChainage))
                    +";maxVelocity: "+(this.maxVelocity==null?"Null":Double.toString(this.maxVelocity))
                    +";gradient: "+(this.gradient==null?"Null":Double.toString(this.gradient))
                    +";stationName: "+this.stationName;
        }
    }
}
