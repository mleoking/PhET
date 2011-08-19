// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static java.awt.Color.white;

/**
 * Water molecule: H2O for use in the water model.  Units of this compound are in meters, and the values can get updated by Box2D update steps by Box2DAdapter
 * TODO: rename to WaterMolecule
 *
 * @author Sam Reid
 */
public class WaterMolecule extends Compound<SphericalParticle> {
    public WaterMolecule( ImmutableVector2D position, double angle ) {
        super( position, angle );

        final double spacing = ( new SphericalParticle.FreeOxygen().radius + new SphericalParticle.Hydrogen().radius ) * 0.5;
//        final double spacing = Units.picometersToMeters( SphericalParticle.FreeOxygen.RADIUS_PICOMETERS );

        //Spacing given by the water model: http://en.wikipedia.org/wiki/Water_model
//        final double spacing = Units.picometersToMeters( 95.84 ) * MicroModel.sizeScale;

        final double waterAngleRadians = Math.toRadians( 104.45 );
        addConstituent( new Constituent<SphericalParticle>( new Hydrogen(), parseAngleAndMagnitude( spacing, waterAngleRadians / 2 ) ) );
        addConstituent( new Constituent<SphericalParticle>( new Oxygen(), ZERO ) );
        addConstituent( new Constituent<SphericalParticle>( new Hydrogen(), parseAngleAndMagnitude( spacing, -waterAngleRadians / 2 ) ) );
    }

    public static class Hydrogen extends SphericalParticle {
        public Hydrogen() {
            //See this table for the charge, using TIP3P model: http://en.wikipedia.org/wiki/Water_model
            super( SphericalParticle.Hydrogen.RADIUS_PICOMETERS, SphericalParticle.POSITIVE_COLOR, white, +0.417 );
        }
    }

    public static class Oxygen extends SphericalParticle {
        public Oxygen() {
            //See this table for the charge, using TIP3P model: http://en.wikipedia.org/wiki/Water_model
            super( SphericalParticle.Oxygen.RADIUS_PICOMETERS, NEUTRAL_COLOR, Color.red, -0.834 );
        }
    }
}