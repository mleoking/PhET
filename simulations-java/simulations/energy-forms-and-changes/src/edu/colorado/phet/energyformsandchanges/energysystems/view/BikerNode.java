// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Biker;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the biker in the view.
 *
 * @author John Blanco
 */
public class BikerNode extends PositionableFadableModelElementNode {

    public BikerNode( final Biker biker, final ModelViewTransform mvt ) {
        super( biker, mvt );

        // Create and add the various image nodes.
        final PNode spokesImage = new ModelElementImageNode( Biker.REAR_WHEEL_SPOKES_IMAGE, mvt );
        addChild( spokesImage );
        PNode backLegRootNode = new PNode();
        addChild( backLegRootNode );
        addChild( new ModelElementImageNode( Biker.FRAME_IMAGE, mvt ) );
        PNode frontLegRootNode = new PNode();
        addChild( frontLegRootNode );

        // Add slider that will control pedaling rate.
        addChild( new CrankRateSlider( biker.targetCrankAngularVelocity ) {{
            setOffset( -getFullBoundsReference().width / 2, 120 );
        }} );

        // Add the images that will animate the front and back legs.
        int numAnimationImages = Biker.BACK_LEG_IMAGES.size();
        assert numAnimationImages == Biker.FRONT_LEG_IMAGES.size();
        final List<PNode> backLegImageNodes = new ArrayList<PNode>();
        final List<PNode> frontLegImageNodes = new ArrayList<PNode>();
        for ( int i = 0; i < numAnimationImages; i++ ) {
            PNode backLegImageNode = new ModelElementImageNode( Biker.BACK_LEG_IMAGES.get( i ), mvt );
            backLegImageNode.setVisible( false );
            backLegImageNodes.add( backLegImageNode );
            backLegRootNode.addChild( backLegImageNode );
            PNode frontLegImageNode = new ModelElementImageNode( Biker.FRONT_LEG_IMAGES.get( i ), mvt );
            frontLegImageNode.setVisible( false );
            frontLegImageNodes.add( frontLegImageNode );
            frontLegRootNode.addChild( frontLegImageNode );
        }
        biker.getCrankAngle().addObserver( new VoidFunction1<Double>() {
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

        // Add the upper body.
        addChild( new ModelElementImageNode( Biker.RIDER_UPPER_BODY_IMAGE, mvt ) );

        // Add and observer that will turn the back wheel.
        final Point2D wheelRotationPoint = new Point2D.Double( spokesImage.getFullBoundsReference().getCenterX(),
                                                               spokesImage.getFullBoundsReference().getCenterY() );
        biker.getRearWheelAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                assert angle < 2 * Math.PI; // Limit this to one rotation.
                // Piccolo doesn't use the convention in physics where a
                // positive rotation is counter-clockwise, so we have to
                // invert the angle in the following calculation.
                double delta = -angle - spokesImage.getRotation();
                spokesImage.rotateAboutPoint( delta, wheelRotationPoint );
            }
        } );
    }

    private static class CrankRateSlider extends PNode {
        public CrankRateSlider( final SettableProperty<Double> value ) {
            addChild( new ControlPanelNode( new HSliderNode( EnergyFormsAndChangesSimSharing.UserComponents.bicyclePedalRateSlider,
                                                             0,
                                                             Biker.MAX_ANGULAR_VELOCITY_OF_CRANK,
                                                             value ),
                                            EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR ) );
        }
    }
}