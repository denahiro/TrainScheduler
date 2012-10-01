/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

import ch.sreng.schedule.components.mobile.SimplePower;
import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.components.stationary.TrackFactory;
import ch.sreng.schedule.output.EnergyGraph;
import ch.sreng.schedule.output.Graph;
import ch.sreng.schedule.output.GraphPrinter;
import ch.sreng.schedule.output.InitialConditionsGraph;
import ch.sreng.schedule.output.TimePosGraph;
import ch.sreng.schedule.procedure.DriveStrategyBangBang;
import ch.sreng.schedule.procedure.SafetyStrategy;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Denahiro
 */
public class SimulationDispatcher {

    public static void run(String dispatchFilename) throws IOException {
        try {
            Document dispatchFile = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new File(dispatchFilename));

            NodeList tasks=dispatchFile.getElementsByTagName("task");
            for(int i=0;i<tasks.getLength();++i) {
                doTask(tasks.item(i));
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SimulationDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SimulationDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void doTask(Node task) throws IOException {
        if(task.getNodeType()==Node.ELEMENT_NODE) {
            Element element=(Element) task;

            double timeStep=Double.parseDouble(element.getElementsByTagName("timeStep")
                    .item(0).getFirstChild().getNodeValue());
            double headway=Double.parseDouble(element.getElementsByTagName("headway")
                    .item(0).getFirstChild().getNodeValue());
            String trackFilename=element.getElementsByTagName("trackFile").item(0).getFirstChild().getNodeValue();

            TrackFactory.TrackContainer track=TrackFactory.loadFile(trackFilename);

            TimetableMaster newMaster=new TimetableMaster(timeStep);

            initialiseTrains(newMaster,track,timeStep,headway);

            NodeList graphNodes=element.getElementsByTagName("graphs");
            List<GraphContainer> graphContainers=new ArrayList<GraphContainer>();
            for(int i=0;i<graphNodes.getLength();++i) {
                graphContainers.addAll(parseGraphs(graphNodes.item(i), newMaster));
            }

            double finalTime=Double.parseDouble(element.getElementsByTagName("finalTime").item(0).getFirstChild().getNodeValue());

            while(newMaster.getTime()<finalTime) {
                newMaster.doFrame();
            }

            for(GraphContainer c :graphContainers) {
                c.print();
            }
        }
    }

    private static List<GraphContainer> parseGraphs(Node gNode,TimetableMaster master) {
        List<GraphContainer> returnGraphs=new ArrayList<GraphContainer>();
        if(gNode.getNodeType()==Node.ELEMENT_NODE) {
            Element graphElement=(Element) gNode;
            NodeList timePosGraphNodes=graphElement.getElementsByTagName("timePosGraph");
            for(int i=0;i<timePosGraphNodes.getLength();++i) {
                Node timePosNode=timePosGraphNodes.item(i);
                if(timePosNode.getNodeType()==Node.ELEMENT_NODE) {
                    Graph tmpGraph;
                    Element timePosElement=(Element) timePosNode;
                    String units=timePosElement.getElementsByTagName("units").item(0).getChildNodes().item(0).getNodeValue();
                    if(units.equalsIgnoreCase("large")) {
                        tmpGraph=new TimePosGraph(TimePosGraph.Units.LARGE);
                    } else {
                        tmpGraph=new TimePosGraph();
                    }
                    master.addOutputGraph(tmpGraph);
                    returnGraphs.add(generateContainer(timePosElement,tmpGraph));
                }
            }
            NodeList energyGraphNodes=graphElement.getElementsByTagName("energyGraph");
            for(int i=0;i<energyGraphNodes.getLength();++i) {
                Node energyNode=energyGraphNodes.item(i);
                if(energyNode.getNodeType()==Node.ELEMENT_NODE) {
                    Graph tmpGraph;
                    Element energyElement=(Element) energyNode;
                    String units=energyElement.getElementsByTagName("units").item(0).getChildNodes().item(0).getNodeValue();
                    if(units.equalsIgnoreCase("large")) {
                        tmpGraph=new EnergyGraph(EnergyGraph.Units.LARGE);
                    } else {
                        tmpGraph=new EnergyGraph();
                    }
                    master.addOutputGraph(tmpGraph);
                    returnGraphs.add(generateContainer(energyElement,tmpGraph));
                }
            }
        }
        return returnGraphs;
    }

    private static GraphContainer generateContainer(Element myDOMElement,Graph myGraph) {
        GraphContainer newContainer=new GraphContainer(myGraph);

        NodeList dataNodes=myDOMElement.getElementsByTagName("dataOutput");
        if(dataNodes.getLength()>0) {
            newContainer.addDataFile(((Element) dataNodes.item(0)).getElementsByTagName("filename")
                    .item(0).getFirstChild().getNodeValue());
        }

        NodeList imageNodes=myDOMElement.getElementsByTagName("imageOutput");
        if(imageNodes.getLength()>0) {
            String filename=((Element) imageNodes.item(0)).getElementsByTagName("filename")
                    .item(0).getFirstChild().getNodeValue();
            int height=Integer.parseInt(((Element) imageNodes.item(0)).getElementsByTagName("height")
                    .item(0).getFirstChild().getNodeValue());
            int width=Integer.parseInt(((Element) imageNodes.item(0)).getElementsByTagName("width")
                    .item(0).getFirstChild().getNodeValue());
            Dimension dim=new Dimension(width, height);
            newContainer.addImageFile(filename,dim);
        }

        return newContainer;
    }

    private static void initialiseTrains(Master targetMaster,TrackFactory.TrackContainer track
            ,double timeStep,double headway) {

        InitialConditionsGraph initGraph=null;
        double initialVelocity=0;
        for(int i=0;i<2;++i) {
            TimetableMaster headwayMaster=new TimetableMaster(timeStep);

            Train trainDummy=new Train(new DriveStrategyBangBang(), new SafetyStrategy(), new SimplePower(), Color.RED);
            trainDummy.setInitialConditions(track.getFirstTrack(), track.getFirstStation(), 0, initialVelocity);
            headwayMaster.registerTrain(trainDummy);

            initGraph=new InitialConditionsGraph(headway);
            headwayMaster.addOutputGraph(initGraph);

            TrackComponent lastTrack=track.getFirstTrack();
            while(trainDummy.getCurrentTrack()!=track.getFirstTrack() || lastTrack==track.getFirstTrack()) {
                lastTrack=trainDummy.getCurrentTrack();
                headwayMaster.doFrame();
            }

            initialVelocity=initGraph.getLastVelocity();

            trainDummy.remove();
        }

        initGraph.initialiseTrains(targetMaster);
    }

    private static class GraphContainer {

        private Graph graph;

        private String dataFile=null;
        private String imageFile=null;
        private Dimension imageDim=null;

        public GraphContainer(Graph myGraph) {
            this.graph=myGraph;
        }

        public void addDataFile(String filename) {
            this.dataFile=filename;
        }

        public void addImageFile(String filename,Dimension myDimension) {
            this.imageFile=filename;
            this.imageDim=myDimension;
        }

        public void print() throws FileNotFoundException {
            if(this.dataFile!=null) {
                File tmpFile=new File(this.dataFile);
                tmpFile.getParentFile().mkdirs();
                PrintWriter output = new PrintWriter(tmpFile);
                this.graph.saveToWriter(output);
                output.close();
            }

            if(this.imageFile!=null) {
                File tmpFile=new File(this.imageFile);
                tmpFile.getParentFile().mkdirs();
                GraphPrinter.print(this.graph,tmpFile,this.imageDim);
            }
        }
    }
}
