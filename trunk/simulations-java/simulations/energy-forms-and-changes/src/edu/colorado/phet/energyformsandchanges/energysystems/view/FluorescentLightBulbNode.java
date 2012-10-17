// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FluorescentLightBulb;
import edu.colorado.phet.energyformsandchanges.energysystems.model.LightBulb;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the fluorescent light bulb in the view.
 *
 * @author John Blanco
 */
public class FluorescentLightBulbNode extends ImageBasedEnergySystemElementNode {

    public FluorescentLightBulbNode( final FluorescentLightBulb lightBulb, final ModelViewTransform mvt, ObservableProperty<Boolean> energyChunksVisible ) {
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
        addImageNode( FluorescentLightBulb.BACK_OFF );
        final PNode energizedBack = addImageNode( FluorescentLightBulb.BACK_ON );
        addChild( new EnergyChunkLayer( lightBulb.energyChunkList, lightBulb.getObservablePosition(), mvt ) );
        addImageNode( LightBulb.ELEMENT_BASE_FRONT_IMAGE );
        addImageNode( FluorescentLightBulb.FRONT_OFF );
        final PNode energizedFront = addImageNode( FluorescentLightBulb.FRONT_ON );

        // Make bulb partially transparent when energy chunks visible.
        energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        } );

        // Center the light rays on the bulb image.
        lightRays.setOffset( energizedFront.getFullBoundsReference().getCenterX(),
                             energizedFront.getFullBoundsReference().getCenterY() - energizedFront.getFullBoundsReference().getHeight() * 0.10 );

        // Update the transparency of the lit bulb based on model element.
        lightBulb.litProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedFront.setTransparency( litProportion.floatValue() );
                energizedBack.setTransparency( litProportion.floatValue() );
                lightRays.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}