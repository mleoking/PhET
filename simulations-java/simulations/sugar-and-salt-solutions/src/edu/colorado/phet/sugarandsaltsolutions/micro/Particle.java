// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import org.jbox2d.common.Vec2;

/**
 * Interface used by coulomb force calculation.
 *
 * @author Sam Reid
 */
interface Particle {
    Vec2 getPosition();

    double getCharge();
}
