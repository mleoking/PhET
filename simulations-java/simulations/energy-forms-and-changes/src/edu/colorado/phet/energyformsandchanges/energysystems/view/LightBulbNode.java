// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
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

        // Add the light rays.
        final LightRays lightRays = new LightRays( new Vector2D( 0, 0 ), 30, 400, 20, Math.PI / 4, Color.YELLOW );
        addChild( lightRays );

        // Add the images and the layer that will contain the energy chunks.
        addImageNode( LightBulb.WIRE_FLAT_IMAGE );
        addImageNode( LightBulb.WIRE_CURVE_IMAGE );
        addImageNode( lightBulb.offImage );
        addChild( new EnergyChunkLayer( lightBulb.energyChunkList, lightBulb.getObservablePosition(), mvt ) );
        addImageNode( LightBulb.ELEMENT_BASE_IMAGE );
        final PNode energizedBulb = addImageNode( lightBulb.onImage );

        // Center the light rays on the bulb image.
        lightRays.setOffset( energizedBulb.getFullBoundsReference().getCenterX(),
                             energizedBulb.getFullBoundsReference().getCenterY() - energizedBulb.getFullBoundsReference().getHeight() * 0.10 );


        // Update the transparency of the lit bulb based on model element.
        lightBulb.litProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedBulb.setTransparency( litProportion.floatValue() );
                lightRays.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}