/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resourceTest;

import ch.sreng.schedule.Scheduler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Denahiro
 */
public class ResourceTest {
    public static void main(String[] args) throws IOException{
        String resourceLocation="data/track/alignmentCorridor1.csv";
        InputStream align1=Scheduler.class.getResourceAsStream(resourceLocation);
        BufferedReader myReader=new BufferedReader(new InputStreamReader(align1));
        System.out.println(myReader.readLine());
    }
}
