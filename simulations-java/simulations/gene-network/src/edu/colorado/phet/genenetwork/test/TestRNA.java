package edu.colorado.phet.genenetwork.test;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

public class TestRNA {
    private JFrame frame;

    public TestRNA() {
        frame = new JFrame("Test RNA");
        frame.setContentPane(new TestRNAContentPane(new RNA()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024,768);
    }

    public static void main(String[] args) {
        new TestRNA().start();
    }

    private void start() {
        frame.setVisible(true);
    }

    static class RNA{
        public Shape getShape() {
            GeneralPath generalPath=new GeneralPath();
            Point2D.Float currentPoint = new Point2D.Float(400,400);
            generalPath.moveTo(currentPoint.x, currentPoint.y);
            Random random=new Random();
            int numSegments = 20;
            float segmentLength = 20;
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
        private TestRNAContentPane(RNA rna) {
            RNANode rnaNode=new RNANode(rna);
            getCamera().addChild(rnaNode);
        }
    }

    private static class RNANode extends PNode {
        private RNA rna;
        private PhetPPath pPath;

        public RNANode(RNA rna) {
            this.rna=rna;

            pPath = new PhetPPath(new BasicStroke(4),Color.black );
            addChild(pPath);

            pPath.setPathTo(rna.getShape());
        }
    }
}
