// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phetgraphicsdemo.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * CarModelElement is the model of a car that moves horizontally.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CarModelElement extends SimpleObservable implements ModelElement {

    private double _range;
    private double _location;
    private double _direction;
    
    public CarModelElement( double range ) {
        _range = range;
        _direction = 0.0;
    }
    
    public double getRange() {
        return _range;
    }
    
    public double getLocation() {
        return _location;
    }
    
    public double getDirection() {
        return _direction;
    }
    
    public void stepInTime( double dt ) {
        if ( _direction == 0.0 ) {
            _location += 1;
        }
        else {
            _location -= 1;
        }
        
        // Change direction when the car hits its range limit.
        if ( _location <= 0 || _location >= _range ) {
            _direction = ( _direction + 180 ) % 360;
        }
        notifyObservers();
    }

}
