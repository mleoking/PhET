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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Images.GRAY_ARROW;

/**
 * Primary canvas for the tab in which the user interacts with individual
 * biomolecules in order to transcribe DNA and then translate RNA.  This is,
 * at the time of this writing, the first tab.
 *
 * @author John Blanco
 */
public class ManualGeneExpressionCanvas extends PhetPCanvas implements Resettable {

    // Stage size, based on default screen size.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    // Constants the define the zoom range.
    private static final double MIN_ZOOM = 0.005;
    private static final double MAX_ZOOM = 1;

    // Time for animations.
    private static final long ZOOM_ANIMATION_TIME = 2000; // In milliseconds.
    private static final long GENE_TO_GENE_ANIMATION_TIME = 1000; // In milliseconds.

    // Inset for several of the controls.
    private static final double INSET = 15;

    // Value used for comparing numbers that represent the scale factor of the
    // PNodes.  Not entirely sure why this is needed, but it turns out that
    // when a zoom in or zoom out is performed, the scale gets close but not
    // exactly to the target value.  Hence the need for this constant.  Its
    // values was empirically determined.
    private static final double SCALE_COMPARISON_FACTOR = 1E-4;

    // Debug variable for turning on the visibility of the motion bounds.
    private static final boolean SHOW_MOTION_BOUNDS = false;

    private final ModelViewTransform mvt;
    private final Vector2D viewportOffset = new Vector2D( 0, 0 );
    private final List<BiomoleculeToolBoxNode> biomoleculeToolBoxNodeList = new ArrayList<BiomoleculeToolBoxNode>();

    // PNodes that are used as layers and that are involved in the zoom
    // functionality.
    private PNode backgroundCellLayer;
    private PNode modelRootNode;

    // Background cell that the user zooms in and out of.
    protected BackgroundCellNode backgroundCell;

    /**
     * Constructor.
     *
     * @param model
     */
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
        setBackground( Color.BLACK );

        // Add a background layer where the cell(s) that make up the background
        // will reside.
        backgroundCellLayer = new PNode();
        addWorldChild( backgroundCellLayer );

        // Set up the node where all controls that need to be below the
        // biomolecules should be placed.  This node and its children will
        // stay in one place and not scroll.
        final PNode backControlsLayer = new PNode();
        addWorldChild( backControlsLayer );

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

        // Set up the node where all controls that need to be above the
        // biomolecules should be placed.  This node and its children will
        // stay in one place and not scroll.
        final PNode frontControlsLayer = new PNode();
        addWorldChild( frontControlsLayer );

