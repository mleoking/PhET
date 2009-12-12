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
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Provides a demonstration of the StageCanvas class and its supporting classes.
 *
 * @author Sam Reid
 */
public class TestStageCanvas {
    private JFrame frame = new JFrame();
    private PhetPPath rectNode;
    private PositionedTextNode stageTextNode;
    private StageCanvas canvas;

    public void updateRectNodeLocation() {
        Rectangle2D rectNodeBounds = rectNode.globalToLocal(stageTextNode.getGlobalFullBounds());
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

        GridNode screenGridNode = new GridNode(1f, 0, 100, 100);
        canvas.addScreenNode(screenGridNode);
        GridNode stageGridNode = new GridNode(1f, 0, 0, 10, 10, 20, 10);
        canvas.addStageNode(stageGridNode);
        GridNode modelGridNode = new GridNode(1E-6 / 600f, modelBounds.getX(), modelBounds.getY(), modelBounds.getWidth() / 11.0, modelBounds.getHeight() / 11.0, 11, 11);
        canvas.addModelNode(modelGridNode);

        canvas.addScreenNode(new PositionedTextNode("Screen at 50,50", 50, 50));
        stageTextNode = new PositionedTextNode("Stage at 100,50", 100, 50, 0.5);
        canvas.addStageNode(stageTextNode);
        canvas.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageHeight), new BasicStroke(2), Color.yellow));
        canvas.addScreenNode(new PositionedTextNode("Screen at 100,100", 100, 100));
        canvas.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5E-6, 0.5E-6), Color.blue));
        canvas.addModelNode(new PositionedTextNode("hello from left edge of world bounds", modelBounds.getMinX(), modelBounds.getCenterY(), 1E-6 / 400));
        canvas.addModelNode(new PositionedTextNode("hello from center of model bounds", modelBounds.getCenterX(), modelBounds.getCenterY(), 1E-6 / 400));

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

        //todo: maybe stage bounds should be mutable, since it is preferable to create the nodes as children of the canvas

//        new Timer(15, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                canvas.panModelViewport(-1E-8 / 4, -1E-8 / 4);
//            }
//        }).start();

        canvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                canvas.toggleDebugRegionVisibility();
            }
        });
        canvas.setPanEventHandler(null);
        canvas.setZoomEventHandler(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(canvas, BorderLayout.CENTER);

        frame.setContentPane(contentPane);


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(new VisibilityCheckBox("Show Screen Grid", screenGridNode));
        controlPanel.add(new VisibilityCheckBox("Show Stage Grid", stageGridNode));
        controlPanel.add(new VisibilityCheckBox("Show Model Grid", modelGridNode));
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

    private static class GridNode extends PNode {
        double x0;
        double y0;
        double dx;
        double dy;
        int nx;
        int ny;

        private GridNode(double strokeWidth, double x0, double y0, double dx, double dy, int nx, int ny) {
            this.x0 = x0;
            this.y0 = y0;
            this.dx = dx;
            this.dy = dy;
            this.nx = nx;
            this.ny = ny;

            for (int i = 0; i < nx + 1; i++) {//the +1 puts the end caps on the gridlines
                double x = x0 + i * dx;
                PhetPPath path = new PhetPPath(new Line2D.Double(x, y0, x, y0 + ny * dy), new BasicStroke((float) strokeWidth), getColor(i));
                addChild(path);
            }
            for (int k = 0; k < ny + 1; k++) {
                double y = y0 + k * dy;
                PhetPPath path = new PhetPPath(new Line2D.Double(x0, y, x0 + nx * dx, y), new BasicStroke((float) strokeWidth), getColor(k));
                addChild(path);
            }
        }

        private Color getColor(int k) {
            if (k % 10 == 0) return Color.darkGray;
            else if (k % 5 == 0) return Color.gray;
            else return Color.lightGray;
        }

        /**
         * Creates a grid that is symmetrical in x-y
         */
        private GridNode(double strokeWidth, double x0, double dx, int nx) {
            this(strokeWidth, x0, x0, dx, dx, nx, nx);
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
