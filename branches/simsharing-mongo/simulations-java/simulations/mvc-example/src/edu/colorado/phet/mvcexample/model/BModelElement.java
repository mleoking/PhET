// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerController;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerControllerFactory;

/**
 * BModelElement is a model element that uses a DynamicListenerController to notify listeners.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BModelElement extends Pointer implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BModelElementListener _controller;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BModelElement( Point2D position, double orientation, Dimension size, Color color ) {
        super( position, orientation, size, color );
        _controller = (BModelElementListener) DynamicListenerControllerFactory.newController( BModelElementListener.class );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPosition( Point2D position ) {
        if ( !position.equals( getPosition() ) ) {
            Point2D oldPosition = getPosition();
            super.setPosition( position );
            _controller.positionChanged( oldPosition, getPosition() );
        }
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != getOrientation() ) {
            double oldOrientation = getOrientation();
            super.setOrientation( orientation );
            _controller.orientationChanged( oldOrientation, getOrientation() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface BModelElementListener {
        public void positionChanged( Point2D oldPosition, Point2D newPosition );
        public void orientationChanged( double oldOrientation, double newOrientation );
    }

    public static class BModelElementAdapter implements BModelElementListener {
        public void orientationChanged( double oldOrientation, double newOrientation ) {}
        public void positionChanged( Point2D oldPosition, Point2D newPosition ) {}
    }

    public void addListener( BModelElementListener listener ) {
        ( (DynamicListenerController) _controller ).addListener( listener );
        Point2D position = getPosition();
        _controller.positionChanged( position, position );
        double orientation = getOrientation();
        _controller.orientationChanged( orientation, orientation );
    }

    public void removeListener( BModelElementListener listener ) {
        ( (DynamicListenerController) _controller ).removeListener( listener );
    }

    public Collection getAllListeners() {
        return ( (DynamicListenerController) _controller ).getAllListeners();
    }
}
