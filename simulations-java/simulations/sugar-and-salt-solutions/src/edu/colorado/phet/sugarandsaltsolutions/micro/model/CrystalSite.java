// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Immutable data structure used to incrementally grow a crystal
 *
 * @author Sam Reid
 */
public class CrystalSite {
    public final ImmutableVector2D position;
    public final Class<? extends Particle> type;

    public CrystalSite( ImmutableVector2D position, Class<? extends Particle> type ) {
        this.position = position;
        this.type = type;
    }

    //Determine if the specified particle is the right type to bond to the crystal at the specified location
    public boolean matches( Particle particle ) {
        return type.isInstance( particle );
    }
}