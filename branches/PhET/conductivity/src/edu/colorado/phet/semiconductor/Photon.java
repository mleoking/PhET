// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.semiconductor.common.Particle;

public class Photon extends SimpleObservable
        implements ModelElement {

    public Photon() {
        particle = new Particle( 0.0D, 0.0D );
    }

    public void setPosition( double d, double d1 ) {
        particle.setPosition( d, d1 );
        updateObservers();
    }

    public void setVelocity( PhetVector phetvector ) {
        particle.setVelocity( phetvector.getX(), phetvector.getY() );
        updateObservers();
    }

    public PhetVector getPosition() {
        return particle.getPosition();
    }

    public void stepInTime( double d ) {
        particle.setAcceleration( 0.0D, 0.0D );
        particle.stepInTime( d );
        updateObservers();
    }

    public void setPosition( PhetVector phetvector ) {
        setPosition( phetvector.getX(), phetvector.getY() );
    }

    public PhetVector getVelocity() {
        return particle.getVelocity();
    }

    public double getSpeed() {
        return getVelocity().getMagnitude();
    }

    Particle particle;
}
