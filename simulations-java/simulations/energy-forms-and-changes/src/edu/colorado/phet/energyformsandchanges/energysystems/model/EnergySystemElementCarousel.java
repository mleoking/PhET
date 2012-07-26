// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Carousel for containing and managing the position of energy system elements.
 *
 * @author John Blanco
 */
public class EnergySystemElementCarousel<T extends EnergySystemElement> extends Carousel<T> {
    public EnergySystemElementCarousel( Vector2D selectedElementPosition, Vector2D offsetBetweenElements ) {
        super( selectedElementPosition, offsetBetweenElements );

        // Activate and deactivate energy system elements as they come into
        // the center location.
        getAnimationInProgressProperty().addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean animationInProgress ) {
                if ( animationInProgress ) {
                    for ( EnergySystemElement energySystemElement : getElementList() ) {
                        energySystemElement.deactivate();
                    }
                }
                else {
                    getElement( targetIndex.get() ).activate();
                }
            }
        } );
    }
}
