// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;

/**
 * This class represents an electrical generator that is powered by flowing
 * water or steam.
 *
 * @author John Blanco
 */
public class WaterPoweredGenerator extends EnergyConverter {

    public WaterPoweredGenerator() {
        super( new ModelElementImage( EnergyFormsAndChangesResources.Images.GENERATOR, 0.1, new ImmutableVector2D( 0, 0 ) ) );
    }

    @Override public double stepInTime( double dt, double incomingEnergy ) {
        // TODO - Implement.
        return 0;
    }
}
