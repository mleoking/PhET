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
    public final ImmutableVector2D position;
    public final Particle target;
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