/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * @author John Blanco
 */
public class JmolTest {

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main(String[] args) {

        SmarterJmolAdapter smarterJmolAdapter = new SmarterJmolAdapter();

        // Create the canvas.
        final PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground(Color.BLACK);


        final Dimension currentSize = new Dimension();
        final Rectangle rectClip = new Rectangle();

        // Create the JMOL viewer.
        final JmolViewer viewer = JmolViewer.allocateViewer(canvas, smarterJmolAdapter, null, null, null, "load file:/C:/temp/jmol/co2.nbo.log filter \"NBOCharges MO\";frame 2; connect (all) (all) delete;connect (atomno=1) (atomno=2) 2;connect (atomno=1) (atomno=3) 2; ;set echo bottom center; font echo 20 sanserif bold;select *;color bonds [174,174,174];color echo grey; center;move 90  120 -90 0 0 0 0 0 2;echo Carbon Dioxide", null);
        viewer.openFile("C:\\temp\\jmol\\co2.nbo.log");
        viewer.evalString("select *; spacefill on;");
        PNode jmolViewerNode = new PNode() {
            protected void paint(final PPaintContext paintContext) {
                super.paint(paintContext);

                viewer.setScreenDimension(canvas.getSize(currentSize));
                paintContext.getGraphics().getClipBounds(rectClip);
                viewer.renderScreenImage(paintContext.getGraphics(), currentSize, rectClip);
            }
        };
        jmolViewerNode.setBounds(0, 0, 400, 400);
        canvas.addScreenChild(jmolViewerNode);

        // Create a non-transformed test node.
        PNode nonMvtTestNode = new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.yellow);

        canvas.addWorldChild(nonMvtTestNode);

        // Create the frame and put the canvas in it.
        JFrame frame = new JFrame();
        frame.setSize(1024, 768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(canvas);
        frame.setVisible(true);
    }
}
