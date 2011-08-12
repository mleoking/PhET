// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SucrosePositions;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Carbon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Hydrogen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NeutralOxygen;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.sizeScale;

/**
 * A single glucose molecule, which is used in lattice creation
 *
 * @author Sam Reid
 */
public class Glucose extends Compound<SphericalParticle> {
    public Glucose() {
        this( ZERO, Math.random() * 2 * Math.PI );
    }

    public Glucose( ImmutableVector2D relativePosition ) {
        this( relativePosition, Math.random() * 2 * Math.PI );
    }

    public Glucose( ImmutableVector2D relativePosition, double angle ) {
        super( relativePosition, angle );

        //Add the glucose molecule atoms in the right locations
        SucrosePositions sucrosePositions = new SucrosePositions();
        for ( ImmutableVector2D offset : sucrosePositions.getHydrogenPositions() ) {
            constituents.add( new Constituent<SphericalParticle>( new Hydrogen(), relativePosition.plus( offset.times( sizeScale ) ) ) );
        }
        for ( ImmutableVector2D offset : sucrosePositions.getCarbonPositions() ) {
            constituents.add( new Constituent<SphericalParticle>( new Carbon(), relativePosition.plus( offset.times( sizeScale ) ) ) );
        }
        for ( ImmutableVector2D offset : sucrosePositions.getOxygenPositions() ) {
            constituents.add( new Constituent<SphericalParticle>( new NeutralOxygen(), relativePosition.plus( offset.times( sizeScale ) ) ) );
        }

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( ZERO, 0.0 );
    }
}