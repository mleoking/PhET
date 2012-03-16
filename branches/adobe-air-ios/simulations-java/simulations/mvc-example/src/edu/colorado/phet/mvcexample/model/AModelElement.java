// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AModelElement is a model element that uses Observer to notify listeners.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AModelElement extends Pointer implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POSITION = "position";
    public static final String PROPERTY_ORIENTATION = "orientation";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Observable _observable;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AModelElement( Point2D position, double orientation, Dimension size, Color color ) {
        super( position, orientation, size, color );
        
        _observable = new Observable() {
            public void notifyObservers( Object arg ) {
                setChanged();
                super.notifyObservers( arg );
                clearChanged();
            }
        };
    }

    public void addObserver( Observer observer ) {
        _observable.addObserver( observer );
    }
    
    public void deleteObserver( Observer observer ) {
        _observable.deleteObserver( observer );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPosition( Point2D position ) {
        if ( ! position.equals( getPosition() ) ) {
            super.setPosition( position );
            _observable.notifyObservers( PROPERTY_POSITION );   
        }
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != getOrientation() ) {
            super.setOrientation( orientation );
            _observable.notifyObservers( PROPERTY_ORIENTATION );
        }
    }
}
