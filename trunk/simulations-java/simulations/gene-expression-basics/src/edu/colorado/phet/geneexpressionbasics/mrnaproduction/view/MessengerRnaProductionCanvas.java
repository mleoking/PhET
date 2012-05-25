// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.DnaMoleculeNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MessengerRnaNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.PlacementHintNode;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.model.MessengerRnaProductionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents.negativeTranscriptionFactorCheckBox;

/**
 * Primary canvas for the Messenger RNA Production tab.
 *
 * @author John Blanco
 */
public class MessengerRnaProductionCanvas extends PhetPCanvas implements Resettable {

    // Stage size, based on default screen size.
    private static final Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    // For debug.
    private static final boolean SHOW_MOTION_BOUNDS = false;

    // Inset for several of the controls.
    private static final double INSET = 15;

    private final ModelViewTransform mvt;
    private final BooleanProperty clockRunning = new BooleanProperty( false );
    private final BooleanProperty negativeTranscriptionFactorEnabled = new BooleanProperty( false );

    /**
     * Constructor.
     *
     * @param model
     */
    public MessengerRnaProductionCanvas( final MessengerRnaProductionModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.4 ) ),
                0.20 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 171, 202, 217 ) );

        // Set up the root node for all model objects.  Nodes placed under
        // this one will scroll when the user moves along the DNA strand.
        PNode modelRootNode = new PNode();
        addWorldChild( modelRootNode );

        // Add some layers for enforcing some z-order relationships needed in
        // order to keep things looking good.
        final PNode dnaLayer = new PNode();
        modelRootNode.addChild( dnaLayer );
        final PNode biomoleculeToolBoxLayer = new PNode();
        modelRootNode.addChild( biomoleculeToolBoxLayer );
        final PNode messengerRnaLayer = new PNode();
        modelRootNode.addChild( messengerRnaLayer );
        final PNode topBiomoleculeLayer = new PNode();
        modelRootNode.addChild( topBiomoleculeLayer );
        final PNode placementHintLayer = new PNode();
        modelRootNode.addChild( placementHintLayer );
        final PNode controlsNode = new PNode();
        addWorldChild( controlsNode );

        // Add the representation of the DNA strand.
        final PNode dnaMoleculeNode = new DnaMoleculeNode( model.getDnaMolecule(), mvt, 5, false );
        dnaLayer.addChild( dnaMoleculeNode );

        // Add the placement hints that go on the DNA molecule.  These exist on
        // their own layer so that they can be seen above any molecules that
        // are attached to the DNA strand.
        for ( Gene gene : model.getDnaMolecule().getGenes() ) {
            for ( PlacementHint placementHint : gene.getPlacementHints() ) {
                placementHintLayer.addChild( new PlacementHintNode( mvt, placementHint ) );
            }
        }

        // Add motion bounds indicator, if turned on.
        if ( SHOW_MOTION_BOUNDS ) {
            topBiomoleculeLayer.addChild( new PhetPPath( mvt.modelToView( model.moleculeMotionBounds.getBounds() ), new BasicStroke( 2 ), Color.RED ) );
        }

        // Get a reference to the gene being controlled.
        Gene gene = model.getDnaMolecule().getGenes().get( 0 );

        // Add the nodes that allow the user to control the concentrations and
        // affinities.
        TranscriptionFactorControlPanel positiveTranscriptionFactorControlPanel =
                new TranscriptionFactorControlPanel( model,
                                                     MessengerRnaProductionModel.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG,
                                                     gene.getTranscriptionFactorAffinityProperty( MessengerRnaProductionModel.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG ) );
        controlsNode.addChild( positiveTranscriptionFactorControlPanel );
        PolymeraseAffinityControlPanel polymeraseAffinityControlPanel = new PolymeraseAffinityControlPanel( MessengerRnaProductionModel.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG,
                                                                                                            positiveTranscriptionFactorControlPanel.getFullBoundsReference().height,
                                                                                                            gene.getPolymeraseAffinityProperty() );
        controlsNode.addChild( polymeraseAffinityControlPanel );
        final TranscriptionFactorControlPanel negativeTranscriptionFactorControlPanel =
                new TranscriptionFactorControlPanel( model,
                                                     MessengerRnaProductionModel.NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG,
                                                     gene.getTranscriptionFactorAffinityProperty( MessengerRnaProductionModel.NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG ) );
        controlsNode.addChild( negativeTranscriptionFactorControlPanel );

        // Add the check box for showing/hiding the control panel for the
        // negative transcription factor.
        PNode negativeFactorEnabledCheckBox = new PropertyCheckBoxNode( negativeTranscriptionFactorCheckBox,
                                                                        GeneExpressionBasicsResources.Strings.NEGATIVE_TRANSCRIPTION_FACTOR,
                                                                        negativeTranscriptionFactorEnabled );
        controlsNode.addChild( negativeFactorEnabledCheckBox );

        // Only show the control for the negative transcription factor if it
        // is enabled.
        negativeTranscriptionFactorEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                negativeTranscriptionFactorControlPanel.setVisible( enabled );
                if ( !enabled ) {
                    // When the negative transcription factor control is
                    // hidden, there should be no negative factors.
                    model.negativeTranscriptionFactorCount.reset();
                }
            }
        } );

        // Add the floating clock control.
        final ConstantDtClock modelClock = model.getClock();
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isRunning ) {
                modelClock.setRunning( isRunning );
            }
        } );
        final FloatingClockControlNode floatingClockControlNode = new FloatingClockControlNode( clockRunning, null,
                                                                                                model.getClock(), null,
                                                                                                new Property<Color>( Color.white ) );
        controlsNode.addChild( floatingClockControlNode );

        // Make sure that the floating clock control sees the change when the
        // clock gets started.
        model.getClock().addClockListener( new edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter() {
            @Override public void clockStarted( edu.colorado.phet.common.phetcommon.model.clock.ClockEvent clockEvent ) {
                clockRunning.set( true );
            }
        } );

        // Add the Reset All button.
        ResetAllButtonNode resetAllButton = new ResetAllButtonNode( new Resettable[] { model, this }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
        }};
        controlsNode.addChild( resetAllButton );

        // Lay out the controls.
        positiveTranscriptionFactorControlPanel.setOffset( INSET,
                                                           STAGE_SIZE.getHeight() - positiveTranscriptionFactorControlPanel.getFullBoundsReference().height - INSET );
        polymeraseAffinityControlPanel.setOffset( positiveTranscriptionFactorControlPanel.getFullBoundsReference().getMaxX() + 10,
                                                  positiveTranscriptionFactorControlPanel.getFullBoundsReference().getMinY() );
        negativeTranscriptionFactorControlPanel.setOffset( polymeraseAffinityControlPanel.getFullBoundsReference().getMaxX() + 10,
                                                           polymeraseAffinityControlPanel.getFullBoundsReference().getMinY() );
        double middleXOfUnusedSpace = ( negativeTranscriptionFactorControlPanel.getFullBoundsReference().getMaxX() +
                                        STAGE_SIZE.getWidth() ) / 2;

        resetAllButton.setOffset( middleXOfUnusedSpace - resetAllButton.getFullBoundsReference().width / 2,
                                  positiveTranscriptionFactorControlPanel.getFullBoundsReference().getMaxY() - resetAllButton.getFullBoundsReference().height );
        negativeFactorEnabledCheckBox.setOffset( middleXOfUnusedSpace - negativeFactorEnabledCheckBox.getFullBoundsReference().width / 2,
                                                 resetAllButton.getFullBoundsReference().getMinY() - negativeFactorEnabledCheckBox.getFullBoundsReference().height - 10 );
        floatingClockControlNode.setOffset( middleXOfUnusedSpace - floatingClockControlNode.getFullBoundsReference().width / 2,
                                            negativeFactorEnabledCheckBox.getFullBoundsReference().getMinY() - floatingClockControlNode.getFullBoundsReference().height - 10 );

        // Watch for and handle comings and goings of biomolecules in the model.
        // Most, but not all, of the biomolecules are handled by this.  A few
        // others are handled as special cases.
        model.mobileBiomoleculeList.addElementAddedObserver( new VoidFunction1<MobileBiomolecule>() {
            public void apply( final MobileBiomolecule addedBiomolecule ) {

                final PNode biomoleculeNode;
                biomoleculeNode = new MobileBiomoleculeNode( mvt, addedBiomolecule );

                // On this tab, users can't directly interact with individual biomolecules.
                biomoleculeNode.setPickable( false );
                biomoleculeNode.setChildrenPickable( false );

                // Add a listener that moves the child on to a lower layer when
                // it connects to the DNA so that we see the desired overlap
                // behavior.
                addedBiomolecule.attachedToDna.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean attachedToDna ) {
                        if ( attachedToDna ) {
                            topBiomoleculeLayer.removeChild( biomoleculeNode );
                            dnaLayer.addChild( biomoleculeNode );
                        }
                        else {
                            dnaLayer.removeChild( biomoleculeNode );
                            topBiomoleculeLayer.addChild( biomoleculeNode );
                        }
                    }
                } );
                model.mobileBiomoleculeList.addElementRemovedObserver( new VoidFunction1<MobileBiomolecule>() {
                    public void apply( MobileBiomolecule removedBiomolecule ) {
                        if ( removedBiomolecule == addedBiomolecule ) {
                            if ( topBiomoleculeLayer.isAncestorOf( biomoleculeNode ) ) {
                                topBiomoleculeLayer.removeChild( biomoleculeNode );
                            }
                            else if ( dnaLayer.isAncestorOf( biomoleculeNode ) ) {
                                dnaLayer.removeChild( biomoleculeNode );
                            }
                        }
                    }
                } );
            }
        } );

        // Watch for and handle comings and goings of messenger RNA.
        model.messengerRnaList.addElementAddedObserver( new VoidFunction1<MessengerRna>() {
            public void apply( final MessengerRna addedMessengerRna ) {
                final PNode messengerRnaNode;
                messengerRnaNode = new MessengerRnaNode( mvt, addedMessengerRna );
                messengerRnaLayer.addChild( messengerRnaNode );
                model.messengerRnaList.addElementRemovedObserver( new VoidFunction1<MessengerRna>() {
                    public void apply( MessengerRna removedMessengerRna ) {
                        if ( removedMessengerRna == addedMessengerRna ) {
                            messengerRnaLayer.removeChild( messengerRnaNode );
                        }
                    }
                } );
            }
        } );
    }

    public void reset() {
        clockRunning.set( true );
        negativeTranscriptionFactorEnabled.reset();
    }

    // Convenience class for check boxes, prevents code duplication.
    private static class PropertyCheckBoxNode extends PNode {
        private PropertyCheckBoxNode( IUserComponent userComponent, String text, BooleanProperty property ) {
            PropertyCheckBox checkBox = new PropertyCheckBox( userComponent, text, property );
            checkBox.setFont( new PhetFont( 18 ) );
            checkBox.setBackground( new Color( 0, 0, 0, 0 ) );
            addChild( new PSwing( checkBox ) );
        }
    }
}
