// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ArrowNode is a PPath that draws an arrow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ArrowNode extends PPath {

    Arrow arrow;

    /**
     * Constructor.
     *
     * @param tailLocation
     * @param tipLocation
     * @param headHeight
     * @param headWidth
     * @param tailWidth
     */
    public ArrowNode( Point2D tailLocation, Point2D tipLocation,
                      double headHeight, double headWidth, double tailWidth ) {
        super();
        arrow = new Arrow( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        updateShape();
    }

    /**
     * Constructor.
     *
     * @param tailLocation
     * @param tipLocation
     * @param headHeight
     * @param headWidth
     * @param tailWidth
     * @param fractionalHeadHeight
     * @param scaleTailToo
     */
    public ArrowNode( Point2D tailLocation, Point2D tipLocation,
                      double headHeight, double headWidth, double tailWidth,
                      double fractionalHeadHeight, boolean scaleTailToo ) {
        super();
        arrow = new Arrow( tailLocation, tipLocation, headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
        updateShape();
    }

    /**
     * Sets the new location for both the tail and the tip of the arrow.  This
     * method can be used to translate or rotate the arrow.
     */
    public void setTipAndTailLocations( Point2D newTipLocation, Point2D newTailLocation ) {
        arrow.setTipAndTailLocations( newTipLocation, newTailLocation );
        updateShape();
    }

    public void setTipAndTailLocations( double x1, double y1, double x2, double y2 ) {
        setTipAndTailLocations( new Point2D.Double( x1, y1 ), new Point2D.Double( x2, y2 ) );
    }

    public Point2D getTipLocation() {
        return arrow.getTipLocation();
    }

    public Point2D getTailLocation() {
        return arrow.getTailLocation();
    }

    public void setTailWidth( double tailWidth ) {
        arrow.setTailWidth( tailWidth );
        updateShape();
    }

    private void updateShape() {
        setPathTo( arrow.getShape() );
    }

    public void setHeadHeight( double headHeight ) {
        arrow.setHeadHeight( headHeight );
        updateShape();
    }

    public void setHeadWidth( double headWidth ) {
        arrow.setHeadWidth( headWidth );
        updateShape();
    }
}
