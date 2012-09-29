/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resourceTest;

import ch.sreng.schedule.components.stationary.TrackFactory;

/**
 *
 * @author Denahiro
 */
public class FactoryTester {
    public static void main(String[] args) {
        TrackFactory myFactory=new TrackFactory();
        myFactory.loadFile("alignmentCorridor2.csv");
    }
}
