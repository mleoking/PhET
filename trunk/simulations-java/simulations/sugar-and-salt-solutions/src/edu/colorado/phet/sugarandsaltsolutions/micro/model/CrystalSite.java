// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Immutable data structure used to incrementally grow a crystal
 *
 * @author Sam Reid
 */
public class CrystalSite {

    //Location of the potential bonding site
    public final ImmutableVector2D position;

    //Particle that would be added at the bonding site
    public final Particle target;

    //Corresponding site for partner lattice topology
    public final LatticeSite latticeSite;

    public CrystalSite( ImmutableVector2D position, Particle target, LatticeSite latticeSite ) {
        this.position = position;
        this.target = target;
        this.latticeSite = latticeSite;
        target.setPosition( position );
    }

    public Rectangle2D getTargetBounds() {
        return target.getShape().getBounds2D();
    }

    //Determine if the specified particle is the right type to bond to the crystal at the specified location
    public boolean matches( Particle particle ) {
        return target.getClass().equals( particle.getClass() );
    }
}