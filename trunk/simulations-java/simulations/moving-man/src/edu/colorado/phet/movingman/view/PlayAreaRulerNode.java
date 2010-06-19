package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * @author Sam Reid
 */
public class PlayAreaRulerNode extends PNode {
    private final PhetPPath face;
    private final PhetPPath base;
    private final PhetPPath side;
    private Range modelRange;
    private Range viewRange;
    private PNode tickLayer;

    public PlayAreaRulerNode(Range modelRange, Range viewRange) {
        this.modelRange = modelRange;
        this.viewRange = viewRange;
        face = new PhetPPath(new Color(229, 221, 127), new BasicStroke(1), Color.black);
        base = new PhetPPath(new Color(181, 174, 98), new BasicStroke(1), Color.black);
        side = new PhetPPath(new Color(173, 165, 80), new BasicStroke(1), Color.black);
        viewRange.addObserver(new SimpleObserver() {
            public void update() {
                updateShape();
            }
        });
        tickLayer = new PNode();
        updateShape();
        addChild(face);
        addChild(base);
        addChild(side);
        addChild(tickLayer);
    }

    private void updateShape() {
        int faceHeight = 20;//pixels
        int rulerInsetWidth = 12;//pixels

        //Support for flipped coordinate frame
        final double rulerWidth = Math.abs(viewRange.getMax() - viewRange.getMin()) + rulerInsetWidth * 2;
        final double rulerX = viewRange.getMin() < viewRange.getMax() ? viewRange.getMin() - rulerInsetWidth  : viewRange.getMin() + rulerInsetWidth - rulerWidth;

        face.setPathTo(new Rectangle2D.Double(rulerX, 0, rulerWidth, faceHeight));

        DoubleGeneralPath basePath = new DoubleGeneralPath(rulerX, faceHeight);
        basePath.lineToRelative(rulerWidth, 0);
        basePath.lineToRelative(3, 3);
        basePath.lineToRelative(-rulerWidth, 0);
        basePath.lineTo(rulerX, faceHeight);
        base.setPathTo(basePath.getGeneralPath());

        DoubleGeneralPath sidePath = new DoubleGeneralPath(rulerX + rulerWidth, 0);
        sidePath.lineToRelative(3, 3);
        sidePath.lineToRelative(0, faceHeight);
        sidePath.lineToRelative(-3, -3);
        sidePath.lineTo(rulerX + rulerWidth, 0);
        side.setPathTo(sidePath.getGeneralPath());

        tickLayer.removeAllChildren();
        Function.LinearFunction linearFunction = new Function.LinearFunction(modelRange.getMin(), modelRange.getMax(), viewRange.getMin(), viewRange.getMax());
        for (double i = modelRange.getMin(); i <= modelRange.getMax() + 1E-6;//make sure we hit the endpoint
             i += 1) {
            double viewValue = linearFunction.evaluate(i);
            PNode rulerTickMark;
            if (i % 2 == 0) {
                final LabeledTickMark tm = new LabeledTickMark(4, i);
                if (i == 0) {
                    tm.setTickText("0 meters");
                }
                rulerTickMark = tm;
            } else {
                rulerTickMark = new TickMark(7);
            }
            rulerTickMark.setOffset(viewValue, 0);
            tickLayer.addChild(rulerTickMark);
        }
    }

    public static class TickMark extends PNode {
        private final PhetPPath tick;

        public TickMark(int tickHeight) {
            double tickWidth = 1.0;
            tick = new PhetPPath(new Rectangle2D.Double(-tickWidth / 2, 0, tickWidth, tickHeight), Color.black);
            addChild(tick);
        }
    }

    public static class LabeledTickMark extends TickMark {
        private PText text;

        public LabeledTickMark(int tickHeight, double x) {
            super(tickHeight);
            text = new PText(new DecimalFormat("0").format(x));
            text.setFont(new PhetFont(14, true));
            addChild(text);
            text.setOffset(super.tick.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2, super.tick.getFullBounds().getHeight());
        }

        public String getTickText() {
            return text.getText();
        }

        public void setTickText(String s) {
            text.setText(s);//todo: need a better way of doing this that respects layout
        }
    }
}
