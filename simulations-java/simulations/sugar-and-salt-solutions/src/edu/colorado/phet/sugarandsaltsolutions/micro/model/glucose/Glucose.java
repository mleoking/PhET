// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.GlucosePositions;
import edu.colorado.phet.sugarandsaltsolutions.common.model.GlucosePositions.Atom;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

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

    public Glucose( ImmutableVector2D relativePosition, double angle ) {
        super( relativePosition, angle );

        //Add the glucose molecule atoms in the right locations, and in the right z-ordering
        for ( Atom atom : new GlucosePositions().getAtoms() ) {
            constituents.add( new Constituent<SphericalParticle>( atom.createConstituent(), relativePosition.plus( atom.position.times( sizeScale ) ) ) );
        }

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( ZERO, 0.0 );
    }
}