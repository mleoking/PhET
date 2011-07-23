// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CarbonIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.HydrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NeutralOxygenParticle;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SucrosePositions;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.sizeScale;

/**
 * A single sucrose molecule, which is used in lattice creation
 *
 * @author Sam Reid
 */
public class SucroseMolecule extends Compound {

    public SucroseMolecule( ImmutableVector2D relativePosition ) {
        super( relativePosition );

        //Add the salt molecule atoms in the right locations
        SucrosePositions sucrosePositions = new SucrosePositions();
        for ( ImmutableVector2D offset : sucrosePositions.getHydrogenPositions() ) {
            constituents.add( new Constituent( new HydrogenIonParticle(), relativePosition.plus( offset.times( sizeScale ) ) ) );
        }
        for ( ImmutableVector2D offset : sucrosePositions.getCarbonPositions() ) {
            constituents.add( new Constituent( new CarbonIonParticle(), relativePosition.plus( offset.times( sizeScale ) ) ) );
        }
        for ( ImmutableVector2D offset : sucrosePositions.getOxygenPositions() ) {
            constituents.add( new Constituent( new NeutralOxygenParticle(), relativePosition.plus( offset.times( sizeScale ) ) ) );
        }
    }
}