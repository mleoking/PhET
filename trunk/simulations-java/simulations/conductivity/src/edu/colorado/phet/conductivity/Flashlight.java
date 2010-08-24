// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

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
