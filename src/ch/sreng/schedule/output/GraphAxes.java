/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.freehep.graphics2d.VectorGraphics;

/**
 *
 * @author Denahiro
 */
public class GraphAxes {
    private List<Curve> curves=new ArrayList<Curve>();
    private String xLabel=new String();
    private String yLabel=new String();
    private String axisTitle=new String();

    final protected int N_TICKS=11;
    final protected int BOTTOM_GAP=30;
    final protected int TOP_GAP=30;
    final protected int LEFT_GAP=50;
    final protected int RIGHT_GAP=50;
    final protected double CURVE_LINE_WIDTH=1;
    final protected double AXIS_LINE_WIDTH=0.5;
    final protected int TICK_LENGTH=5;
    final protected int TICK_TO_LABEL_GAP=10;
    final protected Font LABEL_FONT=new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    final protected Font TITLE_FONT=new Font(Font.SANS_SERIF, Font.BOLD, 10);

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

    public void title(String newTitle) {
        this.axisTitle=newTitle;
    }

    private BoundingBox getPlotArea(Dimension dim,Insets insets) {
        BoundingBox pa=new BoundingBox();

        //top left corner of panel is xMin, yMax
        pa.yMin=dim.height-insets.top-insets.bottom-this.BOTTOM_GAP;
        pa.yMax=insets.top+this.TOP_GAP;
        pa.xMin=insets.left+this.LEFT_GAP;
        pa.xMax=dim.width-insets.left-insets.right-this.RIGHT_GAP;

        return pa;
    }

    private List<Double> getTicks(double max,double min) {
        double exponent=Math.floor(Math.log10(max-min));
        double coefficient=(max-min)/Math.pow(10,exponent);

        if(coefficient<4) {
            exponent-=1;
            coefficient*=10;
        }

        double newMin=Math.floor(min/Math.pow(10, exponent));
        double newMax=Math.ceil(max/Math.pow(10, exponent));

        long tickCount=Math.round(newMax-newMin);

        while(tickCount>10) {
            if(tickCount%2==0) {
                tickCount/=2;
            } else if(tickCount%5==0) {
                tickCount/=5;
            } else {
                break;
            }
        }

        newMin*=Math.pow(10, exponent);
        newMax*=Math.pow(10, exponent);

        List<Double> returnList=new ArrayList<Double>();
        returnList.add(newMin);
        double tickLength=(newMax-newMin)/tickCount;
        for(long i=1;i<=tickCount;++i) {
            returnList.add(tickLength*i+newMin);
        }
        return returnList;
    }

