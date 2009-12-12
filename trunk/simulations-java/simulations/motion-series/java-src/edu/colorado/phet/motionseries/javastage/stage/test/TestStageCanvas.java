/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.motionseries.javastage.stage.StageCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class TestStageCanvas {
    private JFrame frame = new JFrame();
    private PhetPPath rectNode;
    private MyPText stageText;
    private StageCanvas canvas;

    public void updateRectNodeLocation() {
        Rectangle2D rectNodeBounds = rectNode.globalToLocal(stageText.getGlobalFullBounds());
        rectNodeBounds = rectNode.localToParent(rectNodeBounds);
        rectNode.setOffset(rectNodeBounds.getCenterX() - rectNode.getFullBounds().getWidth() / 2, rectNodeBounds.getMaxY());
    }

    public TestStageCanvas() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        SwingUtils.centerWindowOnScreen(frame);

        double stageWidth = 200;
        double stageHeight = 100;
        Rectangle.Double modelBounds = new Rectangle2D.Double(0, 0, 2E-6, 1E-6);
        canvas = new StageCanvas(stageWidth, stageHeight, modelBounds);
        canvas.addScreenNode(new MyPText("Hello from screen at 50,50", 50, 50));
        stageText = new MyPText("Hello from Stage at 100,50", 100, 50, 0.5);
        canvas.addStageNode(stageText);
        canvas.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageHeight), new BasicStroke(2), Color.yellow));
        canvas.addScreenNode(new MyPText("Hello from screen at 100,100", 100, 100));
        canvas.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5E-6, 0.5E-6), Color.blue));
        canvas.addModelNode(new MyPText("hello from left edge of world bounds", modelBounds.getMinX(), modelBounds.getCenterY(), 1E-6 / 100));

        //center one node beneath another, though they be in different coordinate frames
        rectNode = new PhetPPath(new Rectangle2D.Double(0, 0, 50, 10), Color.red);
        canvas.addScreenNode(rectNode);

        updateRectNodeLocation();
        //coordinates can change, so need to update when they do
        canvas.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateRectNodeLocation();
            }
        });

        //todo: compute stage bounds dynamically, based on contents of the stage
        //todo: maybe stage bounds should be mutable, since it is preferable to create the nodes as children of the canvas

        //todo: how to implement pan/zoom with this paradigm?  This would probably entail changing the ModelViewTransform2D's model bounds (i.e. viewport).
        //    canvas.setStageBounds(200,200)

        Timer timer = new Timer(15, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.panModelViewport(-1E-8 / 4, -1E-8 / 4);
            }
        });
        timer.start();

        frame.setContentPane(canvas);
        canvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                canvas.toggleDebugs();
            }
        });
        canvas.setPanEventHandler(null);
        canvas.setZoomEventHandler(null);
    }

    private void start() {
        frame.setVisible(true);
        canvas.requestFocus();
    }

    public static void main(String[] args) {
        new TestStageCanvas().start();
    }
}
