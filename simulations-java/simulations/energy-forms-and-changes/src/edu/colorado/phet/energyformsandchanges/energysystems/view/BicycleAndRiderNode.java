// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

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
        final PNode rearWheelImage = new ModelElementImageNode( BicycleAndRider.REAR_WHEEL_IMAGE, mvt );
        addChild( rearWheelImage );
        PNode backLegRootNode = new PNode();
        addChild( backLegRootNode );
        addChild( new ModelElementImageNode( BicycleAndRider.FRAME_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( BicycleAndRider.RIDER_IMAGE, mvt ) );
        PNode frontLegRootNode = new PNode();
        addChild( frontLegRootNode );

        // Add slider that will control pedaling rate.
        addChild( new CrankRateSlider( bicycleAndRider.targetCrankAngularVelocity ) {{
            setOffset( -getFullBoundsReference().width / 2, 120 );
        }} );

        // Add the images that will animate the front and back legs.
        int numAnimationImages = BicycleAndRider.BACK_LEG_IMAGES.size();
        assert numAnimationImages == BicycleAndRider.FRONT_LEG_IMAGES.size();
        final List<PNode> backLegImageNodes = new ArrayList<PNode>();
        final List<PNode> frontLegImageNodes = new ArrayList<PNode>();
        for ( int i = 0; i < numAnimationImages; i++ ) {
            PNode backLegImageNode = new ModelElementImageNode( BicycleAndRider.BACK_LEG_IMAGES.get( i ), mvt );
            backLegImageNode.setVisible( false );
            backLegImageNodes.add( backLegImageNode );
            backLegRootNode.addChild( backLegImageNode );
            PNode frontLegImageNode = new ModelElementImageNode( BicycleAndRider.FRONT_LEG_IMAGES.get( i ), mvt );
            frontLegImageNode.setVisible( false );
            frontLegImageNodes.add( frontLegImageNode );
            frontLegRootNode.addChild( frontLegImageNode );
        }
        bicycleAndRider.getCrankAngle().addObserver( new VoidFunction1<Double>() {
            int numImages = backLegImageNodes.size();

            public void apply( Double rotationalAngle ) {
                int currentImageIndex = (int) ( Math.floor( ( rotationalAngle % ( 2 * Math.PI ) ) / ( Math.PI * 2 / backLegImageNodes.size() ) ) );
                int previousImageIndex = currentImageIndex == 0 ? numImages - 1 : currentImageIndex - 1;
                backLegImageNodes.get( previousImageIndex ).setVisible( false );
                backLegImageNodes.get( currentImageIndex ).setVisible( true );
                frontLegImageNodes.get( previousImageIndex ).setVisible( false );
                frontLegImageNodes.get( currentImageIndex ).setVisible( true );
            }
        } );

        // Add and observer that will turn the back wheel.
        final Point2D wheelRotationPoint = new Point2D.Double( rearWheelImage.getFullBoundsReference().getCenterX(),
                                                               rearWheelImage.getFullBoundsReference().getCenterY() );
        bicycleAndRider.getRearWheelAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                double delta = angle - rearWheelImage.getRotation();
                rearWheelImage.rotateAboutPoint( delta, wheelRotationPoint );
            }
        } );

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