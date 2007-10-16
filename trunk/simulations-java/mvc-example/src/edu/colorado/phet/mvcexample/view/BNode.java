/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.mvcexample.model.BModelElement.BModelElementListener;

/**
 * BNode is the visual representation of a BModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BNode extends PointerNode implements BModelElementListener {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BNode( Dimension size, Color fillColor ) {
        super( size, fillColor );
    }
    
    //----------------------------------------------------------------------------
    // BModelElementListener implementation (model changes)
    //----------------------------------------------------------------------------

    public void positionChanged(Point2D oldPosition, Point2D newPosition) {
        setOffset( newPosition );
    }
    
    public void orientationChanged(double oldOrientation, double newOrientation) {
        setRotation( newOrientation );
    }
}