    public void draw(VectorGraphics g,Dimension dim,Insets insets)
    {
        BoundingBox dataBB=new BoundingBox();

        g.setLineWidth(this.CURVE_LINE_WIDTH);

        for(Curve c: this.curves) {
            BoundingBox tmpBB=c.getBoundingBox();
            if(tmpBB.xMin<dataBB.xMin) dataBB.xMin=tmpBB.xMin;
            if(tmpBB.xMax>dataBB.xMax) dataBB.xMax=tmpBB.xMax;
            if(tmpBB.yMin<dataBB.yMin) dataBB.yMin=tmpBB.yMin;
            if(tmpBB.yMax>dataBB.yMax) dataBB.yMax=tmpBB.yMax;
        }

        BoundingBox plotBB=this.getPlotArea(dim,insets);

        g.setClip(plotBB.xMin, plotBB.yMax, plotBB.xMax-plotBB.xMin, plotBB.yMin-plotBB.yMax);

        for(Curve c: this.curves) {
            c.draw(g,plotBB,dataBB);
        }

        g.setClip(insets.left, insets.top, dim.width-insets.left-insets.right, dim.height-insets.bottom-insets.top);

        g.setLineWidth(this.AXIS_LINE_WIDTH);
        g.setColor(Color.BLACK);
        g.drawRect(plotBB.xMin, plotBB.yMax, plotBB.xMax-plotBB.xMin, plotBB.yMin-plotBB.yMax);

        List<Double> xTicks=this.getTicks(dataBB.xMax,dataBB.xMin);
        List<Double> yTicks=this.getTicks(dataBB.yMax,dataBB.yMin);
        Transform xTrans=new Transform(dataBB.xMax, dataBB.xMin, plotBB.xMax, plotBB.xMin);
        Transform yTrans=new Transform(dataBB.yMax, dataBB.yMin, plotBB.yMax, plotBB.yMin);
        g.setFont(this.LABEL_FONT);
        FontMetrics fm=g.getFontMetrics();
        for(Double pos: xTicks) {
            double tickPos=xTrans.doTransform(pos);
            String tickString;
            if(pos<=dataBB.xMax) {
                if(pos==0 || pos>=1) {
                    tickString=Integer.toString((int)Math.round(pos));
                } else {
                    tickString=Double.toString(pos);
                }
                g.drawLine(tickPos, plotBB.yMin, tickPos, plotBB.yMin-this.TICK_LENGTH);
                g.drawString(tickString, tickPos-fm.stringWidth(tickString)/2, plotBB.yMin+fm.getMaxAscent());
            }
        }
        for(Double pos: yTicks) {
            double tickPos=yTrans.doTransform(pos);
            String tickString;
            if(pos<=dataBB.yMax) {
                if(pos==0 || pos>=1) {
                    tickString=Integer.toString((int)Math.round(pos));
                } else {
                    tickString=Double.toString(pos);
                }
                g.drawLine(plotBB.xMin, tickPos, plotBB.xMin+this.TICK_LENGTH, tickPos);
                g.drawString(tickString, plotBB.xMin-fm.stringWidth(tickString), tickPos+3);
            }
        }

        g.drawString(this.xLabel, (plotBB.xMax+plotBB.xMin)/2-fm.stringWidth(this.xLabel)/2
                , plotBB.yMin+fm.getHeight()+fm.getMaxDescent()+this.TICK_TO_LABEL_GAP);
        AffineTransform standard=g.getTransform();
        AffineTransform newTrans=new AffineTransform(standard);
        newTrans.setToRotation(-Math.PI/2);
        g.setTransform(newTrans);
        g.drawString(this.yLabel, -(plotBB.yMax+plotBB.yMin)/2-fm.stringWidth(this.yLabel)/2,5+fm.getMaxAscent());
        g.setTransform(standard);

        g.setFont(this.TITLE_FONT);
        fm=g.getFontMetrics();
        g.drawString(this.axisTitle, (plotBB.xMax+plotBB.xMin)/2-fm.stringWidth(this.axisTitle)/2
                , plotBB.yMax-fm.getMaxDescent()-this.TICK_TO_LABEL_GAP);
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

        public void draw(VectorGraphics g,BoundingBox plotBB,BoundingBox dataBB) {
            double[] xA=new double[this.x.size()];
            double[] yA=new double[this.y.size()];
            ListIterator<Double> itX=this.x.listIterator();
            ListIterator<Double> itY=this.y.listIterator();
            int currentIndex=0;
            Transform xTrans=new Transform(dataBB.xMax, dataBB.xMin, plotBB.xMax, plotBB.xMin);
            Transform yTrans=new Transform(dataBB.yMax, dataBB.yMin, plotBB.yMax, plotBB.yMin);
            while(itX.hasNext() && itY.hasNext()) {
                double cX=itX.next();
                double cY=itY.next();
                xA[currentIndex]=xTrans.doTransform(cX);
                yA[currentIndex]=yTrans.doTransform(cY);
                ++currentIndex;
            }
            g.setColor(this.lineColor);
            g.drawPolyline(xA, yA, xA.length);
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

    public class Transform {
        public double aStart;
        public double aLength;
        public double bStart;
        public double bLength;

        public Transform(double aMax,double aMin,double bMax, double bMin) {
            this.aStart=aMin;
            this.aLength=aMax-aMin;
            this.bStart=bMin;
            this.bLength=bMax-bMin;
        }

        public double doTransform(double input) {
            return ((input-this.aStart)/this.aLength)*this.bLength+this.bStart;
        }
    }
}
