// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

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
//        final double spacing = Units.picometersToMeters( SphericalParticle.FreeOxygen.RADIUS_PICOMETERS );

        //Spacing given by the water model: http://en.wikipedia.org/wiki/Water_model
//        final double spacing = Units.picometersToMeters( 95.84 ) * MicroModel.sizeScale;

        final double waterAngleRadians = Math.toRadians( 104.45 );
        addConstituent( new Constituent<ChargedSphericalParticle>( new ChargedSphericalParticle.Hydrogen(), parseAngleAndMagnitude( spacing, waterAngleRadians / 2 ) ) );
        addConstituent( new Constituent<ChargedSphericalParticle>( new ChargedSphericalParticle.Oxygen(), ZERO ) );
        addConstituent( new Constituent<ChargedSphericalParticle>( new ChargedSphericalParticle.Hydrogen(), parseAngleAndMagnitude( spacing, -waterAngleRadians / 2 ) ) );
    }
}