// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * @author John Blanco
 */
public class EnergySystemElementCarousel extends Carousel<EnergySystemElement> {

    public EnergySystemElementCarousel( Vector2D selectedElementPosition, Vector2D offsetBetweenElements ) {
        super( selectedElementPosition, offsetBetweenElements );
    }
}
