// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsApplication.RESOURCES;

/**
 * @author John Blanco
 */
public class ManualGeneExpressionCanvas extends PhetPCanvas {
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;
    private PTransformActivity activity;

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
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.75 ) ),
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
        // TODO: Using the general ModelObjectNode for now, will probably need a specific node soon.
        final PNode dnaMoleculeNode = new DnaMoleculeNode( model.getDnaMolecule(), mvt );
        modelRootNode.addChild( dnaMoleculeNode );

        // Add buttons for moving to next and previous genes.
        controlsRootNode.addChild( new HTMLImageButtonNode( "Next Gene", RESOURCES.getImage( "gray-arrow.png" ) ) {{
            setTextPosition( TextPosition.LEFT );
            setFont( new PhetFont( 20 ) );
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - 20, dnaMoleculeNode.getFullBoundsReference().getMaxY() + 20 );
            setBackground( Color.GREEN );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.nextGene();
                }
            } );
        }} );
        controlsRootNode.addChild( new HTMLImageButtonNode( "Prev Gene", flipX( RESOURCES.getImage( "gray-arrow.png" ) ) ) {{
            setTextPosition( TextPosition.RIGHT );
            setFont( new PhetFont( 20 ) );
            setOffset( 20, dnaMoleculeNode.getFullBoundsReference().getMaxY() + 20 );
            setBackground( Color.GREEN );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.previousGene();
                }
            } );
        }} );

        model.activeGene.addObserver( new VoidFunction1<Gene>() {
            public void apply( Gene gene ) {
                if ( activity != null ) {
                    activity.terminate( 0 );
                }
                activity = modelRootNode.animateToPositionScaleRotation( -mvt.modelToViewX( gene.getRect().getCenterX() ) + STAGE_SIZE.getWidth() / 2, 0, 1, 0, 1000 );
            }
        } );

        //Uncomment this line to add zoom on right mouse click drag
//        addInputEventListener( getZoomEventHandler() );
    }
}
