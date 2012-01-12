// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.GlucosePositions;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarMolecule;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * A single glucose molecule, which is used in lattice creation
 *
 * @author Sam Reid
 */
public class Glucose extends SugarMolecule {
    public Glucose() {
        this( ZERO, Math.random() * 2 * Math.PI );
    }

    public Glucose( ImmutableVector2D relativePosition, double angle ) {
        super( relativePosition, angle, new GlucosePositions() );
    }
}