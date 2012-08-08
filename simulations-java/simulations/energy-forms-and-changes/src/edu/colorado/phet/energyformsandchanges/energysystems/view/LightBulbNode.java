// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.LightBulb;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the light bulb in the view.
 *
 * @author John Blanco
 */
public class LightBulbNode extends ImageBasedEnergySystemElementNode {

    public LightBulbNode( final LightBulb lightBulb, final ModelViewTransform mvt ) {
        super( lightBulb, mvt );

        // Add the images.  Assumes that the last image is the energized bulb.
        for ( int i = 0; i < lightBulb.getImageList().size() - 1; i++ ) {
            addImageNode( lightBulb.getImageList().get( i ) );
        }
        final PNode energizedBulb = addImageNode( lightBulb.getImageList().get( lightBulb.getImageList().size() - 1 ) );

        // Update the transparency of the lit bulb based on model element.
        lightBulb.litProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedBulb.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}