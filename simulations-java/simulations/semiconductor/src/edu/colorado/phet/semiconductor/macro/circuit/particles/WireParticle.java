package edu.colorado.phet.semiconductor.macro.circuit.particles;

import edu.colorado.phet.semiconductor.macro.circuit.Circuit;


import edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 12:50:49 AM
 */
public class WireParticle extends SimpleObservable implements ModelElement {
    double dist;
    Circuit circuit;
    //    private double speed=-.004;
    private double speed = 0;

    public WireParticle( double dist, Circuit circuit ) {
        this.dist = dist;
        this.circuit = circuit;
    }

    public void setPosition( double dist ) {
        this.dist = dist;
//        System.out.println("dist = " + dist);
        updateObservers();
    }

    public AbstractVector2D getPosition() {
        return circuit.getPosition( dist );
    }

    public void stepInTime( double dt ) {
        double newLoc = dist + speed * dt;
        if ( circuit.contains( newLoc ) ) {
            setPosition( newLoc );
        }
        else {
            double length = circuit.getLength();
            while ( newLoc < 0 ) {
                newLoc += length;
            }
            while ( newLoc > length ) {
                newLoc -= length;
            }
            setPosition( newLoc );
        }

    }

    public void setSpeed( double speed ) {
        this.speed = speed;
    }

}
