/**
 * Class: PolarBody
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.model;

import edu.colorado.phet.microwaves.coreadditions.CompositeBody;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;

public abstract class PolarBody extends CompositeBody {
    protected double dipoleOrientation;

    public abstract void respondToEmf( Vector2D emf, double dt );

    public double getDipoleOrientation() {
        return dipoleOrientation;
    }

    public void setDipoleOrientation( double dipoleOrientation ) {
        this.dipoleOrientation = dipoleOrientation;
    }
}
