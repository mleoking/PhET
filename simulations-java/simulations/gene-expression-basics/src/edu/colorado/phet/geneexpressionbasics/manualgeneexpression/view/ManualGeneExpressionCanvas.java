// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Images.GRAY_ARROW;

/**
 * @author John Blanco
 */
public class ManualGeneExpressionCanvas extends PhetPCanvas implements Resettable {
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;
    private PTransformActivity activity;
    private final Vector2D viewportOffset = new Vector2D( 0, 0 );
    private final List<BiomoleculeToolBoxNode> biomoleculeToolBoxNodeList = new ArrayList<BiomoleculeToolBoxNode>();

    public ManualGeneExpressionCanvas( final ManualGeneExpressionModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.70 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 190, 231, 251 ) );

        // Set up the node where all controls should be placed.  These will
        // stay in one place and not scroll.
        PNode controlsRootNode = new PNode();
        addWorldChild( controlsRootNode );

        // Set up the root node for all model objects.  Nodes placed under
        // this one will scroll when the user moves along the DNA strand.
        final PNode modelRootNode = new PNode();
        addWorldChild( modelRootNode );

        // Add the representation of the DNA strand.
        final PNode dnaMoleculeNode = new DnaMoleculeNode( model.getDnaMolecule(), mvt );
        modelRootNode.addChild( dnaMoleculeNode );

        // Add any initial molecules.
        for ( MobileBiomolecule biomolecule : model.getMobileBiomoleculeList() ) {
            modelRootNode.addChild( new MobileBiomoleculeNode( mvt, biomolecule ) );
        }

        // Watch for and handle comings and goings of molecules in the mode.
        model.mobileBiomoleculeList.addElementAddedObserver( new VoidFunction1<MobileBiomolecule>() {
            public void apply( final MobileBiomolecule addedBiomolecule ) {
                final PNode biomoleculeNode = new MobileBiomoleculeNode( mvt, addedBiomolecule );
                modelRootNode.addChild( biomoleculeNode );
                model.mobileBiomoleculeList.addElementRemovedObserver( new VoidFunction1<MobileBiomolecule>() {
                    public void apply( MobileBiomolecule removedBiomolecule ) {
                        if ( removedBiomolecule == addedBiomolecule ) {
                            modelRootNode.removeChild( biomoleculeNode );
                        }
                    }
                } );
            }
        } );

        // Add buttons for moving to next and previous genes.
        // TODO: i18n
        final HTMLImageButtonNode nextGeneButton = new HTMLImageButtonNode( "Next Gene", GRAY_ARROW ) {{
            setTextPosition( TextPosition.LEFT );
            setFont( new PhetFont( 20 ) );
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - 20, mvt.modelToViewY( model.getDnaMolecule().getLeftEdgePos().getY() ) + 90 );
            setBackground( Color.GREEN );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.nextGene();
                }
            } );
            model.isLastGeneActive.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean lastGeneActive ) {
                    setEnabled( !lastGeneActive );
                }
            } );
        }};
        controlsRootNode.addChild( nextGeneButton );
        // TODO: i18n
        controlsRootNode.addChild( new HTMLImageButtonNode( "Prev Gene", flipX( GRAY_ARROW ) ) {{
            setTextPosition( TextPosition.RIGHT );
            setFont( new PhetFont( 20 ) );
            setOffset( 20, mvt.modelToViewY( model.getDnaMolecule().getLeftEdgePos().getY() ) + 90 );
            setBackground( Color.GREEN );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.previousGene();
                }
            } );
            model.isFirstGeneActive.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean firstGeneActive ) {
                    setEnabled( !firstGeneActive );
                }
            } );
        }} );

        // Add the Reset All button.
        controlsRootNode.addChild( new ResetAllButtonNode( new Resettable[] { this, model }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            centerFullBoundsOnPoint( nextGeneButton.getFullBoundsReference().getCenterX(), nextGeneButton.getFullBoundsReference().getMaxY() + 40 );
        }} );

        // Monitor the active gene and move the view port to be centered on it
        // whenever it changes.
        model.activeGene.addObserver( new VoidFunction1<Gene>() {
            public void apply( Gene gene ) {
                if ( activity != null ) {
                    activity.terminate( 0 );
                }
                viewportOffset.setComponents( -mvt.modelToViewX( gene.getCenterX() ) + STAGE_SIZE.getWidth() / 2, 0 );
                activity = modelRootNode.animateToPositionScaleRotation( viewportOffset.getX(), viewportOffset.getY(), 1, 0, 1000 );
            }
        } );

        // Add the tool boxes from which various biomolecules can be moved into
        // the active area of the sim.
        // TODO: There is some code duplication here.  Could be cleaned up by making genes provide their transcription factors.
        BiomoleculeToolBoxNode biomoleculeToolBoxNode1 = new BiomoleculeToolBoxNode( model, this, mvt, 0 ) {{
            setOffset( mvt.modelToViewX( model.getDnaMolecule().getGenes().get( 0 ).getCenterX() ) - STAGE_SIZE.getWidth() / 2 + 15, 15 );
        }};
        biomoleculeToolBoxNodeList.add( biomoleculeToolBoxNode1 );
        modelRootNode.addChild( biomoleculeToolBoxNode1 );
        BiomoleculeToolBoxNode biomoleculeToolBoxNode2 = new BiomoleculeToolBoxNode( model, this, mvt, 1 ) {{
            setOffset( mvt.modelToViewX( model.getDnaMolecule().getGenes().get( 1 ).getCenterX() ) - STAGE_SIZE.getWidth() / 2 + 15, 15 );
        }};
        biomoleculeToolBoxNodeList.add( biomoleculeToolBoxNode2 );
        modelRootNode.addChild( biomoleculeToolBoxNode2 );
        BiomoleculeToolBoxNode biomoleculeToolBoxNode3 = new BiomoleculeToolBoxNode( model, this, mvt, 2 ) {{
            setOffset( mvt.modelToViewX( model.getDnaMolecule().getGenes().get( 2 ).getCenterX() ) - STAGE_SIZE.getWidth() / 2 + 15, 15 );
        }};
        biomoleculeToolBoxNodeList.add( biomoleculeToolBoxNode3 );
        modelRootNode.addChild( biomoleculeToolBoxNode3 );

        //Uncomment this line to add zoom on right mouse click drag
        addInputEventListener( getZoomEventHandler() );
    }

    public ImmutableVector2D getViewportOffset() {
        return new ImmutableVector2D( viewportOffset );
    }

    public void reset() {
        for ( BiomoleculeToolBoxNode biomoleculeToolBoxNode : biomoleculeToolBoxNodeList ) {
            biomoleculeToolBoxNode.reset();
        }
    }
}
