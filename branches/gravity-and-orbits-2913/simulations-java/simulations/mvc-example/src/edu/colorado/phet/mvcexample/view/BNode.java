// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.mvcexample.model.BModelElement.BModelElementListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;

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
        addInputEventListener( new PDragEventHandler() ); // unconstrained dragging
        // NOTE: model updates as the result of dragging this node are handled in BConnectionsManager
    }
    
    //----------------------------------------------------------------------------
    // BModelElementListener implementation
    //----------------------------------------------------------------------------

    public void positionChanged(Point2D oldPosition, Point2D newPosition) {
        setOffset( newPosition );
    }
    
    public void orientationChanged(double oldOrientation, double newOrientation) {
        setRotation( newOrientation );
    }
}
