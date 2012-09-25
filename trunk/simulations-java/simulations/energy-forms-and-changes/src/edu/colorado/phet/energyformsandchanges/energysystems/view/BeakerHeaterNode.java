// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.BeakerHeater;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node that represents the beaker heater view.
 *
 * @author John Blanco
 */
public class BeakerHeaterNode extends ImageBasedEnergySystemElementNode {

    public BeakerHeaterNode( final BeakerHeater beakerHeater, final ModelViewTransform mvt ) {
        super( beakerHeater, mvt );

        // Add the images that are used to depict this element along with the
        // layer that will contain the energy chunks.
        addImageNode( BeakerHeater.WIRE_STRAIGHT_IMAGE );
        addImageNode( BeakerHeater.WIRE_CURVE_IMAGE );
        addChild( new EnergyChunkLayer( beakerHeater.energyChunkList, beakerHeater.getObservablePosition(), mvt ) );
        addImageNode( BeakerHeater.ELEMENT_BASE_IMAGE );
        addImageNode( BeakerHeater.HEATER_ELEMENT_OFF_IMAGE );
        final PImage energizedCoil = addImageNode( BeakerHeater.HEATER_ELEMENT_ON_IMAGE );

        // Update the transparency of the lit bulb based on model element.
        beakerHeater.heatProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedCoil.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}