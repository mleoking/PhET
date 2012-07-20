// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity;


import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

public class Flashlight extends SimpleObservable {

    public Flashlight( double d, double d1, double d2 ) {
        x = d;
        y = d1;
        angle = d2;
    }

    public double getAngle() {
        return angle;
    }

    public MutableVector2D getPosition() {
        return new MutableVector2D( x, y );
    }

    public void setAngle( double d ) {
        angle = d;
        notifyObservers();
    }

    double x;
    double y;
    double angle;
}
