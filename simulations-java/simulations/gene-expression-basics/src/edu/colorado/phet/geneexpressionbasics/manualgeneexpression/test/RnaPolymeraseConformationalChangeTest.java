// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.test;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Tests some of the shape creation utilities.  This exists primarily to get a
 * look at a bunch of the random shapes at the same time in order to decide
 * which ones look best.
 *
 * @author John Blanco
 */
public class RnaPolymeraseConformationalChangeTest {
    private static Dimension2D STAGE_SIZE = new PDimension( 800, 800 );

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.50 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        int seed = 200;
        Shape shape;
        for ( int i = -5; i < 5; i++ ) {
            for ( int j = -5; j < 5; j++ ) {
                RnaPolymerase rnaPolymerase = new RnaPolymerase();
                rnaPolymerase.seed = seed;
                rnaPolymerase.distortShape( 1 );
                rnaPolymerase.setPosition( i * 600, j * 600 );
                MobileBiomoleculeNode biomoleculeNode = new MobileBiomoleculeNode( mvt, rnaPolymerase );
                canvas.getLayer().addChild( biomoleculeNode );
                PText label = new PText( Integer.toString( seed ) );
                label.setOffset( biomoleculeNode.getFullBoundsReference().getCenterX() - label.getFullBoundsReference().width / 2,
                                 biomoleculeNode.getFullBoundsReference().getCenterY() - label.getFullBoundsReference().height / 2 );
                canvas.getLayer().addChild( label );
                seed++;
            }
        }

//        RnaPolymerase rnaPolymerase = new RnaPolymerase();
//        rnaPolymerase.setPosition( 0, 0 );
//        canvas.getLayer().addChild( new MobileBiomoleculeNode( mvt, rnaPolymerase ) );


        // Boiler plate app stuff.
        JFrame frame = new JFrame( "RNA Polymerase Shape Testing" );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
