/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ArrowNode is a PPath that draws an arrow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
        setPathTo( arrow.getShape() );
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
        setPathTo( arrow.getShape() );
    }
    
    /**
     * Sets the new location for both the tail and the tip of the arrow.  This
     * method can be used to translate or rotate the arrow.
     */
    public void setTipAndTailLocations(Point2D newTipLocation, Point2D newTailLocation){
        arrow.setTipAndTailLocations( newTipLocation, newTailLocation );
        setPathTo( arrow.getShape() );
    }

    public Point2D getTipLocation(){
        return arrow.getTipLocation();
    }
}
