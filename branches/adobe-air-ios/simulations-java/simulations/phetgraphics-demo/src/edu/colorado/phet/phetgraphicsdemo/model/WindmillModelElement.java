// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phetgraphicsdemo.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * WindmillModelElement is the model of a windmill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WindmillModelElement extends SimpleObservable implements ModelElement {

    private Point2D _location;
    private int _numberOfBlades;
    private double _rotationAngle;
    
    public WindmillModelElement( int numberOfBlades ) {
        _location = new Point2D.Double();
        _numberOfBlades = numberOfBlades;
        _rotationAngle = 0.0;
    }

    public int getNumberOfBlades() {
        return _numberOfBlades;
    }
    
    public double getRotationAngle() {
        return _rotationAngle;
    }
    
    public void stepInTime( double dt ) {
        _rotationAngle = ( _rotationAngle + 1 ) % 360;
        notifyObservers();
    }

}
