// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.semiconductor.common.Particle;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 2:04:07 AM
 */
public class Dopant extends SimpleObservable {
    private Particle particle;
    DopantType type;

    public Dopant( Vector2D position, DopantType type ) {
        this.type = type;
        this.particle = new Particle( position );
    }

    public Vector2D getPosition() {
        return particle.getPosition();
    }

    public void translate( double x, double y ) {
        particle.translate( x, y );
        notifyObservers();
    }

    public DopantType getType() {
        return type;
    }
}
