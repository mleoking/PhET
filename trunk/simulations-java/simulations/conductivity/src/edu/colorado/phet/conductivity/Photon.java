// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity;


import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.conductivity.common.Particle;

public class Photon extends SimpleObservable
        implements ModelElement {

    public Photon() {
        particle = new Particle( 0.0D, 0.0D );
    }

    public void setPosition( double d, double d1 ) {
        particle.setPosition( d, d1 );
        notifyObservers();
    }

    public void setVelocity( AbstractVector2D phetvector ) {
        particle.setVelocity( phetvector.getX(), phetvector.getY() );
        notifyObservers();
    }

    public AbstractVector2D getPosition() {
        return particle.getPosition();
    }

    public void stepInTime( double d ) {
        particle.setAcceleration( 0.0D, 0.0D );
        particle.stepInTime( d );
        notifyObservers();
    }

    public void setPosition( Vector2D.Double phetvector ) {
        setPosition( phetvector.getX(), phetvector.getY() );
    }

    public AbstractVector2D getVelocity() {
        return particle.getVelocity();
    }

    public double getSpeed() {
        return getVelocity().getMagnitude();
    }

    Particle particle;
}
