package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
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

    public Dopant( AbstractVector2D position, DopantType type ) {
        this.type = type;
        this.particle = new Particle( position );
    }

    public AbstractVector2D getPosition() {
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
