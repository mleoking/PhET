// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;

/**
 * This crystal for Calcium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class CalciumChlorideCrystal extends Crystal<SphericalParticle> {
    public CalciumChlorideCrystal( ImmutableVector2D position, double angle ) {
        super( position, new Calcium().radius + new Chloride().radius, angle );
    }

    //Create the bonding partner for Calcium Chloride
    @Override public SphericalParticle createPartner( SphericalParticle original ) {
        return original instanceof Calcium ? new Chloride() : new Calcium();
    }

    //Randomly choose an initial particle for the crystal lattice
    @Override protected SphericalParticle createSeed() {
        return random.nextBoolean() ? new Calcium() : new Chloride();
    }

    @Override public ImmutableVector2D[] getPossibleDirections( Constituent<SphericalParticle> constituent ) {
        //If there's something North/South, then do not allow going East/West and vice versa
        //This is to match the design doc spec and Soluble Salts sim to get a 2:1 lattice
        //This effectively makes it so that so that every other Ca2+ is omitted from the lattice in a regular way
        if ( constituent.particle instanceof Chloride ) {
            if ( isOccupied( constituent.relativePosition.plus( NORTH ) ) ) { return new ImmutableVector2D[] { SOUTH }; }
            else if ( isOccupied( constituent.relativePosition.plus( SOUTH ) ) ) { return new ImmutableVector2D[] { NORTH };}
            else if ( isOccupied( constituent.relativePosition.plus( EAST ) ) ) { return new ImmutableVector2D[] { WEST };}
            else if ( isOccupied( constituent.relativePosition.plus( WEST ) ) ) { return new ImmutableVector2D[] { EAST };}

            //If no neighbor site is occupied, then this is the first particle in the lattice, so fall through and allow to go any direction
        }
        return super.getPossibleDirections( constituent );
    }
}