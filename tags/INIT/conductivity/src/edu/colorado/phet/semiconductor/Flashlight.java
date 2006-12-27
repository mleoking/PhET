// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;

public class Flashlight extends SimpleObservable {

    public Flashlight( double d, double d1, double d2 ) {
        x = d;
        y = d1;
        angle = d2;
    }

    public double getAngle() {
        return angle;
    }

    public PhetVector getPosition() {
        return new PhetVector( x, y );
    }

    public void setAngle( double d ) {
        angle = d;
        updateObservers();
    }

    double x;
    double y;
    double angle;
}
