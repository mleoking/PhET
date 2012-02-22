// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
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

/**
 * Primary canvas for the Messenger RNA Production tab.
 *
 * @author John Blanco
 */
public class MessengerRnaProductionCanvas extends PhetPCanvas {

    // Stage size, based on default screen size.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    // Inset for several of the controls.
    private static final double INSET = 15;

    private final ModelViewTransform mvt;

    // PNodes that are used as layers and that are involved in the zoom
    // functionality.
    private PNode modelRootNode;

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
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.75 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.4 ) ),
                0.25 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 171, 202, 217 ) );

        // Set up the node where all controls should be placed.  These will
        // stay in one place and not scroll.
        final PNode controlsRootNode = new PNode();
        addWorldChild( controlsRootNode );

        // Set up the root node for all model objects.  Nodes placed under
        // this one will scroll when the user moves along the DNA strand.
        modelRootNode = new PNode();
        addWorldChild( modelRootNode );

        // Add some layers for enforcing some z-order relationships needed in
        // order to keep things looking good.
        PNode dnaLayer = new PNode();
        modelRootNode.addChild( dnaLayer );
        final PNode biomoleculeToolBoxLayer = new PNode();
        modelRootNode.addChild( biomoleculeToolBoxLayer );
        final PNode messengerRnaLayer = new PNode();
        modelRootNode.addChild( messengerRnaLayer );
        final PNode topBiomoleculeLayer = new PNode();
        modelRootNode.addChild( topBiomoleculeLayer );
        final PNode placementHintLayer = new PNode();
        modelRootNode.addChild( placementHintLayer );

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

        // Add any initial molecules.
        for ( MobileBiomolecule biomolecule : model.mobileBiomoleculeList ) {
            topBiomoleculeLayer.addChild( new MobileBiomoleculeNode( mvt, biomolecule ) );
        }

        // Watch for and handle comings and goings of biomolecules in the model.
        // Most, but not all, of the biomolecules are handled by this.  Some
        // others are handled as special cases.
        model.mobileBiomoleculeList.addElementAddedObserver( new VoidFunction1<MobileBiomolecule>() {
            public void apply( final MobileBiomolecule addedBiomolecule ) {
                final PNode biomoleculeNode;
                biomoleculeNode = new MobileBiomoleculeNode( mvt, addedBiomolecule );
                topBiomoleculeLayer.addChild( biomoleculeNode );
                model.mobileBiomoleculeList.addElementRemovedObserver( new VoidFunction1<MobileBiomolecule>() {
                    public void apply( MobileBiomolecule removedBiomolecule ) {
                        if ( removedBiomolecule == addedBiomolecule ) {
                            topBiomoleculeLayer.removeChild( biomoleculeNode );
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

        // Add the Reset All button.
        controlsRootNode.addChild( new ResetAllButtonNode( new Resettable[] { model }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            centerFullBoundsOnPoint( STAGE_SIZE.getWidth() - getFullBoundsReference().getWidth() - INSET,
                                     STAGE_SIZE.getHeight() - getFullBoundsReference().getHeight() - INSET );
        }} );
    }
}
