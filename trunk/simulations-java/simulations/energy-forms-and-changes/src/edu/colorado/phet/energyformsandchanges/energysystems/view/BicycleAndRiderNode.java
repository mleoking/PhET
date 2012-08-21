// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.energysystems.model.BicycleAndRider;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the water-powered generator in the view.
 *
 * @author John Blanco
 */
public class BicycleAndRiderNode extends PNode {

    public BicycleAndRiderNode( final BicycleAndRider bicycleAndRider, final ModelViewTransform mvt ) {

        // Create and add the various image nodes.
        addChild( new ModelElementImageNode( BicycleAndRider.REAR_WHEEL_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( BicycleAndRider.BACK_LEG_AND_CRANK, mvt ) );
        addChild( new ModelElementImageNode( BicycleAndRider.FRAME_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( BicycleAndRider.RIDER_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( BicycleAndRider.FRONT_LEG, mvt ) );

        // Add slider that will control pedaling rate.
        addChild( new CrankRateSlider( bicycleAndRider.targetCrankAngularVelocity ) {{
            setOffset( -getFullBoundsReference().width / 2, 120 );
        }} );

        // Update the overall offset based on the model position.
        bicycleAndRider.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }

    private static class CrankRateSlider extends PNode {
        public CrankRateSlider( final SettableProperty<Double> value ) {
            addChild( new ControlPanelNode( new HSliderNode( EnergyFormsAndChangesSimSharing.UserComponents.bicyclePedalRateSlider,
                                                             0,
                                                             BicycleAndRider.MAX_ANGULAR_VELOCITY_OF_CRANK,
                                                             value ),
                                            EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR ) );
        }
    }
}