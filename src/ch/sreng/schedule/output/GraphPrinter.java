/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JPanel;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.swing.Headless;

/**
 *
 * @author Denahiro
 */
public class GraphPrinter{


    public void print(Graph input,File output,Dimension outputDimension) throws FileNotFoundException {
        JPanel yourPanel = new TempPanel(input,outputDimension);

        // run with -Djava.awt.headless=true
        Headless headless = new Headless(yourPanel);
        headless.pack();
        headless.setVisible(true);
        
        VectorGraphics graphics = new EMFGraphics2D(output, yourPanel);
        graphics.startExport();
        yourPanel.print(graphics);
        graphics.endExport();
    }


    private class TempPanel extends JPanel {
        private Graph myGraph;
        public TempPanel(Graph toPrint,Dimension panelDimension) {
            this.myGraph=toPrint;
            setPreferredSize(panelDimension);
        }

        @Override
        public void paintComponent(Graphics g) {

            if (g == null) return;

            VectorGraphics vg = VectorGraphics.create(g);

            Dimension dim = getSize();
            Insets insets = getInsets();

            //Colour background
            vg.setColor(Color.white);
            vg.fillRect(insets.left, insets.top,
                dim.width-insets.left-insets.right,
                dim.height-insets.top-insets.bottom);
            vg.setColor(Color.black);

            System.out.println(dim);
            System.out.println(insets);

            this.myGraph.draw(vg,dim,insets);
        }
    }

}
