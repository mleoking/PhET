// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.particles;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.semiconductor.macro.circuit.Circuit;

public class WireParticle extends SimpleObservable
        implements ModelElement {

    public WireParticle( double d, Circuit circuit1 ) {
        speed = 0.0D;
        dist = d;
        circuit = circuit1;
    }

    public void setPosition( double d ) {
        dist = d;
        updateObservers();
    }

    public PhetVector getPosition() {
        return circuit.getPosition( dist );
    }

    public void stepInTime( double d ) {
        double d1 = dist + speed * d;
        if( circuit.contains( d1 ) ) {
            setPosition( d1 );
        }
        else {
            double d2 = circuit.getLength();
            for( ; d1 < 0.0D; d1 += d2 ) {
                ;
            }
            for( ; d1 > d2; d1 -= d2 ) {
                ;
            }
            setPosition( d1 );
        }
    }

    public void setSpeed( double d ) {
        speed = d;
    }

    double dist;
    Circuit circuit;
    private double speed;
}
