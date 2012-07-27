// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FluorescentLightBulb;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the fluorescent light bulb in the view.
 *
 * @author John Blanco
 */
public class FluorescentLightBulbNode extends ImageBasedEnergySystemElementNode {

    public FluorescentLightBulbNode( final FluorescentLightBulb fluorescentLightBulb, final ModelViewTransform mvt ) {
        super( fluorescentLightBulb, mvt );

        // Add the images.
        addImageNode( FluorescentLightBulb.BULB_BASE );
        addImageNode( FluorescentLightBulb.NON_ENERGIZED_BULB );
        final PNode energizedBulb = addImageNode( FluorescentLightBulb.ENERGIZED_BULB );

        // Update the transparency of the lit bulb based on model element.
        fluorescentLightBulb.litProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedBulb.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}