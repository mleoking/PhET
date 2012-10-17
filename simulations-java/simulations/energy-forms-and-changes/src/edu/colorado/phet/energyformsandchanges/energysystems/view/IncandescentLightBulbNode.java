// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.IncandescentLightBulb;
import edu.colorado.phet.energyformsandchanges.energysystems.model.LightBulb;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the incandescent light bulb in the view.
 *
 * @author John Blanco
 */
public class IncandescentLightBulbNode extends ImageBasedEnergySystemElementNode {

    public IncandescentLightBulbNode( final IncandescentLightBulb lightBulb, ObservableProperty<Boolean> energyChunksVisible, final ModelViewTransform mvt ) {
        super( lightBulb, mvt );

        // Add the light rays.
        final LightRays lightRays = new LightRays( new Vector2D( 0, 0 ), 30, 400, 20, Math.PI / 4, Color.YELLOW );
        addChild( lightRays );
        energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyChunksVisible ) {
                // Only show the light rays when the energy chunks are not shown.
                lightRays.setVisible( !energyChunksVisible );
            }
        } );

        // Add the images and the layer that will contain the energy chunks.
        addImageNode( LightBulb.WIRE_FLAT_IMAGE );
        addImageNode( LightBulb.WIRE_CURVE_IMAGE );
        addImageNode( LightBulb.ELEMENT_BASE_BACK_IMAGE );
        addChild( new EnergyChunkLayer( lightBulb.energyChunkList, lightBulb.getObservablePosition(), mvt ) );
        addImageNode( LightBulb.ELEMENT_BASE_FRONT_IMAGE );
        addImageNode( IncandescentLightBulb.NON_ENERGIZED_BULB );
        final PNode energizedBulb = addImageNode( IncandescentLightBulb.ENERGIZED_BULB );

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