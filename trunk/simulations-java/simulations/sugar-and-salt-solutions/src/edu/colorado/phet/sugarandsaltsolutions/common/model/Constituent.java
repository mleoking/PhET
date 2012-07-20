// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Member in a compound, including the particle and its relative offset
 *
 * @author Sam Reid
 */
public class Constituent<T extends Particle> {

    //Relative location within the compound
    public final Vector2D relativePosition;

    //Particle within the compound
    public final T particle;

    public Constituent( T particle, Vector2D relativePosition ) {
        this.relativePosition = relativePosition;
        this.particle = particle;
    }
}