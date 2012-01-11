// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Images.GRAY_ARROW;

/**
 * @author John Blanco
 */
public class ManualGeneExpressionCanvas extends PhetPCanvas implements Resettable {

    // Stage size, based on default screen size.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    public static final Color CELL_INTERIOR_COLOR = new Color( 190, 231, 251 );

    // Inset for several of the controls.
    private static final double INSET = 15;

    // Debug variable for turning on the visibility of the motion bounds.
    private static final boolean SHOW_MOTION_BOUNDS = false;

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
        setBackground( CELL_INTERIOR_COLOR );

        // Set up the node where all controls should be placed.  These will
        // stay in one place and not scroll.
        PNode controlsRootNode = new PNode();
        addWorldChild( controlsRootNode );

        // Set up the root node for all model objects.  Nodes placed under
        // this one will scroll when the user moves along the DNA strand.
        final PNode modelRootNode = new PNode();
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

        // Add the representation of the DNA strand.
        final PNode dnaMoleculeNode = new DnaMoleculeNode( model.getDnaMolecule(), mvt );
        dnaLayer.addChild( dnaMoleculeNode );

        // Add the protein collection box.
        final ProteinCollectionNode proteinCollectionNode = new ProteinCollectionNode( model, mvt ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        controlsRootNode.addChild( proteinCollectionNode );

        // TODO: Temp for debug - show the protein capture area.
//        final PPath proteinCaptureAreaNode = new PhetPPath( new BasicStroke( 5 ), Color.RED );
//        model.proteinCaptureAreaProperty.addObserver( new VoidFunction1<Rectangle2D>() {
//            public void apply( Rectangle2D proteinCaptureAreaBounds ) {
//                proteinCaptureAreaNode.setPathTo( mvt.modelToView( proteinCaptureAreaBounds ) );
//            }
//        } );
//        modelRootNode.addChild( proteinCaptureAreaNode );

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
        controlsRootNode.addChild( new HTMLImageButtonNode( "Previous Gene", flipX( GRAY_ARROW ) ) {{
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

        // Add the zoom control slider.
        Property<Double> zoomFactorProperty = new Property<Double>( 1.0 );
        double minZoom = 0.01;
        double maxZoom = 1;
        controlsRootNode.addChild( new ZoomControlSliderNode( new PDimension( 20, 120 ), zoomFactorProperty, minZoom, maxZoom, 10 ) {{
            setOffset( 20, 300 );
        }} );
        zoomFactorProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double zoomFactor ) {
                // Reset any previous transformation.
                modelRootNode.setTransform( new AffineTransform() );
                // Zoom around a point that is in the center of the stage in
                // the x direction and the location of the DNA strand in the
                // y direction.
                modelRootNode.scaleAboutPoint( zoomFactor, STAGE_SIZE.getWidth() / 2, mvt.modelToViewY( DnaMolecule.Y_POS ) );
            }
        } );

        // Monitor the active gene and move the view port to be centered on it
        // whenever it changes.
        model.activeGene.addObserver( new VoidFunction1<Gene>() {
            public void apply( Gene gene ) {
                if ( activity != null ) {
                    activity.terminate( 0 );
                }
                viewportOffset.setComponents( -mvt.modelToViewX( gene.getCenterX() ) + STAGE_SIZE.getWidth() / 2, 0 );
                activity = modelRootNode.animateToPositionScaleRotation( viewportOffset.getX(), viewportOffset.getY(), 1, 0, 1000 );
                System.out.println( "activity.getDelegate() = " + activity.getDelegate() );
                activity.setDelegate( new PActivityDelegateAdapter() {
                    @Override public void activityFinished( PActivity activity ) {
                        // Update the position of the protein capture area in
                        // the model, since a transformation of the model-to-
                        // view relationship just occurred.
                        PBounds boundsInControlNode = proteinCollectionNode.getFullBounds();
                        Rectangle2D boundsAfterTransform;
                        try {
                            boundsAfterTransform = modelRootNode.getTransformReference( true ).createInverse().createTransformedShape( boundsInControlNode ).getBounds2D();
                        }
                        catch ( NoninvertibleTransformException e ) {
                            System.out.println( getClass().getName() + " - Error: Unable to invert transform needed to update the protein capture area." );
                            e.printStackTrace();
                            boundsAfterTransform = new PBounds();
                        }
                        Rectangle2D boundsInModel = mvt.viewToModel( boundsAfterTransform ).getBounds2D();
                        model.setProteinCaptureArea( boundsInModel );
                        model.addOffLimitsMotionSpace( boundsInModel );
                    }
                } );
            }
        } );

        // Add the tool boxes from which the various biomolecules can be moved
        // into the active area of the sim.
        for ( final Gene gene : model.getDnaMolecule().getGenes() ) {
            BiomoleculeToolBoxNode biomoleculeToolBoxNode = new BiomoleculeToolBoxNode( model, this, mvt, gene.identifier ) {{
                setOffset( mvt.modelToViewX( gene.getCenterX() ) - STAGE_SIZE.getWidth() / 2 + INSET, INSET );
            }};
            biomoleculeToolBoxNodeList.add( biomoleculeToolBoxNode );
            biomoleculeToolBoxLayer.addChild( biomoleculeToolBoxNode );
            model.addOffLimitsMotionSpace( mvt.viewToModel( biomoleculeToolBoxNode.getFullBoundsReference() ) );
        }

        // Uncomment this line to add zoom on right mouse click drag.  TODO: Probably should be removed eventually.
//        addInputEventListener( getZoomEventHandler() );

        // Add a node to depict the motion bounds.  This is for debug purposes.
        if ( SHOW_MOTION_BOUNDS ) {
            final PhetPPath motionBoundsIndicator = new PhetPPath( new BasicStroke( 2 ), Color.RED );
            modelRootNode.addChild( motionBoundsIndicator );
            model.activeGene.addObserver( new VoidFunction1<Gene>() {
                public void apply( Gene gene ) {
                    motionBoundsIndicator.setPathTo( mvt.modelToView( model.getBoundsForActiveGene().getBounds() ) );
                }
            } );
        }
    }

    public ImmutableVector2D getViewportOffset() {
        return new ImmutableVector2D( viewportOffset );
    }

    public void reset() {
        for ( BiomoleculeToolBoxNode biomoleculeToolBoxNode : biomoleculeToolBoxNodeList ) {
            biomoleculeToolBoxNode.reset();
        }
    }

    private static class PActivityDelegateAdapter implements PActivity.PActivityDelegate {

        public void activityStarted( PActivity activity ) {
            // Override to change, does nothing by default.
        }

        public void activityStepped( PActivity activity ) {
            // Override to change, does nothing by default.
        }

        public void activityFinished( PActivity activity ) {
            // Override to change, does nothing by default.
        }
    }
}
