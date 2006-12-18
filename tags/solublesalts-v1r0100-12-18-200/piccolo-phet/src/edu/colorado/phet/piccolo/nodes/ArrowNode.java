/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.nodes;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ArrowNode is a PPath that draws an arrow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ArrowNode extends PPath {

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
        Arrow arrow = new Arrow( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
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
        Arrow arrow = new Arrow( tailLocation, tipLocation, headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
        setPathTo( arrow.getShape() );
    }
}
