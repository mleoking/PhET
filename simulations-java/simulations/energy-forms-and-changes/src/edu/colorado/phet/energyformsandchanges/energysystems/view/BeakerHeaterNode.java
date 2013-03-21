// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.view.BeakerView;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.BeakerHeater;
import edu.colorado.phet.energyformsandchanges.intro.view.SensingThermometerNode;
import edu.colorado.phet.energyformsandchanges.intro.view.ThermometerNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node that represents the beaker heater view.
 *
 * @author John Blanco
 */
public class BeakerHeaterNode extends ImageBasedEnergySystemElementNode {

    public BeakerHeaterNode( IClock clock, final BeakerHeater beakerHeater, BooleanProperty energyChunksVisible, final ModelViewTransform mvt ) {
        super( beakerHeater, mvt );

        // Add the images that are used to depict this element along with the
        // layer that will contain the energy chunks.
        addImageNode( BeakerHeater.WIRE_STRAIGHT_IMAGE );
        addImageNode( BeakerHeater.WIRE_CURVE_IMAGE );
        addImageNode( BeakerHeater.ELEMENT_BASE_BACK_IMAGE );
        addImageNode( BeakerHeater.HEATER_ELEMENT_OFF_IMAGE );
        final PImage energizedCoil = addImageNode( BeakerHeater.HEATER_ELEMENT_ON_IMAGE );
        addChild( new EnergyChunkLayer( beakerHeater.energyChunkList, beakerHeater.getObservablePosition(), mvt ) );
        addImageNode( BeakerHeater.ELEMENT_BASE_FRONT_IMAGE );

        // Add the beaker.  A compensating MVT is needed because the beaker
        // node is being added as a child of this node, but wants to set its
        // own offset in model space.
        ModelViewTransform compensatingMvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( -mvt.modelToViewDeltaX( beakerHeater.getPosition().x ),
                                                                                                              -mvt.modelToViewDeltaY( beakerHeater.getPosition().y ) ),
                                                                                          mvt.getTransform().getScaleX(),
                                                                                          mvt.getTransform().getScaleY() );
        BeakerView beakerView = new BeakerView( clock, beakerHeater.beaker, energyChunksVisible, compensatingMvt );
        addChild( beakerView.getBackNode() );
        addChild( new EnergyChunkLayer( beakerHeater.radiatedEnergyChunkList, beakerHeater.getObservablePosition(), mvt ) );
        addChild( beakerView.getFrontNode() );

        // Add the thermometer.
        addChild( new SensingThermometerNode( beakerHeater.thermometer ){{
            setPickable( false );
            setChildrenPickable( false );
            setOffset( 75, -220 );
        }} );

        // Update the transparency of the hot element to make the dark element
        // appear to heat up.
        beakerHeater.heatProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double litProportion ) {
                energizedCoil.setTransparency( litProportion.floatValue() );
            }
        } );
    }
}