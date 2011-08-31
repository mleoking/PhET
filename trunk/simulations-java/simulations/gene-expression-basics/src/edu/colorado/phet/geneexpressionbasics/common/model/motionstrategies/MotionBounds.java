// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * Class that defines the bounds within which some shape or point is allowed to
 * move.  The shape can be anything, and does not need to be rectangular.
 * <p/>
 * If the bounds are not set, they are assumed to be infinite.
 *
 * @author John Blanco
 */
public class MotionBounds {

    private Shape boundsShape = null;

    public MotionBounds() {
        // Default constructor does notion, leaves bounds infinit.
    }

    public MotionBounds( Shape boundsShape ) {
        this.boundsShape = boundsShape;
    }

    public boolean inBounds( Point2D p ) {
        if ( boundsShape == null ) {
            // No bounds means everything is in bounds.
            return true;
        }
        else {
            return boundsShape.contains( p );
        }
    }

    public boolean inBounds( Shape s ) {
        if ( boundsShape == null ) {
            // No bounds means everything is in bounds.
            return true;
        }
        else {
            return boundsShape.contains( s.getBounds2D() );
        }
    }

    public Shape getBounds() {
        return new Area( boundsShape );
    }

    public void setBounds( Shape newBounds ) {
        boundsShape = new Area( newBounds );
    }
}
