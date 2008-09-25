// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.common;


import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.model.ModelElement;

public class Particle extends SimpleObservable
        implements ModelElement {

    public Particle( double d, double d1 ) {
        position = new Vector2D.Double( d, d1 );
        velocity = new Vector2D.Double();
        acceleration = new Vector2D.Double();
    }

    public AbstractVector2D getPosition() {
        return position;
    }

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    public void stepInTime( double d ) {
        AbstractVector2D phetvector = acceleration.getScaledInstance( d );
        velocity = velocity.getAddedInstance( phetvector );
        AbstractVector2D phetvector1 = velocity.getScaledInstance( d );
        position = position.getAddedInstance( phetvector1 );
        notifyObservers();
    }

    public void setAcceleration( double d, double d1 ) {
        acceleration = new Vector2D.Double( d, d1 );
    }

    public void setVelocity( double d, double d1 ) {
        velocity = new Vector2D.Double( d, d1 );
    }

    public void setPosition( double d, double d1 ) {
        position = new Vector2D.Double( d, d1 );
    }

    AbstractVector2D position;
    AbstractVector2D velocity;
    Vector2D.Double acceleration;
}
