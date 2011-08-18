// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

/**
 * Water molecule: H2O for use in the water model.  Units of this compound are in meters, and the values can get updated by Box2D update steps by Box2DAdapter
 * TODO: rename to WaterMolecule
 *
 * @author Sam Reid
 */
public class WaterMolecule2 extends Compound<ChargedSphericalParticle> {
    public WaterMolecule2( ImmutableVector2D position, double angle ) {
        super( position, angle );
        final double spacing = ( new SphericalParticle.FreeOxygen().radius + new SphericalParticle.Hydrogen().radius ) * 0.5;
        addConstituent( new Constituent<ChargedSphericalParticle>( new ChargedSphericalParticle.Hydrogen(), new ImmutableVector2D( spacing, 0 ) ) );
        addConstituent( new Constituent<ChargedSphericalParticle>( new ChargedSphericalParticle.Oxygen(), ImmutableVector2D.ZERO ) );
        addConstituent( new Constituent<ChargedSphericalParticle>( new ChargedSphericalParticle.Hydrogen(), new ImmutableVector2D( 0, spacing ) ) );
    }
}