// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.circuitconstructionkit.view.chart;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 11:11:27 PM
 */

public class CrosshairConnection extends PhetPNode {
    private AbstractFloatingChart FloatingChart;
    private PNode destination;
    private PPath line;

    public CrosshairConnection(AbstractFloatingChart FloatingChart, PNode destination) {
        this.FloatingChart = FloatingChart;
        this.destination = destination;
        line = new PPath();
        line.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        addChild(line);
        getSource().addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                update();
            }
        });
        getDestination().addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                update();
            }
        });
        update();
    }

    private PNode getDestination() {
        return destination;
    }

    private PNode getSource() {
        return (PNode) FloatingChart.getStripChartJFCNode();
    }

    private void update() {
        Point2D srcLoc = new Point2D.Double(getSource().getFullBounds().getMaxX(), getSource().getFullBounds().getCenterY());
        Point2D dstLoc = new Point2D.Double(getDestination().getFullBounds().getMinX(), getDestination().getFullBounds().getCenterY());
        double dist = srcLoc.distance(dstLoc);
        if (dist > 0) {
            DoubleGeneralPath path = new DoubleGeneralPath(srcLoc);
            ImmutableVector2D parallel = new Vector2D(srcLoc, dstLoc).getNormalizedInstance();
            ImmutableVector2D perp = parallel.getRotatedInstance(Math.PI / 2).getNormalizedInstance();
            lineToDst(path, parallel, perp, dist / 5);
            curveToDst(path, parallel, perp, dist / 5);
            lineToDst(path, parallel, perp, dist / 5);
            curveToDst(path, parallel, perp, dist / 5);
            path.lineTo(dstLoc);
            line.setPathTo(path.getGeneralPath());
        } else {
            line.setPathTo(new Rectangle());
        }
    }

    private void curveToDst(DoubleGeneralPath path, ImmutableVector2D par, ImmutableVector2D perp, double segmentLength) {
        double pegDist = segmentLength;
        if (pegDist < 7) {
            pegDist = 7;
        }
        if (pegDist > 20) {
            pegDist = 20;
        }
        double width = 15;
        Point2D peg1 = path.getCurrentPoint();
        Point2D peg2 = par.getInstanceOfMagnitude(pegDist).getDestination(peg1);
        Point2D dst = par.getInstanceOfMagnitude(pegDist / 2).getAddedInstance(perp.getInstanceOfMagnitude(width)).getDestination(peg1);
        path.quadTo(peg2.getX(), peg2.getY(), dst.getX(), dst.getY());
        path.quadTo(peg1.getX(), peg1.getY(), peg2.getX(), peg2.getY());
    }

    private void lineToDst(DoubleGeneralPath path, ImmutableVector2D a, ImmutableVector2D b, double segmentLength) {
        path.lineToRelative(a.getInstanceOfMagnitude(segmentLength));
    }
}
