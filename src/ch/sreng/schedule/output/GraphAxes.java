/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import org.freehep.graphics2d.VectorGraphics;

/**
 *
 * @author Denahiro
 */
public class GraphAxes {
    private List<Curve> curves=new ArrayList<Curve>();
    private String xLabel;
    private String yLabel;

    final public int N_TICKS=11;

    public void plot(List<Double> x, List<Double>y, Color lineColor)
    {
        this.curves.add(new Curve(x,y,lineColor));
    }

    public void plot(Curve newCurve)
    {
        this.curves.add(newCurve);
    }

    public void label(String newXLabel, String newYLabel) {
        this.xLabel=newXLabel;
        this.yLabel=newYLabel;
    }

    private BoundingBox getPlotArea(Dimension dim,Insets insets) {
        
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void draw(VectorGraphics g,Dimension dim,Insets insets)
    {
        BoundingBox dataBB=new BoundingBox();
        for(Curve c: this.curves) {
            BoundingBox tmpBB=c.getBoundingBox();
            if(tmpBB.xMin<dataBB.xMin) dataBB.xMin=tmpBB.xMin;
            if(tmpBB.xMax>dataBB.xMax) dataBB.xMax=tmpBB.xMax;
            if(tmpBB.yMin<dataBB.yMin) dataBB.yMin=tmpBB.yMin;
            if(tmpBB.yMax>dataBB.yMax) dataBB.yMax=tmpBB.yMax;
        }

        System.out.println(dataBB);
        System.out.println(Math.log10(Math.max(Math.max(Math.abs(dataBB.xMax), Math.abs(dataBB.xMin))
                ,Math.max(Math.abs(dataBB.yMax), Math.abs(dataBB.yMin)))));

        BoundingBox plotBB=this.getPlotArea(dim,insets);

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public class Curve {
        public List<Double> x;
        public List<Double> y;
        public Color lineColor;

        public Curve(List<Double> myX, List<Double> myY, Color myLineColor) {
            this.x=myX;
            this.y=myY;
            this.lineColor=myLineColor;
        }

        public BoundingBox getBoundingBox() {
            BoundingBox rBB=new BoundingBox();

            for(double c: this.x) {
                if(c<rBB.xMin) rBB.xMin=c;
                if(c>rBB.xMax) rBB.xMax=c;
            }

            for(double c: this.y) {
                if(c<rBB.yMin) rBB.yMin=c;
                if(c>rBB.yMax) rBB.yMax=c;
            }

            return rBB;
        }
    }

    public class BoundingBox {
        public double xMin;
        public double xMax;
        public double yMin;
        public double yMax;

        public BoundingBox(double nXMin,double nXMax,double nYMin,double nYMax) {
            this.xMax=nXMax;
            this.xMin=nXMin;
            this.yMax=nYMax;
            this.yMin=nYMin;
        }

        public BoundingBox() {
            this(Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY
                    ,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY);
        }

        @Override
        public String toString() {
            return "["+Double.toString(this.xMin)+","+Double.toString(this.xMax)+","
                    +Double.toString(this.yMin)+","+Double.toString(this.yMax)+"]";
        }
    }
}
