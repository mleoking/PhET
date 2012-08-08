// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.BeakerHeater;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the beaker heater view.
 *
 * @author John Blanco
 */
public class BeakerHeaterNode extends ImageBasedEnergySystemElementNode {

    public BeakerHeaterNode( final BeakerHeater beakerHeater, final ModelViewTransform mvt ) {
        super( beakerHeater, mvt );

        // Add the images.  Assumes that the last image is the energized heating coil.
        for ( int i = 0; i < beakerHeater.getImageList().size() - 1; i++ ) {
            addImageNode( beakerHeater.getImageList().get( i ) );
        }
        final PNode energizedCoil = addImageNode( beakerHeater.getImageList().get( beakerHeater.getImageList().size() - 1 ) );

        // Update the transparency of the lit bulb based on model element.
        beakerHeater.heatProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedCoil.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}