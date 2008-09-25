package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.semiconductor.common.Particle;
import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 2:04:07 AM
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
