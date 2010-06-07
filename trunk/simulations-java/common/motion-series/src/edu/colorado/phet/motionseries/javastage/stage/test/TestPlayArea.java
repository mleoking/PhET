/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.motionseries.javastage.stage.PlayArea;
import edu.colorado.phet.motionseries.javastage.stage.StageContainer;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * Provides a demonstration of the PlayArea class and its supporting classes.
 *
 * @author Sam Reid
 */
public class TestPlayArea {
    private final JFrame frame = new JFrame();
    private final PlayArea playArea;

    public TestPlayArea() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        SwingUtils.centerWindowOnScreen(frame);

        double stageWidth = 800;
        double stageHeight = 800;
        Rectangle.Double modelBounds = new Rectangle2D.Double(0, 0, 1, 1);
        playArea = new PlayArea(stageWidth, stageHeight, modelBounds);

        //Add nodes for depicting grids in each coordinate frame.
        GridNode screenGridNode = new GridNode(1f, 0, 100, 100);
        playArea.addScreenNode(screenGridNode);
        GridNode stageGridNode = new GridNode(1f, 0, 0, stageWidth / 10, stageWidth / 10, 10, 10);
        playArea.addStageNode(stageGridNode);
        GridNode modelGridNode = new GridNode(1f / 600f, modelBounds.getX(), modelBounds.getY(), modelBounds.getWidth() / 10.0, modelBounds.getHeight() / 10.0, 10, 10);
        playArea.addModelNode(modelGridNode);

        // Text node in screen coordinates
        playArea.addScreenNode(new PositionedTextNode("Screen at 100,100", 100, 100));

        // Yellow rectangle that shows the stage bounds.
        playArea.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageWidth), new BasicStroke(4), Color.yellow));

        // Text node in screen coordinates
        playArea.addScreenNode(new PositionedTextNode("Screen at 200,200", 200, 200));

        // Green circle in model coordinates.
        playArea.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5, 0.5), Color.green));

        // Text node at the left edge of world bounds
        playArea.addModelNode(new PositionedTextNode("hello from left edge of model bounds", modelBounds.getMinX(), modelBounds.getCenterY(), 2E-3));

        // Text node at the center of model bounds.
        playArea.addModelNode(new PositionedTextNode("hello from center of model bounds", modelBounds.getCenterX(), modelBounds.getCenterY(), 2E-3));
        
        // Text node in model coordinates.
        playArea.addModelNode(new PositionedTextNode("Model at 0.25,0.25", 0.25, 0.25, 2E-3 ));
        
        // Model origin (0,0) as a orange circle. It'll be in the lower-left corner of the stage bounds.
        PhetPPath originPath = new PhetPPath(new Ellipse2D.Double(-1,-1,2,2), Color.orange);
        originPath.scale( 2E-2 );
        playArea.addModelNode( originPath );
        
        /*
         * This block of code demonstrates how to adjust alignment of nodes in different coordinate frames.
         * In this case, a red rectangle in screen coordinates is positioned below text in stage coordinates.
         */
        {
            // Text node in stage coordinates
            final PositionedTextNode stageTextNode = new PositionedTextNode( "Stage at 200,200", 200, 200, 1 );
            playArea.addStageNode( stageTextNode );

            // Red rect in screen coordinates
            final PhetPPath redRectangleNode = new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 10 ), Color.red );
            playArea.addScreenNode( redRectangleNode );

            //This is a closure for updating the rectangle bounds.
            final Runnable updateRedRectangleBounds = new Runnable() {
                public void run() {
                    Rectangle2D stageTextNodeBoundsInScreenCoordinates = playArea.stageToScreen( stageTextNode.getFullBounds() );
                    // left aligned, below text
                    redRectangleNode.setOffset( stageTextNodeBoundsInScreenCoordinates.getMinX(), stageTextNodeBoundsInScreenCoordinates.getMaxY() );
                }
            };
            updateRedRectangleBounds.run();

            // Update when the play area changes size
            playArea.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    updateRedRectangleBounds.run();
                }
            } );
        }

        //Make it so the mouse doesn't pan or zoom the PLayer's camera
        playArea.setPanEventHandler(null);
        playArea.setZoomEventHandler(null);

        //Add the canvas to the frame
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(playArea, BorderLayout.CENTER);
        frame.setContentPane(contentPane);

        //Create a control panel for interacting with the canvas.
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(new VisibilityCheckBox("Show Screen Grid", screenGridNode));
        controlPanel.add(new VisibilityCheckBox("Show Stage Grid", stageGridNode));
        controlPanel.add(new VisibilityCheckBox("Show Model Grid", modelGridNode));
        final Timer panTimer = new Timer(15, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playArea.panModelViewport(-1E-3, -1E-3);
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
        controlPanel.add(new JButton(new AbstractAction("Toggle Debug Regions") {
            public void actionPerformed(ActionEvent e) {
                playArea.toggleDebugRegionVisibility();
            }
        }));
        final JTextArea textArea = new JTextArea(6, 8);
        controlPanel.add(textArea);
        playArea.addContainerBoundsChangeListener(new StageContainer.Listener() {
            public void stageContainerBoundsChanged() {
                Rectangle2D r = playArea.screenToModel(playArea.getScreenBounds());
                DecimalFormat f = new DecimalFormat("0.0000000");
                textArea.setText("Visible Model Bounds:" +
                        "\n x=" + f.format(r.getX()) +
                        "\n y=" + f.format(r.getY()) +
                        "\n w=" + f.format(r.getWidth()) +
                        "\n h=" + f.format(r.getHeight()));
            }
        });
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
        playArea.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestPlayArea().start();
            }
        });
    }
}
