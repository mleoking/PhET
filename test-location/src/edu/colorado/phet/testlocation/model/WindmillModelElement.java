package edu.colorado.phet.testlocation.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;

/**
 * WindmillModelElement is the model of a windmill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
