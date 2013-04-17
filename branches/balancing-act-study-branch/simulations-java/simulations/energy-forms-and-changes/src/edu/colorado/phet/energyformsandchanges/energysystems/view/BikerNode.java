// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Biker;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the biker in the view.
 *
 * @author John Blanco
 */
public class BikerNode extends PositionableFadableModelElementNode {

    private final ButtonNode feedMeButton;

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
            setOffset( -getFullBoundsReference().width / 2, 100 );
        }} );

        // Add the images that will animate the front and back legs.
        final List<PNode> backLegImageNodes = new ArrayList<PNode>();
        final List<PNode> frontLegImageNodes = new ArrayList<PNode>();
        for ( int i = 0; i < Biker.NUM_LEG_IMAGES; i++ ) {
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
            PNode visibleFrontLeg = frontLegImageNodes.get( 0 );
            PNode visibleBackLeg = backLegImageNodes.get( 0 );

            public void apply( Double rotationalAngle ) {
                int currentImageIndex = Biker.mapAngleToImageIndex( rotationalAngle );
                visibleFrontLeg.setVisible( false );
                visibleBackLeg.setVisible( false );
                visibleFrontLeg = frontLegImageNodes.get( currentImageIndex );
                visibleFrontLeg.setVisible( true );
                visibleBackLeg = backLegImageNodes.get( currentImageIndex );
                visibleBackLeg.setVisible( true );
            }
        } );

        // Add the upper body.
        final PNode upperBodyNormal = new ModelElementImageNode( Biker.RIDER_NORMAL_UPPER_BODY_IMAGE, mvt );
        addChild( upperBodyNormal );
        final PNode upperBodyTired = new ModelElementImageNode( Biker.RIDER_TIRED_UPPER_BODY_IMAGE, mvt );
        addChild( upperBodyTired );

        // Add the energy chunk layers.
        addChild( new EnergyChunkLayer( biker.energyChunkList, biker.getObservablePosition(), mvt ) );

        // Add button to replenish the biker's energy.
        feedMeButton = new TextButtonNode( EnergyFormsAndChangesResources.Strings.FEED_ME, new PhetFont( 18 ), new Color( 0, 220, 0 ) );
        feedMeButton.setOffset( -feedMeButton.getFullBoundsReference().width / 2, upperBodyNormal.getFullBoundsReference().getMinY() - 45 );
        feedMeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                biker.replenishEnergyChunks();
            }
        } );
        biker.bikerHasEnergy.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean hasEnergy ) {
                feedMeButton.setVisible( !hasEnergy );
                upperBodyNormal.setVisible( hasEnergy );
                upperBodyTired.setVisible( !hasEnergy );
            }
        } );
        addChild( feedMeButton );

        // Add and observer that will turn the back wheel.
        final Point2D wheelRotationPoint = new Point2D.Double( spokesImage.getFullBoundsReference().getCenterX(),
                                                               spokesImage.getFullBoundsReference().getCenterY() );
        biker.getRearWheelAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                assert angle < 2 * Math.PI; // Limit this to one rotation.
                // Piccolo doesn't use the convention in physics where a
                // positive rotation is counter-clockwise, so we have to
                // invert the angle in the following calculation.
                double compensatedAngle = ( 2 * Math.PI - spokesImage.getRotation() ) % ( 2 * Math.PI );
                double delta = angle - compensatedAngle;
                spokesImage.rotateAboutPoint( -delta, wheelRotationPoint );
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