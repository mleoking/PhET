// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ProjectedPositions;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ProjectedPositions.AtomPosition;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.sizeScale;

/**
 * A single sugar molecule (such as glucose or sucrose), which is used to build up sugar crystals
 *
 * @author Sam Reid
 */
public class SugarMolecule extends Compound<SphericalParticle> {

    public SugarMolecule( Vector2D relativePosition, double angle,

                          //Positions for the atoms within the molecule
                          ProjectedPositions positions ) {
        super( relativePosition, angle );

        //Add the glucose molecule atoms in the right locations, and in the right z-ordering
        for ( AtomPosition atomPosition : positions.getAtoms() ) {
            constituents.add( new Constituent<SphericalParticle>( atomPosition.createConstituent(), relativePosition.plus( atomPosition.position.times( sizeScale.get() ) ) ) );
        }

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( ZERO, 0.0 );
    }
}