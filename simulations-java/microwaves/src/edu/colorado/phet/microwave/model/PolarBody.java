/**
 * Class: PolarBody
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwave.model;

import edu.colorado.phet.common_microwaves.math.Vector2D;
import edu.colorado.phet.coreadditions_microwaves.CompositeBody;

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
