package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.semiconductor.common.Particle;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 2:04:07 AM
 * Copyright (c) Jan 27, 2004 by Sam Reid
 */
public class Dopant extends SimpleObservable {
    private Particle particle;
    DopantType type;

    public Dopant( PhetVector position, DopantType type ) {
        this.type = type;
        this.particle = new Particle( position );
    }

    public PhetVector getPosition() {
        return particle.getPosition();
    }

    public void translate( double x, double y ) {
        particle.translate( x, y );
        updateObservers();
    }

    public DopantType getType() {
        return type;
    }
}
