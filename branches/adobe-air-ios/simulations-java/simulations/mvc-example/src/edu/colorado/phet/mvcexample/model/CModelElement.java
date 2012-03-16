// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * CModelElement is a model element that defines its own listener interface
 * and manages its own listener lists directly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CModelElement extends Pointer implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CModelElement( Point2D position, double orientation, Dimension size, Color color ) {
        super( position, orientation, size, color );
        _listeners = new ArrayList();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPosition( Point2D position ) {
        if ( !position.equals( getPosition() ) ) {
            super.setPosition( position );
            notifyPositionChanged();
        }
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != getOrientation()) {
            super.setOrientation( orientation );
            notifyOrientationChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Listener interface & management
    //----------------------------------------------------------------------------
    
    public interface CModelElementListener {
    	public void positionChanged();
    	public void orientationChanged();
    }
    
    public static class CModelElementAdapter implements CModelElementListener {
		public void orientationChanged() {}
		public void positionChanged() {}
    }
    
    public void addListener( CModelElementListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( CModelElementListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyPositionChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (CModelElementListener) _listeners.get( i ) ).positionChanged();
        }
    }
    
    private void notifyOrientationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (CModelElementListener) _listeners.get( i ) ).orientationChanged();
        }
    }
}
