// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.Vector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.Vector2D.createPolar;

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

    public WaterMolecule( Vector2D position, double angle ) {
        super( position, angle );

        //Spacing should be given by the water model: http://en.wikipedia.org/wiki/Water_model, but we just pick one that looks good
        final double spacing = ( new SphericalParticle.FreeOxygen().radius + new SphericalParticle.Hydrogen().radius ) * 0.5;

        final double waterAngleRadians = Math.toRadians( 104.45 );

        final Constituent<SphericalParticle> h1 = new Constituent<SphericalParticle>( new Hydrogen(), createPolar( spacing, waterAngleRadians / 2 ) );
        final Constituent<SphericalParticle> o = new Constituent<SphericalParticle>( new Oxygen(), ZERO );
        final Constituent<SphericalParticle> h2 = new Constituent<SphericalParticle>( new Hydrogen(), createPolar( spacing, -waterAngleRadians / 2 ) );

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

    //Specialization of SphericalParticle.Hydrogen that uses the TIP3P model of partial charge.
    //Provided here as a separate class so that the creation above is as simple as new Hydrogen() and no code is duplicated in the constructor invocations
    public static class Hydrogen extends SphericalParticle.Hydrogen {
        public Hydrogen() {
            //See this table for the charge, using TIP3P model: http://en.wikipedia.org/wiki/Water_model
            super( POSITIVE_COLOR, +0.417 );
        }
    }

    //Specialization of SphericalParticle.Oxygen that uses the TIP3P model of partial charge.
    //Provided here as a separate class for uniformity with Hydrogen inner class
    public static class Oxygen extends SphericalParticle.Oxygen {
        public Oxygen() {
            //See this table for the charge, using TIP3P model: http://en.wikipedia.org/wiki/Water_model
            super( NEUTRAL_COLOR, -0.834 );
        }
    }
}