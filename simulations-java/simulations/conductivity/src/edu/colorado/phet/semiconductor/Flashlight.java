// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor;

import edu.colorado.phet.common.conductivity.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class Flashlight extends SimpleObservable {

    public Flashlight( double d, double d1, double d2 ) {
        x = d;
        y = d1;
        angle = d2;
    }

    public double getAngle() {
        return angle;
    }

    public Vector2D.Double getPosition() {
        return new Vector2D.Double( x, y );
    }

    public void setAngle( double d ) {
        angle = d;
        updateObservers();
    }

    double x;
    double y;
    double angle;
}