        // Add the background cell that will enclose the DNA strand.  Clicking
        // on this cell will zoom in when we are in the zoomed out state.
        backgroundCell = new BackgroundCellNode( mvt.modelToView( model.getDnaMolecule().getLeftEdgePos().getX() + model.getDnaMolecule().getLength() / 2,
                                                                  DnaMolecule.Y_POS ),
                                                 6 );
        backgroundCell.setPickable( true );
        backgroundCell.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseClicked( PInputEvent event ) {
                if ( isZoomedOut() ) {
                    zoomIn( ZOOM_ANIMATION_TIME );
                }
            }
        } );
        backgroundCellLayer.addChild( backgroundCell );

        // Add a few other background cells to make it look like the cell is
        // amongst a set of other cells.
        backgroundCellLayer.addChild( new BackgroundCellNode( new Point2D.Double( 40000, -40000 ), 2 ) );
        backgroundCellLayer.addChild( new BackgroundCellNode( new Point2D.Double( -40000, -60000 ),
                                                              new PDimension( BackgroundCellNode.DEFAULT_SIZE.getWidth() * 0.7,
                                                                              BackgroundCellNode.DEFAULT_SIZE.getHeight() ),
                                                              -Math.PI / 6,
                                                              2 ) );
        backgroundCellLayer.addChild( new BackgroundCellNode( new Point2D.Double( -1000, -85000 ),
                                                              new PDimension( BackgroundCellNode.DEFAULT_SIZE.getWidth() * 0.7,
                                                                              BackgroundCellNode.DEFAULT_SIZE.getHeight() ),
                                                              -Math.PI / 6,
                                                              2 ) );
        backgroundCellLayer.addChild( new BackgroundCellNode( new Point2D.Double( 90000, -60000 ),
                                                              new PDimension( BackgroundCellNode.DEFAULT_SIZE.getWidth() * 0.8,
                                                                              BackgroundCellNode.DEFAULT_SIZE.getHeight() ),
                                                              Math.PI / 8,
                                                              2 ) );
        backgroundCellLayer.addChild( new BackgroundCellNode( new Point2D.Double( -100000, -20000 ),
                                                              new PDimension( BackgroundCellNode.DEFAULT_SIZE.getWidth() * 0.8,
                                                                              BackgroundCellNode.DEFAULT_SIZE.getHeight() ),
                                                              Math.PI * 0.05,
                                                              2 ) );

        // Add the representation of the DNA strand.
        final PNode dnaMoleculeNode = new DnaMoleculeNode( model.getDnaMolecule(), mvt, 3, true );
        dnaLayer.addChild( dnaMoleculeNode );

        // Add the placement hints that go on the DNA molecule.  These exist on
        // their own layer so that they can be seen above any molecules that
        // are attached to the DNA strand.
        for ( Gene gene : model.getDnaMolecule().getGenes() ) {
            for ( PlacementHint placementHint : gene.getPlacementHints() ) {
                placementHintLayer.addChild( new PlacementHintNode( mvt, placementHint ) );
            }
        }

        // Add the protein collection box.
        final ProteinCollectionNode proteinCollectionNode = new ProteinCollectionNode( model, mvt ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        backControlsLayer.addChild( proteinCollectionNode );

        // TODO: Temp for debug - show the protein capture area.  Remove eventually.
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
        frontControlsLayer.addChild( nextGeneButton );
        // TODO: i18n
        final HTMLImageButtonNode previousGeneButton = new HTMLImageButtonNode( "Previous Gene", flipX( GRAY_ARROW ) ) {{
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
        }};
        frontControlsLayer.addChild( previousGeneButton );

        // Add the Reset All button.
        final ResetAllButtonNode resetAllButton = new ResetAllButtonNode( new Resettable[] { model, this }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            centerFullBoundsOnPoint( nextGeneButton.getFullBoundsReference().getCenterX(), nextGeneButton.getFullBoundsReference().getMaxY() + 40 );
        }};
        frontControlsLayer.addChild( resetAllButton );

        // Monitor the active gene and move the view port to be centered on it
        // whenever it changes.
        model.activeGene.addObserver( new VoidFunction1<Gene>() {
            public void apply( Gene gene ) {
                terminateAnyRunningActivities();
                viewportOffset.setComponents( -mvt.modelToViewX( gene.getCenterX() ) + STAGE_SIZE.getWidth() / 2, 0 );
                // Perform an animation that will put the selected gene in
                // the center of the view port.
                backgroundCellLayer.animateToPositionScaleRotation( viewportOffset.getX(), viewportOffset.getY(), 1, 0, GENE_TO_GENE_ANIMATION_TIME );
                final PTransformActivity animateToActiveGene =
                        modelRootNode.animateToPositionScaleRotation( viewportOffset.getX(), viewportOffset.getY(), 1, 0, GENE_TO_GENE_ANIMATION_TIME );
                animateToActiveGene.setDelegate( new PActivityDelegateAdapter() {
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
            BiomoleculeToolBoxNode biomoleculeToolBoxNode = new BiomoleculeToolBoxNode( model, this, mvt, gene ) {{
                setOffset( mvt.modelToViewX( gene.getCenterX() ) - STAGE_SIZE.getWidth() / 2 + INSET, INSET );
            }};
            biomoleculeToolBoxNodeList.add( biomoleculeToolBoxNode );
            biomoleculeToolBoxLayer.addChild( biomoleculeToolBoxNode );
            model.addOffLimitsMotionSpace( mvt.viewToModel( biomoleculeToolBoxNode.getFullBoundsReference() ) );
        }

        // Add the buttons for zooming in and out.
        final PNode zoomInButton = new TextButtonNode( "Zoom In", new PhetFont( 18 ), Color.YELLOW ) {{
            centerFullBoundsOnPoint( previousGeneButton.getFullBoundsReference().getCenterX(), previousGeneButton.getFullBoundsReference().getMaxY() + 40 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomIn( ZOOM_ANIMATION_TIME );
                }
            } );
        }};
        frontControlsLayer.addChild( zoomInButton );
        final PNode zoomOutButton = new TextButtonNode( "Zoom Out", new PhetFont( 18 ), Color.YELLOW ) {{
            centerFullBoundsOnPoint( previousGeneButton.getFullBoundsReference().getCenterX(), previousGeneButton.getFullBoundsReference().getMaxY() + 40 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomOut( ZOOM_ANIMATION_TIME );
                }
            } );
        }};
        frontControlsLayer.addChild( zoomOutButton );

        // Monitor the zoom and set the visibility of various controls.
        modelRootNode.addPropertyChangeListener( new PropertyChangeListener() {

            private final CursorHandler handCursor = new CursorHandler( CursorHandler.HAND );
            private final CursorHandler defaultCursor = new CursorHandler( CursorHandler.DEFAULT );

            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( "transform" ) ) {
                    double scaleFactor = ( (AffineTransform) evt.getNewValue() ).getScaleX();
                    boolean zoomedOut = scaleFactor < MIN_ZOOM + SCALE_COMPARISON_FACTOR;
                    boolean zoomedIn = scaleFactor > MAX_ZOOM - SCALE_COMPARISON_FACTOR;

                    // Set the visibility of the controls that aren't shown
                    // unless we are zoomed all the way in.
                    biomoleculeToolBoxLayer.setVisible( zoomedIn );
                    proteinCollectionNode.setVisible( zoomedIn );
                    previousGeneButton.setVisible( zoomedIn );
                    nextGeneButton.setVisible( zoomedIn );
                    zoomOutButton.setVisible( zoomedIn );
                    resetAllButton.setVisible( zoomedIn );

                    // Set the visibility of the controls that aren't shown
                    // unless we are zoomed all the way out.
                    zoomInButton.setVisible( zoomedOut );

                    // Fade the DNA molecule.  A linear fade didn't look good,
                    // so the fade is exponential with a threshold.
                    float dnaTransparency = (float) Math.pow( scaleFactor, 2 );
                    if ( dnaTransparency < 0.001 ) {
                        dnaTransparency = 0;
                    }
                    dnaMoleculeNode.setTransparency( dnaTransparency );

                    // Change the cursor handler on the cell when zoomed all
                    // the way out in order to indicate that it is clickable.
                    if ( zoomedOut ) {
                        if ( arrayContainsItem( backgroundCell.getInputEventListeners(), defaultCursor ) ) {
                            backgroundCell.removeInputEventListener( defaultCursor );
                        }
                        if ( !arrayContainsItem( backgroundCell.getInputEventListeners(), handCursor ) ) {
                            backgroundCell.addInputEventListener( handCursor );
                        }
                    }
                    else if ( arrayContainsItem( backgroundCell.getInputEventListeners(), handCursor ) ) {
                        backgroundCell.removeInputEventListener( handCursor );
                        if ( !arrayContainsItem( backgroundCell.getInputEventListeners(), defaultCursor ) ) {
                            backgroundCell.addInputEventListener( defaultCursor );
                        }
                    }
                }
            }
        } );

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

    private void zoomIn( long duration ) {
        zoomInOnNodes( duration, backgroundCellLayer, modelRootNode );
    }

    private void zoomOut( long duration ) {
        zoomOutFromNodes( duration, backgroundCellLayer, modelRootNode );
    }

    private void zoomInOnNodes( long duration, PNode... nodesToZoom ) {
        terminateAnyRunningActivities();
        for ( PNode node : nodesToZoom ) {
            node.animateToPositionScaleRotation( viewportOffset.getX(), viewportOffset.getY(), MAX_ZOOM, 0, duration );
        }
    }

    private void zoomOutFromNodes( long duration, PNode... nodesToZoom ) {
        terminateAnyRunningActivities();
        for ( PNode node : nodesToZoom ) {
            node.animateToPositionScaleRotation( STAGE_SIZE.getWidth() / 2, mvt.modelToViewY( DnaMolecule.Y_POS ), MIN_ZOOM, 0, duration );
        }
    }

    private void terminateAnyRunningActivities() {
        for ( Object activity : new ArrayList( getRoot().getActivityScheduler().getActivitiesReference() ) ) {
            ( (PActivity) activity ).terminate( PActivity.TERMINATE_AND_FINISH );
        }
    }

    // Convenience function for checking if an item is contained in an array.
    private boolean arrayContainsItem( Object[] array, Object item ) {
        boolean contained = false;
        for ( int i = 0; i < array.length; i++ ) {
            if ( item == array[i] ) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    private boolean isZoomedOut() {
        return backgroundCellLayer.getScale() <= MIN_ZOOM + SCALE_COMPARISON_FACTOR &&
               backgroundCellLayer.getScale() >= MIN_ZOOM - SCALE_COMPARISON_FACTOR &&
               modelRootNode.getScale() <= MIN_ZOOM + SCALE_COMPARISON_FACTOR &&
               modelRootNode.getScale() >= MIN_ZOOM - SCALE_COMPARISON_FACTOR;
    }

    private boolean isZoomedIn() {
        return backgroundCellLayer.getScale() <= MAX_ZOOM + SCALE_COMPARISON_FACTOR &&
               backgroundCellLayer.getScale() >= MAX_ZOOM - SCALE_COMPARISON_FACTOR &&
               modelRootNode.getScale() <= MAX_ZOOM + SCALE_COMPARISON_FACTOR &&
               modelRootNode.getScale() >= MAX_ZOOM - SCALE_COMPARISON_FACTOR;
    }

    public ImmutableVector2D getViewportOffset() {
        return new ImmutableVector2D( viewportOffset );
    }

    public void reset() {
        for ( BiomoleculeToolBoxNode biomoleculeToolBoxNode : biomoleculeToolBoxNodeList ) {
            biomoleculeToolBoxNode.reset();
        }
        if ( !isZoomedIn() ) {
            zoomIn( 0 );
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
