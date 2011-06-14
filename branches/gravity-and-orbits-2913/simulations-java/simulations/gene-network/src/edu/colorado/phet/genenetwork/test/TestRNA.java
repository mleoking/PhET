// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

public class TestRNA {
    private JFrame frame;

    public TestRNA() {
        frame = new JFrame("Test RNA");
        frame.setContentPane(new TestRNAContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024,768);
    }

    public static void main(String[] args) {
        new TestRNA().start();
    }

    private void start() {
        frame.setVisible(true);
    }

    public static class RNA{
        private int x;
        private int y;
        private double length;

        public RNA(int x,int y,double length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }

        public Shape getShape() {
            GeneralPath generalPath=new GeneralPath();
            Point2D.Float currentPoint = new Point2D.Float(x,y);
            generalPath.moveTo(currentPoint.x, currentPoint.y);
            Random random=new Random();

            float segmentLength = 5;
            int numSegments = (int) (length/segmentLength);
            for (int i=0;i<numSegments;i++){
                float dx = segmentLength/3;
                float dy1 = (random.nextFloat()-0.5f)*2*segmentLength/3;
                Point2D.Float cp1 =new Point2D.Float(currentPoint.x+dx,currentPoint.y+dy1);

                float dx2 = segmentLength*2/3;
                float dy2 = (random.nextFloat()-0.5f)*2*segmentLength/3;
                Point2D.Float cp2 =new Point2D.Float(currentPoint.x+dx2,currentPoint.y+dy2);

                float dy3 = (random.nextFloat()-0.5f)*2*segmentLength/3;
                Point2D.Float dest = new Point2D.Float(currentPoint.x+segmentLength,currentPoint.y+dy3);

                generalPath.curveTo(cp1.x,cp1.y,cp2.x,cp2.y,dest.x,dest.y);
                currentPoint = dest;
            }
            return generalPath;
        }
    }

    private static class TestRNAContentPane extends PCanvas {
        private TestRNAContentPane() {
            Random r =new Random();
            for (int i=0;i<100;i++){
                getCamera().addChild(new RNANode(new RNA(r.nextInt(800),r.nextInt(600),400 )));
            }
        }
    }

    private static class RNANode extends PNode {
        private PhetPPath pPath;

        public RNANode(RNA rna) {

            pPath = new PhetPPath(new BasicStroke(4),Color.black );
            addChild(pPath);

            pPath.setPathTo(rna.getShape());
        }
    }
}
