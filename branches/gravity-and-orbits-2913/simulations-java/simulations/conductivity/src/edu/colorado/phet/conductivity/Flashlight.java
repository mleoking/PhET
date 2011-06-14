// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity;


import edu.colorado.phet.common.phetcommon.math.Vector2D;
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

    public Vector2D getPosition() {
        return new Vector2D( x, y );
    }

    public void setAngle( double d ) {
        angle = d;
        notifyObservers();
    }

    double x;
    double y;
    double angle;
}
