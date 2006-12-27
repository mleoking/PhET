// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;

public class Particle extends SimpleObservable
        implements ModelElement {

    public Particle( double d, double d1 ) {
        position = new PhetVector( d, d1 );
        velocity = new PhetVector();
        acceleration = new PhetVector();
    }

    public PhetVector getPosition() {
        return position;
    }

    public PhetVector getVelocity() {
        return velocity;
    }

    public void stepInTime( double d ) {
        PhetVector phetvector = acceleration.getScaledInstance( d );
        velocity = velocity.getAddedInstance( phetvector );
        PhetVector phetvector1 = velocity.getScaledInstance( d );
        position = position.getAddedInstance( phetvector1 );
        updateObservers();
    }

    public void setAcceleration( double d, double d1 ) {
        acceleration = new PhetVector( d, d1 );
    }

    public void setVelocity( double d, double d1 ) {
        velocity = new PhetVector( d, d1 );
    }

    public void setPosition( double d, double d1 ) {
        position = new PhetVector( d, d1 );
    }

    PhetVector position;
    PhetVector velocity;
    PhetVector acceleration;
}
