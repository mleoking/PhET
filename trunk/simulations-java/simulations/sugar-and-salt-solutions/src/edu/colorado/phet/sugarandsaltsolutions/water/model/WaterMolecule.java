// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Color;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static java.awt.Color.white;

/**
 * Water molecule: H2O for use in the water model.  Units of this compound are in meters, and the values can get updated by Box2D update steps by Box2DAdapter
 *
 * @author Sam Reid
 */
public class WaterMolecule extends Compound<SphericalParticle> {

    static final Random random = new Random();

    //Auxiliary constructor that creates a water molecule with the oxygen at the origin and an angle of 0 radians
    public WaterMolecule() {
        this( ZERO, 0 );
    }

    public WaterMolecule( ImmutableVector2D position, double angle ) {
        super( position, angle );

        final double spacing = ( new SphericalParticle.FreeOxygen().radius + new SphericalParticle.Hydrogen().radius ) * 0.5;
//        final double spacing = Units.picometersToMeters( SphericalParticle.FreeOxygen.RADIUS_PICOMETERS );

        //Spacing given by the water model: http://en.wikipedia.org/wiki/Water_model
//        final double spacing = Units.picometersToMeters( 95.84 ) * MicroModel.sizeScale;

        final double waterAngleRadians = Math.toRadians( 104.45 );

        final Constituent<SphericalParticle> h1 = new Constituent<SphericalParticle>( new Hydrogen(), parseAngleAndMagnitude( spacing, waterAngleRadians / 2 ) );
        final Constituent<SphericalParticle> o = new Constituent<SphericalParticle>( new Oxygen(), ZERO );
        final Constituent<SphericalParticle> h2 = new Constituent<SphericalParticle>( new Hydrogen(), parseAngleAndMagnitude( spacing, -waterAngleRadians / 2 ) );

        //Use different z-orderings to give make the water look as if it is at different 3d orientations
        int style = random.nextInt( 3 );
        if ( style == 0 ) {
            add( h1, o, h2 );
        }
        else if ( style == 1 ) {
            add( h1, h2, o );
        }
        else if ( style == 2 ) {
            add( h2, o, h1 );
        }
    }

    private void add( Constituent<SphericalParticle> a, Constituent<SphericalParticle> b, Constituent<SphericalParticle> c ) {
        addConstituent( a );
        addConstituent( b );
        addConstituent( c );
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