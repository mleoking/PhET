/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.motionseries.javastage.stage.StageCanvas;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Provides a demonstration of the StageCanvas class and its supporting classes.
 *
 * @author Sam Reid
 */
public class TestStageCanvas {
    private final JFrame frame = new JFrame();
    private final StageCanvas canvas;

    public TestStageCanvas() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        SwingUtils.centerWindowOnScreen(frame);

        double stageWidth = 200;
        double stageHeight = 100;
        Rectangle.Double modelBounds = new Rectangle2D.Double(0, 0, 2E-6, 1E-6);
        canvas = new StageCanvas(stageWidth, stageHeight, modelBounds);

        //Add nodes for depicting grids in each coordinate frame.
        GridNode screenGridNode = new GridNode(1f, 0, 100, 100);
        canvas.addScreenNode(screenGridNode);
        GridNode stageGridNode = new GridNode(1f, 0, 0, 10, 10, 20, 10);
        canvas.addStageNode(stageGridNode);
        GridNode modelGridNode = new GridNode(1E-6 / 600f, modelBounds.getX(), modelBounds.getY(), modelBounds.getWidth() / 11.0, modelBounds.getHeight() / 11.0, 11, 11);
        canvas.addModelNode(modelGridNode);

        //Add a node in screen coordinates at 50,50
        canvas.addScreenNode(new PositionedTextNode("Screen at 50,50", 50, 50));

        //Add a node to the stage coordinates at 100,50
        final PositionedTextNode stageTextNode = new PositionedTextNode("Stage at 100,50", 100, 50, 0.5);
        canvas.addStageNode(stageTextNode);

        //Add a yellow rectangle that shows the stage bounds.
        canvas.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageHeight), new BasicStroke(2), Color.yellow));

        //Show a text node at 100,100 in screen coordinates
        canvas.addScreenNode(new PositionedTextNode("Screen at 100,100", 100, 100));

        //Shows a blue rectangle in model coordinates.
        canvas.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5E-6, 0.5E-6), Color.blue));

        //Shows a text node at the left edge of world bounds
        canvas.addModelNode(new PositionedTextNode("hello from left edge of world bounds", modelBounds.getMinX(), modelBounds.getCenterY(), 1E-6 / 400));

        //Shows a text node at the center of model bounds.
        canvas.addModelNode(new PositionedTextNode("hello from center of model bounds", modelBounds.getCenterX(), modelBounds.getCenterY(), 1E-6 / 400));

        //center one node beneath another, though they be in different coordinate frames
        final PhetPPath redRectangleNode = new PhetPPath(new Rectangle2D.Double(0, 0, 50, 10), Color.red);
        canvas.addScreenNode(redRectangleNode);

        //This is a closure for updating the rectangle bounds.
        final Runnable updateRedRectangleBounds = new Runnable() {
            public void run() {
                Rectangle2D stageTextNodeBoundsInScreenCoordinates = canvas.stageToScreen(stageTextNode.getFullBounds());
                redRectangleNode.setOffset(stageTextNodeBoundsInScreenCoordinates.getCenterX() - redRectangleNode.getFullBounds().getWidth() / 2, stageTextNodeBoundsInScreenCoordinates.getMaxY());
            }
        };
        updateRedRectangleBounds.run();

        //coordinates can change, so need to update when they do
        canvas.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateRedRectangleBounds.run();
            }
        });

        //Make it so the mouse doesn't pan or zoom the PLayer's camera
        canvas.setPanEventHandler(null);
        canvas.setZoomEventHandler(null);

        //Add the canvas to the frame
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(canvas, BorderLayout.CENTER);
        frame.setContentPane(contentPane);

        //Create a control panel for interacting with the canvas.
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(new VisibilityCheckBox("Show Screen Grid", screenGridNode));
        controlPanel.add(new VisibilityCheckBox("Show Stage Grid", stageGridNode));
        controlPanel.add(new VisibilityCheckBox("Show Model Grid", modelGridNode));
        final Timer panTimer = new Timer(15, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.panModelViewport(-1E-9, -1E-9);
            }
        });
        controlPanel.add(new JButton(new AbstractAction("Pan model viewport") {
            public void actionPerformed(ActionEvent e) {
                panTimer.start();
            }
        }));
        controlPanel.add(new JButton(new AbstractAction("Stop pan model viewport") {
            public void actionPerformed(ActionEvent e) {
                panTimer.stop();
            }
        }));
        controlPanel.add(new JButton(new AbstractAction("Toggle Debug Regions"){
            public void actionPerformed(ActionEvent e) {
                canvas.toggleDebugRegionVisibility();
            }
        }));
        contentPane.add(controlPanel, BorderLayout.EAST);
    }

    public static class VisibilityCheckBox extends JCheckBox {
        public VisibilityCheckBox(String text, final PNode node) {
            super(text);
            setSelected(node.getVisible());
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    node.setVisible(isSelected());
                }
            });
        }
    }

    private void start() {
        frame.setVisible(true);
        canvas.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestStageCanvas().start();
            }
        });
    }
}
